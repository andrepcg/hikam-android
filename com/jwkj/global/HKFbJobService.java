package com.jwkj.global;

import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class HKFbJobService extends JobService {
    private static final String TAG = "MyJobService";

    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        return false;
    }

    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
