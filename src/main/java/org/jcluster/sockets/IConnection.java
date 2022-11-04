/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.jcluster.sockets;

import org.jcluster.messages.JcMessage;

/**
 *
 * @author henry
 */
public interface IConnection {

    public JcMessage send(JcMessage msg);
    
    public void destroy();
    
}
