package org.robostorm.controller;

import org.robostorm.config.Config;
import org.robostorm.queue.NameTagQueue;
import org.robostorm.queue.PrinterQueue;
import org.robostorm.server.PrintServer;
import org.robostorm.service.PreviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

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
    private PrintServer printServer;
    @Autowired
    TaskExecutor taskExecutor;

    @PostConstruct
    public void init() {
        // Thread(printServer).start();
    }

    @PreDestroy
    public void destroy() {
        //printServer.stop();
    }

    @RequestMapping("/preview.json")
    @ResponseBody
    public ResponseEntity<String> preview(@RequestParam("name")String name,  HttpServletRequest request) {
        String json;
        if((json = previewService.preview(name, request.getSession().getServletContext().getRealPath("/"))) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }



    @RequestMapping("/ps/start.json")
    @ResponseBody
    public String start() {
        if(printServer != null && (!printServer.isStopped() || printServer.getThread().isAlive())) {
            return "Running";
        } else {
            //new Thread(printServer).start();
            //printServer.getThread().start();
            return "Staring...";
        }
    }

    @RequestMapping("/ps/stop.json")
    @ResponseBody
    public String stop() {
        if(printServer != null && (!printServer.isStopped() || printServer.getThread().isAlive())) {
            //printServer.stop();
            return "Stopping...";
        } else {
            return "Stopped";
        }
    }
}
