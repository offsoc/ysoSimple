package cn.butler.jndi.controller.database;

import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.util.Properties;

@JNDIController
@JNDIMapping("/HikariCP")
public class HikariCPController extends SingleSQLController {

    public Object process(Properties props) {
        Reference ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null);
        ref.add(new StringRefAddr("driverClassName", props.getProperty("driver")));
        ref.add(new StringRefAddr("jdbcUrl", props.getProperty("url")));

        if (props.getProperty("sql") != null) {
            ref.add(new StringRefAddr("connectionInitSql", props.getProperty("sql")));
        }

        return ref;
    }
}
