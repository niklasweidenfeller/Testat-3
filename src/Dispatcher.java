import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Diese Klasse dient als Einstiegspunkt in den Server.
 * Dabei werden Aufträge entgegengenommen und in eine FIFO-
 * Warteschlange eingereiht. Von dort übernehmen die
 * Worker-Threads die Bearbeitung der Aufträge.
 */
public class Dispatcher {

    private static final String FILEPATH = System.getProperty("user.home") + "/Desktop/Files/";

    private int port;
    private DatagramSocket serverSocket = null;
    private Worker[] workers;
    private DatagramQueue requestQueue;
    /**
     * Die FileHandler-Klasse stellt Methoden zum Bearbeiten
     * und Zugreifen auf Dateien des Filesystems bereit.
     */
    private FileHandler fileHandler;

    /**
     * Konstruktor
     * @param port  Der Port, auf dem der FileServer laufen soll.
     * @param workerCount   Die Anzahl der Worker-Threads.
     */
    public Dispatcher(int port, int workerCount) {
        this.port = port;
        workers = new Worker[workerCount];
        requestQueue = new DatagramQueue();
        fileHandler = new FileHandler(FILEPATH);
    }

    /**
     * Die Hauptroutine des Dispatchers wartet auf neue Aufträge
     * und reiht diese in eine Warteschlange ein.
     */
    public void start() {
        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("Dispatcher: running on port " + port);
            // Zählvariable dient nur zur Vereinfachung der Konsolenausgabe.
            int i = 1;
            for (Worker worker : workers) {
                worker = new Worker(serverSocket, requestQueue, fileHandler, i);
                i++;
                new Thread(worker).start();
            }

            // Entgegennehmen und Einreihen der Aufträge
            while (true) {
                try {
                    DatagramPacket incomingPacket = createEmptyDatagramPacket();
                    serverSocket.receive(incomingPacket);
                    requestQueue.append(incomingPacket);
                } catch (IOException ignored) {}
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) serverSocket.close();
        }
    }

    /**
     * Diese Methode erstellt ein leeres DatagramPacket,
     * welches zum Entgegennehmen von Anfragen verwendet wird.
     * 
     * @return Das leere DatagramPacket.
     */
    private DatagramPacket createEmptyDatagramPacket() {
        byte[] bytes = new byte[65535];
        return new DatagramPacket(bytes, bytes.length);
    }

    /**
     * Die main-Methode zur Ausführung des Servers.
     */
    public static void main(String[] args) {
        new Dispatcher(5999, 5).start();
    }
}
