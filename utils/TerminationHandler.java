package utils;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
/*
*Classe che implementa l'handler di terminazione per il server.
*Questo thread viene avviato al momento della pressione dei tasti CTRL+C.
*Lo scopo e' quello di far terminare il main del server bloccato sulla accept()
*in attesa di nuove connessioni e chiudere il pool di thread.
*/

public class TerminationHandler extends Thread{
    private int maxDelay;
    private ExecutorService pool;
    private ServerSocket serverSocket;
    private HotelManager hotelManager;
    private Thread notificationSender;

    public TerminationHandler(int maxDelay, ExecutorService pool, ServerSocket serverSocket, HotelManager hotelManager,Thread notificationSender){
        this.maxDelay = maxDelay;
        this.pool = pool;
        this.serverSocket = serverSocket;
        this.hotelManager = hotelManager;
        this.notificationSender = notificationSender;
    }

    public void run() {
        //Avvio la procedura di terminazione del server.
        System.out.println("[SERVER] Avvio terminazione...");
        // Chiudo la ServerSocket in modo tale da non accettare piu' nuove richieste.
        try {serverSocket.close();}
        catch (IOException e) {System.err.printf("[SERVER] Errore: %s\n", e.getMessage());}

        // Faccio terminare il pool di thread.
        pool.shutdown();
        try {
            if (!pool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS))
                pool.shutdownNow();
        }catch (InterruptedException e){pool.shutdownNow();}
        
        //si forza il logOut di tutti gli utenti connessi
        forcedLogOut();
        //salvataggio pre-chiusura dei dati
        this.hotelManager.updateHotelInfo();
        User.updateUserInfo();

        ReviewHistoryManager.getInstance().saveReview();

        try{
            this.notificationSender.interrupt();
            this.notificationSender.join();
        }catch(Exception e){e.printStackTrace();}

        System.out.println("[SERVER] Terminato.");
    }

    /**
     * metodo che allo spegnimento del server forza il logOut su tutti gli utenti
     */
    private static void forcedLogOut(){
        for(User user: User.getListOfUsers().values()){
            if(user.isOnline())
                user.logOut();
        }
    }
 }
