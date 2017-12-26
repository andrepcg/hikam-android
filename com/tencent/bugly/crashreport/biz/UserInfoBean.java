package com.tencent.bugly.crashreport.biz;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.os.EnvironmentCompat;
import com.tencent.bugly.proguard.C0761y;
import java.util.Map;

/* compiled from: BUGLY */
public class UserInfoBean implements Parcelable {
    public static final Creator<UserInfoBean> CREATOR = new C06951();
    public long f53a;
    public int f54b;
    public String f55c;
    public String f56d;
    public long f57e;
    public long f58f;
    public long f59g;
    public long f60h;
    public long f61i;
    public String f62j;
    public long f63k = 0;
    public boolean f64l = false;
    public String f65m = EnvironmentCompat.MEDIA_UNKNOWN;
    public String f66n;
    public int f67o;
    public int f68p = -1;
    public int f69q = -1;
    public Map<String, String> f70r = null;
    public Map<String, String> f71s = null;

    /* compiled from: BUGLY */
    static class C06951 implements Creator<UserInfoBean> {
        C06951() {
        }

        public final /* synthetic */ Object createFromParcel(Parcel parcel) {
            return new UserInfoBean(parcel);
        }

        public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
            return new UserInfoBean[i];
        }
    }

    public UserInfoBean(Parcel parcel) {
        boolean z = true;
        this.f54b = parcel.readInt();
        this.f55c = parcel.readString();
        this.f56d = parcel.readString();
        this.f57e = parcel.readLong();
        this.f58f = parcel.readLong();
        this.f59g = parcel.readLong();
        this.f60h = parcel.readLong();
        this.f61i = parcel.readLong();
        this.f62j = parcel.readString();
        this.f63k = parcel.readLong();
        if (parcel.readByte() != (byte) 1) {
            z = false;
        }
        this.f64l = z;
        this.f65m = parcel.readString();
        this.f68p = parcel.readInt();
        this.f69q = parcel.readInt();
        this.f70r = C0761y.m512b(parcel);
        this.f71s = C0761y.m512b(parcel);
        this.f66n = parcel.readString();
        this.f67o = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f54b);
        parcel.writeString(this.f55c);
        parcel.writeString(this.f56d);
        parcel.writeLong(this.f57e);
        parcel.writeLong(this.f58f);
        parcel.writeLong(this.f59g);
        parcel.writeLong(this.f60h);
        parcel.writeLong(this.f61i);
        parcel.writeString(this.f62j);
        parcel.writeLong(this.f63k);
        parcel.writeByte((byte) (this.f64l ? 1 : 0));
        parcel.writeString(this.f65m);
        parcel.writeInt(this.f68p);
        parcel.writeInt(this.f69q);
        C0761y.m514b(parcel, this.f70r);
        C0761y.m514b(parcel, this.f71s);
        parcel.writeString(this.f66n);
        parcel.writeInt(this.f67o);
    }
}
