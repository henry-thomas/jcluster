/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.jcluster.messages.JcMessage;
import org.jcluster.proxy.JcProxyMethod;
import org.jcluster.sockets.JcAppInstance;

/**
 *
 * @author henry
 */
public class JcAppCluster {

    private static final Logger LOG = Logger.getLogger(JcAppCluster.class.getName());

    private final String jcAppName;
    private final Map<String, JcAppInstance> instanceMap = new HashMap<>(); //connections for this app
    private final Map<Integer, JcMessage> jcMsgMap = new ConcurrentHashMap<>();

    public JcAppCluster(String jcAppName) {
        this.jcAppName = jcAppName;
    }

    public Object send(JcProxyMethod proxyMethod, Object[] args, String sendInstanceId) {
        JcMessage msg = new JcMessage(proxyMethod.getMethodName(), proxyMethod.getClassName(), args);
        return instanceMap.get(sendInstanceId).send(msg);
    }

    public boolean removeConnection(JcAppDescriptor instance) {

        JcAppInstance instanceConnection = instanceMap.get(instance.getInstanceId());
        instanceConnection.destroy();

        JcAppInstance remove = instanceMap.remove(instance.getInstanceId());
        if (remove == null) {
            return false;
        }

        return true;
    }

    public boolean broadcast(JcProxyMethod proxyMethod, Object[] args) {
        //if broadcast to 0 instances, fail. Otherwise return true
        for (Map.Entry<String, JcAppInstance> entry : instanceMap.entrySet()) {
            String id = entry.getKey();
            JcAppInstance instance = entry.getValue();

            JcMessage msg = new JcMessage(proxyMethod.getMethodName(), proxyMethod.getClassName(), args);
            instance.send(msg);
        }
        return true;
    }

    public String getJcAppName() {
        return jcAppName;
    }

    public Map<String, JcAppInstance> getInstanceMap() {
        return instanceMap;
    }

}
