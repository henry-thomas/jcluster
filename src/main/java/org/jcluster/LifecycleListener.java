/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jcluster.cluster.JcFactory;

/**
 *
 * @author henry
 */
public class LifecycleListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(LifecycleListener.class.getName());
    private Future<?> submit;

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        int port = 4566;
        String ser = "SLV01234";

        JcFactory.initManager("lws", "192.168.100.18", port);
        JcFactory.getManager().addFilter("loggerSerial", ser);
        LOG.log(Level.INFO, "LifecycleListener: contextInitialized() PORT: {0} SER: {1}", new Object[]{port, ser});
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {

        JcFactory.getManager().destroy();
        LOG.info("LifecycleListener: contextDestroyed()");
    }

}
