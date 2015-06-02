package nametagautoprint;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.HBox;
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

    public static final String octoPrintHostName = "127.0.0.1:5000";
    public static final String imagesDirectory = "images";
    public static final String scadDirectory = "scad";
    public static final String stlDirectory = "stl";
    public static final String gcodeDirectory = "gcode";

    public static String name = "tim";

    static Process p;

    static TextField nameField;
    static Image image;
    static ImageView imageView;
    static Button preview;
    static Button sumit;
    static HBox buttonBar;
    static HBox nameBar;
    static ProgressBar progress;

    @Override
    public void start(Stage primaryStage) {

        nameField = new TextField("Name");
        image = new Image("file:openscad/out.png");
        imageView = new ImageView(image);
        preview = new Button("Preview");
        sumit = new Button("Submit");
        progress = new ProgressBar(0);
        buttonBar = new HBox(preview, sumit);
        nameBar = new HBox(nameField, buttonBar);

        preview.setOnAction((ActionEvent event) -> {
            name = nameField.getText();
            preview();
        });

        sumit.setOnAction((ActionEvent event) -> {
            name = nameField.getText();
            export();
        });



        VBox root = new VBox();
        root.getChildren().add(imageView);
        root.getChildren().add(nameBar);
        root.getChildren().add(progress);

        Scene scene = new Scene(root, 510, 560);

        final KeyCombination exitCombo = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(exitCombo.match(event)) {
                System.out.println("CTRL + W Pressed, Exiting... ");
                System.exit(0);
            }
        });

        primaryStage.setTitle("Nametag Generator");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void preview() {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                Platform.runLater(() -> progress.setProgress(-1));

                File images = new File(imagesDirectory);
                if(!images.exists())
                    images.mkdir();

                String pngargs = String.format(" -o %s/%s.png -D name=\"%s\" -D chars=%d " +
                        "--camera=0,0,0,0,0,0,100 openscad/name.scad", imagesDirectory, name, name, name.length(), scadDirectory, name);
                if (p == null || !p.isAlive()) {
                    try {

                        System.out.println("Args: " + pngargs);

                        p = Runtime.getRuntime().exec("openscad" + pngargs);

                        while (p.isAlive()) {
                            image = new Image(String.format("file:%s/%s.png", imagesDirectory, name));
                        }
                        Platform.runLater(() -> imageView.setImage(image));
                        System.out.println("Done");

                    } catch (IOException e) {
                        System.out.println("exception happened - here's what I know: ");
                        e.printStackTrace();
                        System.exit(-1);
                    }
                } else {
                    System.out.println("Openscad already running. Waiting...");
                }
                
                Platform.runLater(() -> progress.setProgress(0));

                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void export() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
                Platform.runLater(() -> progress.setProgress(-1));

                File stl = new File(stlDirectory);
                if(!stl.exists())
                    stl.mkdir();

                File gcode = new File(gcodeDirectory);
                if(!gcode.exists())
                    gcode.mkdir();

                String stlargs = String.format(" -o %s/%s.stl -D name=\"%s\" -D chars=%d " +
                        "--camera=0,0,0,0,0,0,100 openscad/name.scad", stlDirectory, name, name, name.length(), scadDirectory, name);
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
                
                Platform.runLater(() -> progress.setProgress(0));
                upload();
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void upload() throws Exception{
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                File file = new File("scadOut/out.gcode");
                String remotePath = "http://" + octoPrintHostName + "/api/files/local";
                if(!file.exists()) {
                    System.out.println("File upload failed: file not found");
                }
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                FileBody fileBody = new FileBody(file);
                builder.addPart("file", fileBody);

                HttpPost post = new HttpPost(remotePath);
                post.setEntity(builder.build());
                HttpClient client = HttpClientBuilder.create().build();
                HttpResponse response = client.execute(post);
                System.out.printf("Server Returned Code: %d", response.getStatusLine().getStatusCode());
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
