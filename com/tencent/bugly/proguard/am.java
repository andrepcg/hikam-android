package com.tencent.bugly.proguard;

import java.util.HashMap;
import java.util.Map;

/* compiled from: BUGLY */
public final class am extends C0737j {
    private static byte[] f632i;
    private static Map<String, String> f633j = new HashMap();
    public byte f634a = (byte) 0;
    public int f635b = 0;
    public byte[] f636c = null;
    public String f637d = "";
    public long f638e = 0;
    public String f639f = "";
    public Map<String, String> f640g = null;
    private String f641h = "";

    public final void mo2284a(C0736i c0736i) {
        c0736i.m336a(this.f634a, 0);
        c0736i.m337a(this.f635b, 1);
        if (this.f636c != null) {
            c0736i.m346a(this.f636c, 2);
        }
        if (this.f637d != null) {
            c0736i.m341a(this.f637d, 3);
        }
        c0736i.m338a(this.f638e, 4);
        if (this.f641h != null) {
            c0736i.m341a(this.f641h, 5);
        }
        if (this.f639f != null) {
            c0736i.m341a(this.f639f, 6);
        }
        if (this.f640g != null) {
            c0736i.m343a(this.f640g, 7);
        }
    }

    static {
        byte[] bArr = new byte[1];
        f632i = bArr;
        bArr[0] = (byte) 0;
        f633j.put("", "");
    }

    public final void mo2283a(C0735h c0735h) {
        this.f634a = c0735h.m320a(this.f634a, 0, true);
        this.f635b = c0735h.m321a(this.f635b, 1, true);
        byte[] bArr = f632i;
        this.f636c = c0735h.m331c(2, false);
        this.f637d = c0735h.m330b(3, false);
        this.f638e = c0735h.m323a(this.f638e, 4, false);
        this.f641h = c0735h.m330b(5, false);
        this.f639f = c0735h.m330b(6, false);
        this.f640g = (Map) c0735h.m325a(f633j, 7, false);
    }
}
