package org.robostorm.model;

import org.jdom2.Element;
import org.robostorm.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

public class NameTag {

    private int id;
    private String name;
    private Printer printer = null;
    private boolean printing = false;

    private File stl;
    private File gcode;

    private Config config;

    public NameTag(String name, Config config) {
        this.name = name;
        this.config = config;
        id = System.identityHashCode(this);
    }

    public NameTag(String name, Printer printer, String stl, String gcode, Config config) {
        this.name = name;
        if(!stl.equals(""))
            this.stl = new File(config.getScadDirectory() + stl);
        if(!gcode.equals(""))
            this.gcode = new File(config.getGcodeDirectory() + gcode);
        this.printer = printer;
        this.config = config;
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
                + "%s/name.scad", config.getStlDirectoryPath() + name + ".stl", name, name.length(), config.getScadDirectoryPath());

        try {

            setStl(new File(String.format("%s/%s.stl", config.getStlDirectory(), name)));

            System.out.println("Args: " + stlargs);

            Process p = Runtime.getRuntime().exec("openscad" + stlargs);

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
