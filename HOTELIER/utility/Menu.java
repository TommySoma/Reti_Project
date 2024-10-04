package utility;

/**
 * La classe Menu fornisce metodi per stampare messaggi sincronizzati
 * sulla console con formattazione di colore.
 */
public class Menu {

    // Codici ANSI per resettare il colore e impostare il testo in vari colori
    public static final String RESET    = "\u001B[0m";
    public static final String RED      = "\u001B[31m";
    public static final String GREEN    = "\u001B[32m";
    public static final String WHITE    = "\u001B[37m";

    /**
     * Stampa in maniera sincronizzata su stdOut.
     * @param msg messaggio da stampare.
     */
    public static synchronized void printToConsole(String msg){
        System.out.printf(msg);
        System.out.flush();
    }

    /**
     * Stampa il testo principale del menu con formattazione colorata.
     */
    public static void mainText() {
        // Definizione delle stringhe per le varie parti del testo "Hotelier"
        String hot[] = {
            "|     |  _____  _______", 
            "|     | |     |    |   ", 
            "|-----| |     |    |   ", 
            "|     | |     |    |   ", 
            "|     | |_____|    |   "
        };

        String el[] = {
            " ______         ", 
            "|       |       ", 
            "|______ |       ", 
            "|       |       ", 
            "|______ |______ "
        };

        String ier[] = {
            "   ______  _____  ", 
            "| |       |     | ", 
            "| |______ |_____| ", 
            "| |       |   \\  ", 
            "| |______ |    \\ "
        };

        // Costruisce il testo combinato da stampare con formattazione di colore
        StringBuilder combined = new StringBuilder();
        for (int i = 0; i < hot.length; i++) {
            combined.append(GREEN)
                    .append(hot[i])
                    .append(" ") // Spazio tra le due stringhe
                    .append(WHITE)
                    .append(el[i])
                    .append(RED)
                    .append(ier[i])
                    .append(" ") // Spazio tra le due stringhe
                    .append(RESET)
                    .append("\n");
        }
        
        // Stampa il testo combinato sulla console
        printToConsole(combined.toString());
    }

}
