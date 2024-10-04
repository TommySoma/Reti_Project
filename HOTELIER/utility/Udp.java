package utility;

import java.io.IOException;
import java.net.*;

/**
 * La classe Udp rappresenta un thread che si connette a un gruppo multicast e riceve messaggi 
 * dal server finché l'utente è loggato. Utilizza un socket multicast per ascoltare i messaggi.
 */
public class Udp extends Thread {
    // Variabile di controllo per il login dell'utente
    private Login login;
    
    // Indirizzo del gruppo multicast
    private final String Addressname;
    
    // Porta del gruppo multicast
    private final int port;

    /**
     * Costruttore della classe Udp.
     * 
     * @param login Oggetto Login per controllare lo stato di login dell'utente
     * @param Addressname Nome dell'indirizzo del gruppo multicast
     * @param port Porta del gruppo multicast
     */
    public Udp(Login login, String Addressname, int port) {
        this.login = login;
        this.Addressname = Addressname;
        this.port = port;
    }

    /**
     * Metodo eseguito quando il thread viene avviato. Si occupa di connettersi al gruppo multicast
     * e di ricevere messaggi finché l'utente è loggato.
     */
    public void run() {
        MulticastSocket socketClient = null;
        try {
            // Creo una socket multicast sulla porta specificata
            socketClient = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        InetSocketAddress group = null;
        try {
            // Ottengo l'indirizzo del gruppo multicast
            group = new InetSocketAddress(InetAddress.getByName(Addressname), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        NetworkInterface netIf = null;
        try {
            // Ottengo l'interfaccia di rete per il gruppo multicast
            netIf = NetworkInterface.getByName("wlan");
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        try {
            // Mi unisco al gruppo multicast
            socketClient.joinGroup(group, netIf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            // Attendo i messaggi inviati dal server finché l'utente è loggato
            if (login.getIsLogged()) {
                boolean isReceived = true;
                byte[] a = new byte[150];
                DatagramPacket receivedPacket = new DatagramPacket(a, a.length);
                try {
                    // Imposto il timeout della socket
                    socketClient.setSoTimeout(10000);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                try {
                    // Ricevo i dati dal server
                    socketClient.receive(receivedPacket);
                } catch (IOException e) {
                    // Non ho ricevuto i dati, è scaduto il timeout
                    isReceived = false;
                }
                if (isReceived) {
                    // Dati ricevuti
                    String s = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                    System.out.println(s);
                }
            } else
                break;
        }
        
        // L'utente non è più loggato, quindi chiudo il thread
        try {
            socketClient.leaveGroup(group, netIf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketClient.close();
    }
}
