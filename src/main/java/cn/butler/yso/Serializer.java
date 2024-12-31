package cn.butler.yso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

public class Serializer implements Callable<byte[]> {
	private final Object object;
	public Serializer(Object object) {
		this.object = object;
	}

	public byte[] call() throws Exception {
		return serialize(object);
	}

	public static byte[] serialize(final Object obj) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(obj, out);
		return out.toByteArray();
	}

	public static void serialize(final Object obj, final OutputStream out) throws IOException {
		final ObjectOutputStream objOut = new ObjectOutputStream(out);
		objOut.writeObject(obj);
	}

    public static byte[] serializeWithGzip(final Object obj) throws IOException {
        final ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        try (
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOut);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut)) {
            objOut.writeObject(obj);
        }
        return byteArrayOut.toByteArray();
    }

}
