package cn.butler.jndi;

import cn.butler.jndi.server.RMIServer;
import cn.butler.jndi.server.WebServer;
import cn.butler.jndi.server.LDAPServer;
import org.apache.commons.cli.CommandLine;

public class JndiConfig extends cn.butler.payloads.config.Config {
    public static String ip = "127.0.0.1";
    public static int rmiPort = 1099;
    public static int ldapPort = 1389;
    public static int httpPort = 3456;
    public static String url;
    public static String codebase;
    public static boolean useReferenceOnly = false;

    public void parse(CommandLine cmdLine) {
        // 解析命令行参数
        if(cmdLine.hasOption("ip")){
            ip = cmdLine.getOptionValue("ip");
        }
        if(cmdLine.hasOption("rmi-port")){
            rmiPort = Integer.parseInt(cmdLine.getOptionValue("rmi-port"));
        }
        if(cmdLine.hasOption("ldap-port")){
            ldapPort = Integer.parseInt(cmdLine.getOptionValue("ldap-port"));
        }
        if(cmdLine.hasOption("web-port")){
            httpPort = Integer.parseInt(cmdLine.getOptionValue("web-port"));
        }
        if(cmdLine.hasOption("jndi-url")){
            url = cmdLine.getOptionValue("jndi-url");
        }
        if(cmdLine.hasOption("jndi-useReferenceOnly")){
            useReferenceOnly = true;
        }
        if(cmdLine.hasOption("jndi-help")){
            System.out.println("Usage: java -jar ysoSimple.jar -m JNDIAttack [-i <ip>] [-r <rmiPort>] [-l <ldapPort>] [-p <httpPort>] [-u <url>]");
            System.exit(-1);
        }

        codebase = "http://" + ip + ":" + httpPort + "/";

        System.out.println("--------JNDIAttack Model is starting--------");

        RMIServer rmiServer = new RMIServer(JndiConfig.ip, JndiConfig.rmiPort);
        LDAPServer ldapServer = new LDAPServer(JndiConfig.ip, JndiConfig.ldapPort);
        WebServer webServer = new WebServer(JndiConfig.ip, JndiConfig.httpPort);

        Thread rmiThread = new Thread(rmiServer);
        Thread ldapThread = new Thread(ldapServer);
        Thread webThread = new Thread(webServer);

        rmiThread.start();
        ldapThread.start();
        webThread.start();
    }
}
