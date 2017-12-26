package com.firebase.jobdispatcher;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.gcm.PendingCallback;

final class GooglePlayCallbackExtractor {
    private static final String ERROR_INVALID_CALLBACK = "Bad callback received, terminating";
    private static final String ERROR_NULL_CALLBACK = "No callback received, terminating";
    private static final String TAG = "FJD.GooglePlayReceiver";

    GooglePlayCallbackExtractor() {
    }

    public JobCallback extractCallback(@Nullable Bundle data) {
        if (data == null) {
            Log.e(TAG, ERROR_NULL_CALLBACK);
            return null;
        }
        data.setClassLoader(PendingCallback.class.getClassLoader());
        Parcelable parcelledCallback = data.getParcelable("callback");
        if (parcelledCallback == null) {
            Log.e(TAG, ERROR_NULL_CALLBACK);
            return null;
        } else if (parcelledCallback instanceof PendingCallback) {
            return new GooglePlayJobCallback(((PendingCallback) parcelledCallback).getIBinder());
        } else {
            Log.e(TAG, ERROR_INVALID_CALLBACK);
            return null;
        }
    }
}
