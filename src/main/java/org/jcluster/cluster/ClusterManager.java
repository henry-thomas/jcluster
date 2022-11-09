/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster;

import com.hazelcast.map.IMap;
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
    private final Map<String, JcClient> clientMap = new HashMap<>();
    private final ExecutorService exec;
    private final JcAppInstance jcAppInstance = new JcAppInstance();
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
        return jcAppInstance.getFilterMap();
    }

    public void addFilter(String filterName, Object value) {
        if (!jcAppInstance.getFilterMap().containsKey(filterName)) {
            jcAppInstance.getFilterMap().put(filterName, new HashSet<>());
        }
        jcAppInstance.getFilterMap().get(filterName).add(value);
        appMap.put(jcAppInstance.getInstanceId(), jcAppInstance);
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
            bindAddress = "tcp://" + jcAppInstance.getIpAddress() + ":" + jcAppInstance.getIpPort();
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
        appMap.put(jcAppInstance.getInstanceId(), jcAppInstance);

        LOG.info("JCLUSTER -- Running...");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClusterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void onNewMemberJoin(JcAppInstance i) {

        for (Map.Entry<String, JcAppInstance> entry : appMap.entrySet()) {

            String id = entry.getKey();
            JcAppInstance instance = entry.getValue();

            String addr = "tcp://" + instance.getIpAddress() + ":" + instance.getIpPort();

            if (clientMap.containsKey(id) || Objects.equals(instance.getInstanceId(), jcAppInstance.getInstanceId())) {
                continue;
            }

            LOG.log(Level.INFO, "Connected to someone at: {0}", addr);

            JcClient jcClient = new JcClient(addr);
            JcClient putIfAbsent = clientMap.putIfAbsent(instance.getInstanceId(), jcClient);

            if (putIfAbsent == null) {
                //Automatically connecting to the instance
                Thread t = new Thread(jcClient);
                t.start();
            }

            LOG.info(jcClient.printDescription());
        }

    }

    public void onMemberLeave(JcAppInstance instance) {
        JcClient remove = null;
        if (instance != null && clientMap.containsKey(instance.getInstanceId())) {
            clientMap.get(instance.getInstanceId()).destroy();
            remove = clientMap.remove(instance.getInstanceId());
        }

    }

    private boolean broadcast(JcMessage message) {
        for (Map.Entry<String, JcClient> entry : clientMap.entrySet()) {
//            String key = entry.getKey();
            JcClient client = entry.getValue();
            client.send(message);
        }
        return true;
    }

    public Object send(JcMessage message, JcProxyMethod proxyMethod, Object[] args) {
        //Logic to send to correct app

        String appName = proxyMethod.getAppName();
        Map<String, JcAppInstance> filterByAppNameMap = ClusterUtils.getInstance().filterByAppNameMap(appName);

        for (Map.Entry<String, JcAppInstance> entry : filterByAppNameMap.entrySet()) {
            String id = entry.getKey();
            JcAppInstance instance = entry.getValue();

            if (Objects.equals(instance.getInstanceId(), jcAppInstance.getInstanceId())) {
                continue;
            }

            if (!proxyMethod.isInstanceFilter()) {
                //send all
                broadcast(message);
            } else {
                //send to specific with filter
                //we need do use the instance filter and appName to figure out
                //how to get the JcAppInstance
                Map<String, Integer> paramNameIdxMap = proxyMethod.getParamNameIdxMap();
                for (Map.Entry<String, Integer> entry1 : paramNameIdxMap.entrySet()) {
                    String filterName = entry1.getKey();
                    Integer idx = entry1.getValue();

                    if (instance.getFilterMap().containsKey(filterName)) {
                        HashSet<Object> filterSet = instance.getFilterMap().get(filterName);

                        if (filterSet.contains(args[idx])) {
                            //send to this app
                            LOG.log(Level.INFO, "FOUND WHO HAS [{0}] {1}", new Object[]{filterName, args[idx]});
                            JcMessage send = clientMap.get(instance.getInstanceId()).send(message);
                            return send.getResponse().getData();
                        }

                    } else {
                        LOG.warning("Filter does not exist in map");
                    }

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
        for (Map.Entry<String, JcClient> entry : clientMap.entrySet()) {
            String key = entry.getKey();
            JcClient client = entry.getValue();
            client.destroy();
        }
        appMap.remove(jcAppInstance.getInstanceId());

        running = false;
    }

}
