package utility;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe NotifyEventImpl implementa l'interfaccia NotifyEventInterface per ricevere notifiche
 * di aggiornamenti del ranking degli hotel tramite RMI. Estende RemoteObject per fornire 
 * funzionalità di oggetto remoto.
 */
public class NotifyEventImpl extends RemoteObject implements NotifyEventInterface {
    // Struttura dati per salvare le informazioni relative ai ranking di interesse
    private List<HotelRankUpdate> positionHotel;

    /**
     * Costruttore della classe NotifyEventImpl.
     * Inizializza la lista positionHotel.
     * @throws RemoteException in caso di errore di comunicazione RMI.
     */
    public NotifyEventImpl() throws RemoteException {
        super();
        positionHotel = new ArrayList<>();
    }

    /**
     * Metodo chiamato dal server tramite RMI per notificare un cambiamento di posizione di un hotel.
     * @param nameHotel Nome dell'hotel.
     * @param oldPosition Vecchia posizione del ranking.
     * @param newPosition Nuova posizione del ranking.
     * @param cityName Nome della città dell'hotel.
     * @throws RemoteException in caso di errore di comunicazione RMI.
     */
    @Override
    public void NotifyEvent(String nameHotel, int oldPosition, int newPosition, String cityName) throws RemoteException {
        // Crea un oggetto HotelRankUpdate con le informazioni dell'aggiornamento del ranking
        HotelRankUpdate hotelRankUpdate = new HotelRankUpdate(cityName, newPosition, nameHotel, oldPosition);
        // Stampa un messaggio informativo sulla console
        System.out.println("L'hotel " + nameHotel + " ha cambiato posizione: da " + oldPosition + " a " + newPosition);
        // Aggiunge l'aggiornamento del ranking alla lista positionHotel
        positionHotel.add(hotelRankUpdate);
    }
}
