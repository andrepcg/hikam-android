package com.firebase.jobdispatcher;

import android.support.annotation.NonNull;
import com.firebase.jobdispatcher.Job.Builder;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class FirebaseJobDispatcher {
    public static final int CANCEL_RESULT_NO_DRIVER_AVAILABLE = 2;
    public static final int CANCEL_RESULT_SUCCESS = 0;
    public static final int CANCEL_RESULT_UNKNOWN_ERROR = 1;
    public static final int SCHEDULE_RESULT_BAD_SERVICE = 4;
    public static final int SCHEDULE_RESULT_NO_DRIVER_AVAILABLE = 2;
    public static final int SCHEDULE_RESULT_SUCCESS = 0;
    public static final int SCHEDULE_RESULT_UNKNOWN_ERROR = 1;
    public static final int SCHEDULE_RESULT_UNSUPPORTED_TRIGGER = 3;
    private final Driver mDriver;
    private Builder mRetryStrategyBuilder = new Builder(this.mValidator);
    private final ValidationEnforcer mValidator = new ValidationEnforcer(this.mDriver.getValidator());

    @Retention(RetentionPolicy.SOURCE)
    public @interface CancelResult {
    }

    public static final class ScheduleFailedException extends RuntimeException {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ScheduleResult {
    }

    public FirebaseJobDispatcher(Driver driver) {
        this.mDriver = driver;
    }

    public int schedule(@NonNull Job job) {
        if (this.mDriver.isAvailable()) {
            return this.mDriver.schedule(job);
        }
        return 2;
    }

    public int cancel(@NonNull String tag) {
        if (this.mDriver.isAvailable()) {
            return this.mDriver.cancel(tag);
        }
        return 2;
    }

    public int cancelAll() {
        if (this.mDriver.isAvailable()) {
            return this.mDriver.cancelAll();
        }
        return 2;
    }

    public void mustSchedule(Job job) {
        if (schedule(job) != 0) {
            throw new ScheduleFailedException();
        }
    }

    public ValidationEnforcer getValidator() {
        return this.mValidator;
    }

    @NonNull
    public Builder newJobBuilder() {
        return new Builder(this.mValidator);
    }

    public RetryStrategy newRetryStrategy(int policy, int initialBackoff, int maximumBackoff) {
        return this.mRetryStrategyBuilder.build(policy, initialBackoff, maximumBackoff);
    }
}
