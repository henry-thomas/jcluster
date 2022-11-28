/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jcluster.sockets.Client.JcInstanceConnection;

/**
 *
 * @author henry
 */
public class JcAppInstance implements Serializable {

    private final String serialVersionUID = "-1455291844074901991";
    
    private JcAppDescriptor desc;
    private final List<JcInstanceConnection> connList = new ArrayList<>();
    private static JcAppInstance INSTANCE;

    public static JcAppInstance getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JcAppInstance();
        }
        return INSTANCE;
    }
    

    private JcAppInstance() {
//        this.desc = desc;
//        JcInstanceConnection jcInstanceConnection = new JcInstanceConnection(desc);
//        addConnection(jcInstanceConnection);
    }

    public void addConnection(JcInstanceConnection conn) {
        connList.add(conn);
    }

    public JcAppDescriptor getDesc() {
        return desc;
    }

    public List<JcInstanceConnection> getConnList() {
        return connList;
    }

}
