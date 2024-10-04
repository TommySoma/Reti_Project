package utility;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe usata per deserializzare le informazioni relative agli hotel.
 * Questa classe rappresenta un hotel con tutte le sue proprietà e fornisce
 * metodi per stampare le informazioni dell'hotel in un formato leggibile.
 */
public class Hotel {
    // ID univoco dell'hotel
    private int id;
    // Nome dell'hotel
    private String name;
    // Descrizione dell'hotel
    private String description;
    // Città in cui si trova l'hotel
    private String city;
    // Numero di telefono dell'hotel
    private String phone;
    // Servizi offerti dall'hotel
    private String[] services;
    // Valutazione media dell'hotel
    private double rate;
    // Mappa delle valutazioni per categoria (es. pulizia, posizione, ecc.)
    private ConcurrentHashMap<String, Double> ratings;
    // Numero di voti ricevuti (transient: non viene serializzato)
    transient int nOfVote = 0;
    // Data dell'ultimo voto ricevuto (transient: non viene serializzato)
    transient LocalDateTime lastVoteDate;
    // Punteggio totale (transient: non viene serializzato)
    transient double totalScore = 0;
    // Punteggi totali per singola categoria (transient: non viene serializzato)
    transient double[] totalSingleScore = {0, 0, 0, 0};
    // Posizione dell'hotel (transient: non viene serializzato)
    transient int position = 0;

    /**
     * Metodo per stampare le informazioni rilevanti dell'hotel.
     * Le informazioni includono nome, descrizione, città, telefono,
     * servizi, voto globale e voti alle singole categorie.
     */
    public void printAll() {
        // Formattatore per i numeri decimali
        DecimalFormat df = new DecimalFormat("0.00");
        // Variabile per memorizzare temporaneamente il punteggio di una categoria
        double a;

        // Stampa il nome dell'hotel
        System.out.println("nome: " + name);
        // Stampa la descrizione dell'hotel
        System.out.println("descrizione: " + description);
        // Stampa la città in cui si trova l'hotel
        System.out.println("città: " + city);
        // Stampa il numero di telefono dell'hotel
        System.out.println("telefono: " + phone);

        // Stampa la lista dei servizi offerti dall'hotel
        System.out.println("servizi: ");
        for (String service : services) {
            System.out.println(service);
        }

        // Stampa il voto globale dell'hotel, formattato a due decimali
        System.out.println("voto globale: " + df.format(rate));

        // Stampa i voti alle singole categorie
        System.out.println("voti alle singole categorie: ");

        // Stampa il voto per la categoria "cleaning"
        a = ratings.get("cleaning");
        System.out.print("cleaning: ");
        System.out.println(df.format(a));

        // Stampa il voto per la categoria "position"
        a = ratings.get("position");
        System.out.print("position: ");
        System.out.println(df.format(a));

        // Stampa il voto per la categoria "services"
        a = ratings.get("services");
        System.out.print("services: ");
        System.out.println(df.format(a));

        // Stampa il voto per la categoria "quality"
        a = ratings.get("quality");
        System.out.print("quality:  ");
        System.out.println(df.format(a));
    }
}
