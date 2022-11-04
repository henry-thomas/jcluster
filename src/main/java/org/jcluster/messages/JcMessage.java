/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henry
 */
public class JcMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int requestId;
    private String command;
    private String serviceName;
    private final List<Object> args = new ArrayList<>(); //arguments for method execution
    private Object data;
    private Destination src;
    private Destination dest;
    private JcMsgType msgType;
    private JcMessage response;

    private static int MSG_ID_INCR = 0;

    public JcMessage() {
        this.requestId = MSG_ID_INCR;
        MSG_ID_INCR++;
    }

    public JcMessage getResponse() {
        return response;
    }

    public void setResponse(JcMessage response) {
        this.response = response;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Destination getSrc() {
        return src;
    }

    public void setSrc(Destination src) {
        this.src = src;
    }

    public Destination getDest() {
        return dest;
    }

    public void setDest(Destination dest) {
        this.dest = dest;
    }

    public JcMsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(JcMsgType msgType) {
        this.msgType = msgType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<Object> getArgs() {
        return args;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
