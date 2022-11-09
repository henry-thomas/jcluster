/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.jcluster;

import java.io.Serializable;
import javax.ejb.Remote;
import org.jcluster.annotation.JcInstanceFilter;
import org.jcluster.annotation.JcRemote;

/**
 *
 * @author henry
 */
@Remote
@JcRemote(appName = "lws")
public interface IBusinessMethod extends Serializable {

    public String getJndiName();

    public String execBusinessMethod(Object message, @JcInstanceFilter(filterName = "loggerSerial") String serialNumber);

}
