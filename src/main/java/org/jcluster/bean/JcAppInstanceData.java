/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jcluster.sockets.JcClientConnection;

/**
 *
 * @author henry
 */
public class JcAppInstanceData implements Serializable {

    private final String serialVersionUID = "-1455291844074901991";

    private JcAppDescriptor desc;
    private final List<JcClientConnection> ouboundConnections = new ArrayList<>();
    private final List<JcClientConnection> inboundConnections = new ArrayList<>();
    private static JcAppInstanceData INSTANCE;

    public static JcAppInstanceData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JcAppInstanceData();
        }
        return INSTANCE;
    }

    private JcAppInstanceData() {
//        this.desc = desc;
    }

    public void addOutboundConnection(JcClientConnection conn) {
        ouboundConnections.add(conn);
    }

    public void addInboundConnection(JcClientConnection conn) {
        inboundConnections.add(conn);
    }

    public JcAppDescriptor getDesc() {
        return desc;
    }

    public List<JcClientConnection> getOuboundConnections() {
        return ouboundConnections;
    }

    public List<JcClientConnection> getInboundConnections() {
        return inboundConnections;
    }

}
