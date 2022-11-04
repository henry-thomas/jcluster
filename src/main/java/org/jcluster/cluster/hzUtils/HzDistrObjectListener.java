/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster.hzUtils;

import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author henry
 */
public class HzDistrObjectListener implements DistributedObjectListener {

    private static final Logger LOG = Logger.getLogger(HzDistrObjectListener.class.getName());

    @Override
    public void distributedObjectCreated(DistributedObjectEvent event) {
        LOG.log(Level.INFO, "distributedObjectCreated: {0}", event.getDistributedObject().getName());
    }

    @Override
    public void distributedObjectDestroyed(DistributedObjectEvent event) {
        LOG.log(Level.INFO, "distributedObjectDestroyed: {0}", event.getDistributedObject().getName());
    }

}
