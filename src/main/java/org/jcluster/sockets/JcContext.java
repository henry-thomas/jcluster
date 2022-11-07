package org.jcluster.sockets;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.zeromq.ZContext;

/**
 *
 * @author henry
 */
@Singleton
public class JcContext {

    private Set<JcClient> connSet = new HashSet<>();
    private final ZContext context = new ZContext();

    public JcContext() {
    }
    
    @PostConstruct
    public void onStartup(){
        
    }

    public static JcContext getInstance() {
        return JcContextHolder.INSTANCE;
    }

    private static class JcContextHolder {

        private static final JcContext INSTANCE = new JcContext();
    }

    public ZContext getContext() {
        return context;
    }

    @PreDestroy
    public void destroy() {
        this.context.destroy();
    }

}
