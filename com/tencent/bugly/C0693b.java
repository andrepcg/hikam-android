package com.tencent.bugly;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.tencent.bugly.crashreport.biz.C0703b;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.proguard.C0742m;
import com.tencent.bugly.proguard.C0745o;
import com.tencent.bugly.proguard.C0753t;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0760x;
import com.tencent.bugly.proguard.C0761y;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* compiled from: BUGLY */
public final class C0693b {
    public static boolean f45a = true;
    public static List<C0692a> f46b = new ArrayList();
    public static boolean f47c;
    private static C0745o f48d;
    private static C0709a f49e;
    private static C0742m f50f;
    private static boolean f51g;

    private static boolean m34a(C0705a c0705a) {
        List list = c0705a.f142o;
        c0705a.getClass();
        String str = "bugly";
        if (list == null || !list.contains(str)) {
            return false;
        }
        return true;
    }

    public static synchronized void m30a(Context context) {
        synchronized (C0693b.class) {
            C0693b.m31a(context, null);
        }
    }

    public static synchronized void m31a(Context context, BuglyStrategy buglyStrategy) {
        synchronized (C0693b.class) {
            if (f51g) {
                C0757w.m461d("[init] initial Multi-times, ignore this.", new Object[0]);
            } else if (context == null) {
                Log.w(C0757w.f463a, "[init] context of init() is null, check it.");
            } else {
                C0705a a = C0705a.m84a(context);
                if (C0693b.m34a(a)) {
                    f45a = false;
                } else {
                    String f = a.m116f();
                    if (f == null) {
                        Log.e(C0757w.f463a, "[init] meta data of BUGLY_APPID in AndroidManifest.xml should be set.");
                    } else {
                        C0693b.m32a(context, f, a.f148u, buglyStrategy);
                    }
                }
            }
        }
    }

    public static synchronized void m32a(Context context, String str, boolean z, BuglyStrategy buglyStrategy) {
        synchronized (C0693b.class) {
            if (f51g) {
                C0757w.m461d("[init] initial Multi-times, ignore this.", new Object[0]);
            } else if (context == null) {
                Log.w(C0757w.f463a, "[init] context is null, check it.");
            } else if (str == null) {
                Log.e(C0757w.f463a, "init arg 'crashReportAppID' should not be null!");
            } else {
                f51g = true;
                if (z) {
                    f47c = true;
                    C0757w.f464b = true;
                    C0757w.m461d("Bugly debug模式开启，请在发布时把isDebug关闭。 -- Running in debug model for 'isDebug' is enabled. Please disable it when you release.", new Object[0]);
                    C0757w.m462e("--------------------------------------------------------------------------------------------", new Object[0]);
                    C0757w.m461d("Bugly debug模式将有以下行为特性 -- The following list shows the behaviour of debug model: ", new Object[0]);
                    C0757w.m461d("[1] 输出详细的Bugly SDK的Log -- More detailed log of Bugly SDK will be output to logcat;", new Object[0]);
                    C0757w.m461d("[2] 每一条Crash都会被立即上报 -- Every crash caught by Bugly will be uploaded immediately.", new Object[0]);
                    C0757w.m461d("[3] 自定义日志将会在Logcat中输出 -- Custom log will be output to logcat.", new Object[0]);
                    C0757w.m462e("--------------------------------------------------------------------------------------------", new Object[0]);
                    C0757w.m458b("[init] Open debug mode of Bugly.", new Object[0]);
                }
                C0757w.m456a("[init] Bugly version: v%s", "2.4.0");
                C0757w.m456a(" crash report start initializing...", new Object[0]);
                C0757w.m458b("[init] Bugly start initializing...", new Object[0]);
                C0757w.m456a("[init] Bugly complete version: v%s", "2.4.0(1.2.1)");
                Context a = C0761y.m481a(context);
                C0705a a2 = C0705a.m84a(a);
                a2.m132t();
                C0760x.m472a(a);
                f48d = C0745o.m381a(a, f46b);
                C0753t.m413a(a);
                f49e = C0709a.m170a(a, f46b);
                f50f = C0742m.m357a(a);
                if (C0693b.m34a(a2)) {
                    f45a = false;
                } else {
                    a2.m103a(str);
                    C0757w.m456a("[param] Set APP ID:%s", str);
                    if (buglyStrategy != null) {
                        String substring;
                        String appVersion = buglyStrategy.getAppVersion();
                        if (!TextUtils.isEmpty(appVersion)) {
                            if (appVersion.length() > 100) {
                                substring = appVersion.substring(0, 100);
                                C0757w.m461d("appVersion %s length is over limit %d substring to %s", appVersion, Integer.valueOf(100), substring);
                            } else {
                                substring = appVersion;
                            }
                            a2.f137j = substring;
                            C0757w.m456a("[param] Set App version: %s", buglyStrategy.getAppVersion());
                        }
                        try {
                            if (buglyStrategy.isReplaceOldChannel()) {
                                appVersion = buglyStrategy.getAppChannel();
                                if (!TextUtils.isEmpty(appVersion)) {
                                    String str2;
                                    if (appVersion.length() > 100) {
                                        C0757w.m461d("appChannel %s length is over limit %d substring to %s", appVersion, Integer.valueOf(100), appVersion.substring(0, 100));
                                        str2 = substring;
                                    } else {
                                        str2 = appVersion;
                                    }
                                    f48d.m401a(556, "app_channel", str2.getBytes(), null, false);
                                    a2.f139l = str2;
                                }
                            } else {
                                Map a3 = f48d.m398a(556, null, true);
                                if (a3 != null) {
                                    byte[] bArr = (byte[]) a3.get("app_channel");
                                    if (bArr != null) {
                                        a2.f139l = new String(bArr);
                                    }
                                }
                            }
                            C0757w.m456a("[param] Set App channel: %s", a2.f139l);
                        } catch (Exception e) {
                            if (f47c) {
                                e.printStackTrace();
                            }
                        }
                        appVersion = buglyStrategy.getAppPackageName();
                        if (!TextUtils.isEmpty(appVersion)) {
                            if (appVersion.length() > 100) {
                                substring = appVersion.substring(0, 100);
                                C0757w.m461d("appPackageName %s length is over limit %d substring to %s", appVersion, Integer.valueOf(100), substring);
                            } else {
                                substring = appVersion;
                            }
                            a2.f130c = substring;
                            C0757w.m456a("[param] Set App package: %s", buglyStrategy.getAppPackageName());
                        }
                        appVersion = buglyStrategy.getDeviceID();
                        if (appVersion != null) {
                            if (appVersion.length() > 100) {
                                substring = appVersion.substring(0, 100);
                                C0757w.m461d("deviceId %s length is over limit %d substring to %s", appVersion, Integer.valueOf(100), substring);
                            } else {
                                substring = appVersion;
                            }
                            a2.m110c(substring);
                            C0757w.m456a("s[param] Set device ID: %s", substring);
                        }
                        a2.f132e = buglyStrategy.isUploadProcess();
                        C0760x.f472a = buglyStrategy.isBuglyLogUpload();
                    }
                    C0703b.m56a(a, buglyStrategy);
                    for (int i = 0; i < f46b.size(); i++) {
                        try {
                            if (f50f.m368a(((C0692a) f46b.get(i)).id)) {
                                ((C0692a) f46b.get(i)).init(a, z, buglyStrategy);
                            }
                        } catch (Throwable th) {
                            if (!C0757w.m457a(th)) {
                                th.printStackTrace();
                            }
                        }
                    }
                    f49e.m173a(buglyStrategy != null ? buglyStrategy.getAppReportDelay() : 0);
                    C0757w.m458b("[init] Bugly initialization finished.", new Object[0]);
                }
            }
        }
    }

    public static synchronized void m33a(C0692a c0692a) {
        synchronized (C0693b.class) {
            if (!f46b.contains(c0692a)) {
                f46b.add(c0692a);
            }
        }
    }
}
