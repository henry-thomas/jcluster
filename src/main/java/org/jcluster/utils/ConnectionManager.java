/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.utils;

import org.jcluster.messages.Destination;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author henry
 */
public class ConnectionManager {

    Map<Integer, Destination> idDestMap = new HashMap<>();
    
    public Destination getMessageDest(int id){
        return idDestMap.get(id);
    }
}
