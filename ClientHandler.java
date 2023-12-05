
// ClientHandler class
import utils.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private User userIstance;
    private Socket clientSocket;
    private HotelManager hotelManagerIstance;
    private BufferedReader in;
    private PrintWriter out;
    //parametro ausiliare per passare in stampa ulteriori informazioni
    private String otherInfo;

    // Constructor
    public ClientHandler(Socket socket, HotelManager hotelManagerIstance) {
        this.clientSocket = socket;
        this.hotelManagerIstance = hotelManagerIstance;
        this.userIstance = null;
        this.otherInfo = "";
    }

    private String clientName(){
        if(this.userIstance == null) return "notLoggedUser";
        else return this.userIstance.getUsername();
    }

    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            boolean flag = true;
            while(flag){
                String line = in.readLine();
                String[] parsedLine = InputParser.parseInput(line);
                String command = parsedLine[0].toLowerCase();
                ReturnCode res = ReturnCode.UNKNOW_ERROR;
                switch (command) {
                    case "login":
                        res = loginCommand(parsedLine);
                        break;
                    case "register":
                        res = registerCommand(parsedLine);
                        break;
                    case "logout":
                        res = logoutCommand();
                        break;
                    case "showmybadge":
                        res = lastBadgeCommand();
                        break;
                    case "insertreview":
                        res = insertReviewCommand(parsedLine);
                        break;
                    case "searchhotel":
                        res = searchHotelCommand(parsedLine);
                        break;
                    case "searchallhotel":
                        res = searchAllHotelCommand(parsedLine);
                        break;
                    case "exit":
                        res = logoutCommand();
                        res = ReturnCode.CONNECTION_CLOSED;
                        flag = false;
                        break;
                    default:
                        res = ReturnCode.WRONG_INPUT_ERROR;
                        flag = false;
                        break;
                }
                printMessage(command,res, otherInfo);
                //reset della varibile
                this.otherInfo = "";
                if(!flag) break;
            }

            this.in.close();
            this.out.close();
            this.clientSocket.close();
        }catch (Exception e) {
            System.err.printf("[WORKER] Errore: %s\n", e.getMessage());
        }
    }
    /**
     * gestione del comando Login
     * @param parsedLine formato del comando [login <username> <password>]
     * @return un codice ReturnCode che rappresenta l'esito del comando
     */
    private ReturnCode loginCommand(String[] parsedLine) {
        if (parsedLine.length != 3) {
            return ReturnCode.WRONG_INPUT_ERROR;
        }
        this.userIstance = User.logIn(parsedLine[1], parsedLine[2]);
        if (this.userIstance == null)
            return ReturnCode.WRONG_PASSWORD_ERROR;

        return ReturnCode.SUCCESS;
    }
    
    private ReturnCode registerCommand(String[] parsedLine){
        if (parsedLine.length != 3) {
            return ReturnCode.WRONG_INPUT_ERROR;
        }

        this.userIstance = User.registerUser(parsedLine[1], parsedLine[2]);
        if (this.userIstance == null)
            return ReturnCode.WRONG_PASSWORD_ERROR;

        return ReturnCode.SUCCESS;
    }

    private ReturnCode logoutCommand(){
        if(this.userIstance == null) return ReturnCode.NOT_LOGGEDIN_ERROR;
        User userToLogOut = this.userIstance;
        this.userIstance = null;
        return userToLogOut.logOut();
    }

    private ReturnCode insertReviewCommand(String[] parsedLine){

        if(this.userIstance == null || !this.userIstance.isOnline()) return ReturnCode.NOT_LOGGEDIN_ERROR;

        if(parsedLine.length!=8) return ReturnCode.WRONG_INPUT_ERROR;
        String hotelName = parsedLine[1];
        String cityName = parsedLine[2];
        double globalScore = Double.parseDouble(parsedLine[3]);
        double[] singleScores = {
                                    Double.parseDouble(parsedLine[4]),
                                    Double.parseDouble(parsedLine[5]),
                                    Double.parseDouble(parsedLine[6]),
                                    Double.parseDouble(parsedLine[7])
                                };

        return this.userIstance.insertReview(hotelName, cityName, globalScore, singleScores);
    }

    private ReturnCode lastBadgeCommand(){
        if(this.userIstance == null || !this.userIstance.isOnline()) return ReturnCode.NOT_LOGGEDIN_ERROR;
        otherInfo = ": "+this.userIstance.getLast_badges().toString();
        return ReturnCode.SUCCESS;
    }
    
    private ReturnCode searchHotelCommand(String[] parsedStrings){
        if(parsedStrings.length != 3) return ReturnCode.WRONG_INPUT_ERROR;
        Hotel searchedHotel = this.hotelManagerIstance.getHotel(parsedStrings[1], parsedStrings[2]);

        if(searchedHotel == null) return ReturnCode.NO_SUCH_HOTEL_ERROR;
        this.otherInfo = ": "+searchedHotel.toString();

        return ReturnCode.SUCCESS;   
    }

    private ReturnCode searchAllHotelCommand(String[] parsedStrings){
        if(parsedStrings.length != 2) return ReturnCode.WRONG_INPUT_ERROR;
        CopyOnWriteArrayList<Hotel> searchedHotels = this.hotelManagerIstance.getHotelsInCity(parsedStrings[1]);
        if(searchedHotels == null) return ReturnCode.NO_SUCH_CITY_ERROR;

        String msg = "";

        for (Hotel hotel : searchedHotels) {
            msg = msg + "\n\t "+hotel.toString();
        }

        this.otherInfo = msg;
        return ReturnCode.SUCCESS;   
    }
    /**
     * Stampa sul terminale del server e manda la stessa stringa al client
     * @param command comando inviato dal client
     * @param ret valore di ritorno
     */
    private void printMessage(String command, ReturnCode ret, Object otherInfo){
        String  msg, resultCode;
        resultCode = "["+ret.toString()+"]";

/*
        //nel caso venga chiesto di inviare l'ultimo badge ricevuto e che la richiesta sia andata a successo
        if(command.compareTo("showmybadge")==0 && ret.equals(ReturnCode.SUCCESS))
                resultCode = otherInfo+": "+resultCode;
*/
    
        msg = new String("'" + command + "'"+otherInfo+" " + resultCode);


        System.out.println("[WORKER: "+this.clientName()+"] " + command + " - " + ret.toString());
        
        this.out.println(msg.replace("\n", "|"));
        this.out.flush();
    }
}
