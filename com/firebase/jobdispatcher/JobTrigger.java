package com.firebase.jobdispatcher;

public class JobTrigger {

    public static final class ExecutionWindowTrigger extends JobTrigger {
        private final int mWindowEnd;
        private final int mWindowStart;

        ExecutionWindowTrigger(int windowStart, int windowEnd) {
            this.mWindowStart = windowStart;
            this.mWindowEnd = windowEnd;
        }

        public int getWindowStart() {
            return this.mWindowStart;
        }

        public int getWindowEnd() {
            return this.mWindowEnd;
        }
    }

    public static final class ImmediateTrigger extends JobTrigger {
        ImmediateTrigger() {
        }
    }
}
