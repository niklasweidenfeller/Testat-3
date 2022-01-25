public class FileMonitor {
    boolean activeWriter = false;
    int readCount = 0;
    int writeCount = 0;

    public synchronized void startRead() {
        try {
            while (writeCount>0) wait();
            readCount++;
            notifyAll();
        } catch (InterruptedException e) {}
    }
    public synchronized void endRead() {
        readCount--;
        notifyAll();
    }
    public synchronized void startWrite() {
        try {
            writeCount++;
            while (readCount>0 || activeWriter) wait();
            activeWriter = true;
        } catch (InterruptedException e) {}
    }
    public synchronized void endWrite() {
        writeCount--;
        activeWriter = false;
        notifyAll();
    }
}
