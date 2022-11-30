/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcluster.bean.JcAppInstanceData;
import org.jcluster.cluster.ClusterManager;
import org.jcluster.cluster.JcFactory;

/**
 *
 * @author henry
 */
public class JcServerEndpoint implements Runnable {

    private final ClusterManager manager = JcFactory.getManager();
    private final Map<String, JcClientConnection> connMap = new HashMap<>();
    private boolean running;
    ServerSocket server;

    @Override
    public void run() {
        try {
            server = new ServerSocket(manager.getThisDescriptor().getIpPort());
            server.setReuseAddress(true);
            running = true;
            while (running) {
                Socket sock = server.accept();

                JcClientConnection jcClientConnection = new JcClientConnection(sock);
                connMap.put(sock.getInetAddress().getHostAddress() + "-" + sock.getPort(), jcClientConnection);
                JcAppInstanceData.getInstance().addInboundConnection(jcClientConnection);

                Thread cThread = new Thread(jcClientConnection);
                cThread.start();
            }

            server.close();
        } catch (IOException ex) {
            Logger.getLogger(JcServerEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy() {
        try {
            for (Map.Entry<String, JcClientConnection> entry : connMap.entrySet()) {
                JcClientConnection conn = entry.getValue();
                conn.destroy();
            }
            JcAppInstanceData.getInstance().getInboundConnections().clear();
            connMap.clear();
            running = false;
            if (server != null) {
                server.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(JcServerEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}