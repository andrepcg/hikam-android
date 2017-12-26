package com.tencent.bugly.crashreport.common.strategy;

import android.content.Context;
import android.os.Parcelable;
import com.tencent.bugly.C0692a;
import com.tencent.bugly.crashreport.biz.C0703b;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.proguard.C0745o;
import com.tencent.bugly.proguard.C0747q;
import com.tencent.bugly.proguard.C0756v;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import com.tencent.bugly.proguard.ao;
import java.util.List;
import java.util.Map;

/* compiled from: BUGLY */
public final class C0709a {
    public static int f182a = 1000;
    private static C0709a f183b = null;
    private final List<C0692a> f184c;
    private final C0756v f185d;
    private final StrategyBean f186e;
    private StrategyBean f187f = null;
    private Context f188g;

    /* compiled from: BUGLY */
    class C07081 extends Thread {
        private /* synthetic */ C0709a f181a;

        C07081(C0709a c0709a) {
            this.f181a = c0709a;
        }

        public final void run() {
            try {
                Map a = C0745o.m380a().m398a(C0709a.f182a, null, true);
                if (a != null) {
                    byte[] bArr = (byte[]) a.get("key_imei");
                    byte[] bArr2 = (byte[]) a.get("key_ip");
                    if (bArr != null) {
                        C0705a.m84a(this.f181a.f188g).m115e(new String(bArr));
                    }
                    if (bArr2 != null) {
                        C0705a.m84a(this.f181a.f188g).m113d(new String(bArr2));
                    }
                }
                C0709a c0709a = this.f181a;
                this.f181a.f187f = C0709a.m172d();
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
            this.f181a.m174a(this.f181a.f187f, false);
        }
    }

    private C0709a(Context context, List<C0692a> list) {
        this.f188g = context;
        this.f186e = new StrategyBean();
        this.f184c = list;
        this.f185d = C0756v.m449a();
    }

    public static synchronized C0709a m170a(Context context, List<C0692a> list) {
        C0709a c0709a;
        synchronized (C0709a.class) {
            if (f183b == null) {
                f183b = new C0709a(context, list);
            }
            c0709a = f183b;
        }
        return c0709a;
    }

    public final void m173a(long j) {
        this.f185d.m451a(new C07081(this), j);
    }

    public static synchronized C0709a m169a() {
        C0709a c0709a;
        synchronized (C0709a.class) {
            c0709a = f183b;
        }
        return c0709a;
    }

    public final synchronized boolean m176b() {
        return this.f187f != null;
    }

    public final StrategyBean m177c() {
        if (this.f187f != null) {
            return this.f187f;
        }
        return this.f186e;
    }

    protected final void m174a(StrategyBean strategyBean, boolean z) {
        C0757w.m460c("[Strategy] Notify %s", C0703b.class.getName());
        C0703b.m57a(strategyBean, z);
        for (C0692a c0692a : this.f184c) {
            try {
                C0757w.m460c("[Strategy] Notify %s", c0692a.getClass().getName());
                c0692a.onServerStrategyChanged(strategyBean);
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
    }

    public final void m175a(ao aoVar) {
        if (aoVar != null) {
            if (this.f187f == null || aoVar.f654h != this.f187f.f171p) {
                StrategyBean strategyBean = new StrategyBean();
                strategyBean.f162g = aoVar.f647a;
                strategyBean.f164i = aoVar.f649c;
                strategyBean.f163h = aoVar.f648b;
                if (C0761y.m522c(aoVar.f650d)) {
                    C0757w.m460c("[Strategy] Upload url changes to %s", aoVar.f650d);
                    strategyBean.f173r = aoVar.f650d;
                }
                if (C0761y.m522c(aoVar.f651e)) {
                    C0757w.m460c("[Strategy] Exception upload url changes to %s", aoVar.f651e);
                    strategyBean.f174s = aoVar.f651e;
                }
                if (!(aoVar.f652f == null || C0761y.m501a(aoVar.f652f.f642a))) {
                    strategyBean.f176u = aoVar.f652f.f642a;
                }
                if (aoVar.f654h != 0) {
                    strategyBean.f171p = aoVar.f654h;
                }
                if (aoVar.f653g != null && aoVar.f653g.size() > 0) {
                    strategyBean.f177v = aoVar.f653g;
                    String str = (String) aoVar.f653g.get("B11");
                    if (str == null || !str.equals("1")) {
                        strategyBean.f165j = false;
                    } else {
                        strategyBean.f165j = true;
                    }
                    str = (String) aoVar.f653g.get("B3");
                    if (str != null) {
                        strategyBean.f180y = Long.valueOf(str).longValue();
                    }
                    strategyBean.f172q = (long) aoVar.f655i;
                    strategyBean.f179x = (long) aoVar.f655i;
                    str = (String) aoVar.f653g.get("B27");
                    if (str != null && str.length() > 0) {
                        try {
                            int parseInt = Integer.parseInt(str);
                            if (parseInt > 0) {
                                strategyBean.f178w = parseInt;
                            }
                        } catch (Throwable e) {
                            if (!C0757w.m457a(e)) {
                                e.printStackTrace();
                            }
                        }
                    }
                    str = (String) aoVar.f653g.get("B25");
                    if (str == null || !str.equals("1")) {
                        strategyBean.f167l = false;
                    } else {
                        strategyBean.f167l = true;
                    }
                }
                C0757w.m456a("[Strategy] enableCrashReport:%b, enableQuery:%b, enableUserInfo:%b, enableAnr:%b, enableBlock:%b, enableSession:%b, enableSessionTimer:%b, sessionOverTime:%d, enableCocos:%b, strategyLastUpdateTime:%d", Boolean.valueOf(strategyBean.f162g), Boolean.valueOf(strategyBean.f164i), Boolean.valueOf(strategyBean.f163h), Boolean.valueOf(strategyBean.f165j), Boolean.valueOf(strategyBean.f166k), Boolean.valueOf(strategyBean.f169n), Boolean.valueOf(strategyBean.f170o), Long.valueOf(strategyBean.f172q), Boolean.valueOf(strategyBean.f167l), Long.valueOf(strategyBean.f171p));
                this.f187f = strategyBean;
                C0745o.m380a().m403b(2);
                C0747q c0747q = new C0747q();
                c0747q.f405b = 2;
                c0747q.f404a = strategyBean.f160e;
                c0747q.f408e = strategyBean.f161f;
                c0747q.f410g = C0761y.m504a((Parcelable) strategyBean);
                C0745o.m380a().m402a(c0747q);
                m174a(strategyBean, true);
            }
        }
    }

    public static StrategyBean m172d() {
        List a = C0745o.m380a().m397a(2);
        if (a != null && a.size() > 0) {
            C0747q c0747q = (C0747q) a.get(0);
            if (c0747q.f410g != null) {
                return (StrategyBean) C0761y.m485a(c0747q.f410g, StrategyBean.CREATOR);
            }
        }
        return null;
    }
}
