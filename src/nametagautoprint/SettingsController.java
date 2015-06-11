/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

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

        
        Printer test = new Printer("Test", "3247215", 1234);
        Printer test2 = new Printer("Test2", "sdewrqewrvqewr ", 5678);
        
        printerBox.getChildren().addAll(test.getPane(), test2.getPane());
        
    }    
    
}
