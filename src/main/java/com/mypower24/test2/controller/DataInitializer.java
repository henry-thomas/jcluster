/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2.controller;

import com.mypower24.test2.controller.entity.Dummy;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import org.jcluster.cluster.JcFactory;

/**
 *
 * @author henry
 */
@Singleton
@Startup
public class DataInitializer {

    private static final Logger LOG = Logger.getLogger(DataInitializer.class.getName());

    private final HashMap<String, Dummy> dataMap = new HashMap<>();

    @PostConstruct
    public void init() {
        
        int port = 4567;
        String ser = "SLV012345";

        JcFactory.initManager("lws", "192.168.100.18", port);
        
        JcFactory.getManager().addFilter("loggerSerial", ser);
        LOG.log(Level.INFO, "LifecycleListener: contextInitialized() PORT: {0} SER: {1}", new Object[]{port, ser});

        //Adding some dummy data
        dataMap.put("1", new Dummy("1", "nobody"));
        dataMap.put("12", new Dummy("12", "somebody"));
        dataMap.put("123", new Dummy("1234", "most people"));
        dataMap.put("1234", new Dummy("1234", "everybody"));

        for (Map.Entry<String, Dummy> entry : dataMap.entrySet()) {
            String key = entry.getKey();
//            Dummy val = entry.getValue();

            JcFactory.getManager().addFilter("name", key);
            LOG.log(Level.INFO, "DataController init() Added: {0}", key);
        }
    }
}
