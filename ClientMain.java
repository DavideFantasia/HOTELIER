/**
* Classe Java che rappresenta il client del gioco.
*
* Il client esegue un ciclo nel quale:
* (1) legge l'input dell'utente da tastiera;
* (2) invia il messaggio letto al server;
* (3) riceve (e interpreta) la risposta del server.
*
* Le principali operazione da eseguire tramite CLI sono:
* (1) register(username,password)
* (2) login(username, password)
* (3) logout
* (4) searchHotel(nomeHotel, città)
* (5) serchAllHotels(città)
* (6) insertReview(nomeHotel, nomeCittà, GlobalScore, SingleScores[ ])
* (7) showMyBadges()
*/

public class ClientMain {
    public static void main(String[] args) {
       ;
    }
    /**
     * Classe effettiva del client che verrà poi inizializzato nel metodo main
     */
    public static class Client{
        private boolean loggedInStatus = false;

        /**
         * Prova a registrare l'utente con la coppia {@code <username, password>} sul servizio. Se la registrazione non va a buon fine viene
         * ritornato un valore diverso dallo zero (0)
         * Cambia lo stato della variabile booleana {@code loggedInStatus} in true se l'operazione va a buon fine
         *
         * @param username lo username scelto dall'utente con cui registrarsi sul portale
         * @param password la password in chiaro scelta dall'utente con la quale registrarsi sul portale
         * 
         * @return il codice intero di avvenuta registrazione o di errore
         */
        int register(String username, String password){
            return 0;
        }

        /**
         * Prova ad effettuare un log-in sul servizio attraverso la coppia {@code <username, password>}, se il log-in non va a buon fine
         * viene ritornato un valore diverso dallo zero (0).
         * Cambia lo stato della variabile booleana {@code loggedInStatus} in true se l'operazione va a buon fine
         * 
         * @param username lo username dell'utente con cui fare log-in sul portale
         * @param password la password in chiaro dell'utente con cui fare log-in sul portale
         * 
         * @return il codice intero di avvenuto log-in o di errore
         */
        int login(String username, String password){
            return 0;
        }
        /**
         * Effettua il log-out dal servizio. Cambia lo stato della variabile booleana {@code loggedInStatus} in false
         * 
         * @param username lo username dell'utente da cui fare log-out
         */
        void logout(String username){
            ;
        }
        /**
         * Metodo che effettua la ricerca di un hotel, ottenendo le info su quel Hotel come stringa
         * @param nomeHotel nome dell'hotel da cercare
         * @param città città di appartenenza dell'hotel
         * @return una stringa contenente il toString() dell'oggetto Hotel riconvertito in un pretty print
         */
        String searchHotel(String nomeHotel, String città){
            String result = "";
            return result;
        }
    }
}