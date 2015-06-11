/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class PreviewController implements Initializable {
    
    @FXML private ImageView previewImage;
    @FXML private TextField nameField;
    @FXML private Button previewBtn;
    @FXML private Button submitBtn;
    @FXML private Button settingsBtn;
    @FXML private ProgressBar progressBar;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        previewImage.setImage(new Image("file:openscad/out.png"));
        
        setProgress(0);
        
        previewBtn.setOnAction((ActionEvent e) -> {
            NametagAutoPrint.getInstance().name = nameField.getText();
            NametagAutoPrint.getInstance().preview();
        });
        
        submitBtn.setOnAction((ActionEvent e) -> {
            NametagAutoPrint.getInstance().name = nameField.getText();
            NametagAutoPrint.getInstance().export();
        });
        
        settingsBtn.setOnAction((ActionEvent e) -> {
            NametagAutoPrint.getInstance().showSettings(true);
        });
    }
    
    public void refreshImage(Image image){
        previewImage.setImage(image);
    }
    
    public void setProgress(double progress){
        progressBar.setProgress(progress);
    }
    
}