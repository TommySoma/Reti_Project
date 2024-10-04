package utility;

/**
 * La classe Login rappresenta lo stato di accesso di un utente.
 * Contiene un campo booleano che indica se l'utente è loggato o meno.
 */
public class Login {

    // Campo privato che tiene traccia dello stato di accesso dell'utente
    private boolean isLogged;

    /**
     * Costruttore della classe Login.
     * @param isLogged lo stato iniziale di accesso.
     */
    public Login(boolean isLogged) {
        this.isLogged = isLogged;
    }

    /**
     * Metodo getter per ottenere lo stato di accesso.
     * @return lo stato di accesso attuale.
     */
    public boolean getIsLogged() {
        return isLogged;
    }

    /**
     * Metodo setter per impostare lo stato di accesso.
     * @param a il nuovo stato di accesso da impostare.
     */
    public void setIsLogged(boolean a) {
        isLogged = a;
    }

}
