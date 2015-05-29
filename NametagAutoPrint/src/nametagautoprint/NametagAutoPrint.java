/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author tim
 */
public class NametagAutoPrint extends Application {
    
    public static String name = "tim";
    
    String pngargs = " -o openscad/out.png -D name=\""+name+"\" -D chars="+name.length()+" --camera=0,0,0,0,0,0,100 openscad/name.scad";
    
    static Process p;
    
    static TextField nameField = new TextField("Name");
    static Image image = new Image("file:openscad/out.png");
    static ImageView imageView = new ImageView(image);
    static Button preview = new Button("Preview");
    static Button sumit = new Button("Submit");
    static HBox buttonBar = new HBox(preview, sumit);
    static HBox nameBar = new HBox(nameField, buttonBar);
    
    @Override
    public void start(Stage primaryStage) {
        preview.setOnAction((ActionEvent e) -> {
            this.name = nameField.getText();
            preview();
            //(new Thread(new OpenscadThread())).start();
        });
        
        VBox root = new VBox();
        root.getChildren().add(imageView);
        root.getChildren().add(nameBar);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Nametag Generator");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void preview() {
        pngargs = " -o openscad/out.png -D name=\""+name+"\" -D chars="+name.length()+" --camera=0,0,0,0,0,0,100 openscad/name.scad";
        if(p == null || !p.isAlive()){
            try {

                System.out.println("Args: "+pngargs);

                p = Runtime.getRuntime().exec("openscad" + pngargs);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                String s = null;  
                
                // read the output from the command
                //System.out.println("Here is the standard output of the command:\n");
                //while ((s = stdInput.readLine()) != null) {
                    //System.out.println(s);
                //}

                // read any errors from the attempted command
                //System.out.println("Here is the standard error of the command (if any):\n");
               //while ((s = stdError.readLine()) != null) {
                    //System.out.println(s);
                //}
                
                while(p.isAlive())
                
                image = new Image("file:openscad/out.png");
                imageView.setImage(image);
                
                System.out.println("Done");
                
            }catch (IOException e) {
                System.out.println("exception happened - here's what I know: ");
                e.printStackTrace();
                System.exit(-1);
            }
        }else {
            System.out.println("Openscad already running. Waiting...");
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    
}
