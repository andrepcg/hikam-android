package com.jwkj.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.Account;

public class AutoStartReceiver extends BroadcastReceiver {
    private static final String action_boot = "android.intent.action.BOOT_COMPLETED";
    public static boolean isMainActivityStart;
    Context mContext;

    public void onReceive(Context context, Intent intent) {
        Log.e("my", "************************************************");
        this.mContext = context;
        if (intent.getAction().equals(action_boot)) {
            Account activeUser = AccountPersist.getInstance().getActiveAccountInfo(context);
            if (!(activeUser == null || activeUser.three_number == null || "".equals(activeUser.three_number) || !SharedPreferencesManager.getInstance().getIsAutoStart(context, activeUser.three_number))) {
                Log.e("my", "execute");
                isMainActivityStart = false;
                NpcCommon.mThreeNum = activeUser.three_number;
                Intent service = new Intent(MyApp.MAIN_SERVICE_START);
                service.setPackage(context.getPackageName());
                context.startService(service);
                MyApp.app.showNotification();
            }
        }
        Log.e("my", "************************************************");
    }
}
