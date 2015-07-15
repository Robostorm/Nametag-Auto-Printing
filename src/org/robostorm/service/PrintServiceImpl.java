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
    public boolean isStopped() {
        return printServer.isStopped();
    }

    @Override
    public boolean isRunning() {
        if(printServer.getThread() != null)
            return printServer.getThread().isAlive();
        else
            return false;
    }
}
