package com.jwkj.thread;

public class DelayThread extends Thread {
    int delayTime;
    OnRunListener onRunListener;

    public interface OnRunListener {
        void run();
    }

    public DelayThread(int delayTime, OnRunListener onRunListener) {
        this.delayTime = delayTime;
        this.onRunListener = onRunListener;
    }

    public void run() {
        try {
            Thread.sleep((long) this.delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.onRunListener.run();
    }
}
