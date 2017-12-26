package com.firebase.jobdispatcher;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public final class GooglePlayDriver implements Driver {
    private static final String ACTION_SCHEDULE = "com.google.android.gms.gcm.ACTION_SCHEDULE";
    private static final String BACKEND_PACKAGE = "com.google.android.gms";
    private static final String BUNDLE_PARAM_COMPONENT = "component";
    private static final String BUNDLE_PARAM_SCHEDULER_ACTION = "scheduler_action";
    private static final String BUNDLE_PARAM_TAG = "tag";
    private static final String BUNDLE_PARAM_TOKEN = "app";
    private static final String SCHEDULER_ACTION_CANCEL_ALL = "CANCEL_ALL";
    private static final String SCHEDULER_ACTION_CANCEL_TASK = "CANCEL_TASK";
    private static final String SCHEDULER_ACTION_SCHEDULE_TASK = "SCHEDULE_TASK";
    private final boolean mAvailable = true;
    private final Context mContext;
    private final PendingIntent mToken;
    private final JobValidator mValidator;
    private final GooglePlayJobWriter mWriter;

    public GooglePlayDriver(Context context) {
        this.mContext = context;
        this.mToken = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
        this.mWriter = new GooglePlayJobWriter();
        this.mValidator = new DefaultJobValidator(context);
    }

    public boolean isAvailable() {
        return true;
    }

    public int schedule(@NonNull Job job) {
        this.mContext.sendBroadcast(createScheduleRequest(job));
        return 0;
    }

    public int cancel(@NonNull String tag) {
        this.mContext.sendBroadcast(createCancelRequest(tag));
        return 0;
    }

    public int cancelAll() {
        this.mContext.sendBroadcast(createBatchCancelRequest());
        return 0;
    }

    @NonNull
    protected Intent createCancelRequest(@NonNull String tag) {
        Intent cancelReq = createSchedulerIntent(SCHEDULER_ACTION_CANCEL_TASK);
        cancelReq.putExtra(BUNDLE_PARAM_TAG, tag);
        cancelReq.putExtra(BUNDLE_PARAM_COMPONENT, new ComponentName(this.mContext, getReceiverClass()));
        return cancelReq;
    }

    @NonNull
    protected Intent createBatchCancelRequest() {
        Intent cancelReq = createSchedulerIntent(SCHEDULER_ACTION_CANCEL_ALL);
        cancelReq.putExtra(BUNDLE_PARAM_COMPONENT, new ComponentName(this.mContext, getReceiverClass()));
        return cancelReq;
    }

    @NonNull
    protected Class<GooglePlayReceiver> getReceiverClass() {
        return GooglePlayReceiver.class;
    }

    @NonNull
    public JobValidator getValidator() {
        return this.mValidator;
    }

    @NonNull
    private Intent createScheduleRequest(JobParameters job) {
        Intent scheduleReq = createSchedulerIntent(SCHEDULER_ACTION_SCHEDULE_TASK);
        scheduleReq.putExtras(this.mWriter.writeToBundle(job, scheduleReq.getExtras()));
        return scheduleReq;
    }

    @NonNull
    private Intent createSchedulerIntent(String schedulerAction) {
        Intent scheduleReq = new Intent(ACTION_SCHEDULE);
        scheduleReq.setPackage("com.google.android.gms");
        scheduleReq.putExtra(BUNDLE_PARAM_SCHEDULER_ACTION, schedulerAction);
        scheduleReq.putExtra(BUNDLE_PARAM_TOKEN, this.mToken);
        return scheduleReq;
    }
}
