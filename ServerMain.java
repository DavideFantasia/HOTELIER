import utils.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
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
    public static NotificationManager notificationManager;
    public static NotificationSender notificationSender;
    public static ReviewHistoryManager reviewHistoryIstance;

    public static int port;
    public static int multicastPort;
    public static final String multicastIP = "230.0.0.0";
    public static int maxDelay;

    public static final ExecutorService threadPool = Executors.newCachedThreadPool();
    public static ServerSocket serverSocket;

    public static void main(String[] args){ 
		try{
            serverBooting();

            serverSocket = new ServerSocket(port);

            Runtime.getRuntime().addShutdownHook(new TerminationHandler(maxDelay, threadPool, serverSocket, hotelManager,notificationSender));
            System.out.printf("[SERVER] In ascolto sulla porta: %d\n", port);
            while (true){
                Socket socket = null;
                // Accetto le richieste provenienti dai client.
                try {socket = serverSocket.accept();}
                catch (SocketException e) {break;}
                threadPool.execute(new ClientHandler(socket,hotelManager,reviewHistoryIstance));
            }
        }catch(Exception e){
            System.err.printf("[SERVER]: %s\n",e.getMessage());
            System.exit(1);
        }
           
 
	}

    private static class NotificationSender extends Thread{
        private HashMap<String, Hotel> prevTopHotels;

        public NotificationSender(){
            this.prevTopHotels = Ranking.getAllRanking();
            notificationManager = new NotificationManager(multicastIP, multicastPort);
        }

        public void run(){
            HashMap<String, Hotel> newTopHotel = null;
            String msg = "";
            long timeToWait = maxDelay/10;

            while (true) {
                try{
                    sleep(timeToWait); //ogni maxDelay/10 millisecondi faccio il check

                    newTopHotel = Ranking.getAllRanking();

                    for (Hotel newTopHotelInCity : newTopHotel.values()){
                        
                        String city = newTopHotelInCity.getCity();
                        Hotel prevTop = prevTopHotels.get(city);
                        
                        //sono hotel con diversi nomi nella stessa città => hotel diversi
                        if(prevTop.compareTo(newTopHotelInCity)!=0){
                            //abbiamo un nuovo top hotel
                            msg = String.format("[%s] '%s' is now the top hotel!",city,newTopHotelInCity.getName());
                            notificationManager.sendNotification(msg);
                            System.out.println("[LOCAL RANKING] - "+msg);
                        }
                    }

                    this.prevTopHotels = newTopHotel;

                }catch(Exception e){break;}
            }
        }
    }

    private static void serverBooting() throws Exception{
        System.out.println("[SERVER] avvio server");
        //lettura input staticic
        readConfig();
        //metodo per iniziare la lettura dei dati degli utenti in memoria
        User.dataBooting(hotelManager);

        //Metodo per inizializzare la lettura dei dati dallo storico delle recensioni
        reviewHistoryIstance = ReviewHistoryManager.getInstance();

        //metodo per iniziare il ranking per le città
        Ranking.boot(hotelManager);
        
        //thread che gestisce l'invio di notifiche multicast ogni tot secondi
        notificationSender = new NotificationSender();

        //avvio del thread che gestisce le notifiche sul ranking 
        notificationSender.start();
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
        multicastPort = Integer.parseInt(prop.getProperty("multicastPort"));
        input.close();
    }
    
}
