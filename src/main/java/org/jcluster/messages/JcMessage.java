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
public class JcMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int requestId;
    private final String methodName;
    private final String className;
    private final Object[] args; //arguments for method execution
    private JcMsgResponse response;
//    private final Object lock = new Object(); //Sync on lock

    private static int MSG_ID_INCR = 0;

    public JcMessage(String methodName, String className, Object[] args) {
        this.methodName = methodName;
        this.className = className;
        this.args = args;
        this.requestId = MSG_ID_INCR ++;
    }

    public JcMsgResponse getResponse() {
        return response;
    }

    public void setResponse(JcMsgResponse response) {
        this.response = response;
    }

//    public static long getSerialVersionUID() {
//        return serialVersionUID;
//    }

    public int getRequestId() {
        return requestId;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public Object[] getArgs() {
        return args;
    }

//    public Object getLock() {
//        return lock;
//    }
    
    

}
