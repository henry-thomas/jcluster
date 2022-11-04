/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author henry
 */
@Interceptor
public class SomeInterceptor {

    private static final Logger LOG = Logger.getLogger(SomeInterceptor.class.getName());

    @AroundInvoke
    public Object intercept(InvocationContext ctx) {
        try {
            LOG.log(Level.INFO, "Interceptor triggered: {0}", ctx.getContextData());
            return ctx.proceed();
        } catch (Exception ex) {
            Logger.getLogger(SomeInterceptor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
