package com.tencent.bugly.crashreport.crash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.PointerIconCompat;
import com.jwkj.global.Constants.Action;
import com.tencent.bugly.crashreport.biz.C0703b;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.C0706b;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.proguard.C0753t;
import com.tencent.bugly.proguard.C0757w;

/* compiled from: BUGLY */
public class BuglyBroadcastRecevier extends BroadcastReceiver {
    private static BuglyBroadcastRecevier f189d = null;
    private IntentFilter f190a = new IntentFilter();
    private Context f191b;
    private String f192c;
    private boolean f193e = true;

    public static synchronized BuglyBroadcastRecevier getInstance() {
        BuglyBroadcastRecevier buglyBroadcastRecevier;
        synchronized (BuglyBroadcastRecevier.class) {
            if (f189d == null) {
                f189d = new BuglyBroadcastRecevier();
            }
            buglyBroadcastRecevier = f189d;
        }
        return buglyBroadcastRecevier;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (this.f191b != null) {
            this.f191b.unregisterReceiver(this);
        }
    }

    public synchronized void addFilter(String str) {
        if (!this.f190a.hasAction(str)) {
            this.f190a.addAction(str);
        }
        C0757w.m460c("add action %s", str);
    }

    public synchronized void regist(Context context) {
        try {
            C0757w.m456a("regis BC", new Object[0]);
            this.f191b = context;
            context.registerReceiver(this, this.f190a);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public synchronized void unregist(Context context) {
        try {
            C0757w.m456a("unregis BC", new Object[0]);
            context.unregisterReceiver(this);
            this.f191b = context;
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
        }
    }

    public final void onReceive(Context context, Intent intent) {
        try {
            m178a(context, intent);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
        }
    }

    private synchronized boolean m178a(Context context, Intent intent) {
        boolean z = true;
        synchronized (this) {
            if (!(context == null || intent == null)) {
                if (intent.getAction().equals(Action.ACTION_NETWORK_CHANGE)) {
                    if (this.f193e) {
                        this.f193e = false;
                    } else {
                        String e = C0706b.m149e(this.f191b);
                        C0757w.m460c("is Connect BC " + e, new Object[0]);
                        C0757w.m456a("network %s changed to %s", this.f192c, e);
                        if (e == null) {
                            this.f192c = null;
                        } else {
                            String str = this.f192c;
                            this.f192c = e;
                            long currentTimeMillis = System.currentTimeMillis();
                            C0709a a = C0709a.m169a();
                            C0753t a2 = C0753t.m412a();
                            C0705a a3 = C0705a.m84a(context);
                            if (a == null || a2 == null || a3 == null) {
                                C0757w.m461d("not inited BC not work", new Object[0]);
                            } else if (!e.equals(str)) {
                                if (currentTimeMillis - a2.m430a(C0721c.f286a) > 30000) {
                                    C0757w.m456a("try to upload crash on network changed.", new Object[0]);
                                    C0721c a4 = C0721c.m218a();
                                    if (a4 != null) {
                                        a4.m222a(0);
                                    }
                                }
                                if (currentTimeMillis - a2.m430a((int) PointerIconCompat.TYPE_CONTEXT_MENU) > 30000) {
                                    C0757w.m456a("try to upload userinfo on network changed.", new Object[0]);
                                    C0703b.f85a.m51b();
                                }
                            }
                        }
                    }
                }
            }
            z = false;
        }
        return z;
    }
}
