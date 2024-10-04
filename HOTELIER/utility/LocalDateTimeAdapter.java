package utility;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * La classe LocalDateTimeAdapter implementa JsonSerializer e JsonDeserializer per la gestione della serializzazione e deserializzazione di oggetti LocalDateTime.
 * Utilizza il formato "yyyy-MM-dd'T'HH:mm:ss" per la conversione.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // Definisce un formatter per il formato data e ora utilizzato per la serializzazione/deserializzazione
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Metodo per serializzare un oggetto LocalDateTime in JsonElement
     *
     * @param src l'oggetto LocalDateTime da serializzare
     * @param typeOfSrc il tipo dell'oggetto sorgente
     * @param context il contesto della serializzazione
     * @return un JsonElement rappresentante il LocalDateTime serializzato
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        // Converte l'oggetto LocalDateTime in una stringa formattata e lo avvolge in un JsonPrimitive
        return new JsonPrimitive(formatter.format(src));
    }

    /**
     * Metodo per deserializzare un JsonElement in un oggetto LocalDateTime
     *
     * @param json l'elemento JSON da deserializzare
     * @param typeOfT il tipo dell'oggetto di destinazione
     * @param context il contesto della deserializzazione
     * @return un oggetto LocalDateTime deserializzato
     * @throws JsonParseException se la stringa JSON non può essere analizzata come LocalDateTime
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Converte la stringa formattata in un oggetto LocalDateTime utilizzando il formatter
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}
