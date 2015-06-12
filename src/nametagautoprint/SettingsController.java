/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class SettingsController implements Initializable {

    @FXML ScrollPane printerList;
    @FXML VBox printerBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        printerList = new ScrollPane() {
            @Override
            public void requestFocus() { }
        };

        
        /*Printer test = new Printer("Test", "3247215", 1234);
        Printer test2 = new Printer("Test2", "sdewrqewrvqewr ", 5678);

        printerBox.getChildren().addAll(test.getPane(), test2.getPane());*/

        List<Printer> printers = null;
        try {
            printers = loadPrinters();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(printers != null)
            for (int i = 0; i < printers.size(); i++)
                printerBox.getChildren().add(i, printers.get(i).getPane());
        else
            System.out.println("could not load printers");
    }    

    public List<Printer> loadPrinters() throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(NametagAutoPrint.configFile);
        Element config = document.getRootElement();
        Element printers = config.getChild("printers");
        List<Printer> list = new ArrayList<>();
        for(Element printer : printers.getChildren()) {
            list.add(new Printer(printer.getAttributeValue("name"), printer.getAttributeValue("ip"),
                    printer.getAttribute("port").getIntValue(), printer.getAttribute("active").getBooleanValue()));
        }
        return list;
    }

    public static void build() throws IOException {
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
