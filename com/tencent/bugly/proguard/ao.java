package com.tencent.bugly.proguard;

import com.tencent.bugly.crashreport.crash.jni.C0729b;
import java.util.HashMap;
import java.util.Map;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;

/* compiled from: BUGLY */
public final class ao extends C0737j implements Cloneable {
    private static an f644m = new an();
    private static Map<String, String> f645n = new HashMap();
    private static /* synthetic */ boolean f646o;
    public boolean f647a = true;
    public boolean f648b = true;
    public boolean f649c = true;
    public String f650d = "";
    public String f651e = "";
    public an f652f = null;
    public Map<String, String> f653g = null;
    public long f654h = 0;
    public int f655i = 0;
    private String f656j = "";
    private String f657k = "";
    private int f658l = 0;

    static {
        boolean z;
        if (ao.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        f646o = z;
        f645n.put("", "");
    }

    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        ao aoVar = (ao) o;
        if (C0738k.m354a(this.f647a, aoVar.f647a) && C0738k.m354a(this.f648b, aoVar.f648b) && C0738k.m354a(this.f649c, aoVar.f649c) && C0738k.m353a(this.f650d, aoVar.f650d) && C0738k.m353a(this.f651e, aoVar.f651e) && C0738k.m353a(this.f652f, aoVar.f652f) && C0738k.m353a(this.f653g, aoVar.f653g) && C0738k.m352a(this.f654h, aoVar.f654h) && C0738k.m353a(this.f656j, aoVar.f656j) && C0738k.m353a(this.f657k, aoVar.f657k) && C0738k.m351a(this.f658l, aoVar.f658l) && C0738k.m351a(this.f655i, aoVar.f655i)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        try {
            throw new Exception("Need define key first!");
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public final Object clone() {
        Object obj = null;
        try {
            obj = super.clone();
        } catch (CloneNotSupportedException e) {
            if (!f646o) {
                throw new AssertionError();
            }
        }
        return obj;
    }

    public final void mo2284a(C0736i c0736i) {
        c0736i.m345a(this.f647a, 0);
        c0736i.m345a(this.f648b, 1);
        c0736i.m345a(this.f649c, 2);
        if (this.f650d != null) {
            c0736i.m341a(this.f650d, 3);
        }
        if (this.f651e != null) {
            c0736i.m341a(this.f651e, 4);
        }
        if (this.f652f != null) {
            c0736i.m339a(this.f652f, 5);
        }
        if (this.f653g != null) {
            c0736i.m343a(this.f653g, 6);
        }
        c0736i.m338a(this.f654h, 7);
        if (this.f656j != null) {
            c0736i.m341a(this.f656j, 8);
        }
        if (this.f657k != null) {
            c0736i.m341a(this.f657k, 9);
        }
        c0736i.m337a(this.f658l, 10);
        c0736i.m337a(this.f655i, 11);
    }

    public final void mo2283a(C0735h c0735h) {
        boolean z = this.f647a;
        this.f647a = c0735h.m329a(0, true);
        z = this.f648b;
        this.f648b = c0735h.m329a(1, true);
        z = this.f649c;
        this.f649c = c0735h.m329a(2, true);
        this.f650d = c0735h.m330b(3, false);
        this.f651e = c0735h.m330b(4, false);
        this.f652f = (an) c0735h.m324a(f644m, 5, false);
        this.f653g = (Map) c0735h.m325a(f645n, 6, false);
        this.f654h = c0735h.m323a(this.f654h, 7, false);
        this.f656j = c0735h.m330b(8, false);
        this.f657k = c0735h.m330b(9, false);
        this.f658l = c0735h.m321a(this.f658l, 10, false);
        this.f655i = c0735h.m321a(this.f655i, 11, false);
    }

    public final void mo2285a(StringBuilder stringBuilder, int i) {
        C0729b c0729b = new C0729b(stringBuilder, i);
        c0729b.m276a(this.f647a, "enable");
        c0729b.m276a(this.f648b, "enableUserInfo");
        c0729b.m276a(this.f649c, "enableQuery");
        c0729b.m284b(this.f650d, Values.URL);
        c0729b.m284b(this.f651e, "expUrl");
        c0729b.m271a(this.f652f, "security");
        c0729b.m274a(this.f653g, "valueMap");
        c0729b.m270a(this.f654h, "strategylastUpdateTime");
        c0729b.m284b(this.f656j, "httpsUrl");
        c0729b.m284b(this.f657k, "httpsExpUrl");
        c0729b.m269a(this.f658l, "eventRecordCount");
        c0729b.m269a(this.f655i, "eventTimeInterval");
    }
}
