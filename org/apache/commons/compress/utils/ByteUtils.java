package org.apache.commons.compress.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteUtils {

    public interface ByteConsumer {
        void accept(int i) throws IOException;
    }

    public interface ByteSupplier {
        int getAsByte() throws IOException;
    }

    public static class InputStreamByteSupplier implements ByteSupplier {
        private final InputStream is;

        public InputStreamByteSupplier(InputStream is) {
            this.is = is;
        }

        public int getAsByte() throws IOException {
            return this.is.read();
        }
    }

    public static class OutputStreamByteConsumer implements ByteConsumer {
        private final OutputStream os;

        public OutputStreamByteConsumer(OutputStream os) {
            this.os = os;
        }

        public void accept(int b) throws IOException {
            this.os.write(b);
        }
    }

    private ByteUtils() {
    }

    public static long fromLittleEndian(byte[] bytes) {
        return fromLittleEndian(bytes, 0, bytes.length);
    }

    public static long fromLittleEndian(byte[] bytes, int off, int length) {
        checkReadLength(length);
        long l = 0;
        for (int i = 0; i < length; i++) {
            l |= (((long) bytes[off + i]) & 255) << (i * 8);
        }
        return l;
    }

    public static long fromLittleEndian(InputStream in, int length) throws IOException {
        checkReadLength(length);
        long l = 0;
        for (int i = 0; i < length; i++) {
            long b = (long) in.read();
            if (b == -1) {
                throw new IOException("premature end of data");
            }
            l |= b << (i * 8);
        }
        return l;
    }

    public static long fromLittleEndian(ByteSupplier supplier, int length) throws IOException {
        checkReadLength(length);
        long l = 0;
        for (int i = 0; i < length; i++) {
            long b = (long) supplier.getAsByte();
            if (b == -1) {
                throw new IOException("premature end of data");
            }
            l |= b << (i * 8);
        }
        return l;
    }

    public static long fromLittleEndian(DataInput in, int length) throws IOException {
        checkReadLength(length);
        long l = 0;
        for (int i = 0; i < length; i++) {
            l |= ((long) in.readUnsignedByte()) << (i * 8);
        }
        return l;
    }

    public static void toLittleEndian(byte[] b, long value, int off, int length) {
        long num = value;
        for (int i = 0; i < length; i++) {
            b[off + i] = (byte) ((int) (255 & num));
            num >>= 8;
        }
    }

    public static void toLittleEndian(OutputStream out, long value, int length) throws IOException {
        long num = value;
        for (int i = 0; i < length; i++) {
            out.write((int) (255 & num));
            num >>= 8;
        }
    }

    public static void toLittleEndian(ByteConsumer consumer, long value, int length) throws IOException {
        long num = value;
        for (int i = 0; i < length; i++) {
            consumer.accept((int) (255 & num));
            num >>= 8;
        }
    }

    public static void toLittleEndian(DataOutput out, long value, int length) throws IOException {
        long num = value;
        for (int i = 0; i < length; i++) {
            out.write((int) (255 & num));
            num >>= 8;
        }
    }

    private static final void checkReadLength(int length) {
        if (length > 8) {
            throw new IllegalArgumentException("can't read more than eight bytes into a long value");
        }
    }
}
