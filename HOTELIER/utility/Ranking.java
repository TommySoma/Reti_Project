package utility;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La classe Ranking è responsabile dell'aggiornamento periodico del ranking degli hotel
 * e della notifica dei cambiamenti di posizione degli hotel. Questa classe estende Thread 
 * e viene eseguita in un ciclo continuo per aggiornare il ranking ad intervalli regolari.
 */
public class Ranking extends Thread {
    // Mappa concorrente che contiene gli hotel raggruppati per città
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<HotelServer>> mapCity;
    // Lista concorrente delle città
    private final CopyOnWriteArrayList<String> city;
    // Tempo di attesa tra gli aggiornamenti del ranking in millisecondi
    private final int time;
    // Riferimento al server RMI per notificare i cambiamenti di posizione degli hotel
    private final RmiServerImpl server;
    // Indirizzo e porta per l'invio dei messaggi multicast
    private final String nameAddress;
    private final int port;

    /**
     * Costruttore della classe Ranking.
     * 
     * @param mapCity Mappa degli hotel raggruppati per città.
     * @param city Lista delle città.
     * @param time Intervallo di tempo tra gli aggiornamenti del ranking.
     * @param server Riferimento al server RMI.
     * @param nameAddress Indirizzo per i messaggi multicast.
     * @param port Porta per i messaggi multicast.
     */
    public Ranking(ConcurrentHashMap<String, CopyOnWriteArrayList<HotelServer>> mapCity, CopyOnWriteArrayList<String> city, int time, RmiServerImpl server, String nameAddress, int port) {
        this.mapCity = mapCity;
        this.city = city;
        this.time = time;
        this.server = server;
        this.nameAddress = nameAddress;
        this.port = port;
    }

    /**
     * Metodo run eseguito dal thread per aggiornare il ranking degli hotel in un ciclo continuo.
     */
    @Override
    public void run() {
        while (true) {
            updateRankings();
            try {
                Thread.sleep(time); // Attende l'intervallo di tempo specificato prima di aggiornare nuovamente
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metodo privato per aggiornare il ranking degli hotel.
     */
    private void updateRankings() {
        // Mappa per memorizzare le posizioni correnti degli hotel
        Map<HotelServer, Integer> currentPositions = new ConcurrentHashMap<>();
        // Array per memorizzare gli ID delle prime posizioni per ogni città
        int[] currentRank = new int[mapCity.size()];

        // Salva le posizioni correnti degli hotel prima del ranking
        for (int i = 0; i < city.size(); i++) {
            List<HotelServer> hotels = mapCity.get(city.get(i));
            if (hotels == null || hotels.isEmpty()) continue;
            int id = hotels.get(0).getId();
            currentRank[i] = id;
            for (HotelServer hotel : hotels)
                currentPositions.put(hotel, hotel.getPosition());
        }

        // Aggiorna le posizioni degli hotel in base al nuovo ranking
        for (List<HotelServer> hotels : mapCity.values()) {
            // Ordina la lista degli hotel utilizzando la classe HotelComparator
            hotels.sort(new HotelComparator());

            // Imposta le nuove posizioni degli hotel
            for (int i = 0; i < hotels.size(); i++) {
                HotelServer hotel = hotels.get(i);
                int oldPosition = currentPositions.getOrDefault(hotel, -1);
                hotel.setPosition(i + 1);

                // Se le posizioni sono cambiate, notifica l'evento tramite callback RMI
                if (hotel.getPosition() != oldPosition) {
                    server.notifyHotelPositionChanged(hotel.getName(), oldPosition, hotel.getPosition(), hotel.getCity());
                }
            }
        }

        // Se la prima posizione è cambiata, invia un messaggio multicast a tutti gli utenti loggati
        for (int i = 0; i < mapCity.size(); i++) {
            List<HotelServer> hotels = mapCity.get(city.get(i));
            if (hotels == null || hotels.isEmpty()) continue;
            if (currentRank[i] != hotels.get(0).getId()) {
                UdpMulticast udpMulticast = new UdpMulticast();
                try {
                    udpMulticast.sendMulticast(hotels.get(0).getName(), city.get(i), hotels.get(0).getRate(), nameAddress, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
