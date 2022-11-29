/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author henry
 */
public class ServiceLookup {

    public static Object getService(String jndiName) throws NamingException {
        Object serviceObj = null;

        InitialContext ctx = new InitialContext();
        serviceObj = ctx.lookup(jndiName);

        return serviceObj;
    }
}
