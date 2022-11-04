/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.utils;

import org.jcluster.messages.JcMessage;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author henry
 */
public class MessageUtils {

    public static byte[] srlz(JcMessage msg) {
        return SerializationUtils.serialize(msg);
    }

    public static JcMessage dsrlz(byte[] msg) {
        return SerializationUtils.deserialize(msg);
    }
}
