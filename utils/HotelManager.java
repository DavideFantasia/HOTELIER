package utils;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.*;

/**
 * classe (SINGLETON) che funge da interfaccia per interagire con gli hotel.
 * Quando si istanzia questa classe per la prima volta viene automaticamente caricato il file JSON contenente
 * tutte le info sugli Hotel.
 */
public class HotelManager {
        /**
         * Questa struttura dati contiene le istanze degli hotel letti dal json.
         * Ogni capoluogo è una chiave in una (concurrent) Hash Map il cui valore è un CopyOnWrite Array List 
         * contenente tutti gli hotel del capoluogo.
         * Gli Hotel contenuti nel relativo CopyOnWrite Array List, sono ordinati in maniera lessicografica.
         */
        private ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> hotelsByCity;
        private String hotelJSONPath;

        public HotelManager(String hotelJSONPath) {
            this.hotelsByCity = new ConcurrentHashMap<>();
            this.hotelJSONPath = hotelJSONPath;
            hotelReaderJSON();
            
        }
    
        public void addHotel(Hotel hotel) {
            String city = hotel.getCity();
            this.hotelsByCity.computeIfAbsent(city, k -> new CopyOnWriteArrayList<Hotel>());
            CopyOnWriteArrayList<Hotel> hotelsInCity = this.hotelsByCity.get(city);

            //blocco sincronizzato per inserire il nuovo hotel
            synchronized(hotelsInCity){
                //troviamo il posto in cui inserire il nuovo hotel
                int index = 0;
                for (Hotel h : hotelsInCity) {
                    if (hotel.compareTo(h) <= 0) {
                        break;
                    }
                    index++;
                }
        
                hotelsInCity.add(index, hotel);
            }
        }
    
        public CopyOnWriteArrayList<Hotel> getHotelsInCity(String city){
            return hotelsByCity.getOrDefault(city, null);
        }
        /**
         * @return un array contenente tutti gli hotel (non ordinati), a scopo di testing
         */
        public Enumeration<CopyOnWriteArrayList<Hotel>> getAllHotels(){
            return hotelsByCity.elements();
        }


        /**
         * 
         */
        public Hotel getHotel(String name, String city){
            Hotel result = null;
            Hotel[] listOfHotels = new Hotel[this.hotelsByCity.get(city).size()];
            listOfHotels = this.hotelsByCity.get(city).toArray(listOfHotels);
            
            int index = hotelBinarySearch(listOfHotels, name);
            result= (index>=0)? listOfHotels[index] : null;
    
            return result;
        }

        /**
         * Metodo che legge il file JSON con i dati degli hotel e li salva nella variabile hotelManager.
         * 
         * @throws FileNotFoundException se il file non esiste
         * @throws IOException           se si verifica un errore durante la lettura
         */
        private void hotelReaderJSON(){
            try{
                String jsonContent = new String(Files.readAllBytes(Paths.get(this.hotelJSONPath)));
                Gson gson = new Gson();

                for(Hotel hotel : gson.fromJson(jsonContent, Hotel[].class)){
                    this.addHotel(hotel);
                }

            }catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            
        }
        /**
         * Ricerca di un hotel specifico in base al nome nell'array ordinato di hotel per singola città
         * 
         * @param hotelsInACity l'array di hotel in una città 
         * @param name nome dell'hotel
         * @return l'indice dell'hotel nell'array, -1 se non presente
         */
        int hotelBinarySearch(Hotel hotelsInACity[], String name){
            int l = 0, r = hotelsInACity.length - 1;
            while (l <= r) {
                int m = l + (r - l) / 2;
    
                // Check if x is present at mid
                if (hotelsInACity[m].getName().compareTo(name)==0)
                    return m;
    
                // If x greater, ignore left half
                if (hotelsInACity[m].getName().compareTo(name) < 0)
                    l = m + 1;
    
                // If x is smaller, ignore right half
                else
                    r = m - 1;
            }
    
            // If we reach here, then element was
            // not present
            return -1;
        }
    }