package utility;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * L'interfaccia Registration definisce i metodi che devono essere implementati per fornire un servizio di registrazione remoto tramite RMI.
 * Estende l'interfaccia Remote, il che significa che i metodi definiti in questa interfaccia possono essere invocati da un client remoto.
 */
public interface Registration extends Remote {
    /**
     * Metodo remoto per la registrazione.
     * Questo metodo riceve una stringa di azione contenente i dettagli necessari per la registrazione e restituisce una stringa di risposta.
     * 
     * @param action Stringa che rappresenta i dettagli per la registrazione.
     * @return Una stringa che rappresenta l'esito della registrazione.
     * @throws RemoteException Se si verifica un errore di comunicazione durante l'invocazione remota.
     */
    String reg(String action) throws RemoteException;
}
