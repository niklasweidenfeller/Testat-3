import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Diese Klasse dient zur dialogbasierten Kommunikation mit dem Server.
 */
public class Client {

    DatagramSocket socket = null;
    BufferedReader userIn = null;
    int serverPort;

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Die run()-Methode wartet auf eine Nutzereingabe, welche dann als Request
     * an den Server gesendet wird. Im Anschluss wird auf eine Antwort des
     * Servers gewartet und diese Ausgegeben.
     */
    public void run() {
        try {
            socket = new DatagramSocket();
            userIn = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("======================");
                System.out.println("Please enter a Request");
                String input = userIn.readLine();
                if (input.equals(".")) break;

                DatagramPacket p = createRequestPacket(input);
                socket.send(p);
                System.out.println("Server response:\n" + waitForAnswer());
                System.out.println("======================");
            }
        } catch (IOException e) {
        } finally {
            try {
                if (socket != null) socket.close();
                if (userIn != null) userIn.close();
            } catch (IOException ignored) {}
        }
    }

    /**
     * Diese Methode generiert ein DatagramPacket, welches
     * den per Konsoleneingabe eigegebenen String als
     * Nutzdaten enthält.
     *
     * @param input Der im Datagram zu verpackende String.
     * @return      Das zum Versenden bereite DatagramPacket. 
     */
    private DatagramPacket createRequestPacket(String input) {
        byte[] bytes = input.getBytes();
        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket p = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);

        return p;
    }

    /**
     * Diese Methode erstellt ein leeres DatagramPacket und
     * wartet im Anschluss darauf, dass das Packet mit
     * empfangenen Nutzdaten befüllt wird.
     * 
     * @return   Die empfangenden Nutzdaten
     */
    private String waitForAnswer() {
        byte[] buffer = new byte[65535];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(receivedPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer = receivedPacket.getData();
        int length = receivedPacket.getLength();
        return new String(buffer, 0, length);
    }

    public static void main(String[] args) {
        new Client(5999).run();
    }
}
