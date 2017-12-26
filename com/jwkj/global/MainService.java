package com.jwkj.global;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.jwkj.P2PConnect;
import com.jwkj.entity.Account;
import com.jwkj.thread.CoverThread;
import com.jwkj.thread.MainThread;
import com.jwkj.thread.PingBingThread;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;

public class MainService extends Service {
    Context context;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.context = this;
        startForeground(1, new Notification());
    }

    public void onStart(Intent intent, int startId) {
        Account account = AccountPersist.getInstance().getActiveAccountInfo(this);
        if (account != null) {
            try {
                if (!"000000".equals(account.three_number2)) {
                    int codeStr1 = (int) Long.parseLong(account.rCode1);
                    int codeStr2 = (int) Long.parseLong(account.rCode2);
                    MediaPlayer.getInstance().native_p2p_disconnect();
                    P2PHandler.getInstance().p2pConnect(account.three_number2, codeStr1, codeStr2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        P2PConnect p2PConnect = new P2PConnect(getApplicationContext());
        new PingBingThread(this.context).go();
        new MainThread(this.context).go();
        new CoverThread(this.context).go();
        super.onStart(intent, startId);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, 1, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("few", "service     destroy  !");
        if (CoverThread.getInstance() != null) {
            CoverThread.getInstance().kill();
        }
        if (MainThread.getInstance() != null) {
            MainThread.getInstance().kill();
        }
        if (PingBingThread.getInstance() != null) {
            PingBingThread.getInstance().kill();
        }
        P2PHandler.getInstance().p2pDisconnect();
        P2pJni.P2pClientSdkUnInit();
    }
}
