package com.firebase.jobdispatcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Job implements JobParameters {
    private final int[] mConstraints;
    private Bundle mExtras;
    private final int mLifetime;
    private final boolean mRecurring;
    private final boolean mReplaceCurrent;
    private final RetryStrategy mRetryStrategy;
    private final String mService;
    private final String mTag;
    private final JobTrigger mTrigger;

    public static final class Builder implements JobParameters {
        private int[] mConstraints;
        private Bundle mExtras;
        private int mLifetime = 1;
        private boolean mRecurring = false;
        private boolean mReplaceCurrent = false;
        private RetryStrategy mRetryStrategy = RetryStrategy.DEFAULT_EXPONENTIAL;
        private Class<? extends JobService> mServiceClass;
        private String mTag;
        private JobTrigger mTrigger = Trigger.NOW;
        private final ValidationEnforcer mValidator;

        Builder(ValidationEnforcer validator) {
            this.mValidator = validator;
        }

        public Builder addConstraint(int constraint) {
            int[] newConstraints = new int[(this.mConstraints == null ? 1 : this.mConstraints.length + 1)];
            if (!(this.mConstraints == null || this.mConstraints.length == 0)) {
                System.arraycopy(this.mConstraints, 0, newConstraints, 0, this.mConstraints.length);
            }
            newConstraints[newConstraints.length - 1] = constraint;
            this.mConstraints = newConstraints;
            return this;
        }

        public Builder setReplaceCurrent(boolean replaceCurrent) {
            this.mReplaceCurrent = replaceCurrent;
            return this;
        }

        public Job build() {
            this.mValidator.ensureValid((JobParameters) this);
            return new Job();
        }

        @NonNull
        public String getService() {
            return this.mServiceClass.getName();
        }

        public Builder setService(Class<? extends JobService> serviceClass) {
            this.mServiceClass = serviceClass;
            return this;
        }

        @NonNull
        public String getTag() {
            return this.mTag;
        }

        public Builder setTag(String tag) {
            this.mTag = tag;
            return this;
        }

        @NonNull
        public JobTrigger getTrigger() {
            return this.mTrigger;
        }

        public Builder setTrigger(JobTrigger trigger) {
            this.mTrigger = trigger;
            return this;
        }

        public int getLifetime() {
            return this.mLifetime;
        }

        public Builder setLifetime(int lifetime) {
            this.mLifetime = lifetime;
            return this;
        }

        public boolean isRecurring() {
            return this.mRecurring;
        }

        public Builder setRecurring(boolean recurring) {
            this.mRecurring = recurring;
            return this;
        }

        public int[] getConstraints() {
            return this.mConstraints == null ? new int[0] : this.mConstraints;
        }

        public Builder setConstraints(int... constraints) {
            this.mConstraints = constraints;
            return this;
        }

        @Nullable
        public Bundle getExtras() {
            return this.mExtras;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        @NonNull
        public RetryStrategy getRetryStrategy() {
            return this.mRetryStrategy;
        }

        public Builder setRetryStrategy(RetryStrategy retryStrategy) {
            this.mRetryStrategy = retryStrategy;
            return this;
        }

        public boolean shouldReplaceCurrent() {
            return this.mReplaceCurrent;
        }
    }

    private Job(Builder builder) {
        this.mService = builder.mServiceClass != null ? builder.mServiceClass.getName() : null;
        this.mExtras = builder.mExtras;
        this.mTag = builder.mTag;
        this.mTrigger = builder.mTrigger;
        this.mRetryStrategy = builder.mRetryStrategy;
        this.mLifetime = builder.mLifetime;
        this.mRecurring = builder.mRecurring;
        this.mConstraints = builder.mConstraints != null ? builder.mConstraints : new int[0];
        this.mReplaceCurrent = builder.mReplaceCurrent;
    }

    @NonNull
    public int[] getConstraints() {
        return this.mConstraints;
    }

    @Nullable
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
    public String getService() {
        return this.mService;
    }
}
