# jcluster

A peer to peer messaging system for Java applications which is simple to use, for use cases where transactions aren't required and message persistence not a priority.

Uses a combination of Hazelcast for member discovery, plain sockets with java serialization for connections and messaging, dynamic proxies, and jndi lookup for remote method invocation.


To use:

In META-INF folder, add a file called "javax.enterprise.inject.spi.Extension" with contents "org.jcluster.JcBootstrap"

Create your remote interfaces if you haven't done so already, and annotate them using the provided annotations:
@Remote
public interface IBusinessMethod extends Serializable {

    public String getJndiName();

    @JcRemote(appName = "lws")
    public String execBusinessMethod(Object message, @JcInstanceFilter(filterName = "loggerSerial") String serialNumber);
}

@JcRemote annotation is required to match the remote appName when you bootstrapped that application.

@JcInstanceFilter is used to find a specific instance of an app with that app name, and will send the request to that specific instance.

In JcBootstrap class, register all remote interfaces:
@Startup
@Singleton
@LocalBean //required for glassfish
public class JcBootstrap implements Extension {

    private static final Logger LOG = Logger.getLogger(JcBootstrap.class.getName());

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
        LOG.info("JcBootstrap afterBeanDiscovery()");
        ClassLoader classLoader = IBusinessMethod.class.getClassLoader();
        Object newProxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{IBusinessMethod.class}, new JcRemoteExecutionHandler());
        event.addBean().types(IBusinessMethod.class).createWith(e -> newProxyInstance);

    }

    @PostConstruct
    public void init() {

    }
}


In LifeCycleListerner, in contextInitialized method, call the JcFactory to initialize with the correct configuration, and add any filters you will need.

Eg: 

public class LifecycleListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(LifecycleListener.class.getName());
    private Future<?> submit;


    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {

        JcFactory.initManager("lws", "192.168.100.18", 4567);
        JcFactory.getManager().addFilter("loggerSerial", "SLV012345");
        LOG.info("LifecycleListener: contextInitialized()");
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {

        JcFactory.getManager().destroy();
        LOG.info("LifecycleListener: contextDestroyed()");
    }
}

Then inject remote interface and call the interface method you need. JCluster will make sure it ends up calling the correct instance!
