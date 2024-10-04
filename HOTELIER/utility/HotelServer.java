package utility;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * La classe HotelServer viene utilizzata per serializzare e deserializzare le informazioni degli hotel.
 * Memorizza dettagli come ID, nome, descrizione, città, telefono, servizi offerti, valutazione complessiva,
 * voti per categoria, numero di voti, data dell'ultimo voto, punteggio totale e posizione in classifica.
 */
public class HotelServer {
    // ID dell'hotel
    private int id;
    // Nome dell'hotel
    private String name;
    // Descrizione dell'hotel
    private String description;
    // Città dove si trova l'hotel
    private String city;
    // Numero di telefono dell'hotel
    private String phone;
    // Servizi offerti dall'hotel
    private String[] services;
    // Valutazione complessiva dell'hotel
    private double rate;
    // Mappa concorrente per memorizzare le valutazioni per categoria
    private ConcurrentHashMap<String, Double> ratings;
    // Numero di voti ricevuti dall'hotel (transient non serializzato)
    private transient int nOfVote = 0;
    // Data dell'ultimo voto ricevuto (transient non serializzato)
    private transient LocalDateTime lastVoteDate;
    // Punteggio totale dell'hotel (transient non serializzato)
    private transient double totalScore = 0;
    // Punteggi totali per ogni categoria (transient non serializzato)
    private transient double[] totalSingleScore = {0, 0, 0, 0};
    // Posizione dell'hotel in classifica (transient non serializzato)
    private transient int position = 0;

    // Setter per il nome dell'hotel
    public void setName(String name) {
        this.name = name;
    }

    // Setter per la città dell'hotel
    public void setCity(String city) {
        this.city = city;
    }

    // Getter per la posizione dell'hotel
    public int getPosition() {
        return position;
    }

    // Getter sincronizzato per la data dell'ultimo voto
    public synchronized LocalDateTime getLastVoteDate() {
        return lastVoteDate;
    }

    // Getter sincronizzato per la valutazione complessiva
    public synchronized double getRate() {
        return rate;
    }

    // Getter per la descrizione dell'hotel
    public String getDescription() {
        return description;
    }

    // Setter per la descrizione dell'hotel
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter per il punteggio totale dell'hotel
    public double getTotalScore() {
        return totalScore;
    }

    // Setter per i punteggi totali per ogni categoria
    public void setTotalSingleScore(double[] totalSingleScore) {
        this.totalSingleScore = totalSingleScore;
    }

    // Metodo sincronizzato per calcolare la media delle valutazioni
    public synchronized double getAverageRating() {
        if (ratings.isEmpty()) {
            // Ritorna 0 se non ci sono voti
            return 0.0;
        }
        double sum = 0.0;
        for (double rating : ratings.values()) {
            sum += rating;
        }
        // Calcola e ritorna la media dei voti
        return sum / ratings.size();
    }

    // Metodo non sincronizzato per impostare la posizione dell'hotel
    // Utilizzato una sola volta all'interno di un unico thread
    public void setPosition(int position) {
        this.position = position;
    }

    // Setter sincronizzato per la data dell'ultimo voto
    public synchronized void setLastVoteDate(LocalDateTime lastVoteDate) {
        this.lastVoteDate = lastVoteDate;
    }

    // Metodo sincronizzato per incrementare il numero di voti
    public synchronized void incrementVote() {
        nOfVote++;
    }

    // Getter sincronizzato per il numero di voti
    public synchronized int getnOfVote() {
        return nOfVote;
    }

    // Setter sincronizzato per la valutazione complessiva
    public synchronized void setRate(double rate) {
        this.rate = rate;
    }

    // Setter sincronizzato per il punteggio totale
    public synchronized void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    // Getter sincronizzato per le valutazioni per categoria
    public synchronized ConcurrentHashMap<String, Double> getRatings() {
        return ratings;
    }

    // Getter sincronizzato per i punteggi totali per ogni categoria
    public synchronized double[] getTotalSingleScore() {
        return totalSingleScore;
    }

    // Getter per l'ID dell'hotel
    public int getId() {
        return id;
    }

    // Getter per il nome dell'hotel
    public String getName() {
        return name;
    }

    // Getter per la città dell'hotel
    public String getCity() {
        return city;
    }

    // Override del metodo equals per confrontare gli oggetti HotelServer basandosi sull'ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelServer hotel = (HotelServer) o;
        return id == hotel.id;
    }

    // Override del metodo hashCode per generare un hash basato sull'ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
