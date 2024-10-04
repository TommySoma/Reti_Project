package utility;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * La classe UdpMulticast si occupa di inviare messaggi multicast tramite pacchetti UDP.
 * Questo è utile per notificare i client di un cambio di classifica degli hotel.
 */
public class UdpMulticast {

    /**
     * Invia un messaggio multicast tramite un pacchetto UDP.
     *
     * @param name        Nome dell'hotel
     * @param city        Città dell'hotel
     * @param vote        Voto globale dell'hotel
     * @param nameAddress Indirizzo del gruppo multicast
     * @param port        Porta del gruppo multicast
     * @throws IOException Se si verifica un errore durante l'invio del pacchetto
     */
    public void sendMulticast(String name, String city, Double vote, String nameAddress, int port) throws IOException {
        // Crea un socket UDP per inviare il pacchetto
        DatagramSocket serverSock = new DatagramSocket();

        // Crea il messaggio da inviare
        String message = "Cambio di classifica, l'attuale hotel primo classificato e' " + name + " nella citta' di " + city + " con un voto globale di " + vote;

        // Converti il messaggio in byte usando la codifica ASCII
        byte[] send = message.getBytes(StandardCharsets.US_ASCII);

        // Crea un pacchetto datagramma con il messaggio, l'indirizzo del gruppo multicast e la porta
        DatagramPacket dp = new DatagramPacket(send, send.length, InetAddress.getByName(nameAddress), port);

        // Invia il pacchetto tramite il socket
        serverSock.send(dp);

        // Chiudi il socket
        serverSock.close();

        // Disconnetti il socket (opzionale in questo caso poiché è già chiuso)
        serverSock.disconnect();
    }
}
