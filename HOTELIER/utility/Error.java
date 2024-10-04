package utility;

/**
 * Classe per il controllo degli errori nei comandi dell'applicazione.
 * Questa classe fornisce metodi per verificare la correttezza dei comandi 
 * inseriti dall'utente, controllando la presenza di errori di trascrizione 
 * e la validità dei dati forniti.
 */
public class Error {

    /**
     * Controlla se ci sono errori di trascrizione nei comandi forniti.
     * @param command Il comando da verificare
     * @param token Gli argomenti del comando
     * @return true se c'è un errore, false altrimenti
     */
    public Boolean Check(String command, String[] token) {
        switch (command) {
            //----------------------------------------------REGISTER-------------------------------------------------------------------------
            case "register":
                // Register (username, password)
                if (token.length != 2) {
                    // Messaggio di errore se ci sono più o meno di due argomenti
                    System.out.println("Errore nella trascrizione, il comando register richiede solo due campi, username e password");
                    return true;
                } else {
                    // Controlla che nessun argomento sia nullo o vuoto
                    for (String s : token) {
                        if (s == null || s.trim().equals("")) {
                            System.out.println("Non è possibile avere un campo vuoto");
                            return true;
                        }
                    }
                    // Comando valido
                    return false;
                }

            //-----------------------------------------------LOGIN--------------------------------------------------
            case "login":
                // Login (username, password)
                if (token.length != 2) {
                    // Messaggio di errore se ci sono più o meno di due argomenti
                    System.out.println("Errore nella trascrizione, il comando login richiede solo due campi, username e password");
                    return true;
                } else {
                    // Controlla che nessun argomento sia nullo o vuoto
                    for (int i = 1; i < token.length; i++) {
                        if (token[i] == null || token[i].trim().equals("")) {
                            System.out.println("Non è possibile avere un campo vuoto");
                            return true;
                        }
                    }
                    // Comando valido
                    return false;
                }

            //--------------------------------------------SEARCHHOTEL--------------------------------------------------
            case "searchHotel":
                // SearchHotel (nomeHotel, città)
                if (token.length != 2) {
                    // Messaggio di errore se ci sono più o meno di due argomenti
                    System.out.println("Errore nella trascrizione, il comando searchHotel richiede solo due campi, nomeHotel e città");
                    return true;
                }
                // Controlla che nessun argomento sia nullo o vuoto
                for (String s : token) {
                    if (s == null || s.trim().equals("")) {
                        System.out.println("Non è possibile avere un campo vuoto");
                        return true;
                    }
                }
                // Comando valido
                return false;

            //-------------------------------------------SEARCHALLHOTELS-------------------------------------------------
            case "searchAllHotels":
                // SearchAllHotels (città)
                if (token.length != 1) {
                    // Messaggio di errore se ci sono più o meno di un argomento
                    System.out.println("Errore nella trascrizione, il comando searchAllHotels richiede solo un campo, città");
                    return true;
                }
                // Controlla che l'argomento non sia nullo o vuoto
                if (token[0] == null || token[0].trim().equals("")) {
                    System.out.println("Non è possibile avere un campo vuoto");
                    return true;
                }
                // Comando valido
                return false;

            //--------------------------------------------INSERTREVIEW--------------------------------------------------
            case "insertReview":
                // InsertReview (nome Hotel, città, punteggio globale, pulizia, posizione, servizi, qualità)
                try {
                    if (token.length != 7) {
                        // Messaggio di errore se ci sono più o meno di sette argomenti
                        System.out.println("Errore nella trascrizione, il comando insertReview richiede nome hotel, nome città, punteggi globali e punteggi delle singole categorie");
                        return true;
                    }
                    // Controlla che nessun argomento sia nullo o vuoto e che i punteggi siano validi
                    for (int i = 0; i < token.length; i++) {
                        if (token[i] == null) {
                            return true;
                        }
                        String current = token[i].trim();
                        if (current.equals("")) {
                            System.out.println("Non è possibile avere un campo vuoto");
                            return true;
                        } else if (i >= 2) { // Controlla solo i campi di punteggio
                            current = current.replace("(", "").replace(")", "");
                            int a = Integer.parseInt(current);
                            // Verifica che il punteggio sia compreso tra 0 e 5
                            if (a < 0 || a > 5) {
                                System.out.println("Il punteggio deve essere compreso tra 0 e 5 estremi inclusi");
                                return true;
                            }
                        }
                    }
                    // Comando valido
                    return false;
                } catch (NumberFormatException e) {
                    // Messaggio di errore se il punteggio non è un numero
                    System.out.println("Errore, il punteggio inserito deve essere un numero");
                    return true;
                }

            // Comando non riconosciuto
            default:
                System.out.println("Errore, ripetere l'operazione");
                return false;
        }
    }
}
