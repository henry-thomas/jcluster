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

/**
 *
 * @author henry
 */
@RequestScoped
@Named
public class CoolView implements Serializable {

    private static final Logger LOG = Logger.getLogger(CoolView.class.getName());

//    @Inject
//    private SomeFunctionality fn;
//    @Inject
//    private JcClient client;
//    
//    @Resource(lookup = "")

//    @Inject
//    TestIFace prnt;
    @Inject
    Instance<IBusinessMethod> iFace;
//    @EJB
//    IBusinessMethod iFace;

    private long timeTaken = 0l;
    private String result;

//    @PostConstruct
//    public void init() {
//        ClassLoader classLoader = IBusinessMethod.class.getClassLoader();
//        iFace = (IBusinessMethod) Proxy.newProxyInstance(classLoader, new Class[]{IBusinessMethod.class}, new JcRemoteExecutionHandler(client));
//    }

    public long getTimeTaken() {

        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void test() {
//        prnt.print("sad");
//        InitialContext ctx = new InitialContext();
//            serviceObj = (IBusinessMethod) ctx.lookup(jndiName);
//        result = fn.doSomething();

        result = iFace.get().execBusinessMethod();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
