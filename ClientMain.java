import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

import utils.ConsolePrinter;
import utils.NotificationManager;

/**
* Classe Java che rappresenta il client del gioco.
*
* Il client esegue un ciclo nel quale:
* (1) legge l'input dell'utente da tastiera;
* (2) invia il messaggio letto al server;
* (3) riceve (e interpreta) la risposta del server.
*
* Le principali operazione da eseguire tramite CLI sono:
* (1) register(username,password)
* (2) login(username, password)
* (3) logout
* (4) searchHotel(nomeHotel, città)
* (5) serchAllHotels(città)
* (6) insertReview(nomeHotel, nomeCittà, GlobalScore, SingleScores[ ])
* (7) showMyBadges()
*/

public class ClientMain {
    public static final String configFile = "client.properties";

    public static String hostname; // localhost
    public static int port; // 12000
    public static int multicastPort;
    public static final String multicastIP = "230.0.0.0";

    // Socket e relativi stream di input/output.
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    private static boolean mustLoop = true;

    private static NotificationManager notificationListener = null;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {

            readConfig();
            socket = new Socket(hostname, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            //stampa del main menu
            ConsolePrinter.mainMenu();

            //Invio del comando al server
            while(mustLoop){
                ConsolePrinter.printToConsole("> ");
                String command = scanner.nextLine();

                out.println(command);

                //risposta del server
                String reply= in.readLine();
                reply = reply.replace("|", "\n");
                ConsolePrinter.printToConsole(reply+"\n");

                if(reply.contains("SUCCESS") || reply.contains("CONNECTION_CLOSED")){
                    commandSideEffectManager(command);
                }
            }

            out.close();
            in.close();
            socket.close();
        }catch(Exception e){
            System.out.printf("Errore [%s]: %s\n",e.getClass().getSimpleName(), e.getMessage());
            System.exit(1);
        }
        scanner.close();
    }
    /**
     * Metodo che esegue e gestisce gli effetti collaterali dei comandi LogIn, LogOut ed Exit.
     * Col comando LogIn joina il multicast, con il logout e il termina ne esce e in più il comando
     * exit chiude anche l'esecuzione del client
     * @param command
     */
    public static void commandSideEffectManager(String command){
        switch (command.split(" ")[0].toLowerCase()) {
            case "login":
                notificationListener = new NotificationManager(multicastIP, multicastPort);
                notificationListener.joinMulticast();
                notificationListener.start();
                break;
            case "logout":
                chiusuraThreadListener();
                break;
            case "exit":
                chiusuraThreadListener();
                mustLoop = false;
            default:
                break;
        }
    }
    /**
     * metodo che va a resettare la variabile interna dedicata al thread di ascolto delle notifiche
     * abbandona il multicast e chiude il thread dedicato
     */
    public static void chiusuraThreadListener(){
        if(!notificationListener.isAlive()) return;
        notificationListener.leaveMulticast();
        try{
            notificationListener.interrupt();
            notificationListener.join();
            notificationListener = null;
        }catch(Exception e){e.printStackTrace();}
    }
    
    /**
     * Metodo che legge il file di configurazione.
     * 
     * @throws FileNotFoundException se il file non esiste
     * @throws IOException           se si verifica un errore durante la lettura
     */
    public static void readConfig() throws FileNotFoundException, IOException {
        InputStream input = ClientMain.class.getResourceAsStream(configFile);
        Properties prop = new Properties();
        prop.load(input);
        port = Integer.parseInt(prop.getProperty("port"));
        multicastPort = Integer.parseInt(prop.getProperty("multicastPort"));
        input.close();

        notificationListener = new NotificationManager(multicastIP, multicastPort);
    }
}