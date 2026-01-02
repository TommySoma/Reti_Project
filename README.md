# Reti_Project
Progetto HOTELIER Reti 12 CFU - Luglio 2024

La realizzazione del progetto si è basata sul linguaggio di programmazione Java e sulla libreria Gson, impiegata per la gestione della serializzazione e deserializzazione dei dati relativi agli utenti e agli hotel del sistema Hotelier.
L'architettura del progetto si articola in due directory principali: Client e Server, che comunicano tra loro principalmente attraverso socket TCP.

Compilazione ed esecuzione:
A. Tramite terminale:
  • Posizionarsi all’interno della cartella Hotelier e digitare il seguente comando per la compilazione:
      - javac -cp "lib/*" -d . utility/*.java ClientMain.java ServerMain.java
  • Posizionarsi all’interno della cartella Hotelier e digitare i seguenti comandi (in due terminali diversi) per l’esecuzione:
    - java -cp ".:lib/*" ServerMain
    - java -cp ".:lib/*" ClientMain
B. Tramite .jar
  • Posizionarsi all’interno della cartella Hotelier e digitare i seguenti comandi (in due terminali diversi) per l’esecuzione:
    - java -jar server.jar
    - java -jar client.jar
