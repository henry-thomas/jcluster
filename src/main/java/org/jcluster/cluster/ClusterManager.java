/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster;

import com.hazelcast.map.IMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import org.jcluster.bean.JcAppInstance;
import org.jcluster.cluster.hzUtils.HzController;

/**
 *
 * @author henry
 *
 * Keeps record of all connected apps. Also has the logic for sending to the
 * correct app.
 */
public final class ClusterManager {

    private static final Logger LOG = Logger.getLogger(ClusterManager.class.getName());

    private IMap<String, JcAppInstance> appMap = null; //This map will be managed by Hazelcast
    private static final ClusterManager INSTANCE = new ClusterManager();
    private boolean running = false;
    private boolean configDone = false;
    private Future<?> submit;
    ExecutorService exec = Executors.newSingleThreadExecutor();
    private final JcAppInstance jcAppInstance = new JcAppInstance();

    private ClusterManager() {
        HzController hzController = HzController.getInstance();
        appMap = hzController.getMap();
    }

    protected static ClusterManager getInstance() {
        return INSTANCE;
    }

    protected ClusterManager initConfig(String appName, String ipAddress, Integer port) {
        if (!running) {
            jcAppInstance.setAppName(appName);
            jcAppInstance.setIpPort(port);
            jcAppInstance.setIpAddress(ipAddress);
            configDone = true;
            init();
        } else {
            LOG.log(Level.WARNING, "Cannot set JC App instance config, already running! instance ID: {0}", jcAppInstance.getInstanceId());
        }
        return INSTANCE;
    }

    private synchronized void init() {
        if (!running) {
            if (!configDone) {
                LOG.warning("JCLUSTER -- App instance is not configured, using default settings. Consider calling JcFactory.initManager(appName, ipAddress, port)");
            }
            LOG.info("JCLUSTER -- Startup...");

            submit = exec.submit(this::initMainThread);
            running = true;
        }
    }

    private void initMainThread() {
        Thread.currentThread().setName("ClusterManager-mainThread");

        //add this instance to the shared map
        appMap.put(jcAppInstance.getInstanceId(), jcAppInstance);

        LOG.info("JCLUSTER -- Running...");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClusterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        LOG.info("JCLUSTER -- Stopping...");
        submit.cancel(true);
        exec.shutdownNow();
        appMap.remove(jcAppInstance.getInstanceId());
        running = false;
    }

}
