package com.firebase.jobdispatcher;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

public final class GooglePlayReceiver extends ExternalReceiver {
    @VisibleForTesting
    static final String ACTION_EXECUTE = "com.google.android.gms.gcm.ACTION_TASK_READY";
    @VisibleForTesting
    static final String ACTION_INITIALIZE = "com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE";
    private static final String ERROR_NO_DATA = "No data provided, terminating";
    private static final String ERROR_NULL_INTENT = "Null Intent passed, terminating";
    private static final String ERROR_UNKNOWN_ACTION = "Unknown action received, terminating";
    static final String TAG = "FJD.GooglePlayReceiver";
    private GooglePlayCallbackExtractor callbackExtractor = new GooglePlayCallbackExtractor();
    private SimpleArrayMap<String, SimpleArrayMap<String, JobCallback>> callbacks = new SimpleArrayMap(1);
    private final JobCoder prefixedCoder = new JobCoder("com.firebase.jobdispatcher.", true);

    private static void sendResultSafely(JobCallback callback, int result) {
        try {
            callback.jobFinished(result);
        } catch (Throwable e) {
            Log.e(TAG, "Encountered error running callback", e.getCause());
        }
    }

    public final int onStartCommand(Intent intent, int flags, int startId) {
        try {
            super.onStartCommand(intent, flags, startId);
            if (intent == null) {
                Log.w(TAG, ERROR_NULL_INTENT);
                synchronized (this) {
                    if (this.callbacks.isEmpty()) {
                        stopSelf(startId);
                    }
                }
            } else {
                String action = intent.getAction();
                if (ACTION_EXECUTE.equals(action)) {
                    executeJob(prepareJob(intent));
                    synchronized (this) {
                        if (this.callbacks.isEmpty()) {
                            stopSelf(startId);
                        }
                    }
                } else if (ACTION_INITIALIZE.equals(action)) {
                    synchronized (this) {
                        if (this.callbacks.isEmpty()) {
                            stopSelf(startId);
                        }
                    }
                } else {
                    Log.e(TAG, ERROR_UNKNOWN_ACTION);
                    synchronized (this) {
                        if (this.callbacks.isEmpty()) {
                            stopSelf(startId);
                        }
                    }
                }
            }
            return 2;
        } catch (Throwable th) {
            synchronized (this) {
                if (this.callbacks.isEmpty()) {
                    stopSelf(startId);
                }
            }
        }
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nullable
    private JobParameters prepareJob(Intent intent) {
        Bundle data = intent.getExtras();
        if (data == null) {
            Log.e(TAG, ERROR_NO_DATA);
            return null;
        }
        JobCallback callback = this.callbackExtractor.extractCallback(data);
        if (callback == null) {
            Log.i(TAG, "no callback found");
            return null;
        }
        Bundle extras = data.getBundle("extras");
        if (extras == null) {
            Log.i(TAG, "no 'extras' bundle found");
            sendResultSafely(callback, 2);
            return null;
        }
        JobParameters job = this.prefixedCoder.decode(extras);
        if (job == null) {
            Log.i(TAG, "unable to decode job from extras");
            sendResultSafely(callback, 2);
            return null;
        }
        job.getExtras().putAll(extras);
        synchronized (this) {
            SimpleArrayMap<String, JobCallback> map = (SimpleArrayMap) this.callbacks.get(job.getService());
            if (map == null) {
                map = new SimpleArrayMap(1);
                this.callbacks.put(job.getService(), map);
            }
            map.put(job.getTag(), callback);
        }
        return job;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onJobFinished(@android.support.annotation.NonNull com.firebase.jobdispatcher.JobParameters r6, int r7) {
        /*
        r5 = this;
        monitor-enter(r5);
        r2 = r5.callbacks;	 Catch:{ all -> 0x0057 }
        r3 = r6.getService();	 Catch:{ all -> 0x0057 }
        r1 = r2.get(r3);	 Catch:{ all -> 0x0057 }
        r1 = (android.support.v4.util.SimpleArrayMap) r1;	 Catch:{ all -> 0x0057 }
        if (r1 != 0) goto L_0x0011;
    L_0x000f:
        monitor-exit(r5);	 Catch:{ all -> 0x0057 }
    L_0x0010:
        return;
    L_0x0011:
        r2 = r6.getTag();	 Catch:{ all -> 0x0057 }
        r0 = r1.remove(r2);	 Catch:{ all -> 0x0057 }
        r0 = (com.firebase.jobdispatcher.JobCallback) r0;	 Catch:{ all -> 0x0057 }
        if (r0 == 0) goto L_0x0046;
    L_0x001d:
        r2 = "FJD.GooglePlayReceiver";
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0057 }
        r3.<init>();	 Catch:{ all -> 0x0057 }
        r4 = "sending jobFinished for ";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0057 }
        r4 = r6.getTag();	 Catch:{ all -> 0x0057 }
        r3 = r3.append(r4);	 Catch:{ all -> 0x0057 }
        r4 = " = ";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0057 }
        r3 = r3.append(r7);	 Catch:{ all -> 0x0057 }
        r3 = r3.toString();	 Catch:{ all -> 0x0057 }
        android.util.Log.i(r2, r3);	 Catch:{ all -> 0x0057 }
        sendResultSafely(r0, r7);	 Catch:{ all -> 0x0057 }
    L_0x0046:
        r2 = r1.isEmpty();	 Catch:{ all -> 0x0057 }
        if (r2 == 0) goto L_0x0055;
    L_0x004c:
        r2 = r5.callbacks;	 Catch:{ all -> 0x0057 }
        r3 = r6.getService();	 Catch:{ all -> 0x0057 }
        r2.remove(r3);	 Catch:{ all -> 0x0057 }
    L_0x0055:
        monitor-exit(r5);	 Catch:{ all -> 0x0057 }
        goto L_0x0010;
    L_0x0057:
        r2 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0057 }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.firebase.jobdispatcher.GooglePlayReceiver.onJobFinished(com.firebase.jobdispatcher.JobParameters, int):void");
    }
}
