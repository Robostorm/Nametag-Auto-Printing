package org.robostorm.service;

import org.json.simple.JSONObject;
import org.robostorm.config.Config;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class PreviewServiceImpl implements PreviewService {

    @Autowired
    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public String preview(String name) {
        if(name == null || name.equals("")) {
            return null;
        }

        JSONObject json = new JSONObject();

        File imagesDir = new File(config.getImagesDirectoryPath());
        if(!imagesDir.exists())
            imagesDir.mkdir();

        File image = new File(imagesDir.getAbsolutePath() + "/" + name + ".png" );

        if(image.exists()) {
            json.put("code", 0);
            json.put("image", config.getImagesDirectory() + name + ".png");
            return json.toJSONString();
        }


        String pngargs = String.format(" -o %s/%s.png -D name=\"%s\" -D chars=%d "
                        + "--camera=0,0,0,0,0,0,100 %sname.scad", imagesDir.getAbsolutePath(),
                name, name, name.equals("") ? 4 : name.length(), config.getScadDirectoryPath());

/*        String pngargs = String.format(" -o %s/%s.png -D chars=%d"
                        + " --camera=0,0,0,0,0,0,100 %sname.scad", imagesDir.getAbsolutePath(),
                name, name.equals("") ? 4 : name.length(), config.getScadDirectoryPath());;*/

        //System.out.println(pngargs);

        String command = "sh " + config.getScadDirectoryPath() + "test.sh";

        try {
            System.out.println("Args: " + pngargs);
            //System.out.println("DISPLAY: " + System.getenv("DISPLAY"));
/*            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                System.out.format("%s=%s%n", envName, env.get(envName));
            }*/
            //System.out.println(command);
            Process p = Runtime.getRuntime().exec("openscad" + pngargs);
            //Process p = Runtime.getRuntime().exec("echo $DISPLAY");
//            Process p = Runtime.getRuntime().exec(command);

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

            while (p.isAlive()){}

            json.put("code", 0);
            json.put("image", config.getImagesDirectory() + name + ".png");

            System.out.println("Done rendering");
        } catch (IOException e) {
            System.err.println("Could not generate preview!");
            json.put("code", -1);
            json.put("error", "Could not generate preview!");
        }

        return json.toJSONString();
    }
}
