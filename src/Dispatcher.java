import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Dispatcher {

    private static final String FILEPATH = "./Files";

    private int port;
    private DatagramSocket serverSocket = null;
    private Worker[] workers;
    private DatagramQueue requestQueue;
    private FileHandler fileHandler;

    public Dispatcher(int port, int workerCount) {
        this.port = port;
        workers = new Worker[workerCount];
        requestQueue = new DatagramQueue();
        fileHandler = new FileHandler(FILEPATH);
    }

    public void start() {
        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("Dispatcher running on port " + port);
            for (Worker worker : workers) {
                worker = new Worker(serverSocket, requestQueue, fileHandler);
                new Thread(worker).start();
            }

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

    private DatagramPacket createEmptyDatagramPacket() {
        byte[] bytes = new byte[65535];
        return new DatagramPacket(bytes, bytes.length);
    }

    public static void main(String[] args) {
        new Dispatcher(5999, 5).start();
    }
}
