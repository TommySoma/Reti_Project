package utility;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La classe ClientHandler gestisce l'interazione con un client.
 * Estende Thread per poter gestire ogni client in un thread separato.
 */
public class ClientHandler extends Thread {
    // Attributi della classe
    private Socket client; // Socket per comunicare con il client
    private CopyOnWriteArrayList<Account> allAccount; // Lista thread-safe di tutti gli account
    private CopyOnWriteArrayList<HotelServer> allHotel; // Lista thread-safe di tutti gli hotel
    private JsonServices jsonServices; // Servizi JSON per manipolare i dati
    private RmiServerImpl server; // Implementazione del server RMI
    private HotelServices hotelServices; // Servizi per la gestione degli hotel
    private int iterator; // Identificatore del client
    private AccountServices accountServices; // Servizi per la gestione degli account

    // Costruttore della classe
    public ClientHandler(Socket client, CopyOnWriteArrayList<Account> allAccount, CopyOnWriteArrayList<HotelServer> allHotel, JsonServices jsonServices, RmiServerImpl server, int iterator) throws IOException {
        this.client = client;
        this.allAccount = allAccount;
        this.allHotel = allHotel;
        this.jsonServices = jsonServices;
        this.server = server;
        this.hotelServices = new HotelServices(jsonServices);
        this.iterator = iterator;
        this.accountServices = new AccountServices(jsonServices);
    }

    // Metodo run eseguito quando il thread viene avviato
    public void run() {
        Thread.currentThread().setName("Client " + iterator);
        System.out.println("Nuovo utente connesso " + Thread.currentThread().getName());
        String message;
        BufferedReader in;
        Account user = null;
        DataOutputStream out;

        // Associa uno stream di input e di output alla socket
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());

            // Legge il messaggio inviato dal client
            while ((message = in.readLine()) != null) {
                String[] args;
                String[] token = message.split(" ", 2);

                switch (token[0]) {
                    // -----------------------------------------LOGIN----------------------------------------------------
                    case "login" -> {
                        if (user == null) {
                            args = token[1].split(",");
                            List<String> cityOfInterest;
                            message = accountServices.login(args[0].trim(), args[1].trim());

                            // Se la fase di login è andata a buon fine, controllo città d'interesse
                            if (message.equals("utente loggato")) {
                                System.out.println("login andato buon fine per " + Thread.currentThread().getName());
                                user = jsonServices.getAccount(args[0].trim());
                                cityOfInterest = user.getCities();
                                String citiesOutput = "";

                                if (!cityOfInterest.isEmpty()) {
                                    citiesOutput = String.join(",", cityOfInterest);
                                }
                                out.writeBytes(message + "\n");
                                out.writeBytes(citiesOutput + "\n");
                                break;
                            }
                            out.writeBytes(message + "\n");
                            System.out.println("login fallito per " + Thread.currentThread().getName() + " utente non trovato");
                        } else {
                            out.writeBytes("non puoi effettuare il login se sei loggato \n");
                            System.out.println("login non effettuato per " + Thread.currentThread().getName() + " perche' già loggato");
                        }
                    }

                    // -----------------------------------------CITTA' DI INTERESSE---------------------------------------------------------
                    case "favorite" -> {
                        if (user == null) {
                            out.writeBytes("non puoi impostare le città perchè non sei loggato\n");
                            System.out.println("Scelta città di interesse non effettuata per " + Thread.currentThread().getName() + ", utente non loggato");
                            break;
                        }
                        // Setta le città d'interesse
                        String[] cities = Arrays.stream(token[1].split(","))
                                .map(String::trim)
                                .toArray(String[]::new);
                        if (cities.length == 0) {
                            out.writeBytes("Stringa vuota non valida, ripetere la procedura di login\n");
                            System.out.println("Scelta città di interesse fallita per " + Thread.currentThread().getName() + ", stringa vuota, utente sloggato");
                            String s = accountServices.logout(user);
                            user = null;
                            break;
                        }
                        for (String city : cities) {
                            boolean cityExist = hotelServices.containsCity(city);
                            // Se le città indicate non sono presenti nella lista devo ripetere la procedura di login
                            if (!cityExist) {
                                out.writeBytes("citta' non valida, ripetere la procedura di login\n");
                                System.out.println("Scelta città di interesse fallita per " + Thread.currentThread().getName() + ", utente sloggato");
                                String s = accountServices.logout(user);
                                user = null;
                                break;
                            }
                        }
                        if (user == null)
                            break;
                        // Città d'interesse settate correttamente
                        user.setCities(Arrays.asList(cities));
                        boolean isUpdateFine = jsonServices.updateUser(user);
                        if (isUpdateFine) {
                            System.out.println("Scelta città di interesse eseguita con successo per " + Thread.currentThread().getName());
                            out.writeBytes("ok\n");
                        } else {
                            out.writeBytes("errore nell'update del file json\n");
                            System.out.println("Scelta città di interesse fallita per " + Thread.currentThread().getName() + " errore nel file json");
                        }
                    }

                    // --------------------------------------------------LOGOUT------------------------------------------------------------------
                    case "logout" -> {
                        if (user == null) {
                            out.writeBytes("non sei loggato\n");
                            System.out.println("logout non effettuato per " + Thread.currentThread().getName() + ", utente non è loggato");
                        } else {
                            message = accountServices.logout(user);
                            out.writeBytes(message + "\n");
                            System.out.println("logout effettuato con successo per " + Thread.currentThread().getName());
                            user = null;
                        }
                    }

                    // -----------------------------------------SEARCHHOTEL-------------------------------------------
                    case "searchHotel" -> {
                        args = token[1].split(",");
                        // Verifica l'esistenza della città
                        boolean nameCity = hotelServices.containsCity(args[1].trim());
                        if (nameCity) {
                            // Verifica l'esistenza dell'hotel
                            HotelServer hotel = hotelServices.searchHotel(args[0].trim(), args[1].trim());
                            if (hotel == null) {
                                out.writeBytes("hotel non trovato\n");
                                System.out.println("searchHotel per " + Thread.currentThread().getName() + " fallita, hotel non trovato");
                            } else {
                                // Serializza l'istanza dell'hotel in una stringa JSON da inviare al client
                                String JsonHotel;
                                Gson gson = new Gson();
                                JsonHotel = gson.toJson(hotel);
                                out.writeBytes(JsonHotel + "\n");
                                System.out.println("searchHotel per " + Thread.currentThread().getName() + " andata a buon fine");
                            }
                        } else {
                            out.writeBytes("Citta' non trovata\n");
                            System.out.println("searchHotel fallita per " + Thread.currentThread().getName() + " citta' non trovata");
                        }
                    }

                    // --------------------------------------SEARCHALLHOTELS--------------------------------------------------------
                    case "searchAllHotels" -> {
                        args = token[1].split(",");
                        CopyOnWriteArrayList<HotelServer> hotels;
                        hotels = hotelServices.searchAllHotels(args[0].trim());
                        if (hotels == null) {
                            out.writeBytes("Lista di Hotel non trovata\n");
                            System.out.println("searchAllHotels per " + Thread.currentThread().getName() + " fallita");
                        } else {
                            // Serializza la lista di hotel in una stringa JSON da inviare al client
                            String JsonHotel;
                            Gson gson = new Gson();
                            JsonHotel = gson.toJson(hotels);
                            out.writeBytes(JsonHotel + "\n");
                            System.out.println("searchAllHotels per " + Thread.currentThread().getName() + " andata buon fine");
                        }
                    }

                    // ---------------------------------------INSERTREVIEW----------------------------------------------------
                    case "insertReview" -> {
                        if (user == null) {
                            out.writeBytes("Non è possibile inserire review se non si è loggati\n");
                            System.out.println("insertReview per " + Thread.currentThread().getName() + " fallita, utente non loggato");
                            break;
                        }
                        // Try-catch per controllare il parsing dei voti a interi
                        try {
                            args = token[1].split(",");
                            String nameHotel = args[0].trim();
                            String namecity = args[1].trim();
                            if (hotelServices.searchAllHotels(namecity) == null) {
                                out.writeBytes("Citta' non trovata\n");
                                System.out.println("insertReview per " + Thread.currentThread().getName() + " fallita, città non trovata");
                                break;
                            }
                            int globalScore = Integer.parseInt(args[2].trim());

                            int[] singleScore = new int[4];
                            for (int i = 0; i < 4; i++) {
                                args[i + 3] = args[i + 3].replace("(", "").replace(")", "");
                                singleScore[i] = Integer.parseInt(args[i + 3].trim());
                            }
                            boolean review = hotelServices.addReview(namecity, nameHotel, globalScore, singleScore, user);
                            if (!review) {
                                out.writeBytes("errore, la recensione non e' stata inserita \n");
                                System.out.println("insertReview per " + Thread.currentThread().getName() + " fallita");
                            } else {
                                out.writeBytes("recensione inserita con successo \n");
                                System.out.println("insertReview per " + Thread.currentThread().getName() + " eseguita con successo");
                            }

                        } catch (NumberFormatException e) {
                            out.writeBytes("Errore, i voti inseriti devono essere numeri\n");
                            System.out.println("Errore, i voti inseriti da" + Thread.currentThread().getName() + "non sono caratteri numerici\n");
                        }
                    }

                    // ---------------------------------------SHOWMYBADGES-------------------------------------------
                    case "showMyBadges" -> {
                        if (user != null) {
                            String badge = user.setBadge();
                            jsonServices.updateUser(user);
                            out.writeBytes(badge + "\n");
                            System.out.println("ShowMybadges per " + Thread.currentThread().getName() + " eseguita con successo");
                        } else {
                            out.writeBytes("Comando valido solo per utenti loggati\n");
                            System.out.println("ShowMybadges per " + Thread.currentThread().getName() + " fallita, utente non loggato");
                        }
                    }
                    // Comando non riconosciuto
                    default -> System.out.println("Commando non trovato");
                }
            }
        } catch (IOException SocketException) {
            System.out.println("ERRORE: " + Thread.currentThread().getName() + " disconnesso");
            if (user != null) {
                try {
                    String s = accountServices.logout(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
