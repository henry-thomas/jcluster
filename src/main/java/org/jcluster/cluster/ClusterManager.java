/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster;

import com.hazelcast.map.IMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import org.jcluster.bean.JcAppCluster;
import org.jcluster.bean.JcAppDescriptor;
import org.jcluster.cluster.hzUtils.HzController;
import org.jcluster.messages.JcMessage;
import org.jcluster.proxy.JcProxyMethod;
import org.jcluster.sockets.JcAppInstance;
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

    private IMap<String, JcAppDescriptor> appMap = null; //This map will be managed by Hazelcast

    private final Map<String, JcAppCluster> clusterMap = new HashMap<>();
//    private final Map<String, JcAppInstance> clientMap = new HashMap<>();
    private final JcAppDescriptor thisDescriptor = new JcAppDescriptor(); //representst this app instance, configured at bootstrap

    private static final ClusterManager INSTANCE = new ClusterManager();
    private boolean running = false;
    private boolean configDone = false;
    private final ExecutorService exec;
    private String bindAddress;
    private JcServer server;

    private ClusterManager() {
        exec = Executors.newFixedThreadPool(5);
        HzController hzController = HzController.getInstance();
        appMap = hzController.getMap();
    }

    protected static ClusterManager getInstance() {
        return INSTANCE;
    }

    public Map<String, HashSet<Object>> getFilterMap() {
        return thisDescriptor.getFilterMap();
    }

    public void addFilter(String filterName, Object value) {
        HashSet<Object> filterSet = thisDescriptor.getFilterMap().get(filterName);
        if (filterSet == null) {
            filterSet =thisDescriptor.getFilterMap().put(filterName, new HashSet<>());
        }
        filterSet.add(value);
        //update distributed map
        appMap.put(thisDescriptor.getInstanceId(), thisDescriptor);
    }

    protected ClusterManager initConfig(String appName, String ipAddress, Integer port) {
        if (!running) {
            thisDescriptor.setAppName(appName);
            thisDescriptor.setIpPort(port);
            thisDescriptor.setIpAddress(ipAddress);
            configDone = true;
            init();
        } else {
            LOG.log(Level.WARNING, "Cannot set JC App instance config, already running! instance ID: {0}", thisDescriptor.getInstanceId());
        }
        return INSTANCE;
    }

    private synchronized void init() {
        if (!running) {
            if (!configDone) {
                LOG.warning("JCLUSTER -- App instance is not configured, using default settings. Consider calling JcFactory.initManager(appName, ipAddress, port)");
            }
            LOG.info("JCLUSTER -- Startup...");
            bindAddress = "tcp://" + thisDescriptor.getIpAddress() + ":" + thisDescriptor.getIpPort();
            server = new JcServer(bindAddress);
            Thread t = new Thread(server);
            t.start();
            exec.submit(this::initMainThread);
//            exec.submit(jcServer);
            running = true;
        }
    }

    private void initMainThread() {
        Thread.currentThread().setName("JCLUSTER--ClusterManager-MainThread");

        //add this instance to the shared map
        appMap.put(thisDescriptor.getInstanceId(), thisDescriptor);

        LOG.info("JCLUSTER -- Running...");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClusterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void onNewMemberJoin(JcAppDescriptor i) {

        for (Map.Entry<String, JcAppDescriptor> entry : appMap.entrySet()) {

            String id = entry.getKey();
            JcAppDescriptor desc = entry.getValue();

            String addr = "tcp://" + desc.getIpAddress() + ":" + desc.getIpPort();

            JcAppCluster cluster = clusterMap.get(desc.getAppName());
            if (cluster == null) {
                cluster = new JcAppCluster(desc.getAppName());
                clusterMap.put(desc.getInstanceId(), cluster);
            }

            if (cluster.getInstanceMap().containsKey(id) || Objects.equals(desc.getInstanceId(), thisDescriptor.getInstanceId())) {
                continue;
            }

            LOG.log(Level.INFO, "Connecting to someone at: {0}", addr);

            JcAppInstance jcClient = new JcAppInstance(addr);

            JcAppInstance putIfAbsent = cluster.getInstanceMap().putIfAbsent(id, jcClient);

            if (putIfAbsent == null) {
                //Automatically connecting to the instance
                Thread t = new Thread(jcClient);
                t.start();
            }

            LOG.info(jcClient.printDescription());
        }

    }

    public void onMemberLeave(JcAppDescriptor instance) {

        if (instance != null) {
            JcAppCluster cluster = clusterMap.get(instance.getAppName());

            if (cluster != null) {

                if (!cluster.removeConnection(instance)) {
                    LOG.log(Level.WARNING, "AppInstance not in cluster!: {0}", instance.getInstanceId());
                    return;
                }

                clusterMap.remove(instance.getInstanceId());

            } else {
                LOG.log(Level.WARNING, "Tried to remove cluster that does not exist!");
            }

        }
    }

    public Object send(JcProxyMethod proxyMethod, Object[] args) {
        //Logic to send to correct app

        JcAppCluster cluster = clusterMap.get(proxyMethod.getAppName());
        if (cluster == null) {
            //ex   
            return null;
        }

        if (!proxyMethod.isInstanceFilter()) {
            cluster.broadcast(proxyMethod, args);
        } else {

            Map<String, JcAppDescriptor> idDescMap = getIdDescMap(cluster);

            Map<String, Integer> paramNameIdxMap = proxyMethod.getParamNameIdxMap();

            String sendInstanceId = getSendInstance(idDescMap, paramNameIdxMap, args);

            if (sendInstanceId == null) {
                //ex
                return null;
            }

            return cluster.send(proxyMethod, args, sendInstanceId);
        }
        return null;

    }

    private Map<String, JcAppDescriptor> getIdDescMap(JcAppCluster cluster) {
        Map<String, JcAppInstance> instanceMap = cluster.getInstanceMap();
        Map<String, JcAppDescriptor> idDescMap = new HashMap<>();

        for (Map.Entry<String, JcAppInstance> entry : instanceMap.entrySet()) {
            String instanceId = entry.getKey();

            JcAppDescriptor desc = appMap.get(instanceId);

            idDescMap.put(instanceId, desc);
        }
        return idDescMap;
    }

    ;

    private String getSendInstance(Map<String, JcAppDescriptor> idDescMap, Map<String, Integer> paramNameIdxMap, Object[] args) {
        for (Map.Entry<String, JcAppDescriptor> entry : idDescMap.entrySet()) {
            String descId = entry.getKey();
            JcAppDescriptor desc = entry.getValue();

            //Checking parameters of instanceDesc with args of method
            for (Map.Entry<String, Integer> entry1 : paramNameIdxMap.entrySet()) {
                String filterName = entry1.getKey();
                Integer idx = entry1.getValue();

                HashSet<Object> filterSet = desc.getFilterMap().get(filterName);
                if (filterSet != null) {

                    if (filterSet.contains(args[idx])) {
                        LOG.log(Level.INFO, "FOUND WHO HAS [{0}] {1}", new Object[]{filterName, args[idx]});
                        return descId;
                    }

                } else {
                    LOG.warning("Filter does not exist in map");
                }

            }
        }
        return null;
    }

    @PreDestroy
    public void destroy() {
        LOG.info("JCLUSTER -- Stopping...");
        exec.shutdownNow();
        server.destroy();
//        for (Map.Entry<String, JcAppInstance> entry : clusterMap.entrySet()) {
//            String key = entry.getKey();
//            JcAppInstance client = entry.getValue();
//            client.destroy();
//        }
        appMap.remove(thisDescriptor.getInstanceId());

        running = false;
    }

}
