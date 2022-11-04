/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.proxy;

import org.jcluster.annotation.JcRemote;
import org.jcluster.annotation.JcInstanceFilter;
import org.jcluster.sockets.JcClient;
import org.jcluster.messages.JcMessage;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author henry
 */
public class JcRemoteExecutionHandler implements InvocationHandler, Serializable {

//    @EJB
    private JcClient client;
    private final Map<String, JcProxyMethod> methodCache = new HashMap<>();
//    private static final Logger LOG = Logger.getLogger(JcRemoteExecutionHandler.class.getName());
//    private final Object invocationTarget;

    public JcRemoteExecutionHandler() {
//        this.invocationTarget = invocationTarget;
//        this.client = client;
        client = JcClient.getClient();
    }

    //caches different ways to call this method
    private JcProxyMethod initProxyMethod(Method method, Object[] args) {
        JcProxyMethod proxyMethod = new JcProxyMethod();
        Class<?> returnType = method.getReturnType();
        JcRemote jcRemoteAnn = method.getAnnotation(JcRemote.class);
        if (jcRemoteAnn != null) {
            proxyMethod.setAppName(jcRemoteAnn.appName());
        }

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            JcInstanceFilter instanceFilter = param.getAnnotation(JcInstanceFilter.class);

            if (instanceFilter != null) {
                proxyMethod.addInstanceFilterParam(instanceFilter.filterName(), i);
            }
        }
        return proxyMethod;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        LOG.entering(JcRemoteExecutionHandler.class.getName(), "invoke()");
        if (!methodCache.containsKey(method.getName())) {
            methodCache.put(method.getName(), initProxyMethod(method, args));
        }
        

        JcProxyMethod proxyMethod = methodCache.get(method.getName());//contains info to send to correct App/Instance if specified
        String appName = proxyMethod.getAppName();
//         //check if we have connection in manager with appName, else exception!
//        if(){
//            
//        }
        JcMessage jcMessage = new JcMessage();
        jcMessage.setServiceName("com.mypower24.smd.rar.lib.IBusinessMethod#com.mypower24.smd.rar.lib.IBusinessMethod");
        jcMessage.setCommand(method.getName());
//
        JcMessage send = client.send(jcMessage);
        return null;

    }//pojo with app name, filter for app instance

}
