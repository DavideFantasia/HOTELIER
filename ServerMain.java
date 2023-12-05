import utils.*;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.concurrent.*;


/**
* Reti e Laboratorio III - A.A. 2023/2024
* HOTELIER
*
* Classe Java che rappresenta il server del servizio.
*
* Il server gestisce un pool di thread ed esegue un ciclo nel quale:
* (1) accetta richieste di connessione da parte dei vari client;
* (2) per ogni richiesta, attiva un thread Worker per interagire con il client;
*
*/

class ServerMain {
    public static final String configFile = "server.properties";
    public static final String hotelJSON = "Hotels.json";

    public static HotelManager hotelManager = new HotelManager(hotelJSON);

    public static int port;
    public static int maxDelay;

    public static final ExecutorService threadPool = Executors.newCachedThreadPool();
    public static ServerSocket serverSocket;

    public static void main(String[] args){ 
		try{
            serverBooting();

            serverSocket = new ServerSocket(port);
            Runtime.getRuntime().addShutdownHook(new TerminationHandler(maxDelay, threadPool, serverSocket, hotelManager));
            System.out.printf("[SERVER] In ascolto sulla porta: %d\n", port);
            while (true){
                Socket socket = null;
                // Accetto le richieste provenienti dai client.
                try {socket = serverSocket.accept();}
                catch (SocketException e) {break;}
                threadPool.execute(new ClientHandler(socket,hotelManager));
            }
        }catch(Exception e){
            System.err.printf("[SERVER]: %s\n",e.getMessage());
            System.exit(1);
        }
           
 
	}

    private static void serverBooting() throws Exception{
        //lettura input staticic
        readConfig();
        //metodo per iniziare la lettura dei dati degli utenti in memoria
        User.dataBooting(hotelManager);
        //metodo per iniziare il ranking per le città
        Ranking.boot(hotelManager);
    }
    public static void ReturnCodeHandler(ReturnCode code){
        switch (code) {
            case SUCCESS:
                System.out.println("Operazione completata con successo");
                break;
            case USER_ALREADY_PRESENT_ERROR:
                System.out.println("Utente già presente, errore");
                break;
            case HASHING_ERROR:
                System.out.println("Errore durante l'hashing");
                break;
            case NOT_LOGGEDIN_ERROR:
                System.out.println("Non autenticato, errore");
                break;
            case NO_SUCH_HOTEL_ERROR:
                System.out.println("Nessun hotel trovato, errore");
                break;
            case UNKNOW_ERROR:
                System.out.println("Errore sconosciuto");
                break;
            default:
                System.out.println("Codice non gestito");
                break;
        }
    }

    /**
     * Metodo che legge il file di configurazione del server.
     * 
     * @throws FileNotFoundException se il file non esiste
     * @throws IOException           se si verifica un errore durante la lettura
     */
    public static void readConfig() throws FileNotFoundException, IOException {
        InputStream input = ServerMain.class.getResourceAsStream(configFile);
        Properties prop = new Properties();
        prop.load(input);
        port = Integer.parseInt(prop.getProperty("port"));
        maxDelay = Integer.parseInt(prop.getProperty("maxDelay"));
        input.close();
    }
    
}
