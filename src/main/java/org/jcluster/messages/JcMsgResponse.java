/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.messages;

import java.io.Serializable;

/**
 *
 * @author henry
 */
public class JcMsgResponse implements Serializable {

    private final int requestId;
    private final Object data;

    public JcMsgResponse(int requestId, Object data) {
        this.requestId = requestId;
        this.data = data;
    }

    public int getRequestId() {
        return requestId;
    }

    public Object getData() {
        return data;
    }

}
