package com.tencent.bugly.proguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* compiled from: BUGLY */
public final class aj extends C0737j {
    private static ArrayList<ai> f575A = new ArrayList();
    private static Map<String, String> f576B = new HashMap();
    private static Map<String, String> f577C = new HashMap();
    private static Map<String, String> f578v = new HashMap();
    private static ah f579w = new ah();
    private static ag f580x = new ag();
    private static ArrayList<ag> f581y = new ArrayList();
    private static ArrayList<ag> f582z = new ArrayList();
    public String f583a = "";
    public long f584b = 0;
    public String f585c = "";
    public String f586d = "";
    public String f587e = "";
    public String f588f = "";
    public String f589g = "";
    public Map<String, String> f590h = null;
    public String f591i = "";
    public ah f592j = null;
    public int f593k = 0;
    public String f594l = "";
    public String f595m = "";
    public ag f596n = null;
    public ArrayList<ag> f597o = null;
    public ArrayList<ag> f598p = null;
    public ArrayList<ai> f599q = null;
    public Map<String, String> f600r = null;
    public Map<String, String> f601s = null;
    public String f602t = "";
    private boolean f603u = true;

    public final void mo2284a(C0736i c0736i) {
        c0736i.m341a(this.f583a, 0);
        c0736i.m338a(this.f584b, 1);
        c0736i.m341a(this.f585c, 2);
        if (this.f586d != null) {
            c0736i.m341a(this.f586d, 3);
        }
        if (this.f587e != null) {
            c0736i.m341a(this.f587e, 4);
        }
        if (this.f588f != null) {
            c0736i.m341a(this.f588f, 5);
        }
        if (this.f589g != null) {
            c0736i.m341a(this.f589g, 6);
        }
        if (this.f590h != null) {
            c0736i.m343a(this.f590h, 7);
        }
        if (this.f591i != null) {
            c0736i.m341a(this.f591i, 8);
        }
        if (this.f592j != null) {
            c0736i.m339a(this.f592j, 9);
        }
        c0736i.m337a(this.f593k, 10);
        if (this.f594l != null) {
            c0736i.m341a(this.f594l, 11);
        }
        if (this.f595m != null) {
            c0736i.m341a(this.f595m, 12);
        }
        if (this.f596n != null) {
            c0736i.m339a(this.f596n, 13);
        }
        if (this.f597o != null) {
            c0736i.m342a(this.f597o, 14);
        }
        if (this.f598p != null) {
            c0736i.m342a(this.f598p, 15);
        }
        if (this.f599q != null) {
            c0736i.m342a(this.f599q, 16);
        }
        if (this.f600r != null) {
            c0736i.m343a(this.f600r, 17);
        }
        if (this.f601s != null) {
            c0736i.m343a(this.f601s, 18);
        }
        if (this.f602t != null) {
            c0736i.m341a(this.f602t, 19);
        }
        c0736i.m345a(this.f603u, 20);
    }

    static {
        f578v.put("", "");
        f581y.add(new ag());
        f582z.add(new ag());
        f575A.add(new ai());
        f576B.put("", "");
        f577C.put("", "");
    }

    public final void mo2283a(C0735h c0735h) {
        this.f583a = c0735h.m330b(0, true);
        this.f584b = c0735h.m323a(this.f584b, 1, true);
        this.f585c = c0735h.m330b(2, true);
        this.f586d = c0735h.m330b(3, false);
        this.f587e = c0735h.m330b(4, false);
        this.f588f = c0735h.m330b(5, false);
        this.f589g = c0735h.m330b(6, false);
        this.f590h = (Map) c0735h.m325a(f578v, 7, false);
        this.f591i = c0735h.m330b(8, false);
        this.f592j = (ah) c0735h.m324a(f579w, 9, false);
        this.f593k = c0735h.m321a(this.f593k, 10, false);
        this.f594l = c0735h.m330b(11, false);
        this.f595m = c0735h.m330b(12, false);
        this.f596n = (ag) c0735h.m324a(f580x, 13, false);
        this.f597o = (ArrayList) c0735h.m325a(f581y, 14, false);
        this.f598p = (ArrayList) c0735h.m325a(f582z, 15, false);
        this.f599q = (ArrayList) c0735h.m325a(f575A, 16, false);
        this.f600r = (Map) c0735h.m325a(f576B, 17, false);
        this.f601s = (Map) c0735h.m325a(f577C, 18, false);
        this.f602t = c0735h.m330b(19, false);
        boolean z = this.f603u;
        this.f603u = c0735h.m329a(20, false);
    }
}
