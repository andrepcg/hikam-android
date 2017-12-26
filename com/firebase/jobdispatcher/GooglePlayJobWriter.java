package com.firebase.jobdispatcher;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import com.firebase.jobdispatcher.JobTrigger.ExecutionWindowTrigger;

final class GooglePlayJobWriter {
    @VisibleForTesting
    static final int LEGACY_NETWORK_ANY = 2;
    @VisibleForTesting
    static final int LEGACY_NETWORK_CONNECTED = 0;
    @VisibleForTesting
    static final int LEGACY_NETWORK_UNMETERED = 1;
    @VisibleForTesting
    static final int LEGACY_RETRY_POLICY_EXPONENTIAL = 0;
    @VisibleForTesting
    static final int LEGACY_RETRY_POLICY_LINEAR = 1;
    static final String REQUEST_PARAM_EXTRAS = "extras";
    static final String REQUEST_PARAM_PERSISTED = "persisted";
    static final String REQUEST_PARAM_REQUIRED_NETWORK = "requiredNetwork";
    static final String REQUEST_PARAM_REQUIRES_CHARGING = "requiresCharging";
    static final String REQUEST_PARAM_RETRY_STRATEGY = "retryStrategy";
    static final String REQUEST_PARAM_RETRY_STRATEGY_INITIAL_BACKOFF_SECONDS = "initial_backoff_seconds";
    static final String REQUEST_PARAM_RETRY_STRATEGY_MAXIMUM_BACKOFF_SECONDS = "maximum_backoff_seconds";
    static final String REQUEST_PARAM_RETRY_STRATEGY_POLICY = "retry_policy";
    static final String REQUEST_PARAM_SERVICE = "service";
    static final String REQUEST_PARAM_TAG = "tag";
    static final String REQUEST_PARAM_TRIGGER_TYPE = "trigger_type";
    static final String REQUEST_PARAM_TRIGGER_WINDOW_END = "window_end";
    static final String REQUEST_PARAM_TRIGGER_WINDOW_FLEX = "period_flex";
    static final String REQUEST_PARAM_TRIGGER_WINDOW_PERIOD = "period";
    static final String REQUEST_PARAM_TRIGGER_WINDOW_START = "window_start";
    static final String REQUEST_PARAM_UPDATE_CURRENT = "update_current";
    private JobCoder jobCoder = new JobCoder("com.firebase.jobdispatcher.", false);

    GooglePlayJobWriter() {
    }

    private static boolean isSupported(JobTrigger trigger) {
        return (trigger instanceof ExecutionWindowTrigger) || trigger == Trigger.NOW;
    }

    private static void writeExecutionWindowTriggerToBundle(JobParameters job, Bundle b, ExecutionWindowTrigger trigger) {
        b.putInt(REQUEST_PARAM_TRIGGER_TYPE, 1);
        if (job.isRecurring()) {
            b.putLong(REQUEST_PARAM_TRIGGER_WINDOW_PERIOD, (long) trigger.getWindowEnd());
            b.putLong(REQUEST_PARAM_TRIGGER_WINDOW_FLEX, (long) (trigger.getWindowEnd() - trigger.getWindowStart()));
            return;
        }
        b.putLong(REQUEST_PARAM_TRIGGER_WINDOW_START, (long) trigger.getWindowStart());
        b.putLong(REQUEST_PARAM_TRIGGER_WINDOW_END, (long) trigger.getWindowEnd());
    }

    private static void writeImmediateTriggerToBundle(Bundle b) {
        b.putInt(REQUEST_PARAM_TRIGGER_TYPE, 2);
        b.putLong(REQUEST_PARAM_TRIGGER_WINDOW_START, 0);
        b.putLong(REQUEST_PARAM_TRIGGER_WINDOW_END, 30);
    }

    public Bundle writeToBundle(JobParameters job, Bundle b) {
        b.putString(REQUEST_PARAM_TAG, job.getTag());
        b.putBoolean(REQUEST_PARAM_UPDATE_CURRENT, job.shouldReplaceCurrent());
        b.putBoolean(REQUEST_PARAM_PERSISTED, job.getLifetime() == 2);
        b.putString("service", GooglePlayReceiver.class.getName());
        writeTriggerToBundle(job, b);
        writeConstraintsToBundle(job, b);
        writeRetryStrategyToBundle(job, b);
        Bundle extras = job.getExtras();
        if (extras == null) {
            extras = new Bundle();
        }
        b.putBundle(REQUEST_PARAM_EXTRAS, this.jobCoder.encode(job, extras));
        return b;
    }

    private void writeRetryStrategyToBundle(JobParameters job, Bundle b) {
        RetryStrategy strategy = job.getRetryStrategy();
        Bundle rb = new Bundle();
        rb.putInt(REQUEST_PARAM_RETRY_STRATEGY_POLICY, convertRetryPolicyToLegacyVersion(strategy.getPolicy()));
        rb.putInt(REQUEST_PARAM_RETRY_STRATEGY_INITIAL_BACKOFF_SECONDS, strategy.getInitialBackoff());
        rb.putInt(REQUEST_PARAM_RETRY_STRATEGY_MAXIMUM_BACKOFF_SECONDS, strategy.getMaximumBackoff());
        b.putBundle(REQUEST_PARAM_RETRY_STRATEGY, rb);
    }

    private int convertRetryPolicyToLegacyVersion(int policy) {
        switch (policy) {
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    private void writeTriggerToBundle(JobParameters job, Bundle b) {
        JobTrigger trigger = job.getTrigger();
        if (!isSupported(trigger)) {
            throw new IllegalArgumentException("Unknown trigger: " + trigger.getClass());
        } else if (trigger == Trigger.NOW) {
            writeImmediateTriggerToBundle(b);
        } else {
            writeExecutionWindowTriggerToBundle(job, b, (ExecutionWindowTrigger) trigger);
        }
    }

    private void writeConstraintsToBundle(JobParameters job, Bundle b) {
        int c = Constraint.compact(job.getConstraints());
        b.putBoolean(REQUEST_PARAM_REQUIRES_CHARGING, (c & 4) == 4);
        b.putInt(REQUEST_PARAM_REQUIRED_NETWORK, convertConstraintsToLegacyNetConstant(c));
    }

    private int convertConstraintsToLegacyNetConstant(int constraintMap) {
        int reqNet = 2;
        if ((constraintMap & 2) == 2) {
            reqNet = 0;
        }
        if ((constraintMap & 1) == 1) {
            return 1;
        }
        return reqNet;
    }
}
