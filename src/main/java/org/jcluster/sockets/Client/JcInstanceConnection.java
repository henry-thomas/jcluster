/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.sockets.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcluster.bean.JcAppDescriptor;
import org.jcluster.cluster.ClusterManager;
import org.jcluster.cluster.JcFactory;
import org.jcluster.messages.ConnectionParam;
import org.jcluster.messages.JcMessage;
import org.jcluster.messages.JcMsgResponse;

/**
 *
 * @author henry
 */
public class JcInstanceConnection implements Runnable {

    private static final Logger LOG = Logger.getLogger(JcInstanceConnection.class.getName());

    private final ClusterManager manager = JcFactory.getManager();
    private boolean running = false;
    private final Socket sock = new Socket();
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final JcAppDescriptor desc;
    private final Map<Integer, JcMessage> reqRespMap = new HashMap<>();
    Thread rec = new Thread(this::readTask);

    public JcInstanceConnection(JcAppDescriptor desc) {
        this.desc = desc;
    }

    private boolean connect() {
//        JcAppDescriptor desc = manager.getThisDescriptor();
        if (!sock.isConnected()) {
            try {
                SocketAddress socketAddress = new InetSocketAddress(desc.getIpAddress(), desc.getIpPort());

                //try connect with timeout of 2000ms
                sock.connect(socketAddress, 2000);

                running = true;
                ois = new ObjectInputStream(sock.getInputStream());
                oos = new ObjectOutputStream(sock.getOutputStream());

                LOG.log(Level.INFO, "Connected to: {0}:{1}", new Object[]{desc.getIpAddress(), desc.getIpPort()});
                rec.setName(this.desc.getAppName() + "-ReceiveThread");
                rec.start();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not connect to: {0}:{1} Exception: {2}", new Object[]{desc.getIpAddress(), desc.getIpPort(), ex.getMessage()});
                running = false;
                return false;
            }
        }
        return true;
    }

    private boolean initHandshake() {
        try {
            Map<String, String> connInit = (HashMap<String, String>) ois.readObject();
            String init = connInit.get("connInit");
            if (init != null) {
                ConnectionParam connectionParam = new ConnectionParam(false, manager.getThisDescriptor().getInstanceId());
                oos.writeObject(connectionParam);
                return true;
            }
        } catch (IOException | ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, "Handshake failed! {0}", ex.getMessage());
        }
        return false;
    }

//    private void receive() {
//        Thread rec = new Thread(this::readTask);
//        rec.setName(this.desc.getAppName() + "-ReceiveThread");
//    }
    public JcMsgResponse send(JcMessage msg) {
        try {
//            MessageUtils.srlz(msg);
            oos.writeObject(msg);
            reqRespMap.put(msg.getRequestId(), msg);

            synchronized (msg) {
                msg.wait(2000);
            }

            if (msg.getResponse() == null) {
                throw new IOException("No response received, timeout");
            }

            return msg.getResponse();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(JcInstanceConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void run() {
        connect();
//        if (!initHandshake()) {
//            try {
//                sock.close();
//            } catch (IOException ex) {
//                Logger.getLogger(JcInstanceConnection.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    private void readTask() {
        while (running) {
            try {
                if (sock.isClosed()) {
                    return;
                }

                Object readObject = ois.readObject();
                if (readObject instanceof JcMessage) {

                    JcMessage response = (JcMessage) readObject;
//                            JcMsgResponse response = new JcMsgResponse(request.getRequestId(), readObject);
                    JcMessage request = reqRespMap.remove(response.getRequestId());
                    if (request != null) {
                        synchronized (request) {
//                            request.setResponse(response);
                            request.notifyAll();
                        }
                    }
                }
            } catch (IOException ex) {
                running = false;
                try {
                    Thread.sleep(500);
                    Logger.getLogger(JcInstanceConnection.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(JcInstanceConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JcInstanceConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void destroy() {
        try {
            sock.close();
            running = false;
            rec.interrupt();
        } catch (IOException ex) {
            Logger.getLogger(JcInstanceConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JcAppDescriptor getDesc() {
        return desc;
    }

}
