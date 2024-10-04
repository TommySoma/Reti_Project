package utility;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * La classe rmiCallback gestisce la registrazione e la deregistrazione delle callback RMI per ricevere notifiche dal server.
 */
public class rmiCallback {

    // Interfaccia per ricevere notifiche dal server
    NotifyEventInterface callbackObj;
    // Stub dell'oggetto remoto per inviare notifiche
    NotifyEventInterface stub;
    // Interfaccia del server per registrarsi alle notifiche
    ServerInterface server;

    /**
     * Costruttore della classe rmiCallback.
     * Inizializza il registry e ottiene il riferimento al server remoto.
     * 
     * @param port Il numero di porta su cui è in ascolto il registry RMI
     * @param nameServer Il nome del server RMI
     * @throws RemoteException Se c'è un errore di comunicazione RMI
     * @throws NotBoundException Se il nome del server non è legato nel registry
     */
    public rmiCallback(int port, String nameServer) throws RemoteException, NotBoundException {
        // Ottiene il registry RMI al nome e porta specificati
        Registry registry = LocateRegistry.getRegistry(nameServer, port);
        // Nome del servizio di server nel registry
        String name = "Server";
        // Effettua il lookup del servizio di server
        server = (ServerInterface) registry.lookup(name);
    }

    /**
     * Registra l'oggetto per ricevere notifiche di callback dal server.
     * 
     * @param username Il nome utente che si registra per le notifiche
     * @throws RemoteException Se c'è un errore di comunicazione RMI
     */
    public void callBackreg(String username) throws RemoteException {
        // Crea un'istanza di NotifyEventImpl per gestire le notifiche
        callbackObj = new NotifyEventImpl();
        // Esporta l'oggetto remoto per inviare le notifiche
        stub = (NotifyEventInterface) UnicastRemoteObject.exportObject(callbackObj, 0);
        // Registra l'oggetto stub per ricevere notifiche dal server
        server.registerForCallback(username, stub);
    }

    /**
     * Deregistra l'oggetto dal ricevere notifiche di callback dal server.
     * 
     * @param username Il nome utente che si deregistra dalle notifiche
     * @throws RemoteException Se c'è un errore di comunicazione RMI
     */
    public void callBackUnreg(String username) throws RemoteException {
        // Deregistra l'utente dal ricevere notifiche dal server
        server.unregisterForCallback(username);
    }
}
