package nametagautoprint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;

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
    
    static BorderPane root;
    static Pane preview;
    static Pane settings;
    static Pane printers;
    
    // Remove static after preview and export are moved to printer and/or nametag classes
    public static PreviewController previewController;
    public static SettingsController settingsController;
    public static PrintersController printersController;
    
    public static enum Panes{Preview, Settings, Printers};

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
                break;
            
            case Settings:
                root.getChildren().remove(root.getCenter());
                root.setCenter(settings);
                break;
                
            case Printers:
                root.getChildren().remove(root.getCenter());
                root.setCenter(printers);
                break;
        }
    }
    
    public Stage getStage(){
        return stage;
    }
    
    public static void preview() {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                Platform.runLater(() -> previewController.setProgress(-1));

                File images = new File(imagesDirectory);
                if (!images.exists()) {
                    images.mkdir();
                }

                String pngargs = String.format(" -o %s/%s.png -D name=\"%s\" -D chars=%d "
                        + "--camera=0,0,0,0,0,0,100 openscad/name.scad", imagesDirectory, name, name, name.length(), scadDirectory, name);
                if (p == null || !p.isAlive()) {
                    try {

                        System.out.println("Args: " + pngargs);

                        p = Runtime.getRuntime().exec("openscad" + pngargs);
                        
                        
                        do {
                            image = new Image(String.format("file:%s/%s.png", imagesDirectory, name));
                        } while(p.isAlive());
                        
                        Platform.runLater(() -> previewController.refreshImage(image));
                        System.out.println("Done");

                    } catch (IOException e) {
                        System.out.println("exception happened - here's what I know: ");
                        e.printStackTrace();
                        System.exit(-1);
                    }
                } else {
                    System.out.println("Openscad already running. Waiting...");
                }

                Platform.runLater(() -> previewController.setProgress(0));

                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void export() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Platform.runLater(() -> previewController.setProgress(0.1));

                File stl = new File(stlDirectory);
                if (!stl.exists()) {
                    stl.mkdir();
                }

                File gcode = new File(gcodeDirectory);
                if (!gcode.exists()) {
                    gcode.mkdir();
                }

                String stlargs = String.format(" -o %s/%s.stl -D name=\"%s\" -D chars=%d --camera=0,0,0,0,0,0,100 "
                        + "openscad/name.scad", stlDirectory, name, name, name.length(), scadDirectory, name);
                if (p == null || !p.isAlive()) {
                    try {

                        System.out.println("Args: " + stlargs);

                        p = Runtime.getRuntime().exec("openscad" + stlargs);

                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                        String s;

                        // read the output from the command
                        System.out.println("Here is the standard output of the command:\n");
                        while ((s = stdInput.readLine()) != null) {
                            System.out.println(s);
                        }

                        // read any errors from the attempted command
                        System.out.println("Here is the standard error of the command (if any):\n");
                        while ((s = stdError.readLine()) != null) {
                            System.out.println(s);
                        }

                        while (p.isAlive()) //image = new Image("file:openscad/out.png");
                        //imageView.setImage(image);
                        {
                            System.out.println("Done");
                        }

                    } catch (IOException e) {
                        System.out.println("exception happened - here's what I know: ");
                        e.printStackTrace();
                        System.exit(-1);
                    }
                } else {
                    System.out.println("Openscad already running. Waiting...");
                }
                
                Platform.runLater(() -> previewController.setProgress(0.33));
                
                String slic3rargs = String.format(" %s/%s.stl --output %s/%s.gcode", stlDirectory, name, gcodeDirectory, name);
                if (p == null || !p.isAlive()) {
                    try {

                        System.out.println("Args: " + slic3rargs);

                        p = Runtime.getRuntime().exec("slic3r" + slic3rargs);

                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                        String s;

                        // read the output from the command
                        System.out.println("Here is the standard output of the command:\n");
                        while ((s = stdInput.readLine()) != null) {
                            System.out.println(s);
                        }

                        // read any errors from the attempted command
                        System.out.println("Here is the standard error of the command (if any):\n");
                        while ((s = stdError.readLine()) != null) {
                            System.out.println(s);
                        }

                        while (p.isAlive()) //image = new Image("file:openscad/out.png");
                        //imageView.setImage(image);
                        {
                            System.out.println("Done");
                        }

                    } catch (IOException e) {
                        System.out.println("exception happened - here's what I know: ");
                        e.printStackTrace();
                        System.exit(-1);
                    }
                } else {
                    System.out.println("Openscad already running. Waiting...");
                }
                
                Platform.runLater(() -> previewController.setProgress(0.66));
                
                //upload file
                File file = new File(String.format("%s/%s.gcode", gcodeDirectory, name));
                String remotePath = "http://" + octoPrintHostName + "/api/files/local";
                if (!file.exists()) {
                    System.out.println("File upload failed: file not found");
                }
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                FileBody fileBody = new FileBody(file);
                builder.addPart("file", fileBody);

                HttpPost post = new HttpPost(remotePath);
                
                post.setEntity(builder.build());
                post.addHeader("X-Api-Key", "08723BF9C8EE487CB4B7E3F2D989EA8F");
                HttpClient client = HttpClientBuilder.create().build();
                HttpResponse response = client.execute(post);
                System.out.printf("Server Returned Code: %d\n", response.getStatusLine().getStatusCode());
                String message;
                switch (response.getStatusLine().getStatusCode()) {
                    case 201:
                        message = "Upload Successful";
                        break;
                    case 400:
                        message = "File was not uploaded properly";
                        break;
                    case 401:
                        message = "Incorrect API Key";
                        break;
                    case 404:
                        message = "Either invalid save location was provided or API key was incorrect";
                        break;
                    case 409:
                        message = "Either you are attemping to overwirte a file being printed or printer is not operational";
                        break;
                    case 415:
                        message = "You attempting to uplaod a file other than a gcode or stl file";
                        break;
                    case 500:
                        message = "Internal server error, upload failed";
                        break;
                    default:
                        message = "Unexpected responses";
                        break;
                }
                System.out.println(message);

                Platform.runLater(() -> previewController.setProgress(1));
                
                Thread.sleep(500);
                
                Platform.runLater(() -> previewController.setProgress(0));
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
