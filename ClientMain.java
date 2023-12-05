import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

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
    // Socket e relativi stream di input/output.
    private static Scanner scanner = new Scanner(System.in);
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    public static void main(String[] args) {
        boolean mustLoop = true;
        try {
            readConfig();
            socket = new Socket(hostname, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            //Invio del comando al server
            while(mustLoop){
                System.out.printf("> ");
                String command = scanner.nextLine();
                if(command.compareTo("exit")==0) mustLoop = false;
                out.println(command);

                //risposta del server
                String reply= in.readLine();
                reply = reply.replace("|", "\n");
                System.out.println(reply);
            }
            out.close();
            in.close();
            socket.close();
        }catch(Exception e){
            System.err.printf("Errore [%s]: %s\n",e.getClass().getSimpleName(), e.getMessage());
            System.exit(1);
        }
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
        input.close();
    }
}