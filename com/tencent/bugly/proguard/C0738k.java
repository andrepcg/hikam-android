package com.tencent.bugly.proguard;

import java.nio.ByteBuffer;
import org.apache.commons.compress.archivers.tar.TarConstants;

/* compiled from: BUGLY */
public final class C0738k {
    public static boolean m354a(boolean z, boolean z2) {
        return z == z2;
    }

    public static boolean m351a(int i, int i2) {
        return i == i2;
    }

    public static boolean m352a(long j, long j2) {
        return j == j2;
    }

    public static boolean m353a(Object obj, Object obj2) {
        return obj.equals(obj2);
    }

    public static byte[] m355a(ByteBuffer byteBuffer) {
        Object obj = new byte[byteBuffer.position()];
        System.arraycopy(byteBuffer.array(), 0, obj, 0, obj.length);
        return obj;
    }

    static {
        byte[] bArr = new byte[]{TarConstants.LF_NORMAL, TarConstants.LF_LINK, TarConstants.LF_SYMLINK, TarConstants.LF_CHR, TarConstants.LF_BLK, TarConstants.LF_DIR, TarConstants.LF_FIFO, TarConstants.LF_CONTIG, (byte) 56, (byte) 57, (byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70};
        byte[] bArr2 = new byte[256];
        byte[] bArr3 = new byte[256];
        for (int i = 0; i < 256; i++) {
            bArr2[i] = bArr[i >>> 4];
            bArr3[i] = bArr[i & 15];
        }
    }
}
