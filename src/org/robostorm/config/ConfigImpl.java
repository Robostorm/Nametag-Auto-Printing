package org.robostorm.config;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.robostorm.model.NameTag;
import org.robostorm.model.Printer;
import org.robostorm.queue.NameTagQueue;
import org.robostorm.queue.PrinterQueue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigImpl implements Config {

    private File configFile;
    private File queueFile;
    private String octoPrintHostName;
    private String imagesDirectory;
    private String scadDirectory;
    private String stlDirectory;
    private String gcodeDirectory;
    private long loopTime;

    @Override
    public File getConfigFile() {
        return configFile;
    }

    @Override
    public File getQueueFile() {
        return queueFile;
    }

    @Override
    public String getOctoPrintHostName() {
        return octoPrintHostName;
    }

    @Override
    public String getImagesDirectory() {
        return imagesDirectory;
    }

    @Override
    public String getScadDirectory() {
        return scadDirectory;
    }

    @Override
    public String getStlDirectory() {
        return stlDirectory;
    }

    @Override
    public String getGcodeDirectory() {
        return gcodeDirectory;
    }

    @Override
    public long getLoopTime() {
        return loopTime;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public void setQueueFile(File queueFile) {
        this.queueFile = queueFile;
    }

    public void setOctoPrintHostName(String octoPrintHostName) {
        this.octoPrintHostName = octoPrintHostName;
    }

    public void setImagesDirectory(String imagesDirectory) {
        this.imagesDirectory = imagesDirectory;
    }

    public void setScadDirectory(String scadDirectory) {
        this.scadDirectory = scadDirectory;
    }

    public void setStlDirectory(String stlDirectory) {
        this.stlDirectory = stlDirectory;
    }

    public void setGcodeDirectory(String gcodeDirectory) {
        this.gcodeDirectory = gcodeDirectory;
    }

    public void setLoopTime(long loopTime) {
        this.loopTime = loopTime;
    }

    @Override
    public void loadPrinters(PrinterQueue printerQueue) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(configFile);
        Element configElement = document.getRootElement();
        Element printers = configElement.getChild("printers");
        List<Printer> list = new ArrayList<>();
        for(Element printer : printers.getChildren()) {
            list.add(new Printer(printer.getAttributeValue("name"), printer.getAttributeValue("ip"),
                    printer.getAttribute("port").getIntValue(), printer.getAttributeValue("apiKey"),
                    printer.getAttribute("active").getBooleanValue(), printer.getAttribute("available").getBooleanValue()));
        }
        System.out.println("Built read config file");
        printerQueue.addAllPrinters(list);
    }

    @Override
    public void savePrinters(PrinterQueue printerQueue) throws IOException {
        Element root = new Element("config");
        Document config = new Document(root);
        Element printers = new Element("printers");
        for(Printer printer : printerQueue.getAllPrinters())
            printers.addContent(printer.toElement());
        config.getRootElement().addContent(printers);
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Built wrote config file");
        //xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(configFile.getName()));
    }

    @Override
    public void buildConfig() throws IOException {
        Element root = new Element("config");
        Document config = new Document(root);
        Element printers = new Element("printers");
        config.getRootElement().addContent(printers);
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Built queue config file");
        //xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(configFile.getName()));

    }

    @Override
    public void loadQueue(NameTagQueue nameTagQueue, PrinterQueue printerQueue) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(queueFile);
        Element queue = document.getRootElement();
        List<NameTag> list = new ArrayList<>();
        for(Element nametag : queue.getChildren()) {
            list.add(new NameTag(nametag.getAttributeValue("name"), printerQueue.getPrinter(nametag.getAttributeValue("printer")),
                    nametag.getAttributeValue("stl"), nametag.getAttributeValue("gcode")));
        }
        System.out.println("Read queue file");
        nameTagQueue.addAllToQueue(list);
    }

    @Override
    public void saveQueue(NameTagQueue nameTagQueue) throws IOException {
        Element root = new Element("queue");
        Document queue = new Document(root);
        for(NameTag nameTag : nameTagQueue.getAllNametags())
            queue.getRootElement().addContent(nameTag.toElement());
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Wrote config file");
        //xmlOutputter.output(queue, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(queue, new FileWriter(queueFile.getName()));
    }

    @Override
    public void buildQueue() throws IOException {
        Element root = new Element("queue");
        Document config = new Document(root);
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Built queue file");
        //xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(queueFile.getName()));
    }
}
