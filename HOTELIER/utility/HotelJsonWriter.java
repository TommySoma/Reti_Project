package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La classe HotelJsonWriter è un thread che periodicamente (ogni "time" millisecondi)
 * serializza i dati degli hotel in un file JSON.
 */
public class HotelJsonWriter extends Thread {
    // Lista concorrente di hotel da serializzare
    private CopyOnWriteArrayList<HotelServer> hotels;
    // Intervallo di tempo (in millisecondi) tra una serializzazione e l'altra
    private int time;

    /**
     * Costruttore della classe HotelJsonWriter.
     * @param hotels La lista degli hotel da serializzare
     * @param time L'intervallo di tempo tra una serializzazione e l'altra
     */
    public HotelJsonWriter(CopyOnWriteArrayList<HotelServer> hotels, int time) {
        this.hotels = hotels;
        this.time = time;
    }

    /**
     * Metodo eseguito quando il thread viene avviato.
     * Esegue un ciclo infinito che serializza la lista degli hotel in un file JSON
     * ogni "time" millisecondi, se la lista non è vuota.
     */
    @Override
    public void run() {
        while(true) {
            // Controlla se la lista degli hotel non è vuota
            if (!hotels.isEmpty()) {
                try {
                    // Crea un FileWriter per scrivere il file JSON
                    FileWriter fileWriter = new FileWriter("Hotels.json");
                    // Configura Gson per creare una rappresentazione JSON formattata
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    // Converte la lista degli hotel in una stringa JSON
                    String Json = gson.toJson(hotels);
                    // Scrive la stringa JSON nel file
                    fileWriter.write(Json);
                    // Forza la scrittura dei dati sul file
                    fileWriter.flush();
                    // Chiude il FileWriter
                    fileWriter.close();
                } catch (IOException e) {
                    // Stampa lo stack trace dell'eccezione in caso di errore di I/O
                    e.printStackTrace();
                }
            }
            try {
                // Fa dormire il thread per il tempo specificato
                Thread.sleep(time);
            } catch (InterruptedException e) {
                // Stampa lo stack trace dell'eccezione in caso di interruzione del thread
                e.printStackTrace();
            }
        }
    }
}
