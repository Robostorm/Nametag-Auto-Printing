package org.robostorm.service;

import org.robostorm.server.PrintServer;
import org.springframework.beans.factory.annotation.Autowired;

public class PrintServiceImpl implements PrintService {

    @Autowired
    private PrintServer printServer;

    @Override
    public void start() {
        new Thread(printServer).start();
    }

    @Override
    public void stop() {
        printServer.stop();
    }

    @Override
    public boolean isRunning() {
        //boolean t = printServer.getThread().isInterrupted();
        return printServer.getThread() != null && printServer.isRunning();
    }
}
