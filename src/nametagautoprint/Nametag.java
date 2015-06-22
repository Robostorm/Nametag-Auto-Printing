/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nametagautoprint;

import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
import org.jdom2.Element;

import static nametagautoprint.NametagAutoPrint.*;

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

    public Nametag(String name) {
        this.name = name;

        init();
    }

    public Nametag(String name, String stl, String gcode) {
        this.name = name;
        if(!stl.equals(""))
            this.stl = new File(scadDirectory + stl);
        if(!gcode.equals(""))
            this.gcode = new File(gcodeDirectory + gcode);

        init();
    }

    public Nametag(Nametag nametag) {
        this.name = nametag.name;
        this.stl = nametag.stl;
        this.gcode = nametag.gcode;
    }

    // So that we don't need to repeat this in every copnstructor
    private void init() {

        grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setBackground(new Background(new BackgroundFill(Paint.valueOf("yellow"), CornerRadii.EMPTY, Insets.EMPTY)));
        grid.setPrefWidth(1000);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(20);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(30);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(30);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(100 - (column1.getPercentWidth() + column2.getPercentWidth() + column3.getPercentWidth()));
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

    public Pane getPane() {
        return (Pane) grid;
    }

    public void setName(String name) {
        this.name = name;
        nameLabel.setText(name);
    }

    public void preview() {

        String pngargs = String.format(" -o %s/%s.png -D name=\"%s\" -D chars=%d "
                + "--camera=0,0,0,0,0,0,100 openscad/name.scad", NametagAutoPrint.imagesDirectory,
                name, name, name.equals("") ? 4 : name.length(), scadDirectory, name);
        if (p == null || !p.isAlive()) {
            try {

                System.out.println("Args: " + pngargs);

                p = Runtime.getRuntime().exec("openscad" + pngargs);

                while (p.isAlive()){};

                preview = new Image(String.format("file:%s/%s.png", imagesDirectory, name));

                Platform.runLater(() -> previewController.refreshImage(preview));
                System.out.println("Done");

            } catch (IOException e) {
                System.err.println("Could not generate preview!");
                System.exit(-1);
            }
        } else {
            System.out.println("Openscad already running. Waiting...");
        }
    }

    public void export() {

        stl = new File(String.format("%s/%s.stl", NametagAutoPrint.stlDirectory, name));
        
        stlField.setText(stl.getName());
        
        String stlargs = String.format(" -o %s -D name=\"%s\" -D chars=%d --camera=0,0,0,0,0,0,100 "
                + "openscad/name.scad", stl, name, name.length(), scadDirectory, name);
        if (p == null || !p.isAlive()) {
            try {

                System.out.println("Args: " + stlargs);

                p = Runtime.getRuntime().exec("openscad" + stlargs);

                while (p.isAlive()) {}
                System.out.println("Done");

            } catch (IOException e) {
                System.err.println("Could not generate stl!");
                System.exit(-1);
            }
        } else {
            System.out.println("Openscad already running. Waiting...");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public Element toElement() {
        Element nametagElement = new Element("nametag");
        nametagElement.setAttribute("name", name);
        if (stl != null)
            nametagElement.setAttribute("stl", stl.getName());
        else
            nametagElement.setAttribute("stl", "");
        if (gcode != null)
            nametagElement.setAttribute("gcode", gcode.getName());
        else
            nametagElement.setAttribute("gcode", "");
        return nametagElement;
    }

    public Printer getPrinter() {
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }
}
