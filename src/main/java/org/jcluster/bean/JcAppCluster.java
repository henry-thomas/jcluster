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
import org.jcluster.sockets.Client.JcInstanceConnection;

/**
 *
 * @author henry
 */
public class JcAppCluster {

    private static final Logger LOG = Logger.getLogger(JcAppCluster.class.getName());

    private final String jcAppName;
    private final Map<String, JcInstanceConnection> instanceMap = new HashMap<>(); //connections for this app
//    private final Map<Integer, JcMessage> jcMsgMap = new ConcurrentHashMap<>();

    public JcAppCluster(String jcAppName) {
        this.jcAppName = jcAppName;
    }

    public Object send(JcProxyMethod proxyMethod, Object[] args, String sendInstanceId) {
        JcMessage msg = new JcMessage(proxyMethod.getMethodName(), proxyMethod.getClassName(), args);
//        return null;
        return instanceMap.get(sendInstanceId).send(msg);
    }

    public boolean removeConnection(JcAppDescriptor instance) {

        JcInstanceConnection instanceConnection = instanceMap.get(instance.getInstanceId());
        if (instanceConnection != null) {
            instanceConnection.destroy();
        }

        JcInstanceConnection remove = instanceMap.remove(instance.getInstanceId());
        if (remove == null) {
            return false;
        }

        return true;
    }

    public boolean broadcast(JcProxyMethod proxyMethod, Object[] args) {
        //if broadcast to 0 instances, fail. Otherwise return true
        for (Map.Entry<String, JcInstanceConnection> entry : instanceMap.entrySet()) {
            String id = entry.getKey();
            JcInstanceConnection instance = entry.getValue();

            JcMessage msg = new JcMessage(proxyMethod.getMethodName(), proxyMethod.getClassName(), args);
//            instance.send(msg);
        }
        return true;
    }

    public void addConnection(JcInstanceConnection conn) {
        instanceMap.put(conn.getDesc().getInstanceId(), conn);
    }

    public String getJcAppName() {
        return jcAppName;
    }

    public Map<String, JcInstanceConnection> getInstanceMap() {
        return instanceMap;
    }

    public void destroy() {
        for (Map.Entry<String, JcInstanceConnection> entry : instanceMap.entrySet()) {
            String key = entry.getKey();
            JcInstanceConnection conn = entry.getValue();
            conn.destroy();
        }
    }

}
