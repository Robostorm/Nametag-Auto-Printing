package org.robostorm.controller;

import org.json.simple.JSONArray;
import org.robostorm.config.Config;
import org.robostorm.model.NameTag;
import org.robostorm.queue.NameTagQueue;
import org.robostorm.queue.PrinterQueue;
import org.robostorm.service.PreviewService;
import org.robostorm.service.PrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
/*        if(!printService.isRunning()) {
            printService.start();
        }*/
    }

    @PreDestroy
    public void destroy() {
/*        if(printService.isRunning()) {
            printService.stop();
        }*/
    }

    @RequestMapping("/preview.json")
    @ResponseBody
    public ResponseEntity<String> preview(@RequestParam("name") String name, HttpServletRequest request) {
        String json;
        if ((json = previewService.preview(name, request.getSession().getServletContext().getRealPath("/"))) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/queue/add.json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> queueAdd(@RequestParam("name") String name) {
        if (name != null && !name.equals("")) {
            try {
                nameTagQueue.addToQueue(new NameTag(name));
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping("/queue/view.json")
    @ResponseBody
    public ResponseEntity<String> queueView() {
        JSONArray list = new JSONArray();
        for(NameTag nameTag : nameTagQueue.getAllNametags())
            list.add(nameTag.toString());
        return new ResponseEntity<>(list.toJSONString(), HttpStatus.OK);
    }

    @RequestMapping("/ps/start.json")
    @ResponseBody
    public String start() {
        if (printService.isRunning()) {
            return "Running";
        } else {
            printService.start();
            return "Staring...";
        }
    }

    @RequestMapping("/ps/stop.json")
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