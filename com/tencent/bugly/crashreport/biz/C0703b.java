package com.tencent.bugly.crashreport.biz;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.os.EnvironmentCompat;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.bugly.crashreport.biz.C0700a.C06962;
import com.tencent.bugly.crashreport.biz.C0700a.C0697a;
import com.tencent.bugly.crashreport.biz.C0700a.C0699c;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import com.tencent.bugly.proguard.C0756v;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.util.List;

/* compiled from: BUGLY */
public class C0703b {
    public static C0700a f85a;
    private static boolean f86b;
    private static int f87c = 10;
    private static long f88d = 300000;
    private static long f89e = 30000;
    private static long f90f = 0;
    private static int f91g;
    private static long f92h;
    private static long f93i;
    private static long f94j = 0;
    private static ActivityLifecycleCallbacks f95k = null;
    private static Class<?> f96l = null;
    private static boolean f97m = true;

    /* compiled from: BUGLY */
    static class C07022 implements ActivityLifecycleCallbacks {
        C07022() {
        }

        public final void onActivityStopped(Activity activity) {
        }

        public final void onActivityStarted(Activity activity) {
        }

        public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        public final void onActivityResumed(Activity activity) {
            String str = EnvironmentCompat.MEDIA_UNKNOWN;
            if (activity != null) {
                str = activity.getClass().getName();
            }
            if (C0703b.f96l == null || C0703b.f96l.getName().equals(str)) {
                C0757w.m460c(">>> %s onResumed <<<", str);
                C0705a b = C0705a.m85b();
                if (b != null) {
                    b.f104B.add(C0703b.m52a(str, "onResumed"));
                    b.m105a(true);
                    b.f143p = str;
                    b.f144q = System.currentTimeMillis();
                    b.f147t = b.f144q - C0703b.f93i;
                    if (b.f144q - C0703b.f92h > (C0703b.f90f > 0 ? C0703b.f90f : C0703b.f89e)) {
                        b.m112d();
                        C0703b.m67g();
                        C0757w.m456a("[session] launch app one times (app in background %d seconds and over %d seconds)", Long.valueOf(r4 / 1000), Long.valueOf(C0703b.f89e / 1000));
                        if (C0703b.f91g % C0703b.f87c == 0) {
                            C0703b.f85a.m50a(4, C0703b.f97m, 0);
                            return;
                        }
                        C0703b.f85a.m50a(4, false, 0);
                        long currentTimeMillis = System.currentTimeMillis();
                        if (currentTimeMillis - C0703b.f94j > C0703b.f88d) {
                            C0703b.f94j = currentTimeMillis;
                            C0757w.m456a("add a timer to upload hot start user info", new Object[0]);
                            if (C0703b.f97m) {
                                C0756v.m449a().m451a(new C0697a(C0703b.f85a, null, true), C0703b.f88d);
                            }
                        }
                    }
                }
            }
        }

        public final void onActivityPaused(Activity activity) {
            String str = EnvironmentCompat.MEDIA_UNKNOWN;
            if (activity != null) {
                str = activity.getClass().getName();
            }
            if (C0703b.f96l == null || C0703b.f96l.getName().equals(str)) {
                C0757w.m460c(">>> %s onPaused <<<", str);
                C0705a b = C0705a.m85b();
                if (b != null) {
                    b.f104B.add(C0703b.m52a(str, "onPaused"));
                    b.m105a(false);
                    b.f145r = System.currentTimeMillis();
                    b.f146s = b.f145r - b.f144q;
                    C0703b.f92h = b.f145r;
                    if (b.f146s < 0) {
                        b.f146s = 0;
                    }
                    if (activity != null) {
                        b.f143p = "background";
                    } else {
                        b.f143p = EnvironmentCompat.MEDIA_UNKNOWN;
                    }
                }
            }
        }

        public final void onActivityDestroyed(Activity activity) {
        }

        public final void onActivityCreated(Activity activity, Bundle bundle) {
            String str = EnvironmentCompat.MEDIA_UNKNOWN;
            if (activity != null) {
                str = activity.getClass().getName();
            }
            if (C0703b.f96l == null || C0703b.f96l.getName().equals(str)) {
                C0757w.m460c(">>> %s onCreated <<<", str);
                C0705a b = C0705a.m85b();
                if (b != null) {
                    b.f104B.add(C0703b.m52a(str, "onCreated"));
                }
            }
        }
    }

    static /* synthetic */ String m52a(String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C0761y.m486a());
        stringBuilder.append("  ");
        stringBuilder.append(str);
        stringBuilder.append("  ");
        stringBuilder.append(str2);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    static /* synthetic */ int m67g() {
        int i = f91g;
        f91g = i + 1;
        return i;
    }

    private static void m63c(Context context, BuglyStrategy buglyStrategy) {
        boolean isEnableUserInfo;
        boolean z;
        if (buglyStrategy != null) {
            boolean recordUserInfoOnceADay = buglyStrategy.recordUserInfoOnceADay();
            isEnableUserInfo = buglyStrategy.isEnableUserInfo();
            z = recordUserInfoOnceADay;
        } else {
            isEnableUserInfo = true;
            z = false;
        }
        if (z) {
            Object obj;
            C0705a a = C0705a.m84a(context);
            List a2 = f85a.m48a(a.f131d);
            if (a2 != null) {
                for (int i = 0; i < a2.size(); i++) {
                    UserInfoBean userInfoBean = (UserInfoBean) a2.get(i);
                    if (userInfoBean.f66n.equals(a.f137j) && userInfoBean.f54b == 1) {
                        long b = C0761y.m509b();
                        if (b <= 0) {
                            break;
                        } else if (userInfoBean.f57e >= b) {
                            if (userInfoBean.f58f <= 0) {
                                C0700a c0700a = f85a;
                                C0756v a3 = C0756v.m449a();
                                if (a3 != null) {
                                    a3.m450a(new C06962(c0700a));
                                }
                            }
                            obj = null;
                            if (obj == null) {
                                isEnableUserInfo = false;
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
            obj = 1;
            if (obj == null) {
                isEnableUserInfo = false;
            } else {
                return;
            }
        }
        C0705a b2 = C0705a.m85b();
        if (b2 != null) {
            Object obj2 = null;
            String str = null;
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                if (stackTraceElement.getMethodName().equals("onCreate")) {
                    str = stackTraceElement.getClassName();
                }
                if (stackTraceElement.getClassName().equals("android.app.Activity")) {
                    obj2 = 1;
                }
            }
            if (str == null) {
                str = EnvironmentCompat.MEDIA_UNKNOWN;
            } else if (obj2 != null) {
                b2.m105a(true);
            } else {
                str = "background";
            }
            b2.f143p = str;
        }
        if (isEnableUserInfo) {
            Application application = null;
            if (VERSION.SDK_INT >= 14) {
                if (context.getApplicationContext() instanceof Application) {
                    application = (Application) context.getApplicationContext();
                }
                if (application != null) {
                    try {
                        if (f95k == null) {
                            f95k = new C07022();
                        }
                        application.registerActivityLifecycleCallbacks(f95k);
                    } catch (Throwable e) {
                        if (!C0757w.m457a(e)) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (f97m) {
            f93i = System.currentTimeMillis();
            f85a.m50a(1, false, 0);
            C0757w.m456a("[session] launch app, new start", new Object[0]);
            f85a.m49a();
            C0756v.m449a().m451a(new C0699c(f85a, 21600000), 21600000);
        }
    }

    public static void m56a(final Context context, final BuglyStrategy buglyStrategy) {
        if (!f86b) {
            long appReportDelay;
            f97m = C0705a.m84a(context).f132e;
            f85a = new C0700a(context, f97m);
            f86b = true;
            if (buglyStrategy != null) {
                f96l = buglyStrategy.getUserInfoActivity();
                appReportDelay = buglyStrategy.getAppReportDelay();
            } else {
                appReportDelay = 0;
            }
            if (appReportDelay <= 0) {
                C0703b.m63c(context, buglyStrategy);
            } else {
                C0756v.m449a().m451a(new Runnable() {
                    public final void run() {
                        C0703b.m63c(context, buglyStrategy);
                    }
                }, appReportDelay);
            }
        }
    }

    public static void m54a(long j) {
        if (j < 0) {
            j = C0709a.m169a().m177c().f172q;
        }
        f90f = j;
    }

    public static void m57a(StrategyBean strategyBean, boolean z) {
        if (!(f85a == null || z)) {
            C0700a c0700a = f85a;
            C0756v a = C0756v.m449a();
            if (a != null) {
                a.m450a(new C06962(c0700a));
            }
        }
        if (strategyBean != null) {
            if (strategyBean.f172q > 0) {
                f89e = strategyBean.f172q;
            }
            if (strategyBean.f178w > 0) {
                f87c = strategyBean.f178w;
            }
            if (strategyBean.f179x > 0) {
                f88d = strategyBean.f179x;
            }
        }
    }

    public static void m53a() {
        if (f85a != null) {
            f85a.m50a(2, false, 0);
        }
    }

    public static void m55a(Context context) {
        if (f86b && context != null) {
            Application application = null;
            if (VERSION.SDK_INT >= 14) {
                if (context.getApplicationContext() instanceof Application) {
                    application = (Application) context.getApplicationContext();
                }
                if (application != null) {
                    try {
                        if (f95k != null) {
                            application.unregisterActivityLifecycleCallbacks(f95k);
                        }
                    } catch (Throwable e) {
                        if (!C0757w.m457a(e)) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            f86b = false;
        }
    }
}
