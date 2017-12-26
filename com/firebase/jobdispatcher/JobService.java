package com.firebase.jobdispatcher;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class JobService extends Service {
    @VisibleForTesting
    static final String ACTION_EXECUTE = "com.firebase.jobdispatcher.ACTION_EXECUTE";
    public static final int RESULT_FAIL_NORETRY = 2;
    public static final int RESULT_FAIL_RETRY = 1;
    public static final int RESULT_SUCCESS = 0;
    static final String TAG = "FJD.JobService";
    private LocalBinder binder = new LocalBinder();
    private final SimpleArrayMap<String, JobCallback> runningJobs = new SimpleArrayMap(1);

    private static final class JobCallback {
        public final JobParameters jobParameters;
        public final Message message;

        private JobCallback(JobParameters jobParameters, Message message) {
            this.jobParameters = jobParameters;
            this.message = message;
        }

        void sendResult(int result) {
            this.message.arg1 = result;
            this.message.sendToTarget();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface JobResult {
    }

    class LocalBinder extends Binder {
        LocalBinder() {
        }

        JobService getService() {
            return JobService.this;
        }
    }

    public abstract boolean onStartJob(JobParameters jobParameters);

    public abstract boolean onStopJob(JobParameters jobParameters);

    final void start(JobParameters job, Message msg) {
        synchronized (this.runningJobs) {
            this.runningJobs.put(job.getTag(), new JobCallback(job, msg));
        }
        if (!onStartJob(job)) {
            synchronized (this.runningJobs) {
                ((JobCallback) this.runningJobs.remove(job.getTag())).sendResult(0);
            }
        }
    }

    public final void jobFinished(@NonNull JobParameters job, boolean needsReschedule) {
        if (job == null) {
            Log.e(TAG, "jobFinished called with a null JobParameters");
            return;
        }
        synchronized (this.runningJobs) {
            JobCallback jobCallback = (JobCallback) this.runningJobs.remove(job.getTag());
        }
        if (jobCallback != null) {
            jobCallback.sendResult(needsReschedule ? 1 : 0);
        }
    }

    public final int onStartCommand(Intent intent, int flags, int startId) {
        stopSelf(startId);
        return 2;
    }

    @Nullable
    public final IBinder onBind(Intent intent) {
        return this.binder;
    }

    public final boolean onUnbind(Intent intent) {
        synchronized (this.runningJobs) {
            for (int i = this.runningJobs.size() - 1; i >= 0; i--) {
                JobCallback message = (JobCallback) this.runningJobs.get(this.runningJobs.keyAt(i));
                if (message != null) {
                    message.sendResult(onStopJob(message.jobParameters) ? 1 : 2);
                }
            }
        }
        return super.onUnbind(intent);
    }

    public final void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public final void onStart(Intent intent, int startId) {
    }

    protected final void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
    }

    public final void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public final void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
