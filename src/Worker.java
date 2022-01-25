import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Worker implements Runnable {
    private enum Command {
        READ,
        WRITE
    }

    private DatagramSocket serverSocket;
    private DatagramQueue requestQueue;
    private FileHandler fileHandler;
    private String workerNameAndIndent = "";

    public Worker(DatagramSocket serverSocket, DatagramQueue requestQueue, FileHandler fileHandler, int id) {
        this.serverSocket = serverSocket;
        this.requestQueue = requestQueue;
        this.fileHandler = fileHandler;

        // nur zur Veranschaulichung der Konsolenausgabe
        for (int i = 0; i < id; i++) {
            workerNameAndIndent += "\t\t";
        }
        workerNameAndIndent += ("Worker " + id);
    }

    @Override
    public void run() {
        while(true) {
            System.out.println(workerNameAndIndent + ": awaiting request");
            DatagramPacket requestPacket = requestQueue.remove(workerNameAndIndent);
            System.out.println(workerNameAndIndent + ": handling request");
            DatagramPacket response = handleIncomingRequest(requestPacket);
            try {
                serverSocket.send(response);
            } catch (IOException e) {}
            System.out.println(workerNameAndIndent + ": finished request");
        }
    }
    
    private DatagramPacket handleIncomingRequest(DatagramPacket requestPacket) {

        byte[] data = requestPacket.getData();
        int length = requestPacket.getLength();
        String request = new String(data, 0, length);
        System.out.println(workerNameAndIndent + ": Client request: <"+request+">");

        String responseString = parseRequest(request);
        System.out.println(workerNameAndIndent + ": Sending response: " + responseString);
        byte[] responseBytes = responseString.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(
            responseBytes, responseBytes.length, requestPacket.getAddress(), requestPacket.getPort()
        );

        return responsePacket;
    }

    private String parseRequest(String request) {
        Command command = null;

        if (request.startsWith("READ")) command = Command.READ;
        else if (request.startsWith("WRITE")) command = Command.WRITE;
        else return "BAD REQUEST: Only READ or WRITE allowed.";

        String[] commandAndRest = request.split(" ", 2);
        if (commandAndRest.length != 2)
            return "BAD REQUEST: Invalid parameters.";

        String filename, line, data = null;
        if (command == Command.READ) {
            String[] split = commandAndRest[1].split(",", 2);
            if (split.length != 2) return "BAD REQUEST: Command READ takes 2 arguments.";
            filename = split[0];
            line = split[1];
        } else {
            String[] split = commandAndRest[1].split(",", 3);
            if (split.length != 3) return "BAD REQUEST: Command WRITE takes 3 arguments.";
            filename = split[0];
            line = split[1];
            data = split[2];
        }

        int line_no = 0;
        try {
            line_no = Integer.parseInt(line);
            if (line_no < 1) return "ILLEGAL LINE NUMBER";
        } catch (NumberFormatException e) {
            return "ILLEGAL LINE NUMBER";
        }

        if (command == Command.READ) {
            return fileHandler.read(filename, line_no, workerNameAndIndent);
        } else {
            return fileHandler.write(filename, line_no, data, workerNameAndIndent);
        }
    }
}
