package com.firebase.jobdispatcher;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RetryStrategy {
    public static final RetryStrategy DEFAULT_EXPONENTIAL = new RetryStrategy(1, 30, 3600);
    public static final RetryStrategy DEFAULT_LINEAR = new RetryStrategy(2, 30, 3600);
    public static final int RETRY_POLICY_EXPONENTIAL = 1;
    public static final int RETRY_POLICY_LINEAR = 2;
    private final int mInitialBackoff;
    private final int mMaximumBackoff;
    private final int mPolicy;

    static final class Builder {
        private final ValidationEnforcer mValidator;

        Builder(ValidationEnforcer validator) {
            this.mValidator = validator;
        }

        public RetryStrategy build(int policy, int initialBackoff, int maxBackoff) {
            RetryStrategy rs = new RetryStrategy(policy, initialBackoff, maxBackoff);
            this.mValidator.ensureValid(rs);
            return rs;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface RetryPolicy {
    }

    RetryStrategy(int policy, int initialBackoff, int maximumBackoff) {
        this.mPolicy = policy;
        this.mInitialBackoff = initialBackoff;
        this.mMaximumBackoff = maximumBackoff;
    }

    public int getPolicy() {
        return this.mPolicy;
    }

    public int getInitialBackoff() {
        return this.mInitialBackoff;
    }

    public int getMaximumBackoff() {
        return this.mMaximumBackoff;
    }
}
