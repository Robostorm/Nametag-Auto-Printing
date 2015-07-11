package org.robostorm.service;

import org.robostorm.model.NameTag;
import org.robostorm.model.Printer;
import org.robostorm.queue.NameTagQueue;
import org.robostorm.queue.PrinterQueue;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class PrintServiceImpl implements PrintService {

    @Autowired
    PrinterQueue printerQueue;
    @Autowired
    NameTagQueue nameTagQueue;

    @Override
    public String print(String name) {
        return null;
    }

    @Override
    public void addPrinter(Printer printer) throws IOException {
        printerQueue.addPrinter(printer);
    }

    @Override
    public void updatePrinter(Printer oldPrinter, Printer newPrinter) throws IOException {
        printerQueue.updatePrinter(oldPrinter, newPrinter);
    }

    @Override
    public void removePrinter(Printer printer) throws IOException {
        printerQueue.removePrinter(printer);
    }

    @Override
    public void addNameTag(NameTag nameTag) throws IOException {
        nameTagQueue.addToQueue(nameTag);
    }

    @Override
    public void updateNameTag(NameTag oldNameTag, NameTag newNameTag) throws IOException {
        nameTagQueue.updateNameTag(oldNameTag, newNameTag);
    }

    @Override
    public void removeNameTag(NameTag nameTag) throws IOException {
        nameTagQueue.removeFromQueue(nameTag);
    }

    @Override
    public List<Printer> getPrinters() {
        return printerQueue.getAllPrinters();
    }

    @Override
    public List<NameTag> getQueue() {
        return nameTagQueue.getAllNametags();
    }
}
