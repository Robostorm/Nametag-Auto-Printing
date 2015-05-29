/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nametagautoprint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author tim
 */
public class OpenscadThread implements Runnable {
    
    public static String name = "tim";
    
    String s = null;
    
    String pngargs = " -o openscad/out.png -D name=\""+name+"\" --camera=0,0,0,0,0,0,100 openscad/name.scad";
    
    Process p;
    
    @Override
    public void run() {
        if(p == null || !p.isAlive()){
            try {

                System.out.println("Args: "+pngargs);

                p = Runtime.getRuntime().exec("openscad" + pngargs);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

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
            }catch (IOException e) {
                System.out.println("exception happened - here's what I know: ");
                e.printStackTrace();
                System.exit(-1);
            }
        }else {
            System.out.println("Openscad already running. Waiting...");
        }
    }

}
