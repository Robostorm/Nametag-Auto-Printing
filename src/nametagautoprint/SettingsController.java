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
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class SettingsController implements Initializable {

    @FXML VBox printerList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Printer test = new Printer("Robostorm", "3247215", 1234);
        Printer test2 = new Printer("Test2", "sdewrqewrvqewr ", 5678);
        
        printerList.getChildren().addAll(test.getPane(), test2.getPane());
        
    }    
    
}
