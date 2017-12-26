package com.google.firebase.messaging;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzc implements Creator<RemoteMessage> {
    static void zza(RemoteMessage remoteMessage, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, remoteMessage.mVersionCode);
        zzb.zza(parcel, 2, remoteMessage.eZ, false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzxd(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzagn(i);
    }

    public RemoteMessage[] zzagn(int i) {
        return new RemoteMessage[i];
    }

    public RemoteMessage zzxd(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        Bundle bundle = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    bundle = zza.zzs(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new RemoteMessage(i, bundle);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
