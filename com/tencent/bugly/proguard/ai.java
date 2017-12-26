package com.tencent.bugly.proguard;

/* compiled from: BUGLY */
public final class ai extends C0737j implements Cloneable {
    private static byte[] f571d;
    private byte f572a = (byte) 0;
    private String f573b = "";
    private byte[] f574c = null;

    public ai(byte b, String str, byte[] bArr) {
        this.f572a = b;
        this.f573b = str;
        this.f574c = bArr;
    }

    public final void mo2284a(C0736i c0736i) {
        c0736i.m336a(this.f572a, 0);
        c0736i.m341a(this.f573b, 1);
        if (this.f574c != null) {
            c0736i.m346a(this.f574c, 2);
        }
    }

    public final void mo2283a(C0735h c0735h) {
        byte[] bArr;
        this.f572a = c0735h.m320a(this.f572a, 0, true);
        this.f573b = c0735h.m330b(1, true);
        if (f571d == null) {
            bArr = new byte[1];
            f571d = bArr;
            bArr[0] = (byte) 0;
        }
        bArr = f571d;
        this.f574c = c0735h.m331c(2, false);
    }

    public final void mo2285a(StringBuilder stringBuilder, int i) {
    }
}
