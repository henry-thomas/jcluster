/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.jcluster.ServiceLookup;
import org.jcluster.annotation.JcCommand;
import org.jcluster.bean.JcAppDescriptor;
import org.jcluster.cluster.ClusterManager;
import org.jcluster.cluster.JcFactory;
import org.jcluster.cluster.hzUtils.HzController;
import org.jcluster.messages.ConnectionParam;
import org.jcluster.messages.JcMessage;
import org.jcluster.messages.JcMsgResponse;
import org.jcluster.sockets.exception.JcResponseTimeoutException;

/**
 *
 * @author henry
 */
public class JcClientConnection implements Runnable {

    private static final Logger LOG = Logger.getLogger(JcClientConnection.class.getName());
    private final ClusterManager manager = JcFactory.getManager();

    private final JcAppDescriptor desc;
    private String connId;
    private final int port;
    private final String hostName;
    private final Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private static int parallelConnectionCount = 0;
    private int paralConnWaterMark = 0;
    private boolean secure;
    private boolean running;
    private final ConnectionType connType;
    private int txCount = 0;
    private int rxCount = 0;
    private int errCount = 0;
    private int timeoutCount = 0;

    private final Map<Integer, JcMessage> reqRespMap = new HashMap<>();

    public JcClientConnection(Socket sock) {
        this.desc = JcFactory.getManager().getThisDescriptor();
        this.connType = ConnectionType.INBOUND;
        this.socket = sock;
        this.port = sock.getLocalPort();
        this.hostName = sock.getInetAddress().getHostAddress();

        parallelConnectionCount++;
        if (parallelConnectionCount > paralConnWaterMark) {
            paralConnWaterMark = parallelConnectionCount;
            System.out.println("Number of connections reached: " + paralConnWaterMark);
        }
    }

    public JcClientConnection(JcAppDescriptor desc) {
        this.desc = desc;
        this.socket = new Socket();
        this.connType = ConnectionType.OUTBOUND;
        this.port = this.desc.getIpPort();
        this.hostName = this.desc.getIpAddress();
        parallelConnectionCount++;
        if (parallelConnectionCount > paralConnWaterMark) {
            paralConnWaterMark = parallelConnectionCount;
            System.out.println("Number of connections reached: " + paralConnWaterMark);
        }
    }

    private boolean connect() throws IOException {
        if (!socket.isConnected() || socket.isClosed()) {
            try {
                SocketAddress socketAddress = new InetSocketAddress(this.hostName, this.port);
                //try connect with timeout of 2000ms
                socket.connect(socketAddress, 2000);

                LOG.log(Level.INFO, "Connected to: {0}:{1}", new Object[]{this.hostName, this.port});
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
            Map<String, String> connInitMsg = new HashMap<>();

            connInitMsg.put("connInit", hostName);
            oos.writeObject(connInitMsg);

            ConnectionParam connParam = (ConnectionParam) ois.readObject();

            if (HzController.getInstance().getMap().containsKey(connParam.getAppId())) {
                ConnectionParam connectionParam = new ConnectionParam(secure, manager.getThisDescriptor().getInstanceId());
                oos.writeObject(connectionParam);
                return true;
            } else {
                return false;
            }

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //Called from Client Thread
    public JcMsgResponse send(JcMessage msg) {
        return send(msg, 2000);
    }

    public JcMsgResponse send(JcMessage msg, int timeoutMs) {
        try {

            oos.writeObject(msg);
            txCount++;
            reqRespMap.put(msg.getRequestId(), msg);

            synchronized (msg) {
                msg.wait(timeoutMs);
            }

            if (msg.getResponse() == null) {
                timeoutCount++;
                throw new JcResponseTimeoutException("No response received, timeout");
            }

            return msg.getResponse();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JcResponseTimeoutException ex) {
            JcMsgResponse resp = new JcMsgResponse(msg.getRequestId(), ex.getMessage());
            msg.setResponse(resp);
            return resp;
//            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //Called From This Thread or Client Thread
    public void sendNoResponse(JcMessage msg) {
        try {

            oos.writeObject(msg);

        } catch (IOException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        Thread.currentThread().setName(desc.getAppName() + "-" + hostName + ":" + port);
        try {
            connect();
        } catch (IOException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        running = true;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Handshake
//            if (!initHandshake()) {
//                LOG.severe("Handshake failed!");
//                return;
//            }

        while (running) {
            if (this.connType == ConnectionType.OUTBOUND) {
                getResponse();
            } else {
                try {
                    JcMessage request = (JcMessage) ois.readObject();
                    manager.getExecutorService().submit(new ExecuteMethod(oos, request));
//                    execMethod(request);
                    rxCount++;
                } catch (ClassNotFoundException | IOException ex) {
                    running = false;
                    Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        try {
            parallelConnectionCount--;
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void getResponse() {
        try {

            Object readObject = ois.readObject();
            rxCount++;

            if (readObject instanceof JcMessage) {

                JcMessage response = (JcMessage) readObject;
//                            JcMsgResponse respMsg = new JcMsgResponse(request.getRequestId(), readObject);
                JcMessage request = reqRespMap.remove(response.getRequestId());
                if (request != null) {
                    synchronized (request) {
                        request.setResponse(response.getResponse());
                        request.notifyAll();
                    }
                }
            }
        } catch (IOException ex) {
//            running = false;
            try {
                Thread.sleep(5000);
                connect();
                Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);

            } catch (InterruptedException ex1) {
                Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex1) {
                Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JcClientConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void execMethod(JcMessage request) throws ClassNotFoundException {
        JcMsgResponse response;
        String jndiName;
        try {
            jndiName = request.getClassName() + "#" + request.getClassName();
            Object service;

            try {
                service = ServiceLookup.getService(jndiName);
            } catch (NamingException ex) {
                response = new JcMsgResponse(request.getRequestId(), "Could not find service: " + jndiName);
                request.setResponse(response);
                sendNoResponse(request);
                Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }

            //TODO To be cached later
            Map<String, Method> commands = new HashMap<>();

            for (Method method : service.getClass().getMethods()) {
                if (method.isAnnotationPresent(JcCommand.class)) {
                    JcCommand tCommand = method.getAnnotation(JcCommand.class);
                }
                commands.put(method.getName(), method);
            }

            Method method = commands.get(request.getMethodName());
            Object result = method.invoke(service, request.getArgs());

            //Do work, then assign response here
            response = new JcMsgResponse(request.getRequestId(), result);
            request.setResponse(response);
            LOG.info("Sending response...");
            sendNoResponse(request);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            running = false;
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy() {
        try {
            oos.close();
            ois.close();
            running = false;
            socket.close();
            parallelConnectionCount--;

        } catch (IOException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JcAppDescriptor getDesc() {
        return desc;
    }

}
