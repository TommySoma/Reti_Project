package utility;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * L'interfaccia ServerInterface definisce i metodi per la registrazione e la deregistrazione dei client
 * per le callback tramite RMI. Estende l'interfaccia Remote per supportare la comunicazione remota.
 */
public interface ServerInterface extends Remote {

    /**
     * Metodo per registrare un client per le callback.
     * 
     * @param username Nome utente del client
     * @param ClientInterface Interfaccia di notifica del client
     * @throws RemoteException Se c'è un errore di comunicazione RMI
     */
    void registerForCallback(String username, NotifyEventInterface ClientInterface) throws RemoteException;

    /**
     * Metodo per deregistrare un client dalle callback.
     * 
     * @param username Nome utente del client
     * @throws RemoteException Se c'è un errore di comunicazione RMI
     */
    void unregisterForCallback(String username) throws RemoteException;
}
