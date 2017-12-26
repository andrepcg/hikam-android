package com.tencent.bugly.crashreport.crash;

import android.content.Context;
import com.tencent.bugly.BuglyStrategy.C0691a;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import com.tencent.bugly.crashreport.crash.anr.C0717b;
import com.tencent.bugly.crashreport.crash.jni.NativeCrashHandler;
import com.tencent.bugly.proguard.C0743n;
import com.tencent.bugly.proguard.C0745o;
import com.tencent.bugly.proguard.C0747q;
import com.tencent.bugly.proguard.C0753t;
import com.tencent.bugly.proguard.C0756v;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* compiled from: BUGLY */
public final class C0721c {
    public static int f286a = 0;
    public static boolean f287b = false;
    public static boolean f288c = true;
    public static int f289d = 20000;
    public static int f290e = 20000;
    public static long f291f = 604800000;
    public static String f292g = null;
    public static boolean f293h = false;
    public static String f294i = null;
    public static int f295j = 5000;
    public static boolean f296k = true;
    public static String f297l = null;
    public static String f298m = null;
    private static C0721c f299p;
    public final C0718b f300n;
    private final Context f301o;
    private final C0725e f302q;
    private final NativeCrashHandler f303r;
    private C0709a f304s = C0709a.m169a();
    private C0756v f305t;
    private final C0717b f306u;
    private Boolean f307v;

    /* compiled from: BUGLY */
    class C07202 extends Thread {
        private /* synthetic */ C0721c f285a;

        C07202(C0721c c0721c) {
            this.f285a = c0721c;
        }

        public final void run() {
            if (C0761y.m499a(this.f285a.f301o, "local_crash_lock", 10000)) {
                List a = this.f285a.f300n.m211a();
                if (a != null && a.size() > 0) {
                    List arrayList;
                    int size = a.size();
                    if (((long) size) > 100) {
                        arrayList = new ArrayList();
                        Collections.sort(a);
                        for (int i = 0; ((long) i) < 100; i++) {
                            arrayList.add(a.get((size - 1) - i));
                        }
                    } else {
                        arrayList = a;
                    }
                    this.f285a.f300n.m213a(arrayList, 0, false, false, false);
                }
                C0761y.m516b(this.f285a.f301o, "local_crash_lock");
            }
        }
    }

    private C0721c(int i, Context context, C0756v c0756v, boolean z, C0691a c0691a, C0743n c0743n, String str) {
        f286a = i;
        Context a = C0761y.m481a(context);
        this.f301o = a;
        this.f305t = c0756v;
        this.f300n = new C0718b(i, a, C0753t.m412a(), C0745o.m380a(), this.f304s, c0691a, c0743n);
        C0705a a2 = C0705a.m84a(a);
        this.f302q = new C0725e(a, this.f300n, this.f304s, a2);
        this.f303r = NativeCrashHandler.getInstance(a, a2, this.f300n, this.f304s, c0756v, z, str);
        a2.f105C = this.f303r;
        this.f306u = new C0717b(a, this.f304s, a2, c0756v, this.f300n);
    }

    public static synchronized void m220a(int i, Context context, boolean z, C0691a c0691a, C0743n c0743n, String str) {
        synchronized (C0721c.class) {
            if (f299p == null) {
                f299p = new C0721c(1004, context, C0756v.m449a(), z, c0691a, null, null);
            }
        }
    }

    public static synchronized C0721c m218a() {
        C0721c c0721c;
        synchronized (C0721c.class) {
            c0721c = f299p;
        }
        return c0721c;
    }

    public final void m223a(StrategyBean strategyBean) {
        this.f302q.m249a(strategyBean);
        this.f303r.onStrategyChanged(strategyBean);
        this.f306u.m193a(strategyBean);
        C0756v.m449a().m451a(new C07202(this), 0);
    }

    public final boolean m226b() {
        Boolean bool = this.f307v;
        if (bool != null) {
            return bool.booleanValue();
        }
        String str = C0705a.m85b().f131d;
        List<C0747q> a = C0745o.m380a().m397a(1);
        List arrayList = new ArrayList();
        if (a == null || a.size() <= 0) {
            this.f307v = Boolean.valueOf(false);
            return false;
        }
        for (C0747q c0747q : a) {
            if (str.equals(c0747q.f406c)) {
                this.f307v = Boolean.valueOf(true);
                arrayList.add(c0747q);
            }
        }
        if (arrayList.size() > 0) {
            C0745o.m380a().m399a(arrayList);
        }
        return true;
    }

    public final synchronized void m227c() {
        this.f302q.m248a();
        this.f303r.setUserOpened(true);
        this.f306u.m195a(true);
    }

    public final synchronized void m228d() {
        this.f302q.m251b();
        this.f303r.setUserOpened(false);
        this.f306u.m195a(false);
    }

    public final void m229e() {
        this.f302q.m248a();
    }

    public final void m230f() {
        this.f303r.setUserOpened(false);
    }

    public final void m231g() {
        this.f303r.setUserOpened(true);
    }

    public final void m232h() {
        this.f306u.m195a(true);
    }

    public final void m233i() {
        this.f306u.m195a(false);
    }

    public final synchronized void m234j() {
        this.f303r.testNativeCrash();
    }

    public final synchronized void m235k() {
        int i = 0;
        synchronized (this) {
            C0717b c0717b = this.f306u;
            while (true) {
                int i2 = i + 1;
                if (i >= 30) {
                    break;
                }
                try {
                    C0757w.m456a("try main sleep for make a test anr! try:%d/30 , kill it if you don't want to wait!", Integer.valueOf(i2));
                    C0761y.m513b(5000);
                    i = i2;
                } catch (Throwable th) {
                    if (!C0757w.m457a(th)) {
                        th.printStackTrace();
                    }
                }
            }
        }
    }

    public final boolean m236l() {
        return this.f306u.m196a();
    }

    public final void m225a(Thread thread, Throwable th, boolean z, String str, byte[] bArr, boolean z2) {
        final Thread thread2 = thread;
        final Throwable th2 = th;
        final byte[] bArr2 = null;
        final boolean z3 = z2;
        this.f305t.m450a(new Runnable(this, false, null) {
            private /* synthetic */ C0721c f284g;

            public final void run() {
                try {
                    C0757w.m460c("post a throwable %b", Boolean.valueOf(false));
                    this.f284g.f302q.m250a(thread2, th2, false, null, bArr2);
                    if (z3) {
                        C0757w.m456a("clear user datas", new Object[0]);
                        C0705a.m84a(this.f284g.f301o).m87A();
                    }
                } catch (Throwable th) {
                    if (!C0757w.m459b(th)) {
                        th.printStackTrace();
                    }
                    C0757w.m462e("java catch error: %s", th2.toString());
                }
            }
        });
    }

    public final void m224a(CrashDetailBean crashDetailBean) {
        this.f300n.m217c(crashDetailBean);
    }

    public final void m222a(long j) {
        C0756v.m449a().m451a(new C07202(this), 0);
    }
}
