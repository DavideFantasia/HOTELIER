import utils.*;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
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
    public static final String passwordFile = "serverPassword.properties";
    public static final String hotelJSON = "Hotels.json";

    public static HotelManager hotelManager = new HotelManager(hotelJSON);

    public static int port;
    public static int maxDelay;

    public static final ExecutorService threadPool = Executors.newCachedThreadPool();
    public static ServerSocket serverSocket;

    public static void main(String[] args){ 
		try{
            readConfig();

            serverSocket = new ServerSocket(port);
            Runtime.getRuntime().addShutdownHook(new TerminationHandler(maxDelay, threadPool, serverSocket));
            System.out.printf("[SERVER] In ascolto sulla porta: %d\n", port);
            while (true){
                Socket socket = null;
                // Accetto le richieste provenienti dai client.
                try {socket = serverSocket.accept();}
                catch (SocketException e) {break;}
                threadPool.execute(new ClientHandler(socket));
            }
        }catch(Exception e){
            System.err.printf("[SERVER]: %s\n",e.getMessage());
            System.exit(1);
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

    /**
     * metodo per segnare sul file di config la nuova coppia username, password
     * @param username
     * @param password
     * @return un codice di esito, x: successo, y: username già presente, z:errore
     */
    public static synchronized ReturnCode registerUser(String username, String wantedPassword) throws FileNotFoundException, IOException{
        //prima si controlla se uno user ha già lo stesso nome
        FileReader input = new FileReader(passwordFile);
        Properties prop = new Properties();
        ReturnCode resultCode = ReturnCode.UNKNOW_ERROR;

        prop.load(input);
        input.close();

        if(prop.getProperty(username)!=null) resultCode = ReturnCode.USER_ALREADY_PRESENT_ERROR;
        else{
            try{
                
                prop.setProperty(username, Hashing.digestString(wantedPassword));
                prop.store(new FileWriter(passwordFile),"Coppie User-Password registrate al servizio");

                resultCode = ReturnCode.SUCCESS;
            }catch(NoSuchAlgorithmException e){
                resultCode = ReturnCode.HASHING_ERROR;
            }catch(Exception e){
                resultCode = ReturnCode.UNKNOW_ERROR;
            }
        }

        input.close();

        return resultCode;
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            
        }
    }
}
