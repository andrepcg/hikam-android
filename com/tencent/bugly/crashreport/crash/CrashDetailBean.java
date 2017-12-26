package com.tencent.bugly.crashreport.crash;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.tencent.bugly.crashreport.common.info.PlugInBean;
import com.tencent.bugly.proguard.C0761y;
import java.util.Map;
import java.util.UUID;

/* compiled from: BUGLY */
public class CrashDetailBean implements Parcelable, Comparable<CrashDetailBean> {
    public static final Creator<CrashDetailBean> CREATOR = new C07101();
    public String f194A = "";
    public long f195B = -1;
    public long f196C = -1;
    public long f197D = -1;
    public long f198E = -1;
    public long f199F = -1;
    public long f200G = -1;
    public String f201H = "";
    public String f202I = "";
    public String f203J = "";
    public String f204K = "";
    public long f205L = -1;
    public boolean f206M = false;
    public Map<String, String> f207N = null;
    public int f208O = -1;
    public int f209P = -1;
    public Map<String, String> f210Q = null;
    public Map<String, String> f211R = null;
    public byte[] f212S = null;
    public String f213T = null;
    public String f214U = null;
    private String f215V = "";
    public long f216a = -1;
    public int f217b = 0;
    public String f218c = UUID.randomUUID().toString();
    public boolean f219d = false;
    public String f220e = "";
    public String f221f = "";
    public String f222g = "";
    public Map<String, PlugInBean> f223h = null;
    public Map<String, PlugInBean> f224i = null;
    public boolean f225j = false;
    public boolean f226k = false;
    public int f227l = 0;
    public String f228m = "";
    public String f229n = "";
    public String f230o = "";
    public String f231p = "";
    public String f232q = "";
    public long f233r = -1;
    public String f234s = null;
    public int f235t = 0;
    public String f236u = "";
    public String f237v = "";
    public String f238w = null;
    public byte[] f239x = null;
    public Map<String, String> f240y = null;
    public String f241z = "";

    /* compiled from: BUGLY */
    static class C07101 implements Creator<CrashDetailBean> {
        C07101() {
        }

        public final /* synthetic */ Object createFromParcel(Parcel parcel) {
            return new CrashDetailBean(parcel);
        }

        public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
            return new CrashDetailBean[i];
        }
    }

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        CrashDetailBean crashDetailBean = (CrashDetailBean) obj;
        if (crashDetailBean != null) {
            long j = this.f233r - crashDetailBean.f233r;
            if (j <= 0) {
                return j < 0 ? -1 : 0;
            }
        }
        return 1;
    }

    public CrashDetailBean(Parcel parcel) {
        boolean z;
        boolean z2 = true;
        this.f217b = parcel.readInt();
        this.f218c = parcel.readString();
        this.f219d = parcel.readByte() == (byte) 1;
        this.f220e = parcel.readString();
        this.f221f = parcel.readString();
        this.f222g = parcel.readString();
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.f225j = z;
        if (parcel.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.f226k = z;
        this.f227l = parcel.readInt();
        this.f228m = parcel.readString();
        this.f229n = parcel.readString();
        this.f230o = parcel.readString();
        this.f231p = parcel.readString();
        this.f232q = parcel.readString();
        this.f233r = parcel.readLong();
        this.f234s = parcel.readString();
        this.f235t = parcel.readInt();
        this.f236u = parcel.readString();
        this.f237v = parcel.readString();
        this.f238w = parcel.readString();
        this.f240y = C0761y.m512b(parcel);
        this.f241z = parcel.readString();
        this.f194A = parcel.readString();
        this.f195B = parcel.readLong();
        this.f196C = parcel.readLong();
        this.f197D = parcel.readLong();
        this.f198E = parcel.readLong();
        this.f199F = parcel.readLong();
        this.f200G = parcel.readLong();
        this.f201H = parcel.readString();
        this.f215V = parcel.readString();
        this.f202I = parcel.readString();
        this.f203J = parcel.readString();
        this.f204K = parcel.readString();
        this.f205L = parcel.readLong();
        if (parcel.readByte() != (byte) 1) {
            z2 = false;
        }
        this.f206M = z2;
        this.f207N = C0761y.m512b(parcel);
        this.f223h = C0761y.m496a(parcel);
        this.f224i = C0761y.m496a(parcel);
        this.f208O = parcel.readInt();
        this.f209P = parcel.readInt();
        this.f210Q = C0761y.m512b(parcel);
        this.f211R = C0761y.m512b(parcel);
        this.f212S = parcel.createByteArray();
        this.f239x = parcel.createByteArray();
        this.f213T = parcel.readString();
        this.f214U = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2;
        int i3 = 1;
        parcel.writeInt(this.f217b);
        parcel.writeString(this.f218c);
        parcel.writeByte((byte) (this.f219d ? 1 : 0));
        parcel.writeString(this.f220e);
        parcel.writeString(this.f221f);
        parcel.writeString(this.f222g);
        if (this.f225j) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        if (this.f226k) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        parcel.writeInt(this.f227l);
        parcel.writeString(this.f228m);
        parcel.writeString(this.f229n);
        parcel.writeString(this.f230o);
        parcel.writeString(this.f231p);
        parcel.writeString(this.f232q);
        parcel.writeLong(this.f233r);
        parcel.writeString(this.f234s);
        parcel.writeInt(this.f235t);
        parcel.writeString(this.f236u);
        parcel.writeString(this.f237v);
        parcel.writeString(this.f238w);
        C0761y.m514b(parcel, this.f240y);
        parcel.writeString(this.f241z);
        parcel.writeString(this.f194A);
        parcel.writeLong(this.f195B);
        parcel.writeLong(this.f196C);
        parcel.writeLong(this.f197D);
        parcel.writeLong(this.f198E);
        parcel.writeLong(this.f199F);
        parcel.writeLong(this.f200G);
        parcel.writeString(this.f201H);
        parcel.writeString(this.f215V);
        parcel.writeString(this.f202I);
        parcel.writeString(this.f203J);
        parcel.writeString(this.f204K);
        parcel.writeLong(this.f205L);
        if (!this.f206M) {
            i3 = 0;
        }
        parcel.writeByte((byte) i3);
        C0761y.m514b(parcel, this.f207N);
        C0761y.m497a(parcel, this.f223h);
        C0761y.m497a(parcel, this.f224i);
        parcel.writeInt(this.f208O);
        parcel.writeInt(this.f209P);
        C0761y.m514b(parcel, this.f210Q);
        C0761y.m514b(parcel, this.f211R);
        parcel.writeByteArray(this.f212S);
        parcel.writeByteArray(this.f239x);
        parcel.writeString(this.f213T);
        parcel.writeString(this.f214U);
    }
}
