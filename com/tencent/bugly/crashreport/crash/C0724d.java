package com.tencent.bugly.crashreport.crash;

import android.content.Context;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.proguard.C0756v;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.util.Map;

/* compiled from: BUGLY */
public final class C0724d {
    private static C0724d f315a = null;
    private C0709a f316b;
    private C0705a f317c;
    private C0718b f318d;
    private Context f319e;

    /* compiled from: BUGLY */
    class C07221 implements Runnable {
        private /* synthetic */ C0724d f308a;

        C07221(C0724d c0724d) {
            this.f308a = c0724d;
        }

        public final void run() {
            C0724d.m239a(this.f308a);
        }
    }

    static /* synthetic */ void m239a(C0724d c0724d) {
        C0757w.m460c("[ExtraCrashManager] Trying to notify Bugly agents.", new Object[0]);
        try {
            Class cls = Class.forName("com.tencent.bugly.agent.GameAgent");
            Object obj = "com.tencent.bugly";
            c0724d.f317c.getClass();
            String str = "";
            if (!"".equals(str)) {
                obj = obj + "." + str;
            }
            C0761y.m498a(cls, "sdkPackageName", obj, null);
            C0757w.m460c("[ExtraCrashManager] Bugly game agent has been notified.", new Object[0]);
        } catch (Throwable th) {
            C0757w.m456a("[ExtraCrashManager] no game agent", new Object[0]);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void m240a(com.tencent.bugly.crashreport.crash.C0724d r8, java.lang.Thread r9, int r10, java.lang.String r11, java.lang.String r12, java.lang.String r13, java.util.Map r14) {
        /*
        r2 = 1;
        r6 = 0;
        switch(r10) {
            case 4: goto L_0x00a3;
            case 5: goto L_0x0013;
            case 6: goto L_0x0013;
            case 7: goto L_0x0005;
            case 8: goto L_0x00a7;
            default: goto L_0x0005;
        };
    L_0x0005:
        r0 = "[ExtraCrashManager] Unknown extra crash type: %d";
        r1 = new java.lang.Object[r2];
        r2 = java.lang.Integer.valueOf(r10);
        r1[r6] = r2;
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);
    L_0x0012:
        return;
    L_0x0013:
        r0 = "Cocos";
    L_0x0015:
        r1 = "[ExtraCrashManager] %s Crash Happen";
        r2 = new java.lang.Object[r2];
        r2[r6] = r0;
        com.tencent.bugly.proguard.C0757w.m462e(r1, r2);
        r1 = r8.f316b;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m176b();	 Catch:{ Throwable -> 0x028d }
        if (r1 != 0) goto L_0x0042;
    L_0x0026:
        r1 = "waiting for remote sync";
        r2 = 0;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x028d }
        com.tencent.bugly.proguard.C0757w.m462e(r1, r2);	 Catch:{ Throwable -> 0x028d }
        r1 = r6;
    L_0x002f:
        r2 = r8.f316b;	 Catch:{ Throwable -> 0x028d }
        r2 = r2.m176b();	 Catch:{ Throwable -> 0x028d }
        if (r2 != 0) goto L_0x0042;
    L_0x0037:
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        com.tencent.bugly.proguard.C0761y.m513b(r2);	 Catch:{ Throwable -> 0x028d }
        r1 = r1 + 500;
        r2 = 3000; // 0xbb8 float:4.204E-42 double:1.482E-320;
        if (r1 < r2) goto L_0x002f;
    L_0x0042:
        r1 = r8.f316b;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m176b();	 Catch:{ Throwable -> 0x028d }
        if (r1 != 0) goto L_0x0052;
    L_0x004a:
        r1 = "[ExtraCrashManager] There is no remote strategy, but still store it.";
        r2 = 0;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x028d }
        com.tencent.bugly.proguard.C0757w.m461d(r1, r2);	 Catch:{ Throwable -> 0x028d }
    L_0x0052:
        r1 = r8.f316b;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m177c();	 Catch:{ Throwable -> 0x028d }
        r2 = r1.f162g;	 Catch:{ Throwable -> 0x028d }
        if (r2 != 0) goto L_0x00ab;
    L_0x005c:
        r2 = r8.f316b;	 Catch:{ Throwable -> 0x028d }
        r2 = r2.m176b();	 Catch:{ Throwable -> 0x028d }
        if (r2 == 0) goto L_0x00ab;
    L_0x0064:
        r1 = "[ExtraCrashManager] Crash report was closed by remote , will not upload to Bugly , print local for helpful!";
        r2 = 0;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x028d }
        com.tencent.bugly.proguard.C0757w.m462e(r1, r2);	 Catch:{ Throwable -> 0x028d }
        r1 = com.tencent.bugly.proguard.C0761y.m486a();	 Catch:{ Throwable -> 0x028d }
        r2 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r2 = r2.f131d;	 Catch:{ Throwable -> 0x028d }
        r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x028d }
        r3.<init>();	 Catch:{ Throwable -> 0x028d }
        r3 = r3.append(r11);	 Catch:{ Throwable -> 0x028d }
        r4 = "\n";
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x028d }
        r3 = r3.append(r12);	 Catch:{ Throwable -> 0x028d }
        r4 = "\n";
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x028d }
        r3 = r3.append(r13);	 Catch:{ Throwable -> 0x028d }
        r4 = r3.toString();	 Catch:{ Throwable -> 0x028d }
        r5 = 0;
        r3 = r9;
        com.tencent.bugly.crashreport.crash.C0718b.m203a(r0, r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x028d }
        r0 = "[ExtraCrashManager] Successfully handled.";
        r1 = new java.lang.Object[r6];
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);
        goto L_0x0012;
    L_0x00a3:
        r0 = "Unity";
        goto L_0x0015;
    L_0x00a7:
        r0 = "H5";
        goto L_0x0015;
    L_0x00ab:
        switch(r10) {
            case 5: goto L_0x00c6;
            case 6: goto L_0x00c6;
            case 7: goto L_0x00ae;
            case 8: goto L_0x00de;
            default: goto L_0x00ae;
        };
    L_0x00ae:
        r0 = "[ExtraCrashManager] Unknown extra crash type: %d";
        r1 = 1;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x028d }
        r2 = 0;
        r3 = java.lang.Integer.valueOf(r10);	 Catch:{ Throwable -> 0x028d }
        r1[r2] = r3;	 Catch:{ Throwable -> 0x028d }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);	 Catch:{ Throwable -> 0x028d }
        r0 = "[ExtraCrashManager] Successfully handled.";
        r1 = new java.lang.Object[r6];
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);
        goto L_0x0012;
    L_0x00c6:
        r1 = r1.f167l;	 Catch:{ Throwable -> 0x028d }
        if (r1 != 0) goto L_0x00f6;
    L_0x00ca:
        r1 = "[ExtraCrashManager] %s report is disabled.";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x028d }
        r3 = 0;
        r2[r3] = r0;	 Catch:{ Throwable -> 0x028d }
        com.tencent.bugly.proguard.C0757w.m462e(r1, r2);	 Catch:{ Throwable -> 0x028d }
        r0 = "[ExtraCrashManager] Successfully handled.";
        r1 = new java.lang.Object[r6];
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);
        goto L_0x0012;
    L_0x00de:
        r1 = r1.f168m;	 Catch:{ Throwable -> 0x028d }
        if (r1 != 0) goto L_0x00f6;
    L_0x00e2:
        r1 = "[ExtraCrashManager] %s report is disabled.";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x028d }
        r3 = 0;
        r2[r3] = r0;	 Catch:{ Throwable -> 0x028d }
        com.tencent.bugly.proguard.C0757w.m462e(r1, r2);	 Catch:{ Throwable -> 0x028d }
        r0 = "[ExtraCrashManager] Successfully handled.";
        r1 = new java.lang.Object[r6];
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);
        goto L_0x0012;
    L_0x00f6:
        r1 = 8;
        if (r10 != r1) goto L_0x00fb;
    L_0x00fa:
        r10 = 5;
    L_0x00fb:
        r5 = new com.tencent.bugly.crashreport.crash.CrashDetailBean;	 Catch:{ Throwable -> 0x028d }
        r5.<init>();	 Catch:{ Throwable -> 0x028d }
        r2 = com.tencent.bugly.crashreport.common.info.C0706b.m152g();	 Catch:{ Throwable -> 0x028d }
        r5.f195B = r2;	 Catch:{ Throwable -> 0x028d }
        r2 = com.tencent.bugly.crashreport.common.info.C0706b.m148e();	 Catch:{ Throwable -> 0x028d }
        r5.f196C = r2;	 Catch:{ Throwable -> 0x028d }
        r2 = com.tencent.bugly.crashreport.common.info.C0706b.m156i();	 Catch:{ Throwable -> 0x028d }
        r5.f197D = r2;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r2 = r1.m128p();	 Catch:{ Throwable -> 0x028d }
        r5.f198E = r2;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r2 = r1.m127o();	 Catch:{ Throwable -> 0x028d }
        r5.f199F = r2;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r2 = r1.m129q();	 Catch:{ Throwable -> 0x028d }
        r5.f200G = r2;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f319e;	 Catch:{ Throwable -> 0x028d }
        r2 = com.tencent.bugly.crashreport.crash.C0721c.f289d;	 Catch:{ Throwable -> 0x028d }
        r3 = 0;
        r1 = com.tencent.bugly.proguard.C0761y.m488a(r1, r2, r3);	 Catch:{ Throwable -> 0x028d }
        r5.f238w = r1;	 Catch:{ Throwable -> 0x028d }
        r5.f217b = r10;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m120h();	 Catch:{ Throwable -> 0x028d }
        r5.f220e = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.f137j;	 Catch:{ Throwable -> 0x028d }
        r5.f221f = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m135w();	 Catch:{ Throwable -> 0x028d }
        r5.f222g = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m118g();	 Catch:{ Throwable -> 0x028d }
        r5.f228m = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x028d }
        r1.<init>();	 Catch:{ Throwable -> 0x028d }
        r1 = r1.append(r11);	 Catch:{ Throwable -> 0x028d }
        r1 = r1.toString();	 Catch:{ Throwable -> 0x028d }
        r5.f229n = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x028d }
        r1.<init>();	 Catch:{ Throwable -> 0x028d }
        r1 = r1.append(r12);	 Catch:{ Throwable -> 0x028d }
        r1 = r1.toString();	 Catch:{ Throwable -> 0x028d }
        r5.f230o = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = "";
        if (r13 == 0) goto L_0x0240;
    L_0x0177:
        r2 = "\n";
        r2 = r13.split(r2);	 Catch:{ Throwable -> 0x028d }
        r3 = r2.length;	 Catch:{ Throwable -> 0x028d }
        if (r3 <= 0) goto L_0x0183;
    L_0x0180:
        r1 = 0;
        r1 = r2[r1];	 Catch:{ Throwable -> 0x028d }
    L_0x0183:
        r2 = r1;
        r1 = r13;
    L_0x0185:
        r5.f231p = r2;	 Catch:{ Throwable -> 0x028d }
        r5.f232q = r1;	 Catch:{ Throwable -> 0x028d }
        r2 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x028d }
        r5.f233r = r2;	 Catch:{ Throwable -> 0x028d }
        r1 = r5.f232q;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.getBytes();	 Catch:{ Throwable -> 0x028d }
        r1 = com.tencent.bugly.proguard.C0761y.m511b(r1);	 Catch:{ Throwable -> 0x028d }
        r5.f236u = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = com.tencent.bugly.crashreport.crash.C0721c.f290e;	 Catch:{ Throwable -> 0x028d }
        r2 = 0;
        r1 = com.tencent.bugly.proguard.C0761y.m495a(r1, r2);	 Catch:{ Throwable -> 0x028d }
        r5.f240y = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.f131d;	 Catch:{ Throwable -> 0x028d }
        r5.f241z = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x028d }
        r1.<init>();	 Catch:{ Throwable -> 0x028d }
        r2 = r9.getName();	 Catch:{ Throwable -> 0x028d }
        r1 = r1.append(r2);	 Catch:{ Throwable -> 0x028d }
        r2 = "(";
        r1 = r1.append(r2);	 Catch:{ Throwable -> 0x028d }
        r2 = r9.getId();	 Catch:{ Throwable -> 0x028d }
        r1 = r1.append(r2);	 Catch:{ Throwable -> 0x028d }
        r2 = ")";
        r1 = r1.append(r2);	 Catch:{ Throwable -> 0x028d }
        r1 = r1.toString();	 Catch:{ Throwable -> 0x028d }
        r5.f194A = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m137y();	 Catch:{ Throwable -> 0x028d }
        r5.f201H = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m134v();	 Catch:{ Throwable -> 0x028d }
        r5.f223h = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r2 = r1.f128a;	 Catch:{ Throwable -> 0x028d }
        r5.f205L = r2;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m106a();	 Catch:{ Throwable -> 0x028d }
        r5.f206M = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m92F();	 Catch:{ Throwable -> 0x028d }
        r5.f208O = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m93G();	 Catch:{ Throwable -> 0x028d }
        r5.f209P = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m138z();	 Catch:{ Throwable -> 0x028d }
        r5.f210Q = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r1 = r1.m91E();	 Catch:{ Throwable -> 0x028d }
        r5.f211R = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r8.f318d;	 Catch:{ Throwable -> 0x028d }
        r1.m216b(r5);	 Catch:{ Throwable -> 0x028d }
        r1 = 0;
        r1 = com.tencent.bugly.proguard.C0760x.m475a(r1);	 Catch:{ Throwable -> 0x028d }
        r5.f239x = r1;	 Catch:{ Throwable -> 0x028d }
        r1 = r5.f207N;	 Catch:{ Throwable -> 0x028d }
        if (r1 != 0) goto L_0x0226;
    L_0x021f:
        r1 = new java.util.LinkedHashMap;	 Catch:{ Throwable -> 0x028d }
        r1.<init>();	 Catch:{ Throwable -> 0x028d }
        r5.f207N = r1;	 Catch:{ Throwable -> 0x028d }
    L_0x0226:
        if (r14 == 0) goto L_0x022d;
    L_0x0228:
        r1 = r5.f207N;	 Catch:{ Throwable -> 0x028d }
        r1.putAll(r14);	 Catch:{ Throwable -> 0x028d }
    L_0x022d:
        if (r5 != 0) goto L_0x0247;
    L_0x022f:
        r0 = "[ExtraCrashManager] Failed to package crash data.";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x028d }
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);	 Catch:{ Throwable -> 0x028d }
        r0 = "[ExtraCrashManager] Successfully handled.";
        r1 = new java.lang.Object[r6];
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);
        goto L_0x0012;
    L_0x0240:
        r2 = "";
        r7 = r2;
        r2 = r1;
        r1 = r7;
        goto L_0x0185;
    L_0x0247:
        r1 = com.tencent.bugly.proguard.C0761y.m486a();	 Catch:{ Throwable -> 0x028d }
        r2 = r8.f317c;	 Catch:{ Throwable -> 0x028d }
        r2 = r2.f131d;	 Catch:{ Throwable -> 0x028d }
        r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x028d }
        r3.<init>();	 Catch:{ Throwable -> 0x028d }
        r3 = r3.append(r11);	 Catch:{ Throwable -> 0x028d }
        r4 = "\n";
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x028d }
        r3 = r3.append(r12);	 Catch:{ Throwable -> 0x028d }
        r4 = "\n";
        r3 = r3.append(r4);	 Catch:{ Throwable -> 0x028d }
        r3 = r3.append(r13);	 Catch:{ Throwable -> 0x028d }
        r4 = r3.toString();	 Catch:{ Throwable -> 0x028d }
        r3 = r9;
        com.tencent.bugly.crashreport.crash.C0718b.m203a(r0, r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x028d }
        r0 = r8.f318d;	 Catch:{ Throwable -> 0x028d }
        r0 = r0.m214a(r5);	 Catch:{ Throwable -> 0x028d }
        if (r0 != 0) goto L_0x0284;
    L_0x027c:
        r0 = r8.f318d;	 Catch:{ Throwable -> 0x028d }
        r2 = 3000; // 0xbb8 float:4.204E-42 double:1.482E-320;
        r1 = 0;
        r0.m212a(r5, r2, r1);	 Catch:{ Throwable -> 0x028d }
    L_0x0284:
        r0 = "[ExtraCrashManager] Successfully handled.";
        r1 = new java.lang.Object[r6];
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);
        goto L_0x0012;
    L_0x028d:
        r0 = move-exception;
        r1 = com.tencent.bugly.proguard.C0757w.m457a(r0);	 Catch:{ all -> 0x02a0 }
        if (r1 != 0) goto L_0x0297;
    L_0x0294:
        r0.printStackTrace();	 Catch:{ all -> 0x02a0 }
    L_0x0297:
        r0 = "[ExtraCrashManager] Successfully handled.";
        r1 = new java.lang.Object[r6];
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);
        goto L_0x0012;
    L_0x02a0:
        r0 = move-exception;
        r1 = "[ExtraCrashManager] Successfully handled.";
        r2 = new java.lang.Object[r6];
        com.tencent.bugly.proguard.C0757w.m462e(r1, r2);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.crashreport.crash.d.a(com.tencent.bugly.crashreport.crash.d, java.lang.Thread, int, java.lang.String, java.lang.String, java.lang.String, java.util.Map):void");
    }

    private C0724d(Context context) {
        C0721c a = C0721c.m218a();
        if (a != null) {
            this.f316b = C0709a.m169a();
            this.f317c = C0705a.m84a(context);
            this.f318d = a.f300n;
            this.f319e = context;
            C0756v.m449a().m450a(new C07221(this));
        }
    }

    public static C0724d m238a(Context context) {
        if (f315a == null) {
            f315a = new C0724d(context);
        }
        return f315a;
    }

    public static void m241a(Thread thread, int i, String str, String str2, String str3, Map<String, String> map) {
        final Thread thread2 = thread;
        final int i2 = i;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final Map<String, String> map2 = map;
        C0756v.m449a().m450a(new Runnable() {
            public final void run() {
                try {
                    if (C0724d.f315a == null) {
                        C0757w.m462e("[ExtraCrashManager] Extra crash manager has not been initialized.", new Object[0]);
                    } else {
                        C0724d.m240a(C0724d.f315a, thread2, i2, str4, str5, str6, map2);
                    }
                } catch (Throwable th) {
                    if (!C0757w.m459b(th)) {
                        th.printStackTrace();
                    }
                    C0757w.m462e("[ExtraCrashManager] Crash error %s %s %s", str4, str5, str6);
                }
            }
        });
    }
}
