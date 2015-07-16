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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
public class PrintersController implements Initializable {

    @FXML private ScrollPane printerList;
    @FXML private VBox printerBox;
    @FXML private TextField newPrinter;
    @FXML private Button addPrinter;
    @FXML private Button printersBack;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        printerList = new ScrollPane() {
            @Override
            public void requestFocus() { }
        };
        
        newPrinter.setOnAction(event -> addPrinter.fire());
        printersBack.setOnAction(event -> {
            NametagAutoPrint.getInstance().setPane(NametagAutoPrint.Panes.Settings);
            try {
                XML.savePrinters();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        addPrinter.setOnAction(event -> {
            PrintMaster.addPrinter(new Printer(newPrinter.getText()));
            newPrinter.selectAll();
        });
    }

    public ObservableList<Node> getPrinterPanes(){
        return printerBox.getChildren();
    }

}
