import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DebugClient {

    DatagramSocket socket = null;
    BufferedReader userIn = null;
    int serverPort;

    public DebugClient(int serverPort) {
        this.serverPort = serverPort;
    }

    public void run() {
        try {
            socket = new DatagramSocket();
            userIn = new BufferedReader(new InputStreamReader(System.in));
            
            while(true) {
                System.out.println("press key to run");
                userIn.readLine();

                sendReadFile();
                sendReadFile();
                sendReadFile();
                sendReadFile();
                sendReadFile();
                sendWriteFile();
                sendWriteFile();
                sendWriteFile();
                sendWriteFile();
                sendWriteFile();
            }
        } catch (IOException e) {
        } finally {
            try {
                if (socket != null) socket.close();
                if (userIn != null) userIn.close();
            } catch (IOException ignored) {}
        }
    }

    private void sendReadFile() throws IOException {
        DatagramPacket p = createRequestPacket("READ file1.txt,1");
        socket.send(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {}
    }
    private void sendWriteFile() throws IOException {
        DatagramPacket p = createRequestPacket("WRITE file1.txt,1,Test");
        socket.send(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {}
    }

    private DatagramPacket createRequestPacket(String request) {
        byte[] bytes = request.getBytes();
        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket p = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);

        return p;
    }

    public static void main(String[] args) {
        new DebugClient(5999).run();
    }
}
