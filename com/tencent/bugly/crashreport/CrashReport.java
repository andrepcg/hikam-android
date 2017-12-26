package com.tencent.bugly.crashreport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.bugly.BuglyStrategy.C0691a;
import com.tencent.bugly.C0693b;
import com.tencent.bugly.CrashModule;
import com.tencent.bugly.crashreport.biz.C0703b;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.crash.BuglyBroadcastRecevier;
import com.tencent.bugly.crashreport.crash.C0721c;
import com.tencent.bugly.crashreport.crash.h5.C0727b;
import com.tencent.bugly.crashreport.crash.h5.H5JavaScriptInterface;
import com.tencent.bugly.crashreport.crash.jni.NativeCrashHandler;
import com.tencent.bugly.proguard.C0756v;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* compiled from: BUGLY */
public class CrashReport {
    private static Context f52a;

    /* compiled from: BUGLY */
    public static class CrashHandleCallback extends C0691a {
    }

    /* compiled from: BUGLY */
    public static class UserStrategy extends BuglyStrategy {
        private CrashHandleCallback f533a;

        public UserStrategy(Context context) {
        }

        public synchronized CrashHandleCallback getCrashHandleCallback() {
            return this.f533a;
        }

        public synchronized void setCrashHandleCallback(CrashHandleCallback crashHandleCallback) {
            this.f533a = crashHandleCallback;
        }
    }

    public static void enableBugly(boolean z) {
        C0693b.f45a = z;
    }

    public static void initCrashReport(Context context) {
        f52a = context;
        C0693b.m33a(CrashModule.getInstance());
        C0693b.m30a(context);
    }

    public static void initCrashReport(Context context, UserStrategy userStrategy) {
        f52a = context;
        C0693b.m33a(CrashModule.getInstance());
        C0693b.m31a(context, userStrategy);
    }

    public static void initCrashReport(Context context, String str, boolean z) {
        if (context != null) {
            f52a = context;
            C0693b.m33a(CrashModule.getInstance());
            C0693b.m32a(context, str, z, null);
        }
    }

    public static void initCrashReport(Context context, String str, boolean z, UserStrategy userStrategy) {
        if (context != null) {
            f52a = context;
            C0693b.m33a(CrashModule.getInstance());
            C0693b.m32a(context, str, z, userStrategy);
        }
    }

    public static String getBuglyVersion(Context context) {
        if (context == null) {
            C0757w.m461d("Please call with context.", new Object[0]);
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
        C0705a.m84a(context);
        return C0705a.m86c();
    }

    public static void testJavaCrash() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not test Java crash because bugly is disable.");
        } else if (CrashModule.hasInitialized()) {
            C0705a b = C0705a.m85b();
            if (b != null) {
                b.m107b(24096);
            }
            throw new RuntimeException("This Crash create for Test! You can go to Bugly see more detail!");
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        }
    }

    public static void testNativeCrash() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not test native crash because bugly is disable.");
        } else if (CrashModule.hasInitialized()) {
            C0757w.m456a("start to create a native crash for test!", new Object[0]);
            C0721c.m218a().m234j();
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        }
    }

    public static void testANRCrash() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not test ANR crash because bugly is disable.");
        } else if (CrashModule.hasInitialized()) {
            C0757w.m456a("start to create a anr crash for test!", new Object[0]);
            C0721c.m218a().m235k();
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        }
    }

    public static void postCatchedException(Throwable th) {
        postCatchedException(th, Thread.currentThread(), false);
    }

    public static void postCatchedException(Throwable th, Thread thread) {
        postCatchedException(th, thread, false);
    }

    public static void postCatchedException(Throwable th, Thread thread, boolean z) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not post crash caught because bugly is disable.");
        } else if (!CrashModule.hasInitialized()) {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        } else if (th == null) {
            C0757w.m461d("throwable is null, just return", new Object[0]);
        } else {
            Thread currentThread;
            if (thread == null) {
                currentThread = Thread.currentThread();
            } else {
                currentThread = thread;
            }
            C0721c.m218a().m225a(currentThread, th, false, null, null, z);
        }
    }

    public static void closeNativeReport() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not close native report because bugly is disable.");
        } else if (CrashModule.hasInitialized()) {
            C0721c.m218a().m230f();
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        }
    }

    public static void startCrashReport() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not start crash report because bugly is disable.");
        } else if (CrashModule.hasInitialized()) {
            C0721c.m218a().m227c();
        } else {
            Log.w(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        }
    }

    public static void closeCrashReport() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not close crash report because bugly is disable.");
        } else if (CrashModule.hasInitialized()) {
            C0721c.m218a().m228d();
        } else {
            Log.w(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        }
    }

    public static void closeBugly() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not close bugly because bugly is disable.");
        } else if (!CrashModule.hasInitialized()) {
            Log.w(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        } else if (f52a != null) {
            BuglyBroadcastRecevier instance = BuglyBroadcastRecevier.getInstance();
            if (instance != null) {
                instance.unregist(f52a);
            }
            closeCrashReport();
            C0703b.m55a(f52a);
            C0756v a = C0756v.m449a();
            if (a != null) {
                a.m452b();
            }
        }
    }

    public static void setUserSceneTag(Context context, int i) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not set tag caught because bugly is disable.");
        } else if (context == null) {
            Log.e(C0757w.f463a, "setTag args context should not be null");
        } else {
            if (i <= 0) {
                C0757w.m461d("setTag args tagId should > 0", new Object[0]);
            }
            C0705a.m84a(context).m102a(i);
            C0757w.m458b("[param] set user scene tag: %d", Integer.valueOf(i));
        }
    }

    public static int getUserSceneTagId(Context context) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get user scene tag because bugly is disable.");
            return -1;
        } else if (context != null) {
            return C0705a.m84a(context).m92F();
        } else {
            Log.e(C0757w.f463a, "getUserSceneTagId args context should not be null");
            return -1;
        }
    }

    public static String getUserData(Context context, String str) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get user data because bugly is disable.");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (context == null) {
            Log.e(C0757w.f463a, "getUserDataValue args context should not be null");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (C0761y.m501a(str)) {
            return null;
        } else {
            return C0705a.m84a(context).m119g(str);
        }
    }

    public static void putUserData(Context context, String str, String str2) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not put user data because bugly is disable.");
        } else if (context == null) {
            Log.w(C0757w.f463a, "putUserData args context should not be null");
        } else if (str == null) {
            str;
            C0757w.m461d("putUserData args key should not be null or empty", new Object[0]);
        } else if (str2 == null) {
            str2;
            C0757w.m461d("putUserData args value should not be null", new Object[0]);
        } else if (str.matches("[a-zA-Z[0-9]]+")) {
            if (str2.length() > 200) {
                C0757w.m461d("user data value length over limit %d, it will be cutted!", Integer.valueOf(200));
                str2 = str2.substring(0, 200);
            }
            C0705a a = C0705a.m84a(context);
            NativeCrashHandler instance;
            if (a.m89C().contains(str)) {
                instance = NativeCrashHandler.getInstance();
                if (instance != null) {
                    instance.putKeyValueToNative(str, str2);
                }
                C0705a.m84a(context).m109b(str, str2);
                C0757w.m460c("replace KV %s %s", str, str2);
            } else if (a.m88B() >= 10) {
                C0757w.m461d("user data size is over limit %d, it will be cutted!", Integer.valueOf(10));
            } else {
                if (str.length() > 50) {
                    C0757w.m461d("user data key length over limit %d , will drop this new key %s", Integer.valueOf(50), str);
                    str = str.substring(0, 50);
                }
                instance = NativeCrashHandler.getInstance();
                if (instance != null) {
                    instance.putKeyValueToNative(str, str2);
                }
                C0705a.m84a(context).m109b(str, str2);
                C0757w.m458b("[param] set user data: %s - %s", str, str2);
            }
        } else {
            C0757w.m461d("putUserData args key should match [a-zA-Z[0-9]]+  {" + str + "}", new Object[0]);
        }
    }

    public static String removeUserData(Context context, String str) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not remove user data because bugly is disable.");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (context == null) {
            Log.e(C0757w.f463a, "removeUserData args context should not be null");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (C0761y.m501a(str)) {
            return null;
        } else {
            C0757w.m458b("[param] remove user data: %s", str);
            return C0705a.m84a(context).m117f(str);
        }
    }

    public static Set<String> getAllUserDataKeys(Context context) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get all keys of user data because bugly is disable.");
            return new HashSet();
        } else if (context != null) {
            return C0705a.m84a(context).m89C();
        } else {
            Log.e(C0757w.f463a, "getAllUserDataKeys args context should not be null");
            return new HashSet();
        }
    }

    public static int getUserDatasSize(Context context) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get size of user data because bugly is disable.");
            return -1;
        } else if (context != null) {
            return C0705a.m84a(context).m88B();
        } else {
            Log.e(C0757w.f463a, "getUserDatasSize args context should not be null");
            return -1;
        }
    }

    public static String getAppID() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get App ID because bugly is disable.");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (CrashModule.hasInitialized()) {
            return C0705a.m84a(f52a).m116f();
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }

    public static void setUserId(String str) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not set user ID because bugly is disable.");
        } else if (CrashModule.hasInitialized()) {
            setUserId(f52a, str);
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
        }
    }

    public static void setUserId(Context context, String str) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not set user ID because bugly is disable.");
        } else if (context == null) {
            Log.e(C0757w.f463a, "Context should not be null when bugly has not been initialed!");
        } else if (str == null) {
            C0757w.m461d("userId should not be null", new Object[0]);
        } else {
            if (str.length() > 100) {
                C0757w.m461d("userId %s length is over limit %d substring to %s", str, Integer.valueOf(100), str.substring(0, 100));
                str = r0;
            }
            if (!str.equals(C0705a.m84a(context).m118g())) {
                C0705a.m84a(context).m108b(str);
                C0757w.m458b("[user] set userId : %s", str);
                NativeCrashHandler instance = NativeCrashHandler.getInstance();
                if (instance != null) {
                    instance.setNativeUserId(str);
                }
                if (CrashModule.hasInitialized()) {
                    C0703b.m53a();
                }
            }
        }
    }

    public static String getUserId() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get user ID because bugly is disable.");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (CrashModule.hasInitialized()) {
            return C0705a.m84a(f52a).m118g();
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }

    public static String getAppVer() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get app version because bugly is disable.");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (CrashModule.hasInitialized()) {
            return C0705a.m84a(f52a).f137j;
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }

    public static String getAppChannel() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get App channel because bugly is disable.");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (CrashModule.hasInitialized()) {
            return C0705a.m84a(f52a).f139l;
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }

    public static void setContext(Context context) {
        f52a = context;
    }

    public static boolean isLastSessionCrash() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "The info 'isLastSessionCrash' is not accurate because bugly is disable.");
            return false;
        } else if (CrashModule.hasInitialized()) {
            return C0721c.m218a().m226b();
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
            return false;
        }
    }

    public static void setSdkExtraData(Context context, String str, String str2) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not put SDK extra data because bugly is disable.");
        } else if (context != null && !C0761y.m501a(str) && !C0761y.m501a(str2)) {
            C0705a.m84a(context).m104a(str, str2);
        }
    }

    public static Map<String, String> getSdkExtraData() {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get SDK extra data because bugly is disable.");
            return new HashMap();
        } else if (CrashModule.hasInitialized()) {
            return C0705a.m84a(f52a).f103A;
        } else {
            Log.e(C0757w.f463a, "CrashReport has not been initialed! pls to call method 'initCrashReport' first!");
            return null;
        }
    }

    public static Map<String, String> getSdkExtraData(Context context) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not get SDK extra data because bugly is disable.");
            return new HashMap();
        } else if (context != null) {
            return C0705a.m84a(context).f103A;
        } else {
            C0757w.m461d("Context should not be null.", new Object[0]);
            return null;
        }
    }

    private static void putSdkData(Context context, String str, String str2) {
        if (context != null && !C0761y.m501a(str) && !C0761y.m501a(str2)) {
            String replace = str.replace("[a-zA-Z[0-9]]+", "");
            if (replace.length() > 100) {
                Log.w(C0757w.f463a, String.format("putSdkData key length over limit %d, will be cutted.", new Object[]{Integer.valueOf(50)}));
                replace = replace.substring(0, 50);
            }
            if (str2.length() > 500) {
                Log.w(C0757w.f463a, String.format("putSdkData value length over limit %d, will be cutted!", new Object[]{Integer.valueOf(200)}));
                str2 = str2.substring(0, 200);
            }
            C0705a.m84a(context).m111c(replace, str2);
            C0757w.m458b(String.format("[param] putSdkData data: %s - %s", new Object[]{replace, str2}), new Object[0]);
        }
    }

    public static void setIsAppForeground(Context context, boolean z) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not set 'isAppForeground' because bugly is disable.");
        } else if (context == null) {
            C0757w.m461d("Context should not be null.", new Object[0]);
        } else {
            if (z) {
                C0757w.m460c("App is in foreground.", new Object[0]);
            } else {
                C0757w.m460c("App is in background.", new Object[0]);
            }
            C0705a.m84a(context).m105a(z);
        }
    }

    public static void setIsDevelopmentDevice(Context context, boolean z) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not set 'isDevelopmentDevice' because bugly is disable.");
        } else if (context == null) {
            C0757w.m461d("Context should not be null.", new Object[0]);
        } else {
            if (z) {
                C0757w.m460c("This is a development device.", new Object[0]);
            } else {
                C0757w.m460c("This is not a development device.", new Object[0]);
            }
            C0705a.m84a(context).f152y = z;
        }
    }

    public static void setSessionIntervalMills(long j) {
        if (C0693b.f45a) {
            C0703b.m54a(j);
        } else {
            Log.w(C0757w.f463a, "Can not set 'SessionIntervalMills' because bugly is disable.");
        }
    }

    public static void setAppVersion(Context context, String str) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not set App version because bugly is disable.");
        } else if (context == null) {
            Log.w(C0757w.f463a, "setAppVersion args context should not be null");
        } else if (str == null) {
            Log.w(C0757w.f463a, "App version is null, will not set");
        } else {
            C0705a.m84a(context).f137j = str;
            NativeCrashHandler instance = NativeCrashHandler.getInstance();
            if (instance != null) {
                instance.setNativeAppVersion(str);
            }
        }
    }

    public static void setAppChannel(Context context, String str) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not set App channel because Bugly is disable.");
        } else if (context == null) {
            Log.w(C0757w.f463a, "setAppChannel args context should not be null");
        } else if (str == null) {
            Log.w(C0757w.f463a, "App channel is null, will not set");
        } else {
            C0705a.m84a(context).f139l = str;
            NativeCrashHandler instance = NativeCrashHandler.getInstance();
            if (instance != null) {
                instance.setNativeAppChannel(str);
            }
        }
    }

    public static void setAppPackage(Context context, String str) {
        if (!C0693b.f45a) {
            Log.w(C0757w.f463a, "Can not set App package because bugly is disable.");
        } else if (context == null) {
            Log.w(C0757w.f463a, "setAppPackage args context should not be null");
        } else if (str == null) {
            Log.w(C0757w.f463a, "App package is null, will not set");
        } else {
            C0705a.m84a(context).f130c = str;
            NativeCrashHandler instance = NativeCrashHandler.getInstance();
            if (instance != null) {
                instance.setNativeAppPackage(str);
            }
        }
    }

    public static void setCrashFilter(String str) {
        if (C0693b.f45a) {
            Log.w(C0757w.f463a, "Set crash stack filter: " + str);
            C0721c.f297l = str;
            return;
        }
        Log.w(C0757w.f463a, "Can not set App package because bugly is disable.");
    }

    public static void setCrashRegularFilter(String str) {
        if (C0693b.f45a) {
            Log.w(C0757w.f463a, "Set crash stack filter: " + str);
            C0721c.f298m = str;
            return;
        }
        Log.w(C0757w.f463a, "Can not set App package because bugly is disable.");
    }

    public static boolean setJavascriptMonitor(WebView webView, boolean z) {
        return setJavascriptMonitor(webView, z, false);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static boolean setJavascriptMonitor(WebView webView, boolean z, boolean z2) {
        if (webView == null) {
            Log.w(C0757w.f463a, "Webview is null.");
            return false;
        } else if (CrashModule.hasInitialized()) {
            C0757w.m456a("Set Javascript exception monitor of webview.", new Object[0]);
            if (C0693b.f45a) {
                C0757w.m460c("URL of webview is %s", webView.getUrl());
                if (webView.getUrl() == null) {
                    return false;
                }
                if (z2 || VERSION.SDK_INT >= 19) {
                    WebSettings settings = webView.getSettings();
                    if (!settings.getJavaScriptEnabled()) {
                        C0757w.m456a("Enable the javascript needed by webview monitor.", new Object[0]);
                        settings.setJavaScriptEnabled(true);
                    }
                    H5JavaScriptInterface instance = H5JavaScriptInterface.getInstance(webView);
                    if (instance != null) {
                        C0757w.m456a("Add a secure javascript interface to the webview.", new Object[0]);
                        webView.addJavascriptInterface(instance, "exceptionUploader");
                    }
                    if (z) {
                        C0757w.m456a("Inject bugly.js(v%s) to the webview.", C0727b.m254b());
                        String a = C0727b.m253a();
                        if (a == null) {
                            C0757w.m462e("Failed to inject Bugly.js.", C0727b.m254b());
                            return false;
                        }
                        webView.loadUrl("javascript:" + a);
                    }
                    return true;
                }
                C0757w.m462e("This interface is only available for Android 4.4 or later.", new Object[0]);
                return false;
            }
            Log.w(C0757w.f463a, "Can not set JavaScript monitor because bugly is disable.");
            return false;
        } else {
            C0757w.m462e("CrashReport has not been initialed! please to call method 'initCrashReport' first!", new Object[0]);
            return false;
        }
    }
}
