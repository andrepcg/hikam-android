package com.tencent.bugly.proguard;

import com.tencent.bugly.C0693b;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/* compiled from: BUGLY */
public final class C0756v {
    private static C0756v f461a;
    private ScheduledExecutorService f462b;

    /* compiled from: BUGLY */
    class C07551 implements ThreadFactory {
        C07551(C0756v c0756v) {
        }

        public final Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("BUGLY_THREAD");
            return thread;
        }
    }

    protected C0756v() {
        this.f462b = null;
        this.f462b = Executors.newScheduledThreadPool(3, new C07551(this));
        if (this.f462b == null || this.f462b.isShutdown()) {
            C0757w.m461d("[AsyncTaskHandler] ScheduledExecutorService is not valiable!", new Object[0]);
        }
    }

    public static synchronized C0756v m449a() {
        C0756v c0756v;
        synchronized (C0756v.class) {
            if (f461a == null) {
                f461a = new C0756v();
            }
            c0756v = f461a;
        }
        return c0756v;
    }

    public final synchronized boolean m451a(Runnable runnable, long j) {
        boolean z = false;
        synchronized (this) {
            if (!m453c()) {
                C0757w.m461d("[AsyncTaskHandler] Async handler was closed, should not post task.", new Object[0]);
            } else if (runnable == null) {
                C0757w.m461d("[AsyncTaskHandler] Task input is null.", new Object[0]);
            } else {
                if (j <= 0) {
                    j = 0;
                }
                C0757w.m460c("[AsyncTaskHandler] Post a delay(time: %dms) task: %s", Long.valueOf(j), runnable.getClass().getName());
                try {
                    this.f462b.schedule(runnable, j, TimeUnit.MILLISECONDS);
                    z = true;
                } catch (Throwable th) {
                    if (C0693b.f47c) {
                        th.printStackTrace();
                    }
                }
            }
        }
        return z;
    }

    public final synchronized boolean m450a(Runnable runnable) {
        boolean z = false;
        synchronized (this) {
            if (!m453c()) {
                C0757w.m461d("[AsyncTaskHandler] Async handler was closed, should not post task.", new Object[0]);
            } else if (runnable == null) {
                C0757w.m461d("[AsyncTaskHandler] Task input is null.", new Object[0]);
            } else {
                C0757w.m460c("[AsyncTaskHandler] Post a normal task: %s", runnable.getClass().getName());
                try {
                    this.f462b.execute(runnable);
                    z = true;
                } catch (Throwable th) {
                    if (C0693b.f47c) {
                        th.printStackTrace();
                    }
                }
            }
        }
        return z;
    }

    public final synchronized void m452b() {
        if (!(this.f462b == null || this.f462b.isShutdown())) {
            C0757w.m460c("[AsyncTaskHandler] Close async handler.", new Object[0]);
            this.f462b.shutdownNow();
        }
    }

    public final synchronized boolean m453c() {
        boolean z;
        z = (this.f462b == null || this.f462b.isShutdown()) ? false : true;
        return z;
    }
}
