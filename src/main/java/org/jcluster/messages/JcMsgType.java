/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.jcluster.messages;

import java.io.Serializable;

/**
 *
 * @author henry
 */
public enum JcMsgType implements Serializable{
    REQUEST, RESPONSE, BROADCAST, INIT, TEST
}
