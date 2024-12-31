package cn.butler.hessian;

import cn.butler.hessian.type.HessianBasic;
import com.caucho.hessian.io.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;

public class Serializer  implements Callable<byte[]> {
    private final Object object;
    public Serializer(Object object) {
        this.object = object;
    }

    @Override
    public byte[] call() throws Exception {
        return new byte[0];
    }

    /**
     * caucho Hessian原生序列化器
     */
    public static byte[] serialize(Object o) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AbstractHessianOutput out = new Hessian2Output(bos);
        //序列化没有实现 Serializable 接口的类
        HessianBasic.NoWriteReplaceSerializerFactory sf = new HessianBasic.NoWriteReplaceSerializerFactory();
        sf.setAllowNonSerializable(true);
        out.setSerializerFactory(sf);
        out.writeObject(o);
        out.close();
        return bos.toByteArray();
    }

    /**
     * caucho Hessian1原生序列化器
     */
    public static byte[] serialize1(Object o) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput out = new HessianOutput(bos);
        //序列化没有实现 Serializable 接口的类
        HessianBasic.NoWriteReplaceSerializerFactory sf = new HessianBasic.NoWriteReplaceSerializerFactory();
        sf.setAllowNonSerializable(true);
        out.setSerializerFactory(sf);
        out.getSerializerFactory().setAllowNonSerializable(true);
        out.writeObject(o);
        out.flush();
        return bos.toByteArray();
    }

    /**
     * 构造Except Hessian2原生序列化器
     * @param o
     * @return
     * @throws IOException
     */
    public static byte[] serializeExcept(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(bos);
        //序列化没有实现 Serializable 接口的类
        HessianBasic.NoWriteReplaceSerializerFactory sf = new HessianBasic.NoWriteReplaceSerializerFactory();
        sf.setAllowNonSerializable(true);
        out.setSerializerFactory(sf);
        bos.write(67);
        out.getSerializerFactory().setAllowNonSerializable(true);
        out.writeObject(o);
        out.flushBuffer();
        return bos.toByteArray();
    }

    /**
     * 构造Except Hessian1原生序列化器(后面发现Hessian1没有Except构造打法,删了)
     * @param o
     * @return
     * @throws IOException
     */
//    public static byte[] serialize1Except(Object o) throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        HessianOutput out = new HessianOutput(bos);
//        //序列化没有实现 Serializable 接口的类
//        HessianBasic.NoWriteReplaceSerializerFactory sf = new HessianBasic.NoWriteReplaceSerializerFactory();
//        sf.setAllowNonSerializable(true);
//        out.setSerializerFactory(sf);
//        bos.write(67);
//        out.getSerializerFactory().setAllowNonSerializable(true);
//        out.writeObject(o);
//        out.flush();
//        return bos.toByteArray();
//    }

    /**
     * caucho Hessian2原生序列化器UTF8-Bytes-Mix
     * @return
     */
    public static byte[] serializeUTF8BytesMix(Object o) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2OutputOverlongEncoding hessian2OutputOverlongEncoding = new Hessian2OutputOverlongEncoding(bos);
        hessian2OutputOverlongEncoding.getSerializerFactory().setAllowNonSerializable(true);
        hessian2OutputOverlongEncoding.writeObject(o);
        hessian2OutputOverlongEncoding.flushBuffer();
        return bos.toByteArray();
    }

    /**
     * caucho Hessian1原生序列化器UTF8-Bytes-Mix
     * @return
     */
    public static byte[] serialize1UTF8BytesMix(Object o) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput hessian1OutputOverlongEncoding = Hessian1OutputEnhance.getHessian1Output(bos);
        hessian1OutputOverlongEncoding.getSerializerFactory().setAllowNonSerializable(true);
        hessian1OutputOverlongEncoding.writeObject(o);
        hessian1OutputOverlongEncoding.flush();
        return bos.toByteArray();
    }

    /**
     * 构造Except caucho Hessian原生序列化器UTF8-Bytes-Mix
     * @param o
     * @return
     * @throws IOException
     */
    public static byte[] serializeExceptUTF8BytesMix(Object o) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2OutputOverlongEncoding hessian2OutputOverlongEncoding = new Hessian2OutputOverlongEncoding(bos);
        bos.write(67);
        hessian2OutputOverlongEncoding.getSerializerFactory().setAllowNonSerializable(true);
        hessian2OutputOverlongEncoding.writeObject(o);
        hessian2OutputOverlongEncoding.flushBuffer();
        return bos.toByteArray();
    }

    /**
     * (后面发现Hessian1没有Except构造打法,删了)
     * @param o
     * @return
     * @throws IOException
     */
//    public static byte[] serialize1ExceptUTF8BytesMix(Object o) throws IOException{
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        HessianOutput hessian1OutputOverlongEncoding = Hessian1OutputEnhance.getHessian1Output(bos);
//        bos.write(67);
//        hessian1OutputOverlongEncoding.getSerializerFactory().setAllowNonSerializable(true);
//        hessian1OutputOverlongEncoding.writeObject(o);
//        hessian1OutputOverlongEncoding.flush();
//        return bos.toByteArray();
//    }
}
 class Hessian2OutputOverlongEncoding extends Hessian2Output {
    public Hessian2OutputOverlongEncoding(OutputStream os) {
        super(os);
    }

    @Override
    public void printString(String v, int strOffset, int length) throws IOException {
        final int SIZE = 8 * 1024;
        int offset = (int) getSuperFieldValue("_offset");
        byte[] buffer = (byte[]) getSuperFieldValue("_buffer");

        for (int i = 0; i < length; i++) {
            if (SIZE <= offset + 16) {
                setSuperFieldValue("_offset", offset);
                flushBuffer();
                offset = (int) getSuperFieldValue("_offset");
            }

            char ch = v.charAt(i + strOffset);

            // 2 bytes UTF-8
            buffer[offset++] = (byte) (0xc0 + (convert(ch)[0] & 0x1f));
            buffer[offset++] = (byte) (0x80 + (convert(ch)[1] & 0x3f));

//            if (ch < 0x80)
//                buffer[offset++] = (byte) (ch);
//            else if (ch < 0x800) {
//                buffer[offset++] = (byte) (0xc0 + ((ch >> 6) & 0x1f));
//                buffer[offset++] = (byte) (0x80 + (ch & 0x3f));
//            }
//            else {
//                buffer[offset++] = (byte) (0xe0 + ((ch >> 12) & 0xf));
//                buffer[offset++] = (byte) (0x80 + ((ch >> 6) & 0x3f));
//                buffer[offset++] = (byte) (0x80 + (ch & 0x3f));
//            }
        }

        setSuperFieldValue("_offset", offset);
    }

    @Override
    public void printString(char[] v, int strOffset, int length) throws IOException {
        int offset = (int) getSuperFieldValue("_offset");
        byte[] buffer = (byte[]) getSuperFieldValue("_buffer");

        for (int i = 0; i < length; i++) {
            if (SIZE <= offset + 16) {
                setSuperFieldValue("_offset", offset);
                flushBuffer();
                offset = (int) getSuperFieldValue("_offset");
            }

            char ch = v[i + strOffset];

            // 2 bytes UTF-8
            buffer[offset++] = (byte) (0xc0 + (convert(ch)[0] & 0x1f));
            buffer[offset++] = (byte) (0x80 + (convert(ch)[1] & 0x3f));
        }

        setSuperFieldValue("_offset", offset);
    }

    public int[] convert(int i) {
        int b1 = ((i >> 6) & 0b11111) | 0b11000000;
        int b2 = (i & 0b111111) | 0b10000000;
        return new int[]{b1, b2};
    }

    public Object getSuperFieldValue(String name) {
        try {
            Field f = this.getClass().getSuperclass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(this);
        } catch (Exception e) {
            return null;
        }
    }

    public void setSuperFieldValue(String name, Object val) {
        try {
            Field f = this.getClass().getSuperclass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(this, val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
