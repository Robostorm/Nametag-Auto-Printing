/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class PrintersController implements Initializable {

    @FXML ScrollPane printerList;
    @FXML VBox printerBox;
    @FXML TextField newPrinter;
    @FXML Button addPrinter;
    @FXML Button printersBack;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        printerList = new ScrollPane() {
            @Override
            public void requestFocus() { }
        };
        
        newPrinter.setOnAction(e -> addPrinter.fire());
        printersBack.setOnAction(e -> NametagAutoPrint.getInstance().setPane(NametagAutoPrint.Panes.Settings));
        
        addPrinter.setOnAction(e -> {
            PrintMaster.addPrinter(new Printer(newPrinter.getText()));
            newPrinter.selectAll();
        });
    }
    
    public ObservableList<Node> getPrinterPanes(){
        return printerBox.getChildren();
    }
    
}
