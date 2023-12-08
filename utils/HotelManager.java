package utils;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
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
        /**
         * permette di aggiungere un nuovo hotel alla struttura in modo ordinato
         * @param hotel nuovo hotel da aggiungere all'insieme
         */
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
         * @return un array contenente tutti gli hotel (non ordinati)
         */
        public synchronized CopyOnWriteArrayList<Hotel> getAllHotels(){
            //creiamo un array contenente tutti gli hotel disponibili
            CopyOnWriteArrayList<Hotel> allHotels = new CopyOnWriteArrayList<>();
            //per ogni città, aggiungiamo tutti gli hotel nell'ArrayList
            for (CopyOnWriteArrayList<Hotel> cityHotels : hotelsByCity.values()) {
                allHotels.addAll(cityHotels);
            }
            //ordiniamo gli hotel sulla base dell'ID
            allHotels.sort(Comparator.comparingInt(Hotel::getId));
            return allHotels;
        }


        /**
         *  Metodo che restituisce l'hotel con un dato nome in una data città
         *  @param name nome dell'hotel
         *  @param city nome della città in cui risiede l'hotel
         *  @return l'istanza dell'hotel cercato o {@byte null} se l'hotel non viene trovato
         */
        public Hotel getHotel(String name, String city){
            if(this.hotelsByCity.containsKey(city)){
                for(Hotel hotel : getHotelsInCity(city)){
                    if(hotel.getName().equals(name)) return hotel;
                }
            }
            return null;
        }

        public String[] getListOfCity(){
            return this.hotelsByCity.keySet().toArray(new String[0]);
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
         * Aggiorna il file persistente degl'hotel con le informazioni di RunTime
         */
        public void updateHotelInfo(){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File jsonFile  = new File(hotelJSONPath);

            try(OutputStream outputStream = new FileOutputStream(jsonFile)){

                outputStream.write(gson.toJson(this.getAllHotels()).getBytes());
                outputStream.flush();

            }catch(IOException e){e.printStackTrace();}
        }
    }