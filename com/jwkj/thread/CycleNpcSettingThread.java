package com.jwkj.thread;

import com.p2p.core.P2PHandler;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CycleNpcSettingThread {
    private static final int processorsNum = ((Runtime.getRuntime().availableProcessors() * 3) + 2);
    private ScheduledExecutorService mScheduledThreadExecutor = Executors.newScheduledThreadPool(processorsNum);
    private TimerTask task;

    public CycleNpcSettingThread(final String contactModel, final String contactId, final String password) {
        this.task = new TimerTask() {
            public void run() {
                P2PHandler.getInstance().getUserNum(contactModel, contactId, password);
            }
        };
    }

    public void startGetNpcSettings() {
        this.mScheduledThreadExecutor.scheduleAtFixedRate(this.task, 0, 5, TimeUnit.SECONDS);
    }

    public void stopGetNpcSettings() {
        this.mScheduledThreadExecutor.shutdownNow();
        this.task = null;
        this.mScheduledThreadExecutor = null;
    }
}
