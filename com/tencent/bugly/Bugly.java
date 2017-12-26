package com.tencent.bugly;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.proguard.C0745o;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.util.Map;

/* compiled from: BUGLY */
public class Bugly {
    public static final String SDK_IS_DEV = "false";
    private static boolean f27a;
    public static Context applicationContext = null;
    private static String[] f28b = new String[]{"BuglyCrashModule", "BuglyRqdModule", "BuglyBetaModule"};
    private static String[] f29c = new String[]{"BuglyRqdModule", "BuglyCrashModule", "BuglyBetaModule"};
    public static boolean enable = true;
    public static Boolean isDev;

    public static void init(Context context, String str, boolean z) {
        init(context, str, z, null);
    }

    public static synchronized void init(Context context, String str, boolean z, BuglyStrategy buglyStrategy) {
        synchronized (Bugly.class) {
            if (!f27a) {
                f27a = true;
                Context a = C0761y.m481a(context);
                applicationContext = a;
                if (a == null) {
                    Log.e(C0757w.f463a, "init arg 'context' should not be null!");
                } else {
                    if (isDev()) {
                        f28b = f29c;
                    }
                    for (String str2 : f28b) {
                        try {
                            if (str2.equals("BuglyCrashModule")) {
                                C0693b.m33a(CrashModule.getInstance());
                            } else if (!(str2.equals("BuglyBetaModule") || str2.equals("BuglyRqdModule"))) {
                                str2.equals("BuglyFeedbackModule");
                            }
                        } catch (Throwable th) {
                            C0757w.m459b(th);
                        }
                    }
                    C0693b.f45a = enable;
                    C0693b.m32a(applicationContext, str, z, buglyStrategy);
                }
            }
        }
    }

    public static synchronized String getAppChannel() {
        String str = null;
        synchronized (Bugly.class) {
            C0705a b = C0705a.m85b();
            if (b != null) {
                if (TextUtils.isEmpty(b.f139l)) {
                    C0745o a = C0745o.m380a();
                    if (a == null) {
                        str = b.f139l;
                    } else {
                        Map a2 = a.m398a(556, null, true);
                        if (a2 != null) {
                            byte[] bArr = (byte[]) a2.get("app_channel");
                            if (bArr != null) {
                                str = new String(bArr);
                            }
                        }
                    }
                }
                str = b.f139l;
            }
        }
        return str;
    }

    public static boolean isDev() {
        if (isDev == null) {
            isDev = Boolean.valueOf(Boolean.parseBoolean(SDK_IS_DEV.replace("@", "")));
        }
        return isDev.booleanValue();
    }
}
