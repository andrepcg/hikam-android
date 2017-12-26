package com.tencent.bugly.crashreport.crash.anr;

import android.content.Context;
import android.os.FileObserver;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.C0706b;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import com.tencent.bugly.crashreport.crash.C0718b;
import com.tencent.bugly.crashreport.crash.C0721c;
import com.tencent.bugly.crashreport.crash.CrashDetailBean;
import com.tencent.bugly.crashreport.crash.anr.TraceFileHelper.C0712a;
import com.tencent.bugly.proguard.C0756v;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0760x;
import com.tencent.bugly.proguard.C0761y;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/* compiled from: BUGLY */
public final class C0717b {
    private AtomicInteger f261a = new AtomicInteger(0);
    private long f262b = -1;
    private final Context f263c;
    private final C0705a f264d;
    private final C0756v f265e;
    private final C0709a f266f;
    private final String f267g;
    private final C0718b f268h;
    private FileObserver f269i;
    private boolean f270j = true;

    /* compiled from: BUGLY */
    class C07151 extends FileObserver {
        private /* synthetic */ C0717b f259a;

        C07151(C0717b c0717b, String str, int i) {
            this.f259a = c0717b;
            super(str, 8);
        }

        public final void onEvent(int i, String str) {
            if (str != null) {
                String str2 = "/data/anr/" + str;
                if (str2.contains("trace")) {
                    this.f259a.m194a(str2);
                    return;
                }
                C0757w.m461d("not anr file %s", str2);
            }
        }
    }

    /* compiled from: BUGLY */
    class C07162 implements Runnable {
        private /* synthetic */ C0717b f260a;

        C07162(C0717b c0717b) {
            this.f260a = c0717b;
        }

        public final void run() {
            this.f260a.m197b();
        }
    }

    public C0717b(Context context, C0709a c0709a, C0705a c0705a, C0756v c0756v, C0718b c0718b) {
        this.f263c = C0761y.m481a(context);
        this.f267g = context.getDir("bugly", 0).getAbsolutePath();
        this.f264d = c0705a;
        this.f265e = c0756v;
        this.f266f = c0709a;
        this.f268h = c0718b;
    }

    private CrashDetailBean m185a(C0714a c0714a) {
        CrashDetailBean crashDetailBean = new CrashDetailBean();
        try {
            crashDetailBean.f195B = C0706b.m152g();
            crashDetailBean.f196C = C0706b.m148e();
            crashDetailBean.f197D = C0706b.m156i();
            crashDetailBean.f198E = this.f264d.m128p();
            crashDetailBean.f199F = this.f264d.m127o();
            crashDetailBean.f200G = this.f264d.m129q();
            crashDetailBean.f238w = C0761y.m488a(this.f263c, C0721c.f289d, null);
            crashDetailBean.f239x = C0760x.m475a(true);
            crashDetailBean.f217b = 3;
            crashDetailBean.f220e = this.f264d.m120h();
            crashDetailBean.f221f = this.f264d.f137j;
            crashDetailBean.f222g = this.f264d.m135w();
            crashDetailBean.f228m = this.f264d.m118g();
            crashDetailBean.f229n = "ANR_EXCEPTION";
            crashDetailBean.f230o = c0714a.f257f;
            crashDetailBean.f232q = c0714a.f258g;
            crashDetailBean.f207N = new HashMap();
            crashDetailBean.f207N.put("BUGLY_CR_01", c0714a.f256e);
            int i = -1;
            if (crashDetailBean.f232q != null) {
                i = crashDetailBean.f232q.indexOf("\n");
            }
            crashDetailBean.f231p = i > 0 ? crashDetailBean.f232q.substring(0, i) : "GET_FAIL";
            crashDetailBean.f233r = c0714a.f254c;
            if (crashDetailBean.f232q != null) {
                crashDetailBean.f236u = C0761y.m511b(crashDetailBean.f232q.getBytes());
            }
            crashDetailBean.f240y = c0714a.f253b;
            crashDetailBean.f241z = this.f264d.f131d;
            crashDetailBean.f194A = "main(1)";
            crashDetailBean.f201H = this.f264d.m137y();
            crashDetailBean.f223h = this.f264d.m134v();
            crashDetailBean.f224i = this.f264d.m95I();
            crashDetailBean.f237v = c0714a.f255d;
            crashDetailBean.f204K = this.f264d.f141n;
            crashDetailBean.f205L = this.f264d.f128a;
            crashDetailBean.f206M = this.f264d.m106a();
            crashDetailBean.f208O = this.f264d.m92F();
            crashDetailBean.f209P = this.f264d.m93G();
            crashDetailBean.f210Q = this.f264d.m138z();
            crashDetailBean.f211R = this.f264d.m91E();
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
        }
        return crashDetailBean;
    }

    private static boolean m186a(String str, String str2, String str3) {
        Throwable e;
        C0712a readTargetDumpInfo = TraceFileHelper.readTargetDumpInfo(str3, str, true);
        if (readTargetDumpInfo == null || readTargetDumpInfo.f251d == null || readTargetDumpInfo.f251d.size() <= 0) {
            C0757w.m462e("not found trace dump for %s", str3);
            return false;
        }
        File file = new File(str2);
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            if (file.exists() && file.canWrite()) {
                BufferedWriter bufferedWriter = null;
                BufferedWriter bufferedWriter2;
                try {
                    bufferedWriter2 = new BufferedWriter(new FileWriter(file, false));
                    try {
                        String[] strArr = (String[]) readTargetDumpInfo.f251d.get("main");
                        if (strArr != null && strArr.length >= 3) {
                            String str4 = strArr[0];
                            bufferedWriter2.write("\"main\" tid=" + strArr[2] + " :\n" + str4 + "\n" + strArr[1] + "\n\n");
                            bufferedWriter2.flush();
                        }
                        for (Entry entry : readTargetDumpInfo.f251d.entrySet()) {
                            if (!(((String) entry.getKey()).equals("main") || entry.getValue() == null || ((String[]) entry.getValue()).length < 3)) {
                                String str5 = ((String[]) entry.getValue())[0];
                                bufferedWriter2.write("\"" + ((String) entry.getKey()) + "\" tid=" + ((String[]) entry.getValue())[2] + " :\n" + str5 + "\n" + ((String[]) entry.getValue())[1] + "\n\n");
                                bufferedWriter2.flush();
                            }
                        }
                        try {
                            bufferedWriter2.close();
                        } catch (Throwable e2) {
                            if (!C0757w.m457a(e2)) {
                                e2.printStackTrace();
                            }
                        }
                        return true;
                    } catch (IOException e3) {
                        e2 = e3;
                        bufferedWriter = bufferedWriter2;
                        try {
                            if (!C0757w.m457a(e2)) {
                                e2.printStackTrace();
                            }
                            C0757w.m462e("dump trace fail %s", e2.getClass().getName() + ":" + e2.getMessage());
                            if (bufferedWriter != null) {
                                try {
                                    bufferedWriter.close();
                                } catch (Throwable e22) {
                                    if (!C0757w.m457a(e22)) {
                                        e22.printStackTrace();
                                    }
                                }
                            }
                            return false;
                        } catch (Throwable th) {
                            e22 = th;
                            bufferedWriter2 = bufferedWriter;
                            if (bufferedWriter2 != null) {
                                try {
                                    bufferedWriter2.close();
                                } catch (Throwable e4) {
                                    if (!C0757w.m457a(e4)) {
                                        e4.printStackTrace();
                                    }
                                }
                            }
                            throw e22;
                        }
                    } catch (Throwable th2) {
                        e22 = th2;
                        if (bufferedWriter2 != null) {
                            bufferedWriter2.close();
                        }
                        throw e22;
                    }
                } catch (IOException e5) {
                    e22 = e5;
                    if (C0757w.m457a(e22)) {
                        e22.printStackTrace();
                    }
                    C0757w.m462e("dump trace fail %s", e22.getClass().getName() + ":" + e22.getMessage());
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                    return false;
                } catch (Throwable th3) {
                    e22 = th3;
                    bufferedWriter2 = null;
                    if (bufferedWriter2 != null) {
                        bufferedWriter2.close();
                    }
                    throw e22;
                }
            }
            C0757w.m462e("backup file create fail %s", str2);
            return false;
        } catch (Throwable e222) {
            if (!C0757w.m457a(e222)) {
                e222.printStackTrace();
            }
            C0757w.m462e("backup file create error! %s  %s", e222.getClass().getName() + ":" + e222.getMessage(), str2);
            return false;
        }
    }

    public final boolean m196a() {
        return this.f261a.get() != 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void m194a(java.lang.String r13) {
        /*
        r12 = this;
        monitor-enter(r12);
        r0 = r12.f261a;	 Catch:{ all -> 0x0066 }
        r0 = r0.get();	 Catch:{ all -> 0x0066 }
        if (r0 == 0) goto L_0x0013;
    L_0x0009:
        r0 = "trace started return ";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x0066 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ all -> 0x0066 }
        monitor-exit(r12);	 Catch:{ all -> 0x0066 }
    L_0x0012:
        return;
    L_0x0013:
        r0 = r12.f261a;	 Catch:{ all -> 0x0066 }
        r1 = 1;
        r0.set(r1);	 Catch:{ all -> 0x0066 }
        monitor-exit(r12);	 Catch:{ all -> 0x0066 }
        r0 = "read trace first dump for create time!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = -1;
        r2 = 0;
        r2 = com.tencent.bugly.crashreport.crash.anr.TraceFileHelper.readFirstDumpInfo(r13, r2);	 Catch:{ Throwable -> 0x01cd }
        if (r2 == 0) goto L_0x002d;
    L_0x002b:
        r0 = r2.f250c;	 Catch:{ Throwable -> 0x01cd }
    L_0x002d:
        r2 = -1;
        r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r2 != 0) goto L_0x02f0;
    L_0x0033:
        r0 = "trace dump fail could not get time!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x01cd }
        r4 = r0;
    L_0x0040:
        r0 = r12.f262b;	 Catch:{ Throwable -> 0x01cd }
        r0 = r4 - r0;
        r0 = java.lang.Math.abs(r0);	 Catch:{ Throwable -> 0x01cd }
        r2 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x0069;
    L_0x004e:
        r0 = "should not process ANR too Fre in %d";
        r1 = 1;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        r2 = 0;
        r3 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Throwable -> 0x01cd }
        r1[r2] = r3;	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f261a;
        r1 = 0;
        r0.set(r1);
        goto L_0x0012;
    L_0x0066:
        r0 = move-exception;
        monitor-exit(r12);
        throw r0;
    L_0x0069:
        r12.f262b = r4;	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f261a;	 Catch:{ Throwable -> 0x01cd }
        r1 = 1;
        r0.set(r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = com.tencent.bugly.crashreport.crash.C0721c.f290e;	 Catch:{ Throwable -> 0x008f }
        r1 = 0;
        r6 = com.tencent.bugly.proguard.C0761y.m495a(r0, r1);	 Catch:{ Throwable -> 0x008f }
        if (r6 == 0) goto L_0x0080;
    L_0x007a:
        r0 = r6.size();	 Catch:{ Throwable -> 0x01cd }
        if (r0 > 0) goto L_0x00a3;
    L_0x0080:
        r0 = "can't get all thread skip this anr";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f261a;
        r1 = 0;
        r0.set(r1);
        goto L_0x0012;
    L_0x008f:
        r0 = move-exception;
        com.tencent.bugly.proguard.C0757w.m457a(r0);	 Catch:{ Throwable -> 0x01cd }
        r0 = "get all thread stack fail!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f261a;
        r1 = 0;
        r0.set(r1);
        goto L_0x0012;
    L_0x00a3:
        r7 = r12.f263c;	 Catch:{ Throwable -> 0x01cd }
        r0 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r2 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x0103;
    L_0x00ad:
        r0 = 0;
        r2 = r0;
    L_0x00b0:
        r0 = "to find!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = "activity";
        r0 = r7.getSystemService(r0);	 Catch:{ Throwable -> 0x01cd }
        r0 = (android.app.ActivityManager) r0;	 Catch:{ Throwable -> 0x01cd }
        r8 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r8 = r2 / r8;
        r1 = 0;
        r2 = r1;
    L_0x00c6:
        r1 = "waiting!";
        r3 = 0;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m460c(r1, r3);	 Catch:{ Throwable -> 0x01cd }
        r1 = r0.getProcessesInErrorState();	 Catch:{ Throwable -> 0x01cd }
        if (r1 == 0) goto L_0x0107;
    L_0x00d4:
        r3 = r1.iterator();	 Catch:{ Throwable -> 0x01cd }
    L_0x00d8:
        r1 = r3.hasNext();	 Catch:{ Throwable -> 0x01cd }
        if (r1 == 0) goto L_0x0107;
    L_0x00de:
        r1 = r3.next();	 Catch:{ Throwable -> 0x01cd }
        r1 = (android.app.ActivityManager.ProcessErrorStateInfo) r1;	 Catch:{ Throwable -> 0x01cd }
        r7 = r1.condition;	 Catch:{ Throwable -> 0x01cd }
        r10 = 2;
        if (r7 != r10) goto L_0x00d8;
    L_0x00e9:
        r0 = "found!";
        r2 = 0;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r2);	 Catch:{ Throwable -> 0x01cd }
    L_0x00f1:
        if (r1 != 0) goto L_0x011d;
    L_0x00f3:
        r0 = "proc state is unvisiable!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f261a;
        r1 = 0;
        r0.set(r1);
        goto L_0x0012;
    L_0x0103:
        r0 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r2 = r0;
        goto L_0x00b0;
    L_0x0107:
        r10 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        com.tencent.bugly.proguard.C0761y.m513b(r10);	 Catch:{ Throwable -> 0x01cd }
        r1 = r2 + 1;
        r2 = (long) r2;	 Catch:{ Throwable -> 0x01cd }
        r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r2 < 0) goto L_0x02ed;
    L_0x0113:
        r0 = "end!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r1 = 0;
        goto L_0x00f1;
    L_0x011d:
        r0 = r1.pid;	 Catch:{ Throwable -> 0x01cd }
        r2 = android.os.Process.myPid();	 Catch:{ Throwable -> 0x01cd }
        if (r0 == r2) goto L_0x013a;
    L_0x0125:
        r0 = "not mind proc!";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x01cd }
        r3 = 0;
        r1 = r1.processName;	 Catch:{ Throwable -> 0x01cd }
        r2[r3] = r1;	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r2);	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f261a;
        r1 = 0;
        r0.set(r1);
        goto L_0x0012;
    L_0x013a:
        r0 = "found visiable anr , start to process!";
        r2 = 0;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m456a(r0, r2);	 Catch:{ Throwable -> 0x01cd }
        r2 = r12.f263c;	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f266f;	 Catch:{ Throwable -> 0x01cd }
        r0.m177c();	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f266f;	 Catch:{ Throwable -> 0x01cd }
        r0 = r0.m176b();	 Catch:{ Throwable -> 0x01cd }
        if (r0 != 0) goto L_0x016d;
    L_0x0151:
        r0 = "waiting for remote sync";
        r3 = 0;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m462e(r0, r3);	 Catch:{ Throwable -> 0x01cd }
        r0 = 0;
    L_0x015a:
        r3 = r12.f266f;	 Catch:{ Throwable -> 0x01cd }
        r3 = r3.m176b();	 Catch:{ Throwable -> 0x01cd }
        if (r3 != 0) goto L_0x016d;
    L_0x0162:
        r8 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        com.tencent.bugly.proguard.C0761y.m513b(r8);	 Catch:{ Throwable -> 0x01cd }
        r0 = r0 + 500;
        r3 = 3000; // 0xbb8 float:4.204E-42 double:1.482E-320;
        if (r0 < r3) goto L_0x015a;
    L_0x016d:
        r0 = new java.io.File;	 Catch:{ Throwable -> 0x01cd }
        r2 = r2.getFilesDir();	 Catch:{ Throwable -> 0x01cd }
        r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x01cd }
        r7 = "bugly/bugly_trace_";
        r3.<init>(r7);	 Catch:{ Throwable -> 0x01cd }
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x01cd }
        r7 = ".txt";
        r3 = r3.append(r7);	 Catch:{ Throwable -> 0x01cd }
        r3 = r3.toString();	 Catch:{ Throwable -> 0x01cd }
        r0.<init>(r2, r3);	 Catch:{ Throwable -> 0x01cd }
        r7 = new com.tencent.bugly.crashreport.crash.anr.a;	 Catch:{ Throwable -> 0x01cd }
        r7.<init>();	 Catch:{ Throwable -> 0x01cd }
        r7.f254c = r4;	 Catch:{ Throwable -> 0x01cd }
        r0 = r0.getAbsolutePath();	 Catch:{ Throwable -> 0x01cd }
        r7.f255d = r0;	 Catch:{ Throwable -> 0x01cd }
        r0 = r1.processName;	 Catch:{ Throwable -> 0x01cd }
        r7.f252a = r0;	 Catch:{ Throwable -> 0x01cd }
        r0 = r1.shortMsg;	 Catch:{ Throwable -> 0x01cd }
        r7.f257f = r0;	 Catch:{ Throwable -> 0x01cd }
        r0 = r1.longMsg;	 Catch:{ Throwable -> 0x01cd }
        r7.f256e = r0;	 Catch:{ Throwable -> 0x01cd }
        r7.f253b = r6;	 Catch:{ Throwable -> 0x01cd }
        if (r6 == 0) goto L_0x01f2;
    L_0x01a8:
        r0 = r6.keySet();	 Catch:{ Throwable -> 0x01cd }
        r1 = r0.iterator();	 Catch:{ Throwable -> 0x01cd }
    L_0x01b0:
        r0 = r1.hasNext();	 Catch:{ Throwable -> 0x01cd }
        if (r0 == 0) goto L_0x01f2;
    L_0x01b6:
        r0 = r1.next();	 Catch:{ Throwable -> 0x01cd }
        r0 = (java.lang.String) r0;	 Catch:{ Throwable -> 0x01cd }
        r2 = "main(";
        r2 = r0.startsWith(r2);	 Catch:{ Throwable -> 0x01cd }
        if (r2 == 0) goto L_0x01b0;
    L_0x01c4:
        r0 = r6.get(r0);	 Catch:{ Throwable -> 0x01cd }
        r0 = (java.lang.String) r0;	 Catch:{ Throwable -> 0x01cd }
        r7.f258g = r0;	 Catch:{ Throwable -> 0x01cd }
        goto L_0x01b0;
    L_0x01cd:
        r0 = move-exception;
        r1 = com.tencent.bugly.proguard.C0757w.m457a(r0);	 Catch:{ all -> 0x0264 }
        if (r1 != 0) goto L_0x01d7;
    L_0x01d4:
        r0.printStackTrace();	 Catch:{ all -> 0x0264 }
    L_0x01d7:
        r1 = "handle anr error %s";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x0264 }
        r3 = 0;
        r0 = r0.getClass();	 Catch:{ all -> 0x0264 }
        r0 = r0.toString();	 Catch:{ all -> 0x0264 }
        r2[r3] = r0;	 Catch:{ all -> 0x0264 }
        com.tencent.bugly.proguard.C0757w.m462e(r1, r2);	 Catch:{ all -> 0x0264 }
        r0 = r12.f261a;
        r1 = 0;
        r0.set(r1);
        goto L_0x0012;
    L_0x01f2:
        r1 = "anr tm:%d\ntr:%s\nproc:%s\nsMsg:%s\n lMsg:%s\n threads:%d";
        r0 = 6;
        r2 = new java.lang.Object[r0];	 Catch:{ Throwable -> 0x01cd }
        r0 = 0;
        r4 = r7.f254c;	 Catch:{ Throwable -> 0x01cd }
        r3 = java.lang.Long.valueOf(r4);	 Catch:{ Throwable -> 0x01cd }
        r2[r0] = r3;	 Catch:{ Throwable -> 0x01cd }
        r0 = 1;
        r3 = r7.f255d;	 Catch:{ Throwable -> 0x01cd }
        r2[r0] = r3;	 Catch:{ Throwable -> 0x01cd }
        r0 = 2;
        r3 = r7.f252a;	 Catch:{ Throwable -> 0x01cd }
        r2[r0] = r3;	 Catch:{ Throwable -> 0x01cd }
        r0 = 3;
        r3 = r7.f257f;	 Catch:{ Throwable -> 0x01cd }
        r2[r0] = r3;	 Catch:{ Throwable -> 0x01cd }
        r0 = 4;
        r3 = r7.f256e;	 Catch:{ Throwable -> 0x01cd }
        r2[r0] = r3;	 Catch:{ Throwable -> 0x01cd }
        r3 = 5;
        r0 = r7.f253b;	 Catch:{ Throwable -> 0x01cd }
        if (r0 != 0) goto L_0x024a;
    L_0x0219:
        r0 = 0;
    L_0x021a:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Throwable -> 0x01cd }
        r2[r3] = r0;	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m460c(r1, r2);	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f266f;	 Catch:{ Throwable -> 0x01cd }
        r0 = r0.m176b();	 Catch:{ Throwable -> 0x01cd }
        if (r0 != 0) goto L_0x0251;
    L_0x022b:
        r0 = "crash report sync remote fail, will not upload to Bugly , print local for helpful!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = "ANR";
        r1 = com.tencent.bugly.proguard.C0761y.m486a();	 Catch:{ Throwable -> 0x01cd }
        r2 = r7.f252a;	 Catch:{ Throwable -> 0x01cd }
        r3 = 0;
        r4 = r7.f256e;	 Catch:{ Throwable -> 0x01cd }
        r5 = 0;
        com.tencent.bugly.crashreport.crash.C0718b.m203a(r0, r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x01cd }
    L_0x0242:
        r0 = r12.f261a;
        r1 = 0;
        r0.set(r1);
        goto L_0x0012;
    L_0x024a:
        r0 = r7.f253b;	 Catch:{ Throwable -> 0x01cd }
        r0 = r0.size();	 Catch:{ Throwable -> 0x01cd }
        goto L_0x021a;
    L_0x0251:
        r0 = r12.f266f;	 Catch:{ Throwable -> 0x01cd }
        r0 = r0.m177c();	 Catch:{ Throwable -> 0x01cd }
        r0 = r0.f165j;	 Catch:{ Throwable -> 0x01cd }
        if (r0 != 0) goto L_0x026c;
    L_0x025b:
        r0 = "ANR Report is closed!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        goto L_0x0242;
    L_0x0264:
        r0 = move-exception;
        r1 = r12.f261a;
        r2 = 0;
        r1.set(r2);
        throw r0;
    L_0x026c:
        r0 = "found visiable anr , start to upload!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m456a(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        r5 = r12.m185a(r7);	 Catch:{ Throwable -> 0x01cd }
        if (r5 != 0) goto L_0x0283;
    L_0x027a:
        r0 = "pack anr fail!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        goto L_0x0242;
    L_0x0283:
        r0 = com.tencent.bugly.crashreport.crash.C0721c.m218a();	 Catch:{ Throwable -> 0x01cd }
        r0.m224a(r5);	 Catch:{ Throwable -> 0x01cd }
        r0 = r5.f216a;	 Catch:{ Throwable -> 0x01cd }
        r2 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 < 0) goto L_0x02e4;
    L_0x0292:
        r0 = "backup anr record success!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m456a(r0, r1);	 Catch:{ Throwable -> 0x01cd }
    L_0x029a:
        if (r13 == 0) goto L_0x02bf;
    L_0x029c:
        r0 = new java.io.File;	 Catch:{ Throwable -> 0x01cd }
        r0.<init>(r13);	 Catch:{ Throwable -> 0x01cd }
        r0 = r0.exists();	 Catch:{ Throwable -> 0x01cd }
        if (r0 == 0) goto L_0x02bf;
    L_0x02a7:
        r0 = r12.f261a;	 Catch:{ Throwable -> 0x01cd }
        r1 = 3;
        r0.set(r1);	 Catch:{ Throwable -> 0x01cd }
        r0 = r7.f255d;	 Catch:{ Throwable -> 0x01cd }
        r1 = r7.f252a;	 Catch:{ Throwable -> 0x01cd }
        r0 = com.tencent.bugly.crashreport.crash.anr.C0717b.m186a(r13, r0, r1);	 Catch:{ Throwable -> 0x01cd }
        if (r0 == 0) goto L_0x02bf;
    L_0x02b7:
        r0 = "backup trace success";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m456a(r0, r1);	 Catch:{ Throwable -> 0x01cd }
    L_0x02bf:
        r0 = "ANR";
        r1 = com.tencent.bugly.proguard.C0761y.m486a();	 Catch:{ Throwable -> 0x01cd }
        r2 = r7.f252a;	 Catch:{ Throwable -> 0x01cd }
        r3 = 0;
        r4 = r7.f256e;	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.crashreport.crash.C0718b.m203a(r0, r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x01cd }
        r0 = r12.f268h;	 Catch:{ Throwable -> 0x01cd }
        r0 = r0.m214a(r5);	 Catch:{ Throwable -> 0x01cd }
        if (r0 != 0) goto L_0x02dd;
    L_0x02d5:
        r0 = r12.f268h;	 Catch:{ Throwable -> 0x01cd }
        r2 = 3000; // 0xbb8 float:4.204E-42 double:1.482E-320;
        r1 = 1;
        r0.m212a(r5, r2, r1);	 Catch:{ Throwable -> 0x01cd }
    L_0x02dd:
        r0 = r12.f268h;	 Catch:{ Throwable -> 0x01cd }
        r0.m216b(r5);	 Catch:{ Throwable -> 0x01cd }
        goto L_0x0242;
    L_0x02e4:
        r0 = "backup anr record fail!";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x01cd }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);	 Catch:{ Throwable -> 0x01cd }
        goto L_0x029a;
    L_0x02ed:
        r2 = r1;
        goto L_0x00c6;
    L_0x02f0:
        r4 = r0;
        goto L_0x0040;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.crashreport.crash.anr.b.a(java.lang.String):void");
    }

    private synchronized void m188c() {
        if (m191e()) {
            C0757w.m461d("start when started!", new Object[0]);
        } else {
            this.f269i = new C07151(this, "/data/anr/", 8);
            try {
                this.f269i.startWatching();
                C0757w.m456a("start anr monitor!", new Object[0]);
                this.f265e.m450a(new C07162(this));
            } catch (Throwable th) {
                this.f269i = null;
                C0757w.m461d("start anr monitor failed!", new Object[0]);
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
    }

    private synchronized void m190d() {
        if (m191e()) {
            try {
                this.f269i.stopWatching();
                this.f269i = null;
                C0757w.m461d("close anr monitor!", new Object[0]);
            } catch (Throwable th) {
                C0757w.m461d("stop anr monitor failed!", new Object[0]);
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        } else {
            C0757w.m461d("close when closed!", new Object[0]);
        }
    }

    private synchronized boolean m191e() {
        return this.f269i != null;
    }

    private synchronized void m187b(boolean z) {
        if (z) {
            m188c();
        } else {
            m190d();
        }
    }

    private synchronized boolean m192f() {
        return this.f270j;
    }

    private synchronized void m189c(boolean z) {
        if (this.f270j != z) {
            C0757w.m456a("user change anr %b", Boolean.valueOf(z));
            this.f270j = z;
        }
    }

    public final void m195a(boolean z) {
        m189c(z);
        boolean f = m192f();
        C0709a a = C0709a.m169a();
        if (a != null) {
            f = f && a.m177c().f162g;
        }
        if (f != m191e()) {
            C0757w.m456a("anr changed to %b", Boolean.valueOf(f));
            m187b(f);
        }
    }

    protected final void m197b() {
        long b = C0761y.m509b() - C0721c.f291f;
        File file = new File(this.f267g);
        if (file.exists() && file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length != 0) {
                String str = "bugly_trace_";
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
                            C0757w.m462e("tomb format error delete %s", name);
                        }
                        if (file2.delete()) {
                            i++;
                        }
                    }
                }
                C0757w.m460c("clean tombs %d", Integer.valueOf(i));
            }
        }
    }

    public final synchronized void m193a(StrategyBean strategyBean) {
        boolean z = true;
        synchronized (this) {
            if (strategyBean != null) {
                if (strategyBean.f165j != m191e()) {
                    C0757w.m461d("server anr changed to %b", Boolean.valueOf(strategyBean.f165j));
                }
                if (!(strategyBean.f165j && m192f())) {
                    z = false;
                }
                if (z != m191e()) {
                    C0757w.m456a("anr changed to %b", Boolean.valueOf(z));
                    m187b(z);
                }
            }
        }
    }
}
