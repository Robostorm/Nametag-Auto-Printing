package org.robostorm.wrapper;

import org.robostorm.model.Printer;

import java.util.ArrayList;
import java.util.List;

public class PrinterWrapper {
    List<Printer> printers;

    public PrinterWrapper(){}

    public PrinterWrapper(List<Printer> printers) {
        this.printers = printers;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public void setPrinters(List<Printer> printers) {
        this.printers = printers;
    }
}
