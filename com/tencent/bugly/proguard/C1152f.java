package com.tencent.bugly.proguard;

import com.tencent.bugly.crashreport.crash.jni.C0729b;
import java.util.HashMap;
import java.util.Map;

/* compiled from: BUGLY */
public final class C1152f extends C0737j {
    private static byte[] f678k = null;
    private static Map<String, String> f679l = null;
    private static /* synthetic */ boolean f680m;
    public short f681a = (short) 0;
    public int f682b = 0;
    public String f683c = null;
    public String f684d = null;
    public byte[] f685e;
    private byte f686f = (byte) 0;
    private int f687g = 0;
    private int f688h = 0;
    private Map<String, String> f689i;
    private Map<String, String> f690j;

    static {
        boolean z;
        if (C1152f.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        f680m = z;
    }

    public final boolean equals(Object o) {
        C1152f c1152f = (C1152f) o;
        if (C0738k.m351a(1, c1152f.f681a) && C0738k.m351a(1, c1152f.f686f) && C0738k.m351a(1, c1152f.f687g) && C0738k.m351a(1, c1152f.f682b) && C0738k.m353a(Integer.valueOf(1), c1152f.f683c) && C0738k.m353a(Integer.valueOf(1), c1152f.f684d) && C0738k.m353a(Integer.valueOf(1), c1152f.f685e) && C0738k.m351a(1, c1152f.f688h) && C0738k.m353a(Integer.valueOf(1), c1152f.f689i) && C0738k.m353a(Integer.valueOf(1), c1152f.f690j)) {
            return true;
        }
        return false;
    }

    public final Object clone() {
        Object obj = null;
        try {
            obj = super.clone();
        } catch (CloneNotSupportedException e) {
            if (!f680m) {
                throw new AssertionError();
            }
        }
        return obj;
    }

    public final void mo2284a(C0736i c0736i) {
        c0736i.m344a(this.f681a, 1);
        c0736i.m336a(this.f686f, 2);
        c0736i.m337a(this.f687g, 3);
        c0736i.m337a(this.f682b, 4);
        c0736i.m341a(this.f683c, 5);
        c0736i.m341a(this.f684d, 6);
        c0736i.m346a(this.f685e, 7);
        c0736i.m337a(this.f688h, 8);
        c0736i.m343a(this.f689i, 9);
        c0736i.m343a(this.f690j, 10);
    }

    public final void mo2283a(C0735h c0735h) {
        try {
            Map hashMap;
            this.f681a = c0735h.m327a(this.f681a, 1, true);
            this.f686f = c0735h.m320a(this.f686f, 2, true);
            this.f687g = c0735h.m321a(this.f687g, 3, true);
            this.f682b = c0735h.m321a(this.f682b, 4, true);
            this.f683c = c0735h.m330b(5, true);
            this.f684d = c0735h.m330b(6, true);
            if (f678k == null) {
                f678k = new byte[]{(byte) 0};
            }
            byte[] bArr = f678k;
            this.f685e = c0735h.m331c(7, true);
            this.f688h = c0735h.m321a(this.f688h, 8, true);
            if (f679l == null) {
                hashMap = new HashMap();
                f679l = hashMap;
                hashMap.put("", "");
            }
            this.f689i = (Map) c0735h.m325a(f679l, 9, true);
            if (f679l == null) {
                hashMap = new HashMap();
                f679l = hashMap;
                hashMap.put("", "");
            }
            this.f690j = (Map) c0735h.m325a(f679l, 10, true);
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("RequestPacket decode error " + C0732e.m304a(this.f685e));
            throw new RuntimeException(e);
        }
    }

    public final void mo2285a(StringBuilder stringBuilder, int i) {
        C0729b c0729b = new C0729b(stringBuilder, i);
        c0729b.m275a(this.f681a, "iVersion");
        c0729b.m265a(this.f686f, "cPacketType");
        c0729b.m269a(this.f687g, "iMessageType");
        c0729b.m269a(this.f682b, "iRequestId");
        c0729b.m284b(this.f683c, "sServantName");
        c0729b.m284b(this.f684d, "sFuncName");
        c0729b.m277a(this.f685e, "sBuffer");
        c0729b.m269a(this.f688h, "iTimeout");
        c0729b.m274a(this.f689i, "context");
        c0729b.m274a(this.f690j, "status");
    }
}
