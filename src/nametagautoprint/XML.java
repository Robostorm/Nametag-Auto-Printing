package nametagautoprint;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XML {

    public static void loadPrinters() throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(NametagAutoPrint.configFile);
        Element config = document.getRootElement();
        Element printers = config.getChild("printers");
        List<Printer> list = new ArrayList<>();
        for(Element printer : printers.getChildren()) {
            list.add(new Printer(printer.getAttributeValue("name"), printer.getAttributeValue("ip"),
                    printer.getAttribute("port").getIntValue(), printer.getAttributeValue("apiKey"),
                    printer.getAttribute("active").getBooleanValue()));
        }
        System.out.println("Built read config file");
        PrintMaster.addAllPrinters(list);
    }

    public static void savePrinters() throws IOException {
        Element root = new Element("config");
        Document config = new Document(root);
        Element printers = new Element("printers");
        for(Printer printer : PrintMaster.getAllPrinters())
            printers.addContent(printer.toElement());
        config.getRootElement().addContent(printers);
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Built wrote config file");
        xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(NametagAutoPrint.configFile.getName()));
    }

    public static void buildConfig() throws IOException {
        Element root = new Element("config");
        Document config = new Document(root);
        Element printers = new Element("printers");
        config.getRootElement().addContent(printers);
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Built queue config file");
        xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(NametagAutoPrint.configFile.getName()));

    }

    public static void buildQueue() throws IOException {
        Element root = new Element("queue");
        Document config = new Document(root);
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Built queue file");
        xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(NametagAutoPrint.queueFile.getName()));
    }

    public static void loadQueue() throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(NametagAutoPrint.queueFile);
        Element queue = document.getRootElement();
        List<Nametag> list = new ArrayList<>();
        for(Element nametag : queue.getChildren()) {
            list.add(new Nametag(nametag.getAttributeValue("name"), nametag.getAttributeValue("stl"),
                    nametag.getAttributeValue("gcode")));
        }
        System.out.println("Read queue file");
        PrintMaster.addAllToQueue(list);
    }

    public static void saveQueue() throws IOException {
        Element root = new Element("queue");
        Document queue = new Document(root);
        for(Nametag nametag : PrintMaster.getAllNametags())
            queue.getRootElement().addContent(nametag.toElement());
        XMLOutputter xmlOutputter = new XMLOutputter();
        System.out.println("Wrote config file");
        xmlOutputter.output(queue, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(queue, new FileWriter(NametagAutoPrint.queueFile.getName()));
    }

}
