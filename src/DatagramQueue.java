import java.net.DatagramPacket;
import java.util.LinkedList;

/**
 * Diese Klasse enthält die Implementierung einer FIFO-
 * Warteschlange für am Server eingehende Aufträge.
 * Anhängen an die Liste und Entfernen aus der Liste
 * geschehen unter gegenseitigem Ausschluss.
 * 
 * Die Methode add() der Klasse LinkedList fügt ein
 * neues Element am Ende der Liste an. pop() gibt das
 * erste Element der Liste zurück. So wird FIFO
 * gewährleistet. 
 */
public class DatagramQueue {
    /**
     * Die Warteschlange, in welche eingehende Requests eingereiht
     * werden.
     */
    private LinkedList<DatagramPacket> queue = new LinkedList<>();

    /**
     * Diese Methode fügt ein DatagramPacket am Ende der
     * Warteschlange an. Dies geschieht unter gegenseitigem
     * Ausschluss.
     * 
     * @param packet Das einzufügende DatagramPacket.
     */
    public synchronized void append(DatagramPacket packet) {
        queue.add(packet);
        System.out.println("Dispatcher: added Element to Queue. Size: " + queue.size());
        // Den ersten wartenden Thread aufwecken.
        notify();
    }

    /**
     * Diese Methode entnimmt das erste DatagramPacket aus 
     * der Warteschlange. Dies geschieht unter gegenseitigem
     * Ausschluss.
     * 
      * @param workerNameAndIndent Nur zu Debugginzwecken. 
      * @return Das aus der Warteschlange entnommene DatagramPacket.
      */
    public synchronized DatagramPacket remove(String workerNameAndIndent) {
        try {
            // Warten, wenn die Warteschlange leer ist.
            while (queue.size() == 0) wait();
        } catch (InterruptedException e) {}

        System.out.println(workerNameAndIndent+": Removing Element from Queue. New size: " + (queue.size()-1));
        return queue.pop();
    }
}
