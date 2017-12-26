package com.tencent.bugly.crashreport.crash.jni;

import android.annotation.SuppressLint;
import android.content.Context;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.C0693b;
import com.tencent.bugly.crashreport.C0694a;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.C0706b;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import com.tencent.bugly.crashreport.crash.C0718b;
import com.tencent.bugly.crashreport.crash.C0721c;
import com.tencent.bugly.crashreport.crash.CrashDetailBean;
import com.tencent.bugly.proguard.C0756v;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.io.File;
import org.apache.commons.compress.archivers.tar.TarConstants;

/* compiled from: BUGLY */
public class NativeCrashHandler implements C0694a {
    private static NativeCrashHandler f542a;
    private static boolean f543l = false;
    private static boolean f544m = false;
    private final Context f545b;
    private final C0705a f546c;
    private final C0756v f547d;
    private NativeExceptionHandler f548e;
    private String f549f;
    private final boolean f550g;
    private boolean f551h = false;
    private boolean f552i = false;
    private boolean f553j = false;
    private boolean f554k = false;
    private C0718b f555n;

    /* compiled from: BUGLY */
    class C07281 implements Runnable {
        private /* synthetic */ NativeCrashHandler f348a;

        C07281(NativeCrashHandler nativeCrashHandler) {
            this.f348a = nativeCrashHandler;
        }

        public final void run() {
            if (C0761y.m499a(this.f348a.f545b, "native_record_lock", 10000)) {
                try {
                    this.f348a.setNativeAppVersion(this.f348a.f546c.f137j);
                    this.f348a.setNativeAppChannel(this.f348a.f546c.f139l);
                    this.f348a.setNativeAppPackage(this.f348a.f546c.f130c);
                    this.f348a.setNativeUserId(this.f348a.f546c.m118g());
                    this.f348a.setNativeIsAppForeground(this.f348a.f546c.m106a());
                    this.f348a.setNativeLaunchTime(this.f348a.f546c.f128a);
                } catch (Throwable th) {
                    if (!C0757w.m457a(th)) {
                        th.printStackTrace();
                    }
                }
                CrashDetailBean a = C0729b.m255a(this.f348a.f545b, this.f348a.f549f, this.f348a.f548e);
                if (a != null) {
                    C0757w.m456a("[Native] Get crash from native record.", new Object[0]);
                    if (!this.f348a.f555n.m214a(a)) {
                        this.f348a.f555n.m212a(a, 3000, false);
                    }
                    C0729b.m260b(this.f348a.f549f);
                }
                this.f348a.m550a();
                C0761y.m516b(this.f348a.f545b, "native_record_lock");
                return;
            }
            C0757w.m456a("[Native] Failed to lock file for handling native crash record.", new Object[0]);
        }
    }

    protected native boolean appendNativeLog(String str, String str2, String str3);

    protected native boolean appendWholeNativeLog(String str);

    protected native String getNativeKeyValueList();

    protected native String getNativeLog();

    protected native boolean putNativeKeyValue(String str, String str2);

    protected native String regist(String str, boolean z, int i);

    protected native String removeNativeKeyValue(String str);

    protected native void setNativeInfo(int i, String str);

    protected native void testCrash();

    protected native String unregist();

    @SuppressLint({"SdCardPath"})
    private NativeCrashHandler(Context context, C0705a c0705a, C0718b c0718b, C0756v c0756v, boolean z, String str) {
        this.f545b = C0761y.m481a(context);
        try {
            if (C0761y.m501a(str)) {
                str = context.getDir("bugly", 0).getAbsolutePath();
            }
        } catch (Throwable th) {
            str = "/data/data/" + C0705a.m84a(context).f130c + "/app_bugly";
        }
        this.f555n = c0718b;
        this.f549f = str;
        this.f546c = c0705a;
        this.f547d = c0756v;
        this.f550g = z;
    }

    public static synchronized NativeCrashHandler getInstance(Context context, C0705a c0705a, C0718b c0718b, C0709a c0709a, C0756v c0756v, boolean z, String str) {
        NativeCrashHandler nativeCrashHandler;
        synchronized (NativeCrashHandler.class) {
            if (f542a == null) {
                f542a = new NativeCrashHandler(context, c0705a, c0718b, c0756v, z, str);
            }
            nativeCrashHandler = f542a;
        }
        return nativeCrashHandler;
    }

    public static synchronized NativeCrashHandler getInstance() {
        NativeCrashHandler nativeCrashHandler;
        synchronized (NativeCrashHandler.class) {
            nativeCrashHandler = f542a;
        }
        return nativeCrashHandler;
    }

    public synchronized String getDumpFilePath() {
        return this.f549f;
    }

    public synchronized void setDumpFilePath(String str) {
        this.f549f = str;
    }

    private synchronized void m540a(boolean z) {
        if (this.f553j) {
            C0757w.m461d("[Native] Native crash report has already registered.", new Object[0]);
        } else {
            this.f548e = new C1150a(this.f545b, this.f546c, this.f555n, C0709a.m169a(), this.f549f);
            String replace;
            if (this.f552i) {
                try {
                    String regist = regist(this.f549f, z, 1);
                    if (regist != null) {
                        C0757w.m456a("[Native] Native Crash Report enable.", new Object[0]);
                        C0757w.m460c("[Native] Check extra jni for Bugly NDK v%s", regist);
                        String replace2 = "2.1.1".replace(".", "");
                        String replace3 = "2.3.0".replace(".", "");
                        replace = regist.replace(".", "");
                        if (replace.length() == 2) {
                            replace = replace + "0";
                        } else if (replace.length() == 1) {
                            replace = replace + TarConstants.VERSION_POSIX;
                        }
                        try {
                            if (Integer.parseInt(replace) >= Integer.parseInt(replace2)) {
                                f543l = true;
                            }
                            if (Integer.parseInt(replace) >= Integer.parseInt(replace3)) {
                                f544m = true;
                            }
                        } catch (Throwable th) {
                        }
                        if (f544m) {
                            C0757w.m456a("[Native] Info setting jni can be accessed.", new Object[0]);
                        } else {
                            C0757w.m461d("[Native] Info setting jni can not be accessed.", new Object[0]);
                        }
                        if (f543l) {
                            C0757w.m456a("[Native] Extra jni can be accessed.", new Object[0]);
                        } else {
                            C0757w.m461d("[Native] Extra jni can not be accessed.", new Object[0]);
                        }
                        this.f546c.f141n = regist;
                        this.f553j = true;
                    }
                } catch (Throwable th2) {
                    C0757w.m460c("[Native] Failed to load Bugly SO file.", new Object[0]);
                }
            } else if (this.f551h) {
                try {
                    Class[] clsArr = new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE};
                    r4 = new Object[4];
                    C0705a.m85b();
                    r4[2] = Integer.valueOf(C0705a.m83J());
                    r4[3] = Integer.valueOf(1);
                    replace = (String) C0761y.m484a("com.tencent.feedback.eup.jni.NativeExceptionUpload", "registNativeExceptionHandler2", null, clsArr, r4);
                    if (replace == null) {
                        clsArr = new Class[]{String.class, String.class, Integer.TYPE};
                        r4 = new Object[3];
                        r4[0] = this.f549f;
                        r4[1] = C0706b.m141a(false);
                        C0705a.m85b();
                        r4[2] = Integer.valueOf(C0705a.m83J());
                        replace = (String) C0761y.m484a("com.tencent.feedback.eup.jni.NativeExceptionUpload", "registNativeExceptionHandler", null, clsArr, r4);
                    }
                    if (replace != null) {
                        this.f553j = true;
                        C0705a.m85b().f141n = replace;
                        C0761y.m484a("com.tencent.feedback.eup.jni.NativeExceptionUpload", "enableHandler", null, new Class[]{Boolean.TYPE}, new Object[]{Boolean.valueOf(true)});
                        int i = C0693b.f47c ? 3 : 5;
                        C0761y.m484a("com.tencent.feedback.eup.jni.NativeExceptionUpload", "setLogMode", null, new Class[]{Integer.TYPE}, new Object[]{Integer.valueOf(i)});
                    }
                } catch (Throwable th3) {
                }
            }
            this.f552i = false;
            this.f551h = false;
        }
    }

    public synchronized void startNativeMonitor() {
        if (this.f552i || this.f551h) {
            m540a(this.f550g);
        } else {
            String str;
            if (!C0761y.m501a(this.f546c.f140m)) {
                str = this.f546c.f140m;
            }
            str = "Bugly";
            this.f546c.getClass();
            this.f552i = m542a(C0761y.m501a(this.f546c.f140m) ? str : this.f546c.f140m, !C0761y.m501a(this.f546c.f140m));
            if (this.f552i || this.f551h) {
                m540a(this.f550g);
                this.f547d.m450a(new C07281(this));
            }
        }
    }

    private static boolean m542a(String str, boolean z) {
        Throwable th;
        boolean z2;
        try {
            C0757w.m456a("[Native] Trying to load so: %s", str);
            if (z) {
                System.load(str);
            } else {
                System.loadLibrary(str);
            }
            try {
                C0757w.m456a("[Native] Successfully loaded SO: %s", str);
                return true;
            } catch (Throwable th2) {
                th = th2;
                z2 = true;
            }
        } catch (Throwable th22) {
            th = th22;
            z2 = false;
            C0757w.m461d(th.getMessage(), new Object[0]);
            C0757w.m461d("[Native] Failed to load so: %s", str);
            return z2;
        }
    }

    private synchronized void m544b() {
        if (this.f553j) {
            try {
                if (unregist() != null) {
                    C0757w.m456a("[Native] Successfully closed native crash report.", new Object[0]);
                    this.f553j = false;
                }
            } catch (Throwable th) {
                C0757w.m460c("[Native] Failed to close native crash report.", new Object[0]);
            }
            try {
                C0761y.m484a("com.tencent.feedback.eup.jni.NativeExceptionUpload", "enableHandler", null, new Class[]{Boolean.TYPE}, new Object[]{Boolean.valueOf(false)});
                this.f553j = false;
                C0757w.m456a("[Native] Successfully closed native crash report.", new Object[0]);
            } catch (Throwable th2) {
                C0757w.m460c("[Native] Failed to close native crash report.", new Object[0]);
                this.f552i = false;
                this.f551h = false;
            }
        } else {
            C0757w.m461d("[Native] Native crash report has already unregistered.", new Object[0]);
        }
        return;
    }

    public void testNativeCrash() {
        if (this.f552i) {
            testCrash();
        } else {
            C0757w.m461d("[Native] Bugly SO file has not been load.", new Object[0]);
        }
    }

    public NativeExceptionHandler getNativeExceptionHandler() {
        return this.f548e;
    }

    protected final void m550a() {
        long b = C0761y.m509b() - C0721c.f291f;
        File file = new File(this.f549f);
        if (file.exists() && file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length != 0) {
                String str = "tomb_";
                String str2 = ".txt";
                int length = str.length();
                int i = 0;
                for (File file2 : listFiles) {
                    String name = file2.getName();
                    if (name.startsWith(str)) {
                        try {
                            int indexOf = name.indexOf(str2);
                            if (indexOf > 0 && Long.parseLong(name.substring(length, indexOf)) >= b) {
                            }
                        } catch (Throwable th) {
                            C0757w.m462e("[Native] Tomb file format error, delete %s", name);
                        }
                        if (file2.delete()) {
                            i++;
                        }
                    }
                }
                C0757w.m460c("[Native] Clean tombs %d", Integer.valueOf(i));
            }
        }
    }

    private synchronized void m545b(boolean z) {
        if (z) {
            startNativeMonitor();
        } else {
            m544b();
        }
    }

    public synchronized boolean isUserOpened() {
        return this.f554k;
    }

    private synchronized void m547c(boolean z) {
        if (this.f554k != z) {
            C0757w.m456a("user change native %b", Boolean.valueOf(z));
            this.f554k = z;
        }
    }

    public synchronized void setUserOpened(boolean z) {
        boolean z2 = true;
        synchronized (this) {
            m547c(z);
            boolean isUserOpened = isUserOpened();
            C0709a a = C0709a.m169a();
            if (a == null) {
                z2 = isUserOpened;
            } else if (!(isUserOpened && a.m177c().f162g)) {
                z2 = false;
            }
            if (z2 != this.f553j) {
                C0757w.m456a("native changed to %b", Boolean.valueOf(z2));
                m545b(z2);
            }
        }
    }

    public synchronized void onStrategyChanged(StrategyBean strategyBean) {
        boolean z = true;
        synchronized (this) {
            if (strategyBean != null) {
                if (strategyBean.f162g != this.f553j) {
                    C0757w.m461d("server native changed to %b", Boolean.valueOf(strategyBean.f162g));
                }
            }
            if (!(C0709a.m169a().m177c().f162g && this.f554k)) {
                z = false;
            }
            if (z != this.f553j) {
                C0757w.m456a("native changed to %b", Boolean.valueOf(z));
                m545b(z);
            }
        }
    }

    public boolean appendLogToNative(String str, String str2, String str3) {
        boolean z = false;
        if (!(!this.f552i || !f543l || str == null || str2 == null || str3 == null)) {
            try {
                z = appendNativeLog(str, str2, str3);
            } catch (UnsatisfiedLinkError e) {
                f543l = z;
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
        return z;
    }

    public boolean putKeyValueToNative(String str, String str2) {
        boolean z = false;
        if (this.f552i && f543l && str != null && str2 != null) {
            try {
                z = putNativeKeyValue(str, str2);
            } catch (UnsatisfiedLinkError e) {
                f543l = z;
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
        return z;
    }

    private boolean m541a(int i, String str) {
        if (!this.f552i || !f544m) {
            return false;
        }
        try {
            setNativeInfo(i, str);
            return true;
        } catch (UnsatisfiedLinkError e) {
            f544m = false;
            return false;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return false;
            }
            th.printStackTrace();
            return false;
        }
    }

    public boolean setNativeAppVersion(String str) {
        return m541a(10, str);
    }

    public boolean setNativeAppChannel(String str) {
        return m541a(12, str);
    }

    public boolean setNativeAppPackage(String str) {
        return m541a(13, str);
    }

    public boolean setNativeUserId(String str) {
        return m541a(11, str);
    }

    public boolean setNativeIsAppForeground(boolean z) {
        return m541a(14, z ? "true" : Bugly.SDK_IS_DEV);
    }

    public boolean setNativeLaunchTime(long j) {
        try {
            return m541a(15, String.valueOf(j));
        } catch (Throwable e) {
            if (!C0757w.m457a(e)) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
