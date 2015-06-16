package nametagautoprint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static nametagautoprint.NametagAutoPrint.queueController;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jdom2.JDOMException;

/**
 *
 * @author Tim, Ben
 */
public class NametagAutoPrint extends Application {

    //public static final String octoPrintHostName = "127.0.0.1:5000";
    public static final String octoPrintHostName = "192.168.5.36/octoprint";
    public static final String imagesDirectory = "images";
    public static final String scadDirectory = "scad";
    public static final String stlDirectory = "stl";
    public static final String gcodeDirectory = "gcode";
    
    public static String name = "tim";

    static Process p;

    public static File configFile = new File("config.xml");
    public static File queueFile = new File("queue.xml");
    
    static BorderPane root;
    static Pane preview;
    static Pane settings;
    static Pane printers;
    static Pane queue;
    
    // Remove static after preview and export are moved to printer and/or nametag classes
    public static PreviewController previewController;
    public static SettingsController settingsController;
    public static PrintersController printersController;
    public static QueueController queueController;
    
    public static enum Panes{Preview, Settings, Printers, Queue};

    static Image image;
    
    private static NametagAutoPrint instance;
    
    private static Stage stage;

    public NametagAutoPrint() {
        instance = this;
    }
    // static method to get instance of view

    public static NametagAutoPrint getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        
        stage = primaryStage;
        
        FXMLLoader previewFxmlLoader = new FXMLLoader();
        previewFxmlLoader.setLocation(getClass().getResource("preview.fxml"));
        preview = (Pane) previewFxmlLoader.load();
        previewController = (PreviewController) previewFxmlLoader.getController();
        
        FXMLLoader settingsFxmlLoader = new FXMLLoader();
        settingsFxmlLoader.setLocation(getClass().getResource("settings.fxml"));
        settings = (Pane) settingsFxmlLoader.load();
        settingsController = (SettingsController) settingsFxmlLoader.getController();
        
        FXMLLoader printersFxmlLoader = new FXMLLoader();
        printersFxmlLoader.setLocation(getClass().getResource("printers.fxml"));
        printers = (Pane) printersFxmlLoader.load();
        printersController = (PrintersController) printersFxmlLoader.getController();
        
        FXMLLoader queueFxmlLoader = new FXMLLoader();
        queueFxmlLoader.setLocation(getClass().getResource("queue.fxml"));
        queue = (Pane) queueFxmlLoader.load();
        queueController = (QueueController) queueFxmlLoader.getController();
        
        root = new BorderPane();
        
        root.setCenter(preview);
        //root.setCenter(settings);
        
        Scene scene = new Scene(root, 1000, 800);

        final KeyCombination exitCombo = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (exitCombo.match(event)) {
                System.out.println("CTRL + W Pressed, Exiting... ");
                System.exit(0);
            }
        });

        if(!configFile.exists()) {
            configFile.createNewFile();
            XML.buildConfig();
        }
        if(!queueFile.exists())
            queueFile.createNewFile();
        
        try {
            XML.loadPrinters();
        } catch (JDOMException | IOException e) {
            System.err.println("Could not load printers!");
        }
        
        PrintMaster.addToQueue(new Nametag("Test"));
        PrintMaster.addToQueue(new Nametag("Test2"));
        PrintMaster.addToQueue(new Nametag("Test3"));
        
        scene.getStylesheets().add("nametagautoprint/style.css");

        stage.setTitle("Nametag Generator");
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }
    
    public void setPane(Panes pane){
        switch(pane){
            case Preview:
                root.getChildren().remove(root.getCenter());
                root.setCenter(preview);
                settingsController.resetLogin();
                break;
            
            case Settings:
                root.getChildren().remove(root.getCenter());
                root.setCenter(settings);
                settingsController.init();
                break;
                
            case Printers:
                root.getChildren().remove(root.getCenter());
                root.setCenter(printers);
                break;
                
            case Queue:
                root.getChildren().remove(root.getCenter());
                root.setCenter(queue);
                break;
        }
    }
    
    public Stage getStage(){
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
