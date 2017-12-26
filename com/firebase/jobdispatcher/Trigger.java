package com.firebase.jobdispatcher;

import com.firebase.jobdispatcher.JobTrigger.ExecutionWindowTrigger;
import com.firebase.jobdispatcher.JobTrigger.ImmediateTrigger;

public final class Trigger {
    public static final ImmediateTrigger NOW = new ImmediateTrigger();

    public static ExecutionWindowTrigger executionWindow(int windowStart, int windowEnd) {
        if (windowStart < 0) {
            throw new IllegalArgumentException("Window start can't be less than 0");
        } else if (windowEnd >= windowStart) {
            return new ExecutionWindowTrigger(windowStart, windowEnd);
        } else {
            throw new IllegalArgumentException("Window end can't be less than window start");
        }
    }
}
