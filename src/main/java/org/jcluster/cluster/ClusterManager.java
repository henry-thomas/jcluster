/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcluster.bean.JcAppInstance;

/**
 *
 * @author henry
 *
 * Keeps of all connected apps. Also has the logic for sending to the correct
 * app.
 */
public final class ClusterManager {

    private static final Logger LOG = Logger.getLogger(ClusterManager.class.getName());

    private final Map<String, JcAppInstance> appMap = new HashMap<>(); //This map will be managed by Hazelcast
    private static final ClusterManager INSTANCE = new ClusterManager();
    private boolean running = false;
    private final int defaultPort = 4004;

    private ClusterManager() {
        init();
    }

    protected static ClusterManager getInstance() {
        return INSTANCE;
    }

    private synchronized void init() {
        if (!running) {
            LOG.info("JCLUSTER -- Startup...");
            running = true;
            ExecutorService exec = Executors.newSingleThreadExecutor();
            exec.submit(this::initMainThread);
        }
    }

    private void initMainThread() {
        try {
            Thread.currentThread().setName("ClusterManager-mainThread");

            //initialise other stuff
            JcAppInstance jcAppInstance = new JcAppInstance();
            jcAppInstance.setInstanceId("rAnDomId");
            jcAppInstance.setIpAddress(InetAddress.getLocalHost().getHostAddress());
            jcAppInstance.setIpPort(defaultPort);
            appMap.put(jcAppInstance.getInstanceId(), jcAppInstance);

            LOG.info("JCLUSTER -- Running...");
            while (running) {

            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClusterManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
