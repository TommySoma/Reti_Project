package utility;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe per serializzare e deserializzare le informazioni relative agli account.
 * Questa classe gestisce le informazioni di un account utente, inclusi il nome utente, 
 * la password, il badge, il numero di recensioni, lo stato di login e la lista delle città visitate.
 */
public class Account {
    private String username; // Nome utente
    private String password; // Password dell'account
    private String badge; // Badge dell'account basato sul numero di recensioni
    private int nReview = 0; // Numero di recensioni inserite dall'utente
    private transient boolean loggedIn = false; // Stato di login dell'utente (non serializzabile)
    private List<String> cities = new CopyOnWriteArrayList<>(); // Lista delle città 

    // Getter per il nome utente
    public String getUsername() {
        return username;
    }

    // Setter per il nome utente
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter per la password
    public String getPassword() {
        return password;
    }

    // Setter per la password
    public void setPassword(String password) {
        this.password = password;
    }

    // Costruttore con solo nome utente
    public Account(String username) {
        this.username = username;
    }

    // Costruttore completo
    public Account(String username, String password, String badge, int nRecensioni, boolean loggedIn) {
        this.password = password;
        this.username = username;
        this.badge = badge;
        this.nReview = nRecensioni;
        this.loggedIn = loggedIn;
    }

    // Metodo per incrementare il numero di recensioni inserite dall'utente
    public void incrementReview() {
        nReview++;
    }

    // Getter per la lista delle città
    public List<String> getCities() {
        return cities;
    }

    // Setter per la lista delle città
    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    // Metodo per impostare il badge basato sul numero di recensioni
    public String setBadge() {
        if (nReview < 0) {
            return "Il numero di recensioni non può essere negativo.";
        } else {
            switch (nReview) {
                case 0 -> badge = "Nessun badge";
                case 1 -> badge = "Recensore";
                case 2 -> badge = "Recensore esperto";
                case 3 -> badge = "Contributore";
                case 4 -> badge = "Contributore esperto";
                default -> badge = "Contributore Super";
            }
        }
        return badge;
    }

    // Metodo sincronizzato per impostare lo stato di login
    public synchronized void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    // Metodo sincronizzato per verificare lo stato di login
    public synchronized boolean isLoggedIn() {
        return loggedIn;
    }

    // Override del metodo equals per confrontare gli oggetti Account basandosi sul nome utente
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return username.equals(account.username);
    }

    // Override del metodo hashCode per generare un hash basato sul nome utente
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
