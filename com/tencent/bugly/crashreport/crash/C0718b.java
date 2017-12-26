package com.tencent.bugly.crashreport.crash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import com.tencent.bugly.BuglyStrategy.C0691a;
import com.tencent.bugly.C0693b;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.PlugInBean;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import com.tencent.bugly.proguard.C0730a;
import com.tencent.bugly.proguard.C0737j;
import com.tencent.bugly.proguard.C0743n;
import com.tencent.bugly.proguard.C0745o;
import com.tencent.bugly.proguard.C0747q;
import com.tencent.bugly.proguard.C0749s;
import com.tencent.bugly.proguard.C0753t;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import com.tencent.bugly.proguard.ag;
import com.tencent.bugly.proguard.ai;
import com.tencent.bugly.proguard.aj;
import com.tencent.bugly.proguard.ak;
import com.tencent.bugly.proguard.al;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/* compiled from: BUGLY */
public final class C0718b {
    private static int f271a = 0;
    private Context f272b;
    private C0753t f273c;
    private C0745o f274d;
    private C0709a f275e;
    private C0743n f276f;
    private C0691a f277g;

    public C0718b(int i, Context context, C0753t c0753t, C0745o c0745o, C0709a c0709a, C0691a c0691a, C0743n c0743n) {
        f271a = i;
        this.f272b = context;
        this.f273c = c0753t;
        this.f274d = c0745o;
        this.f275e = c0709a;
        this.f277g = c0691a;
        this.f276f = c0743n;
    }

    private static List<C0711a> m202a(List<C0711a> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        List<C0711a> arrayList = new ArrayList();
        for (C0711a c0711a : list) {
            if (c0711a.f245d && c0711a.f243b <= currentTimeMillis - 86400000) {
                arrayList.add(c0711a);
            }
        }
        return arrayList;
    }

    private CrashDetailBean m199a(List<C0711a> list, CrashDetailBean crashDetailBean) {
        if (list == null || list.size() == 0) {
            return crashDetailBean;
        }
        CrashDetailBean crashDetailBean2;
        CrashDetailBean crashDetailBean3 = null;
        List arrayList = new ArrayList(10);
        for (C0711a c0711a : list) {
            if (c0711a.f246e) {
                arrayList.add(c0711a);
            }
        }
        if (arrayList.size() > 0) {
            List b = m207b(arrayList);
            if (b != null && b.size() > 0) {
                Collections.sort(b);
                int i = 0;
                while (i < b.size()) {
                    crashDetailBean2 = (CrashDetailBean) b.get(i);
                    if (i != 0) {
                        if (crashDetailBean2.f234s != null) {
                            String[] split = crashDetailBean2.f234s.split("\n");
                            if (split != null) {
                                for (String str : split) {
                                    if (!crashDetailBean3.f234s.contains(str)) {
                                        crashDetailBean3.f235t++;
                                        crashDetailBean3.f234s += str + "\n";
                                    }
                                }
                            }
                        }
                        crashDetailBean2 = crashDetailBean3;
                    }
                    i++;
                    crashDetailBean3 = crashDetailBean2;
                }
                crashDetailBean2 = crashDetailBean3;
                if (crashDetailBean2 != null) {
                    crashDetailBean.f225j = true;
                    crashDetailBean.f235t = 0;
                    crashDetailBean.f234s = "";
                    crashDetailBean3 = crashDetailBean;
                } else {
                    crashDetailBean3 = crashDetailBean2;
                }
                for (C0711a c0711a2 : list) {
                    if (!(c0711a2.f246e || c0711a2.f245d || crashDetailBean3.f234s.contains(c0711a2.f243b))) {
                        crashDetailBean3.f235t++;
                        crashDetailBean3.f234s += c0711a2.f243b + "\n";
                    }
                }
                if (crashDetailBean3.f233r == crashDetailBean.f233r && !crashDetailBean3.f234s.contains(crashDetailBean.f233r)) {
                    crashDetailBean3.f235t++;
                    crashDetailBean3.f234s += crashDetailBean.f233r + "\n";
                    return crashDetailBean3;
                }
            }
        }
        crashDetailBean2 = null;
        if (crashDetailBean2 != null) {
            crashDetailBean3 = crashDetailBean2;
        } else {
            crashDetailBean.f225j = true;
            crashDetailBean.f235t = 0;
            crashDetailBean.f234s = "";
            crashDetailBean3 = crashDetailBean;
        }
        for (C0711a c0711a22 : list) {
            crashDetailBean3.f235t++;
            crashDetailBean3.f234s += c0711a22.f243b + "\n";
        }
        return crashDetailBean3.f233r == crashDetailBean.f233r ? crashDetailBean3 : crashDetailBean3;
    }

    public final boolean m214a(CrashDetailBean crashDetailBean) {
        return m215a(crashDetailBean, -123456789);
    }

    public final boolean m215a(CrashDetailBean crashDetailBean, int i) {
        if (crashDetailBean == null) {
            return true;
        }
        if (!(C0721c.f297l == null || C0721c.f297l.isEmpty())) {
            C0757w.m460c("Crash filter for crash stack is: %s", C0721c.f297l);
            if (crashDetailBean.f232q.contains(C0721c.f297l)) {
                C0757w.m461d("This crash contains the filter string set. It will not be record and upload.", new Object[0]);
                return true;
            }
        }
        if (!(C0721c.f298m == null || C0721c.f298m.isEmpty())) {
            C0757w.m460c("Crash regular filter for crash stack is: %s", C0721c.f298m);
            if (Pattern.compile(C0721c.f298m).matcher(crashDetailBean.f232q).find()) {
                C0757w.m461d("This crash matches the regular filter string set. It will not be record and upload.", new Object[0]);
                return true;
            }
        }
        int i2 = crashDetailBean.f217b;
        String str = crashDetailBean.f229n;
        str = crashDetailBean.f231p;
        str = crashDetailBean.f232q;
        long j = crashDetailBean.f233r;
        str = crashDetailBean.f228m;
        str = crashDetailBean.f220e;
        str = crashDetailBean.f218c;
        if (this.f276f != null) {
            C0743n c0743n = this.f276f;
            String str2 = crashDetailBean.f241z;
            if (!c0743n.m371c()) {
                return true;
            }
        }
        if (crashDetailBean.f217b != 2) {
            C0747q c0747q = new C0747q();
            c0747q.f405b = 1;
            c0747q.f406c = crashDetailBean.f241z;
            c0747q.f407d = crashDetailBean.f194A;
            c0747q.f408e = crashDetailBean.f233r;
            this.f274d.m403b(1);
            this.f274d.m402a(c0747q);
            C0757w.m458b("[crash] a crash occur, handling...", new Object[0]);
        } else {
            C0757w.m458b("[crash] a caught exception occur, handling...", new Object[0]);
        }
        List<C0711a> b = m206b();
        List list = null;
        if (b != null && b.size() > 0) {
            List arrayList = new ArrayList(10);
            List<C0711a> arrayList2 = new ArrayList(10);
            arrayList.addAll(C0718b.m202a((List) b));
            b.removeAll(arrayList);
            if (!C0693b.f47c && C0721c.f288c) {
                int i3 = 0;
                for (C0711a c0711a : b) {
                    if (crashDetailBean.f236u.equals(c0711a.f244c)) {
                        if (c0711a.f246e) {
                            i3 = true;
                        }
                        arrayList2.add(c0711a);
                    }
                    i3 = i3;
                }
                if (i3 != 0 || arrayList2.size() >= 2) {
                    C0757w.m456a("same crash occur too much do merged!", new Object[0]);
                    CrashDetailBean a = m199a((List) arrayList2, crashDetailBean);
                    for (C0711a c0711a2 : arrayList2) {
                        if (c0711a2.f242a != a.f216a) {
                            arrayList.add(c0711a2);
                        }
                    }
                    m217c(a);
                    C0718b.m208c(arrayList);
                    C0757w.m458b("[crash] save crash success. For this device crash many times, it will not upload crashes immediately", new Object[0]);
                    return true;
                }
            }
            list = arrayList;
        }
        m217c(crashDetailBean);
        if (!(list == null || list.isEmpty())) {
            C0718b.m208c(list);
        }
        C0757w.m458b("[crash] save crash success", new Object[0]);
        return false;
    }

    public final List<CrashDetailBean> m211a() {
        StrategyBean c = C0709a.m169a().m177c();
        if (c == null) {
            C0757w.m461d("have not synced remote!", new Object[0]);
            return null;
        } else if (c.f162g) {
            long currentTimeMillis = System.currentTimeMillis();
            long b = C0761y.m509b();
            List b2 = m206b();
            if (b2 == null || b2.size() <= 0) {
                return null;
            }
            List arrayList = new ArrayList();
            Iterator it = b2.iterator();
            while (it.hasNext()) {
                C0711a c0711a = (C0711a) it.next();
                if (c0711a.f243b < b - C0721c.f291f) {
                    it.remove();
                    arrayList.add(c0711a);
                } else if (c0711a.f245d) {
                    if (c0711a.f243b >= currentTimeMillis - 86400000) {
                        it.remove();
                    } else if (!c0711a.f246e) {
                        it.remove();
                        arrayList.add(c0711a);
                    }
                } else if (((long) c0711a.f247f) >= 3 && c0711a.f243b < currentTimeMillis - 86400000) {
                    it.remove();
                    arrayList.add(c0711a);
                }
            }
            if (arrayList.size() > 0) {
                C0718b.m208c(arrayList);
            }
            List arrayList2 = new ArrayList();
            List<CrashDetailBean> b3 = m207b(b2);
            if (b3 != null && b3.size() > 0) {
                String str = C0705a.m85b().f137j;
                Iterator it2 = b3.iterator();
                while (it2.hasNext()) {
                    CrashDetailBean crashDetailBean = (CrashDetailBean) it2.next();
                    if (!str.equals(crashDetailBean.f221f)) {
                        it2.remove();
                        arrayList2.add(crashDetailBean);
                    }
                }
            }
            if (arrayList2.size() > 0) {
                C0718b.m210d(arrayList2);
            }
            return b3;
        } else {
            C0757w.m461d("Crashreport remote closed, please check your APP ID correct and Version available, then uninstall and reinstall your app.", new Object[0]);
            C0757w.m458b("[init] WARNING! Crashreport closed by server, please check your APP ID correct and Version available, then uninstall and reinstall your app.", new Object[0]);
            return null;
        }
    }

    public final void m212a(CrashDetailBean crashDetailBean, long j, boolean z) {
        boolean z2 = false;
        if (C0721c.f296k) {
            C0757w.m456a("try to upload right now", new Object[0]);
            List arrayList = new ArrayList();
            arrayList.add(crashDetailBean);
            if (crashDetailBean.f217b == 7) {
                z2 = true;
            }
            m213a(arrayList, 3000, z, z2, z);
            if (this.f276f != null) {
                C0743n c0743n = this.f276f;
                int i = crashDetailBean.f217b;
            }
        }
    }

    public final void m213a(final List<CrashDetailBean> list, long j, boolean z, boolean z2, boolean z3) {
        if (!C0705a.m84a(this.f272b).f132e || this.f273c == null) {
            return;
        }
        if (z3 || this.f273c.m442b(C0721c.f286a)) {
            StrategyBean c = this.f275e.m177c();
            if (!c.f162g) {
                C0757w.m461d("remote report is disable!", new Object[0]);
                C0757w.m458b("[crash] server closed bugly in this app. please check your appid if is correct, and re-install it", new Object[0]);
            } else if (list != null && list.size() != 0) {
                try {
                    C0737j c0737j;
                    String str = this.f273c.f423a ? c.f174s : c.f175t;
                    String str2 = this.f273c.f423a ? StrategyBean.f158c : StrategyBean.f156a;
                    int i = this.f273c.f423a ? 830 : 630;
                    Context context = this.f272b;
                    C0705a b = C0705a.m85b();
                    if (context == null || list == null || list.size() == 0 || b == null) {
                        C0757w.m461d("enEXPPkg args == null!", new Object[0]);
                        c0737j = null;
                    } else {
                        C0737j akVar = new ak();
                        akVar.f605a = new ArrayList();
                        for (CrashDetailBean a : list) {
                            akVar.f605a.add(C0718b.m201a(context, a, b));
                        }
                        c0737j = akVar;
                    }
                    if (c0737j == null) {
                        C0757w.m461d("create eupPkg fail!", new Object[0]);
                        return;
                    }
                    byte[] a2 = C0730a.m294a(c0737j);
                    if (a2 == null) {
                        C0757w.m461d("send encode fail!", new Object[0]);
                        return;
                    }
                    al a3 = C0730a.m286a(this.f272b, i, a2);
                    if (a3 == null) {
                        C0757w.m461d("request package is null.", new Object[0]);
                        return;
                    }
                    C0749s c11491 = new C0749s(this) {
                        private /* synthetic */ C0718b f541b;

                        public final void mo2269a(boolean z) {
                            C0718b c0718b = this.f541b;
                            C0718b.m204a(z, list);
                        }
                    };
                    if (z) {
                        this.f273c.m434a(f271a, a3, str, str2, c11491, j, z2);
                    } else {
                        this.f273c.m435a(f271a, a3, str, str2, c11491, false);
                    }
                } catch (Throwable th) {
                    C0757w.m462e("req cr error %s", th.toString());
                    if (!C0757w.m459b(th)) {
                        th.printStackTrace();
                    }
                }
            }
        }
    }

    public static void m204a(boolean z, List<CrashDetailBean> list) {
        if (list != null && list.size() > 0) {
            C0757w.m460c("up finish update state %b", Boolean.valueOf(z));
            for (CrashDetailBean crashDetailBean : list) {
                C0757w.m460c("pre uid:%s uc:%d re:%b me:%b", crashDetailBean.f218c, Integer.valueOf(crashDetailBean.f227l), Boolean.valueOf(crashDetailBean.f219d), Boolean.valueOf(crashDetailBean.f225j));
                crashDetailBean.f227l++;
                crashDetailBean.f219d = z;
                C0757w.m460c("set uid:%s uc:%d re:%b me:%b", crashDetailBean.f218c, Integer.valueOf(crashDetailBean.f227l), Boolean.valueOf(crashDetailBean.f219d), Boolean.valueOf(crashDetailBean.f225j));
            }
            for (CrashDetailBean crashDetailBean2 : list) {
                C0721c.m218a().m224a(crashDetailBean2);
            }
            C0757w.m460c("update state size %d", Integer.valueOf(list.size()));
        }
        if (!z) {
            C0757w.m458b("[crash] upload fail.", new Object[0]);
        }
    }

    public final void m216b(CrashDetailBean crashDetailBean) {
        if (crashDetailBean != null) {
            if (this.f277g != null || this.f276f != null) {
                try {
                    int i;
                    String b;
                    C0757w.m456a("[crash callback] start user's callback:onCrashHandleStart()", new Object[0]);
                    switch (crashDetailBean.f217b) {
                        case 0:
                            i = 0;
                            break;
                        case 1:
                            i = 2;
                            break;
                        case 2:
                            i = 1;
                            break;
                        case 3:
                            i = 4;
                            break;
                        case 4:
                            i = 3;
                            break;
                        case 5:
                            i = 5;
                            break;
                        case 6:
                            i = 6;
                            break;
                        case 7:
                            i = 7;
                            break;
                        default:
                            return;
                    }
                    int i2 = crashDetailBean.f217b;
                    String str = crashDetailBean.f229n;
                    str = crashDetailBean.f231p;
                    str = crashDetailBean.f232q;
                    long j = crashDetailBean.f233r;
                    Map map = null;
                    if (this.f276f != null) {
                        C0743n c0743n = this.f276f;
                        b = this.f276f.m370b();
                        if (b != null) {
                            map = new HashMap(1);
                            map.put("userData", b);
                        }
                    } else if (this.f277g != null) {
                        map = this.f277g.onCrashHandleStart(i, crashDetailBean.f229n, crashDetailBean.f230o, crashDetailBean.f232q);
                    }
                    if (map != null && map.size() > 0) {
                        crashDetailBean.f207N = new LinkedHashMap(map.size());
                        for (Entry entry : map.entrySet()) {
                            if (!C0761y.m501a((String) entry.getKey())) {
                                b = (String) entry.getKey();
                                if (b.length() > 100) {
                                    b = b.substring(0, 100);
                                    C0757w.m461d("setted key length is over limit %d substring to %s", Integer.valueOf(100), b);
                                }
                                String str2 = b;
                                if (C0761y.m501a((String) entry.getValue()) || ((String) entry.getValue()).length() <= C0691a.MAX_USERDATA_VALUE_LENGTH) {
                                    str = ((String) entry.getValue());
                                } else {
                                    str = ((String) entry.getValue()).substring(((String) entry.getValue()).length() - 30000);
                                    C0757w.m461d("setted %s value length is over limit %d substring", str2, Integer.valueOf(C0691a.MAX_USERDATA_VALUE_LENGTH));
                                }
                                crashDetailBean.f207N.put(str2, str);
                                C0757w.m456a("add setted key %s value size:%d", str2, Integer.valueOf(str.length()));
                            }
                        }
                    }
                    C0757w.m456a("[crash callback] start user's callback:onCrashHandleStart2GetExtraDatas()", new Object[0]);
                    byte[] bArr = null;
                    if (this.f276f != null) {
                        bArr = this.f276f.m369a();
                    } else if (this.f277g != null) {
                        bArr = this.f277g.onCrashHandleStart2GetExtraDatas(i, crashDetailBean.f229n, crashDetailBean.f230o, crashDetailBean.f232q);
                    }
                    crashDetailBean.f212S = bArr;
                    if (crashDetailBean.f212S != null) {
                        if (crashDetailBean.f212S.length > C0691a.MAX_USERDATA_VALUE_LENGTH) {
                            C0757w.m461d("extra bytes size %d is over limit %d will drop over part", Integer.valueOf(crashDetailBean.f212S.length), Integer.valueOf(C0691a.MAX_USERDATA_VALUE_LENGTH));
                        }
                        C0757w.m456a("add extra bytes %d ", Integer.valueOf(crashDetailBean.f212S.length));
                    }
                } catch (Throwable th) {
                    C0757w.m461d("crash handle callback somthing wrong! %s", th.getClass().getName());
                    if (!C0757w.m457a(th)) {
                        th.printStackTrace();
                    }
                }
            }
        }
    }

    private static ContentValues m209d(CrashDetailBean crashDetailBean) {
        int i = 1;
        if (crashDetailBean == null) {
            return null;
        }
        try {
            int i2;
            ContentValues contentValues = new ContentValues();
            if (crashDetailBean.f216a > 0) {
                contentValues.put("_id", Long.valueOf(crashDetailBean.f216a));
            }
            contentValues.put("_tm", Long.valueOf(crashDetailBean.f233r));
            contentValues.put("_s1", crashDetailBean.f236u);
            String str = "_up";
            if (crashDetailBean.f219d) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            contentValues.put(str, Integer.valueOf(i2));
            String str2 = "_me";
            if (!crashDetailBean.f225j) {
                i = 0;
            }
            contentValues.put(str2, Integer.valueOf(i));
            contentValues.put("_uc", Integer.valueOf(crashDetailBean.f227l));
            contentValues.put("_dt", C0761y.m504a((Parcelable) crashDetailBean));
            return contentValues;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    private static CrashDetailBean m198a(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        try {
            byte[] blob = cursor.getBlob(cursor.getColumnIndex("_dt"));
            if (blob == null) {
                return null;
            }
            long j = cursor.getLong(cursor.getColumnIndex("_id"));
            CrashDetailBean crashDetailBean = (CrashDetailBean) C0761y.m485a(blob, CrashDetailBean.CREATOR);
            if (crashDetailBean == null) {
                return crashDetailBean;
            }
            crashDetailBean.f216a = j;
            return crashDetailBean;
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    public final void m217c(CrashDetailBean crashDetailBean) {
        if (crashDetailBean != null) {
            ContentValues d = C0718b.m209d(crashDetailBean);
            if (d != null) {
                long a = C0745o.m380a().m395a("t_cr", d, null, true);
                if (a >= 0) {
                    C0757w.m460c("insert %s success!", "t_cr");
                    crashDetailBean.f216a = a;
                }
            }
        }
    }

    private List<CrashDetailBean> m207b(List<C0711a> list) {
        Throwable th;
        Cursor cursor;
        if (list == null || list.size() == 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (C0711a c0711a : list) {
            stringBuilder.append(" or _id").append(" = ").append(c0711a.f242a);
        }
        String stringBuilder2 = stringBuilder.toString();
        if (stringBuilder2.length() > 0) {
            stringBuilder2 = stringBuilder2.substring(4);
        }
        stringBuilder.setLength(0);
        Cursor a;
        try {
            a = C0745o.m380a().m396a("t_cr", null, stringBuilder2, null, null, true);
            if (a == null) {
                if (a != null) {
                    a.close();
                }
                return null;
            }
            try {
                List<CrashDetailBean> arrayList = new ArrayList();
                while (a.moveToNext()) {
                    CrashDetailBean a2 = C0718b.m198a(a);
                    if (a2 != null) {
                        arrayList.add(a2);
                    } else {
                        try {
                            stringBuilder.append(" or _id").append(" = ").append(a.getLong(a.getColumnIndex("_id")));
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                }
                String stringBuilder3 = stringBuilder.toString();
                if (stringBuilder3.length() > 0) {
                    int a3 = C0745o.m380a().m394a("t_cr", stringBuilder3.substring(4), null, null, true);
                    C0757w.m461d("deleted %s illegle data %d", "t_cr", Integer.valueOf(a3));
                }
                if (a != null) {
                    a.close();
                }
                return arrayList;
            } catch (Throwable th22) {
                th = th22;
            }
        } catch (Throwable th3) {
            th = th3;
            a = null;
            if (a != null) {
                a.close();
            }
            throw th;
        }
    }

    private static C0711a m205b(Cursor cursor) {
        boolean z = true;
        if (cursor == null) {
            return null;
        }
        try {
            C0711a c0711a = new C0711a();
            c0711a.f242a = cursor.getLong(cursor.getColumnIndex("_id"));
            c0711a.f243b = cursor.getLong(cursor.getColumnIndex("_tm"));
            c0711a.f244c = cursor.getString(cursor.getColumnIndex("_s1"));
            c0711a.f245d = cursor.getInt(cursor.getColumnIndex("_up")) == 1;
            if (cursor.getInt(cursor.getColumnIndex("_me")) != 1) {
                z = false;
            }
            c0711a.f246e = z;
            c0711a.f247f = cursor.getInt(cursor.getColumnIndex("_uc"));
            return c0711a;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    private List<C0711a> m206b() {
        Throwable th;
        Cursor cursor = null;
        List<C0711a> arrayList = new ArrayList();
        Cursor a;
        try {
            a = C0745o.m380a().m396a("t_cr", new String[]{"_id", "_tm", "_s1", "_up", "_me", "_uc"}, null, null, null, true);
            if (a == null) {
                if (a != null) {
                    a.close();
                }
                return null;
            }
            try {
                StringBuilder stringBuilder = new StringBuilder();
                while (a.moveToNext()) {
                    C0711a b = C0718b.m205b(a);
                    if (b != null) {
                        arrayList.add(b);
                    } else {
                        try {
                            stringBuilder.append(" or _id").append(" = ").append(a.getLong(a.getColumnIndex("_id")));
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                }
                String stringBuilder2 = stringBuilder.toString();
                if (stringBuilder2.length() > 0) {
                    int a2 = C0745o.m380a().m394a("t_cr", stringBuilder2.substring(4), null, null, true);
                    C0757w.m461d("deleted %s illegle data %d", "t_cr", Integer.valueOf(a2));
                }
                if (a != null) {
                    a.close();
                }
                return arrayList;
            } catch (Throwable th22) {
                th = th22;
            }
        } catch (Throwable th3) {
            th = th3;
            a = null;
            if (a != null) {
                a.close();
            }
            throw th;
        }
    }

    private static void m208c(List<C0711a> list) {
        if (list != null && list.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (C0711a c0711a : list) {
                stringBuilder.append(" or _id").append(" = ").append(c0711a.f242a);
            }
            String stringBuilder2 = stringBuilder.toString();
            if (stringBuilder2.length() > 0) {
                stringBuilder2 = stringBuilder2.substring(4);
            }
            stringBuilder.setLength(0);
            try {
                int a = C0745o.m380a().m394a("t_cr", stringBuilder2, null, null, true);
                C0757w.m460c("deleted %s data %d", "t_cr", Integer.valueOf(a));
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
    }

    private static void m210d(List<CrashDetailBean> list) {
        if (list != null) {
            try {
                if (list.size() != 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (CrashDetailBean crashDetailBean : list) {
                        stringBuilder.append(" or _id").append(" = ").append(crashDetailBean.f216a);
                    }
                    String stringBuilder2 = stringBuilder.toString();
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2 = stringBuilder2.substring(4);
                    }
                    stringBuilder.setLength(0);
                    int a = C0745o.m380a().m394a("t_cr", stringBuilder2, null, null, true);
                    C0757w.m460c("deleted %s data %d", "t_cr", Integer.valueOf(a));
                }
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
    }

    private static aj m201a(Context context, CrashDetailBean crashDetailBean, C0705a c0705a) {
        boolean z = true;
        if (context == null || crashDetailBean == null || c0705a == null) {
            C0757w.m461d("enExp args == null", new Object[0]);
            return null;
        }
        ai a;
        aj ajVar = new aj();
        switch (crashDetailBean.f217b) {
            case 0:
                ajVar.f583a = crashDetailBean.f225j ? "200" : "100";
                break;
            case 1:
                ajVar.f583a = crashDetailBean.f225j ? "201" : "101";
                break;
            case 2:
                ajVar.f583a = crashDetailBean.f225j ? "202" : "102";
                break;
            case 3:
                ajVar.f583a = crashDetailBean.f225j ? "203" : "103";
                break;
            case 4:
                ajVar.f583a = crashDetailBean.f225j ? "204" : "104";
                break;
            case 5:
                ajVar.f583a = crashDetailBean.f225j ? "207" : "107";
                break;
            case 6:
                ajVar.f583a = crashDetailBean.f225j ? "206" : "106";
                break;
            case 7:
                ajVar.f583a = crashDetailBean.f225j ? "208" : "108";
                break;
            default:
                C0757w.m462e("crash type error! %d", Integer.valueOf(crashDetailBean.f217b));
                break;
        }
        ajVar.f584b = crashDetailBean.f233r;
        ajVar.f585c = crashDetailBean.f229n;
        ajVar.f586d = crashDetailBean.f230o;
        ajVar.f587e = crashDetailBean.f231p;
        ajVar.f589g = crashDetailBean.f232q;
        ajVar.f590h = crashDetailBean.f240y;
        ajVar.f591i = crashDetailBean.f218c;
        ajVar.f592j = null;
        ajVar.f594l = crashDetailBean.f228m;
        ajVar.f595m = crashDetailBean.f220e;
        ajVar.f588f = crashDetailBean.f194A;
        ajVar.f602t = C0705a.m85b().m121i();
        ajVar.f596n = null;
        if (crashDetailBean.f224i != null && crashDetailBean.f224i.size() > 0) {
            ajVar.f597o = new ArrayList();
            for (Entry entry : crashDetailBean.f224i.entrySet()) {
                ag agVar = new ag();
                agVar.f563a = ((PlugInBean) entry.getValue()).f99a;
                agVar.f565c = ((PlugInBean) entry.getValue()).f101c;
                agVar.f566d = ((PlugInBean) entry.getValue()).f100b;
                agVar.f564b = c0705a.m130r();
                ajVar.f597o.add(agVar);
            }
        }
        if (crashDetailBean.f223h != null && crashDetailBean.f223h.size() > 0) {
            ajVar.f598p = new ArrayList();
            for (Entry entry2 : crashDetailBean.f223h.entrySet()) {
                agVar = new ag();
                agVar.f563a = ((PlugInBean) entry2.getValue()).f99a;
                agVar.f565c = ((PlugInBean) entry2.getValue()).f101c;
                agVar.f566d = ((PlugInBean) entry2.getValue()).f100b;
                ajVar.f598p.add(agVar);
            }
        }
        if (crashDetailBean.f225j) {
            int size;
            ajVar.f593k = crashDetailBean.f235t;
            if (crashDetailBean.f234s != null && crashDetailBean.f234s.length() > 0) {
                if (ajVar.f599q == null) {
                    ajVar.f599q = new ArrayList();
                }
                try {
                    ajVar.f599q.add(new ai((byte) 1, "alltimes.txt", crashDetailBean.f234s.getBytes("utf-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    ajVar.f599q = null;
                }
            }
            String str = "crashcount:%d sz:%d";
            Object[] objArr = new Object[2];
            objArr[0] = Integer.valueOf(ajVar.f593k);
            if (ajVar.f599q != null) {
                size = ajVar.f599q.size();
            } else {
                size = 0;
            }
            objArr[1] = Integer.valueOf(size);
            C0757w.m460c(str, objArr);
        }
        if (crashDetailBean.f238w != null) {
            if (ajVar.f599q == null) {
                ajVar.f599q = new ArrayList();
            }
            try {
                ajVar.f599q.add(new ai((byte) 1, "log.txt", crashDetailBean.f238w.getBytes("utf-8")));
            } catch (UnsupportedEncodingException e2) {
                e2.printStackTrace();
                ajVar.f599q = null;
            }
        }
        if (!C0761y.m501a(crashDetailBean.f213T)) {
            Object aiVar;
            if (ajVar.f599q == null) {
                ajVar.f599q = new ArrayList();
            }
            try {
                aiVar = new ai((byte) 1, "crashInfos.txt", crashDetailBean.f213T.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e22) {
                e22.printStackTrace();
                aiVar = null;
            }
            if (aiVar != null) {
                C0757w.m460c("attach crash infos", new Object[0]);
                ajVar.f599q.add(aiVar);
            }
        }
        if (crashDetailBean.f214U != null) {
            if (ajVar.f599q == null) {
                ajVar.f599q = new ArrayList();
            }
            a = C0718b.m200a("backupRecord.zip", context, crashDetailBean.f214U);
            if (a != null) {
                C0757w.m460c("attach backup record", new Object[0]);
                ajVar.f599q.add(a);
            }
        }
        if (crashDetailBean.f239x != null && crashDetailBean.f239x.length > 0) {
            a = new ai((byte) 2, "buglylog.zip", crashDetailBean.f239x);
            C0757w.m460c("attach user log", new Object[0]);
            if (ajVar.f599q == null) {
                ajVar.f599q = new ArrayList();
            }
            ajVar.f599q.add(a);
        }
        if (crashDetailBean.f217b == 3) {
            if (ajVar.f599q == null) {
                ajVar.f599q = new ArrayList();
            }
            if (crashDetailBean.f207N != null && crashDetailBean.f207N.containsKey("BUGLY_CR_01")) {
                try {
                    ajVar.f599q.add(new ai((byte) 1, "anrMessage.txt", ((String) crashDetailBean.f207N.get("BUGLY_CR_01")).getBytes("utf-8")));
                    C0757w.m460c("attach anr message", new Object[0]);
                } catch (UnsupportedEncodingException e222) {
                    e222.printStackTrace();
                    ajVar.f599q = null;
                }
                crashDetailBean.f207N.remove("BUGLY_CR_01");
            }
            if (crashDetailBean.f237v != null) {
                a = C0718b.m200a("trace.zip", context, crashDetailBean.f237v);
                if (a != null) {
                    C0757w.m460c("attach traces", new Object[0]);
                    ajVar.f599q.add(a);
                }
            }
        }
        if (crashDetailBean.f217b == 1) {
            if (ajVar.f599q == null) {
                ajVar.f599q = new ArrayList();
            }
            if (crashDetailBean.f237v != null) {
                a = C0718b.m200a("tomb.zip", context, crashDetailBean.f237v);
                if (a != null) {
                    C0757w.m460c("attach tombs", new Object[0]);
                    ajVar.f599q.add(a);
                }
            }
        }
        if (!(c0705a.f104B == null || c0705a.f104B.isEmpty())) {
            if (ajVar.f599q == null) {
                ajVar.f599q = new ArrayList();
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String append : c0705a.f104B) {
                stringBuilder.append(append);
            }
            try {
                ajVar.f599q.add(new ai((byte) 1, "martianlog.txt", stringBuilder.toString().getBytes("utf-8")));
                C0757w.m460c("attach pageTracingList", new Object[0]);
            } catch (UnsupportedEncodingException e2222) {
                e2222.printStackTrace();
            }
        }
        if (crashDetailBean.f212S != null && crashDetailBean.f212S.length > 0) {
            if (ajVar.f599q == null) {
                ajVar.f599q = new ArrayList();
            }
            ajVar.f599q.add(new ai((byte) 1, "userExtraByteData", crashDetailBean.f212S));
            C0757w.m460c("attach extraData", new Object[0]);
        }
        ajVar.f600r = new HashMap();
        ajVar.f600r.put("A9", crashDetailBean.f195B);
        ajVar.f600r.put("A11", crashDetailBean.f196C);
        ajVar.f600r.put("A10", crashDetailBean.f197D);
        ajVar.f600r.put("A23", crashDetailBean.f221f);
        ajVar.f600r.put("A7", c0705a.f133f);
        ajVar.f600r.put("A6", c0705a.m131s());
        ajVar.f600r.put("A5", c0705a.m130r());
        ajVar.f600r.put("A22", c0705a.m120h());
        ajVar.f600r.put("A2", crashDetailBean.f199F);
        ajVar.f600r.put("A1", crashDetailBean.f198E);
        ajVar.f600r.put("A24", c0705a.f135h);
        ajVar.f600r.put("A17", crashDetailBean.f200G);
        ajVar.f600r.put("A3", c0705a.m123k());
        ajVar.f600r.put("A16", c0705a.m125m());
        ajVar.f600r.put("A25", c0705a.m126n());
        ajVar.f600r.put("A14", c0705a.m124l());
        ajVar.f600r.put("A15", c0705a.m135w());
        ajVar.f600r.put("A13", c0705a.m136x());
        ajVar.f600r.put("A34", crashDetailBean.f241z);
        if (c0705a.f151x != null) {
            ajVar.f600r.put("productIdentify", c0705a.f151x);
        }
        try {
            ajVar.f600r.put("A26", URLEncoder.encode(crashDetailBean.f201H, "utf-8"));
        } catch (UnsupportedEncodingException e22222) {
            e22222.printStackTrace();
        }
        if (crashDetailBean.f217b == 1) {
            ajVar.f600r.put("A27", crashDetailBean.f203J);
            ajVar.f600r.put("A28", crashDetailBean.f202I);
            ajVar.f600r.put("A29", crashDetailBean.f226k);
        }
        ajVar.f600r.put("A30", crashDetailBean.f204K);
        ajVar.f600r.put("A18", crashDetailBean.f205L);
        ajVar.f600r.put("A36", (!crashDetailBean.f206M));
        ajVar.f600r.put("F02", c0705a.f144q);
        ajVar.f600r.put("F03", c0705a.f145r);
        ajVar.f600r.put("F04", c0705a.m114e());
        ajVar.f600r.put("F05", c0705a.f146s);
        ajVar.f600r.put("F06", c0705a.f143p);
        ajVar.f600r.put("F08", c0705a.f149v);
        ajVar.f600r.put("F09", c0705a.f150w);
        ajVar.f600r.put("F10", c0705a.f147t);
        if (crashDetailBean.f208O >= 0) {
            ajVar.f600r.put("C01", crashDetailBean.f208O);
        }
        if (crashDetailBean.f209P >= 0) {
            ajVar.f600r.put("C02", crashDetailBean.f209P);
        }
        if (crashDetailBean.f210Q != null && crashDetailBean.f210Q.size() > 0) {
            for (Entry entry22 : crashDetailBean.f210Q.entrySet()) {
                ajVar.f600r.put("C03_" + ((String) entry22.getKey()), entry22.getValue());
            }
        }
        if (crashDetailBean.f211R != null && crashDetailBean.f211R.size() > 0) {
            for (Entry entry222 : crashDetailBean.f211R.entrySet()) {
                ajVar.f600r.put("C04_" + ((String) entry222.getKey()), entry222.getValue());
            }
        }
        ajVar.f601s = null;
        if (crashDetailBean.f207N != null && crashDetailBean.f207N.size() > 0) {
            ajVar.f601s = crashDetailBean.f207N;
            C0757w.m456a("setted message size %d", Integer.valueOf(ajVar.f601s.size()));
        }
        String append2 = "%s rid:%s sess:%s ls:%ds isR:%b isF:%b isM:%b isN:%b mc:%d ,%s ,isUp:%b ,vm:%d";
        Object[] objArr2 = new Object[12];
        objArr2[0] = crashDetailBean.f229n;
        objArr2[1] = crashDetailBean.f218c;
        objArr2[2] = c0705a.m114e();
        objArr2[3] = Long.valueOf((crashDetailBean.f233r - crashDetailBean.f205L) / 1000);
        objArr2[4] = Boolean.valueOf(crashDetailBean.f226k);
        objArr2[5] = Boolean.valueOf(crashDetailBean.f206M);
        objArr2[6] = Boolean.valueOf(crashDetailBean.f225j);
        if (crashDetailBean.f217b != 1) {
            z = false;
        }
        objArr2[7] = Boolean.valueOf(z);
        objArr2[8] = Integer.valueOf(crashDetailBean.f235t);
        objArr2[9] = crashDetailBean.f234s;
        objArr2[10] = Boolean.valueOf(crashDetailBean.f219d);
        objArr2[11] = Integer.valueOf(ajVar.f600r.size());
        C0757w.m460c(append2, objArr2);
        return ajVar;
    }

    private static ai m200a(String str, Context context, String str2) {
        Throwable e;
        Throwable th;
        if (str2 == null || context == null) {
            C0757w.m461d("rqdp{  createZipAttachment sourcePath == null || context == null ,pls check}", new Object[0]);
            return null;
        }
        C0757w.m460c("zip %s", str2);
        File file = new File(str2);
        File file2 = new File(context.getCacheDir(), str);
        if (C0761y.m500a(file, file2, 5000)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(file2);
                try {
                    byte[] bArr = new byte[1000];
                    while (true) {
                        int read = fileInputStream.read(bArr);
                        if (read <= 0) {
                            break;
                        }
                        byteArrayOutputStream.write(bArr, 0, read);
                        byteArrayOutputStream.flush();
                    }
                    C0757w.m460c("read bytes :%d", Integer.valueOf(byteArrayOutputStream.toByteArray().length));
                    ai aiVar = new ai((byte) 2, file2.getName(), bArr);
                    try {
                        fileInputStream.close();
                    } catch (Throwable e2) {
                        if (!C0757w.m457a(e2)) {
                            e2.printStackTrace();
                        }
                    }
                    if (file2.exists()) {
                        C0757w.m460c("del tmp", new Object[0]);
                        file2.delete();
                    }
                    return aiVar;
                } catch (Throwable th2) {
                    th = th2;
                    try {
                        if (!C0757w.m457a(th)) {
                            th.printStackTrace();
                        }
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th3) {
                                if (!C0757w.m457a(th3)) {
                                    th3.printStackTrace();
                                }
                            }
                        }
                        if (file2.exists()) {
                            return null;
                        }
                        C0757w.m460c("del tmp", new Object[0]);
                        file2.delete();
                        return null;
                    } catch (Throwable th4) {
                        e2 = th4;
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th32) {
                                if (!C0757w.m457a(th32)) {
                                    th32.printStackTrace();
                                }
                            }
                        }
                        if (file2.exists()) {
                            C0757w.m460c("del tmp", new Object[0]);
                            file2.delete();
                        }
                        throw e2;
                    }
                }
            } catch (Throwable th322) {
                fileInputStream = null;
                e2 = th322;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (file2.exists()) {
                    C0757w.m460c("del tmp", new Object[0]);
                    file2.delete();
                }
                throw e2;
            }
        }
        C0757w.m461d("zip fail!", new Object[0]);
        return null;
    }

    public static void m203a(String str, String str2, String str3, Thread thread, String str4, CrashDetailBean crashDetailBean) {
        C0705a b = C0705a.m85b();
        if (b != null) {
            C0757w.m462e("#++++++++++Record By Bugly++++++++++#", new Object[0]);
            C0757w.m462e("# You can use Bugly(http:\\\\bugly.qq.com) to get more Crash Detail!", new Object[0]);
            C0757w.m462e("# PKG NAME: %s", b.f130c);
            C0757w.m462e("# APP VER: %s", b.f137j);
            C0757w.m462e("# LAUNCH TIME: %s", C0761y.m491a(new Date(C0705a.m85b().f128a)));
            C0757w.m462e("# CRASH TYPE: %s", str);
            C0757w.m462e("# CRASH TIME: %s", str2);
            C0757w.m462e("# CRASH PROCESS: %s", str3);
            if (thread != null) {
                C0757w.m462e("# CRASH THREAD: %s", thread.getName());
            }
            if (crashDetailBean != null) {
                C0757w.m462e("# REPORT ID: %s", crashDetailBean.f218c);
                String str5 = "# CRASH DEVICE: %s %s";
                Object[] objArr = new Object[2];
                objArr[0] = b.f134g;
                objArr[1] = b.m136x().booleanValue() ? "ROOTED" : "UNROOT";
                C0757w.m462e(str5, objArr);
                C0757w.m462e("# RUNTIME AVAIL RAM:%d ROM:%d SD:%d", Long.valueOf(crashDetailBean.f195B), Long.valueOf(crashDetailBean.f196C), Long.valueOf(crashDetailBean.f197D));
                C0757w.m462e("# RUNTIME TOTAL RAM:%d ROM:%d SD:%d", Long.valueOf(crashDetailBean.f198E), Long.valueOf(crashDetailBean.f199F), Long.valueOf(crashDetailBean.f200G));
                if (!C0761y.m501a(crashDetailBean.f203J)) {
                    C0757w.m462e("# EXCEPTION FIRED BY %s %s", crashDetailBean.f203J, crashDetailBean.f202I);
                } else if (crashDetailBean.f217b == 3) {
                    str5 = "# EXCEPTION ANR MESSAGE:\n %s";
                    objArr = new Object[1];
                    objArr[0] = crashDetailBean.f207N == null ? "null" : ((String) crashDetailBean.f207N.get("BUGLY_CR_01"));
                    C0757w.m462e(str5, objArr);
                }
            }
            if (!C0761y.m501a(str4)) {
                C0757w.m462e("# CRASH STACK: ", new Object[0]);
                C0757w.m462e(str4, new Object[0]);
            }
            C0757w.m462e("#++++++++++++++++++++++++++++++++++++++++++#", new Object[0]);
        }
    }
}
