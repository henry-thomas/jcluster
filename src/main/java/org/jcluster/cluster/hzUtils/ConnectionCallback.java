/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.cluster.hzUtils;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

/**
 *
 * @author henry
 */
public class ConnectionCallback implements EntryAddedListener<String, String>, EntryRemovedListener<String, String>, EntryUpdatedListener<String, String> {

    @Override
    public void entryAdded(EntryEvent<String, String> event) {
        System.out.println(event.getValue());
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
