/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.messages;

import java.io.Serializable;

/**
 *
 * @author henry
 */
public class ConnectionParam implements Serializable {

    private final boolean secure;
    private final String appId;

    public ConnectionParam(boolean secure, String appId) {
        this.secure = secure;
        this.appId = appId;
    }

    public boolean isSecure() {
        return secure;
    }

    public String getAppId() {
        return appId;
    }

}
