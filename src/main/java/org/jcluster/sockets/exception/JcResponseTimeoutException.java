/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package org.jcluster.sockets.exception;

/**
 *
 * @author henry
 */
public class JcResponseTimeoutException extends Exception{

    /**
     * Creates a new instance of <code>JcResponseTimeoutException</code> without
     * detail message.
     */
    public JcResponseTimeoutException() {
    }

    /**
     * Constructs an instance of <code>JcResponseTimeoutException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public JcResponseTimeoutException(String msg) {
        super(msg);
    }
}
