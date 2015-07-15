package org.robostorm.service;

import org.robostorm.model.NameTag;
import org.robostorm.model.Printer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface PrintService {

    void start();

    void stop();

    boolean isStopped();

    boolean isRunning();
}
