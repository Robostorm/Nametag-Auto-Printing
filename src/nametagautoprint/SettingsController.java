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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class SettingsController implements Initializable {
    
    @FXML private VBox passBox;
    @FXML private PasswordField password;
    @FXML private HBox settings;
    @FXML private Button printersBtn;
    @FXML private Button settingsBack;
    
    //Not meant to be secure- just to keep stray kids from screwing with things
    private static final String passwordStr = "r0b0tic$";
    public boolean loggedIn = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        password.setOnAction(e -> {
            if(password.getText().equals(passwordStr)){
                settings.setVisible(true);
                loggedIn = true;
            }
            password.clear();
        });
        
        printersBtn.setOnAction(e -> NametagAutoPrint.getInstance().setPane(NametagAutoPrint.Panes.Printers));
        settingsBack.setOnAction(e -> NametagAutoPrint.getInstance().setPane(NametagAutoPrint.Panes.Preview));
        
    }
    
    public void init(){
        settings.setVisible(loggedIn);
    }
    
    public void resetLogin(){
        loggedIn = false;
    }
    
    public boolean isLoggedIn(){
        return loggedIn;
    }
   
}
