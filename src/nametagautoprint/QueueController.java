/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class QueueController implements Initializable {
    
    @FXML private ScrollPane queueList;
    @FXML private VBox queueBox;
    @FXML private Button queueBack;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        queueList = new ScrollPane() {
            @Override
            public void requestFocus() { }
        };
        
        queueBack.setOnAction(event -> {
            NametagAutoPrint.getInstance().setPane(NametagAutoPrint.Panes.Settings);
            try {
                XML.saveQueue();
            } catch (IOException e) {
                
            }
        });
        
    }
    
    public ObservableList<Node> getPrinterPanes(){
        return queueBox.getChildren();
    }
    
}
