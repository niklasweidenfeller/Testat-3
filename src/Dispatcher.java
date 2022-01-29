import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.*;

/**
 * Diese Klasse dient als Einstiegspunkt in den Server.
 * Dabei werden Aufträge entgegengenommen und in eine FIFO-
 * Warteschlange eingereiht. Von dort übernehmen die
 * Worker-Threads die Bearbeitung der Aufträge.
 * 
 * Es wird ein "Files" Verzeichnis auf dem Desktop erwartet.
 */
public class Dispatcher {

    private static final String FILEPATH = System.getProperty("user.home") + "/Desktop/Files/";

    private int port;
    private DatagramSocket serverSocket = null;
    private Worker[] workers;
    private boolean[] workerFree;
    private Semaphore[] workerSem;
    private Semaphore freeWorkers;
    private Semaphore mutex = new Semaphore(1, true);
    private DatagramPacket[] tasks;

    /* FileHandler-Klasse zum Bearbeiten und Zugreifen
     * auf Dateien des Filesystems. */
    private FileHandler fileHandler;

    /**
     * Konstruktor
     * 
     * @param port Der Port, auf dem der FileServer laufen soll.
     * @param workerCount Die Anzahl der Worker-Threads.
     */
    public Dispatcher(int port, int workerCount) {
        this.port = port;
        workers = new Worker[workerCount];
        fileHandler = new FileHandler(FILEPATH);
        workerFree = new boolean[workerCount];
        workerSem = new Semaphore[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workerFree[i] = true;
            workerSem[i] = new Semaphore(0, true);
        }
        freeWorkers = new Semaphore(5, true);
        tasks = new DatagramPacket[workerCount];
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
            int i = 0;
            for (Worker worker : workers) {
                worker = new Worker(serverSocket, fileHandler, i, this);
                i++;
                new Thread(worker).start();
            }

            // Entgegennehmen und Einreihen der Aufträge
            while (true) {
                try {
                    DatagramPacket incomingPacket = createEmptyDatagramPacket();
                    serverSocket.receive(incomingPacket);
                    assignToWorker(incomingPacket);
                } catch (IOException ignored) {}
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) serverSocket.close();
        }
    }

    private void assignToWorker(DatagramPacket dp) {
        try {
            freeWorkers.acquire();

            mutex.acquire();
            int worker = -1;
            for (int i = 0; i < workerFree.length; i++) {
                if (workerFree[i]) {
                    worker = i;
                    break;
                }
            }

            workerFree[worker] = false;
            mutex.release();
            tasks[worker] = dp;
            workerSem[worker].release();

        } catch (Exception e) {}
    }

    public void setWorkerFree(int id) {
        try {
            workerSem[id].acquire();
            workerFree[id] = true;

            freeWorkers.release();
        } catch (Exception e) {}
    }

    public Semaphore getWorkerSem(int i) {
        return workerSem[i];
    }

    public DatagramPacket getTask(int i) {
        return tasks[i];
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
