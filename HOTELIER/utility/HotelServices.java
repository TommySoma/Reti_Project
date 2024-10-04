package utility;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La classe HotelServices gestisce le operazioni sugli hotel, come la ricerca di hotel,
 * la ricerca di tutti gli hotel in una città, e l'inserimento di recensioni. Utilizza il
 * servizio JsonServices per caricare e salvare i dati degli hotel e delle recensioni.
 */
public class HotelServices {
    // Servizio per la gestione delle operazioni JSON
    private JsonServices jsonServices;
    // Mappa concorrente delle città e delle rispettive liste di hotel
    private ConcurrentHashMap<String, CopyOnWriteArrayList<HotelServer>> mapCity;
    // Lista delle recensioni degli account
    private List<Review> accountReview;

    // Costruttore che inizializza jsonServices, mapCity e accountReview
    public HotelServices(JsonServices jsonServices) throws IOException {
        this.jsonServices = jsonServices;
        this.mapCity = jsonServices.getMapCity();
        accountReview = jsonServices.jsonReview(accountReview);
    }

    // Metodo sincronizzato per verificare se una città esiste nella mappa
    public synchronized boolean containsCity(String name) {
        return mapCity.containsKey(name);
    }

    //-----------------------------------------------SEARCHOTEL-----------------------------------------------------------------------
    // Metodo sincronizzato per cercare un hotel specifico in una città
    public synchronized HotelServer searchHotel(String nameHotel, String nameCity) {
        CopyOnWriteArrayList<HotelServer> hotels;
        hotels = mapCity.get(nameCity);
        for (HotelServer hotel : hotels) {
            // Si suppone non esistano due hotel con lo stesso nome nella stessa città
            if (nameHotel.equals(hotel.getName())) {
                return hotel;
            }
        }
        return null;
    }

    //--------------------------------------------------SEARCHALLHOTELS--------------------------------------------------------------------
    // Metodo sincronizzato per cercare tutti gli hotel in una città
    public synchronized CopyOnWriteArrayList<HotelServer> searchAllHotels(String nameCity) {
        if (mapCity.containsKey(nameCity))
            return mapCity.get(nameCity);
        else
            return null;
    }

    //-------------------------------------------------------INSERTREVIEW----------------------------------------------------------------
    // Metodo sincronizzato per aggiungere una recensione a un hotel
    public synchronized boolean addReview(String namecity, String nameHotel, int globalScore, int[] singleScore, Account currentUser) throws IOException {
        LocalDateTime date;
        int size = mapCity.get(namecity).size();
        for (int i = 0; i < size; i++) {
            HotelServer hotel = mapCity.get(namecity).get(i);

            String currentNameHotel = hotel.getName();

            // Voto totale dato dalla somma dei voti
            double actualScore = hotel.getTotalScore();

            // Ricerca dell'hotel
            if (currentNameHotel.equals(nameHotel)) {

                // Aggiorno la data dell'ultimo voto
                date = LocalDateTime.now();
                hotel.setLastVoteDate(date);

                // Aggiorno il numero di recensioni
                currentUser.incrementReview();
                jsonServices.updateUser(currentUser);

                // Aggiorno il voto globale
                hotel.incrementVote();
                int n = hotel.getnOfVote();
                double sum = (actualScore + globalScore);
                double mean = sum / n;
                hotel.setTotalScore(sum);
                hotel.setRate(mean);

                // AGGIORNO IL VOTO DELLE SINGOLE CATEGORIE
                // Creo una copia della hashmap contenente i voti
                ConcurrentHashMap<String, Double> allSingleScore = hotel.getRatings();

                // Creo una copia dell'array contenente la somma dei singoli voti
                double[] totalSingleScore = hotel.getTotalSingleScore();
                for (int j = 0; j < totalSingleScore.length; j++) {
                    sum = totalSingleScore[j] + singleScore[j];
                    mean = sum / n;
                    totalSingleScore[j] = sum;
                    String category = changeIntCategory(j + 1);
                    allSingleScore.put(category, mean);
                }

                // Aggiungo la recensione alla lista
                Review rev = new Review(currentUser.getUsername(), namecity, nameHotel, globalScore, singleScore, date);
                accountReview.add(rev);

                // Serializzo le informazioni su Review.json
                jsonServices.jsonAddReview(accountReview);
                return true;
            }
        }
        return false;
    }

    // Getter per la lista delle recensioni degli account
    public List<Review> getAccountReview() {
        return accountReview;
    }

    // Setter per la lista delle recensioni degli account
    public void setAccountReview(List<Review> accountReview) {
        this.accountReview = accountReview;
    }

    // Funzione per trasformare l'indice della HashMap nella categoria corrispondente
    String changeIntCategory(int n) {
        String category = null;
        switch (n) {
            case 1:
                category = "cleaning";
                break;
            case 2:
                category = "position";
                break;
            case 3:
                category = "services";
                break;
            case 4:
                category = "quality";
                break;
        }
        return category;
    }
}
