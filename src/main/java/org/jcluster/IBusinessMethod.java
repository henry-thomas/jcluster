/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.jcluster;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ws.rs.core.Context;
import org.jcluster.annotation.JcInstanceFilter;
import org.jcluster.annotation.JcRemote;

/**
 *
 * @author henry
 */
@Remote
public interface IBusinessMethod extends Serializable {
//       @Interceptors(LoggingIntercept.class)

    public String getJndiName();

    @JcRemote(appName = "lws")
    public String execBusinessMethod();

    @JcRemote(appName = "lws")
    public String execBusinessMethod(Object message, @JcInstanceFilter(filterName = "loggerSerial") String serialNumber);

}
