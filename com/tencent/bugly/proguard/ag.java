package com.tencent.bugly.proguard;

/* compiled from: BUGLY */
public final class ag extends C0737j implements Cloneable {
    public String f563a = "";
    public String f564b = "";
    public String f565c = "";
    public String f566d = "";
    private String f567e = "";

    public final void mo2284a(C0736i c0736i) {
        c0736i.m341a(this.f563a, 0);
        if (this.f564b != null) {
            c0736i.m341a(this.f564b, 1);
        }
        if (this.f565c != null) {
            c0736i.m341a(this.f565c, 2);
        }
        if (this.f567e != null) {
            c0736i.m341a(this.f567e, 3);
        }
        if (this.f566d != null) {
            c0736i.m341a(this.f566d, 4);
        }
    }

    public final void mo2283a(C0735h c0735h) {
        this.f563a = c0735h.m330b(0, true);
        this.f564b = c0735h.m330b(1, false);
        this.f565c = c0735h.m330b(2, false);
        this.f567e = c0735h.m330b(3, false);
        this.f566d = c0735h.m330b(4, false);
    }

    public final void mo2285a(StringBuilder stringBuilder, int i) {
    }
}
