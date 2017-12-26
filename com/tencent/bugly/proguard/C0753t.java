package com.tencent.bugly.proguard;

import android.content.Context;
import android.os.Process;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Base64;
import com.tencent.bugly.C0693b;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/* compiled from: BUGLY */
public final class C0753t {
    private static C0753t f422b = null;
    public boolean f423a = true;
    private final C0745o f424c;
    private final Context f425d;
    private Map<Integer, Long> f426e = new HashMap();
    private LinkedBlockingQueue<Runnable> f427f = new LinkedBlockingQueue();
    private LinkedBlockingQueue<Runnable> f428g = new LinkedBlockingQueue();
    private final Object f429h = new Object();
    private String f430i = null;
    private byte[] f431j = null;
    private long f432k = 0;
    private byte[] f433l = null;
    private long f434m = 0;
    private String f435n = null;
    private long f436o = 0;
    private final Object f437p = new Object();
    private boolean f438q = false;
    private final Object f439r = new Object();
    private int f440s = 0;

    /* compiled from: BUGLY */
    class C0752a implements Runnable {
        private final Context f418a;
        private final Runnable f419b;
        private final long f420c;
        private /* synthetic */ C0753t f421d;

        public C0752a(C0753t c0753t, Context context) {
            this.f421d = c0753t;
            this.f418a = context;
            this.f419b = null;
            this.f420c = 0;
        }

        public C0752a(C0753t c0753t, Context context, Runnable runnable, long j) {
            this.f421d = c0753t;
            this.f418a = context;
            this.f419b = runnable;
            this.f420c = j;
        }

        public final void run() {
            if (C0761y.m499a(this.f418a, "security_info", 30000)) {
                if (!this.f421d.m429e()) {
                    C0757w.m461d("[UploadManager] Failed to load security info from database", new Object[0]);
                    this.f421d.m440b(false);
                }
                if (this.f421d.f435n != null) {
                    if (this.f421d.m441b()) {
                        C0757w.m460c("[UploadManager] Sucessfully got session ID, try to execute upload tasks now (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
                        if (this.f419b != null) {
                            this.f421d.m417a(this.f419b, this.f420c);
                        }
                        this.f421d.m423c(0);
                        C0761y.m516b(this.f418a, "security_info");
                        synchronized (this.f421d.f439r) {
                            this.f421d.f438q = false;
                        }
                        return;
                    }
                    C0757w.m456a("[UploadManager] Session ID is expired, drop it.", new Object[0]);
                    this.f421d.m440b(true);
                }
                byte[] a = C0761y.m502a(128);
                if (a == null || (a.length << 3) != 128) {
                    C0757w.m461d("[UploadManager] Failed to create AES key (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
                    this.f421d.m440b(false);
                    C0761y.m516b(this.f418a, "security_info");
                    synchronized (this.f421d.f439r) {
                        this.f421d.f438q = false;
                    }
                    return;
                }
                this.f421d.f433l = a;
                C0757w.m460c("[UploadManager] Execute one upload task for requesting session ID (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
                if (this.f419b != null) {
                    this.f421d.m417a(this.f419b, this.f420c);
                    return;
                } else {
                    this.f421d.m423c(1);
                    return;
                }
            }
            C0757w.m460c("[UploadManager] Sleep %d try to lock security file again (pid=%d | tid=%d)", Integer.valueOf(5000), Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
            C0761y.m513b(5000);
            if (C0761y.m493a((Runnable) this, "BUGLY_ASYNC_UPLOAD") == null) {
                C0757w.m461d("[UploadManager] Failed to start a thread to execute task of initializing security context, try to post it into thread pool.", new Object[0]);
                C0756v a2 = C0756v.m449a();
                if (a2 != null) {
                    a2.m450a(this);
                } else {
                    C0757w.m462e("[UploadManager] Asynchronous thread pool is unavailable now, try next time.", new Object[0]);
                }
            }
        }
    }

    static /* synthetic */ int m422b(C0753t c0753t) {
        int i = c0753t.f440s - 1;
        c0753t.f440s = i;
        return i;
    }

    private C0753t(Context context) {
        this.f425d = context;
        this.f424c = C0745o.m380a();
        try {
            Class.forName("android.util.Base64");
        } catch (ClassNotFoundException e) {
            C0757w.m456a("[UploadManager] Error: Can not find Base64 class, will not use stronger security way to upload", new Object[0]);
            this.f423a = false;
        }
        if (this.f423a) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDP9x32s5pPtZBXzJBz2GWM/sbTvVO2+RvW0PH01IdaBxc/").append("fB6fbHZocC9T3nl1+J5eAFjIRVuV8vHDky7Qo82Mnh0PVvcZIEQvMMVKU8dsMQopxgsOs2gkSHJwgWdinKNS8CmWobo6pFwPUW11lMv714jAUZRq2GBOqiO2vQI6iwIDAQAB");
            this.f430i = stringBuilder.toString();
        }
    }

    public static synchronized C0753t m413a(Context context) {
        C0753t c0753t;
        synchronized (C0753t.class) {
            if (f422b == null) {
                f422b = new C0753t(context);
            }
            c0753t = f422b;
        }
        return c0753t;
    }

    public static synchronized C0753t m412a() {
        C0753t c0753t;
        synchronized (C0753t.class) {
            c0753t = f422b;
        }
        return c0753t;
    }

    public final void m434a(int i, al alVar, String str, String str2, C0749s c0749s, long j, boolean z) {
        try {
            m418a(new C0754u(this.f425d, i, alVar.f614g, C0730a.m293a(alVar), str, str2, c0749s, this.f423a, z), true, true, j);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
        }
    }

    public final void m432a(int i, int i2, byte[] bArr, String str, String str2, C0749s c0749s, int i3, int i4, boolean z, Map<String, String> map) {
        try {
            m418a(new C0754u(this.f425d, i, i2, bArr, str, str2, c0749s, this.f423a, i3, i4, false, map), z, false, 0);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
        }
    }

    public final void m435a(int i, al alVar, String str, String str2, C0749s c0749s, boolean z) {
        m432a(i, alVar.f614g, C0730a.m293a(alVar), str, str2, c0749s, 0, 0, z, null);
    }

    public final long m431a(boolean z) {
        long j;
        long j2 = 0;
        long b = C0761y.m509b();
        List a = this.f424c.m397a(z ? 5 : 3);
        if (a == null || a.size() <= 0) {
            j = 0;
        } else {
            try {
                C0747q c0747q = (C0747q) a.get(0);
                if (c0747q.f408e >= b) {
                    j2 = C0761y.m521c(c0747q.f410g);
                    a.remove(c0747q);
                }
                j = j2;
            } catch (Throwable th) {
                Throwable th2 = th;
                j = 0;
                C0757w.m457a(th2);
            }
            if (a.size() > 0) {
                this.f424c.m399a(a);
            }
        }
        C0757w.m460c("[UploadManager] Local network consume: %d KB", Long.valueOf(j / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));
        return j;
    }

    protected final synchronized void m437a(long j, boolean z) {
        int i = z ? 5 : 3;
        C0747q c0747q = new C0747q();
        c0747q.f405b = i;
        c0747q.f408e = C0761y.m509b();
        c0747q.f406c = "";
        c0747q.f407d = "";
        c0747q.f410g = C0761y.m523c(j);
        this.f424c.m403b(i);
        this.f424c.m402a(c0747q);
        C0757w.m460c("[UploadManager] Network total consume: %d KB", Long.valueOf(j / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));
    }

    public final synchronized void m433a(int i, long j) {
        if (i >= 0) {
            this.f426e.put(Integer.valueOf(i), Long.valueOf(j));
            C0747q c0747q = new C0747q();
            c0747q.f405b = i;
            c0747q.f408e = j;
            c0747q.f406c = "";
            c0747q.f407d = "";
            c0747q.f410g = new byte[0];
            this.f424c.m403b(i);
            this.f424c.m402a(c0747q);
            C0757w.m460c("[UploadManager] Uploading(ID:%d) time: %s", Integer.valueOf(i), C0761y.m487a(j));
        } else {
            C0757w.m462e("[UploadManager] Unknown uploading ID: %d", Integer.valueOf(i));
        }
    }

    public final synchronized long m430a(int i) {
        long j;
        j = 0;
        if (i >= 0) {
            Long l = (Long) this.f426e.get(Integer.valueOf(i));
            if (l != null) {
                j = l.longValue();
            } else {
                List<C0747q> a = this.f424c.m397a(i);
                if (a != null && a.size() > 0) {
                    if (a.size() > 1) {
                        for (C0747q c0747q : a) {
                            long j2;
                            if (c0747q.f408e > j) {
                                j2 = c0747q.f408e;
                            } else {
                                j2 = j;
                            }
                            j = j2;
                        }
                        this.f424c.m403b(i);
                    } else {
                        try {
                            j = ((C0747q) a.get(0)).f408e;
                        } catch (Throwable th) {
                            C0757w.m457a(th);
                        }
                    }
                }
            }
        } else {
            C0757w.m462e("[UploadManager] Unknown upload ID: %d", Integer.valueOf(i));
        }
        return j;
    }

    public final boolean m442b(int i) {
        if (C0693b.f47c) {
            C0757w.m460c("Uploading frequency will not be checked if SDK is in debug mode.", new Object[0]);
            return true;
        }
        C0757w.m460c("[UploadManager] Time interval is %d seconds since last uploading(ID: %d).", Long.valueOf((System.currentTimeMillis() - m430a(i)) / 1000), Integer.valueOf(i));
        if (System.currentTimeMillis() - m430a(i) >= 30000) {
            return true;
        }
        C0757w.m456a("[UploadManager] Data only be uploaded once in %d seconds.", Long.valueOf(30));
        return false;
    }

    private static boolean m424c() {
        boolean z = false;
        C0757w.m460c("[UploadManager] Drop security info of database (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        try {
            C0745o a = C0745o.m380a();
            if (a == null) {
                C0757w.m461d("[UploadManager] Failed to get Database", new Object[0]);
            } else {
                z = a.m400a(555, "security_info", null, true);
            }
        } catch (Throwable th) {
            C0757w.m457a(th);
        }
        return z;
    }

    private boolean m427d() {
        C0757w.m460c("[UploadManager] Record security info to database (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        try {
            C0745o a = C0745o.m380a();
            if (a == null) {
                C0757w.m461d("[UploadManager] Failed to get database", new Object[0]);
                return false;
            }
            StringBuilder stringBuilder = new StringBuilder();
            if (this.f433l != null) {
                stringBuilder.append(Base64.encodeToString(this.f433l, 0));
                stringBuilder.append("#");
                if (this.f434m != 0) {
                    stringBuilder.append(Long.toString(this.f434m));
                } else {
                    stringBuilder.append("null");
                }
                stringBuilder.append("#");
                if (this.f435n != null) {
                    stringBuilder.append(this.f435n);
                } else {
                    stringBuilder.append("null");
                }
                stringBuilder.append("#");
                if (this.f436o != 0) {
                    stringBuilder.append(Long.toString(this.f436o));
                } else {
                    stringBuilder.append("null");
                }
                a.m401a(555, "security_info", stringBuilder.toString().getBytes(), null, true);
                return true;
            }
            C0757w.m460c("[UploadManager] AES key is null, will not record", new Object[0]);
            return false;
        } catch (Throwable th) {
            C0757w.m457a(th);
            C0753t.m424c();
            return false;
        }
    }

    private boolean m429e() {
        C0757w.m460c("[UploadManager] Load security info from database (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        int i;
        try {
            C0745o a = C0745o.m380a();
            if (a == null) {
                C0757w.m461d("[UploadManager] Failed to get database", new Object[0]);
                return false;
            }
            Map a2 = a.m398a(555, null, true);
            if (a2 != null && a2.containsKey("security_info")) {
                String[] split = new String((byte[]) a2.get("security_info")).split("#");
                if (split.length == 4) {
                    if (split[0].isEmpty() || split[0].equals("null")) {
                        i = 0;
                    } else {
                        this.f433l = Base64.decode(split[0], 0);
                        i = 0;
                    }
                    if (i == 0) {
                        if (!(split[1].isEmpty() || split[1].equals("null"))) {
                            try {
                                this.f434m = Long.parseLong(split[1]);
                            } catch (Throwable th) {
                                C0757w.m457a(th);
                                i = 1;
                            }
                        }
                    }
                    if (i == 0) {
                        if (!(split[2].isEmpty() || split[2].equals("null"))) {
                            this.f435n = split[2];
                        }
                    }
                    if (!(i != 0 || split[3].isEmpty() || split[3].equals("null"))) {
                        try {
                            this.f436o = Long.parseLong(split[3]);
                        } catch (Throwable th2) {
                            C0757w.m457a(th2);
                            i = 1;
                        }
                    }
                } else {
                    C0757w.m456a("SecurityInfo = %s, Strings.length = %d", r3, Integer.valueOf(split.length));
                    i = 1;
                }
                if (i != 0) {
                    C0753t.m424c();
                }
            }
            return true;
        } catch (Throwable th22) {
            C0757w.m457a(th22);
            return false;
        }
    }

    protected final boolean m441b() {
        if (this.f435n == null || this.f436o == 0) {
            return false;
        }
        if (this.f436o >= System.currentTimeMillis() + this.f432k) {
            return true;
        }
        C0757w.m460c("[UploadManager] Session ID expired time from server is: %d(%s), but now is: %d(%s)", Long.valueOf(this.f436o), new Date(this.f436o).toString(), Long.valueOf(System.currentTimeMillis() + this.f432k), new Date(System.currentTimeMillis() + this.f432k).toString());
        return false;
    }

    protected final void m440b(boolean z) {
        synchronized (this.f437p) {
            C0757w.m460c("[UploadManager] Clear security context (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
            this.f433l = null;
            this.f435n = null;
            this.f436o = 0;
        }
        if (z) {
            C0753t.m424c();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void m423c(int r15) {
        /*
        r14 = this;
        r13 = 3;
        r12 = 2;
        r11 = 1;
        r2 = 0;
        if (r15 >= 0) goto L_0x000e;
    L_0x0006:
        r0 = "[UploadManager] Number of task to execute should >= 0";
        r1 = new java.lang.Object[r2];
        com.tencent.bugly.proguard.C0757w.m456a(r0, r1);
    L_0x000d:
        return;
    L_0x000e:
        r4 = com.tencent.bugly.proguard.C0756v.m449a();
        r5 = new java.util.concurrent.LinkedBlockingQueue;
        r5.<init>();
        r6 = new java.util.concurrent.LinkedBlockingQueue;
        r6.<init>();
        r7 = r14.f429h;
        monitor-enter(r7);
        r0 = "[UploadManager] Try to poll all upload task need and put them into temp queue (pid=%d | tid=%d)";
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x0057 }
        r3 = 0;
        r8 = android.os.Process.myPid();	 Catch:{ all -> 0x0057 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ all -> 0x0057 }
        r1[r3] = r8;	 Catch:{ all -> 0x0057 }
        r3 = 1;
        r8 = android.os.Process.myTid();	 Catch:{ all -> 0x0057 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ all -> 0x0057 }
        r1[r3] = r8;	 Catch:{ all -> 0x0057 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ all -> 0x0057 }
        r0 = r14.f427f;	 Catch:{ all -> 0x0057 }
        r1 = r0.size();	 Catch:{ all -> 0x0057 }
        r0 = r14.f428g;	 Catch:{ all -> 0x0057 }
        r0 = r0.size();	 Catch:{ all -> 0x0057 }
        if (r1 != 0) goto L_0x005a;
    L_0x004b:
        if (r0 != 0) goto L_0x005a;
    L_0x004d:
        r0 = "[UploadManager] There is no upload task in queue.";
        r1 = 0;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x0057 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);	 Catch:{ all -> 0x0057 }
        monitor-exit(r7);	 Catch:{ all -> 0x0057 }
        goto L_0x000d;
    L_0x0057:
        r0 = move-exception;
        monitor-exit(r7);
        throw r0;
    L_0x005a:
        if (r15 == 0) goto L_0x016b;
    L_0x005c:
        if (r15 >= r1) goto L_0x0081;
    L_0x005e:
        r0 = r2;
    L_0x005f:
        if (r4 == 0) goto L_0x0067;
    L_0x0061:
        r1 = r4.m453c();	 Catch:{ all -> 0x0057 }
        if (r1 != 0) goto L_0x0168;
    L_0x0067:
        r1 = r2;
    L_0x0068:
        r3 = r2;
    L_0x0069:
        if (r3 >= r15) goto L_0x009a;
    L_0x006b:
        r0 = r14.f427f;	 Catch:{ all -> 0x0057 }
        r0 = r0.peek();	 Catch:{ all -> 0x0057 }
        r0 = (java.lang.Runnable) r0;	 Catch:{ all -> 0x0057 }
        if (r0 == 0) goto L_0x009a;
    L_0x0075:
        r5.put(r0);	 Catch:{ Throwable -> 0x0089 }
        r0 = r14.f427f;	 Catch:{ Throwable -> 0x0089 }
        r0.poll();	 Catch:{ Throwable -> 0x0089 }
    L_0x007d:
        r0 = r3 + 1;
        r3 = r0;
        goto L_0x0069;
    L_0x0081:
        r3 = r1 + r0;
        if (r15 >= r3) goto L_0x016b;
    L_0x0085:
        r0 = r15 - r1;
        r15 = r1;
        goto L_0x005f;
    L_0x0089:
        r0 = move-exception;
        r8 = "[UploadManager] Failed to add upload task to temp urgent queue: %s";
        r9 = 1;
        r9 = new java.lang.Object[r9];	 Catch:{ all -> 0x0057 }
        r10 = 0;
        r0 = r0.getMessage();	 Catch:{ all -> 0x0057 }
        r9[r10] = r0;	 Catch:{ all -> 0x0057 }
        com.tencent.bugly.proguard.C0757w.m462e(r8, r9);	 Catch:{ all -> 0x0057 }
        goto L_0x007d;
    L_0x009a:
        r3 = r2;
    L_0x009b:
        if (r3 >= r1) goto L_0x00c4;
    L_0x009d:
        r0 = r14.f428g;	 Catch:{ all -> 0x0057 }
        r0 = r0.peek();	 Catch:{ all -> 0x0057 }
        r0 = (java.lang.Runnable) r0;	 Catch:{ all -> 0x0057 }
        if (r0 == 0) goto L_0x00c4;
    L_0x00a7:
        r6.put(r0);	 Catch:{ Throwable -> 0x00b3 }
        r0 = r14.f428g;	 Catch:{ Throwable -> 0x00b3 }
        r0.poll();	 Catch:{ Throwable -> 0x00b3 }
    L_0x00af:
        r0 = r3 + 1;
        r3 = r0;
        goto L_0x009b;
    L_0x00b3:
        r0 = move-exception;
        r8 = "[UploadManager] Failed to add upload task to temp urgent queue: %s";
        r9 = 1;
        r9 = new java.lang.Object[r9];	 Catch:{ all -> 0x0057 }
        r10 = 0;
        r0 = r0.getMessage();	 Catch:{ all -> 0x0057 }
        r9[r10] = r0;	 Catch:{ all -> 0x0057 }
        com.tencent.bugly.proguard.C0757w.m462e(r8, r9);	 Catch:{ all -> 0x0057 }
        goto L_0x00af;
    L_0x00c4:
        monitor-exit(r7);	 Catch:{ all -> 0x0057 }
        if (r15 <= 0) goto L_0x00e8;
    L_0x00c7:
        r0 = "[UploadManager] Execute urgent upload tasks of queue which has %d tasks (pid=%d | tid=%d)";
        r3 = new java.lang.Object[r13];
        r7 = java.lang.Integer.valueOf(r15);
        r3[r2] = r7;
        r7 = android.os.Process.myPid();
        r7 = java.lang.Integer.valueOf(r7);
        r3[r11] = r7;
        r7 = android.os.Process.myTid();
        r7 = java.lang.Integer.valueOf(r7);
        r3[r12] = r7;
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);
    L_0x00e8:
        r3 = r2;
    L_0x00e9:
        if (r3 >= r15) goto L_0x0139;
    L_0x00eb:
        r0 = r5.poll();
        r0 = (java.lang.Runnable) r0;
        if (r0 == 0) goto L_0x0139;
    L_0x00f3:
        r7 = r14.f429h;
        monitor-enter(r7);
        r8 = r14.f440s;	 Catch:{ all -> 0x012b }
        if (r8 < r12) goto L_0x0104;
    L_0x00fa:
        if (r4 == 0) goto L_0x0104;
    L_0x00fc:
        r4.m450a(r0);	 Catch:{ all -> 0x012b }
        monitor-exit(r7);	 Catch:{ all -> 0x012b }
    L_0x0100:
        r0 = r3 + 1;
        r3 = r0;
        goto L_0x00e9;
    L_0x0104:
        monitor-exit(r7);
        r7 = "[UploadManager] Create and start a new thread to execute a upload task: %s";
        r8 = new java.lang.Object[r11];
        r9 = "BUGLY_ASYNC_UPLOAD";
        r8[r2] = r9;
        com.tencent.bugly.proguard.C0757w.m456a(r7, r8);
        r7 = new com.tencent.bugly.proguard.t$1;
        r7.<init>(r14, r0);
        r8 = "BUGLY_ASYNC_UPLOAD";
        r7 = com.tencent.bugly.proguard.C0761y.m493a(r7, r8);
        if (r7 == 0) goto L_0x012e;
    L_0x011d:
        r7 = r14.f429h;
        monitor-enter(r7);
        r0 = r14.f440s;	 Catch:{ all -> 0x0128 }
        r0 = r0 + 1;
        r14.f440s = r0;	 Catch:{ all -> 0x0128 }
        monitor-exit(r7);	 Catch:{ all -> 0x0128 }
        goto L_0x0100;
    L_0x0128:
        r0 = move-exception;
        monitor-exit(r7);
        throw r0;
    L_0x012b:
        r0 = move-exception;
        monitor-exit(r7);
        throw r0;
    L_0x012e:
        r7 = "[UploadManager] Failed to start a thread to execute asynchronous upload task, will try again next time.";
        r8 = new java.lang.Object[r2];
        com.tencent.bugly.proguard.C0757w.m461d(r7, r8);
        r14.m420a(r0, r11);
        goto L_0x0100;
    L_0x0139:
        if (r1 <= 0) goto L_0x015c;
    L_0x013b:
        r0 = "[UploadManager] Execute upload tasks of queue which has %d tasks (pid=%d | tid=%d)";
        r3 = new java.lang.Object[r13];
        r5 = java.lang.Integer.valueOf(r1);
        r3[r2] = r5;
        r2 = android.os.Process.myPid();
        r2 = java.lang.Integer.valueOf(r2);
        r3[r11] = r2;
        r2 = android.os.Process.myTid();
        r2 = java.lang.Integer.valueOf(r2);
        r3[r12] = r2;
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);
    L_0x015c:
        if (r4 == 0) goto L_0x000d;
    L_0x015e:
        r0 = new com.tencent.bugly.proguard.t$2;
        r0.<init>(r14, r1, r6);
        r4.m450a(r0);
        goto L_0x000d;
    L_0x0168:
        r1 = r0;
        goto L_0x0068;
    L_0x016b:
        r15 = r1;
        goto L_0x005f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.proguard.t.c(int):void");
    }

    private boolean m420a(Runnable runnable, boolean z) {
        if (runnable == null) {
            C0757w.m456a("[UploadManager] Upload task should not be null", new Object[0]);
            return false;
        }
        try {
            C0757w.m460c("[UploadManager] Add upload task to queue (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
            synchronized (this.f429h) {
                if (z) {
                    this.f427f.put(runnable);
                } else {
                    this.f428g.put(runnable);
                }
            }
            return true;
        } catch (Throwable th) {
            C0757w.m462e("[UploadManager] Failed to add upload task to queue: %s", th.getMessage());
            return false;
        }
    }

    private void m417a(Runnable runnable, long j) {
        if (runnable == null) {
            C0757w.m461d("[UploadManager] Upload task should not be null", new Object[0]);
            return;
        }
        C0757w.m460c("[UploadManager] Execute synchronized upload task (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        Thread a = C0761y.m493a(runnable, "BUGLY_SYNC_UPLOAD");
        if (a == null) {
            C0757w.m462e("[UploadManager] Failed to start a thread to execute synchronized upload task, add it to queue.", new Object[0]);
            m420a(runnable, true);
            return;
        }
        try {
            a.join(j);
        } catch (Throwable th) {
            C0757w.m462e("[UploadManager] Failed to join upload synchronized task with message: %s. Add it to queue.", th.getMessage());
            m420a(runnable, true);
            m423c(0);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void m418a(java.lang.Runnable r7, boolean r8, boolean r9, long r10) {
        /*
        r6 = this;
        r5 = 2;
        r3 = 1;
        r4 = 0;
        if (r7 != 0) goto L_0x000c;
    L_0x0005:
        r0 = "[UploadManager] Upload task should not be null";
        r1 = new java.lang.Object[r4];
        com.tencent.bugly.proguard.C0757w.m461d(r0, r1);
    L_0x000c:
        r0 = "[UploadManager] Add upload task (pid=%d | tid=%d)";
        r1 = new java.lang.Object[r5];
        r2 = android.os.Process.myPid();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r4] = r2;
        r2 = android.os.Process.myTid();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r3] = r2;
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);
        r0 = r6.f435n;
        if (r0 == 0) goto L_0x0077;
    L_0x002b:
        r0 = r6.m441b();
        if (r0 == 0) goto L_0x0059;
    L_0x0031:
        r0 = "[UploadManager] Sucessfully got session ID, try to execute upload task now (pid=%d | tid=%d)";
        r1 = new java.lang.Object[r5];
        r2 = android.os.Process.myPid();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r4] = r2;
        r2 = android.os.Process.myTid();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r3] = r2;
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);
        if (r9 == 0) goto L_0x0052;
    L_0x004e:
        r6.m417a(r7, r10);
    L_0x0051:
        return;
    L_0x0052:
        r6.m420a(r7, r8);
        r6.m423c(r4);
        goto L_0x0051;
    L_0x0059:
        r0 = "[UploadManager] Session ID is expired, drop it (pid=%d | tid=%d)";
        r1 = new java.lang.Object[r5];
        r2 = android.os.Process.myPid();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r4] = r2;
        r2 = android.os.Process.myTid();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r3] = r2;
        com.tencent.bugly.proguard.C0757w.m456a(r0, r1);
        r6.m440b(r4);
    L_0x0077:
        r1 = r6.f439r;
        monitor-enter(r1);
        r0 = r6.f438q;	 Catch:{ all -> 0x0083 }
        if (r0 == 0) goto L_0x0086;
    L_0x007e:
        r6.m420a(r7, r8);	 Catch:{ all -> 0x0083 }
        monitor-exit(r1);	 Catch:{ all -> 0x0083 }
        goto L_0x0051;
    L_0x0083:
        r0 = move-exception;
        monitor-exit(r1);
        throw r0;
    L_0x0086:
        r0 = 1;
        r6.f438q = r0;	 Catch:{ all -> 0x0083 }
        monitor-exit(r1);	 Catch:{ all -> 0x0083 }
        r0 = "[UploadManager] Initialize security context now (pid=%d | tid=%d)";
        r1 = new java.lang.Object[r5];
        r2 = android.os.Process.myPid();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r4] = r2;
        r2 = android.os.Process.myTid();
        r2 = java.lang.Integer.valueOf(r2);
        r1[r3] = r2;
        com.tencent.bugly.proguard.C0757w.m460c(r0, r1);
        if (r9 == 0) goto L_0x00b7;
    L_0x00a7:
        r0 = new com.tencent.bugly.proguard.t$a;
        r2 = r6.f425d;
        r1 = r6;
        r3 = r7;
        r4 = r10;
        r0.<init>(r1, r2, r3, r4);
        r2 = 0;
        r6.m417a(r0, r2);
        goto L_0x0051;
    L_0x00b7:
        r6.m420a(r7, r8);
        r0 = new com.tencent.bugly.proguard.t$a;
        r1 = r6.f425d;
        r0.<init>(r6, r1);
        r1 = "[UploadManager] Create and start a new thread to execute a task of initializing security context: %s";
        r2 = new java.lang.Object[r3];
        r3 = "BUGLY_ASYNC_UPLOAD";
        r2[r4] = r3;
        com.tencent.bugly.proguard.C0757w.m456a(r1, r2);
        r1 = "BUGLY_ASYNC_UPLOAD";
        r1 = com.tencent.bugly.proguard.C0761y.m493a(r0, r1);
        if (r1 != 0) goto L_0x0051;
    L_0x00d4:
        r1 = "[UploadManager] Failed to start a thread to execute task of initializing security context, try to post it into thread pool.";
        r2 = new java.lang.Object[r4];
        com.tencent.bugly.proguard.C0757w.m461d(r1, r2);
        r1 = com.tencent.bugly.proguard.C0756v.m449a();
        if (r1 == 0) goto L_0x00e6;
    L_0x00e1:
        r1.m450a(r0);
        goto L_0x0051;
    L_0x00e6:
        r0 = "[UploadManager] Asynchronous thread pool is unavailable now, try next time.";
        r1 = new java.lang.Object[r4];
        com.tencent.bugly.proguard.C0757w.m462e(r0, r1);
        r1 = r6.f439r;
        monitor-enter(r1);
        r0 = 0;
        r6.f438q = r0;	 Catch:{ all -> 0x00f6 }
        monitor-exit(r1);	 Catch:{ all -> 0x00f6 }
        goto L_0x0051;
    L_0x00f6:
        r0 = move-exception;
        monitor-exit(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.proguard.t.a(java.lang.Runnable, boolean, boolean, long):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void m436a(int r9, com.tencent.bugly.proguard.am r10) {
        /*
        r8 = this;
        r4 = 2;
        r1 = 1;
        r2 = 0;
        r0 = r8.f423a;
        if (r0 != 0) goto L_0x0008;
    L_0x0007:
        return;
    L_0x0008:
        if (r9 != r4) goto L_0x003e;
    L_0x000a:
        r0 = "[UploadManager] Session ID is invalid, will clear security context (pid=%d | tid=%d)";
        r3 = new java.lang.Object[r4];
        r4 = android.os.Process.myPid();
        r4 = java.lang.Integer.valueOf(r4);
        r3[r2] = r4;
        r2 = android.os.Process.myTid();
        r2 = java.lang.Integer.valueOf(r2);
        r3[r1] = r2;
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);
        r8.m440b(r1);
    L_0x0028:
        r1 = r8.f439r;
        monitor-enter(r1);
        r0 = r8.f438q;	 Catch:{ all -> 0x003b }
        if (r0 == 0) goto L_0x0039;
    L_0x002f:
        r0 = 0;
        r8.f438q = r0;	 Catch:{ all -> 0x003b }
        r0 = r8.f425d;	 Catch:{ all -> 0x003b }
        r2 = "security_info";
        com.tencent.bugly.proguard.C0761y.m516b(r0, r2);	 Catch:{ all -> 0x003b }
    L_0x0039:
        monitor-exit(r1);	 Catch:{ all -> 0x003b }
        goto L_0x0007;
    L_0x003b:
        r0 = move-exception;
        monitor-exit(r1);
        throw r0;
    L_0x003e:
        r3 = r8.f439r;
        monitor-enter(r3);
        r0 = r8.f438q;	 Catch:{ all -> 0x0047 }
        if (r0 != 0) goto L_0x004a;
    L_0x0045:
        monitor-exit(r3);	 Catch:{ all -> 0x0047 }
        goto L_0x0007;
    L_0x0047:
        r0 = move-exception;
        monitor-exit(r3);
        throw r0;
    L_0x004a:
        monitor-exit(r3);
        if (r10 == 0) goto L_0x012f;
    L_0x004d:
        r0 = "[UploadManager] Record security context (pid=%d | tid=%d)";
        r3 = new java.lang.Object[r4];
        r4 = android.os.Process.myPid();
        r4 = java.lang.Integer.valueOf(r4);
        r3[r2] = r4;
        r4 = android.os.Process.myTid();
        r4 = java.lang.Integer.valueOf(r4);
        r3[r1] = r4;
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);
        r3 = r10.f640g;	 Catch:{ Throwable -> 0x0118 }
        if (r3 == 0) goto L_0x0102;
    L_0x006c:
        r0 = "S1";
        r0 = r3.containsKey(r0);	 Catch:{ Throwable -> 0x0118 }
        if (r0 == 0) goto L_0x0102;
    L_0x0074:
        r0 = "S2";
        r0 = r3.containsKey(r0);	 Catch:{ Throwable -> 0x0118 }
        if (r0 == 0) goto L_0x0102;
    L_0x007c:
        r4 = r10.f638e;	 Catch:{ Throwable -> 0x0118 }
        r6 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0118 }
        r4 = r4 - r6;
        r8.f432k = r4;	 Catch:{ Throwable -> 0x0118 }
        r0 = "[UploadManager] Time lag of server is: %d";
        r4 = 1;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0118 }
        r5 = 0;
        r6 = r8.f432k;	 Catch:{ Throwable -> 0x0118 }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Throwable -> 0x0118 }
        r4[r5] = r6;	 Catch:{ Throwable -> 0x0118 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r4);	 Catch:{ Throwable -> 0x0118 }
        r0 = "S1";
        r0 = r3.get(r0);	 Catch:{ Throwable -> 0x0118 }
        r0 = (java.lang.String) r0;	 Catch:{ Throwable -> 0x0118 }
        r8.f435n = r0;	 Catch:{ Throwable -> 0x0118 }
        r0 = "[UploadManager] Session ID from server is: %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x0118 }
        r5 = 0;
        r6 = r8.f435n;	 Catch:{ Throwable -> 0x0118 }
        r4[r5] = r6;	 Catch:{ Throwable -> 0x0118 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r4);	 Catch:{ Throwable -> 0x0118 }
        r0 = r8.f435n;	 Catch:{ Throwable -> 0x0118 }
        r0 = r0.length();	 Catch:{ Throwable -> 0x0118 }
        if (r0 <= 0) goto L_0x0126;
    L_0x00b5:
        r0 = "S2";
        r0 = r3.get(r0);	 Catch:{ NumberFormatException -> 0x0109 }
        r0 = (java.lang.String) r0;	 Catch:{ NumberFormatException -> 0x0109 }
        r4 = java.lang.Long.parseLong(r0);	 Catch:{ NumberFormatException -> 0x0109 }
        r8.f436o = r4;	 Catch:{ NumberFormatException -> 0x0109 }
        r0 = "[UploadManager] Session expired time from server is: %d(%s)";
        r3 = 2;
        r3 = new java.lang.Object[r3];	 Catch:{ NumberFormatException -> 0x0109 }
        r4 = 0;
        r6 = r8.f436o;	 Catch:{ NumberFormatException -> 0x0109 }
        r5 = java.lang.Long.valueOf(r6);	 Catch:{ NumberFormatException -> 0x0109 }
        r3[r4] = r5;	 Catch:{ NumberFormatException -> 0x0109 }
        r4 = 1;
        r5 = new java.util.Date;	 Catch:{ NumberFormatException -> 0x0109 }
        r6 = r8.f436o;	 Catch:{ NumberFormatException -> 0x0109 }
        r5.<init>(r6);	 Catch:{ NumberFormatException -> 0x0109 }
        r5 = r5.toString();	 Catch:{ NumberFormatException -> 0x0109 }
        r3[r4] = r5;	 Catch:{ NumberFormatException -> 0x0109 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);	 Catch:{ NumberFormatException -> 0x0109 }
        r4 = r8.f436o;	 Catch:{ NumberFormatException -> 0x0109 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 >= 0) goto L_0x00f7;
    L_0x00ea:
        r0 = "[UploadManager] Session expired time from server is less than 1 second, will set to default value";
        r3 = 0;
        r3 = new java.lang.Object[r3];	 Catch:{ NumberFormatException -> 0x0109 }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r3);	 Catch:{ NumberFormatException -> 0x0109 }
        r4 = 259200000; // 0xf731400 float:1.1984677E-29 double:1.280618154E-315;
        r8.f436o = r4;	 Catch:{ NumberFormatException -> 0x0109 }
    L_0x00f7:
        r0 = r8.m427d();	 Catch:{ Throwable -> 0x0118 }
        if (r0 == 0) goto L_0x011d;
    L_0x00fd:
        r1 = r2;
    L_0x00fe:
        r0 = 0;
        r8.m423c(r0);	 Catch:{ Throwable -> 0x0118 }
    L_0x0102:
        if (r1 == 0) goto L_0x0028;
    L_0x0104:
        r8.m440b(r2);
        goto L_0x0028;
    L_0x0109:
        r0 = move-exception;
        r0 = "[UploadManager] Session expired time is invalid, will set to default value";
        r3 = 0;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0118 }
        com.tencent.bugly.proguard.C0757w.m461d(r0, r3);	 Catch:{ Throwable -> 0x0118 }
        r4 = 259200000; // 0xf731400 float:1.1984677E-29 double:1.280618154E-315;
        r8.f436o = r4;	 Catch:{ Throwable -> 0x0118 }
        goto L_0x00f7;
    L_0x0118:
        r0 = move-exception;
        com.tencent.bugly.proguard.C0757w.m457a(r0);
        goto L_0x0102;
    L_0x011d:
        r0 = "[UploadManager] Failed to record database";
        r3 = 0;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0118 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);	 Catch:{ Throwable -> 0x0118 }
        goto L_0x00fe;
    L_0x0126:
        r0 = "[UploadManager] Session ID from server is invalid, try next time";
        r3 = 0;
        r3 = new java.lang.Object[r3];	 Catch:{ Throwable -> 0x0118 }
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);	 Catch:{ Throwable -> 0x0118 }
        goto L_0x0102;
    L_0x012f:
        r0 = "[UploadManager] Fail to init security context and clear local info (pid=%d | tid=%d)";
        r3 = new java.lang.Object[r4];
        r4 = android.os.Process.myPid();
        r4 = java.lang.Integer.valueOf(r4);
        r3[r2] = r4;
        r4 = android.os.Process.myTid();
        r4 = java.lang.Integer.valueOf(r4);
        r3[r1] = r4;
        com.tencent.bugly.proguard.C0757w.m460c(r0, r3);
        r8.m440b(r2);
        goto L_0x0028;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.proguard.t.a(int, com.tencent.bugly.proguard.am):void");
    }

    public final byte[] m439a(byte[] bArr) {
        if (this.f433l != null && (this.f433l.length << 3) == 128) {
            return C0761y.m503a(1, bArr, this.f433l);
        }
        C0757w.m461d("[UploadManager] AES key is invalid (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        return null;
    }

    public final byte[] m443b(byte[] bArr) {
        if (this.f433l != null && (this.f433l.length << 3) == 128) {
            return C0761y.m503a(2, bArr, this.f433l);
        }
        C0757w.m461d("[UploadManager] AES key is invalid (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        return null;
    }

    public final boolean m438a(Map<String, String> map) {
        if (map == null) {
            return false;
        }
        C0757w.m460c("[UploadManager] Integrate security to HTTP headers (pid=%d | tid=%d)", Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        if (this.f435n != null) {
            map.put("secureSessionId", this.f435n);
            return true;
        } else if (this.f433l == null || (this.f433l.length << 3) != 128) {
            C0757w.m461d("[UploadManager] AES key is invalid", new Object[0]);
            return false;
        } else {
            if (this.f431j == null) {
                this.f431j = Base64.decode(this.f430i, 0);
                if (this.f431j == null) {
                    C0757w.m461d("[UploadManager] Failed to decode RSA public key", new Object[0]);
                    return false;
                }
            }
            byte[] b = C0761y.m517b(1, this.f433l, this.f431j);
            if (b == null) {
                C0757w.m461d("[UploadManager] Failed to encrypt AES key", new Object[0]);
                return false;
            }
            String encodeToString = Base64.encodeToString(b, 0);
            if (encodeToString == null) {
                C0757w.m461d("[UploadManager] Failed to encode AES key", new Object[0]);
                return false;
            }
            map.put("raKey", encodeToString);
            return true;
        }
    }
}
