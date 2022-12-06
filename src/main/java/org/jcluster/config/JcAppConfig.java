/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.config;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author henry
 */
public class JcAppConfig {

    private static final Logger LOG = Logger.getLogger(JcAppConfig.class.getName());

    private final String jcHzPrimaryMember;
    private final Integer port;
    private final String hostName;
    private final String appName;
    private final Long jcLastSendMaxTimeout;

    private static final JcAppConfig INSTANCE = new JcAppConfig();

    private JcAppConfig() {
        this.jcHzPrimaryMember = readProp("JC_HZ_PRIMARY_MEMBER");
        this.port = Integer.valueOf(readProp("JC_PORT"));
        this.hostName = readProp("JC_HOSTNAME");
        this.appName = readProp("JC_APP_NAME");
        this.jcLastSendMaxTimeout = Long.valueOf(readProp("JC_LAST_SEND_MAX_TIMEOUT"));
    }

    private String readProp(String propName) {
        String prop = System.getProperty(propName);
        if (prop == null) {
            LOG.log(Level.SEVERE, "{0} property not set!", propName);
            return null;
        }
        return prop;
    }

    public static JcAppConfig getINSTANCE() {
        return INSTANCE;
    }

    public String getJcHzPrimaryMember() {
        return jcHzPrimaryMember;
    }

    public Integer getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }

    public String getAppName() {
        return appName;
    }

    public Long getJcLastSendMaxTimeout() {
        return jcLastSendMaxTimeout;
    }

}
