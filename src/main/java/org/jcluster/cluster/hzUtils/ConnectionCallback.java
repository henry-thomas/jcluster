/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster.hzUtils;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import java.util.logging.Logger;
import org.jcluster.bean.JcAppInstance;

/**
 *
 * @author henry
 */
public class ConnectionCallback implements EntryAddedListener<String, String>, EntryRemovedListener<String, String>, EntryUpdatedListener<String, String> {

    private static final Logger LOG = Logger.getLogger(ConnectionCallback.class.getName());

    @Override
    public void entryAdded(EntryEvent<String, String> event) {
        LOG.info(event.getValue());
    }

    @Override
    public void entryRemoved(EntryEvent<String, String> event) {
        System.out.println(event.getValue());
    }

    @Override
    public void entryUpdated(EntryEvent<String, String> event) {
        System.out.println(event.getValue());
    }

}
