package com.firebase.jobdispatcher;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

abstract class ExternalReceiver extends Service {
    @VisibleForTesting
    static final int JOB_FINISHED = 1;
    private static final String TAG = "FJD.ExternalReceiver";
    private ResponseHandler responseHandler = new ResponseHandler();
    private final SimpleArrayMap<String, JobServiceConnection> serviceConnections = new SimpleArrayMap();

    private static class JobServiceConnection implements ServiceConnection {
        private static final int NOT_STARTED = 1;
        private static final int RUNNING = 2;
        private LocalBinder binder;
        private boolean isBound;
        private final SimpleArrayMap<JobParameters, Integer> jobSpecs;
        private final Message message;

        private JobServiceConnection(JobParameters jobParameters, Message message) {
            this.jobSpecs = new SimpleArrayMap(1);
            this.isBound = false;
            this.message = message;
            this.jobSpecs.put(jobParameters, Integer.valueOf(1));
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof LocalBinder) {
                this.isBound = true;
                this.binder = (LocalBinder) service;
                JobService jobService = this.binder.getService();
                synchronized (this.jobSpecs) {
                    for (int i = 0; i < this.jobSpecs.size(); i++) {
                        JobParameters job = (JobParameters) this.jobSpecs.keyAt(i);
                        if (((Integer) this.jobSpecs.get(job)).intValue() == 1) {
                            Message copiedMessage = Message.obtain(this.message);
                            copiedMessage.obj = job;
                            jobService.start(job, copiedMessage);
                        }
                    }
                }
                return;
            }
            Log.w(ExternalReceiver.TAG, "Unknown service connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            this.binder = null;
            this.isBound = false;
        }

        public void onJobFinished(JobParameters jobParameters) {
            synchronized (this.jobSpecs) {
                this.jobSpecs.remove(jobParameters);
            }
        }

        public boolean shouldDie() {
            boolean isEmpty;
            synchronized (this.jobSpecs) {
                isEmpty = this.jobSpecs.isEmpty();
            }
            return isEmpty;
        }

        public boolean isBound() {
            return this.isBound;
        }
    }

    private static class ResponseHandler extends Handler {
        private final ExternalReceiver receiver;

        private ResponseHandler(ExternalReceiver receiver) {
            this.receiver = receiver;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (msg.obj instanceof JobParameters) {
                        this.receiver.onJobFinishedMessage((JobParameters) msg.obj, msg.arg1);
                        return;
                    } else {
                        Log.wtf(ExternalReceiver.TAG, "handleMessage: unknown obj returned");
                        return;
                    }
                default:
                    Log.wtf(ExternalReceiver.TAG, "handleMessage: unknown message type received: " + msg.what);
                    return;
            }
        }
    }

    protected abstract void onJobFinished(@NonNull JobParameters jobParameters, int i);

    ExternalReceiver() {
    }

    private void onJobFinishedMessage(JobParameters jobParameters, int result) {
        JobServiceConnection connection;
        synchronized (this.serviceConnections) {
            connection = (JobServiceConnection) this.serviceConnections.get(jobParameters.getService());
        }
        connection.onJobFinished(jobParameters);
        if (connection.shouldDie() && connection.isBound()) {
            try {
                unbindService(connection);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Error unbinding service: " + e.getMessage());
            }
            synchronized (this.serviceConnections) {
                this.serviceConnections.remove(connection);
            }
        }
        onJobFinished(jobParameters, result);
    }

    protected final boolean executeJob(JobParameters jobParameters) {
        if (jobParameters == null) {
            return false;
        }
        JobServiceConnection conn = new JobServiceConnection(jobParameters, this.responseHandler.obtainMessage(1));
        this.serviceConnections.put(jobParameters.getService(), conn);
        bindService(createBindIntent(jobParameters), conn, 1);
        return true;
    }

    @NonNull
    private Intent createBindIntent(JobParameters jobParameters) {
        Intent execReq = new Intent("com.firebase.jobdispatcher.ACTION_EXECUTE");
        execReq.setClassName(this, jobParameters.getService());
        return execReq;
    }
}
