/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.configurator.BeanConfigurator;
import org.jcluster.proxy.JcRemoteExecutionHandler;
import com.mypower24.test2.interfaces.IBusinessMethods;
import com.mypower24.test2.interfaces.IMoreBusinessMethods;

/**
 *
 * @author henry
 */
@LocalBean //required for glassfish
public class JcBootstrap implements Extension {

    private static final Logger LOG = Logger.getLogger(JcBootstrap.class.getName());

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
        LOG.info("JcBootstrap afterBeanDiscovery()");
        ClassLoader classLoader = IBusinessMethods.class.getClassLoader();

        Class[] typeArr = {IBusinessMethods.class, IMoreBusinessMethods.class};
//        Set<Class> types = Set.of(typeArr);
        List<Class> asList = Arrays.asList(typeArr);
        Class[] toArray = asList.toArray(new Class[0]);
//        Class[] toArray = (Class[]) types.toArray();

        Object newProxyInstance = Proxy.newProxyInstance(classLoader, toArray, new JcRemoteExecutionHandler());
//        Object newProxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{IBusinessMethods.class, IMoreBusinessMethods.class}, new JcRemoteExecutionHandler());
        BeanConfigurator<Object> createWith = event.addBean().types(toArray).createWith(e -> newProxyInstance);

    }

}
