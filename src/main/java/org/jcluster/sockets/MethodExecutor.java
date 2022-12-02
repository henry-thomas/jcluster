/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.sockets;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.jcluster.ServiceLookup;
import org.jcluster.annotation.JcCommand;
import org.jcluster.exception.sockets.JcMethodNotFoundException;
import org.jcluster.messages.JcMessage;
import org.jcluster.messages.JcMsgResponse;

/**
 *
 * @author henry
 */
public class MethodExecutor implements Runnable {

    private final ObjectOutputStream oos;
    private final JcMessage request;
    private static final Logger LOG = Logger.getLogger(MethodExecutor.class.getName());

    public MethodExecutor(ObjectOutputStream oos, JcMessage msg) {
        this.oos = oos;
        this.request = msg;
    }

    public void sendAck(JcMessage msg) {
        try {

            oos.writeObject(msg);

        } catch (IOException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        JcMsgResponse response;
        String jndiName;
        try {
            jndiName = request.getClassName() + "#" + request.getClassName();
            Object service;

            try {
                service = ServiceLookup.getService(jndiName);
            } catch (NamingException ex) {
                response = new JcMsgResponse(request.getRequestId(), (JcMethodNotFoundException) ex);
                request.setResponse(response);
                sendAck(request);
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
//            LOG.info("Sending response...");
            sendAck(request);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JcClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
