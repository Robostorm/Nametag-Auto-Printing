/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import org.jdom2.Element;

/**
 *
 * @author tim
 */
public class Printer {
    
    private final String name;
    private String ip;
    private int port;
    private String apiKey;
    private File config;
    private boolean active;
    private boolean available;
    
    private GridPane grid;
    private Label nameLabel;
    private TextField ipField;
    private TextField portField;
    private TextField apiKeyField;
    private TextField configField;
    private Button configButton;
    private CheckBox activeBox;
    private Button deleteButton;
    
    public Printer(String name){
        this.name = name;
        ip = "127.0.0.1";
        port = 5000;
        apiKey = "ApiKey";
        config = new File("config/slic3r/mendel.ini");
        active = false;
        
        init();
    }

    public Printer(String name, String ip, int port, String apiKey, boolean active){
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.apiKey = apiKey;
        this.config = new File("config/slic3r/mendel.ini");
        this.active = active;

        init();
    }
    
    // So that we don't need to repeat this in every copnstructor
    private void init(){
        
        grid = new GridPane();
        
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setBackground(new Background(new BackgroundFill(Paint.valueOf("yellow"), CornerRadii.EMPTY, Insets.EMPTY)));
        grid.setPrefWidth(1000);
        //grid.setGridLinesVisible(true);
        
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(20);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(30);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(7);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(30);
        ColumnConstraints column5 = new ColumnConstraints();
        column5.setPercentWidth(100-(column1.getPercentWidth()+column2.getPercentWidth()+column3.getPercentWidth()+column4.getPercentWidth()));
        column5.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().addAll(column1, column2, column3, column4, column5);
        
        nameLabel = new Label(name);
        nameLabel.setId("printerName");
        grid.add(nameLabel, 0, 0, 1, 3);
        
        ipField = new TextField(ip);
        ipField.setOnKeyTyped(e -> {
            ip = e.getText();
        });
        grid.add(ipField, 1, 0, 1, 1);
        
        portField = new TextField(Integer.toString(port));
        portField.setOnKeyTyped(e -> {
            port = Integer.parseInt(e.getText());
        });
        grid.add(portField, 2, 0, 1, 1);
        
        configField = new TextField(config.getName());
        configField.setEditable(false);
        grid.add(configField, 1, 1, 1, 1);
        
        configButton = new Button("Choose");
        configButton.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Config");
            fileChooser.setInitialDirectory(config.getParentFile());
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Slic3r Config", "ini"));
            config = fileChooser.showOpenDialog(NametagAutoPrint.getInstance().getStage());
            configField.setText(config.getName());
        });
        grid.add(configButton, 2, 1, 1, 1);
        
        apiKeyField = new TextField(apiKey);
        apiKeyField.setOnKeyTyped(e -> {
            apiKey = e.getText();
        });
        grid.add(apiKeyField, 3, 0, 1, 1);
        
        activeBox = new CheckBox();
        activeBox.setSelected(active);
        activeBox.setOnAction(e ->{
            active = activeBox.isSelected();
        });
        Label activeLabel = new Label("Active");
        activeLabel.setGraphic(activeBox);
        activeLabel.setContentDisplay(ContentDisplay.RIGHT);
        grid.add(activeLabel, 4, 0, 1, 1);
        
        deleteButton = new Button("Remove");
        deleteButton.setOnAction(e -> PrintMaster.removePrinter(this));
        grid.add(deleteButton, 4, 1, 1, 1);
    }
    
    public Pane getPane(){
        return (Pane) grid;
    }
    
    @Override
    public String toString(){
        return name;
    }

    public void slice(Nametag tag){
        String slic3rargs = String.format(" %s/%s.stl --output %s/%s.gcode", NametagAutoPrint.stlDirectory, tag.toString(), NametagAutoPrint.gcodeDirectory, tag.toString());
        if (NametagAutoPrint.p == null || !NametagAutoPrint.p.isAlive()) {
            try {

                System.out.println("Args: " + slic3rargs);

                NametagAutoPrint.p = Runtime.getRuntime().exec("slic3r" + slic3rargs);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(NametagAutoPrint.p.getInputStream()));

                BufferedReader stdError = new BufferedReader(new InputStreamReader(NametagAutoPrint.p.getErrorStream()));

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

                while (NametagAutoPrint.p.isAlive()){}
                tag.setPrinter(this);
                System.out.println("Done");

            } catch (IOException e) {
                System.out.println("exception happened - here's what I know: ");
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            System.out.println("Slic3r already running. Waiting...");
        }

    }

    public Element toElement() {
        Element printerElement = new Element("printer");
        printerElement.setAttribute("name", name);
        printerElement.setAttribute("ip", ip);
        printerElement.setAttribute("port", Integer.toString(port));
        printerElement.setAttribute("apiKey", apiKey);
        printerElement.setAttribute("file", config.getPath());
        printerElement.setAttribute("active", Boolean.toString(active));
        return printerElement;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
        ipField.setText(ip);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        portField.setText(Integer.toString(port));
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public File getConfig() {
        return config;
    }

    public void setConfig(File config) {
        this.config = config;
        configField.setText(config.getName());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        activeBox.setSelected(active);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
