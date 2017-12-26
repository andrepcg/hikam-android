package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzw;
import com.google.android.gms.common.util.zzz;

public class zzxb {
    private static boolean DEBUG = false;
    private static String TAG = "WakeLock";
    private static String aAo = "*gcore*:";
    private final String EA;
    private final String Ey;
    private final WakeLock aAp;
    private final int aAq;
    private final String aAr;
    private boolean aAs;
    private int aAt;
    private int aAu;
    private WorkSource agC;
    private final Context mContext;

    public zzxb(Context context, int i, String str) {
        this(context, i, str, null, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzxb(Context context, int i, String str, String str2, String str3) {
        this(context, i, str, str2, str3, null);
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzxb(Context context, int i, String str, String str2, String str3, String str4) {
        this.aAs = true;
        zzac.zzh(str, "Wake lock name can NOT be empty");
        this.aAq = i;
        this.aAr = str2;
        this.EA = str4;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.Ey = str;
        } else {
            String valueOf = String.valueOf(aAo);
            String valueOf2 = String.valueOf(str);
            this.Ey = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.aAp = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        if (zzz.zzcp(this.mContext)) {
            if (zzw.zzij(str3)) {
                str3 = context.getPackageName();
            }
            this.agC = zzz.zzy(context, str3);
            zzc(this.agC);
        }
    }

    private void zzd(WorkSource workSource) {
        try {
            this.aAp.setWorkSource(workSource);
        } catch (IllegalArgumentException e) {
            Log.wtf(TAG, e.toString());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzm(java.lang.String r13, long r14) {
        /*
        r12 = this;
        r0 = r12.zzop(r13);
        r6 = r12.zzp(r13, r0);
        monitor-enter(r12);
        r1 = r12.aAs;	 Catch:{ all -> 0x0044 }
        if (r1 == 0) goto L_0x0017;
    L_0x000d:
        r1 = r12.aAt;	 Catch:{ all -> 0x0044 }
        r2 = r1 + 1;
        r12.aAt = r2;	 Catch:{ all -> 0x0044 }
        if (r1 == 0) goto L_0x001f;
    L_0x0015:
        if (r0 != 0) goto L_0x001f;
    L_0x0017:
        r0 = r12.aAs;	 Catch:{ all -> 0x0044 }
        if (r0 != 0) goto L_0x0042;
    L_0x001b:
        r0 = r12.aAu;	 Catch:{ all -> 0x0044 }
        if (r0 != 0) goto L_0x0042;
    L_0x001f:
        r1 = com.google.android.gms.common.stats.zzh.zzaxf();	 Catch:{ all -> 0x0044 }
        r2 = r12.mContext;	 Catch:{ all -> 0x0044 }
        r0 = r12.aAp;	 Catch:{ all -> 0x0044 }
        r3 = com.google.android.gms.common.stats.zzf.zza(r0, r6);	 Catch:{ all -> 0x0044 }
        r4 = 7;
        r5 = r12.Ey;	 Catch:{ all -> 0x0044 }
        r7 = r12.EA;	 Catch:{ all -> 0x0044 }
        r8 = r12.aAq;	 Catch:{ all -> 0x0044 }
        r0 = r12.agC;	 Catch:{ all -> 0x0044 }
        r9 = com.google.android.gms.common.util.zzz.zzb(r0);	 Catch:{ all -> 0x0044 }
        r10 = r14;
        r1.zza(r2, r3, r4, r5, r6, r7, r8, r9, r10);	 Catch:{ all -> 0x0044 }
        r0 = r12.aAu;	 Catch:{ all -> 0x0044 }
        r0 = r0 + 1;
        r12.aAu = r0;	 Catch:{ all -> 0x0044 }
    L_0x0042:
        monitor-exit(r12);	 Catch:{ all -> 0x0044 }
        return;
    L_0x0044:
        r0 = move-exception;
        monitor-exit(r12);	 Catch:{ all -> 0x0044 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzxb.zzm(java.lang.String, long):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzoo(java.lang.String r10) {
        /*
        r9 = this;
        r0 = r9.zzop(r10);
        r5 = r9.zzp(r10, r0);
        monitor-enter(r9);
        r1 = r9.aAs;	 Catch:{ all -> 0x0045 }
        if (r1 == 0) goto L_0x0017;
    L_0x000d:
        r1 = r9.aAt;	 Catch:{ all -> 0x0045 }
        r1 = r1 + -1;
        r9.aAt = r1;	 Catch:{ all -> 0x0045 }
        if (r1 == 0) goto L_0x0020;
    L_0x0015:
        if (r0 != 0) goto L_0x0020;
    L_0x0017:
        r0 = r9.aAs;	 Catch:{ all -> 0x0045 }
        if (r0 != 0) goto L_0x0043;
    L_0x001b:
        r0 = r9.aAu;	 Catch:{ all -> 0x0045 }
        r1 = 1;
        if (r0 != r1) goto L_0x0043;
    L_0x0020:
        r0 = com.google.android.gms.common.stats.zzh.zzaxf();	 Catch:{ all -> 0x0045 }
        r1 = r9.mContext;	 Catch:{ all -> 0x0045 }
        r2 = r9.aAp;	 Catch:{ all -> 0x0045 }
        r2 = com.google.android.gms.common.stats.zzf.zza(r2, r5);	 Catch:{ all -> 0x0045 }
        r3 = 8;
        r4 = r9.Ey;	 Catch:{ all -> 0x0045 }
        r6 = r9.EA;	 Catch:{ all -> 0x0045 }
        r7 = r9.aAq;	 Catch:{ all -> 0x0045 }
        r8 = r9.agC;	 Catch:{ all -> 0x0045 }
        r8 = com.google.android.gms.common.util.zzz.zzb(r8);	 Catch:{ all -> 0x0045 }
        r0.zza(r1, r2, r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x0045 }
        r0 = r9.aAu;	 Catch:{ all -> 0x0045 }
        r0 = r0 + -1;
        r9.aAu = r0;	 Catch:{ all -> 0x0045 }
    L_0x0043:
        monitor-exit(r9);	 Catch:{ all -> 0x0045 }
        return;
    L_0x0045:
        r0 = move-exception;
        monitor-exit(r9);	 Catch:{ all -> 0x0045 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzxb.zzoo(java.lang.String):void");
    }

    private boolean zzop(String str) {
        return (TextUtils.isEmpty(str) || str.equals(this.aAr)) ? false : true;
    }

    private String zzp(String str, boolean z) {
        return this.aAs ? z ? str : this.aAr : this.aAr;
    }

    public void acquire(long j) {
        if (!zzs.zzaxn() && this.aAs) {
            String str = TAG;
            String str2 = "Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: ";
            String valueOf = String.valueOf(this.Ey);
            Log.wtf(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzm(null, j);
        this.aAp.acquire(j);
    }

    public boolean isHeld() {
        return this.aAp.isHeld();
    }

    public void release() {
        zzoo(null);
        this.aAp.release();
    }

    public void setReferenceCounted(boolean z) {
        this.aAp.setReferenceCounted(z);
        this.aAs = z;
    }

    public void zzc(WorkSource workSource) {
        if (workSource != null && zzz.zzcp(this.mContext)) {
            if (this.agC != null) {
                this.agC.add(workSource);
            } else {
                this.agC = workSource;
            }
            zzd(this.agC);
        }
    }
}
