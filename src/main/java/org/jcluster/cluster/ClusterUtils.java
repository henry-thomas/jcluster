/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster;

import com.hazelcast.map.IMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jcluster.bean.JcAppInstance;
import org.jcluster.cluster.hzUtils.HzController;

/**
 *
 * @author henry
 */
public class ClusterUtils {

    private final IMap<String, JcAppInstance> appMap;
    private final ClusterUtils INSTANCE = new ClusterUtils();

    private ClusterUtils() {
        appMap = HzController.getInstance().getMap();
    }

    public ClusterUtils getInstance() {
        return INSTANCE;
    }

    public List<JcAppInstance> filterByAppNameList(String filter) {
        List<JcAppInstance> resultList = new ArrayList<>();

        for (Map.Entry<String, JcAppInstance> entry : appMap.entrySet()) {
            JcAppInstance instance = entry.getValue();

            if (instance.getAppName().equals(filter)) {
                resultList.add(instance);
            }

        }

        return resultList;
    }

    public Map<String, JcAppInstance> filterByAppNameMap(String filter) {
        Map<String, JcAppInstance> resultMap = new HashMap<>();

        for (Map.Entry<String, JcAppInstance> entry : appMap.entrySet()) {
            JcAppInstance instance = entry.getValue();

            if (instance.getAppName().equals(filter)) {
                resultMap.put(instance.getInstanceId(), instance);
            }

        }

        return resultMap;
    }

}
