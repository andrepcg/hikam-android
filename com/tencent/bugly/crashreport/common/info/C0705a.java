package com.tencent.bugly.crashreport.common.info;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Process;
import android.support.v4.os.EnvironmentCompat;
import com.tencent.bugly.C0693b;
import com.tencent.bugly.crashreport.C0694a;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

/* compiled from: BUGLY */
public final class C0705a {
    private static C0705a f102Z = null;
    public HashMap<String, String> f103A = new HashMap();
    public List<String> f104B = new ArrayList();
    public C0694a f105C = null;
    private final Context f106D;
    private String f107E;
    private String f108F;
    private String f109G = EnvironmentCompat.MEDIA_UNKNOWN;
    private String f110H = EnvironmentCompat.MEDIA_UNKNOWN;
    private String f111I = "";
    private String f112J = null;
    private String f113K = null;
    private String f114L = null;
    private String f115M = null;
    private long f116N = -1;
    private long f117O = -1;
    private long f118P = -1;
    private String f119Q = null;
    private String f120R = null;
    private Map<String, PlugInBean> f121S = null;
    private boolean f122T = true;
    private String f123U = null;
    private String f124V = null;
    private Boolean f125W = null;
    private String f126X = null;
    private Map<String, PlugInBean> f127Y = null;
    public final long f128a = System.currentTimeMillis();
    private int aa = -1;
    private int ab = -1;
    private Map<String, String> ac = new HashMap();
    private Map<String, String> ad = new HashMap();
    private Map<String, String> ae = new HashMap();
    private boolean af;
    private String ag = null;
    private String ah = null;
    private String ai = null;
    private String aj = null;
    private String ak = null;
    private final Object al = new Object();
    private final Object am = new Object();
    private final Object an = new Object();
    private final Object ao = new Object();
    private final Object ap = new Object();
    private final Object aq = new Object();
    private final Object ar = new Object();
    public final byte f129b;
    public String f130c;
    public final String f131d;
    public boolean f132e = true;
    public final String f133f;
    public final String f134g;
    public final String f135h;
    public long f136i;
    public String f137j = null;
    public String f138k = null;
    public String f139l = null;
    public String f140m = null;
    public String f141n = null;
    public List<String> f142o = null;
    public String f143p = EnvironmentCompat.MEDIA_UNKNOWN;
    public long f144q = 0;
    public long f145r = 0;
    public long f146s = 0;
    public long f147t = 0;
    public boolean f148u = false;
    public String f149v = null;
    public String f150w = null;
    public String f151x = null;
    public boolean f152y = false;
    public boolean f153z = false;

    private C0705a(Context context) {
        this.f106D = C0761y.m481a(context);
        this.f129b = (byte) 1;
        PackageInfo b = AppInfo.m78b(context);
        if (b != null) {
            try {
                this.f137j = b.versionName;
                this.f149v = this.f137j;
                this.f150w = Integer.toString(b.versionCode);
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
        this.f130c = AppInfo.m74a(context);
        this.f131d = AppInfo.m73a(Process.myPid());
        this.f133f = C0706b.m160k();
        this.f134g = C0706b.m139a();
        this.f138k = AppInfo.m79c(context);
        this.f135h = "Android " + C0706b.m142b() + ",level " + C0706b.m144c();
        this.f134g + ";" + this.f135h;
        Map d = AppInfo.m80d(context);
        if (d != null) {
            try {
                this.f142o = AppInfo.m76a(d);
                String str = (String) d.get("BUGLY_APPID");
                if (str != null) {
                    this.f124V = str;
                }
                str = (String) d.get("BUGLY_APP_VERSION");
                if (str != null) {
                    this.f137j = str;
                }
                str = (String) d.get("BUGLY_APP_CHANNEL");
                if (str != null) {
                    this.f139l = str;
                }
                str = (String) d.get("BUGLY_ENABLE_DEBUG");
                if (str != null) {
                    this.f148u = str.equalsIgnoreCase("true");
                }
                str = (String) d.get("com.tencent.rdm.uuid");
                if (str != null) {
                    this.f151x = str;
                }
            } catch (Throwable th2) {
                if (!C0757w.m457a(th2)) {
                    th2.printStackTrace();
                }
            }
        }
        try {
            if (!context.getDatabasePath("bugly_db_").exists()) {
                this.f153z = true;
                C0757w.m460c("App is first time to be installed on the device.", new Object[0]);
            }
        } catch (Throwable th22) {
            if (C0693b.f47c) {
                th22.printStackTrace();
            }
        }
        C0757w.m460c("com info create end", new Object[0]);
    }

    public final boolean m106a() {
        return this.af;
    }

    public final void m105a(boolean z) {
        this.af = z;
        if (this.f105C != null) {
            this.f105C.setNativeIsAppForeground(z);
        }
    }

    public static synchronized C0705a m84a(Context context) {
        C0705a c0705a;
        synchronized (C0705a.class) {
            if (f102Z == null) {
                f102Z = new C0705a(context);
            }
            c0705a = f102Z;
        }
        return c0705a;
    }

    public static synchronized C0705a m85b() {
        C0705a c0705a;
        synchronized (C0705a.class) {
            c0705a = f102Z;
        }
        return c0705a;
    }

    public static String m86c() {
        return "2.4.0";
    }

    public final void m112d() {
        synchronized (this.al) {
            this.f107E = UUID.randomUUID().toString();
        }
    }

    public final String m114e() {
        if (this.f107E == null) {
            synchronized (this.al) {
                if (this.f107E == null) {
                    this.f107E = UUID.randomUUID().toString();
                }
            }
        }
        return this.f107E;
    }

    public final String m116f() {
        if (C0761y.m501a(null)) {
            return this.f124V;
        }
        return null;
    }

    public final void m103a(String str) {
        this.f124V = str;
    }

    public final String m118g() {
        String str;
        synchronized (this.aq) {
            str = this.f109G;
        }
        return str;
    }

    public final void m108b(String str) {
        synchronized (this.aq) {
            if (str == null) {
                str = "10000";
            }
            this.f109G = str;
        }
    }

    public final String m120h() {
        if (this.f108F != null) {
            return this.f108F;
        }
        this.f108F = m123k() + "|" + m125m() + "|" + m126n();
        return this.f108F;
    }

    public final void m110c(String str) {
        this.f108F = str;
    }

    public final synchronized String m121i() {
        return this.f110H;
    }

    public final synchronized void m113d(String str) {
        this.f110H = str;
    }

    public final synchronized String m122j() {
        return this.f111I;
    }

    public final synchronized void m115e(String str) {
        this.f111I = str;
    }

    public final String m123k() {
        if (!this.f122T) {
            return "";
        }
        if (this.f112J == null) {
            this.f112J = C0706b.m140a(this.f106D);
        }
        return this.f112J;
    }

    public final String m124l() {
        if (!this.f122T) {
            return "";
        }
        if (this.f113K == null) {
            this.f113K = C0706b.m147d(this.f106D);
        }
        return this.f113K;
    }

    public final String m125m() {
        if (!this.f122T) {
            return "";
        }
        if (this.f114L == null) {
            this.f114L = C0706b.m143b(this.f106D);
        }
        return this.f114L;
    }

    public final String m126n() {
        if (!this.f122T) {
            return "";
        }
        if (this.f115M == null) {
            this.f115M = C0706b.m145c(this.f106D);
        }
        return this.f115M;
    }

    public final long m127o() {
        if (this.f116N <= 0) {
            this.f116N = C0706b.m146d();
        }
        return this.f116N;
    }

    public final long m128p() {
        if (this.f117O <= 0) {
            this.f117O = C0706b.m150f();
        }
        return this.f117O;
    }

    public final long m129q() {
        if (this.f118P <= 0) {
            this.f118P = C0706b.m154h();
        }
        return this.f118P;
    }

    public final String m130r() {
        if (this.f119Q == null) {
            this.f119Q = C0706b.m141a(true);
        }
        return this.f119Q;
    }

    public final String m131s() {
        if (this.f120R == null) {
            this.f120R = C0706b.m153g(this.f106D);
        }
        return this.f120R;
    }

    public final void m104a(String str, String str2) {
        if (str != null && str2 != null) {
            synchronized (this.am) {
                this.f103A.put(str, str2);
            }
        }
    }

    public final String m132t() {
        try {
            Map all = this.f106D.getSharedPreferences("BuglySdkInfos", 0).getAll();
            if (!all.isEmpty()) {
                synchronized (this.am) {
                    for (Entry entry : all.entrySet()) {
                        try {
                            this.f103A.put(entry.getKey(), entry.getValue().toString());
                        } catch (Throwable th) {
                            C0757w.m457a(th);
                        }
                    }
                }
            }
        } catch (Throwable th2) {
            C0757w.m457a(th2);
        }
        if (this.f103A.isEmpty()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry2 : this.f103A.entrySet()) {
            stringBuilder.append("[");
            stringBuilder.append((String) entry2.getKey());
            stringBuilder.append(",");
            stringBuilder.append((String) entry2.getValue());
            stringBuilder.append("] ");
        }
        m111c("SDK_INFO", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public final String m133u() {
        if (this.ak == null) {
            this.ak = AppInfo.m81e(this.f106D);
        }
        return this.ak;
    }

    public final synchronized Map<String, PlugInBean> m134v() {
        return null;
    }

    public final String m135w() {
        if (this.f123U == null) {
            this.f123U = C0706b.m158j();
        }
        return this.f123U;
    }

    public final Boolean m136x() {
        if (this.f125W == null) {
            this.f125W = Boolean.valueOf(C0706b.m155h(this.f106D));
        }
        return this.f125W;
    }

    public final String m137y() {
        if (this.f126X == null) {
            this.f126X = C0706b.m151f(this.f106D);
            C0757w.m456a("rom:%s", this.f126X);
        }
        return this.f126X;
    }

    public final Map<String, String> m138z() {
        Map<String, String> map;
        synchronized (this.an) {
            if (this.ac.size() <= 0) {
                map = null;
            } else {
                map = new HashMap(this.ac);
            }
        }
        return map;
    }

    public final String m117f(String str) {
        if (C0761y.m501a(str)) {
            C0757w.m461d("key should not be empty %s", str);
            return null;
        }
        String str2;
        synchronized (this.an) {
            str2 = (String) this.ac.remove(str);
        }
        return str2;
    }

    public final void m87A() {
        synchronized (this.an) {
            this.ac.clear();
        }
    }

    public final String m119g(String str) {
        if (C0761y.m501a(str)) {
            C0757w.m461d("key should not be empty %s", str);
            return null;
        }
        String str2;
        synchronized (this.an) {
            str2 = (String) this.ac.get(str);
        }
        return str2;
    }

    public final void m109b(String str, String str2) {
        if (C0761y.m501a(str) || C0761y.m501a(str2)) {
            C0757w.m461d("key&value should not be empty %s %s", str, str2);
            return;
        }
        synchronized (this.an) {
            this.ac.put(str, str2);
        }
    }

    public final int m88B() {
        int size;
        synchronized (this.an) {
            size = this.ac.size();
        }
        return size;
    }

    public final Set<String> m89C() {
        Set<String> keySet;
        synchronized (this.an) {
            keySet = this.ac.keySet();
        }
        return keySet;
    }

    public final Map<String, String> m90D() {
        Map<String, String> map;
        synchronized (this.ar) {
            if (this.ad.size() <= 0) {
                map = null;
            } else {
                map = new HashMap(this.ad);
            }
        }
        return map;
    }

    public final void m111c(String str, String str2) {
        if (C0761y.m501a(str) || C0761y.m501a(str2)) {
            C0757w.m461d("server key&value should not be empty %s %s", str, str2);
            return;
        }
        synchronized (this.ao) {
            this.ae.put(str, str2);
        }
    }

    public final Map<String, String> m91E() {
        Map<String, String> map;
        synchronized (this.ao) {
            if (this.ae.size() <= 0) {
                map = null;
            } else {
                map = new HashMap(this.ae);
            }
        }
        return map;
    }

    public final void m102a(int i) {
        synchronized (this.ap) {
            if (this.aa != i) {
                this.aa = i;
                C0757w.m456a("user scene tag %d changed to tag %d", Integer.valueOf(r0), Integer.valueOf(this.aa));
            }
        }
    }

    public final int m92F() {
        int i;
        synchronized (this.ap) {
            i = this.aa;
        }
        return i;
    }

    public final void m107b(int i) {
        if (this.ab != 24096) {
            this.ab = 24096;
            C0757w.m456a("server scene tag %d changed to tag %d", Integer.valueOf(r0), Integer.valueOf(this.ab));
        }
    }

    public final int m93G() {
        return this.ab;
    }

    public final boolean m94H() {
        return AppInfo.m82f(this.f106D);
    }

    public final synchronized Map<String, PlugInBean> m95I() {
        return null;
    }

    public static int m83J() {
        return C0706b.m144c();
    }

    public final String m96K() {
        if (this.ag == null) {
            this.ag = C0706b.m162l();
        }
        return this.ag;
    }

    public final String m97L() {
        if (this.ah == null) {
            this.ah = C0706b.m157i(this.f106D);
        }
        return this.ah;
    }

    public final String m98M() {
        if (this.ai == null) {
            this.ai = C0706b.m159j(this.f106D);
        }
        return this.ai;
    }

    public final String m99N() {
        Context context = this.f106D;
        return C0706b.m163m();
    }

    public final String m100O() {
        if (this.aj == null) {
            this.aj = C0706b.m161k(this.f106D);
        }
        return this.aj;
    }

    public final long m101P() {
        Context context = this.f106D;
        return C0706b.m164n();
    }
}
