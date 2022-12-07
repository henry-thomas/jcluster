# jcluster

A peer to peer messaging system for Java applications which is simple to use, for use cases where transactions aren't required and message persistence not a priority.

Uses a combination of Hazelcast for member discovery, plain sockets with java serialization for connections and messaging, dynamic proxies, and jndi lookup for remote method invocation.


To use:

In META-INF folder, add a file called "javax.enterprise.inject.spi.Extension" with contents "org.jcluster.JcBootstrap"

Create your remote interfaces if you haven't done so already, and annotate them using the provided annotations:
```
@Remote
@JcRemote(appName = "lws")
public interface IBusinessMethod extends Serializable {

    public String getJndiName();

    public String execBusinessMethod(Object message, @JcInstanceFilter(filterName = "loggerSerial") String serialNumber);
}
```

@JcRemote annotation is required to match the remote appName when you bootstrapped that application.

@JcInstanceFilter is used to find a specific instance of an app that holds the value of that parameter, and will send the request to that specific instance.

