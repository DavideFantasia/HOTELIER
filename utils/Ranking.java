package utils;

import java.util.HashMap;

/**
 * Classe che mantiene la classifica dei migliori hotel città per città
 * La HashMap mantiene il nome della città e il relativo miglior hotel di quella città
 */
public class Ranking {
    private static HotelManager hotelManager;
    private static HashMap<String, Hotel> TopHotelByCity;

    private Ranking(HotelManager hmIstance){
        hotelManager = hmIstance;
        TopHotelByCity = new HashMap<>();
    }

    public static Ranking boot(HotelManager hotelManager){
        return new Ranking(hotelManager);
    }

    /**
     * restituisce il ranking locale di una città
     * @param citta
     * @return il nome dell'hotel attualmente al primo posto
     */
    public static Hotel getLocalRanking(String citta){  
        Hotel maxRatingHotel = null;
        for(Hotel hotel : hotelManager.getHotelsInCity(citta)){
            if(maxRatingHotel == null || hotel.getRate()>=maxRatingHotel.getRate()){
                maxRatingHotel = hotel; //nuovo massimo
            }
        }
        return maxRatingHotel;
    }

    /**
     * metodo che restituisce il ranking di tutte le città
     * @return returna una copia aggiornata della classifica
     */
    public static HashMap<String, Hotel> getAllRanking(){
        assert(TopHotelByCity != null);
        HashMap<String, Hotel> newRanking = new HashMap<>();
        Hotel topInCity;

        for (String city : hotelManager.getListOfCity()){
            topInCity = getLocalRanking(city);
            newRanking.put(city, topInCity);
        }
        
        return newRanking;
    }
}
