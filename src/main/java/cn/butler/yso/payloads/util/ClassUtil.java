package cn.butler.yso.payloads.util;

import cn.butler.yso.JavassistClassLoader;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

public class ClassUtil {
    public static Class genClass(String clazzName) throws Exception {
        if(clazzName.startsWith("java.")){
            return loadClass(clazzName);
        }
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(clazzName);
        ctClass.getClassFile().setVersionToJava5();
        Class clazz = ctClass.toClass(new JavassistClassLoader());
        ctClass.defrost();
        return clazz;
    }

    public static Class genClassBySuid(String className,String suid) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(className);
        ctClass.getClassFile().setVersionToJava5();
        CtClass serializableInterface = classPool.get("java.io.Serializable");
        ctClass.addInterface(serializableInterface);
        ctClass.addField(CtField.make("private static final long serialVersionUID = " + suid + ";", ctClass));
        Class clazz = ctClass.toClass(new JavassistClassLoader());
        ctClass.defrost();
        return clazz;
    }

    public static Class loadClass(String clazzName) throws Exception{
        try{
            return Class.forName(clazzName);
        }catch (ClassNotFoundException e){
            return Thread.currentThread().getContextClassLoader().loadClass(clazzName);
        }
    }
}
