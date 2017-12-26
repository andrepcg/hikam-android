package org.jboss.netty.util.internal.jzlib;

final class Adler32 {
    private static final int BASE = 65521;
    private static final int NMAX = 5552;

    static long adler32(long adler, byte[] buf, int index, int len) {
        if (buf == null) {
            return 1;
        }
        long s1 = adler & 65535;
        long s2 = (adler >> 16) & 65535;
        while (len > 0) {
            int k = len < NMAX ? len : NMAX;
            len -= k;
            int index2 = index;
            while (k >= 16) {
                index = index2 + 1;
                s1 += (long) (buf[index2] & 255);
                s2 += s1;
                index2 = index + 1;
                s1 += (long) (buf[index] & 255);
                s2 += s1;
                index = index2 + 1;
                s1 += (long) (buf[index2] & 255);
                s2 += s1;
                index2 = index + 1;
                s1 += (long) (buf[index] & 255);
                s2 += s1;
                index = index2 + 1;
                s1 += (long) (buf[index2] & 255);
                s2 += s1;
                index2 = index + 1;
                s1 += (long) (buf[index] & 255);
                s2 += s1;
                index = index2 + 1;
                s1 += (long) (buf[index2] & 255);
                s2 += s1;
                index2 = index + 1;
                s1 += (long) (buf[index] & 255);
                s2 += s1;
                index = index2 + 1;
                s1 += (long) (buf[index2] & 255);
                s2 += s1;
                index2 = index + 1;
                s1 += (long) (buf[index] & 255);
                s2 += s1;
                index = index2 + 1;
                s1 += (long) (buf[index2] & 255);
                s2 += s1;
                index2 = index + 1;
                s1 += (long) (buf[index] & 255);
                s2 += s1;
                index = index2 + 1;
                s1 += (long) (buf[index2] & 255);
                s2 += s1;
                index2 = index + 1;
                s1 += (long) (buf[index] & 255);
                s2 += s1;
                index = index2 + 1;
                s1 += (long) (buf[index2] & 255);
                s2 += s1;
                index2 = index + 1;
                s1 += (long) (buf[index] & 255);
                s2 += s1;
                k -= 16;
            }
            if (k != 0) {
                do {
                    index = index2;
                    index2 = index + 1;
                    s1 += (long) (buf[index] & 255);
                    s2 += s1;
                    k--;
                } while (k != 0);
            }
            index = index2;
            s1 %= 65521;
            s2 %= 65521;
        }
        return (s2 << 16) | s1;
    }

    private Adler32() {
    }
}
