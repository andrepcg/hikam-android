package com.jwkj.global;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hikam.C0291R;
import com.jwkj.activity.AlarmRecordActivity;

public class HKFbMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification("you xiaoxi");
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            scheduleJob();
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
            return;
        }
        sendNotification(remoteMessage.getNotification().getBody());
    }

    private void scheduleJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        dispatcher.schedule(dispatcher.newJobBuilder().setService(HKFbJobService.class).setTag("my-job-tag").build());
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, AlarmRecordActivity.class);
        intent.addFlags(67108864);
        ((NotificationManager) getSystemService("notification")).notify(0, new Builder(this).setSmallIcon(C0291R.drawable.ic_launcher).setContentTitle("FCM Message").setContentText(messageBody).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(2)).setContentIntent(PendingIntent.getActivity(this, 0, intent, 1073741824)).build());
    }
}
