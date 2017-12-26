package com.firebase.jobdispatcher;

import android.os.AsyncTask;
import android.support.annotation.CallSuper;
import android.support.v4.util.SimpleArrayMap;

public abstract class SimpleJobService extends JobService {
    private final SimpleArrayMap<JobParameters, AsyncJobTask> runningJobs = new SimpleArrayMap();

    private static class AsyncJobTask extends AsyncTask<Void, Void, Integer> {
        private final JobParameters jobParameters;
        private final SimpleJobService jobService;

        private AsyncJobTask(SimpleJobService jobService, JobParameters jobParameters) {
            this.jobService = jobService;
            this.jobParameters = jobParameters;
        }

        protected Integer doInBackground(Void... params) {
            return Integer.valueOf(this.jobService.onRunJob(this.jobParameters));
        }

        protected void onPostExecute(Integer integer) {
            boolean z = true;
            SimpleJobService simpleJobService = this.jobService;
            JobParameters jobParameters = this.jobParameters;
            if (integer.intValue() != 1) {
                z = false;
            }
            simpleJobService.onJobFinished(jobParameters, z);
        }
    }

    public abstract int onRunJob(JobParameters jobParameters);

    @CallSuper
    public boolean onStartJob(JobParameters job) {
        AsyncJobTask async = new AsyncJobTask(job);
        synchronized (this.runningJobs) {
            this.runningJobs.put(job, async);
        }
        async.execute(new Void[0]);
        return true;
    }

    @CallSuper
    public boolean onStopJob(JobParameters job) {
        synchronized (this.runningJobs) {
            AsyncJobTask async = (AsyncJobTask) this.runningJobs.remove(job);
            if (async != null) {
                async.cancel(true);
                return true;
            }
            return false;
        }
    }

    private void onJobFinished(JobParameters jobParameters, boolean b) {
        synchronized (this.runningJobs) {
            this.runningJobs.remove(jobParameters);
        }
        jobFinished(jobParameters, b);
    }
}
