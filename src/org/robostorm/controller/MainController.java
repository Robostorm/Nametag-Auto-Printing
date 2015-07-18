package org.robostorm.controller;

import org.jdom2.JDOMException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.robostorm.config.Config;
import org.robostorm.model.NameTag;
import org.robostorm.model.Printer;
import org.robostorm.queue.NameTagQueue;
import org.robostorm.queue.PrinterQueue;
import org.robostorm.service.PreviewService;
import org.robostorm.service.PrintService;
import org.robostorm.wrapper.NameTagWrapper;
import org.robostorm.wrapper.PrinterWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.spi.http.HttpContext;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private PreviewService previewService;
    @Autowired
    PrinterQueue printerQueue;
    @Autowired
    NameTagQueue nameTagQueue;
    @Autowired
    private Config config;
    @Autowired
    private PrintService printService;
    @Autowired
    TaskExecutor taskExecutor;

    @PostConstruct
    public void init() {
        try {
            if (!config.getConfigFile().exists())
                config.buildConfig();
            else
                config.loadPrinters(printerQueue);
            if (!config.getQueueFile().exists())
                config.buildQueue();
            else
                config.loadQueue(nameTagQueue, printerQueue);
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }

/*        if(!printService.isRunning()) {
            printService.start();
        }*/
    }

    @PreDestroy
    public void destroy() {
        if (printService.isRunning()) {
            printService.stop();
        }
    }

    /*********/
    /*Preview*/
    /*********/

    @RequestMapping("/preview")
    @ResponseBody
    public ResponseEntity<String> preview(@RequestParam("name") String name) {
        String json;
        if ((json = previewService.preview(name)) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    /*********/
    /*Manager*/
    /*********/

    @RequestMapping(value = "/manager", method = RequestMethod.GET)
    public String manager(Model model) {
        model.addAttribute("printerWrapper", new PrinterWrapper(printerQueue.getAllPrinters()));
        model.addAttribute("nameTagWrapper", new NameTagWrapper(nameTagQueue.getAllNametags()));
        return "manager";
    }

    @RequestMapping(value = "/manager/printers", method = RequestMethod.GET)
    public String editPrinters(Model model) {
        model.addAttribute("printerWrapper", new PrinterWrapper(printerQueue.getAllPrinters()));
        return "printers";
    }

    @RequestMapping(value = "/manager/printers", method = RequestMethod.POST)
    public String editPrintersSubmit(@ModelAttribute("printerWrapper") PrinterWrapper printerWrapper) throws IOException {
        if(printerQueue != null && printerWrapper.getPrinters().size() > 0) {
            for(int i = 0; i < printerWrapper.getPrinters().size(); i++) {
                printerQueue.updatePrinter(printerWrapper.getPrinters().get(i));
            }
        }
        return "redirect:/ntap/manager#printersTab";
    }

    @RequestMapping(value = "/manager/nameTags", method = RequestMethod.POST)
    public String editNameTagsSubmit(@ModelAttribute("nameTagWrapper") NameTagWrapper nameTagWrapper) throws IOException {
        if(printerQueue != null && nameTagWrapper.getNameTags().size() > 0) {
            for(int i = 0; i < nameTagWrapper.getNameTags().size(); i++) {
                nameTagQueue.updateNameTag(nameTagWrapper.getNameTags().get(i));
            }
        }
        return "redirect:/ntap/manager";
    }

    /*******/
    /*Queue*/

    /*******/

    @RequestMapping(value = "/queue/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addToQueue(@RequestParam("name") String name) {
        if (name != null && !name.equals("")) {
            try {
                nameTagQueue.addToQueue(new NameTag(name, config));
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping("/queue/view")
    @ResponseBody
    public String viewQueue() throws IllegalAccessException {
        JSONArray list = new JSONArray();
        for (NameTag nameTag : nameTagQueue.getAllNametags()) {
            JSONObject object = new JSONObject();
            for (Field field : nameTag.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.getName().equals("config"))
                    if(field.getName().equals("printer") ||
                            field.getName().equals("stl") ||
                            field.getName().equals("gcode")) {
                        object.put(field.getName(), field.get(nameTag).toString());
                    } else {
                        object.put(field.getName(), field.get(nameTag));
                    }
            }
            list.add(object);
        }
        return list.toJSONString();
    }

    @RequestMapping("/queue/viewNames")
    @ResponseBody
    public String viewNamesQueue() throws IllegalAccessException {
        JSONArray list = new JSONArray();
        for (NameTag nameTag : nameTagQueue.getAllNametags()) {
            list.add(nameTag.toString());
        }
        return list.toJSONString();
    }

    @RequestMapping(value = "/queue/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> removeFromQueue(@RequestParam("id") Integer id) {
        if (id != null) {
            try {
                nameTagQueue.removeFromQueue(id);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**********/
    /*Printers*/

    /**********/

    @RequestMapping(value = "/printers/add", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> addPrinter(@RequestParam("name") String name,
                                             @RequestParam("ip") String ip,
                                             @RequestParam("port") Integer port,
                                             @RequestParam("apiKey") String apiKey,
                                             @RequestParam("active") Boolean active,
                                             @RequestParam("printing") Boolean printing) {

        if (name != null && !name.equals("")) {
            try {
                printerQueue.addPrinter(new Printer(name, ip, port, apiKey, active, printing, config));
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping("/printers/view")
    @ResponseBody
    public String viewPrinter() throws IllegalAccessException {
        JSONArray list = new JSONArray();
        for (Printer printer : printerQueue.getAllPrinters()) {
            JSONObject object = new JSONObject();
            for (Field field : printer.getClass().getDeclaredFields()) {
                if (!field.getName().equals("config")) {
                    if (field.getName().equals("configFile")) {
                        field.setAccessible(true);
                        object.put(field.getName(), field.get(printer).toString());
                    } else {
                        field.setAccessible(true);
                        object.put(field.getName(), field.get(printer));
                    }
                }
            }
            list.add(object);
        }
        return list.toJSONString();
    }

    @RequestMapping(value = "/printers/update", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> updatePrinter(@RequestParam("id") Integer id,
                                                @RequestParam("name") String name,
                                                @RequestParam("ip") String ip,
                                                @RequestParam("port") Integer port,
                                                @RequestParam("apiKey") String apiKey,
                                                @RequestParam("active") Boolean active,
                                                @RequestParam("printing") Boolean printing) {

        boolean found = false;
        List<Printer> printers = printerQueue.getAllPrinters();
        for (int i = 0; i < printers.size(); i++) {
            if (printers.get(i).getId() == id) {
                found = true;
                printers.get(i).setName(name);
                printers.get(i).setIp(ip);
                printers.get(i).setPort(port);
                printers.get(i).setApiKey(apiKey);
                printers.get(i).setActive(active);
                printers.get(i).setPrinting(printing);
            }
        }
        if (found) {
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/printers/remove", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> removePrinter(@RequestParam("id") Integer id) {
        if (id != null) {
            try {
                printerQueue.removePrinter(id);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**************/
    /*Print Server*/

    /**************/

    @RequestMapping("/ps/start")
    @ResponseBody
    public String start() {
        if (printService.isRunning()) {
            return "Running";
        } else {
            printService.start();
            return "Staring...";
        }
    }

    @RequestMapping("/ps/stop")
    @ResponseBody
    public String stop() {
        if (!printService.isStopped() && printService.isRunning()) {
            printService.stop();
            return "Stopping...";
        } else if (printService.isStopped() && printService.isRunning()) {
            return "Stopped but still alive";
        } else {
            return "Stopped";
        }
    }
}