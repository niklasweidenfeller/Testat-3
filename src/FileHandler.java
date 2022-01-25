import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FileHandler {
    // TODO: Schreiberprio!
    private class FileMonitor {
        int readerCount = 0;
        boolean activeWriter = false;

        public synchronized void startRead() {
            try {
                readerCount++;
                while (activeWriter) wait();
                notify();                
            } catch (InterruptedException e) {}
        }
        public synchronized void endRead() {
            readerCount--;
            if (readerCount==0) notifyAll();            
        }
        public synchronized void startWrite() {
            try {
                while (readerCount > 0 || activeWriter) wait();
                activeWriter = true;
            } catch (InterruptedException e) {}
        }
        public synchronized void endWrite() {
            activeWriter = false;
            notifyAll();
        }
    }

    private Map<String, FileMonitor> fileMonitors;
    private Map<String, File> files;

    public FileHandler(String filePath) {
        fileMonitors = new HashMap<>();
        files = new HashMap<>();

        File directory = new File(filePath);
        if (directory.isDirectory()) {
            File[] listFiles = directory.listFiles();
            for (File file : listFiles) {
                files.put(file.getName(), file);
                fileMonitors.put(file.getName(), new FileMonitor());
            }
        }
    }

    public String read(String filename, int line) {
        FileMonitor monitor = fileMonitors.get(filename);
        if (monitor == null) return "FILE NOT FOUND";

        monitor.startRead();

        ArrayList<String> lines = null;
        String returnValue = null;
        try { 
            lines = readLinesFromFile(filename);
            if (line > lines.size()) {
                returnValue = "LINE NUMBER OUT OF BOUNDS";
            } else {
                returnValue = lines.get(line-1);
            }
        } catch (IOException e) {
            returnValue = "ERROR";
        }

        monitor.endRead();

        return returnValue;
    }

    public String write(String filename, int line, String data) {
        FileMonitor monitor = fileMonitors.get(filename);
        if (monitor == null) return "FILE NOT FOUND";

        monitor.startWrite();

        ArrayList<String> lines = null;
        String returnValue = null;
        try { 
            lines = readLinesFromFile(filename);
            if (line > lines.size()) {
                returnValue = "LINE NUMBER OUT OF BOUNDS";
            } else {
                lines.set(line - 1, data);
                writeBackToFile(filename, lines);
                returnValue = "OK";
            }
        } catch (IOException e) {
            returnValue = "ERROR";
        }

        monitor.endWrite();

        return returnValue;
    }

    private ArrayList<String> readLinesFromFile(String filename) throws IOException {
        File f = files.get(filename);
        BufferedReader fileReader = null;
        ArrayList<String> lines = new ArrayList<>();

        try {
            fileReader = new BufferedReader(
                new FileReader(f)
            );

            while (true) {
                String newLine = fileReader.readLine();
                if (newLine == null) break;
                lines.add(newLine);
            }
        } catch (IOException e) {
            throw e; // Der Aufrufer behandelt die Ausnahme.
        } finally {
            try {
                if (fileReader != null) fileReader.close();
            } catch (IOException e) {}
        }

        return lines;
    }

    private void writeBackToFile(String filename, ArrayList<String> lines) throws FileNotFoundException {

        File f = files.get(filename);
        PrintWriter fileWriter = new PrintWriter(f);
        for (String line : lines) {
            fileWriter.println(line);
        }
        fileWriter.close();
    }
}
