package org.robostorm.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jdom2.Element;
import org.robostorm.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

public class Printer {

    private int id;
    private final String name;
    private String ip;
    private int port;
    private String apiKey;
    private File configFile;
    private boolean active;
    private boolean available;

    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    public Printer(String name){
        this.name = name;
        ip = "127.0.0.1";
        port = 5000;
        apiKey = "ApiKey";
        configFile = new File("config/slic3r/mendel.ini");
        active = false;
        available = true;
    }

    public Printer(String name, String ip, int port, String apiKey, boolean active, boolean available){
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.apiKey = apiKey;
        this.configFile = new File("config/slic3r/mendel.ini");
        this.active = active;
        this.available = available;
    }
    

    @Override
    public String toString(){
        return name;
    }

    public void slice(NameTag tag){
        String slic3rargs = String.format(" %s/%s.stl --output %s/%s.gcode", config.getStlDirectory(), tag.toString(),
                config.getGcodeDirectory(), tag.toString());
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
        printerElement.setAttribute("available", Boolean.toString(available));
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
