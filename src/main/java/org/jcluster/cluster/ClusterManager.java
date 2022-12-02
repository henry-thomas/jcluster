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
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.NamingException;
import org.jcluster.ServiceLookup;
import org.jcluster.bean.JcAppCluster;
import org.jcluster.bean.JcAppDescriptor;
import org.jcluster.bean.JcAppInstanceData;
import org.jcluster.exception.cluster.JcClusterNotFoundException;
import org.jcluster.exception.cluster.JcFilterNotFoundException;
import org.jcluster.exception.cluster.JcInstanceNotFoundException;
import org.jcluster.cluster.hzUtils.HzController;
import org.jcluster.proxy.JcProxyMethod;
import org.jcluster.sockets.JcClientConnection;
import org.jcluster.sockets.JcServerEndpoint;

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
//    private final Map<String, JcAppInstanceZ> clientMap = new HashMap<>();
    private final JcAppDescriptor thisDescriptor = new JcAppDescriptor(); //representst this app instance, configured at bootstrap

    private static final ClusterManager INSTANCE = new ClusterManager();
    private boolean running = false;
    private boolean configDone = false;
    private String bindAddress;
    private JcServerEndpoint server;
    private ManagedExecutorService executorService = null;

    private ClusterManager() {
        HzController hzController = HzController.getInstance();
        appMap = hzController.getMap();
        try {
            executorService = (ManagedExecutorService) ServiceLookup.getService("concurrent/__defaultManagedExecutorService");
            LOG.info("executorService found");
        } catch (NamingException ex) {
            Logger.getLogger(ClusterManager.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            thisDescriptor.getFilterMap().put(filterName, new HashSet<>());
            filterSet = thisDescriptor.getFilterMap().get(filterName);
        }
        filterSet.add(value);
        //update distributed map
        appMap.put(thisDescriptor.getInstanceId(), thisDescriptor);
        LOG.log(Level.INFO, "Added filter: [{0}] with value: [{1}]", new Object[]{filterName, String.valueOf(value)});
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
            server = new JcServerEndpoint();
            executorService.submit(server);
//            Thread t = new Thread(server);
//            t.start();
            appMap.put(thisDescriptor.getInstanceId(), thisDescriptor);
//            exec.submit(this::initMainThread);
//            exec.submit(jcServer);
            running = true;
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
                clusterMap.put(desc.getAppName(), cluster);
            }

            if (cluster.getInstanceMap().containsKey(id)
                    || Objects.equals(desc.getInstanceId(), thisDescriptor.getInstanceId())
                    || Objects.equals(desc.getIpAddress() + desc.getIpPort(),
                            thisDescriptor.getIpAddress() + thisDescriptor.getIpPort())) {
                continue;
            }

            LOG.log(Level.INFO, "Connecting to app{0} at: {1}", new Object[]{desc.getAppName(), addr});

            //Creating an outbound connection as soon as a new member joins.
            //Outbound connections behave diffently when receiving messages
            JcClientConnection jcClientConnection = new JcClientConnection(desc);
            executorService.submit(jcClientConnection);
//            Thread t = new Thread(jcClientConnection);
//            t.start();

            JcAppInstanceData.getInstance().addOutboundConnection(jcClientConnection);
            cluster.addConnection(jcClientConnection);
            JcClientConnection putIfAbsent = cluster.getInstanceMap().putIfAbsent(id, jcClientConnection);
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

    public Object send(JcProxyMethod proxyMethod, Object[] args) throws JcFilterNotFoundException, JcInstanceNotFoundException {
        //Logic to send to correct app

        JcAppCluster cluster = clusterMap.get(proxyMethod.getAppName());
        if (cluster == null) {
            //ex   
            return new JcClusterNotFoundException("Cluster not found for " + proxyMethod.getAppName());
        }

        if (!proxyMethod.isInstanceFilter()) {
            return cluster.broadcast(proxyMethod, args);
        } else {

            Map<String, JcAppDescriptor> idDescMap = getIdDescMap(cluster);

            Map<String, Integer> paramNameIdxMap = proxyMethod.getParamNameIdxMap();

            String sendInstanceId = getSendInstance(idDescMap, paramNameIdxMap, args);

            if (sendInstanceId == null) {
                //ex
                throw new JcInstanceNotFoundException("Instance not found for " + proxyMethod.getMethodName());
            }

            return cluster.send(proxyMethod, args, sendInstanceId);
        }

    }

    private Map<String, JcAppDescriptor> getIdDescMap(JcAppCluster cluster) {
        Map<String, JcClientConnection> instanceMap = cluster.getInstanceMap();
        Map<String, JcAppDescriptor> idDescMap = new HashMap<>();

        for (Map.Entry<String, JcClientConnection> entry : instanceMap.entrySet()) {
            String instanceId = entry.getKey();

            JcAppDescriptor desc = appMap.get(instanceId);

            idDescMap.put(instanceId, desc);
        }
        return idDescMap;
    }

    private String getSendInstance(Map<String, JcAppDescriptor> idDescMap, Map<String, Integer> paramNameIdxMap, Object[] args) throws JcFilterNotFoundException {
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
//                        LOG.log(Level.INFO, "FOUND WHO HAS [{0}] {1}", new Object[]{filterName, args[idx]});
                        return descId;
                    }

                } else {
                    throw new JcFilterNotFoundException("Filter does not exist in map... filterName: [" + filterName + "] Argument: [" + args[idx] + "]");
                }

            }
        }
        return null;
    }

    public JcAppDescriptor getThisDescriptor() {
        return thisDescriptor;
    }

    @PreDestroy
    public void destroy() {
        LOG.info("JCLUSTER -- Stopping...");
        for (Map.Entry<String, JcAppCluster> entry : clusterMap.entrySet()) {
            String key = entry.getKey();
            JcAppCluster cluster = entry.getValue();
            cluster.destroy();
        }
        appMap.remove(thisDescriptor.getInstanceId());
        server.destroy();
        running = false;
        HzController.getInstance().destroy();
    }

    public ManagedExecutorService getExecutorService() {
        return executorService;
    }

}
