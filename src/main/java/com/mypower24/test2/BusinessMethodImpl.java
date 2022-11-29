/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.jcluster.IBusinessMethod;

/**
 *
 * @author henry
 */
@Stateless
public class BusinessMethodImpl implements IBusinessMethod{

    private static final Logger LOG = Logger.getLogger(BusinessMethodImpl.class.getName());

    @Override
    public String getJndiName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String execBusinessMethod(Object message, String serialNumber) {
        LOG.info("Business Method Called");
        return "Hello";
    }
    
}
