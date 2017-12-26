package com.google.android.gms.gcm;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Keep;

@Keep
public final class PendingCallback implements Parcelable {
    public static final Creator<PendingCallback> CREATOR = new C02051();
    private final IBinder mBinder;

    static class C02051 implements Creator<PendingCallback> {
        C02051() {
        }

        public PendingCallback createFromParcel(Parcel parcel) {
            return new PendingCallback(parcel);
        }

        public PendingCallback[] newArray(int i) {
            return new PendingCallback[i];
        }
    }

    public PendingCallback(Parcel in) {
        this.mBinder = in.readStrongBinder();
    }

    public IBinder getIBinder() {
        return this.mBinder;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStrongBinder(this.mBinder);
    }
}
