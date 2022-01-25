public class FileMonitor {
    boolean activeReader = false;
    boolean activeWriter = false;
    int readCount = 0;
    int waitingWriters = 0;

    public synchronized void startRead() {
        try {
            while (activeWriter || waitingWriters>0) wait();
            readCount++;
            if (readCount == 1) activeReader = true;
            notifyAll();
        } catch (InterruptedException e) {}
    }
    public synchronized void endRead() {
        readCount--;
        if (readCount == 0) activeReader = false;
        notifyAll();
    }
    public synchronized void startWrite() {
        try {
            waitingWriters++;
            while (activeReader || activeWriter) wait();
            activeWriter = true;
            waitingWriters--;
        } catch (InterruptedException e) {}
    }
    public synchronized void endWrite() {
        activeWriter = false;
        notifyAll();
    }
}
