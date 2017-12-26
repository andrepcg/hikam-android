package com.tencent.bugly.proguard;

import java.util.ArrayList;

/* compiled from: BUGLY */
public final class ah extends C0737j implements Cloneable {
    private static ArrayList<String> f568c;
    private String f569a = "";
    private ArrayList<String> f570b = null;

    public final void mo2284a(C0736i c0736i) {
        c0736i.m341a(this.f569a, 0);
        if (this.f570b != null) {
            c0736i.m342a(this.f570b, 1);
        }
    }

    public final void mo2283a(C0735h c0735h) {
        this.f569a = c0735h.m330b(0, true);
        if (f568c == null) {
            f568c = new ArrayList();
            f568c.add("");
        }
        this.f570b = (ArrayList) c0735h.m325a(f568c, 1, false);
    }

    public final void mo2285a(StringBuilder stringBuilder, int i) {
    }
}
