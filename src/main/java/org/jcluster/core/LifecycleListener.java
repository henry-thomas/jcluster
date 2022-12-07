/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.core;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jcluster.core.cluster.JcFactory;
import org.jcluster.core.config.JcAppConfig;

/**
 *
 * @author henry
 */
public class LifecycleListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(LifecycleListener.class.getName());
    private Future<?> submit;

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
//        Integer port = JcAppConfig.getINSTANCE().getPort();
//        String hostName = JcAppConfig.getINSTANCE().getHostName();
//        String appName = JcAppConfig.getINSTANCE().getAppName();
//
//        //Initialize J-Cluster for this app
//        JcFactory.initManager(appName, hostName, port);
//        LOG.log(Level.INFO, "LifecycleListener: contextInitialized() HOSTNAME: {1} PORT: {0} APPNAME: {1}", new Object[]{hostName, port, appName});
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        JcFactory.getManager().destroy();
        LOG.info("LifecycleListener: contextDestroyed()");
    }

}
