/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.jcluster;

import java.io.Serializable;
import javax.ejb.Remote;
import org.jcluster.annotation.JcInstanceFilter;

/**
 *
 * @author henry
 */
@Remote
public interface IBusinessMethod extends Serializable{
//       @Interceptors(LoggingIntercept.class)
    public String getJndiName();

    public String execBusinessMethod();
    
    public String execBusinessMethod(@JcInstanceFilter Object message, @JcInstanceFilter String serialNumber);
    
}
