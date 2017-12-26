package com.tencent.bugly.proguard;

import java.util.ArrayList;

/* compiled from: BUGLY */
public final class ak extends C0737j implements Cloneable {
    private static ArrayList<aj> f604b;
    public ArrayList<aj> f605a = null;

    public final void mo2284a(C0736i c0736i) {
        c0736i.m342a(this.f605a, 0);
    }

    public final void mo2283a(C0735h c0735h) {
        if (f604b == null) {
            f604b = new ArrayList();
            f604b.add(new aj());
        }
        this.f605a = (ArrayList) c0735h.m325a(f604b, 0, true);
    }

    public final void mo2285a(StringBuilder stringBuilder, int i) {
    }
}
