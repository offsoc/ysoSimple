package cn.butler.template.config;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.config.Config;
import cn.butler.utils.FileUtils;
import org.apache.commons.cli.CommandLine;

public class SSTIAttackConfig extends Config {
    @Override
    public void parse(CommandLine cmdLine) {
        final String payloadType = cmdLine.getOptionValue("gadget");
        final String command = cmdLine.getOptionValue("args");

        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass("SSTIAttack",payloadType);
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
                FileUtils.savePayloadToFile((String) object,fileName);
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
}
