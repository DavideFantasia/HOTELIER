package utils;

import java.util.Map;

/**
 * Questa classe serve per mantenere i dati relativi a ciascun hotel
 */
public class Hotel implements Comparable<Hotel>{
    private int id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private String[] services;
    private double rate;
    private Map<String, Double> ratings;

    public int getId() {
        return id;
    }

    // Esempio di setter per l'ID
    public void setId(int id) {
        this.id = id;
    }

    
    // Getter e setter per il nome
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter e setter per la descrizione
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter e setter per la città
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter e setter per il numero di telefono
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter e setter per i servizi
    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    // Getter e setter per il tasso
    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    // Getter e setter per le valutazioni
    public Map<String, Double> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Double> ratings) {
        this.ratings = ratings;
    }

    /**
     * @Override
     * Metodo per la costruzione di una versione pretty del toString
     * @return un pretty print di un istanza di hotel
     */
    public String toString(){
        String result = String.format("%s\n%s\ntelefono: %s\nGlobal Rate: %.2f\nServizi Offerti:\n",this.getName(),this.getDescription(),this.getPhone(),this.getRate());
        
        for(String service : this.getServices()){
            result = result + "\t" + service + "\n";
        }

        for (Map.Entry<String,Double>entry : this.getRatings().entrySet())
            result=result + String.format("%s: %.2f\n", entry.getKey(), entry.getValue());
        return result;
    }

    /**
     * Metodo Compare per poter ordinare lessicograficamente l'array di Hotel nel server
     * @Override
     * @param otherHotel un altra istanza di hotel con cui effettuare la comparazione
     * 
     * @return un intero positivo se il nome dell'hotel segue lessicograficamente la stringa dell'argomento. Il risultato è zero se le stringhe sono uguali
     */
    public int compareTo(Hotel otherHotel) {
        return this.name.compareTo(otherHotel.name);
    }


}
/*
*   Esempio:
*   {
*      "id": 1,
        "name": "Hotel Aosta 1",
        "description": "Un ridente hotel a Aosta, in Via della gioia, 25",
        "city": "Aosta",
        "phone": "347-4453634",
        "services": [
            "TV in camera",
            "Palestra",
            "Cancellazione gratuita"
        ],
        "rate": 0,
        "ratings": {
            "cleaning": 0,
            "position": 0,
            "services": 0,
            "quality": 0
        }
    }
 */