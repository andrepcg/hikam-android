package com.tencent.bugly.crashreport.common.strategy;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.tencent.bugly.proguard.C0761y;
import java.util.Map;

/* compiled from: BUGLY */
public class StrategyBean implements Parcelable {
    public static final Creator<StrategyBean> CREATOR = new C07071();
    public static String f156a = "http://rqd.uu.qq.com/rqd/sync";
    public static String f157b = "http://android.bugly.qq.com/rqd/async";
    public static String f158c = "http://android.bugly.qq.com/rqd/async";
    public static String f159d;
    public long f160e;
    public long f161f;
    public boolean f162g;
    public boolean f163h;
    public boolean f164i;
    public boolean f165j;
    public boolean f166k;
    public boolean f167l;
    public boolean f168m;
    public boolean f169n;
    public boolean f170o;
    public long f171p;
    public long f172q;
    public String f173r;
    public String f174s;
    public String f175t;
    public String f176u;
    public Map<String, String> f177v;
    public int f178w;
    public long f179x;
    public long f180y;

    /* compiled from: BUGLY */
    static class C07071 implements Creator<StrategyBean> {
        C07071() {
        }

        public final /* synthetic */ Object createFromParcel(Parcel parcel) {
            return new StrategyBean(parcel);
        }

        public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
            return new StrategyBean[i];
        }
    }

    public StrategyBean() {
        this.f160e = -1;
        this.f161f = -1;
        this.f162g = true;
        this.f163h = true;
        this.f164i = true;
        this.f165j = true;
        this.f166k = false;
        this.f167l = true;
        this.f168m = true;
        this.f169n = true;
        this.f170o = true;
        this.f172q = 30000;
        this.f173r = f157b;
        this.f174s = f158c;
        this.f175t = f156a;
        this.f178w = 10;
        this.f179x = 300000;
        this.f180y = -1;
        this.f161f = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("S(@L@L").append("@)");
        f159d = stringBuilder.toString();
        stringBuilder.setLength(0);
        stringBuilder.append("*^@K#K").append("@!");
        this.f176u = stringBuilder.toString();
    }

    public StrategyBean(Parcel parcel) {
        boolean z = true;
        this.f160e = -1;
        this.f161f = -1;
        this.f162g = true;
        this.f163h = true;
        this.f164i = true;
        this.f165j = true;
        this.f166k = false;
        this.f167l = true;
        this.f168m = true;
        this.f169n = true;
        this.f170o = true;
        this.f172q = 30000;
        this.f173r = f157b;
        this.f174s = f158c;
        this.f175t = f156a;
        this.f178w = 10;
        this.f179x = 300000;
        this.f180y = -1;
        try {
            boolean z2;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("S(@L@L").append("@)");
            f159d = stringBuilder.toString();
            this.f161f = parcel.readLong();
            this.f162g = parcel.readByte() == (byte) 1;
            if (parcel.readByte() == (byte) 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f163h = z2;
            if (parcel.readByte() == (byte) 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f164i = z2;
            this.f173r = parcel.readString();
            this.f174s = parcel.readString();
            this.f176u = parcel.readString();
            this.f177v = C0761y.m512b(parcel);
            if (parcel.readByte() == (byte) 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f165j = z2;
            if (parcel.readByte() == (byte) 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f166k = z2;
            if (parcel.readByte() == (byte) 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f169n = z2;
            if (parcel.readByte() == (byte) 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f170o = z2;
            this.f172q = parcel.readLong();
            if (parcel.readByte() == (byte) 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.f167l = z2;
            if (parcel.readByte() != (byte) 1) {
                z = false;
            }
            this.f168m = z;
            this.f171p = parcel.readLong();
            this.f178w = parcel.readInt();
            this.f179x = parcel.readLong();
            this.f180y = parcel.readLong();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2;
        int i3 = 1;
        parcel.writeLong(this.f161f);
        parcel.writeByte((byte) (this.f162g ? 1 : 0));
        if (this.f163h) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        if (this.f164i) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        parcel.writeString(this.f173r);
        parcel.writeString(this.f174s);
        parcel.writeString(this.f176u);
        C0761y.m514b(parcel, this.f177v);
        if (this.f165j) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        if (this.f166k) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        if (this.f169n) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        if (this.f170o) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        parcel.writeLong(this.f172q);
        if (this.f167l) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        parcel.writeByte((byte) i2);
        if (!this.f168m) {
            i3 = 0;
        }
        parcel.writeByte((byte) i3);
        parcel.writeLong(this.f171p);
        parcel.writeInt(this.f178w);
        parcel.writeLong(this.f179x);
        parcel.writeLong(this.f180y);
    }
}
