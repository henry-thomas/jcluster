/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import org.jcluster.IBusinessMethod;

/**
 *
 * @author henry
 */
@RequestScoped
@Default
public class BusinessMethodImpl implements IBusinessMethod{

    @Override
    public String getJndiName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String execBusinessMethod(Object message, String serialNumber) {
        return "Hello";
    }
    
}
