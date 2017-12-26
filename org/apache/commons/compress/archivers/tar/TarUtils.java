package org.apache.commons.compress.archivers.tar;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.jboss.netty.handler.codec.http.HttpConstants;

public class TarUtils {
    private static final int BYTE_MASK = 255;
    static final ZipEncoding DEFAULT_ENCODING = ZipEncodingHelper.getZipEncoding(null);
    static final ZipEncoding FALLBACK_ENCODING = new C11891();

    static class C11891 implements ZipEncoding {
        C11891() {
        }

        public boolean canEncode(String name) {
            return true;
        }

        public ByteBuffer encode(String name) {
            int length = name.length();
            byte[] buf = new byte[length];
            for (int i = 0; i < length; i++) {
                buf[i] = (byte) name.charAt(i);
            }
            return ByteBuffer.wrap(buf);
        }

        public String decode(byte[] buffer) {
            StringBuilder result = new StringBuilder(buffer.length);
            for (byte b : buffer) {
                if (b == (byte) 0) {
                    break;
                }
                result.append((char) (b & 255));
            }
            return result.toString();
        }
    }

    private TarUtils() {
    }

    public static long parseOctal(byte[] buffer, int offset, int length) {
        long result = 0;
        int end = offset + length;
        int start = offset;
        if (length < 2) {
            throw new IllegalArgumentException("Length " + length + " must be at least 2");
        } else if (buffer[start] == (byte) 0) {
            return 0;
        } else {
            while (start < end && buffer[start] == HttpConstants.SP) {
                start++;
            }
            byte trailer = buffer[end - 1];
            while (start < end && (trailer == (byte) 0 || trailer == HttpConstants.SP)) {
                end--;
                trailer = buffer[end - 1];
            }
            while (start < end) {
                byte currentByte = buffer[start];
                if (currentByte < TarConstants.LF_NORMAL || currentByte > TarConstants.LF_CONTIG) {
                    throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte));
                }
                result = (result << 3) + ((long) (currentByte - 48));
                start++;
            }
            return result;
        }
    }

    public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
        if ((buffer[offset] & 128) == 0) {
            return parseOctal(buffer, offset, length);
        }
        boolean negative = buffer[offset] == (byte) -1;
        if (length < 9) {
            return parseBinaryLong(buffer, offset, length, negative);
        }
        return parseBinaryBigInteger(buffer, offset, length, negative);
    }

    private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
        if (length >= 9) {
            throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
        }
        long val = 0;
        for (int i = 1; i < length; i++) {
            val = (val << 8) + ((long) (buffer[offset + i] & 255));
        }
        if (negative) {
            val = (val - 1) ^ (((long) Math.pow(2.0d, ((double) (length - 1)) * 8.0d)) - 1);
        }
        return negative ? -val : val;
    }

    private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
        byte[] remainder = new byte[(length - 1)];
        System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
        BigInteger val = new BigInteger(remainder);
        if (negative) {
            val = val.add(BigInteger.valueOf(-1)).not();
        }
        if (val.bitLength() <= 63) {
            return negative ? -val.longValue() : val.longValue();
        } else {
            throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
        }
    }

    public static boolean parseBoolean(byte[] buffer, int offset) {
        return buffer[offset] == (byte) 1;
    }

    private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
        return "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + new String(buffer, offset, length).replaceAll("\u0000", "{NUL}") + "' len=" + length;
    }

    public static String parseName(byte[] buffer, int offset, int length) {
        String parseName;
        try {
            parseName = parseName(buffer, offset, length, DEFAULT_ENCODING);
        } catch (IOException e) {
            try {
                parseName = parseName(buffer, offset, length, FALLBACK_ENCODING);
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
        return parseName;
    }

    public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
        int len = length;
        while (len > 0 && buffer[(offset + len) - 1] == (byte) 0) {
            len--;
        }
        if (len <= 0) {
            return "";
        }
        byte[] b = new byte[len];
        System.arraycopy(buffer, offset, b, 0, len);
        return encoding.decode(b);
    }

    public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
        int formatNameBytes;
        try {
            formatNameBytes = formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
        } catch (IOException e) {
            try {
                formatNameBytes = formatNameBytes(name, buf, offset, length, FALLBACK_ENCODING);
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
        return formatNameBytes;
    }

    public static int formatNameBytes(String name, byte[] buf, int offset, int length, ZipEncoding encoding) throws IOException {
        int len = name.length();
        ByteBuffer b = encoding.encode(name);
        while (b.limit() > length && len > 0) {
            len--;
            b = encoding.encode(name.substring(0, len));
        }
        int limit = b.limit() - b.position();
        System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);
        for (int i = limit; i < length; i++) {
            buf[offset + i] = (byte) 0;
        }
        return offset + length;
    }

    public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
        int remaining = length - 1;
        if (value == 0) {
            int remaining2 = remaining - 1;
            buffer[offset + remaining] = TarConstants.LF_NORMAL;
            remaining = remaining2;
        } else {
            long val = value;
            while (remaining >= 0 && val != 0) {
                buffer[offset + remaining] = (byte) (((byte) ((int) (7 & val))) + 48);
                val >>>= 3;
                remaining--;
            }
            if (val != 0) {
                throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length);
            }
        }
        while (remaining >= 0) {
            buffer[offset + remaining] = TarConstants.LF_NORMAL;
            remaining--;
        }
    }

    public static int formatOctalBytes(long value, byte[] buf, int offset, int length) {
        int idx = length - 2;
        formatUnsignedOctalString(value, buf, offset, idx);
        int idx2 = idx + 1;
        buf[offset + idx] = HttpConstants.SP;
        buf[offset + idx2] = (byte) 0;
        return offset + length;
    }

    public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length) {
        int idx = length - 1;
        formatUnsignedOctalString(value, buf, offset, idx);
        buf[offset + idx] = HttpConstants.SP;
        return offset + length;
    }

    public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length) {
        long maxAsOctalChar = length == 8 ? TarConstants.MAXID : TarConstants.MAXSIZE;
        boolean negative = value < 0;
        if (!negative && value <= maxAsOctalChar) {
            return formatLongOctalBytes(value, buf, offset, length);
        }
        if (length < 9) {
            formatLongBinary(value, buf, offset, length, negative);
        }
        formatBigIntegerBinary(value, buf, offset, length, negative);
        buf[offset] = (byte) (negative ? 255 : 128);
        return offset + length;
    }

    private static void formatLongBinary(long value, byte[] buf, int offset, int length, boolean negative) {
        int bits = (length - 1) * 8;
        long max = 1 << bits;
        long val = Math.abs(value);
        if (val >= max) {
            throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field.");
        }
        if (negative) {
            val = ((val ^ (max - 1)) | ((long) (255 << bits))) + 1;
        }
        for (int i = (offset + length) - 1; i >= offset; i--) {
            buf[i] = (byte) ((int) val);
            val >>= 8;
        }
    }

    private static void formatBigIntegerBinary(long value, byte[] buf, int offset, int length, boolean negative) {
        int i = 0;
        byte[] b = BigInteger.valueOf(value).toByteArray();
        int len = b.length;
        int off = (offset + length) - len;
        System.arraycopy(b, 0, buf, off, len);
        if (negative) {
            i = 255;
        }
        byte fill = (byte) i;
        for (int i2 = offset + 1; i2 < off; i2++) {
            buf[i2] = fill;
        }
    }

    public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
        int idx = length - 2;
        formatUnsignedOctalString(value, buf, offset, idx);
        int idx2 = idx + 1;
        buf[offset + idx] = (byte) 0;
        buf[offset + idx2] = HttpConstants.SP;
        return offset + length;
    }

    public static long computeCheckSum(byte[] buf) {
        long sum = 0;
        for (byte element : buf) {
            sum += (long) (element & 255);
        }
        return sum;
    }

    public static boolean verifyCheckSum(byte[] header) {
        long storedSum = parseOctal(header, TarConstants.CHKSUM_OFFSET, 8);
        long unsignedSum = 0;
        long signedSum = 0;
        int i = 0;
        while (i < header.length) {
            byte b = header[i];
            if (TarConstants.CHKSUM_OFFSET <= i && i < 156) {
                b = HttpConstants.SP;
            }
            unsignedSum += (long) (b & 255);
            signedSum += (long) b;
            i++;
        }
        return storedSum == unsignedSum || storedSum == signedSum;
    }
}
