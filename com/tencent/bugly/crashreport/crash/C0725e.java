package com.tencent.bugly.crashreport.crash;

import android.content.Context;
import android.os.Process;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.C0706b;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0760x;
import com.tencent.bugly.proguard.C0761y;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;

/* compiled from: BUGLY */
public final class C0725e implements UncaughtExceptionHandler {
    private static String f320h = null;
    private static final Object f321i = new Object();
    private Context f322a;
    private C0718b f323b;
    private C0709a f324c;
    private C0705a f325d;
    private UncaughtExceptionHandler f326e;
    private UncaughtExceptionHandler f327f;
    private boolean f328g = false;
    private int f329j;

    public C0725e(Context context, C0718b c0718b, C0709a c0709a, C0705a c0705a) {
        this.f322a = context;
        this.f323b = c0718b;
        this.f324c = c0709a;
        this.f325d = c0705a;
    }

    public final synchronized void m248a() {
        if (this.f329j >= 10) {
            C0757w.m456a("java crash handler over %d, no need set.", Integer.valueOf(10));
        } else {
            UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
            if (!(defaultUncaughtExceptionHandler == null || getClass().getName().equals(defaultUncaughtExceptionHandler.getClass().getName()))) {
                if ("com.android.internal.os.RuntimeInit$UncaughtHandler".equals(defaultUncaughtExceptionHandler.getClass().getName())) {
                    C0757w.m456a("backup system java handler: %s", defaultUncaughtExceptionHandler.toString());
                    this.f327f = defaultUncaughtExceptionHandler;
                    this.f326e = defaultUncaughtExceptionHandler;
                } else {
                    C0757w.m456a("backup java handler: %s", defaultUncaughtExceptionHandler.toString());
                    this.f326e = defaultUncaughtExceptionHandler;
                }
                m243a(defaultUncaughtExceptionHandler);
                Thread.setDefaultUncaughtExceptionHandler(this);
                this.f328g = true;
                this.f329j++;
                C0757w.m456a("registered java monitor: %s", toString());
            }
        }
    }

    public final synchronized void m251b() {
        this.f328g = false;
        C0757w.m456a("close java monitor!", new Object[0]);
        if (Thread.getDefaultUncaughtExceptionHandler().getClass().getName().contains("bugly")) {
            C0757w.m456a("Java monitor to unregister: %s", toString());
            Thread.setDefaultUncaughtExceptionHandler(this.f326e);
            this.f329j--;
        }
    }

    private synchronized void m243a(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.f326e = uncaughtExceptionHandler;
    }

    private CrashDetailBean m245b(Thread thread, Throwable th, boolean z, String str, byte[] bArr) {
        if (th == null) {
            C0757w.m461d("We can do nothing with a null throwable.", new Object[0]);
            return null;
        }
        Object a;
        boolean l = C0721c.m218a().m236l();
        String str2 = (l && z) ? " This Crash Caused By ANR , PLS To Fix ANR , This Trace May Be Not Useful![Bugly]" : "";
        if (l && z) {
            C0757w.m462e("This Crash Caused By ANR , PLS To Fix ANR , This Trace May Be Not Useful!", new Object[0]);
        }
        CrashDetailBean crashDetailBean = new CrashDetailBean();
        crashDetailBean.f195B = C0706b.m152g();
        crashDetailBean.f196C = C0706b.m148e();
        crashDetailBean.f197D = C0706b.m156i();
        crashDetailBean.f198E = this.f325d.m128p();
        crashDetailBean.f199F = this.f325d.m127o();
        crashDetailBean.f200G = this.f325d.m129q();
        crashDetailBean.f238w = C0761y.m488a(this.f322a, C0721c.f289d, null);
        crashDetailBean.f239x = C0760x.m475a(z);
        String str3 = "user log size:%d";
        Object[] objArr = new Object[1];
        objArr[0] = Integer.valueOf(crashDetailBean.f239x == null ? 0 : crashDetailBean.f239x.length);
        C0757w.m456a(str3, objArr);
        crashDetailBean.f217b = z ? 0 : 2;
        crashDetailBean.f220e = this.f325d.m120h();
        crashDetailBean.f221f = this.f325d.f137j;
        crashDetailBean.f222g = this.f325d.m135w();
        crashDetailBean.f228m = this.f325d.m118g();
        String name = th.getClass().getName();
        String b = C0725e.m246b(th, 1000);
        if (b == null) {
            b = "";
        }
        String str4 = "stack frame :%d, has cause %b";
        Object[] objArr2 = new Object[2];
        objArr2[0] = Integer.valueOf(th.getStackTrace().length);
        objArr2[1] = Boolean.valueOf(th.getCause() != null);
        C0757w.m462e(str4, objArr2);
        str3 = "";
        if (th.getStackTrace().length > 0) {
            str3 = th.getStackTrace()[0].toString();
        }
        Throwable th2 = th;
        while (th2 != null && th2.getCause() != null) {
            th2 = th2.getCause();
        }
        if (th2 == null || th2 == th) {
            crashDetailBean.f229n = name;
            crashDetailBean.f230o = b + str2;
            if (crashDetailBean.f230o == null) {
                crashDetailBean.f230o = "";
            }
            crashDetailBean.f231p = str3;
            a = C0725e.m242a(th, C0721c.f290e);
            crashDetailBean.f232q = a;
        } else {
            crashDetailBean.f229n = th2.getClass().getName();
            crashDetailBean.f230o = C0725e.m246b(th2, 1000);
            if (crashDetailBean.f230o == null) {
                crashDetailBean.f230o = "";
            }
            crashDetailBean.f231p = th2.getStackTrace()[0].toString();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name).append(":").append(b).append("\n");
            stringBuilder.append(str3);
            stringBuilder.append("\n......");
            stringBuilder.append("\nCaused by:\n");
            stringBuilder.append(crashDetailBean.f229n).append(":").append(crashDetailBean.f230o).append("\n");
            a = C0725e.m242a(th2, C0721c.f290e);
            stringBuilder.append(a);
            crashDetailBean.f232q = stringBuilder.toString();
        }
        crashDetailBean.f233r = System.currentTimeMillis();
        crashDetailBean.f236u = C0761y.m511b(crashDetailBean.f232q.getBytes());
        try {
            crashDetailBean.f240y = C0761y.m495a(C0721c.f290e, false);
            crashDetailBean.f241z = this.f325d.f131d;
            crashDetailBean.f194A = thread.getName() + "(" + thread.getId() + ")";
            crashDetailBean.f240y.put(crashDetailBean.f194A, a);
            crashDetailBean.f201H = this.f325d.m137y();
            crashDetailBean.f223h = this.f325d.m134v();
            crashDetailBean.f224i = this.f325d.m95I();
            crashDetailBean.f205L = this.f325d.f128a;
            crashDetailBean.f206M = this.f325d.m106a();
            crashDetailBean.f208O = this.f325d.m92F();
            crashDetailBean.f209P = this.f325d.m93G();
            crashDetailBean.f210Q = this.f325d.m138z();
            crashDetailBean.f211R = this.f325d.m91E();
        } catch (Throwable th3) {
            C0757w.m462e("handle crash error %s", th3.toString());
        }
        if (z) {
            this.f323b.m216b(crashDetailBean);
        } else {
            Object obj = (str == null || str.length() <= 0) ? null : 1;
            a = (bArr == null || bArr.length <= 0) ? null : 1;
            if (obj != null) {
                crashDetailBean.f207N = new HashMap(1);
                crashDetailBean.f207N.put("UserData", str);
            }
            if (a != null) {
                crashDetailBean.f212S = bArr;
            }
        }
        return crashDetailBean;
    }

    private static boolean m244a(Thread thread) {
        boolean z;
        synchronized (f321i) {
            if (f320h == null || !thread.getName().equals(f320h)) {
                f320h = thread.getName();
                z = false;
            } else {
                z = true;
            }
        }
        return z;
    }

    public final void m250a(Thread thread, Throwable th, boolean z, String str, byte[] bArr) {
        if (z) {
            C0757w.m462e("Java Crash Happen cause by %s(%d)", thread.getName(), Long.valueOf(thread.getId()));
            if (C0725e.m244a(thread)) {
                C0757w.m456a("this class has handled this exception", new Object[0]);
                if (this.f327f != null) {
                    C0757w.m456a("call system handler", new Object[0]);
                    this.f327f.uncaughtException(thread, th);
                } else {
                    C0757w.m462e("current process die", new Object[0]);
                    Process.killProcess(Process.myPid());
                    System.exit(1);
                }
            }
        } else {
            C0757w.m462e("Java Catch Happen", new Object[0]);
        }
        try {
            if (this.f328g) {
                if (!this.f324c.m176b()) {
                    C0757w.m462e("waiting for remote sync", new Object[0]);
                    int i = 0;
                    while (!this.f324c.m176b()) {
                        C0761y.m513b(500);
                        i += 500;
                        if (i >= 3000) {
                            break;
                        }
                    }
                }
                if (!this.f324c.m176b()) {
                    C0757w.m461d("no remote but still store!", new Object[0]);
                }
                if (this.f324c.m177c().f162g || !this.f324c.m176b()) {
                    CrashDetailBean b = m245b(thread, th, z, str, bArr);
                    if (b == null) {
                        C0757w.m462e("pkg crash datas fail!", new Object[0]);
                        if (!z) {
                            return;
                        }
                        if (this.f326e != null && C0725e.m247b(this.f326e)) {
                            C0757w.m462e("sys default last handle start!", new Object[0]);
                            this.f326e.uncaughtException(thread, th);
                            C0757w.m462e("sys default last handle end!", new Object[0]);
                            return;
                        } else if (this.f327f != null) {
                            C0757w.m462e("system handle start!", new Object[0]);
                            this.f327f.uncaughtException(thread, th);
                            C0757w.m462e("system handle end!", new Object[0]);
                            return;
                        } else {
                            C0757w.m462e("crashreport last handle start!", new Object[0]);
                            C0757w.m462e("current process die", new Object[0]);
                            Process.killProcess(Process.myPid());
                            System.exit(1);
                            C0757w.m462e("crashreport last handle end!", new Object[0]);
                            return;
                        }
                    }
                    C0718b.m203a(z ? "JAVA_CRASH" : "JAVA_CATCH", C0761y.m486a(), this.f325d.f131d, thread, C0761y.m490a(th), b);
                    if (!this.f323b.m214a(b)) {
                        this.f323b.m212a(b, 3000, z);
                    }
                    if (!z) {
                        return;
                    }
                    if (this.f326e != null && C0725e.m247b(this.f326e)) {
                        C0757w.m462e("sys default last handle start!", new Object[0]);
                        this.f326e.uncaughtException(thread, th);
                        C0757w.m462e("sys default last handle end!", new Object[0]);
                        return;
                    } else if (this.f327f != null) {
                        C0757w.m462e("system handle start!", new Object[0]);
                        this.f327f.uncaughtException(thread, th);
                        C0757w.m462e("system handle end!", new Object[0]);
                        return;
                    } else {
                        C0757w.m462e("crashreport last handle start!", new Object[0]);
                        C0757w.m462e("current process die", new Object[0]);
                        Process.killProcess(Process.myPid());
                        System.exit(1);
                        C0757w.m462e("crashreport last handle end!", new Object[0]);
                        return;
                    }
                }
                String str2;
                C0757w.m462e("crash report was closed by remote , will not upload to Bugly , print local for helpful!", new Object[0]);
                if (z) {
                    str2 = "JAVA_CRASH";
                } else {
                    str2 = "JAVA_CATCH";
                }
                C0718b.m203a(str2, C0761y.m486a(), this.f325d.f131d, thread, C0761y.m490a(th), null);
                if (!z) {
                    return;
                }
                if (this.f326e != null && C0725e.m247b(this.f326e)) {
                    C0757w.m462e("sys default last handle start!", new Object[0]);
                    this.f326e.uncaughtException(thread, th);
                    C0757w.m462e("sys default last handle end!", new Object[0]);
                    return;
                } else if (this.f327f != null) {
                    C0757w.m462e("system handle start!", new Object[0]);
                    this.f327f.uncaughtException(thread, th);
                    C0757w.m462e("system handle end!", new Object[0]);
                    return;
                } else {
                    C0757w.m462e("crashreport last handle start!", new Object[0]);
                    C0757w.m462e("current process die", new Object[0]);
                    Process.killProcess(Process.myPid());
                    System.exit(1);
                    C0757w.m462e("crashreport last handle end!", new Object[0]);
                    return;
                }
            }
            C0757w.m460c("Java crash handler is disable. Just return.", new Object[0]);
            if (!z) {
                return;
            }
            if (this.f326e != null && C0725e.m247b(this.f326e)) {
                C0757w.m462e("sys default last handle start!", new Object[0]);
                this.f326e.uncaughtException(thread, th);
                C0757w.m462e("sys default last handle end!", new Object[0]);
            } else if (this.f327f != null) {
                C0757w.m462e("system handle start!", new Object[0]);
                this.f327f.uncaughtException(thread, th);
                C0757w.m462e("system handle end!", new Object[0]);
            } else {
                C0757w.m462e("crashreport last handle start!", new Object[0]);
                C0757w.m462e("current process die", new Object[0]);
                Process.killProcess(Process.myPid());
                System.exit(1);
                C0757w.m462e("crashreport last handle end!", new Object[0]);
            }
        } catch (Throwable th2) {
            if (z) {
                if (this.f326e != null && C0725e.m247b(this.f326e)) {
                    C0757w.m462e("sys default last handle start!", new Object[0]);
                    this.f326e.uncaughtException(thread, th);
                    C0757w.m462e("sys default last handle end!", new Object[0]);
                } else if (this.f327f != null) {
                    C0757w.m462e("system handle start!", new Object[0]);
                    this.f327f.uncaughtException(thread, th);
                    C0757w.m462e("system handle end!", new Object[0]);
                } else {
                    C0757w.m462e("crashreport last handle start!", new Object[0]);
                    C0757w.m462e("current process die", new Object[0]);
                    Process.killProcess(Process.myPid());
                    System.exit(1);
                    C0757w.m462e("crashreport last handle end!", new Object[0]);
                }
            }
        }
    }

    public final void uncaughtException(Thread thread, Throwable th) {
        synchronized (f321i) {
            m250a(thread, th, true, null, null);
        }
    }

    private static boolean m247b(UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (uncaughtExceptionHandler == null) {
            return true;
        }
        String name = uncaughtExceptionHandler.getClass().getName();
        String str = "uncaughtException";
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            if (name.equals(className) && str.equals(methodName)) {
                return false;
            }
        }
        return true;
    }

    public final synchronized void m249a(StrategyBean strategyBean) {
        if (strategyBean != null) {
            if (strategyBean.f162g != this.f328g) {
                C0757w.m456a("java changed to %b", Boolean.valueOf(strategyBean.f162g));
                if (strategyBean.f162g) {
                    m248a();
                } else {
                    m251b();
                }
            }
        }
    }

    private static String m242a(Throwable th, int i) {
        if (th == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (th.getStackTrace() != null) {
                StackTraceElement[] stackTrace = th.getStackTrace();
                int length = stackTrace.length;
                int i2 = 0;
                while (i2 < length) {
                    StackTraceElement stackTraceElement = stackTrace[i2];
                    if (i <= 0 || stringBuilder.length() < i) {
                        stringBuilder.append(stackTraceElement.toString()).append("\n");
                        i2++;
                    } else {
                        stringBuilder.append("\n[Stack over limit size :" + i + " , has been cutted !]");
                        return stringBuilder.toString();
                    }
                }
            }
        } catch (Throwable th2) {
            C0757w.m462e("gen stack error %s", th2.toString());
        }
        return stringBuilder.toString();
    }

    private static String m246b(Throwable th, int i) {
        if (th.getMessage() == null) {
            return "";
        }
        if (th.getMessage().length() <= 1000) {
            return th.getMessage();
        }
        return th.getMessage().substring(0, 1000) + "\n[Message over limit size:1000" + ", has been cutted!]";
    }
}
