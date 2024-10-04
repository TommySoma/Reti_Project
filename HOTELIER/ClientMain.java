/**
 * La classe ClientMain gestisce il client di un'applicazione di valutazione degli hotel.
 * Consente all'utente di registrarsi, effettuare il login, cercare hotel, inserire recensioni,
 * visualizzare i badge e impostare città di interesse tramite comandi testuali.
 * La comunicazione con il server avviene tramite connessioni TCP, RMI per la registrazione
 * e multicast UDP per la ricezione delle notifiche.
 */

 import utility.*;
 import utility.Error;
 import java.io.BufferedReader;
 import java.io.DataOutputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.InetSocketAddress;
 import java.net.Socket;
 import java.net.SocketAddress;
 import java.net.SocketException;
 import java.rmi.NotBoundException;
 import java.rmi.Remote;
 import java.rmi.RemoteException;
 import java.rmi.registry.LocateRegistry;
 import java.rmi.registry.Registry;
 import java.util.List;
 import java.util.Properties;
 import java.util.Scanner;
 import com.google.gson.Gson;
 import com.google.gson.reflect.TypeToken;
 
 public class ClientMain {
     // File di configurazione
     public static final String configFile = "client.properties";
 
     // Variabili di configurazione
     private static int TCPPORT;
     private static int RMIPORT;
     private static int NOTIFYPORT;
     private static int MCASTPORT;
 
     // Indirizzi di server e multicast
     public static final String SERVER = "localhost";
     public static final String MULTICAST = "230.0.0.0";
 
     public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
         // Lettura del file di configurazione
         readConfig();
 
         // Istanza per il login dell'utente
         Login login = new Login(false);
         
         // Istanza del Thread che si occupa di ricevere i messaggi multicast
         Udp udp = new Udp(login, MULTICAST, MCASTPORT);
 
         try {
             // Connessione TCP al server
             Socket s = new Socket();
             SocketAddress address = new InetSocketAddress(SERVER, TCPPORT);
             s.connect(address);
 
             // RMI Callback per notifiche
             rmiCallback rmiCallback = new rmiCallback(NOTIFYPORT, SERVER);
 
             // Associazione degli stream di input e output al socket
             BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
             DataOutputStream writer = new DataOutputStream(s.getOutputStream());
 
             String reply;
             Menu.mainText();
 
             String username = null;
 
             // Ciclo principale per la gestione dei comandi utente
             while (true) {
                 System.out.println("\n" +
                                    "*****************************************************\n" +
                                    "* Digita help per visualizzare la lista dei comandi *\n" +
                                    "*****************************************************");
 
                 String[] arg;
                 String[] token;
 
                 // Input da tastiera
                 Scanner input = new Scanner(System.in);
                 String action = input.nextLine();
                 Error error = new Error();
 
                 // Divisione del comando (token[0]) dall'argomento (token[1]) mediante uno spazio
                 token = action.split(" ", 2);
 
                 // Gestione dei comandi
                 switch (token[0]) {
                     //----------------------------------------------REGISTRAZIONE-----------------------------------------------------------
                     case "register" -> {
                         // Verifica se l'utente è già loggato
                         if (login.getIsLogged()) {
                             System.out.println("Non è possibile registrare nessun utente se si è loggati");
                             break;
                         }
                         if (token.length < 2) {
                             System.out.println("Errore nella trascrizione");
                             break;
                         }
 
                         // Divisione degli argomenti separati da una virgola
                         arg = token[1].split(",");
                         
                         // Controllo degli errori
                         if (!error.Check(token[0], arg)) {
                             // Registrazione tramite RMI
                             action = rmi(action, RMIPORT);
                             if (action.equals("utente già registrato"))
                                 System.out.println(action);
                             if (action.equals("utente registrato")) {
                                 action = "login " + arg[0] + "," + arg[1];
                                 token = action.split(" ", 2);
 
                                 //-----------------------------------------LOGIN SUCCESSIVO ALLA REGISTRAZIONE------------------------------------------
                                 arg = token[1].split(",");
                                 if (!error.Check(token[0], arg)) {
                                     // Invio dati al server
                                     writer.writeBytes(action + "\n");
                                     writer.flush();
                                     // Lettura dei dati provenienti dal server
                                     reply = reader.readLine();
                                     System.out.println("utente registato con successo");
                                     if (reply.equals("utente loggato")) {
 
                                         //-------------------------------------------------CITTA' D'INTERESSE---------------------------------------------------
                                         String replycities = reader.readLine();
                                         if (replycities.equals("")) {
                                             System.out.println("indicare la/le città di interesse");
                                             System.out.println("[IN CASO LA/LE CITTA' INSERITA/E NON SIA/SIANO PRESENTE/I NEL DATABASE, RIEFFETTUARE IL LOGIN]");
                                             action = input.nextLine();
                                             writer.writeBytes("favorite " + action + "\n");
                                             reply = reader.readLine();
                                             if (!reply.equals("ok")) {
                                                 System.out.println(reply);
                                                 break;
                                             }
                                             System.out.println("Preferenze salvate");
                                         }
 
                                         // Registrazione a RMI Callback
                                         username = arg[0];
                                         rmiCallback.callBackreg(username);
 
                                         // Avvio del thread UDP per il multicast
                                         login.setIsLogged(true);
                                         udp.start();
                                     }
                                 }
                             }
                         }
                     }
 
                     //-------------------------------------------------LOGIN----------------------------------------------------------------
                     case "login" -> {
                         if (login.getIsLogged()) {
                             System.out.println("Sei già loggato");
                             break;
                         }
                         if (token.length < 2) {
                             System.out.println("Errore nella trascrizione");
                             break;
                         }
                         arg = token[1].split(",");
                         if (!error.Check(token[0], arg)) {
                             // Invio dei dati al server
                             writer.writeBytes(action + "\n");
                             writer.flush();
                             // Lettura della risposta del server
                             reply = reader.readLine();
                             System.out.println(reply);
 
                             //-------------------------------------------------CITTA' D'INTERESSE---------------------------------------------------
                             if (reply.equals("utente loggato")) {
                                 String replycities = reader.readLine();
                                 if (replycities.equals("")) {
                                     System.out.println("indicare le città di interesse");
                                     System.out.println("ATTENZIONE: IN CASO LA CITTA' INSERITA NON SIA PRESENTE NEI NOSTRI ARCHIVI, SI DOVRA' RIPETERE LA PROCEDURA DI LOGIN");
                                     action = input.nextLine();
                                     writer.writeBytes("favorite " + action + "\n");
                                     reply = reader.readLine();
                                     if (!reply.equals("ok")) {
                                         System.out.println(reply);
                                         break;
                                     }
                                     System.out.println("Preferenze salvate");
                                 }
 
                                 // Registrazione a RMI Callback
                                 username = arg[0];
                                 rmiCallback.callBackreg(username);
 
                                 // Avvio del thread UDP per il multicast
                                 login.setIsLogged(true);
                                 udp.start();
                             }
                         }
                     }
 
                     //--------------------------------------------------LOGOUT--------------------------------------------------------------
                     case "logout" -> {
                         // Verifica se l'utente è loggato
                         if (!login.getIsLogged()) {
                             System.out.println("Non sei loggato non puoi effettuare il logout");
                             break;
                         }
                         if (token.length != 1) {
                             System.out.println("Errore nella trascrizione, il comando logout non richiede nessun campo");
                             break;
                         }
                         // Invio dei dati al server
                         writer.writeBytes(action + "\n");
                         writer.flush();
                         // Lettura della risposta del server
                         reply = reader.readLine();
                         System.out.println(reply);
                         if (reply.equals("logout effettuato")) {
                             // Termina il Thread Udp Multicast
                             login.setIsLogged(false);
                             udp.interrupt();
                             udp.join();
                             udp = new Udp(login, MULTICAST, MCASTPORT);
                             // Annulla la registrazione a RMI Callback
                             rmiCallback.callBackUnreg(username);
                             username = null;
                         }
                     }
 
                     //---------------------------------------------SEARCHOTEL---------------------------------------------------------------
                     case "searchHotel" -> {
                         if (token.length < 2) {
                             System.out.println("errore nella trascrizione");
                             break;
                         }
                         arg = token[1].split(",");
                         if (!error.Check(token[0], arg)) {
                             // Invio dei dati al server
                             writer.writeBytes(action + "\n");
                             writer.flush();
                             // Lettura della risposta del server
                             reply = reader.readLine();
                             try {
                                 // Deserializzazione della stringa Json in un'istanza della classe Hotel
                                 Gson gson = new Gson();
                                 Hotel hotel = gson.fromJson(reply, Hotel.class);
                                 hotel.printAll();
                             } catch (Exception e) {
                                 System.out.println(reply);
                             }
                         }
                     }
 
                     //--------------------------------------------SEARCHALLHOTELS-----------------------------------------------------------
                     case "searchAllHotels" -> {
                         if (token.length < 2) {
                             System.out.println("errore nella trascrizione");
                             break;
                         }
                         arg = token[1].split(",");
                         if (!error.Check(token[0], arg)) {
                             // Invio dei dati al server
                             writer.writeBytes(action + "\n");
                             writer.flush();
                             // Lettura della risposta del server
                             reply = reader.readLine();
                             try {
                                 // Deserializzazione della stringa Json in una lista di Hotel
                                 Gson gson = new Gson();
                                 List<Hotel> hotel = gson.fromJson(reply, new TypeToken<List<Hotel>>() {
                                 }.getType());
                                 for (Hotel value : hotel) {
                                     value.printAll();
                                     System.out.println("<----------------------------------------->");
                                 }
                             } catch (Exception e) {
                                 System.out.println(reply);
                             }
                         }
                     }
 
                     //--------------------------------------------INSERTREVIEW--------------------------------------------------------------
                     case "insertReview" -> {
                         if (!login.getIsLogged()) {
                             System.out.println("non puoi aggiungere review senza essere loggato");
                             break;
                         }
                         if (token.length < 2) {
                             System.out.println("errore nella trascrizione");
                             break;
                         }
                         arg = token[1].split(",");
                         if (!error.Check(token[0], arg)) {
                             // Invio dei dati al server
                             writer.writeBytes(action + "\n");
                             writer.flush();
                             // Lettura della risposta del server
                             reply = reader.readLine();
                             System.out.println(reply);
                         }
                     }
 
                     //---------------------------------------------SHOWMYBADGES-------------------------------------------------------------
                     case "showMyBadges" -> {
                         if (token.length > 1) {
                             System.out.println("Errore nella trascrizione, il comando showMybadges non richiede nessun campo");
                             break;
                         }
                         if (!login.getIsLogged()) {
                             System.out.println("Comando valido solo per utenti loggati");
                             break;
                         }
                         // Invio dei dati al server
                         writer.writeBytes(action + "\n");
                         writer.flush();
                         // Lettura della risposta del server
                         reply = reader.readLine();
                         System.out.println(reply);
                     }
 
                     //--------------------------------------------------FAVORITE--------------------------------------------------------------
                     case "favorite" -> {
                         if (!login.getIsLogged()) {
                             System.out.println("Non puoi impostare le città di interesse senza aver effettuato il login");
                             break;
                         }
                         if (token.length < 2) {
                             System.out.println("Errore nella trascrizione del comando");
                             break;
                         }
                         // Invio del comando al server
                         writer.writeBytes(action + "\n");
                         writer.flush();
                         // Lettura della risposta dal server
                         reply = reader.readLine();
                         System.out.println(reply);
                         // Se il server conferma il successo, notifica l'utente
                         if (reply.equals("ok")) {
                             System.out.println("Città di interesse impostate con successo");
                         }
                     }
 
                     // Comando help utile per avere maggiori informazioni
                     case "help" -> {
                         System.out.println("COMANDI:");
                         System.out.println("register (username, password);\nlogin (username, password);\nlogout;\nsearchHotel (nome hotel, città);\nserchAllHotels (città);\ninsertReview (nome hotel, città, punteggio complessivo, pulizia, posizione, servizi, qualità);\nshowMyBadges;\nfavorite (nomi città)");
                         System.out.println("");
                         System.out.println("--> INSERIRE COMANDO E ARGOMENTO NELLA STESSA RIGA SEPARATI DA UNO SPAZIO");
                         System.out.println("    |--> GLI ARGOMENTI (SE PIU' DI UNO) DOVRANNO ESSERE SEPARATI DA UNA VIRGOLA");
                         System.out.println("--> E' OBBLIGATORIO RISPETTARE MAIUSCOLE E MINUSCOLE");
                     }
 
                     default -> System.out.println("Comando non trovato");
                 }
             }
         } catch (SocketException IOException) {
             System.out.println("ERRORE, DI CONNESSIONE");
             login.setIsLogged(false);
             try {
                 udp.join();
             } catch (InterruptedException ignored) {}
         }
     }
 
     //-----------------------------------------REGISTRAZIONE TRAMITE RMI------------------------------------------------
     public static String rmi(String action, int rmiPort) throws RemoteException, NotBoundException {
         Registration serverObject;
         Remote remoteObject;
         Registry r = LocateRegistry.getRegistry(rmiPort);
         remoteObject = r.lookup("register-rmi");
         serverObject = (Registration) remoteObject;
         action = (serverObject.reg(action));
         return action;
     }
 
     // ----------------------------FILE DI CONFIGURAZIONE------------------------------
     /**
      * Metodo che legge il file di configurazione del server.
      * 
      * @throws FileNotFoundException se il file non esiste
      * @throws IOException           se si verifica un errore durante la lettura
      */
     public static void readConfig() throws FileNotFoundException, IOException {
         InputStream input = ClientMain.class.getResourceAsStream(configFile);
         Properties prop = new Properties();
         prop.load(input);
         TCPPORT = Integer.parseInt(prop.getProperty("TCPPORT"));
         RMIPORT = Integer.parseInt(prop.getProperty("RMIPORT"));
         NOTIFYPORT = Integer.parseInt(prop.getProperty("NOTIFYPORT"));
         MCASTPORT = Integer.parseInt(prop.getProperty("MCASTPORT"));
         input.close();
     }
 }
 