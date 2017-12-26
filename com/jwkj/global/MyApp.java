package com.jwkj.global;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import com.hikam.C0291R;
import com.jwkj.activity.AlarmRecordActivity;
import com.jwkj.activity.ForwardActivity;
import com.jwkj.activity.ForwardDownActivity;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemDataManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import java.util.HashMap;
import org.apache.http.cookie.ClientCookie;

public class MyApp extends Application {
    public static final String MAIN_SERVICE_START = "com.hikam.service.MAINSERVICE";
    public static final int NOTIFICATION_DOWN_ID = 8;
    public static int PUSH_METHOD = 2;
    public static final int PUSH_METHOD_NOTIFICATION = 2;
    public static final int PUSH_METHOD_POPUP = 1;
    public static MyApp app;
    public static int index = 10;
    public static int indexMax = 12;
    public static boolean isActive;
    private RemoteViews cur_down_view;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        return ((MyApp) context.getApplicationContext()).refWatcher;
    }

    public void onCreate() {
        System.loadLibrary("Bugly");
        CrashReport.initCrashReport(getApplicationContext(), "ee3fcb987f", true);
        app = this;
        super.onCreate();
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            this.refWatcher = RefWatcher.DISABLED;
            isActive = true;
        }
    }

    public NotificationManager getNotificationManager() {
        if (this.mNotificationManager == null) {
            this.mNotificationManager = (NotificationManager) getSystemService("notification");
        }
        return this.mNotificationManager;
    }

    public void showNotification() {
        if (SharedPreferencesManager.getInstance().getIsShowNotify(this)) {
            RemoteViews contentView = new RemoteViews(getPackageName(), C0291R.layout.notify_status_bar);
            contentView.setImageViewResource(C0291R.id.icon, C0291R.drawable.ic_launcher);
            contentView.setTextViewText(C0291R.id.title, " " + getResources().getString(C0291R.string.app_name) + " " + getResources().getString(C0291R.string.running_in_the_background));
            Intent intent = new Intent(this, ForwardActivity.class);
            intent.addFlags(268435456);
            Notification notification = new Builder(this).setSmallIcon(C0291R.drawable.ic_launcher).setContent(contentView).setAutoCancel(true).setTicker(getResources().getString(C0291R.string.app_name)).setDefaults(4).setContentIntent(PendingIntent.getActivity(this, 0, intent, 134217728)).build();
            notification.flags = 16;
            ((NotificationManager) getSystemService("notification")).notify(C0291R.string.app_name, notification);
        }
    }

    public void hideNotification() {
        this.mNotificationManager = getNotificationManager();
        this.mNotificationManager.cancel(C0291R.string.app_name);
    }

    public void showDownNotification(int state, int value) {
        if (SharedPreferencesManager.getInstance().getIsShowNotify(this)) {
            this.mNotificationManager = getNotificationManager();
            this.mNotification = new Notification();
            this.mNotification = new Notification(C0291R.drawable.ic_launcher, getResources().getString(C0291R.string.app_name), System.currentTimeMillis());
            this.mNotification.flags = 16;
            RemoteViews contentView = new RemoteViews(getPackageName(), C0291R.layout.notify_down_bar);
            this.cur_down_view = contentView;
            contentView.setImageViewResource(C0291R.id.icon, C0291R.drawable.ic_launcher);
            Intent intent = new Intent(this, ForwardDownActivity.class);
            intent.addFlags(268435456);
            switch (state) {
                case 17:
                    this.cur_down_view.setTextViewText(C0291R.id.down_complete_text, getResources().getString(C0291R.string.down_londing_click));
                    this.cur_down_view.setTextViewText(C0291R.id.progress_value, value + "%");
                    contentView.setProgressBar(C0291R.id.progress_bar, 100, value, false);
                    intent.putExtra("state", 17);
                    break;
                case 18:
                    this.cur_down_view.setTextViewText(C0291R.id.down_complete_text, getResources().getString(C0291R.string.down_complete_click));
                    this.cur_down_view.setTextViewText(C0291R.id.progress_value, "100%");
                    contentView.setProgressBar(C0291R.id.progress_bar, 100, 100, false);
                    intent.putExtra("state", 18);
                    break;
                case 19:
                    this.cur_down_view.setTextViewText(C0291R.id.down_complete_text, getResources().getString(C0291R.string.down_fault_click));
                    this.cur_down_view.setTextViewText(C0291R.id.progress_value, value + "%");
                    contentView.setProgressBar(C0291R.id.progress_bar, 100, value, false);
                    intent.putExtra("state", 19);
                    break;
            }
            this.mNotification.contentView = contentView;
            this.mNotification.contentIntent = PendingIntent.getActivity(this, 0, intent, 134217728);
            this.mNotificationManager.notify(8, this.mNotification);
        }
    }

    public void hideDownNotification() {
        this.mNotificationManager = getNotificationManager();
        this.mNotificationManager.cancel(8);
    }

    public void showAlarmNotification(String title, String content, String[] data) {
        Intent resultIntent = new Intent(this, AlarmRecordActivity.class);
        resultIntent.setFlags(268435456);
        Builder builder = new Builder(this).setSmallIcon(C0291R.drawable.ic_launcher).setContentText(content).setAutoCancel(true).setTicker(content).setDefaults(4).setContentIntent(PendingIntent.getActivity(this, index, resultIntent, 134217728));
        if (SharedPreferencesManager.getInstance().getAVibrateState(this) == 1) {
            builder.setDefaults(2);
            builder.setVibrate(new long[]{0, 300, 0, 0});
        }
        if (SharedPreferencesManager.getInstance().getAMuteState(this) == 1) {
            HashMap<String, String> datas;
            if (SharedPreferencesManager.getInstance().getABellType(app) == 0) {
                datas = SystemDataManager.getInstance().findSystemBellById(app, SharedPreferencesManager.getInstance().getASystemBellId(app));
            } else {
                datas = SystemDataManager.getInstance().findSdBellById(app, SharedPreferencesManager.getInstance().getASdBellId(app));
            }
            if (datas != null) {
                String path = (String) datas.get(ClientCookie.PATH_ATTR);
                if (path != null) {
                    builder.setSound(Uri.parse(path));
                } else {
                    builder.setDefaults(1);
                }
            } else {
                builder.setDefaults(1);
            }
        }
        Notification notification = builder.build();
        notification.flags = 16;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService("notification");
        if (index - 2 > 10) {
            for (int i = 10; i < index - 2; i++) {
                mNotifyMgr.cancel(i);
            }
        }
        mNotifyMgr.notify(index, notification);
        index++;
    }
}
