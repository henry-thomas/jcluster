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
import org.jcluster.bean.JcAppInstance;

/**
 *
 * @author henry
 */
public class HzController {

//    private String appId;
    private final IMap<String, JcAppInstance> map;
    private final Config hzConfig = new Config();
    private final HazelcastInstance hz;
    private static HzController INSTANCE = null;

    private HzController() {
        hzConfig.setClusterName("hz-jc-cluster");
        setDiscoveryConfig();

        hz = Hazelcast.newHazelcastInstance(hzConfig);
        
        map = hz.getMap("jc-app-map");
        
        map.addEntryListener(new ConnectionCallback(), true);
        LifecycleService lifeCycle = hz.getLifecycleService();
//        lifeCycle.addLifecycleListener(new HzLifeCycleListener());
        hz.addDistributedObjectListener(new HzDistrObjectListener());
    }
    
    private void setDiscoveryConfig(){
        JoinConfig join = new JoinConfig();
        DiscoveryConfig discoveryConfig = join.getDiscoveryConfig();
        hzConfig.getNetworkConfig().setJoin(join);
    }

    public static HzController getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new HzController();
        }

        return INSTANCE;
    }

    public IMap<String, JcAppInstance> getMap() {
        return map;
    }

    public void showConnected() {
        for (Map.Entry<String, JcAppInstance> entry : map.entrySet()) {
            String appId = entry.getKey();
            String appName = entry.getValue().getAppName();

            System.out.println(appId + " is online as type: " + appName);
        }
    }

}
