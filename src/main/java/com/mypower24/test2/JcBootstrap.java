/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2;

import org.jcluster.IBusinessMethod;
import org.jcluster.proxy.JcRemoteExecutionHandler;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 *
 * @author henry
 */
@Startup
@Singleton
@LocalBean //required for glassfish, no one tells us this
public class JcBootstrap implements Extension {

    private static final Logger LOG = Logger.getLogger(JcBootstrap.class.getName());

    public JcBootstrap() {
//        ClassLoader classLoader = IBusinessMethod.class.getClassLoader();
//        Proxy.newProxyInstance(classLoader, new Class[]{IBusinessMethod.class}, new JcRemoteExecutionHandler());
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
        LOG.info("JcBootstrap afterBeanDiscovery()");

        ClassLoader classLoader = IBusinessMethod.class.getClassLoader();
        Object newProxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{IBusinessMethod.class}, new JcRemoteExecutionHandler());
        event.addBean().types(IBusinessMethod.class).createWith(e -> newProxyInstance);

    }

    private void createClassImplementation(Class clazz) {

    }

    @PostConstruct
    public void init() {
//            LOG.info("JcBootstrap init()");
//            InitialContext ctx = new InitialContext();
//
//            ClassLoader classLoader = IBusinessMethod.class.getClassLoader();
//            Object newProxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{IBusinessMethod.class}, new JcRemoteExecutionHandler());
//
//            String composeName = ctx.composeName("randomString", "prefix");
//            LOG.log(Level.INFO, "ComposeName: {0}", composeName);
//            BeanManager beanManager = CDI.current().getBeanManager();
//            beanManager;
//            ctx.bind(composeName, newProxyInstance);
//            BeanManager beanManager = CDI.current().getBeanManager();
//            ctxService.createContextualProxy(newProxyInstance, IBusinessMethod.class);

    }

}
