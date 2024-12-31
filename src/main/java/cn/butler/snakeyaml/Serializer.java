package cn.butler.snakeyaml;

import org.yaml.snakeyaml.Yaml;

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
        // 使用 Yaml 进行序列化
        Yaml yaml = new Yaml();
        return yaml.dump(obj);
    }
}
