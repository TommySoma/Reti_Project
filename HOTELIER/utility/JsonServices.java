package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La classe JsonServices gestisce la serializzazione e deserializzazione delle informazioni sugli hotel e sugli account.
 * Include metodi per creare file JSON, scrivere periodicamente dati sugli hotel, aggiungere recensioni e gestire utenti.
 */
public class JsonServices {
    // Lista di tutti gli account
    private CopyOnWriteArrayList<Account> AllAccount;
    // Lista di tutti gli hotel
    private CopyOnWriteArrayList<HotelServer> AllHotels;
    // Mappa concorrente delle città e delle rispettive liste di hotel
    private ConcurrentHashMap<String, CopyOnWriteArrayList<HotelServer>> mapCity;
    // Lista di città
    private CopyOnWriteArrayList<String> city;
    // Oggetto per il calcolo del ranking
    private Ranking ranking;

    // Costruttore che inizializza AllAccount e AllHotels
    public JsonServices(CopyOnWriteArrayList<Account> AllAccount, CopyOnWriteArrayList<HotelServer> AllHotels) {
        this.AllAccount = AllAccount;
        this.AllHotels = AllHotels;
    }

    // Crea il file JSON per serializzare e deserializzare le informazioni degli account
    public void createNewFile() throws IOException {
        System.out.println("--> CREAZIONE DEL FILE ACCOUNT <--");
        File file = new File("account.json");
        if (file.createNewFile()) {
            System.out.println("[FILE CREATO CON SUCCESSO]: " + file.getName());
        } else {
            System.out.println("[FILE GIA' PRESENTE]");
        }
    }

    // Avvia il thread che periodicamente serializza le informazioni aggiornate degli hotel nel file JSON
    public void JsonHotelWriter(int time) {
        HotelJsonWriter HotelJsonWriter = new HotelJsonWriter(AllHotels, time);
        HotelJsonWriter.start();
    }

    // Serializza le informazioni degli account dal file "account.json"
    public void JsonAccount() throws IOException {
        File file = new File("account.json");
        if (file.length() != 0) {
            FileInputStream inputStream = new FileInputStream("account.json");
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream)); //(legge i dati JSON in un formato a basso livello (token per token) dal flusso di input(traduce i byte letti da inputStream in caratteri utilizzando la codifica predefinita(UTF-8)))
            reader.beginArray();
            while (reader.hasNext()) {
                Account account = new Gson().fromJson(reader, Account.class);
                account.setLoggedIn(false);
                AllAccount.add(account);
            }
            reader.endArray();
        }
    }

    // Serializza le informazioni degli hotel dal file "Hotels.json"
    public void JsonHotel(RmiServerImpl server, int time, String nameAddress, int port) throws IOException {
        FileInputStream inputStream = new FileInputStream("Hotels.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        reader.beginArray();
        while (reader.hasNext()) {
            HotelServer hotel = new Gson().fromJson(reader, HotelServer.class);
            AllHotels.add(hotel);
        }
        reader.endArray();
        
        city = new CopyOnWriteArrayList<>();
        addCity(city);
        mapCity = new ConcurrentHashMap<>();
        for (String s : city) {
            CopyOnWriteArrayList<HotelServer> list = new CopyOnWriteArrayList<>();
            for (HotelServer allHotel : AllHotels) {
                if (s.equals(allHotel.getCity())) {
                    allHotel.setLastVoteDate(LocalDateTime.now());
                    list.add(allHotel);
                }
            }
            mapCity.put(s, list);
        }

        ranking = new Ranking(mapCity, city, time, server, nameAddress, port);
        ranking.start();
    }

    //------------------------------------------------ADDUSER (REGISTER)------------------------------------------------------------------
    // Metodo sincronizzato per aggiungere un nuovo utente (registrazione)
    public synchronized String JsonAddUser(Account utente) throws IOException {
        String reply;
        if (AllAccount.isEmpty()) {
            FileWriter fileWriter = new FileWriter("account.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            AllAccount.add(utente);
            String Json = gson.toJson(AllAccount);
            fileWriter.write(Json);
            fileWriter.flush();
            fileWriter.close();
            reply = "utente registrato";
            System.out.println(reply);
            return reply;
        } else {
            for (Account account : AllAccount) {
                if (account.getUsername().equals(utente.getUsername())) {
                    reply = "utente già registrato";
                    System.out.println(reply);
                    return reply;
                }
            }
            FileWriter fileWriter = new FileWriter("account.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            AllAccount.add(utente);
            String Json = gson.toJson(AllAccount);
            fileWriter.write(Json);
            reply = "utente registrato";
            fileWriter.flush();
            fileWriter.close();
        }
        System.out.println(reply);
        return reply;
    }

    // Metodo sincronizzato per aggiornare le informazioni di un utente
    public synchronized boolean updateUser(Account account) {
        if (AllAccount.isEmpty())
            return false;
        for (int i = 0; i < AllAccount.size(); i++) {
            if (AllAccount.get(i).getUsername().equals(account.getUsername())) {
                try {
                    FileWriter fileWriter = new FileWriter("account.json");
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String Json = gson.toJson(AllAccount);
                    fileWriter.write(Json);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("scrittura fallita");
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    // Deserializza le recensioni dal file "Review.json"
    public List<Review> jsonReview(List<Review> reviews) throws FileNotFoundException {
        File file = new File("Review.json");
        if (file.length() != 0) {
            FileReader fileReader = new FileReader("Review.json");
            Type type = new TypeToken<List<Review>>() {}.getType();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();

            reviews = gson.fromJson(fileReader, type);
        } else {
            reviews = new ArrayList<>();
        }
        return reviews;
    }

    // Serializza la lista di recensioni nel file "Review.json"
    public void jsonAddReview(List<Review> reviews) throws IOException {
        FileWriter fileWriter = new FileWriter("Review.json");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(reviews);
        fileWriter.write(json);
        fileWriter.flush();
        fileWriter.close();
    }

    // Getter sincronizzato per la lista di tutti gli account
    public synchronized CopyOnWriteArrayList<Account> getAllAccount() {
        return AllAccount;
    }

    // Getter sincronizzato per un account specifico tramite username
    public synchronized Account getAccount(String username) {
        for (Account account : AllAccount) {
            if (account.getUsername().equals(username))
                return account;
        }
        return null;
    }

    // Getter sincronizzato per la mappa delle città e degli hotel
    public synchronized ConcurrentHashMap<String, CopyOnWriteArrayList<HotelServer>> getMapCity() {
        return mapCity;
    }

    // Funzione di JsonServices
    // Crea un ArrayList con le città presenti nel file "listOfCity.txt"
    void addCity(CopyOnWriteArrayList<String> capoluoghi) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Cities.txt"));
        String s;
        while ((s = reader.readLine()) != null)
            capoluoghi.add(s);
    }
}
