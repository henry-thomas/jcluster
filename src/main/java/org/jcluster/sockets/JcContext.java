package org.jcluster.sockets;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.zeromq.ZContext;
/**
 *
 * @author henry
 */
@ApplicationScoped
public class JcContext {

    private Set<JcClient> connSet = new HashSet<>();
    private final ZContext context = new ZContext();

    public JcContext() {
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
