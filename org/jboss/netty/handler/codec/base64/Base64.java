package org.jboss.netty.handler.codec.base64;

import android.support.v4.media.TransportMediator;
import java.nio.ByteOrder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.HeapChannelBufferFactory;

public final class Base64 {
    private static final byte EQUALS_SIGN = (byte) 61;
    private static final byte EQUALS_SIGN_ENC = (byte) -1;
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte NEW_LINE = (byte) 10;
    private static final byte WHITE_SPACE_ENC = (byte) -5;

    private static byte[] alphabet(Base64Dialect dialect) {
        if (dialect != null) {
            return dialect.alphabet;
        }
        throw new NullPointerException("dialect");
    }

    private static byte[] decodabet(Base64Dialect dialect) {
        if (dialect != null) {
            return dialect.decodabet;
        }
        throw new NullPointerException("dialect");
    }

    private static boolean breakLines(Base64Dialect dialect) {
        if (dialect != null) {
            return dialect.breakLinesByDefault;
        }
        throw new NullPointerException("dialect");
    }

    public static ChannelBuffer encode(ChannelBuffer src) {
        return encode(src, Base64Dialect.STANDARD);
    }

    public static ChannelBuffer encode(ChannelBuffer src, Base64Dialect dialect) {
        return encode(src, breakLines(dialect), dialect);
    }

    public static ChannelBuffer encode(ChannelBuffer src, ChannelBufferFactory bufferFactory) {
        return encode(src, Base64Dialect.STANDARD, bufferFactory);
    }

    public static ChannelBuffer encode(ChannelBuffer src, Base64Dialect dialect, ChannelBufferFactory bufferFactory) {
        return encode(src, breakLines(dialect), dialect, bufferFactory);
    }

    public static ChannelBuffer encode(ChannelBuffer src, boolean breakLines) {
        return encode(src, breakLines, Base64Dialect.STANDARD);
    }

    public static ChannelBuffer encode(ChannelBuffer src, boolean breakLines, Base64Dialect dialect) {
        return encode(src, breakLines, dialect, HeapChannelBufferFactory.getInstance());
    }

    public static ChannelBuffer encode(ChannelBuffer src, boolean breakLines, ChannelBufferFactory bufferFactory) {
        return encode(src, breakLines, Base64Dialect.STANDARD, bufferFactory);
    }

    public static ChannelBuffer encode(ChannelBuffer src, boolean breakLines, Base64Dialect dialect, ChannelBufferFactory bufferFactory) {
        if (src == null) {
            throw new NullPointerException("src");
        }
        ChannelBuffer dest = encode(src, src.readerIndex(), src.readableBytes(), breakLines, dialect, bufferFactory);
        src.readerIndex(src.writerIndex());
        return dest;
    }

    public static ChannelBuffer encode(ChannelBuffer src, int off, int len) {
        return encode(src, off, len, Base64Dialect.STANDARD);
    }

    public static ChannelBuffer encode(ChannelBuffer src, int off, int len, Base64Dialect dialect) {
        return encode(src, off, len, breakLines(dialect), dialect);
    }

    public static ChannelBuffer encode(ChannelBuffer src, int off, int len, ChannelBufferFactory bufferFactory) {
        return encode(src, off, len, Base64Dialect.STANDARD, bufferFactory);
    }

    public static ChannelBuffer encode(ChannelBuffer src, int off, int len, Base64Dialect dialect, ChannelBufferFactory bufferFactory) {
        return encode(src, off, len, breakLines(dialect), dialect, bufferFactory);
    }

    public static ChannelBuffer encode(ChannelBuffer src, int off, int len, boolean breakLines) {
        return encode(src, off, len, breakLines, Base64Dialect.STANDARD);
    }

    public static ChannelBuffer encode(ChannelBuffer src, int off, int len, boolean breakLines, Base64Dialect dialect) {
        return encode(src, off, len, breakLines, dialect, HeapChannelBufferFactory.getInstance());
    }

    public static ChannelBuffer encode(ChannelBuffer src, int off, int len, boolean breakLines, ChannelBufferFactory bufferFactory) {
        return encode(src, off, len, breakLines, Base64Dialect.STANDARD, bufferFactory);
    }

    public static ChannelBuffer encode(ChannelBuffer src, int off, int len, boolean breakLines, Base64Dialect dialect, ChannelBufferFactory bufferFactory) {
        if (src == null) {
            throw new NullPointerException("src");
        } else if (dialect == null) {
            throw new NullPointerException("dialect");
        } else if (bufferFactory == null) {
            throw new NullPointerException("bufferFactory");
        } else {
            int i;
            int len43 = (len * 4) / 3;
            ByteOrder order = src.order();
            int i2 = len43 + (len % 3 > 0 ? 4 : 0);
            if (breakLines) {
                i = len43 / 76;
            } else {
                i = 0;
            }
            ChannelBuffer dest = bufferFactory.getBuffer(order, i + i2);
            int d = 0;
            int e = 0;
            int len2 = len - 2;
            int lineLength = 0;
            while (d < len2) {
                encode3to4(src, d + off, 3, dest, e, dialect);
                lineLength += 4;
                if (breakLines && lineLength == 76) {
                    dest.setByte(e + 4, 10);
                    e++;
                    lineLength = 0;
                }
                d += 3;
                e += 4;
            }
            if (d < len) {
                encode3to4(src, d + off, len - d, dest, e, dialect);
                e += 4;
            }
            return dest.slice(0, e);
        }
    }

    private static void encode3to4(ChannelBuffer src, int srcOffset, int numSigBytes, ChannelBuffer dest, int destOffset, Base64Dialect dialect) {
        int i;
        int i2 = 0;
        byte[] ALPHABET = alphabet(dialect);
        if (numSigBytes > 0) {
            i = (src.getByte(srcOffset) << 24) >>> 8;
        } else {
            i = 0;
        }
        int i3 = (numSigBytes > 1 ? (src.getByte(srcOffset + 1) << 24) >>> 16 : 0) | i;
        if (numSigBytes > 2) {
            i2 = (src.getByte(srcOffset + 2) << 24) >>> 24;
        }
        int inBuff = i3 | i2;
        switch (numSigBytes) {
            case 1:
                dest.setByte(destOffset, ALPHABET[inBuff >>> 18]);
                dest.setByte(destOffset + 1, ALPHABET[(inBuff >>> 12) & 63]);
                dest.setByte(destOffset + 2, 61);
                dest.setByte(destOffset + 3, 61);
                return;
            case 2:
                dest.setByte(destOffset, ALPHABET[inBuff >>> 18]);
                dest.setByte(destOffset + 1, ALPHABET[(inBuff >>> 12) & 63]);
                dest.setByte(destOffset + 2, ALPHABET[(inBuff >>> 6) & 63]);
                dest.setByte(destOffset + 3, 61);
                return;
            case 3:
                dest.setByte(destOffset, ALPHABET[inBuff >>> 18]);
                dest.setByte(destOffset + 1, ALPHABET[(inBuff >>> 12) & 63]);
                dest.setByte(destOffset + 2, ALPHABET[(inBuff >>> 6) & 63]);
                dest.setByte(destOffset + 3, ALPHABET[inBuff & 63]);
                return;
            default:
                return;
        }
    }

    public static ChannelBuffer decode(ChannelBuffer src) {
        return decode(src, Base64Dialect.STANDARD);
    }

    public static ChannelBuffer decode(ChannelBuffer src, Base64Dialect dialect) {
        return decode(src, dialect, HeapChannelBufferFactory.getInstance());
    }

    public static ChannelBuffer decode(ChannelBuffer src, ChannelBufferFactory bufferFactory) {
        return decode(src, Base64Dialect.STANDARD, bufferFactory);
    }

    public static ChannelBuffer decode(ChannelBuffer src, Base64Dialect dialect, ChannelBufferFactory bufferFactory) {
        if (src == null) {
            throw new NullPointerException("src");
        }
        ChannelBuffer dest = decode(src, src.readerIndex(), src.readableBytes(), dialect, bufferFactory);
        src.readerIndex(src.writerIndex());
        return dest;
    }

    public static ChannelBuffer decode(ChannelBuffer src, int off, int len) {
        return decode(src, off, len, Base64Dialect.STANDARD);
    }

    public static ChannelBuffer decode(ChannelBuffer src, int off, int len, Base64Dialect dialect) {
        return decode(src, off, len, dialect, HeapChannelBufferFactory.getInstance());
    }

    public static ChannelBuffer decode(ChannelBuffer src, int off, int len, ChannelBufferFactory bufferFactory) {
        return decode(src, off, len, Base64Dialect.STANDARD, bufferFactory);
    }

    public static ChannelBuffer decode(ChannelBuffer src, int off, int len, Base64Dialect dialect, ChannelBufferFactory bufferFactory) {
        if (src == null) {
            throw new NullPointerException("src");
        } else if (dialect == null) {
            throw new NullPointerException("dialect");
        } else if (bufferFactory == null) {
            throw new NullPointerException("bufferFactory");
        } else {
            int b4Posn;
            byte[] DECODABET = decodabet(dialect);
            ChannelBufferFactory channelBufferFactory = bufferFactory;
            ChannelBuffer dest = channelBufferFactory.getBuffer(src.order(), (len * 3) / 4);
            int outBuffPosn = 0;
            byte[] b4 = new byte[4];
            int i = off;
            int b4Posn2 = 0;
            while (i < off + len) {
                byte sbiCrop = (byte) (src.getByte(i) & TransportMediator.KEYCODE_MEDIA_PAUSE);
                byte sbiDecode = DECODABET[sbiCrop];
                if (sbiDecode >= WHITE_SPACE_ENC) {
                    if (sbiDecode >= EQUALS_SIGN_ENC) {
                        b4Posn = b4Posn2 + 1;
                        b4[b4Posn2] = sbiCrop;
                        if (b4Posn > 3) {
                            outBuffPosn += decode4to3(b4, 0, dest, outBuffPosn, dialect);
                            b4Posn = 0;
                            if (sbiCrop == (byte) 61) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        b4Posn = b4Posn2;
                    }
                    i++;
                    b4Posn2 = b4Posn;
                } else {
                    throw new IllegalArgumentException("bad Base64 input character at " + i + ": " + src.getUnsignedByte(i) + " (decimal)");
                }
            }
            b4Posn = b4Posn2;
            return dest.slice(0, outBuffPosn);
        }
    }

    private static int decode4to3(byte[] src, int srcOffset, ChannelBuffer dest, int destOffset, Base64Dialect dialect) {
        byte[] DECODABET = decodabet(dialect);
        if (src[srcOffset + 2] == (byte) 61) {
            dest.setByte(destOffset, (byte) ((((DECODABET[src[srcOffset]] & 255) << 18) | ((DECODABET[src[srcOffset + 1]] & 255) << 12)) >>> 16));
            return 1;
        } else if (src[srcOffset + 3] == (byte) 61) {
            outBuff = (((DECODABET[src[srcOffset]] & 255) << 18) | ((DECODABET[src[srcOffset + 1]] & 255) << 12)) | ((DECODABET[src[srcOffset + 2]] & 255) << 6);
            dest.setByte(destOffset, (byte) (outBuff >>> 16));
            dest.setByte(destOffset + 1, (byte) (outBuff >>> 8));
            return 2;
        } else {
            try {
                outBuff = ((((DECODABET[src[srcOffset]] & 255) << 18) | ((DECODABET[src[srcOffset + 1]] & 255) << 12)) | ((DECODABET[src[srcOffset + 2]] & 255) << 6)) | (DECODABET[src[srcOffset + 3]] & 255);
                dest.setByte(destOffset, (byte) (outBuff >> 16));
                dest.setByte(destOffset + 1, (byte) (outBuff >> 8));
                dest.setByte(destOffset + 2, (byte) outBuff);
                return 3;
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("not encoded in Base64");
            }
        }
    }

    private Base64() {
    }
}
