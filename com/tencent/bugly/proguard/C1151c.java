package com.tencent.bugly.proguard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/* compiled from: BUGLY */
public class C1151c extends C0730a {
    protected HashMap<String, byte[]> f675d = null;
    private HashMap<String, Object> f676e = new HashMap();
    private C0735h f677f = new C0735h();

    public void mo3279b() {
        this.f675d = new HashMap();
    }

    public <T> void mo2287a(String str, T t) {
        if (this.f675d == null) {
            super.mo2287a(str, (Object) t);
        } else if (str == null) {
            throw new IllegalArgumentException("put key can not is null");
        } else if (t == null) {
            throw new IllegalArgumentException("put value can not is null");
        } else if (t instanceof Set) {
            throw new IllegalArgumentException("can not support Set");
        } else {
            C0736i c0736i = new C0736i();
            c0736i.m334a(this.b);
            c0736i.m340a((Object) t, 0);
            this.f675d.put(str, C0738k.m355a(c0736i.m335a()));
        }
    }

    public final <T> T m594b(String str, T t) throws C0731b {
        T a;
        if (this.f675d != null) {
            if (!this.f675d.containsKey(str)) {
                return null;
            }
            if (this.f676e.containsKey(str)) {
                return this.f676e.get(str);
            }
            try {
                this.f677f.m328a((byte[]) this.f675d.get(str));
                this.f677f.m322a(this.b);
                a = this.f677f.m325a((Object) t, 0, true);
                if (a == null) {
                    return a;
                }
                this.f676e.put(str, a);
                return a;
            } catch (Exception e) {
                throw new C0731b(e);
            }
        } else if (!this.a.containsKey(str)) {
            return null;
        } else {
            if (this.f676e.containsKey(str)) {
                return this.f676e.get(str);
            }
            byte[] bArr;
            byte[] bArr2 = new byte[0];
            Iterator it = ((HashMap) this.a.get(str)).entrySet().iterator();
            if (it.hasNext()) {
                Entry entry = (Entry) it.next();
                entry.getKey();
                bArr = (byte[]) entry.getValue();
            } else {
                bArr = bArr2;
            }
            try {
                this.f677f.m328a(bArr);
                this.f677f.m322a(this.b);
                a = this.f677f.m325a((Object) t, 0, true);
                this.f676e.put(str, a);
                return a;
            } catch (Exception e2) {
                throw new C0731b(e2);
            }
        }
    }

    public byte[] mo2289a() {
        if (this.f675d == null) {
            return super.mo2289a();
        }
        C0736i c0736i = new C0736i(0);
        c0736i.m334a(this.b);
        c0736i.m343a(this.f675d, 0);
        return C0738k.m355a(c0736i.m335a());
    }

    public void mo2288a(byte[] bArr) {
        try {
            super.mo2288a(bArr);
        } catch (Exception e) {
            this.f677f.m328a(bArr);
            this.f677f.m322a(this.b);
            Map hashMap = new HashMap(1);
            hashMap.put("", new byte[0]);
            this.f675d = this.f677f.m326a(hashMap, 0, false);
        }
    }
}
