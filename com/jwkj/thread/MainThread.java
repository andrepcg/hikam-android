package com.jwkj.thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants.Action;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.utils.Utils;
import com.p2p.core.update.UpdateManager;

public class MainThread {
    private static final long SYSTEM_MSG_INTERVAL = 3600000;
    private static boolean isOpenThread;
    static MainThread manager;
    Context context;
    boolean isRun;
    long lastSysmsgTime;
    private Main main;
    private int serVersion;
    private String version;

    class Main extends Thread {
        Main() {
        }

        public void run() {
            MainThread.this.isRun = true;
            Utils.sleepThread(3000);
            while (MainThread.this.isRun) {
                if (MainThread.isOpenThread) {
                    Log.e("my", "updateOnlineState");
                    try {
                        FList.getInstance().updateOnlineState();
                        FList.getInstance().searchLocalDevice();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Utils.sleepThread(10000);
                } else {
                    Utils.sleepThread(10000);
                }
            }
        }
    }

    public MainThread(Context context) {
        manager = this;
        this.context = context;
    }

    public static MainThread getInstance() {
        return manager;
    }

    public void go() {
        if (this.main == null || !this.main.isAlive()) {
            this.main = new Main();
            this.main.start();
        }
    }

    public void kill() {
        this.isRun = false;
        this.main = null;
    }

    public static void setOpenThread(boolean isOpenThread) {
        Log.e("setOpenThread", "" + isOpenThread);
        if (isOpenThread) {
            try {
                FList.getInstance().updateOnlineState();
                FList.getInstance().searchLocalDevice();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isOpenThread = isOpenThread;
    }

    public void checkUpdate() {
        try {
            long last_check_update_time = SharedPreferencesManager.getInstance().getLastAutoCheckUpdateTime(MyApp.app);
            long now_time = System.currentTimeMillis();
            if (now_time - last_check_update_time > 43200000) {
                SharedPreferencesManager.getInstance().putLastAutoCheckUpdateTime(now_time, MyApp.app);
                Log.e("my", "后台检查更新");
                if (UpdateManager.getInstance().checkUpdate()) {
                    String data = "";
                    if (Utils.isZh(MyApp.app)) {
                        data = UpdateManager.getInstance().getUpdateDescription();
                    } else {
                        data = UpdateManager.getInstance().getUpdateDescription_en();
                    }
                    Intent i = new Intent(Action.ACTION_UPDATE);
                    i.putExtra("updateDescription", data);
                    MyApp.app.sendBroadcast(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("my", "后台检查更新失败");
        }
    }
}
