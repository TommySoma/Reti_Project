package utility;

import java.util.Comparator;

/**
 * Classe per l'ordinamento degli oggetti di tipo HotelServer.
 * Questa classe implementa l'interfaccia Comparator per definire l'ordinamento
 * degli hotel in base a diversi criteri: voto globale, voto medio, numero di voti,
 * e data dell'ultimo voto.
 */
class HotelComparator implements Comparator<HotelServer> {

    /**
     * Confronta due oggetti HotelServer per determinare il loro ordine.
     * @param h1 Il primo oggetto HotelServer
     * @param h2 Il secondo oggetto HotelServer
     * @return un valore negativo se h1 è minore di h2, zero se sono uguali,
     *         un valore positivo se h1 è maggiore di h2
     */
    @Override
    public int compare(HotelServer h1, HotelServer h2) {
        // Confronto del voto globale dei due hotel, in ordine decrescente
        int result = Double.compare(h2.getRate(), h1.getRate());
        if (result != 0) return result; // Se i voti globali sono diversi, restituisce il risultato

        // Se i voti globali sono uguali, confronta i voti medi dei due hotel, in ordine decrescente
        result = Double.compare(h2.getAverageRating(), h1.getAverageRating());
        if (result != 0) return result; // Se i voti medi sono diversi, restituisce il risultato

        // Se i voti medi sono uguali, confronta il numero di voti dei due hotel, in ordine decrescente
        result = Integer.compare(h2.getnOfVote(), h1.getnOfVote());
        if (result != 0) return result; // Se il numero di voti è diverso, restituisce il risultato

        // Se il numero di voti è uguale, confronta la data dell'ultimo voto dei due hotel, in ordine decrescente
        return h2.getLastVoteDate().compareTo(h1.getLastVoteDate());
    }
}
