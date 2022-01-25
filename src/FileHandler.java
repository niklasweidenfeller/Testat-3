import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Diese Klasse dient dem Zugriff auf die Dateien
 * des Filesystems.
 */
public class FileHandler {

    /**
     * Die eigentlichen Dateien sowie ein Zugriffsmonitor je
     * Datei. Zugriff mittels Dateiname.
     */ 
    private Map<String, FileMonitor> fileMonitors;
    private Map<String, File> files;

    /**
     * Der Konstruktor legt für alle im Dateipfad
     * vorhandenen Dateien einen Zugriffsmonitor und ein
     * File-Objekt an.
     * 
     * @param filePath Der Standard-Dateipfad des Servers.
     */
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

    /**
     * Diese Methode führt die Leseoperation auf eine Datei durch.
     *
     * @param workerNameAndIndent   Dient nur zur Konsolenausgabe.
     * @return  Die gelesene Zeile oder eine entsprechende Fehlermeldung.
     */
    public String read(String filename, int line, String workerNameAndIndent) {
        FileMonitor monitor = fileMonitors.get(filename);
        if (monitor == null) return "FILE NOT FOUND";

        // Zugangsmonitor: Eintrittsprotokoll
        monitor.startRead();
        // Im kritischen Abschnitt

        System.out.println(workerNameAndIndent + ": Reading");

        // Schlafanweisung symbolisiert das Arbeiten im kritischen Abschnitt.
        try {
            Thread.sleep(5000);
        } catch (Exception e) {}

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

        System.out.println(workerNameAndIndent + ": Ending read");
        // Zugangsmonitor: Austrittsprotokoll. Verlassen des k.A.
        monitor.endRead();

        return returnValue;
    }

    /**
     * Diese Methode führt die Schreiboperation auf eine Datei durch.
     *
     * @param data                  Die zu schreibende Zeile.
     * @param workerNameAndIndent   Dient nur zur Konsolenausgabe.
     * @return                      "OK" oder eine entsprechende
     *                              Fehlermeldung.
     */
    public String write(String filename, int line, String data, String workerNameAndIndent) {
        FileMonitor monitor = fileMonitors.get(filename);
        if (monitor == null) return "FILE NOT FOUND";

        // Zugangsmonitor: Eintrittsprotokoll
        monitor.startWrite();
        // Im kritischen Abschnitt

        System.out.println(workerNameAndIndent + ": Writing");

        // Schlafanweisung symbolisiert das Arbeiten im kritischen Abschnitt.
        try {
            Thread.sleep(5000);
        } catch (Exception e) {}

        /* Lesen aller Zeilen der Datei. Anschließend
           die gewünschte Zeile ersetzen und gesamte
           Datei neu schreiben. */
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

        System.out.println(workerNameAndIndent + ": Ending write");
        // Zugangsmonitor: Austrittsprotokoll. Verlassen des k.A.
        monitor.endWrite();

        return returnValue;
    }

    /**
     * Diese Methode liest eine gesamte Datei ein und speichert dabei
     * jede Zeile als String in einer ArrayList.
     * 
     * @return  Die gelesene Datei Zeilenweise in Form einer ArrayList. 
     */
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
                if (newLine == null) break; // Zeilenende
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

    /**
     * Diese Methode schreibt alle Einträge einer ArrayList<String>
     * in eine angegebene Datei.
     * 
      * @param lines    Die zu schreibenden Zeilen.
      */
    private void writeBackToFile(String filename, ArrayList<String> lines) throws FileNotFoundException {

        File f = files.get(filename);
        PrintWriter fileWriter = new PrintWriter(f);
        for (String line : lines) {
            fileWriter.println(line);
        }
        fileWriter.close();
    }
}
