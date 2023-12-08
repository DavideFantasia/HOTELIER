package utils;

import java.net.*;
import java.io.*;

public class NotificationManager extends Thread{

    public MulticastSocket multicastSocket;
    private  int multicastPort;
    private String multicastIP;
    private boolean continueListening=true;

    private InetAddress group;


    public NotificationManager(String multicastIp, int multicastPort){
        this.multicastIP = multicastIp;
        this.multicastPort = multicastPort;
        this.multicastSocket = null;
        this.continueListening = true;
    }

    public void joinMulticast(){
        try {
            this.multicastSocket = new MulticastSocket(this.multicastPort);
            this.group= InetAddress.getByName(this.multicastIP);
            this.multicastSocket.joinGroup(this.group);
        }catch (IOException ex) {System.err.println("=== errore joinMulticast ===");}
    }

    public void leaveMulticast(){
        try{
            this.multicastSocket.leaveGroup(this.group);
            this.continueListening = false;
        }catch(Exception ex){System.err.println("=== errore leaveMulticast ===");}
    }
    /**
     * Invia un messaggio 'msg' al gruppo mulicast
     * @param msg stringa da inviare al gruppo
     * @return un codice di esito dell'operazione
     * @throws IOException 
     */
    public ReturnCode sendNotification(String msg) throws IOException{
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(this.multicastIP);
        byte[] buf = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, this.multicastPort);
        socket.send(packet);
        socket.close();
        return ReturnCode.SUCCESS;
    }

    /**
     * il metodo run funge da receiver delle notifiche,
     * opererà sui vari client rimanendo in ascolta di eventuali pacchetti multicast
     * e printandoli al bisogno
     * @Override
     */
    public void run(){
        byte[] buffer = new byte[256];
        try{
            this.multicastSocket.setSoTimeout(1000); // Imposta un timeout di 1 secondo
            while (this.continueListening) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    this.multicastSocket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    ConsolePrinter.printToConsole("\033[0K\r[NEWS] - " + received + "\n> ");
                } catch (SocketTimeoutException e) {
                    // Controlla regolarmente se è stata richiesta l'interruzione
                    if (!this.continueListening) {
                        break;
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
}
