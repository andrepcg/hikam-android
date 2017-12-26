package com.tencent.bugly.proguard;

import android.content.Context;
import com.tencent.bugly.BuglyStrategy.C0691a;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import java.util.Map;
import java.util.UUID;

/* compiled from: BUGLY */
public final class C0754u implements Runnable {
    private int f441a;
    private int f442b;
    private final Context f443c;
    private final int f444d;
    private final byte[] f445e;
    private final C0705a f446f;
    private final C0709a f447g;
    private final C0748r f448h;
    private final C0753t f449i;
    private final int f450j;
    private final C0749s f451k;
    private final C0749s f452l;
    private String f453m;
    private final String f454n;
    private final Map<String, String> f455o;
    private int f456p;
    private long f457q;
    private long f458r;
    private boolean f459s;
    private boolean f460t;

    public C0754u(Context context, int i, int i2, byte[] bArr, String str, String str2, C0749s c0749s, boolean z, boolean z2) {
        this(context, i, i2, bArr, str, str2, c0749s, z, 2, C0691a.MAX_USERDATA_VALUE_LENGTH, z2, null);
    }

    public C0754u(Context context, int i, int i2, byte[] bArr, String str, String str2, C0749s c0749s, boolean z, int i3, int i4, boolean z2, Map<String, String> map) {
        this.f441a = 2;
        this.f442b = C0691a.MAX_USERDATA_VALUE_LENGTH;
        this.f453m = null;
        this.f456p = 0;
        this.f457q = 0;
        this.f458r = 0;
        this.f459s = true;
        this.f460t = false;
        this.f443c = context;
        this.f446f = C0705a.m84a(context);
        this.f445e = bArr;
        this.f447g = C0709a.m169a();
        this.f448h = C0748r.m405a(context);
        this.f449i = C0753t.m412a();
        this.f450j = i;
        this.f453m = str;
        this.f454n = str2;
        this.f451k = c0749s;
        C0753t c0753t = this.f449i;
        this.f452l = null;
        this.f459s = z;
        this.f444d = i2;
        if (i3 > 0) {
            this.f441a = i3;
        }
        if (i4 > 0) {
            this.f442b = i4;
        }
        this.f460t = z2;
        this.f455o = map;
    }

    private void m445a(am amVar, boolean z, int i, String str, int i2) {
        String str2;
        switch (this.f444d) {
            case 630:
            case 830:
                str2 = "crash";
                break;
            case 640:
            case 840:
                str2 = "userinfo";
                break;
            default:
                str2 = String.valueOf(this.f444d);
                break;
        }
        if (z) {
            C0757w.m456a("[Upload] Success: %s", str2);
        } else {
            C0757w.m462e("[Upload] Failed to upload(%d) %s: %s", Integer.valueOf(i), str2, str);
            if (this.f459s) {
                this.f449i.m436a(i2, null);
            }
        }
        if (this.f457q + this.f458r > 0) {
            this.f449i.m437a((this.f449i.m431a(this.f460t) + this.f457q) + this.f458r, this.f460t);
        }
        if (this.f451k != null) {
            C0749s c0749s = this.f451k;
            int i3 = this.f444d;
            long j = this.f457q;
            j = this.f458r;
            c0749s.mo2269a(z);
        }
        if (this.f452l != null) {
            c0749s = this.f452l;
            i3 = this.f444d;
            j = this.f457q;
            j = this.f458r;
            c0749s.mo2269a(z);
        }
    }

    private static boolean m446a(am amVar, C0705a c0705a, C0709a c0709a) {
        if (amVar == null) {
            C0757w.m461d("resp == null!", new Object[0]);
            return false;
        } else if (amVar.f634a != (byte) 0) {
            C0757w.m462e("resp result error %d", Byte.valueOf(amVar.f634a));
            return false;
        } else {
            try {
                if (!(C0761y.m501a(amVar.f637d) || C0705a.m85b().m121i().equals(amVar.f637d))) {
                    C0745o.m380a().m401a(C0709a.f182a, "key_ip", amVar.f637d.getBytes("UTF-8"), null, true);
                    c0705a.m113d(amVar.f637d);
                }
                if (!(C0761y.m501a(amVar.f639f) || C0705a.m85b().m122j().equals(amVar.f639f))) {
                    C0745o.m380a().m401a(C0709a.f182a, "key_imei", amVar.f639f.getBytes("UTF-8"), null, true);
                    c0705a.m115e(amVar.f639f);
                }
            } catch (Throwable th) {
                C0757w.m457a(th);
            }
            c0705a.f136i = amVar.f638e;
            if (amVar.f635b == 510) {
                if (amVar.f636c == null) {
                    C0757w.m462e("[Upload] Strategy data is null. Response cmd: %d", Integer.valueOf(amVar.f635b));
                    return false;
                }
                ao aoVar = (ao) C0730a.m290a(amVar.f636c, ao.class);
                if (aoVar == null) {
                    C0757w.m462e("[Upload] Failed to decode strategy from server. Response cmd: %d", Integer.valueOf(amVar.f635b));
                    return false;
                }
                c0709a.m175a(aoVar);
            }
            return true;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        /*
        r11 = this;
        r0 = 0;
        r11.f456p = r0;	 Catch:{ Throwable -> 0x0030 }
        r0 = 0;
        r11.f457q = r0;	 Catch:{ Throwable -> 0x0030 }
        r0 = 0;
        r11.f458r = r0;	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f445e;	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f443c;	 Catch:{ Throwable -> 0x0030 }
        r1 = com.tencent.bugly.crashreport.common.info.C0706b.m149e(r1);	 Catch:{ Throwable -> 0x0030 }
        if (r1 != 0) goto L_0x0020;
    L_0x0015:
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = "network is not available";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
    L_0x001f:
        return;
    L_0x0020:
        if (r0 == 0) goto L_0x0025;
    L_0x0022:
        r1 = r0.length;	 Catch:{ Throwable -> 0x0030 }
        if (r1 != 0) goto L_0x003b;
    L_0x0025:
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = "request package is empty!";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x0030:
        r0 = move-exception;
        r1 = com.tencent.bugly.proguard.C0757w.m457a(r0);
        if (r1 != 0) goto L_0x001f;
    L_0x0037:
        r0.printStackTrace();
        goto L_0x001f;
    L_0x003b:
        r1 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r2 = r11.f460t;	 Catch:{ Throwable -> 0x0030 }
        r2 = r1.m431a(r2);	 Catch:{ Throwable -> 0x0030 }
        r1 = r0.length;	 Catch:{ Throwable -> 0x0030 }
        r4 = (long) r1;	 Catch:{ Throwable -> 0x0030 }
        r4 = r4 + r2;
        r6 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r1 < 0) goto L_0x0086;
    L_0x004d:
        r0 = "[Upload] Upload too much data, try next time: %d/%d";
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x0030 }
        r4 = 0;
        r2 = java.lang.Long.valueOf(r2);	 Catch:{ Throwable -> 0x0030 }
        r1[r4] = r2;	 Catch:{ Throwable -> 0x0030 }
        r2 = 1;
        r4 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        r3 = java.lang.Long.valueOf(r4);	 Catch:{ Throwable -> 0x0030 }
        r1[r2] = r3;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r0 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0030 }
        r4 = "over net consume: ";
        r0.<init>(r4);	 Catch:{ Throwable -> 0x0030 }
        r4 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r0 = r0.append(r4);	 Catch:{ Throwable -> 0x0030 }
        r4 = "K";
        r0 = r0.append(r4);	 Catch:{ Throwable -> 0x0030 }
        r4 = r0.toString();	 Catch:{ Throwable -> 0x0030 }
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x0086:
        r1 = "[Upload] Run upload task with cmd: %d";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0030 }
        r3 = 0;
        r4 = r11.f444d;	 Catch:{ Throwable -> 0x0030 }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0030 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r1, r2);	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f443c;	 Catch:{ Throwable -> 0x0030 }
        if (r1 == 0) goto L_0x00a7;
    L_0x009b:
        r1 = r11.f446f;	 Catch:{ Throwable -> 0x0030 }
        if (r1 == 0) goto L_0x00a7;
    L_0x009f:
        r1 = r11.f447g;	 Catch:{ Throwable -> 0x0030 }
        if (r1 == 0) goto L_0x00a7;
    L_0x00a3:
        r1 = r11.f448h;	 Catch:{ Throwable -> 0x0030 }
        if (r1 != 0) goto L_0x00b3;
    L_0x00a7:
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = "illegal access error";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x00b3:
        r1 = r11.f447g;	 Catch:{ Throwable -> 0x0030 }
        r1 = r1.m177c();	 Catch:{ Throwable -> 0x0030 }
        if (r1 != 0) goto L_0x00c7;
    L_0x00bb:
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = "illegal local strategy";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x00c7:
        r3 = 0;
        r7 = new java.util.HashMap;	 Catch:{ Throwable -> 0x0030 }
        r7.<init>();	 Catch:{ Throwable -> 0x0030 }
        r2 = "prodId";
        r4 = r11.f446f;	 Catch:{ Throwable -> 0x0030 }
        r4 = r4.m116f();	 Catch:{ Throwable -> 0x0030 }
        r7.put(r2, r4);	 Catch:{ Throwable -> 0x0030 }
        r2 = "bundleId";
        r4 = r11.f446f;	 Catch:{ Throwable -> 0x0030 }
        r4 = r4.f130c;	 Catch:{ Throwable -> 0x0030 }
        r7.put(r2, r4);	 Catch:{ Throwable -> 0x0030 }
        r2 = "appVer";
        r4 = r11.f446f;	 Catch:{ Throwable -> 0x0030 }
        r4 = r4.f137j;	 Catch:{ Throwable -> 0x0030 }
        r7.put(r2, r4);	 Catch:{ Throwable -> 0x0030 }
        r2 = r11.f455o;	 Catch:{ Throwable -> 0x0030 }
        if (r2 == 0) goto L_0x00f3;
    L_0x00ee:
        r2 = r11.f455o;	 Catch:{ Throwable -> 0x0030 }
        r7.putAll(r2);	 Catch:{ Throwable -> 0x0030 }
    L_0x00f3:
        r2 = r11.f459s;	 Catch:{ Throwable -> 0x0030 }
        if (r2 == 0) goto L_0x015e;
    L_0x00f7:
        r2 = "cmd";
        r4 = r11.f444d;	 Catch:{ Throwable -> 0x0030 }
        r4 = java.lang.Integer.toString(r4);	 Catch:{ Throwable -> 0x0030 }
        r7.put(r2, r4);	 Catch:{ Throwable -> 0x0030 }
        r2 = "platformId";
        r4 = 1;
        r4 = java.lang.Byte.toString(r4);	 Catch:{ Throwable -> 0x0030 }
        r7.put(r2, r4);	 Catch:{ Throwable -> 0x0030 }
        r2 = "sdkVer";
        r4 = r11.f446f;	 Catch:{ Throwable -> 0x0030 }
        r4.getClass();	 Catch:{ Throwable -> 0x0030 }
        r4 = "2.4.0";
        r7.put(r2, r4);	 Catch:{ Throwable -> 0x0030 }
        r2 = "strategylastUpdateTime";
        r4 = r1.f171p;	 Catch:{ Throwable -> 0x0030 }
        r1 = java.lang.Long.toString(r4);	 Catch:{ Throwable -> 0x0030 }
        r7.put(r2, r1);	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r1 = r1.m438a(r7);	 Catch:{ Throwable -> 0x0030 }
        if (r1 != 0) goto L_0x0137;
    L_0x012b:
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = "failed to add security info to HTTP headers";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x0137:
        r1 = 2;
        r0 = com.tencent.bugly.proguard.C0761y.m506a(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x014a;
    L_0x013e:
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = "failed to zip request body";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x014a:
        r1 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r0 = r1.m439a(r0);	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x015e;
    L_0x0152:
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = "failed to encrypt request body";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x015e:
        r6 = r0;
        r0 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f450j;	 Catch:{ Throwable -> 0x0030 }
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0030 }
        r0.m433a(r1, r4);	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f451k;	 Catch:{ Throwable -> 0x0030 }
        if (r0 == 0) goto L_0x0172;
    L_0x016e:
        r0 = r11.f451k;	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f444d;	 Catch:{ Throwable -> 0x0030 }
    L_0x0172:
        r0 = r11.f452l;	 Catch:{ Throwable -> 0x0030 }
        if (r0 == 0) goto L_0x017a;
    L_0x0176:
        r0 = r11.f452l;	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f444d;	 Catch:{ Throwable -> 0x0030 }
    L_0x017a:
        r2 = r11.f453m;	 Catch:{ Throwable -> 0x0030 }
        r5 = -1;
        r0 = 0;
        r1 = r0;
        r0 = r2;
    L_0x0180:
        r4 = r1 + 1;
        r2 = r11.f441a;	 Catch:{ Throwable -> 0x0030 }
        if (r1 >= r2) goto L_0x04ac;
    L_0x0186:
        r1 = 1;
        if (r4 <= r1) goto L_0x01b1;
    L_0x0189:
        r1 = "[Upload] Failed to upload last time, wait and try(%d) again.";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0030 }
        r3 = 0;
        r8 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0030 }
        r2[r3] = r8;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m461d(r1, r2);	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f442b;	 Catch:{ Throwable -> 0x0030 }
        r2 = (long) r1;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0761y.m513b(r2);	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f441a;	 Catch:{ Throwable -> 0x0030 }
        if (r4 != r1) goto L_0x01b1;
    L_0x01a2:
        r0 = "[Upload] Use the back-up url at the last time: %s";
        r1 = 1;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x0030 }
        r2 = 0;
        r3 = r11.f454n;	 Catch:{ Throwable -> 0x0030 }
        r1[r2] = r3;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f454n;	 Catch:{ Throwable -> 0x0030 }
    L_0x01b1:
        r1 = "[Upload] Send %d bytes";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0030 }
        r3 = 0;
        r8 = r6.length;	 Catch:{ Throwable -> 0x0030 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Throwable -> 0x0030 }
        r2[r3] = r8;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r1, r2);	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f459s;	 Catch:{ Throwable -> 0x0030 }
        if (r1 == 0) goto L_0x04b7;
    L_0x01c5:
        r0 = com.tencent.bugly.proguard.C0754u.m444a(r0);	 Catch:{ Throwable -> 0x0030 }
        r2 = r0;
    L_0x01ca:
        r0 = "[Upload] Upload to %s with cmd %d (pid=%d | tid=%d).";
        r1 = 4;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x0030 }
        r3 = 0;
        r1[r3] = r2;	 Catch:{ Throwable -> 0x0030 }
        r3 = 1;
        r8 = r11.f444d;	 Catch:{ Throwable -> 0x0030 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Throwable -> 0x0030 }
        r1[r3] = r8;	 Catch:{ Throwable -> 0x0030 }
        r3 = 2;
        r8 = android.os.Process.myPid();	 Catch:{ Throwable -> 0x0030 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Throwable -> 0x0030 }
        r1[r3] = r8;	 Catch:{ Throwable -> 0x0030 }
        r3 = 3;
        r8 = android.os.Process.myTid();	 Catch:{ Throwable -> 0x0030 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Throwable -> 0x0030 }
        r1[r3] = r8;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f448h;	 Catch:{ Throwable -> 0x0030 }
        r1 = r0.m410a(r2, r6, r11, r7);	 Catch:{ Throwable -> 0x0030 }
        if (r1 != 0) goto L_0x0216;
    L_0x01fc:
        r0 = "Failed to upload for no response!";
        r1 = "[Upload] Failed to upload(%d): %s";
        r3 = 2;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0030 }
        r8 = 0;
        r9 = 1;
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Throwable -> 0x0030 }
        r3[r8] = r9;	 Catch:{ Throwable -> 0x0030 }
        r8 = 1;
        r3[r8] = r0;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m462e(r1, r3);	 Catch:{ Throwable -> 0x0030 }
        r3 = 1;
        r1 = r4;
        r0 = r2;
        goto L_0x0180;
    L_0x0216:
        r0 = r11.f448h;	 Catch:{ Throwable -> 0x0030 }
        r3 = r0.f412a;	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f459s;	 Catch:{ Throwable -> 0x0030 }
        if (r0 == 0) goto L_0x03d8;
    L_0x021e:
        if (r3 == 0) goto L_0x0226;
    L_0x0220:
        r0 = r3.size();	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x0298;
    L_0x0226:
        r0 = "[Upload] Headers is empty.";
        r8 = 0;
        r8 = new java.lang.Object[r8];	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r8);	 Catch:{ Throwable -> 0x0030 }
        r0 = 0;
    L_0x022f:
        if (r0 != 0) goto L_0x0300;
    L_0x0231:
        r0 = "[Upload] Headers from server is not valid, just try again (pid=%d | tid=%d).";
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x0030 }
        r8 = 0;
        r9 = android.os.Process.myPid();	 Catch:{ Throwable -> 0x0030 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Throwable -> 0x0030 }
        r1[r8] = r9;	 Catch:{ Throwable -> 0x0030 }
        r8 = 1;
        r9 = android.os.Process.myTid();	 Catch:{ Throwable -> 0x0030 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Throwable -> 0x0030 }
        r1[r8] = r9;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        r0 = "[Upload] Failed to upload for no status header.";
        r1 = "[Upload] Failed to upload(%d): %s";
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ Throwable -> 0x0030 }
        r9 = 0;
        r10 = 1;
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Throwable -> 0x0030 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x0030 }
        r9 = 1;
        r8[r9] = r0;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m462e(r1, r8);	 Catch:{ Throwable -> 0x0030 }
        if (r3 == 0) goto L_0x02f3;
    L_0x0266:
        r0 = r3.entrySet();	 Catch:{ Throwable -> 0x0030 }
        r1 = r0.iterator();	 Catch:{ Throwable -> 0x0030 }
    L_0x026e:
        r0 = r1.hasNext();	 Catch:{ Throwable -> 0x0030 }
        if (r0 == 0) goto L_0x02f3;
    L_0x0274:
        r0 = r1.next();	 Catch:{ Throwable -> 0x0030 }
        r0 = (java.util.Map.Entry) r0;	 Catch:{ Throwable -> 0x0030 }
        r3 = "[key]: %s, [value]: %s";
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ Throwable -> 0x0030 }
        r9 = 0;
        r10 = r0.getKey();	 Catch:{ Throwable -> 0x0030 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x0030 }
        r9 = 1;
        r0 = r0.getValue();	 Catch:{ Throwable -> 0x0030 }
        r8[r9] = r0;	 Catch:{ Throwable -> 0x0030 }
        r0 = java.lang.String.format(r3, r8);	 Catch:{ Throwable -> 0x0030 }
        r3 = 0;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x026e;
    L_0x0298:
        r0 = "status";
        r0 = r3.containsKey(r0);	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x02af;
    L_0x02a0:
        r0 = "[Upload] Headers does not contain %s";
        r8 = 1;
        r8 = new java.lang.Object[r8];	 Catch:{ Throwable -> 0x0030 }
        r9 = 0;
        r10 = "status";
        r8[r9] = r10;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r8);	 Catch:{ Throwable -> 0x0030 }
        r0 = 0;
        goto L_0x022f;
    L_0x02af:
        r0 = "Bugly-Version";
        r0 = r3.containsKey(r0);	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x02c7;
    L_0x02b7:
        r0 = "[Upload] Headers does not contain %s";
        r8 = 1;
        r8 = new java.lang.Object[r8];	 Catch:{ Throwable -> 0x0030 }
        r9 = 0;
        r10 = "Bugly-Version";
        r8[r9] = r10;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r8);	 Catch:{ Throwable -> 0x0030 }
        r0 = 0;
        goto L_0x022f;
    L_0x02c7:
        r0 = "Bugly-Version";
        r0 = r3.get(r0);	 Catch:{ Throwable -> 0x0030 }
        r0 = (java.lang.String) r0;	 Catch:{ Throwable -> 0x0030 }
        r8 = "bugly";
        r8 = r0.contains(r8);	 Catch:{ Throwable -> 0x0030 }
        if (r8 != 0) goto L_0x02e5;
    L_0x02d7:
        r8 = "[Upload] Bugly version is not valid: %s";
        r9 = 1;
        r9 = new java.lang.Object[r9];	 Catch:{ Throwable -> 0x0030 }
        r10 = 0;
        r9[r10] = r0;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m461d(r8, r9);	 Catch:{ Throwable -> 0x0030 }
        r0 = 0;
        goto L_0x022f;
    L_0x02e5:
        r8 = "[Upload] Bugly version from headers is: %s";
        r9 = 1;
        r9 = new java.lang.Object[r9];	 Catch:{ Throwable -> 0x0030 }
        r10 = 0;
        r9[r10] = r0;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r8, r9);	 Catch:{ Throwable -> 0x0030 }
        r0 = 1;
        goto L_0x022f;
    L_0x02f3:
        r0 = "[Upload] Failed to upload for no status header.";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        r3 = 1;
        r1 = r4;
        r0 = r2;
        goto L_0x0180;
    L_0x0300:
        r0 = "status";
        r0 = r3.get(r0);	 Catch:{ Throwable -> 0x0394 }
        r0 = (java.lang.String) r0;	 Catch:{ Throwable -> 0x0394 }
        r5 = java.lang.Integer.parseInt(r0);	 Catch:{ Throwable -> 0x0394 }
        r0 = "[Upload] Status from server is %d (pid=%d | tid=%d).";
        r8 = 3;
        r8 = new java.lang.Object[r8];	 Catch:{ Throwable -> 0x0394 }
        r9 = 0;
        r10 = java.lang.Integer.valueOf(r5);	 Catch:{ Throwable -> 0x0394 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x0394 }
        r9 = 1;
        r10 = android.os.Process.myPid();	 Catch:{ Throwable -> 0x0394 }
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Throwable -> 0x0394 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x0394 }
        r9 = 2;
        r10 = android.os.Process.myTid();	 Catch:{ Throwable -> 0x0394 }
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Throwable -> 0x0394 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x0394 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r8);	 Catch:{ Throwable -> 0x0394 }
        if (r5 == 0) goto L_0x03d8;
    L_0x0333:
        r0 = 2;
        if (r5 != r0) goto L_0x03c0;
    L_0x0336:
        r0 = r11.f457q;	 Catch:{ Throwable -> 0x0030 }
        r2 = r11.f458r;	 Catch:{ Throwable -> 0x0030 }
        r0 = r0 + r2;
        r2 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x0356;
    L_0x0341:
        r0 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f460t;	 Catch:{ Throwable -> 0x0030 }
        r0 = r0.m431a(r1);	 Catch:{ Throwable -> 0x0030 }
        r2 = r11.f457q;	 Catch:{ Throwable -> 0x0030 }
        r0 = r0 + r2;
        r2 = r11.f458r;	 Catch:{ Throwable -> 0x0030 }
        r0 = r0 + r2;
        r2 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r3 = r11.f460t;	 Catch:{ Throwable -> 0x0030 }
        r2.m437a(r0, r3);	 Catch:{ Throwable -> 0x0030 }
    L_0x0356:
        r0 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r1 = 0;
        r0.m436a(r5, r1);	 Catch:{ Throwable -> 0x0030 }
        r0 = "[Upload] Session ID is invalid, will try again immediately (pid=%d | tid=%d).";
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x0030 }
        r2 = 0;
        r3 = android.os.Process.myPid();	 Catch:{ Throwable -> 0x0030 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Throwable -> 0x0030 }
        r1[r2] = r3;	 Catch:{ Throwable -> 0x0030 }
        r2 = 1;
        r3 = android.os.Process.myTid();	 Catch:{ Throwable -> 0x0030 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Throwable -> 0x0030 }
        r1[r2] = r3;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m456a(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r1 = r11.f450j;	 Catch:{ Throwable -> 0x0030 }
        r2 = r11.f444d;	 Catch:{ Throwable -> 0x0030 }
        r3 = r11.f445e;	 Catch:{ Throwable -> 0x0030 }
        r4 = r11.f453m;	 Catch:{ Throwable -> 0x0030 }
        r5 = r11.f454n;	 Catch:{ Throwable -> 0x0030 }
        r6 = r11.f451k;	 Catch:{ Throwable -> 0x0030 }
        r7 = r11.f441a;	 Catch:{ Throwable -> 0x0030 }
        r8 = r11.f442b;	 Catch:{ Throwable -> 0x0030 }
        r9 = 1;
        r10 = r11.f455o;	 Catch:{ Throwable -> 0x0030 }
        r0.m432a(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x0394:
        r0 = move-exception;
        r0 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0030 }
        r1 = "[Upload] Failed to upload for format of status header is invalid: ";
        r0.<init>(r1);	 Catch:{ Throwable -> 0x0030 }
        r1 = java.lang.Integer.toString(r5);	 Catch:{ Throwable -> 0x0030 }
        r0 = r0.append(r1);	 Catch:{ Throwable -> 0x0030 }
        r0 = r0.toString();	 Catch:{ Throwable -> 0x0030 }
        r1 = "[Upload] Failed to upload(%d): %s";
        r3 = 2;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0030 }
        r8 = 0;
        r9 = 1;
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Throwable -> 0x0030 }
        r3[r8] = r9;	 Catch:{ Throwable -> 0x0030 }
        r8 = 1;
        r3[r8] = r0;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m462e(r1, r3);	 Catch:{ Throwable -> 0x0030 }
        r3 = 1;
        r1 = r4;
        r0 = r2;
        goto L_0x0180;
    L_0x03c0:
        r1 = 0;
        r2 = 0;
        r3 = 1;
        r0 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0030 }
        r4 = "status of server is ";
        r0.<init>(r4);	 Catch:{ Throwable -> 0x0030 }
        r0 = r0.append(r5);	 Catch:{ Throwable -> 0x0030 }
        r4 = r0.toString();	 Catch:{ Throwable -> 0x0030 }
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x03d8:
        r0 = "[Upload] Received %d bytes";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0030 }
        r4 = 0;
        r6 = r1.length;	 Catch:{ Throwable -> 0x0030 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Throwable -> 0x0030 }
        r2[r4] = r6;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r2);	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f459s;	 Catch:{ Throwable -> 0x0030 }
        if (r0 == 0) goto L_0x044d;
    L_0x03ec:
        r0 = r1.length;	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x0426;
    L_0x03ef:
        r0 = r3.entrySet();	 Catch:{ Throwable -> 0x0030 }
        r1 = r0.iterator();	 Catch:{ Throwable -> 0x0030 }
    L_0x03f7:
        r0 = r1.hasNext();	 Catch:{ Throwable -> 0x0030 }
        if (r0 == 0) goto L_0x041a;
    L_0x03fd:
        r0 = r1.next();	 Catch:{ Throwable -> 0x0030 }
        r0 = (java.util.Map.Entry) r0;	 Catch:{ Throwable -> 0x0030 }
        r2 = "[Upload] HTTP headers from server: key = %s, value = %s";
        r3 = 2;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0030 }
        r4 = 0;
        r5 = r0.getKey();	 Catch:{ Throwable -> 0x0030 }
        r3[r4] = r5;	 Catch:{ Throwable -> 0x0030 }
        r4 = 1;
        r0 = r0.getValue();	 Catch:{ Throwable -> 0x0030 }
        r3[r4] = r0;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r2, r3);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x03f7;
    L_0x041a:
        r1 = 0;
        r2 = 0;
        r3 = 1;
        r4 = "response data from server is empty";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x0426:
        r0 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r0 = r0.m443b(r1);	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x043a;
    L_0x042e:
        r1 = 0;
        r2 = 0;
        r3 = 1;
        r4 = "failed to decrypt response from server";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x043a:
        r1 = 2;
        r0 = com.tencent.bugly.proguard.C0761y.m518b(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x044e;
    L_0x0441:
        r1 = 0;
        r2 = 0;
        r3 = 1;
        r4 = "failed unzip(Gzip) response from server";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x044d:
        r0 = r1;
    L_0x044e:
        r1 = r11.f459s;	 Catch:{ Throwable -> 0x0030 }
        r1 = com.tencent.bugly.proguard.C0730a.m287a(r0, r1);	 Catch:{ Throwable -> 0x0030 }
        if (r1 != 0) goto L_0x0462;
    L_0x0456:
        r1 = 0;
        r2 = 0;
        r3 = 1;
        r4 = "failed to decode response package";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x0462:
        r0 = r11.f459s;	 Catch:{ Throwable -> 0x0030 }
        if (r0 == 0) goto L_0x046b;
    L_0x0466:
        r0 = r11.f449i;	 Catch:{ Throwable -> 0x0030 }
        r0.m436a(r5, r1);	 Catch:{ Throwable -> 0x0030 }
    L_0x046b:
        r2 = "[Upload] Response cmd is: %d, length of sBuffer is: %d";
        r0 = 2;
        r3 = new java.lang.Object[r0];	 Catch:{ Throwable -> 0x0030 }
        r0 = 0;
        r4 = r1.f635b;	 Catch:{ Throwable -> 0x0030 }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0030 }
        r3[r0] = r4;	 Catch:{ Throwable -> 0x0030 }
        r4 = 1;
        r0 = r1.f636c;	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x049d;
    L_0x047e:
        r0 = 0;
    L_0x047f:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Throwable -> 0x0030 }
        r3[r4] = r0;	 Catch:{ Throwable -> 0x0030 }
        com.tencent.bugly.proguard.C0757w.m460c(r2, r3);	 Catch:{ Throwable -> 0x0030 }
        r0 = r11.f446f;	 Catch:{ Throwable -> 0x0030 }
        r2 = r11.f447g;	 Catch:{ Throwable -> 0x0030 }
        r0 = com.tencent.bugly.proguard.C0754u.m446a(r1, r0, r2);	 Catch:{ Throwable -> 0x0030 }
        if (r0 != 0) goto L_0x04a1;
    L_0x0492:
        r2 = 0;
        r3 = 2;
        r4 = "failed to process response package";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x049d:
        r0 = r1.f636c;	 Catch:{ Throwable -> 0x0030 }
        r0 = r0.length;	 Catch:{ Throwable -> 0x0030 }
        goto L_0x047f;
    L_0x04a1:
        r2 = 1;
        r3 = 2;
        r4 = "successfully uploaded";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x04ac:
        r1 = 0;
        r2 = 0;
        r4 = "failed after many attempts";
        r5 = 0;
        r0 = r11;
        r0.m445a(r1, r2, r3, r4, r5);	 Catch:{ Throwable -> 0x0030 }
        goto L_0x001f;
    L_0x04b7:
        r2 = r0;
        goto L_0x01ca;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.proguard.u.run():void");
    }

    public final void m447a(long j) {
        this.f456p++;
        this.f457q += j;
    }

    public final void m448b(long j) {
        this.f458r += j;
    }

    private static String m444a(String str) {
        if (!C0761y.m501a(str)) {
            try {
                str = String.format("%s?aid=%s", new Object[]{str, UUID.randomUUID().toString()});
            } catch (Throwable th) {
                C0757w.m457a(th);
            }
        }
        return str;
    }
}
