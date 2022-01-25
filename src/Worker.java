import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Worker implements Runnable {

    private DatagramSocket serverSocket;
    private DatagramQueue requestQueue;
    private FileHandler fileHandler;

    public Worker(DatagramSocket serverSocket, DatagramQueue requestQueue, FileHandler fileHandler) {
        this.serverSocket = serverSocket;
        this.requestQueue = requestQueue;
        this.fileHandler = fileHandler;
    }

    @Override
    public void run() {
    }
    
    public DatagramPacket handleIncomingRequest(DatagramPacket requestPacket) {

        return null;
    }
}
