/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author tim
 */
public class PrintMaster {
    
    private static List<Printer> printers = new ArrayList<>();
    private static List<Nametag> queue = new ArrayList<>();
    
    public static void addPrinter(Printer printer){
        printers.add(printer);
        NametagAutoPrint.getInstance().printersController.getPrinterPanes().add(printer.getPane());
        System.out.println("Added Printer: " + printer);
        System.out.println("All Printers: " + printers);
    }
    
    public static void addAllPrinters(Collection<Printer> newPrinters){
        System.out.println("New Printers: " + newPrinters);
        printers.addAll(newPrinters);
        newPrinters.forEach(p -> {
            NametagAutoPrint.getInstance().printersController.getPrinterPanes().add(p.getPane());
            System.out.println(NametagAutoPrint.getInstance().printersController);
        System.out.println("Added Printer: " + p);
        });
        System.out.println("Added Printers: " + newPrinters);
        System.out.println("All Printers: " + printers);
    }
    
    public static void removePrinter(Printer printer){
        printers.remove(printer);
        NametagAutoPrint.getInstance().printersController.getPrinterPanes().remove(printer.getPane());
        System.out.println("Removed Printer: " + printer);
        System.out.println("All Printers: " + printers);
    }
    
    public static void addToQueue(Nametag tag){
        queue.add(tag);
        NametagAutoPrint.getInstance().queueController.getPrinterPanes().add(tag.getPane());
        System.out.println("Added Nametag to queue: " + tag);
        System.out.println("Queue: " + queue);
    }
    
    public static void addAllToQueue(Collection<Nametag> nameTags){
        System.out.println("New Printers: " + nameTags);
        queue.addAll(nameTags);
        nameTags.forEach(p -> {
            NametagAutoPrint.getInstance().queueController.getPrinterPanes().add(p.getPane());
            System.out.println(NametagAutoPrint.getInstance().queueController);
            System.out.println("Added Nametag to queue: " + p);
        });
        System.out.println("Added to queue: " + nameTags);
        System.out.println("Queue: " + queue);
    }
    
    public static void removeFromQueue(Nametag tag){
        queue.remove(tag);
        NametagAutoPrint.getInstance().queueController.getPrinterPanes().remove(tag.getPane());
        System.out.println("Removed Nametag from queue: " + tag);
        System.out.println("Queue: " + printers);
    }

    public static Printer getPrinter(int i) {
        return printers.get(i);
    }

    public static List<Printer> getAllPrinters() {
        return printers;
    }

    public static Element printerToElement(Printer printer) {
        Element printerElement = new Element("printer");
        printerElement.setAttribute("name", printer.getName());
        printerElement.setAttribute("ip", printer.getIp());
        printerElement.setAttribute("port", Integer.toString(printer.getPort()));
        printerElement.setAttribute("active", Boolean.toString(printer.isActive()));
        return printerElement;
    }
}
