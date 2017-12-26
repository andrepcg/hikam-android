package com.tencent.bugly.proguard;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.tencent.bugly.crashreport.common.info.C0705a;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* compiled from: BUGLY */
public final class C0742m {
    public static final long f374a = System.currentTimeMillis();
    private static C0742m f375b = null;
    private Context f376c;
    private String f377d = C0705a.m85b().f131d;
    private Map<Integer, Map<String, C0739l>> f378e = new HashMap();
    private SharedPreferences f379f;

    private C0742m(Context context) {
        this.f376c = context;
        this.f379f = context.getSharedPreferences("crashrecord", 0);
    }

    public static synchronized C0742m m357a(Context context) {
        C0742m c0742m;
        synchronized (C0742m.class) {
            if (f375b == null) {
                f375b = new C0742m(context);
            }
            c0742m = f375b;
        }
        return c0742m;
    }

    public static synchronized C0742m m356a() {
        C0742m c0742m;
        synchronized (C0742m.class) {
            c0742m = f375b;
        }
        return c0742m;
    }

    private synchronized boolean m363b(int i) {
        boolean z;
        try {
            List<C0739l> c = m366c(i);
            if (c == null) {
                z = false;
            } else {
                long currentTimeMillis = System.currentTimeMillis();
                List arrayList = new ArrayList();
                Collection arrayList2 = new ArrayList();
                for (C0739l c0739l : c) {
                    if (c0739l.f363b != null && c0739l.f363b.equalsIgnoreCase(this.f377d) && c0739l.f365d > 0) {
                        arrayList.add(c0739l);
                    }
                    if (c0739l.f364c + 86400000 < currentTimeMillis) {
                        arrayList2.add(c0739l);
                    }
                }
                Collections.sort(arrayList);
                if (arrayList.size() < 2) {
                    c.removeAll(arrayList2);
                    m360a(i, (List) c);
                    z = false;
                } else if (arrayList.size() <= 0 || ((C0739l) arrayList.get(arrayList.size() - 1)).f364c + 86400000 >= currentTimeMillis) {
                    z = true;
                } else {
                    c.clear();
                    m360a(i, (List) c);
                    z = false;
                }
            }
        } catch (Exception e) {
            C0757w.m462e("isFrequentCrash failed", new Object[0]);
            z = false;
        }
        return z;
    }

    public final synchronized void m367a(int i, final int i2) {
        C0756v.m449a().m450a(new Runnable(this, 1004) {
            private /* synthetic */ C0742m f371c;

            public final void run() {
                try {
                    if (!TextUtils.isEmpty(this.f371c.f377d)) {
                        C0739l c0739l;
                        C0739l c0739l2;
                        List a = this.f371c.m366c(1004);
                        List arrayList;
                        if (a == null) {
                            arrayList = new ArrayList();
                        } else {
                            arrayList = a;
                        }
                        if (this.f371c.f378e.get(Integer.valueOf(1004)) == null) {
                            this.f371c.f378e.put(Integer.valueOf(1004), new HashMap());
                        }
                        if (((Map) this.f371c.f378e.get(Integer.valueOf(1004))).get(this.f371c.f377d) == null) {
                            C0739l c0739l3 = new C0739l();
                            c0739l3.f362a = (long) 1004;
                            c0739l3.f368g = C0742m.f374a;
                            c0739l3.f363b = this.f371c.f377d;
                            c0739l3.f367f = C0705a.m85b().f137j;
                            C0705a.m85b().getClass();
                            c0739l3.f366e = "2.4.0";
                            c0739l3.f364c = System.currentTimeMillis();
                            c0739l3.f365d = i2;
                            ((Map) this.f371c.f378e.get(Integer.valueOf(1004))).put(this.f371c.f377d, c0739l3);
                            c0739l = c0739l3;
                        } else {
                            c0739l2 = (C0739l) ((Map) this.f371c.f378e.get(Integer.valueOf(1004))).get(this.f371c.f377d);
                            c0739l2.f365d = i2;
                            c0739l = c0739l2;
                        }
                        Collection arrayList2 = new ArrayList();
                        int i = 0;
                        for (C0739l c0739l22 : r4) {
                            if (c0739l22.f368g == c0739l.f368g && c0739l22.f363b != null && c0739l22.f363b.equalsIgnoreCase(c0739l.f363b)) {
                                i = 1;
                                c0739l22.f365d = c0739l.f365d;
                            }
                            if ((c0739l22.f366e != null && !c0739l22.f366e.equalsIgnoreCase(c0739l.f366e)) || ((c0739l22.f367f != null && !c0739l22.f367f.equalsIgnoreCase(c0739l.f367f)) || c0739l22.f365d <= 0)) {
                                arrayList2.add(c0739l22);
                            }
                        }
                        r4.removeAll(arrayList2);
                        if (i == 0) {
                            r4.add(c0739l);
                        }
                        this.f371c.m360a(1004, (List) r4);
                    }
                } catch (Exception e) {
                    C0757w.m462e("saveCrashRecord failed", new Object[0]);
                }
            }
        });
    }

    private synchronized <T extends List<?>> T m366c(int i) {
        ObjectInputStream objectInputStream;
        ObjectInputStream objectInputStream2;
        T t;
        Throwable th;
        try {
            File file = new File(this.f376c.getDir("crashrecord", 0), i);
            if (file.exists()) {
                try {
                    objectInputStream = new ObjectInputStream(new FileInputStream(file));
                } catch (IOException e) {
                    objectInputStream2 = null;
                    try {
                        C0757w.m456a("open record file error", new Object[0]);
                        if (objectInputStream2 != null) {
                            objectInputStream2.close();
                        }
                        t = null;
                        return t;
                    } catch (Throwable th2) {
                        Throwable th3 = th2;
                        objectInputStream = objectInputStream2;
                        th = th3;
                        if (objectInputStream != null) {
                            objectInputStream.close();
                        }
                        throw th;
                    }
                } catch (ClassNotFoundException e2) {
                    objectInputStream = null;
                    try {
                        C0757w.m456a("get object error", new Object[0]);
                        if (objectInputStream != null) {
                            objectInputStream.close();
                        }
                        t = null;
                        return t;
                    } catch (Throwable th4) {
                        th = th4;
                        if (objectInputStream != null) {
                            objectInputStream.close();
                        }
                        throw th;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    objectInputStream = null;
                    if (objectInputStream != null) {
                        objectInputStream.close();
                    }
                    throw th;
                }
                try {
                    List list = (List) objectInputStream.readObject();
                    objectInputStream.close();
                } catch (IOException e3) {
                    objectInputStream2 = objectInputStream;
                    C0757w.m456a("open record file error", new Object[0]);
                    if (objectInputStream2 != null) {
                        objectInputStream2.close();
                    }
                    t = null;
                    return t;
                } catch (ClassNotFoundException e4) {
                    C0757w.m456a("get object error", new Object[0]);
                    if (objectInputStream != null) {
                        objectInputStream.close();
                    }
                    t = null;
                    return t;
                }
            }
            t = null;
        } catch (Exception e5) {
            C0757w.m462e("readCrashRecord error", new Object[0]);
        }
        return t;
    }

    private synchronized <T extends List<?>> void m360a(int i, T t) {
        IOException e;
        Throwable th;
        if (t != null) {
            try {
                ObjectOutputStream objectOutputStream;
                try {
                    objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(this.f376c.getDir("crashrecord", 0), i)));
                    try {
                        objectOutputStream.writeObject(t);
                        objectOutputStream.close();
                    } catch (IOException e2) {
                        e = e2;
                        try {
                            e.printStackTrace();
                            C0757w.m456a("open record file error", new Object[0]);
                            if (objectOutputStream != null) {
                                objectOutputStream.close();
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            if (objectOutputStream != null) {
                                objectOutputStream.close();
                            }
                            throw th;
                        }
                    }
                } catch (IOException e3) {
                    e = e3;
                    objectOutputStream = null;
                    e.printStackTrace();
                    C0757w.m456a("open record file error", new Object[0]);
                    if (objectOutputStream != null) {
                        objectOutputStream.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    objectOutputStream = null;
                    if (objectOutputStream != null) {
                        objectOutputStream.close();
                    }
                    throw th;
                }
            } catch (Exception e4) {
                C0757w.m462e("writeCrashRecord error", new Object[0]);
            }
        }
    }

    public final synchronized boolean m368a(final int i) {
        boolean z = true;
        synchronized (this) {
            try {
                z = this.f379f.getBoolean(i + "_" + this.f377d, true);
                C0756v.m449a().m450a(new Runnable(this) {
                    private /* synthetic */ C0742m f373b;

                    public final void run() {
                        this.f373b.f379f.edit().putBoolean(i + "_" + this.f373b.f377d, !this.f373b.m363b(i)).commit();
                    }
                });
            } catch (Exception e) {
                C0757w.m462e("canInit error", new Object[0]);
            }
        }
        return z;
    }
}
