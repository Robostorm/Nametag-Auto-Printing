/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author tim
 */
public class NametagAutoPrint extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        TextField nameField = new TextField("Name");
        Image image = new Image("file:openscad/out.png");
        ImageView imageView = new ImageView(image);
        
        nameField.setOnKeyTyped((KeyEvent e) -> {
            OpenscadThread.name = nameField.getText();
            (new Thread(new OpenscadThread())).start();
        });
        
        VBox root = new VBox();
        root.getChildren().add(imageView);
        root.getChildren().add(nameField);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Nametag Generator");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
