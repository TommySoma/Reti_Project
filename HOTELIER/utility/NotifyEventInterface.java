package utility;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * L'interfaccia NotifyEventInterface definisce i metodi che devono essere implementati
 * per ricevere notifiche di aggiornamenti del ranking degli hotel tramite RMI (Remote Method Invocation).
 * Estende l'interfaccia Remote per abilitare la comunicazione remota.
 */
public interface NotifyEventInterface extends Remote {
    /**
     * Metodo per notificare un cambiamento di posizione di un hotel nel ranking.
     * Questo metodo deve essere implementato dalle classi che desiderano ricevere
     * notifiche sugli aggiornamenti del ranking degli hotel.
     * 
     * @param nameHotel Nome dell'hotel.
     * @param oldPosition Vecchia posizione del ranking.
     * @param newPosition Nuova posizione del ranking.
     * @param cityName Nome della città dell'hotel.
     * @throws RemoteException in caso di errore di comunicazione RMI.
     */
    public void NotifyEvent(String nameHotel, int oldPosition, int newPosition, String cityName) throws RemoteException;
}
