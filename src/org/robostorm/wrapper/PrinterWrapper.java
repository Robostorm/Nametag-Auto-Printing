package org.robostorm.wrapper;

import org.robostorm.model.Printer;

import java.util.ArrayList;
import java.util.List;

public class PrinterWrapper {
    List<Printer> printers;
    boolean[] deleted;

    public PrinterWrapper(){}

    public PrinterWrapper(List<Printer> printers) {
        this.printers = printers;
    }

    public PrinterWrapper(List<Printer> printers, boolean[] deleted) {
        this.printers = printers;
        this.deleted = deleted;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public void setPrinters(List<Printer> printers) {
        this.printers = printers;
    }

    public boolean[] getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean[] deleted) {
        this.deleted = deleted;
    }
}
