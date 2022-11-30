/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mypower24.test2.interfaces;

import javax.ejb.Remote;
import org.jcluster.annotation.JcInstanceFilter;
import org.jcluster.annotation.JcRemote;

/**
 *
 * @author henry
 */
@Remote
@JcRemote(appName = "lws")
public interface IMoreBusinessMethods {

    public Boolean execAnotherBusinessMethod(@JcInstanceFilter(filterName = "name") String name);
}
