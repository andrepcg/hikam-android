package com.google.android.gms.internal;

import android.os.Binder;

public abstract class zzrs<T> {
    private static String READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
    private static zza zB = null;
    private static int zC = 0;
    private static final Object zzaok = new Object();
    private T zD = null;
    protected final String zzbaf;
    protected final T zzbag;

    private interface zza {
        Long getLong(String str, Long l);

        String getString(String str, String str2);

        Boolean zza(String str, Boolean bool);

        Float zzb(String str, Float f);

        Integer zzb(String str, Integer num);
    }

    class C09981 extends zzrs<Boolean> {
        C09981(String str, Boolean bool) {
            super(str, bool);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhh(str);
        }

        protected Boolean zzhh(String str) {
            return null.zza(this.zzbaf, (Boolean) this.zzbag);
        }
    }

    class C09992 extends zzrs<Long> {
        C09992(String str, Long l) {
            super(str, l);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhi(str);
        }

        protected Long zzhi(String str) {
            return null.getLong(this.zzbaf, (Long) this.zzbag);
        }
    }

    class C10003 extends zzrs<Integer> {
        C10003(String str, Integer num) {
            super(str, num);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhj(str);
        }

        protected Integer zzhj(String str) {
            return null.zzb(this.zzbaf, (Integer) this.zzbag);
        }
    }

    class C10014 extends zzrs<Float> {
        C10014(String str, Float f) {
            super(str, f);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhk(str);
        }

        protected Float zzhk(String str) {
            return null.zzb(this.zzbaf, (Float) this.zzbag);
        }
    }

    class C10025 extends zzrs<String> {
        C10025(String str, String str2) {
            super(str, str2);
        }

        protected /* synthetic */ Object zzhg(String str) {
            return zzhl(str);
        }

        protected String zzhl(String str) {
            return null.getString(this.zzbaf, (String) this.zzbag);
        }
    }

    protected zzrs(String str, T t) {
        this.zzbaf = str;
        this.zzbag = t;
    }

    public static zzrs<Float> zza(String str, Float f) {
        return new C10014(str, f);
    }

    public static zzrs<Integer> zza(String str, Integer num) {
        return new C10003(str, num);
    }

    public static zzrs<Long> zza(String str, Long l) {
        return new C09992(str, l);
    }

    public static zzrs<String> zzab(String str, String str2) {
        return new C10025(str, str2);
    }

    public static zzrs<Boolean> zzm(String str, boolean z) {
        return new C09981(str, Boolean.valueOf(z));
    }

    public final T get() {
        T zzhg;
        long clearCallingIdentity;
        try {
            zzhg = zzhg(this.zzbaf);
        } catch (SecurityException e) {
            clearCallingIdentity = Binder.clearCallingIdentity();
            zzhg = zzhg(this.zzbaf);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
        return zzhg;
    }

    protected abstract T zzhg(String str);
}
