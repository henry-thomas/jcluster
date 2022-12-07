/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mypower24.test2.interfaces;

import java.io.Serializable;
import javax.ejb.Remote;
import org.jcluster.lib.annotation.JcInstanceFilter;
import org.jcluster.lib.annotation.JcRemote;

/**
 *
 * @author henry
 */
@Remote
@JcRemote(appName = "lws")
public interface IBusinessMethods extends Serializable {

    public String getJndiName();

    public String execBusinessMethod(Object message, @JcInstanceFilter(filterName = "loggerSerial") String serialNumber);

}
