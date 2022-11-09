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
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        JcProxyMethod proxyMethod = methodCache.get(method.getName());//contains info to send to correct App/Instance if specified
        if (proxyMethod == null) {
            proxyMethod = JcProxyMethod.initProxyMethod(method, args);
            methodCache.put(method.getName(), proxyMethod);
        }

        

        Object send = JcFactory.getManager().send(proxyMethod, args);

        return proxyMethod.getReturnType().cast(send);

    }

}
