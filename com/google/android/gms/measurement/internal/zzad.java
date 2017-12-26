package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zze.zzb;
import com.google.android.gms.common.internal.zze.zzc;
import com.google.android.gms.common.util.zze;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class zzad extends zzaa {
    private final zza arP;
    private zzm arQ;
    private Boolean arR;
    private final zzf arS;
    private final zzah arT;
    private final List<Runnable> arU = new ArrayList();
    private final zzf arV;

    class C02323 implements Runnable {
        final /* synthetic */ zzad arW;

        C02323(zzad com_google_android_gms_measurement_internal_zzad) {
            this.arW = com_google_android_gms_measurement_internal_zzad;
        }

        public void run() {
            zzm zzc = this.arW.arQ;
            if (zzc == null) {
                this.arW.zzbvg().zzbwc().log("Failed to send measurementEnabled to service");
                return;
            }
            try {
                zzc.zzb(this.arW.zzbuy().zzmi(this.arW.zzbvg().zzbwk()));
                this.arW.zzabk();
            } catch (RemoteException e) {
                this.arW.zzbvg().zzbwc().zzj("Failed to send measurementEnabled to the service", e);
            }
        }
    }

    class C02367 implements Runnable {
        final /* synthetic */ zzad arW;

        C02367(zzad com_google_android_gms_measurement_internal_zzad) {
            this.arW = com_google_android_gms_measurement_internal_zzad;
        }

        public void run() {
            zzm zzc = this.arW.arQ;
            if (zzc == null) {
                this.arW.zzbvg().zzbwc().log("Discarding data. Failed to send app launch");
                return;
            }
            try {
                zzc.zza(this.arW.zzbuy().zzmi(this.arW.zzbvg().zzbwk()));
                this.arW.zzabk();
            } catch (RemoteException e) {
                this.arW.zzbvg().zzbwc().zzj("Failed to send app launch to the service", e);
            }
        }
    }

    protected class zza implements ServiceConnection, zzb, zzc {
        final /* synthetic */ zzad arW;
        private volatile boolean arY;
        private volatile zzo arZ;

        class C02404 implements Runnable {
            final /* synthetic */ zza asb;

            C02404(zza com_google_android_gms_measurement_internal_zzad_zza) {
                this.asb = com_google_android_gms_measurement_internal_zzad_zza;
            }

            public void run() {
                zzad com_google_android_gms_measurement_internal_zzad = this.asb.arW;
                Context context = this.asb.arW.getContext();
                String str = (!this.asb.arW.zzbvi().zzact() || this.asb.arW.anq.zzbxg()) ? "com.google.android.gms.measurement.AppMeasurementService" : "com.google.android.gms.measurement.PackageMeasurementService";
                com_google_android_gms_measurement_internal_zzad.onServiceDisconnected(new ComponentName(context, str));
            }
        }

        protected zza(zzad com_google_android_gms_measurement_internal_zzad) {
            this.arW = com_google_android_gms_measurement_internal_zzad;
        }

        @MainThread
        public void onConnected(@Nullable Bundle bundle) {
            zzac.zzhq("MeasurementServiceConnection.onConnected");
            synchronized (this) {
                try {
                    final zzm com_google_android_gms_measurement_internal_zzm = (zzm) this.arZ.zzatx();
                    this.arZ = null;
                    this.arW.zzbvf().zzm(new Runnable(this) {
                        final /* synthetic */ zza asb;

                        public void run() {
                            synchronized (this.asb) {
                                this.asb.arY = false;
                                if (!this.asb.arW.isConnected()) {
                                    this.asb.arW.zzbvg().zzbwi().log("Connected to remote service");
                                    this.asb.arW.zza(com_google_android_gms_measurement_internal_zzm);
                                }
                            }
                        }
                    });
                } catch (DeadObjectException e) {
                    this.arZ = null;
                    this.arY = false;
                } catch (IllegalStateException e2) {
                    this.arZ = null;
                    this.arY = false;
                }
            }
        }

        @MainThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzac.zzhq("MeasurementServiceConnection.onConnectionFailed");
            zzp zzbww = this.arW.anq.zzbww();
            if (zzbww != null) {
                zzbww.zzbwe().zzj("Service connection failed", connectionResult);
            }
            synchronized (this) {
                this.arY = false;
                this.arZ = null;
            }
        }

        @MainThread
        public void onConnectionSuspended(int i) {
            zzac.zzhq("MeasurementServiceConnection.onConnectionSuspended");
            this.arW.zzbvg().zzbwi().log("Service connection suspended");
            this.arW.zzbvf().zzm(new C02404(this));
        }

        @MainThread
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            zzac.zzhq("MeasurementServiceConnection.onServiceConnected");
            synchronized (this) {
                if (iBinder == null) {
                    this.arY = false;
                    this.arW.zzbvg().zzbwc().log("Service connected with null binder");
                    return;
                }
                zzm com_google_android_gms_measurement_internal_zzm = null;
                try {
                    String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                    if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                        com_google_android_gms_measurement_internal_zzm = com.google.android.gms.measurement.internal.zzm.zza.zzjl(iBinder);
                        this.arW.zzbvg().zzbwj().log("Bound to IMeasurementService interface");
                    } else {
                        this.arW.zzbvg().zzbwc().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                    }
                } catch (RemoteException e) {
                    this.arW.zzbvg().zzbwc().log("Service connect failed to get IMeasurementService");
                }
                if (com_google_android_gms_measurement_internal_zzm == null) {
                    this.arY = false;
                    try {
                        com.google.android.gms.common.stats.zzb.zzawu().zza(this.arW.getContext(), this.arW.arP);
                    } catch (IllegalArgumentException e2) {
                    }
                } else {
                    this.arW.zzbvf().zzm(new Runnable(this) {
                        final /* synthetic */ zza asb;

                        public void run() {
                            synchronized (this.asb) {
                                this.asb.arY = false;
                                if (!this.asb.arW.isConnected()) {
                                    this.asb.arW.zzbvg().zzbwj().log("Connected to service");
                                    this.asb.arW.zza(com_google_android_gms_measurement_internal_zzm);
                                }
                            }
                        }
                    });
                }
            }
        }

        @MainThread
        public void onServiceDisconnected(final ComponentName componentName) {
            zzac.zzhq("MeasurementServiceConnection.onServiceDisconnected");
            this.arW.zzbvg().zzbwi().log("Service disconnected");
            this.arW.zzbvf().zzm(new Runnable(this) {
                final /* synthetic */ zza asb;

                public void run() {
                    this.asb.arW.onServiceDisconnected(componentName);
                }
            });
        }

        @WorkerThread
        public void zzac(Intent intent) {
            this.arW.zzyl();
            Context context = this.arW.getContext();
            com.google.android.gms.common.stats.zzb zzawu = com.google.android.gms.common.stats.zzb.zzawu();
            synchronized (this) {
                if (this.arY) {
                    this.arW.zzbvg().zzbwj().log("Connection attempt already in progress");
                    return;
                }
                this.arY = true;
                zzawu.zza(context, intent, this.arW.arP, 129);
            }
        }

        @WorkerThread
        public void zzbye() {
            this.arW.zzyl();
            Context context = this.arW.getContext();
            synchronized (this) {
                if (this.arY) {
                    this.arW.zzbvg().zzbwj().log("Connection attempt already in progress");
                } else if (this.arZ != null) {
                    this.arW.zzbvg().zzbwj().log("Already awaiting connection attempt");
                } else {
                    this.arZ = new zzo(context, Looper.getMainLooper(), this, this);
                    this.arW.zzbvg().zzbwj().log("Connecting to remote service");
                    this.arY = true;
                    this.arZ.zzatu();
                }
            }
        }
    }

    protected zzad(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
        this.arT = new zzah(com_google_android_gms_measurement_internal_zzx.zzaan());
        this.arP = new zza(this);
        this.arS = new zzf(this, com_google_android_gms_measurement_internal_zzx) {
            final /* synthetic */ zzad arW;

            public void run() {
                this.arW.zzabl();
            }
        };
        this.arV = new zzf(this, com_google_android_gms_measurement_internal_zzx) {
            final /* synthetic */ zzad arW;

            public void run() {
                this.arW.zzbvg().zzbwe().log("Tasks have been queued for a long time");
            }
        };
    }

    @WorkerThread
    private void onServiceDisconnected(ComponentName componentName) {
        zzyl();
        if (this.arQ != null) {
            this.arQ = null;
            zzbvg().zzbwj().zzj("Disconnected from device MeasurementService", componentName);
            zzbyc();
        }
    }

    @WorkerThread
    private void zza(zzm com_google_android_gms_measurement_internal_zzm) {
        zzyl();
        zzac.zzy(com_google_android_gms_measurement_internal_zzm);
        this.arQ = com_google_android_gms_measurement_internal_zzm;
        zzabk();
        zzbyd();
    }

    @WorkerThread
    private void zzabk() {
        zzyl();
        this.arT.start();
        if (!this.anq.zzbxg()) {
            this.arS.zzx(zzbvi().zzado());
        }
    }

    @WorkerThread
    private void zzabl() {
        zzyl();
        if (isConnected()) {
            zzbvg().zzbwj().log("Inactivity, disconnecting from the service");
            disconnect();
        }
    }

    private boolean zzbya() {
        if (zzbvi().zzact()) {
            return false;
        }
        List queryIntentServices = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    @WorkerThread
    private void zzbyc() {
        zzyl();
        zzabz();
    }

    @WorkerThread
    private void zzbyd() {
        zzyl();
        zzbvg().zzbwj().zzj("Processing queued up service tasks", Integer.valueOf(this.arU.size()));
        for (Runnable zzm : this.arU) {
            zzbvf().zzm(zzm);
        }
        this.arU.clear();
        this.arV.cancel();
    }

    @WorkerThread
    private void zzo(Runnable runnable) throws IllegalStateException {
        zzyl();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.arU.size()) >= zzbvi().zzbug()) {
            zzbvg().zzbwc().log("Discarding data. Max runnable queue size reached");
        } else {
            this.arU.add(runnable);
            if (!this.anq.zzbxg()) {
                this.arV.zzx(60000);
            }
            zzabz();
        }
    }

    @WorkerThread
    public void disconnect() {
        zzyl();
        zzaax();
        try {
            com.google.android.gms.common.stats.zzb.zzawu().zza(getContext(), this.arP);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.arQ = null;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public boolean isConnected() {
        zzyl();
        zzaax();
        return this.arQ != null;
    }

    @WorkerThread
    protected void zza(final UserAttributeParcel userAttributeParcel) {
        zzyl();
        zzaax();
        zzo(new Runnable(this) {
            final /* synthetic */ zzad arW;

            public void run() {
                zzm zzc = this.arW.arQ;
                if (zzc == null) {
                    this.arW.zzbvg().zzbwc().log("Discarding data. Failed to set user attribute");
                    return;
                }
                try {
                    zzc.zza(userAttributeParcel, this.arW.zzbuy().zzmi(this.arW.zzbvg().zzbwk()));
                    this.arW.zzabk();
                } catch (RemoteException e) {
                    this.arW.zzbvg().zzbwc().zzj("Failed to send attribute to the service", e);
                }
            }
        });
    }

    @WorkerThread
    protected void zza(final AtomicReference<List<UserAttributeParcel>> atomicReference, final boolean z) {
        zzyl();
        zzaax();
        zzo(new Runnable(this) {
            final /* synthetic */ zzad arW;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r5 = this;
                r1 = r2;
                monitor-enter(r1);
                r0 = r5.arW;	 Catch:{ RemoteException -> 0x0046 }
                r0 = r0.arQ;	 Catch:{ RemoteException -> 0x0046 }
                if (r0 != 0) goto L_0x0021;
            L_0x000b:
                r0 = r5.arW;	 Catch:{ RemoteException -> 0x0046 }
                r0 = r0.zzbvg();	 Catch:{ RemoteException -> 0x0046 }
                r0 = r0.zzbwc();	 Catch:{ RemoteException -> 0x0046 }
                r2 = "Failed to get user properties";
                r0.log(r2);	 Catch:{ RemoteException -> 0x0046 }
                r0 = r2;	 Catch:{ all -> 0x0043 }
                r0.notify();	 Catch:{ all -> 0x0043 }
                monitor-exit(r1);	 Catch:{ all -> 0x0043 }
            L_0x0020:
                return;
            L_0x0021:
                r2 = r2;	 Catch:{ RemoteException -> 0x0046 }
                r3 = r5.arW;	 Catch:{ RemoteException -> 0x0046 }
                r3 = r3.zzbuy();	 Catch:{ RemoteException -> 0x0046 }
                r4 = 0;
                r3 = r3.zzmi(r4);	 Catch:{ RemoteException -> 0x0046 }
                r4 = r3;	 Catch:{ RemoteException -> 0x0046 }
                r0 = r0.zza(r3, r4);	 Catch:{ RemoteException -> 0x0046 }
                r2.set(r0);	 Catch:{ RemoteException -> 0x0046 }
                r0 = r5.arW;	 Catch:{ RemoteException -> 0x0046 }
                r0.zzabk();	 Catch:{ RemoteException -> 0x0046 }
                r0 = r2;	 Catch:{ all -> 0x0043 }
                r0.notify();	 Catch:{ all -> 0x0043 }
            L_0x0041:
                monitor-exit(r1);	 Catch:{ all -> 0x0043 }
                goto L_0x0020;
            L_0x0043:
                r0 = move-exception;
                monitor-exit(r1);	 Catch:{ all -> 0x0043 }
                throw r0;
            L_0x0046:
                r0 = move-exception;
                r2 = r5.arW;	 Catch:{ all -> 0x005c }
                r2 = r2.zzbvg();	 Catch:{ all -> 0x005c }
                r2 = r2.zzbwc();	 Catch:{ all -> 0x005c }
                r3 = "Failed to get user properties";
                r2.zzj(r3, r0);	 Catch:{ all -> 0x005c }
                r0 = r2;	 Catch:{ all -> 0x0043 }
                r0.notify();	 Catch:{ all -> 0x0043 }
                goto L_0x0041;
            L_0x005c:
                r0 = move-exception;
                r2 = r2;	 Catch:{ all -> 0x0043 }
                r2.notify();	 Catch:{ all -> 0x0043 }
                throw r0;	 Catch:{ all -> 0x0043 }
                */
                throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzad.6.run():void");
            }
        });
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ zze zzaan() {
        return super.zzaan();
    }

    @WorkerThread
    void zzabz() {
        zzyl();
        zzaax();
        if (!isConnected()) {
            if (this.arR == null) {
                this.arR = zzbvh().zzbwq();
                if (this.arR == null) {
                    zzbvg().zzbwj().log("State of service unknown");
                    this.arR = Boolean.valueOf(zzbyb());
                    zzbvh().zzcf(this.arR.booleanValue());
                }
            }
            if (this.arR.booleanValue()) {
                zzbvg().zzbwj().log("Using measurement service");
                this.arP.zzbye();
            } else if (this.anq.zzbxg() || !zzbya()) {
                zzbvg().zzbwc().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
            } else {
                zzbvg().zzbwj().log("Using local app measurement service");
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                intent.setComponent(new ComponentName(getContext(), zzbvi().zzact() ? "com.google.android.gms.measurement.PackageMeasurementService" : "com.google.android.gms.measurement.AppMeasurementService"));
                this.arP.zzac(intent);
            }
        }
    }

    public /* bridge */ /* synthetic */ void zzbuv() {
        super.zzbuv();
    }

    public /* bridge */ /* synthetic */ zzc zzbuw() {
        return super.zzbuw();
    }

    public /* bridge */ /* synthetic */ zzac zzbux() {
        return super.zzbux();
    }

    public /* bridge */ /* synthetic */ zzn zzbuy() {
        return super.zzbuy();
    }

    public /* bridge */ /* synthetic */ zzg zzbuz() {
        return super.zzbuz();
    }

    public /* bridge */ /* synthetic */ zzad zzbva() {
        return super.zzbva();
    }

    public /* bridge */ /* synthetic */ zze zzbvb() {
        return super.zzbvb();
    }

    public /* bridge */ /* synthetic */ zzal zzbvc() {
        return super.zzbvc();
    }

    public /* bridge */ /* synthetic */ zzv zzbvd() {
        return super.zzbvd();
    }

    public /* bridge */ /* synthetic */ zzaf zzbve() {
        return super.zzbve();
    }

    public /* bridge */ /* synthetic */ zzw zzbvf() {
        return super.zzbvf();
    }

    public /* bridge */ /* synthetic */ zzp zzbvg() {
        return super.zzbvg();
    }

    public /* bridge */ /* synthetic */ zzt zzbvh() {
        return super.zzbvh();
    }

    public /* bridge */ /* synthetic */ zzd zzbvi() {
        return super.zzbvi();
    }

    @WorkerThread
    protected void zzbxw() {
        zzyl();
        zzaax();
        zzo(new C02367(this));
    }

    @WorkerThread
    protected void zzbxz() {
        zzyl();
        zzaax();
        zzo(new C02323(this));
    }

    @WorkerThread
    protected boolean zzbyb() {
        zzyl();
        zzaax();
        if (zzbvi().zzact()) {
            return true;
        }
        zzbvg().zzbwj().log("Checking service availability");
        switch (com.google.android.gms.common.zzc.zzapd().isGooglePlayServicesAvailable(getContext())) {
            case 0:
                zzbvg().zzbwj().log("Service available");
                return true;
            case 1:
                zzbvg().zzbwj().log("Service missing");
                return false;
            case 2:
                zzbvg().zzbwi().log("Service container out of date");
                return true;
            case 3:
                zzbvg().zzbwe().log("Service disabled");
                return false;
            case 9:
                zzbvg().zzbwe().log("Service invalid");
                return false;
            case 18:
                zzbvg().zzbwe().log("Service updating");
                return true;
            default:
                return false;
        }
    }

    @WorkerThread
    protected void zzc(final EventParcel eventParcel, final String str) {
        zzac.zzy(eventParcel);
        zzyl();
        zzaax();
        zzo(new Runnable(this) {
            final /* synthetic */ zzad arW;

            public void run() {
                zzm zzc = this.arW.arQ;
                if (zzc == null) {
                    this.arW.zzbvg().zzbwc().log("Discarding data. Failed to send event to service");
                    return;
                }
                try {
                    if (TextUtils.isEmpty(str)) {
                        zzc.zza(eventParcel, this.arW.zzbuy().zzmi(this.arW.zzbvg().zzbwk()));
                    } else {
                        zzc.zza(eventParcel, str, this.arW.zzbvg().zzbwk());
                    }
                    this.arW.zzabk();
                } catch (RemoteException e) {
                    this.arW.zzbvg().zzbwc().zzj("Failed to send event to the service", e);
                }
            }
        });
    }

    public /* bridge */ /* synthetic */ void zzyl() {
        super.zzyl();
    }

    protected void zzym() {
    }
}
