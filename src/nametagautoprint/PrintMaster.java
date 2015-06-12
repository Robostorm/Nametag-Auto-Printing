/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tim
 */
public class PrintMaster {
    
    public static List<Printer> printers = new ArrayList<>();
    
    public static void addPrinter(Printer printer){
        printers.add(printer);
        NametagAutoPrint.getInstance().printersController.getPrinterPanes().add(printer.getPane());
        System.out.println("Added Printer");
    }
    
    public static void removePrinter(Printer printer){
        printers.remove(printer);
        NametagAutoPrint.getInstance().printersController.getPrinterPanes().remove(printer.getPane());
    }
    
}
