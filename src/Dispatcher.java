import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Dispatcher {

    private static final String FILEPATH = "./Files";

    private int port;
    private DatagramSocket serverSocket = null;
    private DatagramQueue requestQueue;

    public Dispatcher(int port, int workerCount) {
        this.port = port;
        requestQueue = new DatagramQueue();
    }

    public void start() {
        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("Dispatcher running on port " + port);

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
        new Dispatcher(5999, 1).start();
    }
}
