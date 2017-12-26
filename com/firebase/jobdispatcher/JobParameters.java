package com.firebase.jobdispatcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface JobParameters {
    int[] getConstraints();

    @Nullable
    Bundle getExtras();

    int getLifetime();

    @NonNull
    RetryStrategy getRetryStrategy();

    @NonNull
    String getService();

    @NonNull
    String getTag();

    @NonNull
    JobTrigger getTrigger();

    boolean isRecurring();

    boolean shouldReplaceCurrent();
}
