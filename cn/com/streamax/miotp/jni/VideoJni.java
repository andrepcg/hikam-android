package cn.com.streamax.miotp.jni;

public class VideoJni {
    public static native int convertByte2Int(byte[] bArr, int i, int i2, int[] iArr);

    public static native int decodeYUV420P2RGB565(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, byte[] bArr4);

    public static native int decodeYuv420P2RgbForTable(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3, int i4, int[] iArr);

    public static native int initRgbTable();

    static {
        initRgbTable();
    }
}
