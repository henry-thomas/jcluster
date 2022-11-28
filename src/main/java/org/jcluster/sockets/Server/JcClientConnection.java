/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.sockets.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcluster.ServiceLookup;
import org.jcluster.annotation.JcCommand;
import org.jcluster.cluster.ClusterManager;
import org.jcluster.cluster.JcFactory;
import org.jcluster.cluster.hzUtils.HzController;
import org.jcluster.messages.ConnectionParam;
import org.jcluster.messages.JcMessage;
import org.jcluster.messages.JcMsgResponse;

/**
 *
 * @author henry
 */
public class JcClientConnection implements Runnable {

    private static final Logger LOG = Logger.getLogger(JcClientConnection.class.getName());
    private final ClusterManager manager = JcFactory.getManager();

    private String connId;
    private final int port;
    private final String hostName;
    private final Socket socket;
    private ObjectOutputStream outWriter;
    private ObjectInputStream ois;
    private static int parallelConnectionCount = 0;
    private int paralConnWaterMark = 0;
    private boolean isClient;
    private boolean secure;
    private boolean running;

    public JcClientConnection(Socket sock, boolean isClient) {
        this.isClient = isClient;
        this.socket = sock;
        this.port = sock.getPort();
        this.hostName = sock.getInetAddress().getHostAddress();
        parallelConnectionCount++;
        if (parallelConnectionCount > paralConnWaterMark) {
            paralConnWaterMark = parallelConnectionCount;
            System.out.println("Number of connections reached: " + paralConnWaterMark);
        }
    }

    private boolean initHandshake() {

        try {
            Map<String, String> connInitMsg = new HashMap<>();

            connInitMsg.put("connInit", hostName);
            outWriter.writeObject(connInitMsg);

            ConnectionParam connParam = (ConnectionParam) ois.readObject();

            if (HzController.getInstance().getMap().containsKey(connParam.getAppId())) {
                ConnectionParam connectionParam = new ConnectionParam(secure, manager.getThisDescriptor().getInstanceId());
                outWriter.writeObject(connectionParam);
                return true;
            } else {
                return false;
            }

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void run() {
        JcMessage request;
        JcMessage outline;
        running = true;

        try {
            outWriter = new ObjectOutputStream(socket.getOutputStream());
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(isr);

            ois = new ObjectInputStream(socket.getInputStream());
            LOG.info("Client connected.");
            //Handshake
//            if (!initHandshake()) {
//                LOG.severe("Handshake failed!");
//                return;
//            }

            while ((request = (JcMessage) ois.readObject()) != null && running) {
//                JcMessage request = MessageUtils.dsrlz(req);
                JcMsgResponse response;
                String jndiName = request.getClassName() + "#" + request.getClassName(); //using serviceName for service/class name for now
                Object service = ServiceLookup.getService(jndiName);

                if (service == null) {
                    response = new JcMsgResponse(request.getRequestId(), "Could not find service: " + jndiName);
                    request.setResponse(response);
                    outWriter.writeObject(request);
                    return;
                }

                //To be cached later
                Map<String, Method> commands = new HashMap<>();

                for (Method method : service.getClass().getMethods()) {
                    if (method.isAnnotationPresent(JcCommand.class)) {
                        JcCommand tCommand = method.getAnnotation(JcCommand.class);
                    }
                    commands.put(method.getName(), method);
                }

                Method method = commands.get(request.getMethodName());
                Object result = method.invoke(service, request.getArgs());
                //Do work, assign response here
                response = new JcMsgResponse(request.getRequestId(), result);
                request.setResponse(response);
                LOG.info("Sending response...");
                outWriter.writeObject(request);
            }

            socket.close();
            parallelConnectionCount--;
        } catch (IOException | ClassNotFoundException ex) {
            parallelConnectionCount--;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy() {
        try {
            socket.close();
            parallelConnectionCount = 0;
            running = false;
        } catch (IOException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
