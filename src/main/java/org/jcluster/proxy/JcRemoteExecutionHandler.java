/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.proxy;

import org.jcluster.annotation.JcRemote;
import org.jcluster.annotation.JcInstanceFilter;
import org.jcluster.messages.JcMessage;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.jcluster.cluster.JcFactory;

/**
 *
 * @author henry
 */
public class JcRemoteExecutionHandler implements InvocationHandler, Serializable {

    private final Map<String, JcProxyMethod> methodCache = new HashMap<>();

    public JcRemoteExecutionHandler() {

    }

    //caches different ways to call this method
    private JcProxyMethod initProxyMethod(Method method, Object[] args) {
        JcProxyMethod proxyMethod = new JcProxyMethod();

        Class<?> returnType = method.getReturnType();
        proxyMethod.setReturnType(returnType);

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
        
        String jndiLookupName = method.getDeclaringClass().getName();
        proxyMethod.setRemoteJndiName(jndiLookupName);
        
        return proxyMethod;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (!methodCache.containsKey(method.getName())) {
            methodCache.put(method.getName(), initProxyMethod(method, args));
        }

        JcProxyMethod proxyMethod = methodCache.get(method.getName());//contains info to send to correct App/Instance if specified

        JcMessage jcMessage = new JcMessage();
        jcMessage.setServiceName(proxyMethod.getRemoteJndiName() + "#" + proxyMethod.getRemoteJndiName()); 
        jcMessage.setCommand(method.getName());
        
        jcMessage.getArgs().addAll(Arrays.asList(args));

        Object send = JcFactory.getManager().send(jcMessage, proxyMethod, args);

        return proxyMethod.getReturnType().cast(send);

    }

}
