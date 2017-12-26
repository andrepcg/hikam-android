package com.tencent.bugly;

import android.content.Context;
import android.text.TextUtils;
import com.jwkj.global.Constants.Action;
import com.tencent.bugly.BuglyStrategy.C0691a;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import com.tencent.bugly.crashreport.crash.BuglyBroadcastRecevier;
import com.tencent.bugly.crashreport.crash.C0721c;
import com.tencent.bugly.crashreport.crash.C0724d;
import com.tencent.bugly.proguard.C0742m;
import com.tencent.bugly.proguard.C0757w;

/* compiled from: BUGLY */
public class CrashModule extends C0692a {
    public static final int MODULE_ID = 1004;
    private static int f528c = 0;
    private static boolean f529d = false;
    private static CrashModule f530e = new CrashModule();
    private long f531a;
    private C0691a f532b;

    public static CrashModule getInstance() {
        f530e.id = 1004;
        return f530e;
    }

    public static boolean hasInitialized() {
        return f529d;
    }

    public synchronized void init(Context context, boolean z, BuglyStrategy buglyStrategy) {
        if (context != null) {
            if (!f529d) {
                C0757w.m456a("Initializing crash module.", new Object[0]);
                C0742m a = C0742m.m356a();
                int i = f528c + 1;
                f528c = i;
                a.m367a(1004, i);
                f529d = true;
                CrashReport.setContext(context);
                m530a(context, buglyStrategy);
                C0721c.m220a(1004, context, z, this.f532b, null, null);
                C0721c a2 = C0721c.m218a();
                a2.m229e();
                if (buglyStrategy == null || buglyStrategy.isEnableNativeCrashMonitor()) {
                    a2.m231g();
                } else {
                    C0757w.m456a("[crash] Closed native crash monitor!", new Object[0]);
                    a2.m230f();
                }
                if (buglyStrategy == null || buglyStrategy.isEnableANRCrashMonitor()) {
                    a2.m232h();
                } else {
                    C0757w.m456a("[crash] Closed ANR monitor!", new Object[0]);
                    a2.m233i();
                }
                C0724d.m238a(context);
                BuglyBroadcastRecevier instance = BuglyBroadcastRecevier.getInstance();
                instance.addFilter(Action.ACTION_NETWORK_CHANGE);
                instance.regist(context);
                a = C0742m.m356a();
                i = f528c - 1;
                f528c = i;
                a.m367a(1004, i);
            }
        }
    }

    private synchronized void m530a(Context context, BuglyStrategy buglyStrategy) {
        if (buglyStrategy != null) {
            Object libBuglySOFilePath = buglyStrategy.getLibBuglySOFilePath();
            if (!TextUtils.isEmpty(libBuglySOFilePath)) {
                C0705a.m84a(context).f140m = libBuglySOFilePath;
                C0757w.m456a("setted libBugly.so file path :%s", libBuglySOFilePath);
            }
            if (buglyStrategy.getCrashHandleCallback() != null) {
                this.f532b = buglyStrategy.getCrashHandleCallback();
                C0757w.m456a("setted CrashHanldeCallback", new Object[0]);
            }
            if (buglyStrategy.getAppReportDelay() > 0) {
                this.f531a = buglyStrategy.getAppReportDelay();
                C0757w.m456a("setted delay: %d", Long.valueOf(this.f531a));
            }
        }
    }

    public void onServerStrategyChanged(StrategyBean strategyBean) {
        if (strategyBean != null) {
            C0721c a = C0721c.m218a();
            if (a != null) {
                a.m223a(strategyBean);
            }
        }
    }

    public String[] getTables() {
        return new String[]{"t_cr"};
    }
}
