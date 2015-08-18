package org.robostorm.controller;

import org.jdom2.JDOMException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.robostorm.config.Config;
import org.robostorm.model.NameTag;
import org.robostorm.model.Printer;
import org.robostorm.queue.NameTagQueue;
import org.robostorm.queue.PrinterQueue;
import org.robostorm.response.Response;
import org.robostorm.service.PreviewService;
import org.robostorm.service.PrintService;
import org.robostorm.wrapper.NameTagWrapper;
import org.robostorm.wrapper.PrintServerWrapper;
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
import java.io.File;
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
            if (!config.getPrintersFile().exists())
                config.buildPrinters();
            else
                config.loadPrinters(printerQueue);
            if (!config.getQueueFile().exists())
                config.buildQueue();
            else
                config.loadQueue(nameTagQueue, printerQueue);
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }

        if (!printService.isRunning()) {
            printService.start();
        }
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
        model.addAttribute("printServerStatus", new PrintServerWrapper(getPrintServerStatus()));
        return "manager";
    }

    @RequestMapping(value = "/manager/printers", method = RequestMethod.GET)
    public String editPrinters(Model model) {
        model.addAttribute("printerWrapper", new PrinterWrapper(printerQueue.getAllPrinters(), new boolean[printerQueue.getAllPrinters().size()]));
        return "printers";
    }

    @RequestMapping(value = "/manager/printers", method = RequestMethod.POST)
    public String editPrintersSubmit(@ModelAttribute("printerWrapper") PrinterWrapper printerWrapper) throws IOException {
        if (printerQueue != null && printerWrapper != null && printerWrapper.getPrinters().size() > 0) {
            for (int i = 0; i < printerWrapper.getPrinters().size(); i++) {
                if (printerWrapper.getPrinters().get(i).getId() != -1 && printerWrapper.getDeleted()[i]) {
                    printerQueue.removePrinter(printerWrapper.getPrinters().get(i).getId());
                } else {
                    if (printerWrapper.getPrinters().get(i).getConfig() == null)
                        printerWrapper.getPrinters().get(i).setConfig(config);
                    if (printerWrapper.getPrinters().get(i).getId() == -1)
                        printerWrapper.getPrinters().get(i).setId(System.identityHashCode(printerWrapper.getPrinters().get(i)));
                    if (printerWrapper.getPrinters().get(i).getConfigFile() == null)
                        printerWrapper.getPrinters().get(i).setConfigFile(new File("config\\slic3r\\mendel.ini"));
                    printerQueue.updatePrinter(printerWrapper.getPrinters().get(i));
                }
            }
        }
        return "redirect:/ntap/manager#printersTab";
    }

    @RequestMapping(value = "/manager/nameTags", method = RequestMethod.GET)
    public String editNameTags(Model model) {
        model.addAttribute("nameTagWrapper", new NameTagWrapper(nameTagQueue.getAllNametags(), new boolean[nameTagQueue.getAllNametags().size()]));
        return "nameTags";
    }

    @RequestMapping(value = "/manager/nameTags", method = RequestMethod.POST)
    public String editNameTagsSubmit(@ModelAttribute("nameTagWrapper") NameTagWrapper nameTagWrapper) throws IOException {
        if (nameTagQueue != null && nameTagWrapper != null && nameTagWrapper.getNameTags().size() > 0) {
            for (int i = 0; i < nameTagWrapper.getNameTags().size(); i++) {
                if (nameTagWrapper.getDeleted()[i]) {
                    nameTagQueue.removeFromQueue(nameTagWrapper.getNameTags().get(i).getId());
                } else {
                    nameTagQueue.updateNameTag(nameTagWrapper.getNameTags().get(i));
                }
            }
        }
        return "redirect:/ntap/manager";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String submitLogin(@RequestParam("password") String password) {
        if (password.equals(config.getPassword())) {
            config.setLoggedIn(true);
            return "redirect:/ntap/manager";
        } else {
            return "redirect:/login.jsp?failed=true";
        }
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
                    if (field.getName().equals("printer") ||
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

    private JSONObject getPrintServerStatus() {
        JSONObject json = new JSONObject();
        if (printService.isStopped() && !printService.isRunning()) {
            json.put("code", 0);
            json.put("status", "Stopped");
            return json;
        } else if (!printService.isStopped() && printService.isRunning()) {
            json.put("code", 1);
            json.put("status", "Running");
            return json;
        } else if (printService.isStopped()) {
            json.put("code", 2);
            json.put("status", "Stopped but still alive");
            return json;
        }
        json.put("code", 3);
        json.put("status", "Dead but not Stopped");
        return json;
    }

    @RequestMapping("/ps/status")
    @ResponseBody
    public String status() {
        return getPrintServerStatus().toJSONString();
    }

    @RequestMapping("/ps/start")
    @ResponseBody
    public String start() {
        JSONObject json = getPrintServerStatus();
        if (!printService.isRunning()) {
            json.put("action", "Starting");
            printService.start();
        }
        return json.toJSONString();
    }

    @RequestMapping("/ps/stop")
    @ResponseBody
    public String stop() {
        JSONObject json = getPrintServerStatus();
        if (!printService.isStopped() && printService.isRunning()) {
            json.put("action", "Stopping");
            printService.stop();
        }
        return json.toJSONString();
    }

    @RequestMapping(value = "/response", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> printerResponse(@RequestParam("printer") String printerIp) throws IOException {
        String message;
        HttpStatus status;
        System.out.println("Relieved printer DONE response for IP " + printerIp);
        Printer printer = printerQueue.getPrinterByIp(printerIp);
        if (printer != null) {
            NameTag nameTag = printer.getNameTag();
            boolean printerStatus = printer.isPrinting();
            message = String.format("Changed printing status of printer with IP %s form %b to %b", printerIp, printerStatus, printer.isPrinting());
            printer.setPrinting(false);
            status = HttpStatus.OK;
            if (nameTag != null) {
                message += "\nSending delete request to octoprint";
                new Thread(new Response(nameTag, printer)).start();
                nameTagQueue.removeFromQueue(printer.getNameTag());
            } else {
                message += "\nPrinter has no name tag";
            }
            printer.setNameTag(null);
        } else {
            message = "Printer not found";
            status = HttpStatus.BAD_REQUEST;
        }
        System.out.println(message);
        return new ResponseEntity<>(message, status);
    }

    @RequestMapping("/codeTest")
    @ResponseBody
    public ResponseEntity<String> returnCode(@RequestParam("code") Integer code) {
        switch (code) {
            case 201:
                return new ResponseEntity<>(HttpStatus.OK);
            case 400:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            case 404:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            case 500:
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            default:
                return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}