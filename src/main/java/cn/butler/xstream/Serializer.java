package cn.butler.xstream;

import com.thoughtworks.xstream.XStream;

import java.io.IOException;
import java.util.concurrent.Callable;

public class Serializer implements Callable<byte[]> {
    private final Object object;
    public Serializer(Object object) {
        this.object = object;
    }

    @Override
    public byte[] call() throws Exception {
        return new byte[0];
    }

    public static String serialize(final Object obj) throws IOException {
        // 使用 XStream 进行序列化
        XStream xstream = new XStream();
        return xstream.toXML(obj);
    }
}
