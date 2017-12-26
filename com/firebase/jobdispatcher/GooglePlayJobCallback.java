package com.firebase.jobdispatcher;

import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.gcm.INetworkTaskCallback;
import com.google.android.gms.gcm.INetworkTaskCallback.Stub;

final class GooglePlayJobCallback implements JobCallback {
    private final INetworkTaskCallback mCallback;

    public GooglePlayJobCallback(IBinder binder) {
        this.mCallback = Stub.asInterface(binder);
    }

    public void jobFinished(int status) {
        try {
            this.mCallback.taskFinished(status);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
