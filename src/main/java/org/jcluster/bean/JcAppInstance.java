/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author henry Created by JcManager for each instance
 */
public class JcAppInstance {
    //add whatever we need to represent our instances

    private String instanceId;
    private String ipAddress;
    private int ipPort;
    private final Map<String, HashSet<Object>> filterMap = new HashMap<>();

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getIpPort() {
        return ipPort;
    }

    public void setIpPort(int ipPort) {
        this.ipPort = ipPort;
    }

    public Map<String, HashSet<Object>> getFilterMap() {
        return filterMap;
    }

}
