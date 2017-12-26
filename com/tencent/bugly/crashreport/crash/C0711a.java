package com.tencent.bugly.crashreport.crash;

/* compiled from: BUGLY */
public final class C0711a implements Comparable<C0711a> {
    public long f242a = -1;
    public long f243b = -1;
    public String f244c = null;
    public boolean f245d = false;
    public boolean f246e = false;
    public int f247f = 0;

    public final /* bridge */ /* synthetic */ int compareTo(Object obj) {
        C0711a c0711a = (C0711a) obj;
        if (c0711a != null) {
            long j = this.f243b - c0711a.f243b;
            if (j <= 0) {
                return j < 0 ? -1 : 0;
            }
        }
        return 1;
    }
}
