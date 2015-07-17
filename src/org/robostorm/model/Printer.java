package org.robostorm.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jdom2.Element;
import org.robostorm.config.Config;

public class Printer {

    private int id;
    private String name;
    private String ip;
    private int port;
    private String apiKey;
    private File configFile;
    private boolean active;
    private boolean printing;

    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    public Printer(String name, Config config){
        this.name = name;
        ip = "127.0.0.1";
        port = 5000;
        apiKey = "ApiKey";
        configFile = new File("config/slic3r/mendel.ini");
        active = false;
        printing = true;
        this.config = config;
        id = System.identityHashCode(this);
    }

    public Printer(String name, String ip, int port, String apiKey, boolean active, boolean printing, Config config){
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.apiKey = apiKey;
        this.configFile = new File("config/slic3r/mendel.ini");
        this.active = active;
        this.printing = printing;
        this.config = config;
        id = System.identityHashCode(this);
    }
    

    @Override
    public String toString(){
        return name;
    }

    public void slice(NameTag tag){

        File gcodeDirectory = new File(config.getGcodeDirectoryPath());
        if(!gcodeDirectory.exists())
            gcodeDirectory.mkdir();

        String slic3rargs = String.format(" %s/%s.stl --output %s/%s.gcode", config.getStlDirectoryPath(), tag.toString(),
                config.getGcodeDirectoryPath(), tag.toString());
            try {

                System.out.println("Args: " + slic3rargs);

                Process p = Runtime.getRuntime().exec("slic3r" + slic3rargs);

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

                while (p.isAlive()){}
                tag.setPrinter(this);
                tag.setGcode(new File(String.format("%s/%s.gcode", config.getStlDirectory(), tag.toString())));
                System.out.println("Done");

            } catch (IOException e) {
                throw new RuntimeException("exception happened - here's what I know: ", e);
            }

    }

    public Element toElement() {
        Element printerElement = new Element("printer");
        printerElement.setAttribute("name", name);
        printerElement.setAttribute("ip", ip);
        printerElement.setAttribute("port", Integer.toString(port));
        printerElement.setAttribute("apiKey", apiKey);
        printerElement.setAttribute("file", configFile.getPath());
        printerElement.setAttribute("active", Boolean.toString(active));
        printerElement.setAttribute("printing", Boolean.toString(printing));
        return printerElement;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPrinting() {
        return printing;
    }

    public void setPrinting(boolean printing) {
        this.printing = printing;
    }
}
