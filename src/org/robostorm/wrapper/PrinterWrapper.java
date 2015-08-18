package org.robostorm.wrapper;

import org.robostorm.model.Printer;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class PrinterWrapper {
    List<Printer> printers;
    boolean[] deleted;
    List<MultipartFile> files;

    public PrinterWrapper(){}

    public PrinterWrapper(List<Printer> printers) {
        this.printers = printers;
    }

    public PrinterWrapper(List<Printer> printers, boolean[] deleted) {
        this.printers = printers;
        this.deleted = deleted;
    }

    public PrinterWrapper(List<Printer> printers, boolean[] deleted, List<MultipartFile> files) {
        this.printers = printers;
        this.deleted = deleted;
        this.files = files;
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

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }
}
