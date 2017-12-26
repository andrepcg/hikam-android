package com.firebase.jobdispatcher;

final class BundleProtocol {
    static final String PACKED_PARAM_BUNDLE_PREFIX = "com.firebase.jobdispatcher.";
    static final String PACKED_PARAM_CONSTRAINTS = "constraints";
    static final String PACKED_PARAM_EXTRAS = "extras";
    static final String PACKED_PARAM_LIFETIME = "persistent";
    static final String PACKED_PARAM_RECURRING = "recurring";
    static final String PACKED_PARAM_REPLACE_CURRENT = "replace_current";
    static final String PACKED_PARAM_RETRY_STRATEGY_INITIAL_BACKOFF_SECONDS = "initial_backoff_seconds";
    static final String PACKED_PARAM_RETRY_STRATEGY_MAXIMUM_BACKOFF_SECONDS = "maximum_backoff_seconds";
    static final String PACKED_PARAM_RETRY_STRATEGY_POLICY = "retry_policy";
    static final String PACKED_PARAM_SERVICE = "service";
    static final String PACKED_PARAM_TAG = "tag";
    static final String PACKED_PARAM_TRIGGER_TYPE = "trigger_type";
    static final String PACKED_PARAM_TRIGGER_WINDOW_END = "window_end";
    static final String PACKED_PARAM_TRIGGER_WINDOW_START = "window_start";
    static final int TRIGGER_TYPE_EXECUTION_WINDOW = 1;
    static final int TRIGGER_TYPE_IMMEDIATE = 2;

    BundleProtocol() {
    }
}
