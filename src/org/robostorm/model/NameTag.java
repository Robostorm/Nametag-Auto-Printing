package org.robostorm.model;

import org.jdom2.Element;
import org.robostorm.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class NameTag {

    private int id;
    private String name;
    private Printer printer = null;
    private boolean printing = false;

    private File stl;
    private File gcode;

    private Config config;

    public NameTag(){}

    public NameTag(String name, Config config) {
        this.name = name;
        this.config = config;
        id = System.identityHashCode(this);
    }

    public NameTag(String name, Printer printer, String stl, String gcode, boolean printing, Config config) {
        this.name = name;
        if(!stl.equals(""))
            this.stl = new File(config.getScadDirectory() + stl);
        if(!gcode.equals(""))
            this.gcode = new File(config.getGcodeDirectory() + gcode);
        this.printer = printer;
        this.config = config;
        this.printing = printing;
        id = System.identityHashCode(this);
    }

    public NameTag(NameTag nameTag) {
        this.id = nameTag.id;
        this.name = nameTag.name;
        this.stl = nameTag.stl;
        this.gcode = nameTag.gcode;
        this.config = nameTag.config;
    }


    public void export() {

        File stlDirectory = new File(config.getStlDirectoryPath());
        if(!stlDirectory.exists())
            stlDirectory.mkdir();

        String stlargs = String.format(" -o %s -D name=\"%s\" -D chars=%d --camera=0,0,0,0,0,0,100 "
                + "%sname.scad", config.getStlDirectoryPath() + name + ".stl", name, name.length(), config.getScadDirectoryPath());

        try {

            setStl(new File(String.format("%s/%s.stl", config.getStlDirectory(), name)));

            System.out.println("Args: " + stlargs);

            Process p = Runtime.getRuntime().exec("openscad" + stlargs);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            while (p.isAlive()) {}
            System.out.println("Done");

        } catch (IOException e) {
            throw new RuntimeException("Could not generate stl!", e);
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
        if (printer != null)
            nametagElement.setAttribute("printer", printer.getName());
        else
            nametagElement.setAttribute("printer", "");
        nametagElement.setAttribute("printing", Boolean.toString(printing));
        return nametagElement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrinting(){
        return printing;
    }

    public void setPrinting(boolean printing) {
        this.printing = printing;
    }

    public Printer getPrinter() {
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public boolean isGenerated(){
        return stl != null;
    }

    public boolean isSliced(){
        return gcode != null;
    }

    public File getGcode() {
        return gcode;
    }

    public void setGcode(File gcode) {
        this.gcode = gcode;
    }

    public File getStl() {
        return stl;
    }

    public void setStl(File stl) {
        this.stl = stl;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
