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
        xmlOutputter.output(config, System.out);
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(config, new FileWriter(NametagAutoPrint.configFile.getName()));

    }

}
