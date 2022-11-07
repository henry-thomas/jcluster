/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster;

import org.jcluster.sockets.JcServer;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jcluster.cluster.ClusterManager;
import org.jcluster.cluster.JcFactory;

/**
 *
 * @author henry
 */
public class LifecycleListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(LifecycleListener.class.getName());
    private Future<?> submit;

    @Inject
    JcServer server;

//    public void init() {
//        try {
//            LOG.info("JcBootstrap init()");
//            InitialContext ctx = new InitialContext();
//
//            ClassLoader classLoader = IBusinessMethod.class.getClassLoader();
//            Object newProxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{IBusinessMethod.class}, new JcRemoteExecutionHandler());
//            String composeName = ctx.composeName("randomString", "prefix");
//            LOG.log(Level.INFO, "ComposeName: {0}", composeName);
//            
//            ctx.bind(composeName, newProxyInstance);
////            ctxService.createContextualProxy(newProxyInstance, IBusinessMethod.class);
//        } catch (NamingException ex) {
//            Logger.getLogger(LifecycleListener.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
//        init();
//        submit = Executors.newSingleThreadExecutor().submit(server);
        //        server.start();
        LOG.info("LifecycleListener: contextInitialized()");
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
//        submit.cancel(true);
        JcFactory.getManager().destroy();
        LOG.info("LifecycleListener: contextDestroyed()");
    }

}
