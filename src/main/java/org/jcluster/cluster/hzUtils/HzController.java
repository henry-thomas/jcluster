/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster.hzUtils;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.map.IMap;
import org.jcluster.messages.Destination;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author henry
 */
public class HzController {

    private String appId;
    private final IMap<String, Destination> map;
    private final Config hzConfig = new Config();
    private final HazelcastInstance hz;
    private static HzController INSTANCE = null;

    private HzController() {
        hzConfig.setClusterName("hz-lws-cluster");
        JoinConfig join = new JoinConfig();
        DiscoveryConfig discoveryConfig = join.getDiscoveryConfig();
        
        hzConfig.getNetworkConfig().setJoin(join);

        hz = Hazelcast.newHazelcastInstance(hzConfig);
        map = hz.getMap("jc-app-map");
        LifecycleService lifeCycle = hz.getLifecycleService();
        lifeCycle.addLifecycleListener(new HzLifeCycleListener());
        hz.addDistributedObjectListener(new HzDistrObjectListener());
    }

    public static HzController getInstance() throws Exception {

        if (INSTANCE != null && INSTANCE.appId == null) {
            throw new Exception();
        }

        if (INSTANCE == null) {
            INSTANCE = new HzController();
        }

        return INSTANCE;
    }

    public static HzController init(String appId) {

        if (INSTANCE == null) {
            try {
                INSTANCE = getInstance();
                INSTANCE.setAppId(appId);
            } catch (Exception ex) {
                Logger.getLogger(HzController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return INSTANCE;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public IMap<String, Destination> getMap() {
        return map;
    }

    public void showConnected() {
        for (Map.Entry<String, Destination> entry : map.entrySet()) {
            String connectionId = entry.getKey();
            String serverId = entry.getValue().getServerId();

            System.out.println(connectionId + " is online on server: " + serverId);
        }
    }

    public static void put(Destination msg) {
        if (INSTANCE.appId != null) {
            INSTANCE.map.put(msg.getServerId(), msg);
        }
    }

    public static void remove(String id) {
        if (INSTANCE.appId != null) {
            INSTANCE.map.remove(id);
        }
    }

}
