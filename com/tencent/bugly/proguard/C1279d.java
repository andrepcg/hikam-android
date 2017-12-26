package com.tencent.bugly.proguard;

import java.nio.ByteBuffer;
import java.util.HashMap;

/* compiled from: BUGLY */
public final class C1279d extends C1151c {
    private static HashMap<String, byte[]> f702f = null;
    private static HashMap<String, HashMap<String, byte[]>> f703g = null;
    private C1152f f704e = new C1152f();

    public final <T> void mo2287a(String str, T t) {
        if (str.startsWith(".")) {
            throw new IllegalArgumentException("put name can not startwith . , now is " + str);
        }
        super.mo2287a(str, t);
    }

    public final void mo3279b() {
        super.mo3279b();
        this.f704e.f681a = (short) 3;
    }

    public final byte[] mo2289a() {
        if (this.f704e.f681a != (short) 2) {
            if (this.f704e.f683c == null) {
                this.f704e.f683c = "";
            }
            if (this.f704e.f684d == null) {
                this.f704e.f684d = "";
            }
        } else if (this.f704e.f683c.equals("")) {
            throw new IllegalArgumentException("servantName can not is null");
        } else if (this.f704e.f684d.equals("")) {
            throw new IllegalArgumentException("funcName can not is null");
        }
        C0736i c0736i = new C0736i(0);
        c0736i.m334a(this.b);
        if (this.f704e.f681a == (short) 2) {
            c0736i.m343a(this.a, 0);
        } else {
            c0736i.m343a(this.d, 0);
        }
        this.f704e.f685e = C0738k.m355a(c0736i.m335a());
        c0736i = new C0736i(0);
        c0736i.m334a(this.b);
        this.f704e.mo2284a(c0736i);
        byte[] a = C0738k.m355a(c0736i.m335a());
        int length = a.length;
        ByteBuffer allocate = ByteBuffer.allocate(length + 4);
        allocate.putInt(length + 4).put(a).flip();
        return allocate.array();
    }

    public final void mo2288a(byte[] bArr) {
        if (bArr.length < 4) {
            throw new IllegalArgumentException("decode package must include size head");
        }
        try {
            C0735h c0735h = new C0735h(bArr, 4);
            c0735h.m322a(this.b);
            this.f704e.mo2283a(c0735h);
            HashMap hashMap;
            if (this.f704e.f681a == (short) 3) {
                c0735h = new C0735h(this.f704e.f685e);
                c0735h.m322a(this.b);
                if (f702f == null) {
                    hashMap = new HashMap();
                    f702f = hashMap;
                    hashMap.put("", new byte[0]);
                }
                this.d = c0735h.m326a(f702f, 0, false);
                return;
            }
            c0735h = new C0735h(this.f704e.f685e);
            c0735h.m322a(this.b);
            if (f703g == null) {
                f703g = new HashMap();
                hashMap = new HashMap();
                hashMap.put("", new byte[0]);
                f703g.put("", hashMap);
            }
            this.a = c0735h.m326a(f703g, 0, false);
            HashMap hashMap2 = new HashMap();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public final void m606b(String str) {
        this.f704e.f683c = str;
    }

    public final void m607c(String str) {
        this.f704e.f684d = str;
    }

    public final void m605b(int i) {
        this.f704e.f682b = 1;
    }
}
