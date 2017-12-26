package com.jwkj.thread;

import android.content.Context;
import com.jwkj.global.FList;
import com.jwkj.utils.Utils;

public class CoverThread {
    private static boolean isOpenThread;
    static CoverThread manager;
    Context context;
    boolean isRun;
    private Main main;

    class Main extends Thread {
        Main() {
        }

        public void run() {
            CoverThread.this.isRun = true;
            Utils.sleepThread(30000);
            while (CoverThread.this.isRun) {
                if (CoverThread.isOpenThread) {
                    FList.getInstance().updataCameraCover();
                    Utils.sleepThread(900000);
                } else {
                    Utils.sleepThread(10000);
                }
            }
        }
    }

    public CoverThread(Context context) {
        manager = this;
        this.context = context;
    }

    public static CoverThread getInstance() {
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
        isOpenThread = isOpenThread;
    }
}
