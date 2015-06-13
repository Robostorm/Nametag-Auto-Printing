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
import javafx.scene.image.Image;
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
public class Nametag {
    
    private String name;
    private Printer printer = null;
    private boolean printing = false;
    private Image preview;
    
    private File stl;
    private File gcode;
    
    private GridPane grid;
    private Label nameLabel;
    private TextField stlField;
    private TextField gcodeField;
    private TextField printerField;
    private TextField printingField;
    private Button deleteButton;
    
    public Nametag(String name){
        this.name = name;
        
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
        column3.setPercentWidth(30);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(100-(column1.getPercentWidth()+column2.getPercentWidth()+column3.getPercentWidth()));
        column4.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().addAll(column1, column2, column3, column4);
        
        nameLabel = new Label(name);
        nameLabel.setId("tagName");
        grid.add(nameLabel, 0, 0, 1, 3);
        
        stlField = new TextField(stl == null ? "No stl" : stl.getName());
        stlField.setEditable(false);
        grid.add(stlField, 1, 0, 1, 1);
        
        gcodeField = new TextField(gcode == null ? "Not sliced" : gcode.getName());
        gcodeField.setEditable(false);
        grid.add(gcodeField, 2, 0, 1, 1);
        
        printerField = new TextField(printer == null ? "No Printer" : printer.toString());
        printerField.setEditable(false);
        grid.add(printerField, 1, 1, 1, 1);
        
        printingField = new TextField(printing ? "Printing..." : "In Queue");
        printingField.setEditable(false);
        grid.add(printingField, 2, 1, 1, 1);
        
        deleteButton = new Button("Remove");
        deleteButton.setOnAction(e -> PrintMaster.removeFromQueue(this));
        grid.add(deleteButton, 3, 1, 1, 1);
    }
    
    public Pane getPane(){
        return (Pane) grid;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
}
