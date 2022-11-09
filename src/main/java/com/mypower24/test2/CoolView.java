/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2;

import org.jcluster.IBusinessMethod;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import org.jcluster.messages.JcMessage;

/**
 *
 * @author henry
 */
@RequestScoped
@Named
public class CoolView implements Serializable {

    private static final Logger LOG = Logger.getLogger(CoolView.class.getName());

    @Inject
    Instance<IBusinessMethod> iFace;


    private long timeTaken = 0l;
    private String result;

    public long getTimeTaken() {

        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void test() {
        result = iFace.get().execBusinessMethod(new DevMessage(), "SLV01234");
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
