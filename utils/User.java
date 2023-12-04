package utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.*;

/**
 * Classe che rappresenta uno User. Per istanziare un nuovo Oggetto di tipo User bisogna usare il metodo User.registerUser()
 */
public class User {

    private static HotelManager hotelManager;
    private static final String userDataFile = "UserData.json";
    // struttura usata per ricordare gli utenti presenti nel sistema
    private static Map<String, User> listOfUsers = new HashMap<>();

    private String username;
    private String password;
    private Badges last_badges;
    private int pubblished_review;

    private boolean isLogged;
    //il costruttore va chiamato solo durante la registrazione
    private User(String userName, String plainPassword){
        try{
            this.username = userName;
            this.last_badges = Badges.RECENSORE;
            this.password = Hashing.digestString(plainPassword);
            this.pubblished_review = 0;
            this.isLogged = true;
        }catch(Exception e){e.printStackTrace();}
    }
    /**
     * Metodo che registra un nuovo utente nel sistema, returnando l'istanza dell'utente registrato
     * @param username
     * @param password
     * @return un'istanza di questo nuovo user
     * @throws Exception se l'utente che si vuole iscrivere ha uno username già usato da qualcun altro
     */
    public static User registerUser(String username, String password){
        User newUser = new User(username, password);
        if(listOfUsers.containsKey(newUser.getUsername())) return null;
        listOfUsers.computeIfAbsent(newUser.getUsername(), k->newUser ); 
        return newUser;
    }
    /**
     * Metodo usato per ottenere l'istanza User di un determinato utente se, se ne posseggono le credenziali
     * @param username
     * @param plainPassword
     * @return L'istanza dell'utente.
     */
    public static User logIn(String username, String plainPassword){
        User user = null;
        try{
            if(listOfUsers.containsKey(username)){
                //hanno la stessa password e l'utente non è già online
                User sameNameUser = listOfUsers.get(username);
                if(sameNameUser.password.compareTo(Hashing.digestString(plainPassword)) == 0 && (sameNameUser.isLogged == false)){
                    //nome e password coincidono
                    user = sameNameUser;
                    user.isLogged = true;
                }else{
                    return null;
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return user;
    }
    /**
     * funzione che effettua il logout dell'utente
     */
    public synchronized ReturnCode logOut(){
        if(this.isLogged == false) return ReturnCode.NOT_LOGGEDIN_ERROR;
        this.isLogged = false;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File jsonFile  = new File(userDataFile);

        try(OutputStream outputStream = new FileOutputStream(jsonFile)){

            outputStream.write(gson.toJson(listOfUsers.values().toArray()).getBytes());
            outputStream.flush();

        }catch(IOException e){e.printStackTrace();}

        return ReturnCode.SUCCESS;
    }
    /**
     * Metodo che passa una nuova recensione dell'utente al metodo "update" del relativo hotel
     * 
     * @param nomeHotel     Nome dell'hotel
     * @param nomeCitta     Città di appartenenza dell'hotel
     * @param GlobalScore   Intero che rappresenta il punteggio generale con cui l'utente riassume la recensione
     * @param SingleScores  E' un array di interi che rappresenta i singoli punteggi di ogni hotel: "cleaning", "position", "services", "quality", in questo ordine
     * @return              Un codice che rappresenta il successo o gli eventuali errori avvenuti durante l'eleborazione della recensione
     */
    public ReturnCode insertReview(String nomeHotel, String nomeCitta, double GlobalScore, double[] SingleScores){
        if(this.isLogged == false) return ReturnCode.NOT_LOGGEDIN_ERROR;
        assert(SingleScores.length == 4);

        Hotel searchedHotel = hotelManager.getHotel(nomeHotel, nomeCitta);
        //controllo di esistenza dell'hotel ricercato
        if(searchedHotel == null) return ReturnCode.NO_SUCH_HOTEL_ERROR;
        //si aggiorna il Punteggio Globale
        GlobalScore = (searchedHotel.getRate()+GlobalScore)/2;
        searchedHotel.setRate(GlobalScore);

        Map<String, Double> ratings = searchedHotel.getRatings();
        HashMap<String, Double> userRating = new HashMap<String,Double>();
        userRating.put("cleaning", SingleScores[0]);
        userRating.put("position", SingleScores[1]);
        userRating.put("services", SingleScores[2]);
        userRating.put("quality", SingleScores[3]);
        //peso che l'ultimo badge dell'utente ha sulla votazione
        double badge_value = (double)(this.getLast_badges().ordinal()+1);

        Map<String, Double> newRating = new HashMap<String,Double>();
        //calcolo delle nuove votazioni pesate sulla nuova recensione
        userRating.forEach((k,v)->{
            double newValue = (ratings.get(k)+(badge_value*v))/(1.00+badge_value); //media pesata
            newRating.put(k, newValue);
        });
        searchedHotel.setRatings(userRating);
        //questo metodo scrive sul file persistente contenente i dati dell'hotel
        hotelManager.updateHotelInfo();
        this.pubblished_review = this.pubblished_review + 1;
        return ReturnCode.SUCCESS;
    }
    /**
     * Fornisce l'ultimo badge acquisito, ogni 5 recensioni un nuovo badge fino al grado massimo.
     * @return il nuovo grado acquisito
     */
    public Badges getLast_badges() {
        if(this.isLogged == false) return null;
        //ogni 5 votazioni c'è uno scalo di grado
        int grado = Math.floorDiv(this.pubblished_review,5);
        switch (grado) {
            case 0:
                this.last_badges = Badges.RECENSORE;
                break;
            case 1:
                this.last_badges = Badges.RECENSORE_ESPERTO;
                break;
            case 2:
                this.last_badges = Badges.CONTRIBUTORE;
                break;
            case 3:
                this.last_badges = Badges.CONTRIBUTORE_ESPERTO;
            default:
                this.last_badges = Badges.CONTRIBUTORE_SUPER;
                break;
        }
        return this.last_badges;
    }

    public String getUsername() {
        return username;
    }

    public static void dataBooting(HotelManager hotelManagerIstance){
        hotelManager = hotelManagerIstance;
        try{
            String jsonContent = new String(Files.readAllBytes(Paths.get(userDataFile)));
            Gson gson = new Gson();

            for(User user : gson.fromJson(jsonContent, User[].class)){
                listOfUsers.put(user.username, user);
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
                e.printStackTrace();
        }
    }
}
