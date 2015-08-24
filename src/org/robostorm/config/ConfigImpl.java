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

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigImpl implements Config {

    private ServletContext servletContext;
    private File printersFile;
    private File queueFile;
    private File dataDirectory;
    private String password;
    private String imagesDirectory;
    private String scadDirectory;
    private String stlDirectory;
    private String gcodeDirectory;
    private long loopTime;
    private boolean loggedIn = false;
    private char[] bannedChars;

    @Override
    public File getPrintersFile() {
        return printersFile;
    }

    @Override
    public File getQueueFile() {
        return queueFile;
    }

    @Override
    public String getPassword() {
        return password;
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
    public String getImagesDirectoryPath() {
        return servletContext.getRealPath("/") + imagesDirectory;
    }

    @Override
    public String getScadDirectoryPath() {
        return servletContext.getRealPath("/") + scadDirectory;
    }

    @Override
    public String getStlDirectoryPath() {
        return servletContext.getRealPath("/") + stlDirectory;
    }

    @Override
    public String getGcodeDirectoryPath() {
        return servletContext.getRealPath("/") + gcodeDirectory;
    }

    @Override
    public String getDataDirectoryPath() {
        return dataDirectory.getAbsolutePath();
    }

    @Override
    public long getLoopTime() {
        return loopTime;
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public char[] getBannedChars() {
        return bannedChars;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setBannedChars(char[] bannedChars) {
        this.bannedChars = bannedChars;
    }

    @Override
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public ConfigImpl(ServletContext servletContext, String printersFileName, String queueFileName) {
        this.servletContext = servletContext;
        File dataDirectory = new File(servletContext.getRealPath("/") + "../data");
        this.dataDirectory = dataDirectory;
        if(!dataDirectory.exists())
            dataDirectory.mkdir();
        printersFile = new File(dataDirectory.getAbsolutePath() + "/" + printersFileName);
        queueFile = new File(dataDirectory.getAbsolutePath() + "/" + queueFileName);
    }

    @Override
    public void loadPrinters(PrinterQueue printerQueue) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(printersFile);
        Element configElement = document.getRootElement();
        Element printers = configElement.getChild("printers");
        List<Printer> list = new ArrayList<>();
        for(Element printer : printers.getChildren()) {
            list.add(new Printer(printer.getAttributeValue("name"),
                    printer.getAttributeValue("ip"),
                    printer.getAttribute("port").getIntValue(),
                    printer.getAttributeValue("apiKey"),
                    printer.getAttributeValue("configFile"),
                    printer.getAttribute("active").getBooleanValue(),
                    printer.getAttribute("printing").getBooleanValue(),
                    this));
        }
        System.out.println("Read printers file");
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
        System.out.println("Wrote printers file");
        //xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(printersFile.getAbsolutePath()));
    }

    @Override
    public void buildPrinters() throws IOException {
        Element root = new Element("config");
        Document config = new Document(root);
        Element printers = new Element("printers");
        config.getRootElement().addContent(printers);
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Built printers file");
        //xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(printersFile.getAbsolutePath()));

    }

    @Override
    public void loadQueue(NameTagQueue nameTagQueue, PrinterQueue printerQueue) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(queueFile);
        Element queue = document.getRootElement();
        List<NameTag> list = new ArrayList<>();
        for(Element nametag : queue.getChildren()) {
            list.add(new NameTag(nametag.getAttributeValue("name"),
                    printerQueue.getPrinter(nametag.getAttributeValue("printer")),
                    nametag.getAttributeValue("stl"),
                    nametag.getAttributeValue("gcode"),
                    Boolean.parseBoolean(nametag.getAttributeValue("printing")),
                    this));
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
        System.out.println("Wrote queue file");
        //xmlOutputter.output(queue, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(queue, new FileWriter(queueFile.getAbsolutePath()));
    }

    @Override
    public void buildQueue() throws IOException {
        Element root = new Element("queue");
        Document config = new Document(root);
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Built queue file");
        //xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(queueFile.getAbsolutePath()));
    }
}
