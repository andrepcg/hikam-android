package com.tencent.bugly;

import com.tencent.bugly.crashreport.common.info.C0705a;
import java.util.Map;

/* compiled from: BUGLY */
public class BuglyStrategy {
    private String f30a;
    private String f31b;
    private String f32c;
    private long f33d;
    private String f34e;
    private String f35f;
    private boolean f36g = true;
    private boolean f37h = true;
    private boolean f38i = true;
    private Class<?> f39j = null;
    private boolean f40k = true;
    private boolean f41l = true;
    private boolean f42m = true;
    private boolean f43n = false;
    private C0691a f44o;

    /* compiled from: BUGLY */
    public static class C0691a {
        public static final int CRASHTYPE_ANR = 4;
        public static final int CRASHTYPE_BLOCK = 7;
        public static final int CRASHTYPE_COCOS2DX_JS = 5;
        public static final int CRASHTYPE_COCOS2DX_LUA = 6;
        public static final int CRASHTYPE_JAVA_CATCH = 1;
        public static final int CRASHTYPE_JAVA_CRASH = 0;
        public static final int CRASHTYPE_NATIVE = 2;
        public static final int CRASHTYPE_U3D = 3;
        public static final int MAX_USERDATA_KEY_LENGTH = 100;
        public static final int MAX_USERDATA_VALUE_LENGTH = 30000;

        public synchronized Map<String, String> onCrashHandleStart(int i, String str, String str2, String str3) {
            return null;
        }

        public synchronized byte[] onCrashHandleStart2GetExtraDatas(int i, String str, String str2, String str3) {
            return null;
        }
    }

    public synchronized BuglyStrategy setBuglyLogUpload(boolean z) {
        this.f40k = z;
        return this;
    }

    public synchronized BuglyStrategy setRecordUserInfoOnceADay(boolean z) {
        this.f43n = z;
        return this;
    }

    public synchronized BuglyStrategy setUploadProcess(boolean z) {
        this.f42m = z;
        return this;
    }

    public synchronized boolean isUploadProcess() {
        return this.f42m;
    }

    public synchronized boolean isBuglyLogUpload() {
        return this.f40k;
    }

    public synchronized boolean recordUserInfoOnceADay() {
        return this.f43n;
    }

    public boolean isReplaceOldChannel() {
        return this.f41l;
    }

    public void setReplaceOldChannel(boolean z) {
        this.f41l = z;
    }

    public synchronized String getAppVersion() {
        return this.f30a == null ? C0705a.m85b().f137j : this.f30a;
    }

    public synchronized BuglyStrategy setAppVersion(String str) {
        this.f30a = str;
        return this;
    }

    public synchronized BuglyStrategy setUserInfoActivity(Class<?> cls) {
        this.f39j = cls;
        return this;
    }

    public synchronized Class<?> getUserInfoActivity() {
        return this.f39j;
    }

    public synchronized String getAppChannel() {
        return this.f31b == null ? C0705a.m85b().f139l : this.f31b;
    }

    public synchronized BuglyStrategy setAppChannel(String str) {
        this.f31b = str;
        return this;
    }

    public synchronized String getAppPackageName() {
        return this.f32c == null ? C0705a.m85b().f130c : this.f32c;
    }

    public synchronized BuglyStrategy setAppPackageName(String str) {
        this.f32c = str;
        return this;
    }

    public synchronized long getAppReportDelay() {
        return this.f33d;
    }

    public synchronized BuglyStrategy setAppReportDelay(long j) {
        this.f33d = j;
        return this;
    }

    public synchronized String getLibBuglySOFilePath() {
        return this.f34e;
    }

    public synchronized BuglyStrategy setLibBuglySOFilePath(String str) {
        this.f34e = str;
        return this;
    }

    public synchronized String getDeviceID() {
        return this.f35f;
    }

    public synchronized BuglyStrategy setDeviceID(String str) {
        this.f35f = str;
        return this;
    }

    public synchronized boolean isEnableNativeCrashMonitor() {
        return this.f36g;
    }

    public synchronized BuglyStrategy setEnableNativeCrashMonitor(boolean z) {
        this.f36g = z;
        return this;
    }

    public synchronized BuglyStrategy setEnableUserInfo(boolean z) {
        this.f38i = z;
        return this;
    }

    public synchronized boolean isEnableUserInfo() {
        return this.f38i;
    }

    public synchronized boolean isEnableANRCrashMonitor() {
        return this.f37h;
    }

    public synchronized BuglyStrategy setEnableANRCrashMonitor(boolean z) {
        this.f37h = z;
        return this;
    }

    public synchronized C0691a getCrashHandleCallback() {
        return this.f44o;
    }

    public synchronized BuglyStrategy setCrashHandleCallback(C0691a c0691a) {
        this.f44o = c0691a;
        return this;
    }
}
