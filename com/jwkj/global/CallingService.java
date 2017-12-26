package com.jwkj.global;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.jwkj.CallActivity;
import com.jwkj.data.ContactDB;

public class CallingService extends IntentService {
    public static final String MONITOR_RECALL_ACTION = "com.jwkj.global.CallingService.monitor";
    public static final String PLAYBACK_RECALL_ACTION = "com.jwkj.global.CallingService.playback";

    public CallingService() {
        super("CallingService");
    }

    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (MONITOR_RECALL_ACTION.equals(action)) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isOutCall = intent.getBooleanExtra("isOutCall", true);
            String callModel = intent.getStringExtra("callModel");
            String isAlarmTrigger = intent.getStringExtra("isAlarmTrigger");
            String callId = intent.getStringExtra("callId");
            String contactName = intent.getStringExtra(ContactDB.COLUMN_CONTACT_NAME);
            String ipFlag = intent.getStringExtra("ipFlag");
            int type = intent.getIntExtra("type", -1);
            String password = intent.getStringExtra("password");
            Intent intent1 = new Intent(getApplicationContext(), CallActivity.class);
            intent1.addFlags(270532608);
            intent1.putExtra("callModel", callModel);
            intent1.putExtra("callId", callId);
            intent1.putExtra(ContactDB.COLUMN_CONTACT_NAME, contactName);
            intent1.putExtra("password", password);
            intent1.putExtra("isOutCall", isOutCall);
            intent1.putExtra("type", type);
            startActivity(intent1);
            Log.e("oaosj", "recall: " + callModel + " " + callId + " " + contactName + " " + password + " " + isOutCall + " " + type);
        } else if (!PLAYBACK_RECALL_ACTION.equals(action)) {
        }
    }
}
