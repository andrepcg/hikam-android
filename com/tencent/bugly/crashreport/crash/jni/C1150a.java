package com.tencent.bugly.crashreport.crash.jni;

import android.content.Context;
import com.tencent.bugly.crashreport.common.info.AppInfo;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.C0706b;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.crash.C0718b;
import com.tencent.bugly.crashreport.crash.C0721c;
import com.tencent.bugly.crashreport.crash.CrashDetailBean;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0760x;
import com.tencent.bugly.proguard.C0761y;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/* compiled from: BUGLY */
public final class C1150a implements NativeExceptionHandler {
    private final Context f556a;
    private final C0718b f557b;
    private final C0705a f558c;
    private final C0709a f559d;
    private final String f560e;

    public C1150a(Context context, C0705a c0705a, C0718b c0718b, C0709a c0709a, String str) {
        this.f556a = context;
        this.f557b = c0718b;
        this.f558c = c0705a;
        this.f559d = c0709a;
        this.f560e = str;
    }

    public final CrashDetailBean packageCrashDatas(String str, String str2, long j, String str3, String str4, String str5, String str6, String str7, String str8, String str9, byte[] bArr, Map<String, String> map, boolean z) {
        boolean l = C0721c.m218a().m236l();
        if (l) {
            C0757w.m462e("This Crash Caused By ANR , PLS To Fix ANR , This Trace May Be Not Useful!", new Object[0]);
        }
        CrashDetailBean crashDetailBean = new CrashDetailBean();
        crashDetailBean.f217b = 1;
        crashDetailBean.f220e = this.f558c.m120h();
        crashDetailBean.f221f = this.f558c.f137j;
        crashDetailBean.f222g = this.f558c.m135w();
        crashDetailBean.f228m = this.f558c.m118g();
        crashDetailBean.f229n = str3;
        crashDetailBean.f230o = l ? " This Crash Caused By ANR , PLS To Fix ANR , This Trace May Be Not Useful![Bugly]" : "";
        crashDetailBean.f231p = str4;
        if (str5 == null) {
            str5 = "";
        }
        crashDetailBean.f232q = str5;
        crashDetailBean.f233r = j;
        crashDetailBean.f236u = C0761y.m511b(crashDetailBean.f232q.getBytes());
        crashDetailBean.f241z = str;
        crashDetailBean.f194A = str2;
        crashDetailBean.f201H = this.f558c.m137y();
        crashDetailBean.f223h = this.f558c.m134v();
        crashDetailBean.f224i = this.f558c.m95I();
        crashDetailBean.f237v = str8;
        String a = C0729b.m259a(this.f560e, str8);
        if (!C0761y.m501a(a)) {
            crashDetailBean.f213T = a;
        }
        File file = new File(this.f560e, "backup_record.txt");
        crashDetailBean.f214U = file.exists() ? file.getAbsolutePath() : null;
        crashDetailBean.f202I = str7;
        crashDetailBean.f203J = str6;
        crashDetailBean.f204K = str9;
        crashDetailBean.f198E = this.f558c.m128p();
        crashDetailBean.f199F = this.f558c.m127o();
        crashDetailBean.f200G = this.f558c.m129q();
        if (z) {
            crashDetailBean.f195B = C0706b.m152g();
            crashDetailBean.f196C = C0706b.m148e();
            crashDetailBean.f197D = C0706b.m156i();
            crashDetailBean.f238w = C0761y.m488a(this.f556a, C0721c.f289d, null);
            crashDetailBean.f239x = C0760x.m475a(true);
            crashDetailBean.f205L = this.f558c.f128a;
            crashDetailBean.f206M = this.f558c.m106a();
            crashDetailBean.f208O = this.f558c.m92F();
            crashDetailBean.f209P = this.f558c.m93G();
            crashDetailBean.f210Q = this.f558c.m138z();
            crashDetailBean.f211R = this.f558c.m91E();
            crashDetailBean.f240y = C0761y.m495a(C0721c.f290e, false);
            a = "java:\n";
            int indexOf = crashDetailBean.f232q.indexOf(a);
            if (indexOf > 0) {
                indexOf += a.length();
                String substring = crashDetailBean.f232q.substring(indexOf, crashDetailBean.f232q.length() - 1);
                if (substring.length() > 0 && crashDetailBean.f240y.containsKey(crashDetailBean.f194A)) {
                    a = (String) crashDetailBean.f240y.get(crashDetailBean.f194A);
                    int indexOf2 = a.indexOf(substring);
                    if (indexOf2 > 0) {
                        a = a.substring(indexOf2);
                        crashDetailBean.f240y.put(crashDetailBean.f194A, a);
                        crashDetailBean.f232q = crashDetailBean.f232q.substring(0, indexOf);
                        crashDetailBean.f232q += a;
                    }
                }
            }
            if (str == null) {
                crashDetailBean.f241z = this.f558c.f131d;
            }
            this.f557b.m216b(crashDetailBean);
        } else {
            crashDetailBean.f195B = -1;
            crashDetailBean.f196C = -1;
            crashDetailBean.f197D = -1;
            crashDetailBean.f238w = "this crash is occurred at last process! Log is miss, when get an terrible ABRT Native Exception etc.";
            crashDetailBean.f205L = -1;
            crashDetailBean.f208O = -1;
            crashDetailBean.f209P = -1;
            crashDetailBean.f210Q = map;
            crashDetailBean.f211R = this.f558c.m91E();
            crashDetailBean.f240y = null;
            if (str == null) {
                crashDetailBean.f241z = "unknown(record)";
            }
            if (bArr == null) {
                crashDetailBean.f239x = "this crash is occurred at last process! Log is miss, when get an terrible ABRT Native Exception etc.".getBytes();
            } else {
                crashDetailBean.f239x = bArr;
            }
        }
        return crashDetailBean;
    }

    public final void handleNativeException(int i, int i2, long j, long j2, String str, String str2, String str3, String str4, int i3, String str5, int i4, int i5, int i6, String str6, String str7) {
        C0757w.m456a("Native Crash Happen v1", new Object[0]);
        handleNativeException2(i, i2, j, j2, str, str2, str3, str4, i3, str5, i4, i5, i6, str6, str7, null);
    }

    public final void handleNativeException2(int i, int i2, long j, long j2, String str, String str2, String str3, String str4, int i3, String str5, int i4, int i5, int i6, String str6, String str7, String[] strArr) {
        C0757w.m456a("Native Crash Happen v2", new Object[0]);
        try {
            int i7;
            String str8;
            String str9;
            if (!this.f559d.m176b()) {
                C0757w.m462e("waiting for remote sync", new Object[0]);
                i7 = 0;
                while (!this.f559d.m176b()) {
                    C0761y.m513b(500);
                    i7 += 500;
                    if (i7 >= 3000) {
                        break;
                    }
                }
            }
            String a = C0729b.m258a(str3);
            String str10 = "UNKNOWN";
            if (i3 > 0) {
                str8 = "KERNEL";
                str9 = str + "(" + str5 + ")";
            } else {
                if (i4 > 0) {
                    Context context = this.f556a;
                    str10 = AppInfo.m73a(i4);
                }
                if (str10.equals(String.valueOf(i4))) {
                    str8 = str5;
                    str9 = str;
                } else {
                    str10 = str10 + "(" + i4 + ")";
                    str8 = str5;
                    str9 = str;
                }
            }
            if (!this.f559d.m176b()) {
                C0757w.m461d("no remote but still store!", new Object[0]);
            }
            if (this.f559d.m177c().f162g || !this.f559d.m176b()) {
                String str11 = null;
                String str12 = null;
                if (strArr != null) {
                    Map hashMap = new HashMap();
                    for (String str122 : strArr) {
                        String[] split = str122.split("=");
                        if (split.length == 2) {
                            hashMap.put(split[0], split[1]);
                        } else {
                            C0757w.m461d("bad extraMsg %s", str122);
                        }
                    }
                    str122 = (String) hashMap.get("ExceptionThreadName");
                    str11 = (String) hashMap.get("ExceptionProcessName");
                } else {
                    C0757w.m460c("not found extraMsg", new Object[0]);
                }
                if (str11 == null || str11.length() == 0) {
                    str11 = this.f558c.f131d;
                } else {
                    C0757w.m460c("crash process name change to %s", str11);
                }
                Thread currentThread;
                if (str122 != null && str122.length() != 0) {
                    C0757w.m460c("crash thread name change to %s", str122);
                    for (Thread currentThread2 : Thread.getAllStackTraces().keySet()) {
                        if (currentThread2.getName().equals(str122)) {
                            str122 = str122 + "(" + currentThread2.getId() + ")";
                            break;
                        }
                    }
                }
                currentThread2 = Thread.currentThread();
                str122 = currentThread2.getName() + "(" + currentThread2.getId() + ")";
                CrashDetailBean packageCrashDatas = packageCrashDatas(str11, str122, (j2 / 1000) + (1000 * j), str9, str2, a, str8, str10, str4, str7, null, null, true);
                if (packageCrashDatas == null) {
                    C0757w.m462e("pkg crash datas fail!", new Object[0]);
                    return;
                }
                C0718b.m203a("NATIVE_CRASH", C0761y.m486a(), this.f558c.f131d, Thread.currentThread(), str9 + "\n" + str2 + "\n" + a, packageCrashDatas);
                if (!this.f557b.m215a(packageCrashDatas, i3)) {
                    this.f557b.m212a(packageCrashDatas, 3000, true);
                }
                C0729b.m260b(this.f560e);
                return;
            }
            C0757w.m462e("crash report was closed by remote , will not upload to Bugly , print local for helpful!", new Object[0]);
            C0718b.m203a("NATIVE_CRASH", C0761y.m486a(), this.f558c.f131d, Thread.currentThread(), str9 + "\n" + str2 + "\n" + a, null);
            C0761y.m515b(str4);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
        }
    }
}
