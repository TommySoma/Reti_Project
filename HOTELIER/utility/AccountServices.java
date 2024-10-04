package utility;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe per gestire i servizi relativi agli account.
 * Questa classe fornisce funzionalità di login e logout per gli account utente, 
 * utilizzando servizi JSON per la persistenza dei dati degli account.
 */
public class AccountServices {
    private JsonServices jsonServices; // Servizio per la gestione dei dati JSON
    private CopyOnWriteArrayList<Account> AllAccount; // Lista thread-safe di tutti gli account

    // Costruttore che inizializza il servizio JSON e carica tutti gli account
    public AccountServices(JsonServices jsonServices) {
        this.jsonServices = jsonServices;
        AllAccount = jsonServices.getAllAccount();
    }

    //------------------------------------------------LOGIN--------------------------------------------------------------
    /**
     * Metodo sincronizzato per effettuare il login di un utente.
     * @param username Nome utente
     * @param password Password
     * @return Messaggio di stato del login
     * @throws IOException Se si verifica un errore di I/O durante l'operazione
     */
    public synchronized String login(String username, String password) throws IOException {
        Account account;
        // Verifica se non ci sono utenti registrati
        if (AllAccount.isEmpty())
            return "nessun utente registrato";

        // Itera su tutti gli account per trovare l'utente con lo username specificato
        for (Account value : AllAccount) {
            account = value;
            // Check dello username
            if (account.getUsername().equals(username)) {
                // Check della password
                if (account.getPassword().equals(password)) {
                    // Verifica se l'utente è già loggato
                    if (value.isLoggedIn()) {
                        return "utente gia' loggato in un'altra sessione, impossibile connettersi con piu' dispositivi contemporaneamente";
                    }
                    // Imposta l'utente come loggato
                    value.setLoggedIn(true);
                    return "utente loggato";
                } else {
                    return "password errata";
                }
            }
        }
        // Se nessun utente corrisponde allo username
        return "utente non trovato";
    }

    //-----------------------------------------------------LOGOUT------------------------------------------------------------
    /**
     * Metodo sincronizzato per effettuare il logout di un utente.
     * @param utente L'account dell'utente da disconnettere
     * @return Messaggio di stato del logout
     * @throws IOException Se si verifica un errore di I/O durante l'operazione
     */
    public synchronized String logout(Account utente) throws IOException {
        Account account;
        // Verifica se non ci sono utenti registrati
        if (AllAccount.isEmpty()) {
            return "nessun utente registrato";
        }
        // Itera su tutti gli account per trovare l'utente con lo username specificato
        for (Account value : AllAccount) {
            account = value;
            // Check dello username per trovare l'utente in questione
            if (account.getUsername().equals(utente.getUsername())) {
                // Imposta l'utente come disconnesso
                value.setLoggedIn(false);
                return "logout effettuato";
            }
        }
        // Condizione a cui non dovrebbe mai arrivare
        return "account non trovato";
    }
}
