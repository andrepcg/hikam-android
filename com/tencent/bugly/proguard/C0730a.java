package com.tencent.bugly.proguard;

import android.content.Context;
import com.tencent.bugly.C0692a;
import com.tencent.bugly.C0693b;
import com.tencent.bugly.crashreport.biz.UserInfoBean;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.C0706b;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/* compiled from: BUGLY */
public class C0730a {
    protected HashMap<String, HashMap<String, byte[]>> f351a = new HashMap();
    protected String f352b;
    C0735h f353c;
    private HashMap<String, Object> f354d;

    public static af m285a(int i) {
        if (i == 1) {
            return new ae();
        }
        if (i == 3) {
            return new ad();
        }
        return null;
    }

    C0730a() {
        HashMap hashMap = new HashMap();
        this.f354d = new HashMap();
        this.f352b = "GBK";
        this.f353c = new C0735h();
    }

    public static ap m288a(UserInfoBean userInfoBean) {
        if (userInfoBean == null) {
            return null;
        }
        ap apVar = new ap();
        apVar.f660a = userInfoBean.f57e;
        apVar.f664e = userInfoBean.f62j;
        apVar.f663d = userInfoBean.f55c;
        apVar.f662c = userInfoBean.f56d;
        apVar.f666g = C0705a.m85b().m121i();
        apVar.f667h = userInfoBean.f67o == 1;
        switch (userInfoBean.f54b) {
            case 1:
                apVar.f661b = (byte) 1;
                break;
            case 2:
                apVar.f661b = (byte) 4;
                break;
            case 3:
                apVar.f661b = (byte) 2;
                break;
            case 4:
                apVar.f661b = (byte) 3;
                break;
            default:
                if (userInfoBean.f54b >= 10 && userInfoBean.f54b < 20) {
                    apVar.f661b = (byte) userInfoBean.f54b;
                    break;
                }
                C0757w.m462e("unknown uinfo type %d ", Integer.valueOf(userInfoBean.f54b));
                return null;
                break;
        }
        apVar.f665f = new HashMap();
        if (userInfoBean.f68p >= 0) {
            apVar.f665f.put("C01", userInfoBean.f68p);
        }
        if (userInfoBean.f69q >= 0) {
            apVar.f665f.put("C02", userInfoBean.f69q);
        }
        if (userInfoBean.f70r != null && userInfoBean.f70r.size() > 0) {
            for (Entry entry : userInfoBean.f70r.entrySet()) {
                apVar.f665f.put("C03_" + ((String) entry.getKey()), entry.getValue());
            }
        }
        if (userInfoBean.f71s != null && userInfoBean.f71s.size() > 0) {
            for (Entry entry2 : userInfoBean.f71s.entrySet()) {
                apVar.f665f.put("C04_" + ((String) entry2.getKey()), entry2.getValue());
            }
        }
        apVar.f665f.put("A36", (!userInfoBean.f64l));
        apVar.f665f.put("F02", userInfoBean.f59g);
        apVar.f665f.put("F03", userInfoBean.f60h);
        apVar.f665f.put("F04", userInfoBean.f62j);
        apVar.f665f.put("F05", userInfoBean.f61i);
        apVar.f665f.put("F06", userInfoBean.f65m);
        apVar.f665f.put("F10", userInfoBean.f63k);
        C0757w.m460c("summary type %d vm:%d", Byte.valueOf(apVar.f661b), Integer.valueOf(apVar.f665f.size()));
        return apVar;
    }

    public void mo2286a(String str) {
        this.f352b = str;
    }

    public static String m291a(ArrayList<String> arrayList) {
        int i;
        StringBuffer stringBuffer = new StringBuffer();
        for (i = 0; i < arrayList.size(); i++) {
            Object obj = (String) arrayList.get(i);
            if (obj.equals("java.lang.Integer") || obj.equals("int")) {
                obj = "int32";
            } else if (obj.equals("java.lang.Boolean") || obj.equals("boolean")) {
                obj = "bool";
            } else if (obj.equals("java.lang.Byte") || obj.equals("byte")) {
                obj = "char";
            } else if (obj.equals("java.lang.Double") || obj.equals("double")) {
                obj = "double";
            } else if (obj.equals("java.lang.Float") || obj.equals("float")) {
                obj = "float";
            } else if (obj.equals("java.lang.Long") || obj.equals("long")) {
                obj = "int64";
            } else if (obj.equals("java.lang.Short") || obj.equals("short")) {
                obj = "short";
            } else if (obj.equals("java.lang.Character")) {
                throw new IllegalArgumentException("can not support java.lang.Character");
            } else if (obj.equals("java.lang.String")) {
                obj = "string";
            } else if (obj.equals("java.util.List")) {
                obj = "list";
            } else if (obj.equals("java.util.Map")) {
                obj = "map";
            }
            arrayList.set(i, obj);
        }
        Collections.reverse(arrayList);
        for (i = 0; i < arrayList.size(); i++) {
            String str = (String) arrayList.get(i);
            if (str.equals("list")) {
                arrayList.set(i - 1, "<" + ((String) arrayList.get(i - 1)));
                arrayList.set(0, ((String) arrayList.get(0)) + ">");
            } else if (str.equals("map")) {
                arrayList.set(i - 1, "<" + ((String) arrayList.get(i - 1)) + ",");
                arrayList.set(0, ((String) arrayList.get(0)) + ">");
            } else if (str.equals("Array")) {
                arrayList.set(i - 1, "<" + ((String) arrayList.get(i - 1)));
                arrayList.set(0, ((String) arrayList.get(0)) + ">");
            }
        }
        Collections.reverse(arrayList);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            stringBuffer.append((String) it.next());
        }
        return stringBuffer.toString();
    }

    public <T> void mo2287a(String str, T t) {
        if (str == null) {
            throw new IllegalArgumentException("put key can not is null");
        } else if (t == null) {
            throw new IllegalArgumentException("put value can not is null");
        } else if (t instanceof Set) {
            throw new IllegalArgumentException("can not support Set");
        } else {
            C0736i c0736i = new C0736i();
            c0736i.m334a(this.f352b);
            c0736i.m340a((Object) t, 0);
            Object a = C0738k.m355a(c0736i.m335a());
            HashMap hashMap = new HashMap(1);
            ArrayList arrayList = new ArrayList(1);
            m292a(arrayList, (Object) t);
            hashMap.put(C0730a.m291a(arrayList), a);
            this.f354d.remove(str);
            this.f351a.put(str, hashMap);
        }
    }

    public static aq m289a(List<UserInfoBean> list, int i) {
        if (list == null || list.size() == 0) {
            return null;
        }
        C0705a b = C0705a.m85b();
        if (b == null) {
            return null;
        }
        b.m132t();
        aq aqVar = new aq();
        aqVar.f671b = b.f131d;
        aqVar.f672c = b.m120h();
        ArrayList arrayList = new ArrayList();
        for (UserInfoBean a : list) {
            ap a2 = C0730a.m288a(a);
            if (a2 != null) {
                arrayList.add(a2);
            }
        }
        aqVar.f673d = arrayList;
        aqVar.f674e = new HashMap();
        aqVar.f674e.put("A7", b.f133f);
        aqVar.f674e.put("A6", b.m131s());
        aqVar.f674e.put("A5", b.m130r());
        aqVar.f674e.put("A2", b.m128p());
        aqVar.f674e.put("A1", b.m128p());
        aqVar.f674e.put("A24", b.f135h);
        aqVar.f674e.put("A17", b.m129q());
        aqVar.f674e.put("A15", b.m135w());
        aqVar.f674e.put("A13", b.m136x());
        aqVar.f674e.put("F08", b.f149v);
        aqVar.f674e.put("F09", b.f150w);
        Map E = b.m91E();
        if (E != null && E.size() > 0) {
            for (Entry entry : E.entrySet()) {
                aqVar.f674e.put("C04_" + ((String) entry.getKey()), entry.getValue());
            }
        }
        switch (i) {
            case 1:
                aqVar.f670a = (byte) 1;
                break;
            case 2:
                aqVar.f670a = (byte) 2;
                break;
            default:
                C0757w.m462e("unknown up type %d ", Integer.valueOf(i));
                return null;
        }
        return aqVar;
    }

    public static <T extends C0737j> T m290a(byte[] bArr, Class<T> cls) {
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        try {
            C0737j c0737j = (C0737j) cls.newInstance();
            C0735h c0735h = new C0735h(bArr);
            c0735h.m322a("utf-8");
            c0737j.mo2283a(c0735h);
            return c0737j;
        } catch (Throwable th) {
            if (!C0757w.m459b(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    public static al m286a(Context context, int i, byte[] bArr) {
        C0705a b = C0705a.m85b();
        StrategyBean c = C0709a.m169a().m177c();
        if (b == null || c == null) {
            C0757w.m462e("Can not create request pkg for parameters is invalid.", new Object[0]);
            return null;
        }
        try {
            al alVar = new al();
            synchronized (b) {
                alVar.f608a = 1;
                alVar.f609b = b.m116f();
                alVar.f610c = b.f130c;
                alVar.f611d = b.f137j;
                alVar.f612e = b.f139l;
                b.getClass();
                alVar.f613f = "2.4.0";
                alVar.f614g = i;
                alVar.f615h = bArr == null ? "".getBytes() : bArr;
                alVar.f616i = b.f134g;
                alVar.f617j = b.f135h;
                alVar.f618k = new HashMap();
                alVar.f619l = b.m114e();
                alVar.f620m = c.f171p;
                alVar.f622o = b.m120h();
                alVar.f623p = C0706b.m149e(context);
                alVar.f624q = System.currentTimeMillis();
                alVar.f625r = b.m123k();
                alVar.f626s = b.m122j();
                alVar.f627t = b.m125m();
                alVar.f628u = b.m124l();
                alVar.f629v = b.m126n();
                alVar.f630w = alVar.f623p;
                b.getClass();
                alVar.f621n = "com.tencent.bugly";
                alVar.f618k.put("A26", b.m137y());
                alVar.f618k.put("F11", b.f153z);
                alVar.f618k.put("F12", b.f152y);
                alVar.f618k.put("G1", b.m133u());
                alVar.f618k.put("G2", b.m96K());
                alVar.f618k.put("G3", b.m97L());
                alVar.f618k.put("G4", b.m98M());
                alVar.f618k.put("G5", b.m99N());
                alVar.f618k.put("G6", b.m100O());
                alVar.f618k.put("G7", Long.toString(b.m101P()));
                alVar.f618k.put("D3", b.f138k);
                if (C0693b.f46b != null) {
                    for (C0692a c0692a : C0693b.f46b) {
                        if (!(c0692a.versionKey == null || c0692a.version == null)) {
                            alVar.f618k.put(c0692a.versionKey, c0692a.version);
                        }
                    }
                }
            }
            C0753t a = C0753t.m412a();
            if (!(a == null || a.f423a || bArr == null)) {
                alVar.f615h = C0761y.m507a(alVar.f615h, 2, 1, c.f176u);
                if (alVar.f615h == null) {
                    C0757w.m462e("reqPkg sbuffer error!", new Object[0]);
                    return null;
                }
            }
            Map D = b.m90D();
            if (D != null) {
                for (Entry entry : D.entrySet()) {
                    alVar.f618k.put(entry.getKey(), entry.getValue());
                }
            }
            return alVar;
        } catch (Throwable th) {
            if (!C0757w.m459b(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    private void m292a(ArrayList<String> arrayList, Object obj) {
        if (obj.getClass().isArray()) {
            if (!obj.getClass().getComponentType().toString().equals("byte")) {
                throw new IllegalArgumentException("only byte[] is supported");
            } else if (Array.getLength(obj) > 0) {
                arrayList.add("java.util.List");
                m292a((ArrayList) arrayList, Array.get(obj, 0));
            } else {
                arrayList.add("Array");
                arrayList.add("?");
            }
        } else if (obj instanceof Array) {
            throw new IllegalArgumentException("can not support Array, please use List");
        } else if (obj instanceof List) {
            arrayList.add("java.util.List");
            List list = (List) obj;
            if (list.size() > 0) {
                m292a((ArrayList) arrayList, list.get(0));
            } else {
                arrayList.add("?");
            }
        } else if (obj instanceof Map) {
            arrayList.add("java.util.Map");
            Map map = (Map) obj;
            if (map.size() > 0) {
                Object next = map.keySet().iterator().next();
                Object obj2 = map.get(next);
                arrayList.add(next.getClass().getName());
                m292a((ArrayList) arrayList, obj2);
                return;
            }
            arrayList.add("?");
            arrayList.add("?");
        } else {
            arrayList.add(obj.getClass().getName());
        }
    }

    public byte[] mo2289a() {
        C0736i c0736i = new C0736i(0);
        c0736i.m334a(this.f352b);
        c0736i.m343a(this.f351a, 0);
        return C0738k.m355a(c0736i.m335a());
    }

    public void mo2288a(byte[] bArr) {
        this.f353c.m328a(bArr);
        this.f353c.m322a(this.f352b);
        Map hashMap = new HashMap(1);
        HashMap hashMap2 = new HashMap(1);
        hashMap2.put("", new byte[0]);
        hashMap.put("", hashMap2);
        this.f351a = this.f353c.m326a(hashMap, 0, false);
    }

    public static byte[] m293a(al alVar) {
        try {
            C1279d c1279d = new C1279d();
            c1279d.mo3279b();
            c1279d.mo2286a("utf-8");
            c1279d.m605b(1);
            c1279d.m606b("RqdServer");
            c1279d.m607c("sync");
            c1279d.mo2287a("detail", alVar);
            return c1279d.mo2289a();
        } catch (Throwable th) {
            if (!C0757w.m459b(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    public static am m287a(byte[] bArr, boolean z) {
        if (bArr != null) {
            try {
                am amVar;
                C1279d c1279d = new C1279d();
                c1279d.mo3279b();
                c1279d.mo2286a("utf-8");
                c1279d.mo2288a(bArr);
                Object b = c1279d.m594b("detail", new am());
                if (am.class.isInstance(b)) {
                    amVar = (am) am.class.cast(b);
                } else {
                    amVar = null;
                }
                if (z || amVar == null || amVar.f636c == null || amVar.f636c.length <= 0) {
                    return amVar;
                }
                C0757w.m460c("resp buf %d", Integer.valueOf(amVar.f636c.length));
                amVar.f636c = C0761y.m519b(amVar.f636c, 2, 1, StrategyBean.f159d);
                if (amVar.f636c != null) {
                    return amVar;
                }
                C0757w.m462e("resp sbuffer error!", new Object[0]);
                return null;
            } catch (Throwable th) {
                if (!C0757w.m459b(th)) {
                    th.printStackTrace();
                }
            }
        }
        return null;
    }

    public static byte[] m294a(C0737j c0737j) {
        try {
            C0736i c0736i = new C0736i();
            c0736i.m334a("utf-8");
            c0737j.mo2284a(c0736i);
            return c0736i.m347b();
        } catch (Throwable th) {
            if (!C0757w.m459b(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }
}
