package org.robostorm.config;

import org.jdom2.JDOMException;
import org.robostorm.queue.NameTagQueue;
import org.robostorm.queue.PrinterQueue;

import java.io.File;
import java.io.IOException;

public interface Config {

    File getPrintersFile();
    File getQueueFile();
    String getPassword();
    String getImagesDirectory();
    String getScadDirectory();
    String getStlDirectory();
    String getGcodeDirectory();

    String getImagesDirectoryPath();

    String getScadDirectoryPath();

    String getStlDirectoryPath();

    String getGcodeDirectoryPath();

    String getDataDirectoryPath();

    long getLoopTime();

    boolean isLoggedIn();

    void setLoggedIn(boolean loggedIn);

    void loadPrinters(PrinterQueue printerQueue) throws JDOMException, IOException;
    void savePrinters(PrinterQueue printerQueue) throws IOException;
    void buildPrinters() throws IOException;
    void loadQueue(NameTagQueue nameTagQueue, PrinterQueue printerQueue) throws JDOMException, IOException;
    void saveQueue(NameTagQueue nameTagQueue) throws IOException;
    void buildQueue() throws IOException;
}
