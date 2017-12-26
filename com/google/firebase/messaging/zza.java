package com.google.firebase.messaging;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceIdInternalReceiver;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;

class zza {
    static zza bhH;
    private final AtomicInteger bhI = new AtomicInteger((int) SystemClock.elapsedRealtime());
    private final Context mContext;

    private zza(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private PendingIntent zza(Bundle bundle, PendingIntent pendingIntent) {
        Intent intent = new Intent("com.google.firebase.messaging.NOTIFICATION_OPEN");
        zza(intent, bundle);
        intent.putExtra("pending_intent", pendingIntent);
        return PendingIntent.getBroadcast(this.mContext, zzbnz(), FirebaseInstanceIdInternalReceiver.zzh(this.mContext, intent), 1073741824);
    }

    private void zza(Intent intent, Bundle bundle) {
        for (String str : bundle.keySet()) {
            if (str.startsWith("google.c.a.") || str.equals("from")) {
                intent.putExtra(str, bundle.getString(str));
            }
        }
    }

    private void zza(String str, Notification notification) {
        if (Log.isLoggable("FirebaseMessaging", 3)) {
            Log.d("FirebaseMessaging", "Showing notification");
        }
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        if (TextUtils.isEmpty(str)) {
            str = "GCM-Notification:" + SystemClock.uptimeMillis();
        }
        notificationManager.notify(str, 0, notification);
    }

    static boolean zzad(Bundle bundle) {
        return "1".equals(zzf(bundle, "gcm.n.e")) || zzf(bundle, "gcm.n.icon") != null;
    }

    private Notification zzag(Bundle bundle) {
        CharSequence zzg = zzg(bundle, "gcm.n.title");
        CharSequence zzg2 = zzg(bundle, "gcm.n.body");
        int zzkl = zzkl(zzf(bundle, "gcm.n.icon"));
        Object zzf = zzf(bundle, "gcm.n.color");
        Uri zzkm = zzkm(zzat(bundle));
        PendingIntent zzah = zzah(bundle);
        PendingIntent pendingIntent = null;
        if (FirebaseMessagingService.zzav(bundle)) {
            zzah = zza(bundle, zzah);
            pendingIntent = zzau(bundle);
        }
        Builder smallIcon = new Builder(this.mContext).setAutoCancel(true).setSmallIcon(zzkl);
        if (TextUtils.isEmpty(zzg)) {
            smallIcon.setContentTitle(this.mContext.getApplicationInfo().loadLabel(this.mContext.getPackageManager()));
        } else {
            smallIcon.setContentTitle(zzg);
        }
        if (!TextUtils.isEmpty(zzg2)) {
            smallIcon.setContentText(zzg2);
            smallIcon.setStyle(new BigTextStyle().bigText(zzg2));
        }
        if (!TextUtils.isEmpty(zzf)) {
            smallIcon.setColor(Color.parseColor(zzf));
        }
        if (zzkm != null) {
            smallIcon.setSound(zzkm);
        }
        if (zzah != null) {
            smallIcon.setContentIntent(zzah);
        }
        if (pendingIntent != null) {
            smallIcon.setDeleteIntent(pendingIntent);
        }
        return smallIcon.build();
    }

    private PendingIntent zzah(Bundle bundle) {
        Intent intent;
        Object zzf = zzf(bundle, "gcm.n.click_action");
        Intent launchIntentForPackage;
        if (TextUtils.isEmpty(zzf)) {
            launchIntentForPackage = this.mContext.getPackageManager().getLaunchIntentForPackage(this.mContext.getPackageName());
            if (launchIntentForPackage == null) {
                Log.w("FirebaseMessaging", "No activity found to launch app");
                return null;
            }
            intent = launchIntentForPackage;
        } else {
            launchIntentForPackage = new Intent(zzf);
            launchIntentForPackage.setPackage(this.mContext.getPackageName());
            launchIntentForPackage.setFlags(268435456);
            intent = launchIntentForPackage;
        }
        Bundle bundle2 = new Bundle(bundle);
        FirebaseMessagingService.zzac(bundle2);
        intent.putExtras(bundle2);
        for (String str : bundle2.keySet()) {
            if (str.startsWith("gcm.n.") || str.startsWith("gcm.notification.")) {
                intent.removeExtra(str);
            }
        }
        return PendingIntent.getActivity(this.mContext, zzbnz(), intent, 1073741824);
    }

    static String zzat(Bundle bundle) {
        Object zzf = zzf(bundle, "gcm.n.sound2");
        return TextUtils.isEmpty(zzf) ? zzf(bundle, "gcm.n.sound") : zzf;
    }

    private PendingIntent zzau(Bundle bundle) {
        Intent intent = new Intent("com.google.firebase.messaging.NOTIFICATION_DISMISS");
        zza(intent, bundle);
        return PendingIntent.getBroadcast(this.mContext, zzbnz(), FirebaseInstanceIdInternalReceiver.zzh(this.mContext, intent), 1073741824);
    }

    private int zzbnz() {
        return this.bhI.incrementAndGet();
    }

    static boolean zzdd(Context context) {
        if (((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            return false;
        }
        int myPid = Process.myPid();
        List<RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.pid == myPid) {
                return runningAppProcessInfo.importance == 100;
            }
        }
        return false;
    }

    static synchronized zza zzex(Context context) {
        zza com_google_firebase_messaging_zza;
        synchronized (zza.class) {
            if (bhH == null) {
                bhH = new zza(context);
            }
            com_google_firebase_messaging_zza = bhH;
        }
        return com_google_firebase_messaging_zza;
    }

    static String zzf(Bundle bundle, String str) {
        String string = bundle.getString(str);
        return string == null ? bundle.getString(str.replace("gcm.n.", "gcm.notification.")) : string;
    }

    private String zzg(Bundle bundle, String str) {
        Object zzf = zzf(bundle, str);
        if (!TextUtils.isEmpty(zzf)) {
            return zzf;
        }
        String zzh = zzh(bundle, str);
        if (TextUtils.isEmpty(zzh)) {
            return null;
        }
        Resources resources = this.mContext.getResources();
        int identifier = resources.getIdentifier(zzh, "string", this.mContext.getPackageName());
        if (identifier == 0) {
            String str2 = "FirebaseMessaging";
            String valueOf = String.valueOf(str);
            String valueOf2 = String.valueOf("_loc_key");
            valueOf2 = String.valueOf(zzkk(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf)));
            Log.w(str2, new StringBuilder((String.valueOf(valueOf2).length() + 49) + String.valueOf(zzh).length()).append(valueOf2).append(" resource not found: ").append(zzh).append(" Default value will be used.").toString());
            return null;
        }
        Object[] zzi = zzi(bundle, str);
        if (zzi == null) {
            return resources.getString(identifier);
        }
        try {
            return resources.getString(identifier, zzi);
        } catch (Throwable e) {
            valueOf = String.valueOf(Arrays.toString(zzi));
            Log.w("FirebaseMessaging", new StringBuilder((String.valueOf(zzh).length() + 58) + String.valueOf(valueOf).length()).append("Missing format argument for ").append(zzh).append(": ").append(valueOf).append(" Default value will be used.").toString(), e);
            return null;
        }
    }

    static String zzh(Bundle bundle, String str) {
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("_loc_key");
        return zzf(bundle, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
    }

    static Object[] zzi(Bundle bundle, String str) {
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("_loc_args");
        String zzf = zzf(bundle, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        if (TextUtils.isEmpty(zzf)) {
            return null;
        }
        try {
            JSONArray jSONArray = new JSONArray(zzf);
            String[] strArr = new String[jSONArray.length()];
            for (int i = 0; i < strArr.length; i++) {
                strArr[i] = jSONArray.opt(i);
            }
            return strArr;
        } catch (JSONException e) {
            valueOf = "FirebaseMessaging";
            String valueOf3 = String.valueOf(str);
            valueOf2 = String.valueOf("_loc_args");
            valueOf2 = String.valueOf(zzkk(valueOf2.length() != 0 ? valueOf3.concat(valueOf2) : new String(valueOf3)));
            Log.w(valueOf, new StringBuilder((String.valueOf(valueOf2).length() + 41) + String.valueOf(zzf).length()).append("Malformed ").append(valueOf2).append(": ").append(zzf).append("  Default value will be used.").toString());
            return null;
        }
    }

    private static String zzkk(String str) {
        return str.substring("gcm.n.".length());
    }

    private int zzkl(String str) {
        int identifier;
        if (!TextUtils.isEmpty(str)) {
            Resources resources = this.mContext.getResources();
            identifier = resources.getIdentifier(str, "drawable", this.mContext.getPackageName());
            if (identifier != 0) {
                return identifier;
            }
            identifier = resources.getIdentifier(str, "mipmap", this.mContext.getPackageName());
            if (identifier != 0) {
                return identifier;
            }
            Log.w("FirebaseMessaging", new StringBuilder(String.valueOf(str).length() + 57).append("Icon resource ").append(str).append(" not found. Notification will use app icon.").toString());
        }
        identifier = this.mContext.getApplicationInfo().icon;
        return identifier == 0 ? 17301651 : identifier;
    }

    private Uri zzkm(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if ("default".equals(str) || this.mContext.getResources().getIdentifier(str, "raw", this.mContext.getPackageName()) == 0) {
            return RingtoneManager.getDefaultUri(2);
        }
        String valueOf = String.valueOf("android.resource://");
        String valueOf2 = String.valueOf(this.mContext.getPackageName());
        return Uri.parse(new StringBuilder(((String.valueOf(valueOf).length() + 5) + String.valueOf(valueOf2).length()) + String.valueOf(str).length()).append(valueOf).append(valueOf2).append("/raw/").append(str).toString());
    }

    void zzas(Bundle bundle) {
        zza(zzf(bundle, "gcm.n.tag"), zzag(bundle));
    }
}
