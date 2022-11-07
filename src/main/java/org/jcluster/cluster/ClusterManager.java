/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster;

import com.hazelcast.map.IMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import org.jcluster.bean.JcAppInstance;
import org.jcluster.cluster.hzUtils.HzController;
import org.jcluster.messages.JcMessage;
import org.jcluster.proxy.JcProxyMethod;
import org.jcluster.sockets.JcClient;
import org.jcluster.sockets.JcServer;

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
    private Map<String, JcClient> clientMap = new HashMap<>();
    ExecutorService exec;
    private final JcAppInstance jcAppInstance = new JcAppInstance();

    private ClusterManager() {
        exec = Executors.newFixedThreadPool(5);
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

            exec.submit(this::initMainThread);

            String bindAddress = "tcp://" + jcAppInstance.getIpAddress() + ":" + jcAppInstance.getIpPort();
            exec.submit(new JcServer(bindAddress));
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

    public void onNewMemberJoin(JcAppInstance instance) {
        String bindAddress = "tcp://" + instance.getIpAddress() + ":" + instance.getIpPort();
        JcClient jcClient = new JcClient(bindAddress);
        JcClient putIfAbsent = clientMap.putIfAbsent(instance.getInstanceId(), jcClient);

        if (putIfAbsent == null) {
            try {
                exec.submit(jcClient).get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ClusterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        LOG.info(jcClient.printDescription());
    }

    public void onMemberLeave(JcAppInstance instance) {
        JcClient remove = null;
        if (instance != null && clientMap.containsKey(instance.getInstanceId())) {
            clientMap.get(instance.getInstanceId()).destroy();
            remove = clientMap.remove(instance.getInstanceId());
        }

        if (remove == null) {
            LOG.log(Level.WARNING, "was never in the cluster!");
        } else {
            LOG.log(Level.INFO, "{0} was removed from the cluster", remove.printDescription());
        }

    }

    public Object send(JcMessage message, JcProxyMethod method) {
        //Logic to send to correct app
        return new Object();
    }

    @PreDestroy
    public void destroy() {
        LOG.info("JCLUSTER -- Stopping...");
//        exec.shutdownNow();
        appMap.remove(jcAppInstance.getInstanceId());

        running = false;
    }

}
