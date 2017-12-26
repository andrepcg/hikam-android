package com.firebase.jobdispatcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

final class JobInvocation implements JobParameters {
    @NonNull
    private final int[] mConstraints;
    @NonNull
    private final Bundle mExtras;
    private final int mLifetime;
    private final boolean mRecurring;
    private final boolean mReplaceCurrent;
    private final RetryStrategy mRetryStrategy;
    @NonNull
    private final String mService;
    @NonNull
    private final String mTag;
    @NonNull
    private final JobTrigger mTrigger;

    JobInvocation(@NonNull String tag, @NonNull String service, @NonNull JobTrigger trigger, @NonNull RetryStrategy retryStrategy, boolean recurring, int lifetime, @NonNull int[] constraints, @Nullable Bundle extras, boolean replaceCurrent) {
        this.mTag = tag;
        this.mService = service;
        this.mTrigger = trigger;
        this.mRetryStrategy = retryStrategy;
        this.mRecurring = recurring;
        this.mLifetime = lifetime;
        this.mConstraints = constraints;
        if (extras == null) {
            extras = new Bundle();
        }
        this.mExtras = extras;
        this.mReplaceCurrent = replaceCurrent;
    }

    @NonNull
    public String getService() {
        return this.mService;
    }

    @NonNull
    public String getTag() {
        return this.mTag;
    }

    @NonNull
    public JobTrigger getTrigger() {
        return this.mTrigger;
    }

    public int getLifetime() {
        return this.mLifetime;
    }

    public boolean isRecurring() {
        return this.mRecurring;
    }

    @NonNull
    public int[] getConstraints() {
        return this.mConstraints;
    }

    @NonNull
    public Bundle getExtras() {
        return this.mExtras;
    }

    @NonNull
    public RetryStrategy getRetryStrategy() {
        return this.mRetryStrategy;
    }

    public boolean shouldReplaceCurrent() {
        return this.mReplaceCurrent;
    }
}
