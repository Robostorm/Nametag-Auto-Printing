/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static nametagautoprint.NametagAutoPrint.previewController;

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
    
    private Nametag currentTag = new Nametag("");
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        setProgress(0);
        
        previewImage.setImage(new Image("file:openscad/out.png"));
        
        previewBtn.setOnAction(e -> {
            currentTag.setName(nameField.getText());
            System.out.println(nameField.getText());
            Task task = new Task() {

                @Override
                protected Object call() throws Exception {
                    Platform.runLater(() -> setProgress(0.5));
                    currentTag.preview();
                    Platform.runLater(() -> previewController.setProgress(1));
                    Thread.sleep(500);
                    Platform.runLater(() -> previewController.setProgress(0));
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });
        
        submitBtn.setOnAction(e -> {
            currentTag.setName(nameField.getText());
            Nametag nametag = new Nametag(currentTag.toString());
            Task task = new Task() {

                @Override
                protected Object call() throws Exception {
                    Platform.runLater(() -> setProgress(0.5));
                    nametag.export();
                    Platform.runLater(() -> previewController.setProgress(1));
                    PrintMaster.addToQueue(nametag);
                    Thread.sleep(500);
                    Platform.runLater(() -> previewController.setProgress(0));
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });
        
        settingsBtn.setOnAction((ActionEvent e) -> {
            NametagAutoPrint.getInstance().setPane(NametagAutoPrint.Panes.Settings);
        });
    }
    
    public void refreshImage(Image image){
        previewImage.setImage(image);
    }
    
    public void setProgress(double progress){
        progressBar.setProgress(progress);
    }
    
}
