package utility;

import java.time.LocalDateTime;

/**
 * La classe Review rappresenta una recensione di un hotel.
 * Contiene informazioni sull'utente che ha fatto la recensione, la città, il nome dell'hotel,
 * il punteggio globale, i punteggi per diverse categorie e la data della recensione.
 */
public class Review {
    // Nome utente che ha lasciato la recensione
    String username;
    
    // Città in cui si trova l'hotel recensito
    String city;
    
    // Nome dell'hotel recensito
    String nameHotel;
    
    // Punteggio globale della recensione
    int rate;
    
    // Array di punteggi per diverse categorie
    int[] ratings;
    
    // Data della recensione
    LocalDateTime date;

    /**
     * Costruttore per creare una nuova istanza di Review.
     * 
     * @param username Il nome dell'utente che ha lasciato la recensione
     * @param city La città in cui si trova l'hotel
     * @param nameHotel Il nome dell'hotel
     * @param rate Il punteggio globale della recensione
     * @param ratings Array di punteggi per diverse categorie
     * @param date La data in cui è stata lasciata la recensione
     */
    public Review(String username, String city, String nameHotel, int rate, int[] ratings, LocalDateTime date) {
        this.username = username; // Inizializza il nome utente
        this.city = city; // Inizializza la città
        this.nameHotel = nameHotel; // Inizializza il nome dell'hotel
        this.rate = rate; // Inizializza il punteggio globale
        this.ratings = ratings; // Inizializza l'array dei punteggi per le categorie
        this.date = date; // Inizializza la data della recensione
    }
}
