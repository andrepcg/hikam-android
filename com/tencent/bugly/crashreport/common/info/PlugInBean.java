package com.tencent.bugly.crashreport.common.info;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/* compiled from: BUGLY */
public class PlugInBean implements Parcelable {
    public static final Creator<PlugInBean> CREATOR = new C07041();
    public final String f99a;
    public final String f100b;
    public final String f101c;

    /* compiled from: BUGLY */
    static class C07041 implements Creator<PlugInBean> {
        C07041() {
        }

        public final /* synthetic */ Object createFromParcel(Parcel parcel) {
            return new PlugInBean(parcel);
        }

        public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
            return new PlugInBean[i];
        }
    }

    public PlugInBean(String str, String str2, String str3) {
        this.f99a = str;
        this.f100b = str2;
        this.f101c = str3;
    }

    public String toString() {
        return "plid:" + this.f99a + " plV:" + this.f100b + " plUUID:" + this.f101c;
    }

    public PlugInBean(Parcel parcel) {
        this.f99a = parcel.readString();
        this.f100b = parcel.readString();
        this.f101c = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.f99a);
        parcel.writeString(this.f100b);
        parcel.writeString(this.f101c);
    }
}
