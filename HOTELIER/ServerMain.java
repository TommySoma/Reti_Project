/**
 * La classe ServerMain gestisce il server dell'applicazione Hotelier.
 * Il server utilizza RMI per la registrazione degli utenti e callback, TCP per la gestione delle connessioni client,
 * e multicast UDP per la gestione delle notifiche.
 * Utilizza un pool di thread per gestire le richieste in arrivo dai client.
 */

 import utility.*; // Importazione delle utility necessarie

 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.ServerSocket;
 import java.net.Socket;
 import java.nio.channels.AlreadyBoundException;
 import java.rmi.registry.LocateRegistry;
 import java.rmi.registry.Registry;
 import java.rmi.server.UnicastRemoteObject;
 import java.util.Properties;
 import java.util.concurrent.CopyOnWriteArrayList;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 
 class ServerMain {
     // File di configurazione del server
     public static final String configFile = "server.properties";
     public static final String hotelJSON = "Hotels.json";
 
     // Variabili di configurazione
     private static int TCPPORT;
     private static int RMIPORT;
     private static int NOTIFYPORT;
     private static int MCASTPORT;
     private static int TIME;
     private static int TIMEUPDATE;
 
     // Indirizzo multicast
     public static final String MULTICAST = "230.0.0.0";
 
     // Pool di thread per gestire le richieste
     public static final ExecutorService threadPool = Executors.newCachedThreadPool();
     public static ServerSocket serverSocket;
 
     public static void main(String[] args) throws Exception {
         // Avvio del server
         serverBooting();
 
         // Creazione delle liste di Account e Hotel
         CopyOnWriteArrayList<Account> allAccount = new CopyOnWriteArrayList<>();
         CopyOnWriteArrayList<HotelServer> allHotel = new CopyOnWriteArrayList<>();
 
         // Configurazione RMI per le callback
         RmiServerImpl server = new RmiServerImpl(allAccount);
         ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 39000);
         String name = "Server";
         LocateRegistry.createRegistry(NOTIFYPORT);
         Registry registry = LocateRegistry.getRegistry(NOTIFYPORT);
         try {
             registry.bind(name, stub);
         } catch (AlreadyBoundException e) {
             e.printStackTrace();
         }
 
         // Inizializzazione dei file JSON
         JsonServices jsonServices = new JsonServices(allAccount, allHotel);
         jsonServices.createNewFile();
         jsonServices.JsonHotel(server, TIME, MULTICAST, MCASTPORT);
         jsonServices.JsonAccount();
         jsonServices.JsonHotelWriter(TIMEUPDATE);
 
         // Configurazione RMI per la registrazione
         RegisterRmi rmi = new RegisterRmi(jsonServices);
         Registration stubRmi = (Registration) UnicastRemoteObject.exportObject(rmi, 0);
         LocateRegistry.createRegistry(RMIPORT);
         Registry r = LocateRegistry.getRegistry(RMIPORT);
         r.rebind("register-rmi", stubRmi);
 
         // Avvio del server TCP e gestione delle connessioni tramite ThreadPool
         ExecutorService threadPool = Executors.newCachedThreadPool();
         int i = 1;
         try (ServerSocket serverSocket = new ServerSocket(TCPPORT)) {
             while (true) {
                 // Attesa di una connessione dal client
                 Socket clientSocket = serverSocket.accept();
                 ClientHandler clientHandler = new ClientHandler(clientSocket, allAccount, allHotel, jsonServices, server, i);
                 i++;
 
                 // Invio del task di gestione del client al ThreadPool
                 threadPool.execute(clientHandler);
             }
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             threadPool.shutdown();
         }
     }
 
     /**
      * Metodo che avvia il server leggendo la configurazione e eseguendo le operazioni di boot necessarie.
      * 
      * @throws Exception se si verifica un errore durante l'avvio
      */
     private static void serverBooting() throws Exception {
         System.out.println("[SERVER] avvio server");
         // Lettura delle configurazioni statiche
         readConfig();
     }
 
     // ---------------------------- FILE DI CONFIGURAZIONE ------------------------------
     /**
      * Metodo che legge il file di configurazione del server.
      * 
      * @throws FileNotFoundException se il file non esiste
      * @throws IOException           se si verifica un errore durante la lettura
      */
     public static void readConfig() throws FileNotFoundException, IOException {
         InputStream input = ServerMain.class.getResourceAsStream(configFile);
         Properties prop = new Properties();
         prop.load(input);
         TCPPORT = Integer.parseInt(prop.getProperty("TCPPORT"));
         RMIPORT = Integer.parseInt(prop.getProperty("RMIPORT"));
         NOTIFYPORT = Integer.parseInt(prop.getProperty("NOTIFYPORT"));
         MCASTPORT = Integer.parseInt(prop.getProperty("MCASTPORT"));
         TIME = Integer.parseInt(prop.getProperty("TIME"));
         TIMEUPDATE = Integer.parseInt(prop.getProperty("TIMEUPDATE"));
         input.close();
     }
 }
 