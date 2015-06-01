package nametagautoprint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

/**
 *
 * @author Tim, Ben
 */
public class NametagAutoPrint extends Application {

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

        preview.setOnAction((ActionEvent e) -> {
            name = nameField.getText();
            preview();
        });

        sumit.setOnAction((ActionEvent e) -> {
            name = nameField.getText();
            export();
        });



        VBox root = new VBox();
        root.getChildren().add(imageView);
        root.getChildren().add(nameBar);
        root.getChildren().add(progress);

        Scene scene = new Scene(root, 600, 700);

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

                File scadOut = new File("scadOut");
                if(!scadOut.exists())
                    scadOut.mkdir();

                String pngargs = " -o scadOut/out.png -D name=\"" + name + "\" -D chars=" + name.length() + " --camera=0,0,0,0,0,0,100 openscad/name.scad";
                if (p == null || !p.isAlive()) {
                    try {

                        System.out.println("Args: " + pngargs);

                        p = Runtime.getRuntime().exec("openscad" + pngargs);

                        while (p.isAlive()) {
                            image = new Image("file:scadOut/out.png");
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

                File scadOut = new File("scadOut");
                if(!scadOut.exists())
                    scadOut.mkdir();

                String stlargs = " -o scadOut/out.stl -D name=\"" + name + "\" -D chars=" + name.length() + " --camera=0,0,0,0,0,0,100 openscad/name.scad";
                if (p == null || !p.isAlive()) {
                    try {

                        System.out.println("Args: " + stlargs);

                        p = Runtime.getRuntime().exec("openscad" + stlargs);

                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                        String s = null;

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

                String slic3rargs = " scadOut/out.stl";
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
