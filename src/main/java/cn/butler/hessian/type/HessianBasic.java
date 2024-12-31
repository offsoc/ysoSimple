package cn.butler.hessian.type;

import com.caucho.hessian.io.*;

import java.util.concurrent.Callable;

public abstract class HessianBasic implements Callable<byte[]> {
    protected final Object object;

    public HessianBasic() {
        object = null;
    }

    public HessianBasic(Object object) {
        this.object = object;
    }

    @Override
    public byte[] call() throws Exception {
        return new byte[0];
    }

    /**
     * Hessian序列化器
     */
    public abstract byte[] serialize(Object o) throws Exception;

    /**
     * Hessian反序列化器
     */
    public abstract Object deserialize(byte[] data) throws Exception;

    /**
     * 序列化没有实现 Serializable 接口的类
     */
    public static class NoWriteReplaceSerializerFactory extends SerializerFactory {

        /**
         * {@inheritDoc}
         * @see com.caucho.hessian.io.SerializerFactory#getObjectSerializer(java.lang.Class)
         */
        @Override
        public Serializer getObjectSerializer (Class<?> cl ) throws HessianProtocolException {
            return super.getObjectSerializer(cl);
        }


        /**
         * {@inheritDoc}
         * @see com.caucho.hessian.io.SerializerFactory#getSerializer(java.lang.Class)
         */
        @Override
        public Serializer getSerializer ( Class cl ) throws HessianProtocolException {
            Serializer serializer = super.getSerializer(cl);

            if ( serializer instanceof WriteReplaceSerializer) {
                return UnsafeSerializer.create(cl);
            }
            return serializer;
        }
    }
}
