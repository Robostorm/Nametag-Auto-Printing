package nametagautoprint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PrinterQueue {
    private List<Printer> printers = new ArrayList<>();
    int position = 0;

    public PrinterQueue(){}

    public PrinterQueue(List<Printer> printers) {
        this.printers = printers;
    }

    public void add(Printer printer) {
        printers.add(printer);
    }

    public void addAll(Collection printers) {
        this.printers.addAll(printers);
    }

    public void remove(Printer printer) {
        printers.remove(printer);
    }

    public Printer get(int index) {
        return printers.get(index);
    }

    public void set(int index, Printer printer) {
        printers.set(index, printer);
    }

    public List<Printer> getList() {
        return printers;
    }

    public Printer getNext() {
        int i = position, checked = 0;
        for(;;) {
            if(printers.get(i).isActive() && printers.get(i).isAvailable())
                return printers.get(i);
            checked++;
            if(checked == printers.size())
                return null;
            if(i >= printers.size())
                i = 0;
            else
                i++;
        }
    }
}
