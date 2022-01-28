import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Diese Klasse dient zur Ausführung einiger vordefinierter
 * Testfälle. Die Antworten des Servers werden hierbei nicht beachtet.
 */
public class DebugClient {

    DatagramSocket socket = null;
    BufferedReader userIn = null;
    int serverPort;

    public DebugClient(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Die run()-Methode führt je nach Nutzereingabe
     * einen von drei vordefinierten Testfällen aus.
     */
    public void run() {
        try {
            socket = new DatagramSocket();
            userIn = new BufferedReader(new InputStreamReader(System.in));
            
            while(true) {
                System.out.println("Select Testcase (choose from 1 - 3)");
                String input = userIn.readLine();

                if (input.equals("1")) testcase1();
                if (input.equals("2")) testcase2();
                if (input.equals("3")) testcase3();
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
     * Dieser Testfall demonstriert die Schreiber-
     * priorität bei aufeinanderfolgenden Anfragen
     * nach der selben Datei. Außerdem ist erkennbar,
     * dass Schreibzugriffe sequenziell ausgeführt werden,
     * während Lesezugriffe parallel erlaubt sind.
     */
    private void testcase1() throws IOException {
        sendWriteFile1();
        sendReadFile1();
        sendReadFile1();
        sendReadFile1();
        sendWriteFile1();
        sendWriteFile1();
    }

    /**
     * Dieser Testfall demonstriert das parallele
     * Schreiben auf verschiedene Dateien.
     */
    private void testcase2() throws IOException {
        sendWriteFile1();
        sendReadFile1();
        sendReadFile1();
        sendReadFile1();
        sendWriteFile2();
        sendWriteFile2();
        sendReadFile2();        
    }
    
    /**
     * Dieser Testfall demonstriert das Einreihen von
     * Aufträgen in die Warteschlange, falls mehr
     * Anfragen eingehen, als Worker vorhanden sind.
     */
    private void testcase3() throws IOException {
        for (int i = 0; i < 10; i++)
            sendReadFile1();
    }

    // Lesen aus Datei 1
    private void sendReadFile1() throws IOException {
        DatagramPacket p = createRequestPacket("READ file1.txt,1");
        socket.send(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {}
    }

    // Lesen aus Datei 2
    private void sendReadFile2() throws IOException {
        DatagramPacket p = createRequestPacket("READ file2.txt,1");
        socket.send(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {}
    }

    // In Datei 1 schreiben
    private void sendWriteFile1() throws IOException {
        DatagramPacket p = createRequestPacket("WRITE file1.txt,1,Test");
        socket.send(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {}
    }

    // In Datei 2 schreiben
    private void sendWriteFile2() throws IOException {
        DatagramPacket p = createRequestPacket("WRITE file2.txt,1,Test");
        socket.send(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {}
    }

    /**
     * Diese Methode generiert ein DatagramPacket, welches
     * den per Konsoleneingabe eigegebenen String als
     * Nutzdaten enthält.
     *
     * @param input Der im Datagram zu verpackende String.
     * @return      Das zum Versenden bereite DatagramPacket. 
     */
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
