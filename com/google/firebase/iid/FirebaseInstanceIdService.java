package com.google.firebase.iid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.jwkj.global.Constants.Action;

public class FirebaseInstanceIdService extends zzb {
    private static BroadcastReceiver bhu;
    private static final Object bhv = new Object();
    private static boolean bhw = false;
    private boolean bhx = false;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void zza(android.content.Context r2, com.google.firebase.iid.FirebaseInstanceId r3) {
        /*
        r1 = bhv;
        monitor-enter(r1);
        r0 = bhw;	 Catch:{ all -> 0x0026 }
        if (r0 == 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r1);	 Catch:{ all -> 0x0026 }
    L_0x0008:
        return;
    L_0x0009:
        monitor-exit(r1);	 Catch:{ all -> 0x0026 }
        r0 = r3.m14C();
        if (r0 == 0) goto L_0x0022;
    L_0x0010:
        r1 = com.google.firebase.iid.zzd.afY;
        r0 = r0.zztz(r1);
        if (r0 != 0) goto L_0x0022;
    L_0x0018:
        r0 = r3.m16E();
        r0 = r0.m20J();
        if (r0 == 0) goto L_0x0008;
    L_0x0022:
        zzet(r2);
        goto L_0x0008;
    L_0x0026:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0026 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.FirebaseInstanceIdService.zza(android.content.Context, com.google.firebase.iid.FirebaseInstanceId):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zza(android.content.Intent r9, boolean r10) {
        /*
        r8 = this;
        r2 = 1;
        r1 = 0;
        r3 = bhv;
        monitor-enter(r3);
        r0 = 0;
        bhw = r0;	 Catch:{ all -> 0x0010 }
        monitor-exit(r3);	 Catch:{ all -> 0x0010 }
        r0 = com.google.firebase.iid.zzf.zzdj(r8);
        if (r0 != 0) goto L_0x0013;
    L_0x000f:
        return;
    L_0x0010:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0010 }
        throw r0;
    L_0x0013:
        r0 = com.google.firebase.iid.FirebaseInstanceId.getInstance();
        r3 = r0.m14C();
        if (r3 != 0) goto L_0x004d;
    L_0x001d:
        r1 = r0.m15D();	 Catch:{ IOException -> 0x0035, SecurityException -> 0x0044 }
        if (r1 == 0) goto L_0x003e;
    L_0x0023:
        r1 = r8.bhx;	 Catch:{ IOException -> 0x0035, SecurityException -> 0x0044 }
        if (r1 == 0) goto L_0x002e;
    L_0x0027:
        r1 = "FirebaseInstanceId";
        r2 = "get master token succeeded";
        android.util.Log.d(r1, r2);	 Catch:{ IOException -> 0x0035, SecurityException -> 0x0044 }
    L_0x002e:
        zza(r8, r0);	 Catch:{ IOException -> 0x0035, SecurityException -> 0x0044 }
        r8.onTokenRefresh();	 Catch:{ IOException -> 0x0035, SecurityException -> 0x0044 }
        goto L_0x000f;
    L_0x0035:
        r0 = move-exception;
        r0 = r0.getMessage();
        r8.zzd(r9, r0);
        goto L_0x000f;
    L_0x003e:
        r0 = "returned token is null";
        r8.zzd(r9, r0);	 Catch:{ IOException -> 0x0035, SecurityException -> 0x0044 }
        goto L_0x000f;
    L_0x0044:
        r0 = move-exception;
        r1 = "FirebaseInstanceId";
        r2 = "Unable to get master token";
        android.util.Log.e(r1, r2, r0);
        goto L_0x000f;
    L_0x004d:
        r4 = r0.m16E();
        r0 = r4.m20J();
        r3 = r0;
    L_0x0056:
        if (r3 == 0) goto L_0x00be;
    L_0x0058:
        r0 = "!";
        r0 = r3.split(r0);
        r5 = r0.length;
        r6 = 2;
        if (r5 != r6) goto L_0x0071;
    L_0x0062:
        r5 = r0[r1];
        r6 = r0[r2];
        r0 = -1;
        r7 = r5.hashCode();	 Catch:{ IOException -> 0x00a1 }
        switch(r7) {
            case 83: goto L_0x007a;
            case 84: goto L_0x006e;
            case 85: goto L_0x0084;
            default: goto L_0x006e;
        };
    L_0x006e:
        switch(r0) {
            case 0: goto L_0x008e;
            case 1: goto L_0x00ab;
            default: goto L_0x0071;
        };
    L_0x0071:
        r4.zztv(r3);
        r0 = r4.m20J();
        r3 = r0;
        goto L_0x0056;
    L_0x007a:
        r7 = "S";
        r5 = r5.equals(r7);	 Catch:{ IOException -> 0x00a1 }
        if (r5 == 0) goto L_0x006e;
    L_0x0082:
        r0 = r1;
        goto L_0x006e;
    L_0x0084:
        r7 = "U";
        r5 = r5.equals(r7);	 Catch:{ IOException -> 0x00a1 }
        if (r5 == 0) goto L_0x006e;
    L_0x008c:
        r0 = r2;
        goto L_0x006e;
    L_0x008e:
        r0 = com.google.firebase.iid.FirebaseInstanceId.getInstance();	 Catch:{ IOException -> 0x00a1 }
        r0.zzts(r6);	 Catch:{ IOException -> 0x00a1 }
        r0 = r8.bhx;	 Catch:{ IOException -> 0x00a1 }
        if (r0 == 0) goto L_0x0071;
    L_0x0099:
        r0 = "FirebaseInstanceId";
        r5 = "subscribe operation succeeded";
        android.util.Log.d(r0, r5);	 Catch:{ IOException -> 0x00a1 }
        goto L_0x0071;
    L_0x00a1:
        r0 = move-exception;
        r0 = r0.getMessage();
        r8.zzd(r9, r0);
        goto L_0x000f;
    L_0x00ab:
        r0 = com.google.firebase.iid.FirebaseInstanceId.getInstance();	 Catch:{ IOException -> 0x00a1 }
        r0.zztt(r6);	 Catch:{ IOException -> 0x00a1 }
        r0 = r8.bhx;	 Catch:{ IOException -> 0x00a1 }
        if (r0 == 0) goto L_0x0071;
    L_0x00b6:
        r0 = "FirebaseInstanceId";
        r5 = "unsubscribe operation succeeded";
        android.util.Log.d(r0, r5);	 Catch:{ IOException -> 0x00a1 }
        goto L_0x0071;
    L_0x00be:
        r0 = "FirebaseInstanceId";
        r1 = "topic sync succeeded";
        android.util.Log.d(r0, r1);
        goto L_0x000f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.FirebaseInstanceIdService.zza(android.content.Intent, boolean):void");
    }

    private void zza(zzf com_google_firebase_iid_zzf, Bundle bundle) {
        String zzdj = zzf.zzdj(this);
        if (zzdj == null) {
            Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
            return;
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        intent.setPackage(zzdj);
        intent.putExtras(bundle);
        com_google_firebase_iid_zzf.zzs(intent);
        intent.putExtra("google.to", "google.com/iid");
        intent.putExtra("google.message_id", zzf.zzbov());
        sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    private static Intent zzagk(int i) {
        Context applicationContext = FirebaseApp.getInstance().getApplicationContext();
        Intent intent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
        intent.putExtra("next_retry_delay_in_seconds", i);
        return FirebaseInstanceIdInternalReceiver.zzg(applicationContext, intent);
    }

    private void zzagl(int i) {
        ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + ((long) (i * 1000)), PendingIntent.getBroadcast(this, 0, zzagk(i * 2), 268435456));
    }

    private String zzai(Intent intent) {
        String stringExtra = intent.getStringExtra("subtype");
        return stringExtra == null ? "" : stringExtra;
    }

    private int zzb(Intent intent, boolean z) {
        int intExtra = intent == null ? 10 : intent.getIntExtra("next_retry_delay_in_seconds", 0);
        return (intExtra >= 10 || z) ? intExtra >= 10 ? intExtra > 28800 ? 28800 : intExtra : 10 : 30;
    }

    private void zzd(Intent intent, String str) {
        boolean zzeu = zzeu(this);
        final int zzb = zzb(intent, zzeu);
        Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 47).append("background sync failed: ").append(str).append(", retry in ").append(zzb).append("s").toString());
        synchronized (bhv) {
            zzagl(zzb);
            bhw = true;
        }
        if (!zzeu) {
            if (this.bhx) {
                Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
            }
            if (bhu == null) {
                bhu = new BroadcastReceiver(this) {
                    final /* synthetic */ FirebaseInstanceIdService bhz;

                    public void onReceive(Context context, Intent intent) {
                        if (FirebaseInstanceIdService.zzeu(context)) {
                            if (this.bhz.bhx) {
                                Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                            }
                            this.bhz.getApplicationContext().unregisterReceiver(this);
                            context.sendBroadcast(FirebaseInstanceIdService.zzagk(zzb));
                        }
                    }
                };
            }
            getApplicationContext().registerReceiver(bhu, new IntentFilter(Action.ACTION_NETWORK_CHANGE));
        }
    }

    static void zzet(Context context) {
        if (zzf.zzdj(context) != null) {
            synchronized (bhv) {
                if (!bhw) {
                    context.sendBroadcast(zzagk(0));
                    bhw = true;
                }
            }
        }
    }

    private static boolean zzeu(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private zzd zztu(String str) {
        if (str == null) {
            return zzd.zzb(this, null);
        }
        Bundle bundle = new Bundle();
        bundle.putString("subtype", str);
        return zzd.zzb(this, bundle);
    }

    @WorkerThread
    public void onTokenRefresh() {
    }

    protected Intent zzae(Intent intent) {
        return FirebaseInstanceIdInternalReceiver.m528F();
    }

    public boolean zzag(Intent intent) {
        this.bhx = Log.isLoggable("FirebaseInstanceId", 3);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            return false;
        }
        String zzai = zzai(intent);
        if (this.bhx) {
            String str = "FirebaseInstanceId";
            String str2 = "Register result in service ";
            String valueOf = String.valueOf(zzai);
            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zztu(zzai).m19I().zzv(intent);
        return true;
    }

    public void zzah(Intent intent) {
        String zzai = zzai(intent);
        zzd zztu = zztu(zzai);
        String stringExtra = intent.getStringExtra("CMD");
        if (this.bhx) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("FirebaseInstanceId", new StringBuilder(((String.valueOf(zzai).length() + 18) + String.valueOf(stringExtra).length()) + String.valueOf(valueOf).length()).append("Service command ").append(zzai).append(" ").append(stringExtra).append(" ").append(valueOf).toString());
        }
        if (intent.getStringExtra("unregistered") != null) {
            zzg H = zztu.m18H();
            if (zzai == null) {
                zzai = "";
            }
            H.zzku(zzai);
            zztu.m19I().zzv(intent);
        } else if ("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
            zztu.m18H().zzku(zzai);
            zza(intent, false);
        } else if ("RST".equals(stringExtra)) {
            zztu.zzboq();
            zza(intent, true);
        } else if ("RST_FULL".equals(stringExtra)) {
            if (!zztu.m18H().isEmpty()) {
                zztu.zzboq();
                zztu.m18H().zzbow();
                zza(intent, true);
            }
        } else if ("SYNC".equals(stringExtra)) {
            zztu.m18H().zzku(zzai);
            zza(intent, false);
        } else if ("PING".equals(stringExtra)) {
            zza(zztu.m19I(), intent.getExtras());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzm(android.content.Intent r5) {
        /*
        r4 = this;
        r1 = 0;
        r0 = r5.getAction();
        if (r0 != 0) goto L_0x0009;
    L_0x0007:
        r0 = "";
    L_0x0009:
        r2 = -1;
        r3 = r0.hashCode();
        switch(r3) {
            case -1737547627: goto L_0x0019;
            default: goto L_0x0011;
        };
    L_0x0011:
        r0 = r2;
    L_0x0012:
        switch(r0) {
            case 0: goto L_0x0023;
            default: goto L_0x0015;
        };
    L_0x0015:
        r4.zzah(r5);
    L_0x0018:
        return;
    L_0x0019:
        r3 = "ACTION_TOKEN_REFRESH_RETRY";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x0011;
    L_0x0021:
        r0 = r1;
        goto L_0x0012;
    L_0x0023:
        r4.zza(r5, r1);
        goto L_0x0018;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.FirebaseInstanceIdService.zzm(android.content.Intent):void");
    }
}
