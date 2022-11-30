/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package org.jcluster.sockets.exception;

/**
 *
 * @author henry
 */
public class JcSocketConnectException extends Exception{

    /**
     * Creates a new instance of <code>JcSocketConnectException</code> without
     * detail message.
     */
    public JcSocketConnectException() {
    }

    /**
     * Constructs an instance of <code>JcSocketConnectException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public JcSocketConnectException(String msg) {
        super(msg);
    }
}
