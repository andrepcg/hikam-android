package com.p2p.shake;

import android.os.Handler;

public class ShakeManager {
    public static final int HANDLE_ID_RECEIVE_DEVICE_INFO = 18;
    public static final int HANDLE_ID_SEARCH_END = 17;
    private static ShakeManager manager = null;
    public Handler handler;
    private long searchTime = 10000;
    private ShakeThread shakeThread;

    private ShakeManager() {
    }

    public static synchronized ShakeManager getInstance() {
        ShakeManager shakeManager;
        synchronized (ShakeManager.class) {
            if (manager == null) {
                synchronized (ShakeManager.class) {
                    manager = new ShakeManager();
                }
            }
            shakeManager = manager;
        }
        return shakeManager;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setSearchTime(long time) {
        this.searchTime = time;
    }

    public boolean shaking() {
        if (this.shakeThread != null) {
            return false;
        }
        this.shakeThread = new ShakeThread(this.handler);
        this.shakeThread.setHandler(this.handler);
        this.shakeThread.setSearchTime(this.searchTime);
        this.shakeThread.start();
        return true;
    }

    public void stopShaking() {
        if (this.shakeThread != null) {
            this.shakeThread.killThread();
            this.shakeThread = null;
        }
    }

    public boolean isShaking() {
        if (this.shakeThread != null) {
            return true;
        }
        return false;
    }
}
