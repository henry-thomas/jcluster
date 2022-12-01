/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2.controller;

import com.mypower24.test2.controller.entity.Dummy;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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

        //These properties should be stored in your server configuration.
        //E.G. in Payara, in DAC, in server-config -> System Properties
        //Add them there.
        //If someone can think of a better way, please mention.
        Integer port = Integer.valueOf(System.getProperty("JC_PORT"));
        String hostName = System.getProperty("JC_HOSTNAME");
        String appName = System.getProperty("JC_APP_NAME");


        //For Testing, add values to filter here
        String ser;
        if (port == 4566) {
            ser = "SLV012345";
        } else {
            ser = "SLV01234";
        }
        //Initialize J-Cluster for this app
        JcFactory.initManager(appName, hostName, port);
        LOG.log(Level.INFO, "DataInitializer: contextInitialized() PORT: {0} SER: {1}", new Object[]{port, ser});

        JcFactory.getManager().addFilter("loggerSerial", ser);

        //Adding some dummy data
        dataMap.put("1", new Dummy("1", "nobody"));
        dataMap.put("12", new Dummy("12", "somebody"));
        dataMap.put("123", new Dummy("1234", "most people"));
        dataMap.put("1234", new Dummy("1234", "everybody"));

        for (Map.Entry<String, Dummy> entry : dataMap.entrySet()) {
            String key = entry.getKey();

            JcFactory.getManager().addFilter("name", key);
            LOG.log(Level.INFO, "DataController init() Added: {0}", key);
        }
    }
}
