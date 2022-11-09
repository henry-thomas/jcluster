/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster;

import java.lang.reflect.Proxy;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import org.jcluster.proxy.JcRemoteExecutionHandler;

/**
 *
 * @author henry
 */
@LocalBean //required for glassfish
public class JcBootstrap implements Extension {

    private static final Logger LOG = Logger.getLogger(JcBootstrap.class.getName());

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
        LOG.info("JcBootstrap afterBeanDiscovery()");
        ClassLoader classLoader = IBusinessMethod.class.getClassLoader();
        Object newProxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{IBusinessMethod.class}, new JcRemoteExecutionHandler());
        event.addBean().types(IBusinessMethod.class).createWith(e -> newProxyInstance);

    }

}
