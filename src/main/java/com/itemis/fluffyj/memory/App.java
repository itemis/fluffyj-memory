package com.itemis.fluffyj.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    
    public static void main(String[] args) {
        var msg = "Hello World!";
        System.out.println("Sysout: " + msg);
        LOG.info("Logger: " + msg);
    }
}
