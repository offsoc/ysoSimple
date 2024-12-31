package cn.butler.hessian;

import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SerializerFactory;
import flex.messaging.io.StatusInfoProxy;
import java.io.IOException;
import java.io.OutputStream;
import java.util.IdentityHashMap;
import java.util.Random;

public class Hessian1OutputEnhance {
    public OutputStream os;
    public int overlongMode;

    public Hessian1OutputEnhance(OutputStream os) {
        this.os = os;
        this.overlongMode = 3;
    }

    public static HessianOutput getHessian1Output(OutputStream os) throws IOException {
        return new Hessian1OutputEnhance(os).build();
    }

    public HessianOutput build() throws IOException {
        return new InnerHessian1Output(this.os);
    }

    public class InnerHessian1Output extends HessianOutput {
        protected OutputStream os;
        private IdentityHashMap _refs;
        private int _version = 1;

        public InnerHessian1Output(OutputStream os) {
            init(os);
        }

        public InnerHessian1Output() {
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void init(OutputStream os) {
            this.os = os;
            this._refs = null;
            if (this._serializerFactory == null) {
                this._serializerFactory = new SerializerFactory();
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput
        public void setVersion(int version) {
            this._version = version;
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void call(String method, Object[] args) throws IOException {
            int length = args != null ? args.length : 0;
            startCall(method, length);
            for (int i = 0; i < length; i++) {
                writeObject(args[i]);
            }
            completeCall();
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void startCall(String method, int length) throws IOException {
            this.os.write(99);
            this.os.write(this._version);
            this.os.write(0);
            this.os.write(109);
            int len = method.length();
            getOverlongSizeNotCalc$(method);
            this.os.write(len >> 8);
            this.os.write(len);
            printString(method, 0, len);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void startCall() throws IOException {
            this.os.write(99);
            this.os.write(0);
            this.os.write(1);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeMethod(String method) throws IOException {
            this.os.write(109);
            int len = method.length();
            this.os.write(len >> 8);
            this.os.write(len);
            printString(method, 0, len);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void completeCall() throws IOException {
            this.os.write(122);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void startReply() throws IOException {
            this.os.write(114);
            this.os.write(1);
            this.os.write(0);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void completeReply() throws IOException {
            this.os.write(122);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeHeader(String name) throws IOException {
            int len = name.length();
            this.os.write(72);
            this.os.write(len >> 8);
            this.os.write(len);
            printString(name, 0, name.length());
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeFault(String code, String message, Object detail) throws IOException {
            this.os.write(114);
            this.os.write(1);
            this.os.write(0);
            this.os.write(102);
            writeString(StatusInfoProxy.CODE);
            writeString(code);
            writeString("message");
            writeString(message);
            if (detail != null) {
                writeString("detail");
                writeObject(detail);
            }
            this.os.write(122);
            this.os.write(122);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeObject(Object object) throws IOException {
            if (object == null) {
                writeNull();
                return;
            }
            Serializer serializer = this._serializerFactory.getSerializer(object.getClass());
            serializer.writeObject(object, this);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public boolean writeListBegin(int length, String type) throws IOException {
            this.os.write(86);
            if (type != null) {
                this.os.write(116);
                printLenString(type);
            }
            if (length >= 0) {
                this.os.write(108);
                this.os.write(length >> 24);
                this.os.write(length >> 16);
                this.os.write(length >> 8);
                this.os.write(length);
                return true;
            }
            return true;
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeListEnd() throws IOException {
            this.os.write(122);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeMapBegin(String type) throws IOException {
            this.os.write(77);
            this.os.write(116);
            printLenString(type);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeMapEnd() throws IOException {
            this.os.write(122);
        }

        @Override // com.caucho.hessian.io.HessianOutput
        public void writeRemote(String type, String url) throws IOException {
            this.os.write(114);
            this.os.write(116);
            printLenString(type);
            this.os.write(83);
            printLenString(url);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeBoolean(boolean value) throws IOException {
            if (value) {
                this.os.write(84);
            } else {
                this.os.write(70);
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeInt(int value) throws IOException {
            this.os.write(73);
            this.os.write(value >> 24);
            this.os.write(value >> 16);
            this.os.write(value >> 8);
            this.os.write(value);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeLong(long value) throws IOException {
            this.os.write(76);
            this.os.write((byte) (value >> 56));
            this.os.write((byte) (value >> 48));
            this.os.write((byte) (value >> 40));
            this.os.write((byte) (value >> 32));
            this.os.write((byte) (value >> 24));
            this.os.write((byte) (value >> 16));
            this.os.write((byte) (value >> 8));
            this.os.write((byte) value);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeDouble(double value) throws IOException {
            long bits = Double.doubleToLongBits(value);
            this.os.write(68);
            this.os.write((byte) (bits >> 56));
            this.os.write((byte) (bits >> 48));
            this.os.write((byte) (bits >> 40));
            this.os.write((byte) (bits >> 32));
            this.os.write((byte) (bits >> 24));
            this.os.write((byte) (bits >> 16));
            this.os.write((byte) (bits >> 8));
            this.os.write((byte) bits);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeUTCDate(long time) throws IOException {
            this.os.write(100);
            this.os.write((byte) (time >> 56));
            this.os.write((byte) (time >> 48));
            this.os.write((byte) (time >> 40));
            this.os.write((byte) (time >> 32));
            this.os.write((byte) (time >> 24));
            this.os.write((byte) (time >> 16));
            this.os.write((byte) (time >> 8));
            this.os.write((byte) time);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeNull() throws IOException {
            this.os.write(78);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeString(String value) throws IOException {
            if (value == null) {
                this.os.write(78);
                return;
            }
            int length = value.length();
            int i = 0;
            while (true) {
                int offset = i;
                if (length > 32768) {
                    int sublen = 32768;
                    char tail = value.charAt((offset + 32768) - 1);
                    if (55296 <= tail && tail <= 56319) {
                        sublen = 32768 - 1;
                    }
                    this.os.write(115);
                    this.os.write(sublen >> 8);
                    this.os.write(sublen);
                    printString(value, offset, sublen);
                    length -= sublen;
                    i = offset + sublen;
                } else {
                    this.os.write(83);
                    this.os.write(length >> 8);
                    this.os.write(length);
                    printString(value, offset, length);
                    return;
                }
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeString(char[] buffer, int offset, int length) throws IOException {
            if (buffer == null) {
                this.os.write(78);
                return;
            }
            while (length > 32768) {
                int sublen = 32768;
                char tail = buffer[(offset + 32768) - 1];
                if (55296 <= tail && tail <= 56319) {
                    sublen = 32768 - 1;
                }
                this.os.write(115);
                this.os.write(sublen >> 8);
                this.os.write(sublen);
                printString(buffer, offset, sublen);
                length -= sublen;
                offset += sublen;
            }
            this.os.write(83);
            this.os.write(length >> 8);
            this.os.write(length);
            printString(buffer, offset, length);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeBytes(byte[] buffer) throws IOException {
            if (buffer == null) {
                this.os.write(78);
            } else {
                writeBytes(buffer, 0, buffer.length);
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
            if (buffer == null) {
                this.os.write(78);
                return;
            }
            while (length > 32768) {
                this.os.write(98);
                this.os.write(32768 >> 8);
                this.os.write(32768);
                this.os.write(buffer, offset, 32768);
                length -= 32768;
                offset += 32768;
            }
            this.os.write(66);
            this.os.write(length >> 8);
            this.os.write(length);
            this.os.write(buffer, offset, length);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeByteBufferStart() throws IOException {
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeByteBufferPart(byte[] buffer, int offset, int length) throws IOException {
            while (length > 0) {
                int sublen = length;
                if (32768 < sublen) {
                    sublen = 32768;
                }
                this.os.write(98);
                this.os.write(sublen >> 8);
                this.os.write(sublen);
                this.os.write(buffer, offset, sublen);
                length -= sublen;
                offset += sublen;
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeByteBufferEnd(byte[] buffer, int offset, int length) throws IOException {
            writeBytes(buffer, offset, length);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void writeRef(int value) throws IOException {
            this.os.write(82);
            this.os.write(value >> 24);
            this.os.write(value >> 16);
            this.os.write(value >> 8);
            this.os.write(value);
        }

        @Override // com.caucho.hessian.io.HessianOutput
        public void writePlaceholder() throws IOException {
            this.os.write(80);
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public boolean addRef(Object object) throws IOException {
            if (this._refs == null) {
                this._refs = new IdentityHashMap();
            }
            Integer ref = (Integer) this._refs.get(object);
            if (ref != null) {
                int value = ref.intValue();
                writeRef(value);
                return true;
            }
            this._refs.put(object, new Integer(this._refs.size()));
            return false;
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public int getRef(Object obj) {
            Integer value;
            if (this._refs == null || (value = (Integer) this._refs.get(obj)) == null) {
                return -1;
            }
            return value.intValue();
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void resetReferences() {
            if (this._refs != null) {
                this._refs.clear();
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public boolean removeRef(Object obj) throws IOException {
            if (this._refs != null) {
                this._refs.remove(obj);
                return true;
            }
            return false;
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public boolean replaceRef(Object oldRef, Object newRef) throws IOException {
            Integer value = (Integer) this._refs.remove(oldRef);
            if (value != null) {
                this._refs.put(newRef, value);
                return true;
            }
            return false;
        }

        @Override // com.caucho.hessian.io.HessianOutput
        public void printLenString(String v) throws IOException {
            if (v == null) {
                this.os.write(0);
                this.os.write(0);
                return;
            }
            int len = v.length();
            this.os.write(len >> 8);
            this.os.write(len);
            printString(v, 0, len);
        }

        @Override // com.caucho.hessian.io.HessianOutput
        public void printString(String v) throws IOException {
            printString(v, 0, v.length());
        }

        @Override // com.caucho.hessian.io.HessianOutput
        public void printString(String v, int offset, int length) throws IOException {
            int threeByteCount = getThreeBytesSize(length);
            for (int i = 0; i < length; i++) {
                char ch2 = v.charAt(i + offset);
                if (Hessian1OutputEnhance.this.overlongMode == 1) {
                    if (randomCall(length - i, threeByteCount)) {
                        this.os.write((byte) (224 + (convert3(ch2)[0] & 15)));
                        this.os.write((byte) (128 + (convert3(ch2)[1] & 63)));
                        this.os.write((byte) (128 + (convert3(ch2)[2] & 63)));
                    } else {
                        this.os.write((byte) (192 + (convert2(ch2)[0] & 31)));
                        this.os.write((byte) (128 + (convert2(ch2)[1] & 63)));
                    }
                } else if (Hessian1OutputEnhance.this.overlongMode == 2) {
                    this.os.write((byte) (192 + (convert2(ch2)[0] & 31)));
                    this.os.write((byte) (128 + (convert2(ch2)[1] & 63)));
                } else if (Hessian1OutputEnhance.this.overlongMode == 3) {
                    this.os.write((byte) (224 + (convert3(ch2)[0] & 15)));
                    this.os.write((byte) (128 + (convert3(ch2)[1] & 63)));
                    this.os.write((byte) (128 + (convert3(ch2)[2] & 63)));
                } else {
                    this.os.write((byte) (224 + (convert3(ch2)[0] & 15)));
                    this.os.write((byte) (128 + (convert3(ch2)[1] & 63)));
                    this.os.write((byte) (128 + (convert3(ch2)[2] & 63)));
                }
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput
        public void printString(char[] v, int offset, int length) throws IOException {
            int threeByteCount = getThreeBytesSize(length);
            for (int i = 0; i < length; i++) {
                char ch2 = v[i + offset];
                if (Hessian1OutputEnhance.this.overlongMode == 1) {
                    if (randomCall(length - i, threeByteCount)) {
                        this.os.write((byte) (224 + (convert3(ch2)[0] & 15)));
                        this.os.write((byte) (128 + (convert3(ch2)[1] & 63)));
                        this.os.write((byte) (128 + (convert3(ch2)[2] & 63)));
                    } else {
                        this.os.write((byte) (192 + (convert2(ch2)[0] & 31)));
                        this.os.write((byte) (128 + (convert2(ch2)[1] & 63)));
                    }
                } else if (Hessian1OutputEnhance.this.overlongMode == 2) {
                    this.os.write((byte) (192 + (convert2(ch2)[0] & 31)));
                    this.os.write((byte) (128 + (convert2(ch2)[1] & 63)));
                } else if (Hessian1OutputEnhance.this.overlongMode == 3) {
                    this.os.write((byte) (224 + (convert3(ch2)[0] & 15)));
                    this.os.write((byte) (128 + (convert3(ch2)[1] & 63)));
                    this.os.write((byte) (128 + (convert3(ch2)[2] & 63)));
                } else {
                    this.os.write((byte) (224 + (convert3(ch2)[0] & 15)));
                    this.os.write((byte) (128 + (convert3(ch2)[1] & 63)));
                    this.os.write((byte) (128 + (convert3(ch2)[2] & 63)));
                }
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void flush() throws IOException {
            if (this.os != null) {
                this.os.flush();
            }
        }

        @Override // com.caucho.hessian.io.HessianOutput, com.caucho.hessian.io.AbstractHessianOutput
        public void close() throws IOException {
            if (this.os != null) {
                this.os.flush();
            }
        }

        public boolean randomCall(int remainingPositions, int remainingCalls) {
            if (remainingCalls == 0) {
                return false;
            }
            long seed = System.currentTimeMillis();
            Random rand = new Random(seed);
            double probability = remainingCalls / remainingPositions;
            double randomProbability = rand.nextDouble();
            return randomProbability < probability;
        }

        public int getOverlongSizeNotCalc$(String string) {
            int length = string.length();
            int threeBytesSize = getThreeBytesSize(length);
            int twoBytesSize = length - threeBytesSize;
            return (threeBytesSize * 3) + (twoBytesSize * 2);
        }

        public int getThreeBytesSize(int length) {
            int threeLen;
            switch (Hessian1OutputEnhance.this.overlongMode) {
                case 1:
                    threeLen = (length / 2) + 1;
                    break;
                case 2:
                    threeLen = 0;
                    break;
                case 3:
                    threeLen = length;
                    break;
                default:
                    threeLen = 0;
                    break;
            }
            return threeLen;
        }

        public int[] convert2(int i) {
            int b1 = ((i >> 6) & 31) | 192;
            int b2 = (i & 63) | 128;
            return new int[]{b1, b2};
        }

        public int[] convert3(int i) {
            int b1 = ((i >> 12) & 15) | 224;
            int b2 = ((i >> 6) & 63) | 128;
            int b3 = (i & 63) | 128;
            return new int[]{b1, b2, b3};
        }
    }
}
