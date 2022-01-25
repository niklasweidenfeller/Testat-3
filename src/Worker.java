import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Worker implements Runnable {

    private DatagramSocket serverSocket;
    private DatagramQueue requestQueue;

    public Worker(DatagramSocket serverSocket, DatagramQueue requestQueue) {
        this.serverSocket = serverSocket;
        this.requestQueue = requestQueue;
    }

    @Override
    public void run() {
    }
    
    public DatagramPacket handleIncomingRequest(DatagramPacket requestPacket) {

        return null;
    }
}
