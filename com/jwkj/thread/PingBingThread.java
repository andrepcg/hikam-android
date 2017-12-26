package com.jwkj.thread;

import android.content.Context;
import android.content.Intent;
import com.jwkj.global.Constants.Action;
import com.jwkj.utils.Utils;
import java.io.IOException;

public class PingBingThread {
    private static boolean isOpenThread;
    static PingBingThread manager;
    Context context;
    boolean isRun;
    private Main main;

    class Main extends Thread {
        Main() {
        }

        public void run() {
            PingBingThread.this.isRun = true;
            Utils.sleepThread(30000);
            while (PingBingThread.this.isRun) {
                if (PingBingThread.isOpenThread) {
                    if (PingBingThread.pingBing()) {
                        Intent pingSuccess = new Intent();
                        pingSuccess.setAction(Action.ACTION_NETWORK_PING_SUCCESS);
                        PingBingThread.this.context.sendBroadcast(pingSuccess);
                    } else {
                        Intent pingFailed = new Intent();
                        pingFailed.setAction(Action.ACTION_NETWORK_PING_FAILED);
                        PingBingThread.this.context.sendBroadcast(pingFailed);
                    }
                    Utils.sleepThread(10000);
                } else {
                    Utils.sleepThread(10000);
                }
            }
        }
    }

    public PingBingThread(Context context) {
        manager = this;
        this.context = context;
    }

    public static PingBingThread getInstance() {
        return manager;
    }

    private static boolean pingBing() {
        try {
            if (Runtime.getRuntime().exec("ping -c 10 -w 100 " + "www.bing.com").waitFor() == 0) {
                return true;
            }
        } catch (IOException e) {
        } catch (InterruptedException e2) {
        }
        return false;
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
        isOpenThread = isOpenThread;
    }
}
