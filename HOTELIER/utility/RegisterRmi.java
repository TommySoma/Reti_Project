package utility;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;

/**
 * La classe RegisterRmi gestisce la registrazione degli utenti tramite RMI (Remote Method Invocation).
 * Estende RemoteServer e implementa l'interfaccia Registration.
 */
public class RegisterRmi extends RemoteServer implements Registration {
    // Istanza di JsonServices per gestire le operazioni JSON
    JsonServices Json;

    /**
     * Costruttore della classe RegisterRmi.
     * 
     * @param Json Istanza di JsonServices.
     */
    public RegisterRmi(JsonServices Json) {
        this.Json = Json;
    }

    /**
     * Metodo che gestisce la registrazione tramite RMI.
     * 
     * @param action Stringa contenente le informazioni per la registrazione.
     * @return Messaggio di esito della registrazione.
     * @throws RemoteException Se si verifica un errore di comunicazione RMI.
     */
    @Override
    public String reg(String action) throws RemoteException {
        // Divide l'input action in due parti usando lo spazio come delimitatore
        String[] token = action.split(" ", 2);
        // Divide la seconda parte dell'input (argomenti) usando la virgola come delimitatore
        String[] arg = token[1].split(",");
        // Crea un nuovo account con le informazioni fornite
        Account utente = new Account(arg[0].trim(), arg[1].trim(), "", 0, false);
        try {
            // Tenta di aggiungere il nuovo utente utilizzando JsonServices
            action = Json.JsonAddUser(utente);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return action; // Restituisce l'esito della registrazione
    }
}
