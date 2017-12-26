package com.tencent.bugly.proguard;

import java.util.HashMap;
import java.util.Map;

/* compiled from: BUGLY */
public final class ap extends C0737j {
    private static Map<String, String> f659i = new HashMap();
    public long f660a = 0;
    public byte f661b = (byte) 0;
    public String f662c = "";
    public String f663d = "";
    public String f664e = "";
    public Map<String, String> f665f = null;
    public String f666g = "";
    public boolean f667h = true;

    public final void mo2284a(C0736i c0736i) {
        c0736i.m338a(this.f660a, 0);
        c0736i.m336a(this.f661b, 1);
        if (this.f662c != null) {
            c0736i.m341a(this.f662c, 2);
        }
        if (this.f663d != null) {
            c0736i.m341a(this.f663d, 3);
        }
        if (this.f664e != null) {
            c0736i.m341a(this.f664e, 4);
        }
        if (this.f665f != null) {
            c0736i.m343a(this.f665f, 5);
        }
        if (this.f666g != null) {
            c0736i.m341a(this.f666g, 6);
        }
        c0736i.m345a(this.f667h, 7);
    }

    static {
        f659i.put("", "");
    }

    public final void mo2283a(C0735h c0735h) {
        this.f660a = c0735h.m323a(this.f660a, 0, true);
        this.f661b = c0735h.m320a(this.f661b, 1, true);
        this.f662c = c0735h.m330b(2, false);
        this.f663d = c0735h.m330b(3, false);
        this.f664e = c0735h.m330b(4, false);
        this.f665f = (Map) c0735h.m325a(f659i, 5, false);
        this.f666g = c0735h.m330b(6, false);
        boolean z = this.f667h;
        this.f667h = c0735h.m329a(7, false);
    }
}
