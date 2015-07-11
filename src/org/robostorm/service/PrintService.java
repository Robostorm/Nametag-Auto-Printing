package org.robostorm.service;

import org.robostorm.model.NameTag;
import org.robostorm.model.Printer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface PrintService {

    String print(String name);

    void addPrinter(Printer printer) throws IOException;

    void updatePrinter(Printer oldPrinter, Printer newPrinter) throws IOException;

    void removePrinter(Printer printer) throws IOException;

    void addNameTag(NameTag nameTag) throws IOException;

    void updateNameTag(NameTag oldNameTag, NameTag newNameTag) throws IOException;

    void removeNameTag(NameTag nameTag) throws IOException;

    List<Printer> getPrinters();

    List<NameTag> getQueue();
}
