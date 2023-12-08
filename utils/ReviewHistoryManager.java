package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;

/**
 * classe singleton per gestire lo storico delle recensioni
 */
public class ReviewHistoryManager {

    private static final String historyJSON = "ReviewHistory.json";
    //hashmap fra il nome della città e la città
    private HashMap<String, CityReviewHistory> globalReviewHistory; // La lista delle città con i loro hotel e recensioni

    private static ReviewHistoryManager INSTANCE;

    private ReviewHistoryManager(){
        this.globalReviewHistory = new HashMap<String,CityReviewHistory>();
        loadReview();
    }
    //metodo pubblico 
    public static ReviewHistoryManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReviewHistoryManager();
        }
        return INSTANCE;
    }

    public void addReview(String username,String nomeCitta,String nomeHotel,Double GlobalScore, HashMap<String, Double> userRating){
        
        if(this.globalReviewHistory == null){
            this.globalReviewHistory = new HashMap<>();
        }

        Review nReview = new Review(username, GlobalScore, userRating);

        //se la città non è segnata
        if(!this.globalReviewHistory.containsKey(nomeCitta)){
            this.globalReviewHistory.put(nomeCitta, new CityReviewHistory(nomeCitta));
        }
        
        CityReviewHistory cityReviewHistory = this.globalReviewHistory.get(nomeCitta);

        //se l'hotel non è segnato
        if(!cityReviewHistory.hotelReviews.containsKey(nomeHotel)){
            this.globalReviewHistory.get(nomeCitta).hotelReviews.put(nomeHotel, new CopyOnWriteArrayList<Review>());
        }

        cityReviewHistory.hotelReviews.get(nomeHotel).add(nReview);
        saveReview();
    }

    public void saveReview() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(historyJSON)) {
            gson.toJson(globalReviewHistory, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadReview() {
        Gson gson = new Gson();
        Type cityReviewHistoryType = new TypeToken<HashMap<String, CityReviewHistory>>() {}.getType();

        try (BufferedReader reader = new BufferedReader(new FileReader(historyJSON))) {
            this.globalReviewHistory = gson.fromJson(reader, cityReviewHistoryType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfReview(String nomeCitta, String nomeHotel){
        CityReviewHistory city =this.globalReviewHistory.getOrDefault(nomeCitta, null);
        if(city == null) return 0;
        
        CopyOnWriteArrayList<Review> hotel = city.hotelReviews.getOrDefault(nomeHotel, null);
        if(hotel == null) return 0;

        return hotel.size();
    }


    /**
     * classe che descrive le recensioni di tutti gli hotel in città
     */
    public static class CityReviewHistory{
        public String cityName;
        public HashMap<String, CopyOnWriteArrayList<Review>> hotelReviews;

        public CityReviewHistory(String cityName){
            this.cityName = cityName;
            this.hotelReviews = new HashMap<String, CopyOnWriteArrayList<Review>>();
        }
    }

     private static class Review {
        private String username;
        private Double globalScore;
        private Map<String, Double> userRating;

        public Review(String username, Double globalScore, Map<String, Double> userRating){
            this.username = username;
            this.globalScore = globalScore;
            this.userRating = userRating;
        }

        public Double getGlobalScore() {
            return globalScore;
        }

        public Map<String, Double> getUserRating() {
            return userRating;
        }

        public String getUsername() {
            return username;
        }
    }
}


