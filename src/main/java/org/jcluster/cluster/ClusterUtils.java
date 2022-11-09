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
import org.jcluster.bean.JcAppCluster;
import org.jcluster.bean.JcAppDescriptor;
import org.jcluster.cluster.hzUtils.HzController;

/**
 *
 * @author henry
 */
public class ClusterUtils {

    private final IMap<String, JcAppDescriptor> appMap;
    private static final ClusterUtils INSTANCE = new ClusterUtils();

    private ClusterUtils() {
        appMap = HzController.getInstance().getMap();
    }

    public static ClusterUtils getInstance() {
        return INSTANCE;
    }

    public List<JcAppDescriptor> filterByAppNameList(String filter) {
        List<JcAppDescriptor> resultList = new ArrayList<>();

        for (Map.Entry<String, JcAppDescriptor> entry : appMap.entrySet()) {
            JcAppDescriptor instance = entry.getValue();

            if (instance.getAppName().equals(filter)) {
                resultList.add(instance);
            }

        }

        return resultList;
    }

//    public Map<String, JcAppCluster> filterByAppNameMap(String filter) {
//        Map<String, JcAppCluster> resultMap = new HashMap<>();
//
//        for (Map.Entry<String, JcAppCluster> entry : appMap.entrySet()) {
//            JcAppCluster instance = entry.getValue();
//
//            if (instance.getJcAppName().equals(filter)) {
//                resultMap.put(instance.getInstanceId(), instance);
//            }
//
//        }
//
//        return resultMap;
//    }
    
//    public JcAppDescriptor getInstanceFromFilter(String filter){
//        
//    }

}
