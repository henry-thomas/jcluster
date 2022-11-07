/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.sockets;

import org.jcluster.IBusinessMethod;
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
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 *
 * @author henry
 */
@ApplicationScoped
public class JcServer implements Runnable, IJcServerSocket {

    private static final Logger LOG = Logger.getLogger(JcServer.class.getName());

//    private Map<String, Method> commands = new HashMap<>();
    private ZMQ.Socket socket;
    private final String bindAddress = "tcp://localhost:5555";

//    @Inject
//    JcContext jcCtx;
    private ZContext context = JcContext.getInstance().getContext();

    public JcServer() {
    }

    @Override
    public void run() {
//        context = jcCtx.getContext();
        Thread.currentThread().setName("JcServer-ReceiveThread");
        // Socket to talk to clients
//        socket = context.createSocket(SocketType.ROUTER);
        socket = context.createSocket(SocketType.REP);
        socket.bind(bindAddress);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                LOG.info("Waiting for message...");
                byte[] req = socket.recv();

//                ZMsg recvMsg = ZMsg.recvMsg(socket);
                JcMessage request = MessageUtils.dsrlz(req);

                String jndiName = request.getServiceName(); //using serviceName for service/class name for now
//                String jndi = "com.mypower24.smd.rar.lib.IBusinessMethod#com.mypower24.smd.rar.lib.IBusinessMethod";
                IBusinessMethod service = ServiceLookup.getService(jndiName);

                //To be cached later
                Map<String, Method> commands = new HashMap<>();

                for (Method method : service.getClass().getMethods()) {
                    if (method.isAnnotationPresent(JcCommand.class)) {
                        JcCommand tCommand = method.getAnnotation(JcCommand.class);
                    }
                    commands.put(method.getName(), method);
                }

                Method method = commands.get(request.getCommand());
                Object result = method.invoke(service, request.getArgs().toArray());
                //Do work, assign response here
                JcMessage response = new JcMessage();
                response.setData(result);
                request.setResponse(response);
                LOG.info("Sending response...");
                socket.send(MessageUtils.srlz(request));
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                JcMessage response = new JcMessage();
                response.setData(ex.getMessage());
                socket.send(MessageUtils.srlz(response));
                Logger.getLogger(JcServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @PostConstruct
    public void init() {
//            LOG.log(Level.INFO, "Looking for: {0}", IDummy.class.getName());
        LOG.log(Level.INFO, "Starting JcServer: {0}", JcServer.class.getName());
//            Class<?> loadClass = ClassLoader.getSystemClassLoader().loadClass(IDummy.class.getName());
//            loadClass.getConstructor().newInstance();
//            
//
//            for (Method method : loadClass.getMethods()) {
//                if (method.isAnnotationPresent(JcCommand.class)) {
//                    JcCommand tCommand = method.getAnnotation(JcCommand.class);
//                    commands.put(tCommand.name(), method);
//                }
//            }

//        this.run();
    }

    public void start() {
        this.run();
        LOG.log(Level.INFO, "Starting JcServer: {0}", JcServer.class.getName());
    }

    @PreDestroy
    public void destroy() {
        context.destroySocket(socket);
        Thread.currentThread().interrupt();
    }

}
