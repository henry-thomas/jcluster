/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import org.jcluster.core.messages.JcMessage;
import com.mypower24.test2.interfaces.IBusinessMethods;
import com.mypower24.test2.interfaces.IMoreBusinessMethods;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.jcluster.core.cluster.JcFactory;

/**
 *
 * @author henry
 */
@RequestScoped
@Named
public class CoolView implements Serializable {

    private static final Logger LOG = Logger.getLogger(CoolView.class.getName());

    private final List<String> emptyMsg = new ArrayList<>();
    private final List<String> smallData = new ArrayList<>();
    private final List<String> bigData = new ArrayList<>();

    private Object largeDataResult;

    @PostConstruct
    public void init() {
//        for (int i = 0; i < 100; i++) {
//            smallData.add("1234567890_" + i);
//        }
//
//        for (int i = 0; i < 100_000; i++) {
//            bigData.add("1234567890_" + i);
//        }

        test();
    }

    @Inject
    Instance<IBusinessMethods> iFace;

    @Inject
    Instance<IMoreBusinessMethods> anotherIFace;

    private long timeTaken = 0l;
    private String result;

    public long getTimeTaken() {

        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void test() {
        try {
            result = iFace.get().execBusinessMethod("", "SLV012345");
        } catch (Throwable e) {
            LOG.info("Could not get message");
        }
    }

    public void stopSocket() {
        JcFactory.getManager().socketStopTest();
    }

    public void testAnother() {
//        result = iFace.execBusinessMethod("sad", "SLV01234");
        result = anotherIFace.get().execAnotherBusinessMethod("Pieter").getSurname();
//        long start = System.currentTimeMillis();
//
//        largeDataResult = anotherIFace.get().getLargeData();
//        result = String.valueOf(System.currentTimeMillis() - start + " ms");
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
