package com.tencent.bugly.crashreport.inner;

import com.tencent.bugly.crashreport.crash.C0724d;
import com.tencent.bugly.proguard.C0757w;
import java.util.Map;

/* compiled from: BUGLY */
public class InnerApi {
    public static void postU3dCrashAsync(String str, String str2, String str3) {
        if (str == null || str2 == null || str3 == null) {
            C0757w.m462e("post u3d fail args null", new Object[0]);
        }
        C0757w.m456a("post u3d crash %s %s", str, str2);
        C0724d.m241a(Thread.currentThread(), 4, str, str2, str3, null);
    }

    public static void postCocos2dxCrashAsync(int i, String str, String str2, String str3) {
        if (str == null || str2 == null || str3 == null) {
            C0757w.m462e("post cocos2d-x fail args null", new Object[0]);
        } else if (i == 5 || i == 6) {
            C0757w.m456a("post cocos2d-x crash %s %s", str, str2);
            C0724d.m241a(Thread.currentThread(), i, str, str2, str3, null);
        } else {
            C0757w.m462e("post cocos2d-x fail category illeagle: %d", Integer.valueOf(i));
        }
    }

    public static void postH5CrashAsync(Thread thread, String str, String str2, String str3, Map<String, String> map) {
        if (str == null || str2 == null || str3 == null) {
            C0757w.m462e("post h5 fail args null", new Object[0]);
            return;
        }
        C0757w.m456a("post h5 crash %s %s", str, str2);
        C0724d.m241a(thread, 8, str, str2, str3, map);
    }
}
