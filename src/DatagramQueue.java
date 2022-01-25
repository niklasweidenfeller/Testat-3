import java.net.DatagramPacket;
import java.util.LinkedList;

/**
 * Diese Klasse enthält die Implementierung einer FIFO-
 * Warteschlange für am Server eingehende Aufträge.
 * 
 * Anhängend und Entfernen aus der Liste geschieht unter
 * gegenseitigem Ausschluss.
 * 
 * Die Methode add() der Klasse LinkedList fügt ein
 * neues Element am Ende der Liste an. pop() gibt das
 * erste Element der Liste zurück. So wird FIFO
 * gewährleistet. 
 */
public class DatagramQueue {
    private LinkedList<DatagramPacket> queue = new LinkedList<>();

    public synchronized void append(DatagramPacket packet) {
        System.out.println("Adding Element to Queue");
        queue.add(packet);
        // Den ersten wartenden Thread aufwecken. 
        notify();
    }

    public synchronized DatagramPacket remove() {
        try {
            // Warten, wenn die Warteschlange leer ist.
            while (queue.size() == 0) wait();
        } catch (InterruptedException e) {}

        System.out.println("Removing Element from Queue");
        return queue.pop();
    }
}
