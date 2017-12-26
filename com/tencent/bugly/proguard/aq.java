package com.tencent.bugly.proguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* compiled from: BUGLY */
public final class aq extends C0737j implements Cloneable {
    private static ArrayList<ap> f668f;
    private static Map<String, String> f669g;
    public byte f670a = (byte) 0;
    public String f671b = "";
    public String f672c = "";
    public ArrayList<ap> f673d = null;
    public Map<String, String> f674e = null;

    public final void mo2284a(C0736i c0736i) {
        c0736i.m336a(this.f670a, 0);
        if (this.f671b != null) {
            c0736i.m341a(this.f671b, 1);
        }
        if (this.f672c != null) {
            c0736i.m341a(this.f672c, 2);
        }
        if (this.f673d != null) {
            c0736i.m342a(this.f673d, 3);
        }
        if (this.f674e != null) {
            c0736i.m343a(this.f674e, 4);
        }
    }

    public final void mo2283a(C0735h c0735h) {
        this.f670a = c0735h.m320a(this.f670a, 0, true);
        this.f671b = c0735h.m330b(1, false);
        this.f672c = c0735h.m330b(2, false);
        if (f668f == null) {
            f668f = new ArrayList();
            f668f.add(new ap());
        }
        this.f673d = (ArrayList) c0735h.m325a(f668f, 3, false);
        if (f669g == null) {
            f669g = new HashMap();
            f669g.put("", "");
        }
        this.f674e = (Map) c0735h.m325a(f669g, 4, false);
    }

    public final void mo2285a(StringBuilder stringBuilder, int i) {
    }
}
