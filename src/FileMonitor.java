/**
 * Diese Klasse implementiert das zweite Leser-Schreiber-
 * Problem mit Schreiberpriorität. Zur Lösung wird das
 * Konzept des Zugangsmonitors verwendet.
 * 
 * Für die kritischen Abschnitte "Lesen" und "Schreiben"
 * steht je ein Eintritts- und ein Austrittsprotokoll
 * zur Verfügung.  
 */
public class FileMonitor {
    private boolean activeWriter = false;
    private int readCount = 0;
    private int writeCount = 0;

    /**
     * Eintrittsprotkoll Lesen
     */
    public synchronized void startRead() {
        try {
            while (writeCount>0) wait();
            readCount++;
            notifyAll(); // Wartende Leser wecken.
        } catch (InterruptedException e) {}
    }

    /**
     * Austrittsprotokoll Lesen
     */
    public synchronized void endRead() {
        readCount--;
        notifyAll(); // Wartende Schreiber wecken.
    }

    /**
     * Eintrittsprotkoll Schreiben
     */
    public synchronized void startWrite() {
        try {
            writeCount++; // "Anmelden zum Schreiben"
            while (readCount>0 || activeWriter) wait();
            activeWriter = true;
        } catch (InterruptedException e) {}
    }

    /**
     * Austrittsprotokoll Schreiben
     */
    public synchronized void endWrite() {
        writeCount--;
        activeWriter = false;
        notifyAll(); // Wartende Leser/Schreiber wecken.
    }
}
