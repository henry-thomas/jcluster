/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.sockets;

import org.jcluster.IJcServerSocket;
import org.jcluster.annotation.JcCommand;
import org.jcluster.ServiceLookup;
import org.jcluster.messages.JcMessage;
import org.jcluster.utils.MessageUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import org.jcluster.messages.JcMsgResponse;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 *
 * @author henry
 */
public class JcServer implements Runnable, IJcServerSocket {

    private static final Logger LOG = Logger.getLogger(JcServer.class.getName());

    private ZMQ.Socket socket;
    private final String bindAddress;

    private final ZContext context = JcContext.getInstance().getContext();

    public JcServer(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("JcServer-ReceiveThread");

        socket = context.createSocket(SocketType.REP);
        socket.bind(bindAddress);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                LOG.log(Level.INFO, "JcServer listening at: {0}", bindAddress);
                byte[] req = socket.recv();

                JcMessage request = MessageUtils.dsrlz(req);

                String jndiName = request.getClassName(); //using serviceName for service/class name for now
                Object service = ServiceLookup.getService(jndiName);

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
                JcMsgResponse response = new JcMsgResponse(request.getRequestId(), result);
                request.setResponse(response);
                LOG.info("Sending response...");
                socket.send(MessageUtils.srlz(request));
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(JcServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        context.destroySocket(socket);
        Thread.currentThread().interrupt();
    }

}
