package org.robostorm.queue;

import org.robostorm.config.Config;
import org.robostorm.model.Printer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PrinterQueue {
    @Autowired
    private Config config;
    private List<Printer> printers = new ArrayList<>();
    private int position = 0;

    public void addPrinter(Printer printer) throws IOException {
        printers.add(printer);
        config.savePrinters(this);
        System.out.println("Added Printer: " + printer);
        System.out.println("All Printers: " + printers);
    }

    public void addAllPrinters(Collection<Printer> newPrinters) throws IOException {
        System.out.println("New Printers: " + newPrinters);
        printers.addAll(newPrinters);
        config.savePrinters(this);
        System.out.println("Added Printers: " + newPrinters);
        System.out.println("All Printers: " + printers);
    }

    public void removePrinter(Printer printer) throws IOException {
        printers.remove(printer);
        config.savePrinters(this);
        System.out.println("Removed Printer: " + printer);
        System.out.println("All Printers: " + printers);
    }

    public void updatePrinter(Printer oldPrinter, Printer newPrinter) throws IOException {
        for(int i = 0; i < printers.size(); i++) {
            if(printers.get(i) == oldPrinter) {
                printers.set(i, newPrinter);
            }
        }
        config.savePrinters(this);
    }

    public List<Printer> getAllPrinters() {
        return printers;
    }

    public Printer getNextPrinter() {
        int i = position, checked = 0;
        if(printers.size() == 0)
            return null;
        for(;;) {
            if(printers.get(i).isActive() && !printers.get(i).isPrinting()) {
                if(position >= printers.size() - 1)
                    position = 0;
                else
                    position++;
                return printers.get(i);
            }
            checked++;
            if(checked == printers.size())
                return null;
            if(i >= printers.size() - 1)
                i = 0;
            else
                i++;
        }
    }

    public Printer getPrinter(String name) {
        for(Printer printer : printers)
            if(printer.getName().equals(name))
                return printer;
        return null;
    }

    public void removePrinter(String name) throws IOException {
        for(Printer printer : printers)
            if(printer.getName().equals(name))
                removePrinter(printer);
    }
}