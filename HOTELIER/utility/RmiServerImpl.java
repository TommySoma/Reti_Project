package utility;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La classe RmiServerImpl implementa l'interfaccia ServerInterface ed estende RemoteServer per supportare RMI.
 * Questa classe gestisce la registrazione dei client per le callback e notifica i cambiamenti di posizione degli hotel agli utenti interessati.
 */
public class RmiServerImpl extends RemoteServer implements ServerInterface {
    // Mappa che associa ogni città a una lista di account interessati a quella città
    private Map<String, List<Account>> userPreferences;

    // Mappa che associa ogni account alla sua interfaccia di notifica per le callback
    private Map<Account, NotifyEventInterface> clients;

    // Lista di tutti gli account
    private List<Account> accounts;

    /**
     * Costruttore della classe RmiServerImpl.
     * Inizializza le mappe e popola userPreferences con gli account e le loro città di interesse.
     *
     * @param accounts Lista di account iniziali
     * @throws RemoteException Se c'è un errore di comunicazione RMI
     */
    public RmiServerImpl(List<Account> accounts) throws RemoteException {
        super(); //invoco il costruttore  della superclasse
        clients = new ConcurrentHashMap<>();
        this.accounts = accounts;
        userPreferences = new ConcurrentHashMap<>();

        // Inserisce gli account nelle rispettive liste di città in userPreferences
        for (Account account : accounts) {
            for (String city : account.getCities()) {
                List<Account> listSameCity = userPreferences.get(city);
                if (listSameCity == null)
                    listSameCity = new CopyOnWriteArrayList<>();
                listSameCity.add(account);  //Aggiunge l'account corrente alla lista degli account associati alla città.
                userPreferences.put(city, listSameCity);  //Aggiorna la mappa associando la città corrente alla lista aggiornata degli account.
            }
        }
    }

    /**
     * Metodo per registrare un client per le callback.
     *
     * @param username Nome utente del client
     * @param ClientInterface Interfaccia di notifica del client
     * @throws RemoteException Se c'è un errore di comunicazione RMI
     */
    @Override
    public synchronized void registerForCallback(String username, NotifyEventInterface ClientInterface) throws RemoteException {
        Account account = new Account(username);
        if (!clients.containsKey(account)) {
            Account accountFound = null;

            // Cerca l'account corrispondente nella lista degli account
            for (Account current : accounts) {
                if (current.getUsername().equals(username)) {
                    accountFound = current;
                }
            }
            if (accountFound != null) {
                // Aggiunge il client e l'interfaccia di notifica alla mappa clients
                clients.put(accountFound, ClientInterface);

                // Aggiorna userPreferences per ogni città d'interesse dell'account
                for (String city : accountFound.getCities()) {
                    List<Account> listSameCity = userPreferences.get(city);
                    if (listSameCity == null)
                        listSameCity = new CopyOnWriteArrayList<>();
                    listSameCity.add(account);
                    userPreferences.put(city, listSameCity);
                }
            }
        }
    }

    /**
     * Metodo per de-registrare un client dalle callback.
     *
     * @param username Nome utente del client
     * @throws RemoteException Se c'è un errore di comunicazione RMI
     */
    @Override
    public synchronized void unregisterForCallback(String username) throws RemoteException {
        Account account = new Account(username);
        if (clients.containsKey(account)) {
            Account accountFound = null;

            // Cerca l'account corrispondente nella lista degli account
            for (Account current : accounts) {
                if (current.getUsername().equals(username)) {
                    accountFound = current;
                }
            }
            if (accountFound != null) {
                // Rimuove il client dalla mappa clients
                clients.remove(accountFound);

                // Rimuove l'account da userPreferences per ogni città d'interesse
                for (String city : accountFound.getCities()) {
                    List<Account> listSameCity = userPreferences.get(city);
                    if (listSameCity != null)
                        listSameCity.remove(account);
                }
            }
        }
    }

    /**
     * Metodo per notificare i client di un cambio di posizione di un hotel.
     *
     * @param nameHotel Nome dell'hotel
     * @param oldPosition Vecchia posizione dell'hotel
     * @param newPosition Nuova posizione dell'hotel
     * @param cityName Nome della città in cui si trova l'hotel
     */
    public void notifyHotelPositionChanged(String nameHotel, int oldPosition, int newPosition, String cityName) {
        List<Account> accounts = userPreferences.get(cityName);
        if (accounts == null)
            return;

        for (Account account : accounts) {
            NotifyEventInterface notifyEventInterface = clients.get(account);
            try {
                // Invia la notifica ai client
                notifyEventInterface.NotifyEvent(nameHotel, oldPosition, newPosition, cityName);
            } catch (RemoteException e) {
                System.out.println("errore notifica rmi callback " + e.getMessage());
            }
        }
    }

    /**
     * Metodo per ottenere userPreferences.
     *
     * @return Mappa che associa ogni città a una lista di account interessati a quella città
     */
    public Map<String, List<Account>> getUserPreferences() {
        return userPreferences;
    }
}
