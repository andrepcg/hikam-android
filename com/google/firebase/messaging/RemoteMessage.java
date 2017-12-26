package com.google.firebase.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Map;
import java.util.Map.Entry;

public final class RemoteMessage extends AbstractSafeParcelable {
    public static final Creator<RemoteMessage> CREATOR = new zzc();
    private Notification bhM;
    Bundle eZ;
    final int mVersionCode;
    private Map<String, String> zzczu;

    public static class Builder {
        private final Bundle eZ = new Bundle();
        private final Map<String, String> zzczu = new ArrayMap();

        public Builder(String str) {
            if (TextUtils.isEmpty(str)) {
                String str2 = "Invalid to: ";
                String valueOf = String.valueOf(str);
                throw new IllegalArgumentException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            this.eZ.putString("google.to", str);
        }

        public Builder addData(String str, String str2) {
            this.zzczu.put(str, str2);
            return this;
        }

        public RemoteMessage build() {
            Bundle bundle = new Bundle();
            for (Entry entry : this.zzczu.entrySet()) {
                bundle.putString((String) entry.getKey(), (String) entry.getValue());
            }
            bundle.putAll(this.eZ);
            String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null) {
                this.eZ.putString("from", token);
            } else {
                this.eZ.remove("from");
            }
            return new RemoteMessage(bundle);
        }

        public Builder clearData() {
            this.zzczu.clear();
            return this;
        }

        public Builder setCollapseKey(String str) {
            this.eZ.putString("collapse_key", str);
            return this;
        }

        public Builder setData(Map<String, String> map) {
            this.zzczu.clear();
            this.zzczu.putAll(map);
            return this;
        }

        public Builder setMessageId(String str) {
            this.eZ.putString("google.message_id", str);
            return this;
        }

        public Builder setMessageType(String str) {
            this.eZ.putString("message_type", str);
            return this;
        }

        public Builder setTtl(int i) {
            this.eZ.putString("google.ttl", String.valueOf(i));
            return this;
        }
    }

    public static class Notification {
        private final String HP;
        private final String Nu;
        private final String bhN;
        private final String[] bhO;
        private final String bhP;
        private final String[] bhQ;
        private final String bhR;
        private final String bhS;
        private final String bhT;
        private final String mTag;
        private final String zzbjs;

        private Notification(Bundle bundle) {
            this.HP = zza.zzf(bundle, "gcm.n.title");
            this.bhN = zza.zzh(bundle, "gcm.n.title");
            this.bhO = zzj(bundle, "gcm.n.title");
            this.zzbjs = zza.zzf(bundle, "gcm.n.body");
            this.bhP = zza.zzh(bundle, "gcm.n.body");
            this.bhQ = zzj(bundle, "gcm.n.body");
            this.bhR = zza.zzf(bundle, "gcm.n.icon");
            this.bhS = zza.zzat(bundle);
            this.mTag = zza.zzf(bundle, "gcm.n.tag");
            this.Nu = zza.zzf(bundle, "gcm.n.color");
            this.bhT = zza.zzf(bundle, "gcm.n.click_action");
        }

        private String[] zzj(Bundle bundle, String str) {
            Object[] zzi = zza.zzi(bundle, str);
            if (zzi == null) {
                return null;
            }
            String[] strArr = new String[zzi.length];
            for (int i = 0; i < zzi.length; i++) {
                strArr[i] = String.valueOf(zzi[i]);
            }
            return strArr;
        }

        public String getBody() {
            return this.zzbjs;
        }

        public String[] getBodyLocalizationArgs() {
            return this.bhQ;
        }

        public String getBodyLocalizationKey() {
            return this.bhP;
        }

        public String getClickAction() {
            return this.bhT;
        }

        public String getColor() {
            return this.Nu;
        }

        public String getIcon() {
            return this.bhR;
        }

        public String getSound() {
            return this.bhS;
        }

        public String getTag() {
            return this.mTag;
        }

        public String getTitle() {
            return this.HP;
        }

        public String[] getTitleLocalizationArgs() {
            return this.bhO;
        }

        public String getTitleLocalizationKey() {
            return this.bhN;
        }
    }

    RemoteMessage(int i, Bundle bundle) {
        this.mVersionCode = i;
        this.eZ = bundle;
    }

    RemoteMessage(Bundle bundle) {
        this(1, bundle);
    }

    public String getCollapseKey() {
        return this.eZ.getString("collapse_key");
    }

    public Map<String, String> getData() {
        if (this.zzczu == null) {
            this.zzczu = new ArrayMap();
            for (String str : this.eZ.keySet()) {
                Object obj = this.eZ.get(str);
                if (obj instanceof String) {
                    String str2 = (String) obj;
                    if (!(str.startsWith("google.") || str.startsWith("gcm.") || str.equals("from") || str.equals("message_type") || str.equals("collapse_key"))) {
                        this.zzczu.put(str, str2);
                    }
                }
            }
        }
        return this.zzczu;
    }

    public String getFrom() {
        return this.eZ.getString("from");
    }

    public String getMessageId() {
        String string = this.eZ.getString("google.message_id");
        return string == null ? this.eZ.getString("message_id") : string;
    }

    public String getMessageType() {
        return this.eZ.getString("message_type");
    }

    public Notification getNotification() {
        if (this.bhM == null && zza.zzad(this.eZ)) {
            this.bhM = new Notification(this.eZ);
        }
        return this.bhM;
    }

    public long getSentTime() {
        return this.eZ.getLong("google.sent_time");
    }

    public String getTo() {
        return this.eZ.getString("google.to");
    }

    public int getTtl() {
        Object obj = this.eZ.get("google.ttl");
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                String valueOf = String.valueOf(obj);
                Log.w("FirebaseMessaging", new StringBuilder(String.valueOf(valueOf).length() + 13).append("Invalid TTL: ").append(valueOf).toString());
            }
        }
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    void zzak(Intent intent) {
        intent.putExtras(this.eZ);
    }
}
