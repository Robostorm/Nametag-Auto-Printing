/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nametagautoprint;

import java.io.File;
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

/**
 *
 * @author tim
 */
public class Printer {
    
    private final String name;
    private String ip;
    private int port;
    private File config;
    private boolean active;
    
    private GridPane grid;
    private Label nameLabel;
    private TextField ipField;
    private TextField portField;
    private TextField configField;
    private Button configButton;
    private CheckBox activeBox;
    private Button deleteButton;
    
    public Printer(String name){
        this.name = name;
        this.ip = "127.0.0.1";
        this.port = 5000;
        this.config = new File("config/slic3r/mendel.ini");
        this.active = false;
        
        init();
    }
    
    public Printer(String name, String ip, int port){
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.config = new File("config/slic3r/mendel.ini");
        this.active = false;
        
        init();
    }

    public Printer(String name, String ip, int port, boolean active){
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.config = new File("config/slic3r/mendel.ini");
        this.active = active;

        init();
    }
    
    public Printer(String name, String ip, int port, String config){
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.config = new File(config);
        this.active = false;
        
        init();
    }
    
    public Printer(String name, String ip, int port, File config){
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.config = config;
        this.active = false;
        
        init();
    }
    
    public Printer(String name, String ip, int port, String config, boolean active){
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.config = new File(config);
        this.active = active;
        
        init();
    }
    
    public Printer(String name, String ip, int port, File config, boolean active){
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.config = config;
        this.active = active;
        
        init();
    }
    
    // So that we don't need to repeat this in every copnstructor
    private void init(){
        
        this.grid = new GridPane();
        
        this.grid.setHgap(10);
        this.grid.setVgap(10);
        this.grid.setPadding(new Insets(10, 10, 10, 10));
        this.grid.setBackground(new Background(new BackgroundFill(Paint.valueOf("yellow"), CornerRadii.EMPTY, Insets.EMPTY)));
        //this.grid.setGridLinesVisible(true);
        
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(20);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(30);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(7);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(100-(column1.getPercentWidth()+column2.getPercentWidth()+column3.getPercentWidth()));
        column4.setHalignment(HPos.RIGHT);
        this.grid.getColumnConstraints().addAll(column1, column2, column3, column4);
        
        this.nameLabel = new Label(name);
        this.nameLabel.setId("printerName");
        this.grid.add(nameLabel, 0, 0, 1, 3);
        
        this.ipField = new TextField(ip);
        ipField.setOnKeyTyped(e -> {
            this.ip = ipField.getText();
        });
        this.grid.add(ipField, 1, 0, 1, 1);
        
        this.portField = new TextField(Integer.toString(port));
        portField.setOnKeyTyped(e -> {
            port = Integer.parseInt(portField.getText());
        });
        this.grid.add(portField, 2, 0, 1, 1);
        
        this.configField = new TextField(config.getName());
        this.configField.setEditable(false);
        this.grid.add(configField, 1, 1, 1, 1);
        
        this.configButton = new Button("Choose");
        this.configButton.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Config");
            fileChooser.setInitialDirectory(config.getParentFile());
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Slic3r Config", "ini"));
            config = fileChooser.showOpenDialog(NametagAutoPrint.getInstance().getStage());
            configField.setText(config.getName());
        });
        this.grid.add(configButton, 2, 1, 1, 1);
        
        this.activeBox = new CheckBox();
        this.activeBox.setSelected(active);
        this.activeBox.setOnAction(e ->{
            this.active = activeBox.isSelected();
        });
        Label activeLabel = new Label("Active");
        activeLabel.setGraphic(activeBox);
        activeLabel.setContentDisplay(ContentDisplay.RIGHT);
        this.grid.add(activeLabel, 3, 0, 1, 1);
        
        this.deleteButton = new Button("Remove");
        this.deleteButton.setOnAction(e -> PrintMaster.removePrinter(this));
        this.grid.add(deleteButton, 3, 1, 1, 1);
    }
    
    public Pane getPane(){
        return (Pane) this.grid;
    }
    
}
