package com.tencent.bugly.proguard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tencent.bugly.C0692a;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* compiled from: BUGLY */
public final class C0745o {
    private static C0745o f398a = null;
    private static C0746p f399b = null;
    private static boolean f400c = false;

    /* compiled from: BUGLY */
    class C0744a extends Thread {
        private int f380a;
        private C0743n f381b;
        private String f382c;
        private ContentValues f383d;
        private boolean f384e;
        private String[] f385f;
        private String f386g;
        private String[] f387h;
        private String f388i;
        private String f389j;
        private String f390k;
        private String f391l;
        private String f392m;
        private String[] f393n;
        private int f394o;
        private String f395p;
        private byte[] f396q;
        private /* synthetic */ C0745o f397r;

        public C0744a(C0745o c0745o, int i, C0743n c0743n) {
            this.f397r = c0745o;
            this.f380a = i;
            this.f381b = c0743n;
        }

        public final void m373a(boolean z, String str, String[] strArr, String str2, String[] strArr2, String str3, String str4, String str5, String str6) {
            this.f384e = z;
            this.f382c = str;
            this.f385f = strArr;
            this.f386g = str2;
            this.f387h = strArr2;
            this.f388i = str3;
            this.f389j = str4;
            this.f390k = str5;
            this.f391l = str6;
        }

        public final void m372a(int i, String str, byte[] bArr) {
            this.f394o = i;
            this.f395p = str;
            this.f396q = bArr;
        }

        public final void run() {
            switch (this.f380a) {
                case 1:
                    this.f397r.m377a(this.f382c, this.f383d, this.f381b);
                    return;
                case 2:
                    this.f397r.m375a(this.f382c, this.f392m, this.f393n, this.f381b);
                    return;
                case 3:
                    this.f397r.m379a(this.f384e, this.f382c, this.f385f, this.f386g, this.f387h, this.f388i, this.f389j, this.f390k, this.f391l, this.f381b);
                    return;
                case 4:
                    this.f397r.m386a(this.f394o, this.f395p, this.f396q, this.f381b);
                    return;
                case 5:
                    this.f397r.m383a(this.f394o, this.f381b);
                    return;
                case 6:
                    this.f397r.m385a(this.f394o, this.f395p, this.f381b);
                    return;
                default:
                    return;
            }
        }
    }

    private C0745o(Context context, List<C0692a> list) {
        f399b = new C0746p(context, list);
    }

    public static synchronized C0745o m381a(Context context, List<C0692a> list) {
        C0745o c0745o;
        synchronized (C0745o.class) {
            if (f398a == null) {
                f398a = new C0745o(context, list);
            }
            c0745o = f398a;
        }
        return c0745o;
    }

    public static synchronized C0745o m380a() {
        C0745o c0745o;
        synchronized (C0745o.class) {
            c0745o = f398a;
        }
        return c0745o;
    }

    public final long m395a(String str, ContentValues contentValues, C0743n c0743n, boolean z) {
        return m377a(str, contentValues, null);
    }

    public final Cursor m396a(String str, String[] strArr, String str2, String[] strArr2, C0743n c0743n, boolean z) {
        return m379a(false, str, strArr, str2, null, null, null, null, null, null);
    }

    public final int m394a(String str, String str2, String[] strArr, C0743n c0743n, boolean z) {
        return m375a(str, str2, null, null);
    }

    private synchronized long m377a(String str, ContentValues contentValues, C0743n c0743n) {
        long j = 0;
        synchronized (this) {
            try {
                SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
                if (!(writableDatabase == null || contentValues == null)) {
                    long replace = writableDatabase.replace(str, "_id", contentValues);
                    if (replace >= 0) {
                        C0757w.m460c("[Database] insert %s success.", str);
                    } else {
                        C0757w.m461d("[Database] replace %s error.", str);
                    }
                    j = replace;
                }
                if (c0743n != null) {
                    Long.valueOf(j);
                }
            } catch (Throwable th) {
                if (c0743n != null) {
                    Long.valueOf(0);
                }
            }
        }
        return j;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized android.database.Cursor m379a(boolean r12, java.lang.String r13, java.lang.String[] r14, java.lang.String r15, java.lang.String[] r16, java.lang.String r17, java.lang.String r18, java.lang.String r19, java.lang.String r20, com.tencent.bugly.proguard.C0743n r21) {
        /*
        r11 = this;
        monitor-enter(r11);
        r10 = 0;
        r0 = f399b;	 Catch:{ Throwable -> 0x0020 }
        r0 = r0.getWritableDatabase();	 Catch:{ Throwable -> 0x0020 }
        if (r0 == 0) goto L_0x0035;
    L_0x000a:
        r1 = r12;
        r2 = r13;
        r3 = r14;
        r4 = r15;
        r5 = r16;
        r6 = r17;
        r7 = r18;
        r8 = r19;
        r9 = r20;
        r0 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0020 }
    L_0x001c:
        if (r21 == 0) goto L_0x001e;
    L_0x001e:
        monitor-exit(r11);
        return r0;
    L_0x0020:
        r0 = move-exception;
        r1 = com.tencent.bugly.proguard.C0757w.m457a(r0);	 Catch:{ all -> 0x002e }
        if (r1 != 0) goto L_0x002a;
    L_0x0027:
        r0.printStackTrace();	 Catch:{ all -> 0x002e }
    L_0x002a:
        if (r21 == 0) goto L_0x0033;
    L_0x002c:
        r0 = r10;
        goto L_0x001e;
    L_0x002e:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x0030 }
    L_0x0030:
        r0 = move-exception;
        monitor-exit(r11);
        throw r0;
    L_0x0033:
        r0 = r10;
        goto L_0x001e;
    L_0x0035:
        r0 = r10;
        goto L_0x001c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.proguard.o.a(boolean, java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.tencent.bugly.proguard.n):android.database.Cursor");
    }

    private synchronized int m375a(String str, String str2, String[] strArr, C0743n c0743n) {
        int i = 0;
        synchronized (this) {
            try {
                SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
                if (writableDatabase != null) {
                    i = writableDatabase.delete(str, str2, strArr);
                }
                if (c0743n != null) {
                    Integer.valueOf(i);
                }
            } catch (Throwable th) {
                if (c0743n != null) {
                    Integer.valueOf(0);
                }
            }
        }
        return i;
    }

    public final boolean m401a(int i, String str, byte[] bArr, C0743n c0743n, boolean z) {
        if (z) {
            return m386a(i, str, bArr, null);
        }
        Runnable c0744a = new C0744a(this, 4, null);
        c0744a.m372a(i, str, bArr);
        C0756v.m449a().m450a(c0744a);
        return true;
    }

    public final Map<String, byte[]> m398a(int i, C0743n c0743n, boolean z) {
        return m383a(i, null);
    }

    public final boolean m400a(int i, String str, C0743n c0743n, boolean z) {
        return m385a(555, str, null);
    }

    private boolean m386a(int i, String str, byte[] bArr, C0743n c0743n) {
        boolean z = false;
        try {
            C0747q c0747q = new C0747q();
            c0747q.f404a = (long) i;
            c0747q.f409f = str;
            c0747q.f408e = System.currentTimeMillis();
            c0747q.f410g = bArr;
            z = m390b(c0747q);
            if (c0743n != null) {
                Boolean.valueOf(z);
            }
        } catch (Throwable th) {
            if (c0743n != null) {
                Boolean.valueOf(z);
            }
        }
        return z;
    }

    private Map<String, byte[]> m383a(int i, C0743n c0743n) {
        Map<String, byte[]> map;
        Throwable th;
        try {
            List<C0747q> c = m392c(i);
            Map<String, byte[]> hashMap = new HashMap();
            try {
                for (C0747q c0747q : c) {
                    Object obj = c0747q.f410g;
                    if (obj != null) {
                        hashMap.put(c0747q.f409f, obj);
                    }
                }
                if (c0743n != null) {
                    return hashMap;
                }
                return hashMap;
            } catch (Throwable th2) {
                Throwable th3 = th2;
                map = hashMap;
                th = th3;
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
                return c0743n == null ? map : map;
            }
        } catch (Throwable th22) {
            th = th22;
            map = null;
            if (C0757w.m457a(th)) {
                th.printStackTrace();
            }
            if (c0743n == null) {
            }
        }
    }

    public final synchronized boolean m402a(C0747q c0747q) {
        boolean z = false;
        synchronized (this) {
            if (c0747q != null) {
                try {
                    SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
                    if (writableDatabase != null) {
                        ContentValues c = C0745o.m391c(c0747q);
                        if (c != null) {
                            long replace = writableDatabase.replace("t_lr", "_id", c);
                            if (replace >= 0) {
                                C0757w.m460c("[Database] insert %s success.", "t_lr");
                                c0747q.f404a = replace;
                                z = true;
                            }
                        }
                    }
                } catch (Throwable th) {
                    if (!C0757w.m457a(th)) {
                        th.printStackTrace();
                    }
                }
            }
        }
        return z;
    }

    private synchronized boolean m390b(C0747q c0747q) {
        boolean z = false;
        synchronized (this) {
            if (c0747q != null) {
                try {
                    SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
                    if (writableDatabase != null) {
                        ContentValues d = C0745o.m393d(c0747q);
                        if (d != null) {
                            long replace = writableDatabase.replace("t_pf", "_id", d);
                            if (replace >= 0) {
                                C0757w.m460c("[Database] insert %s success.", "t_pf");
                                c0747q.f404a = replace;
                                z = true;
                            }
                        }
                    }
                } catch (Throwable th) {
                    if (!C0757w.m457a(th)) {
                        th.printStackTrace();
                    }
                }
            }
        }
        return z;
    }

    public final synchronized List<C0747q> m397a(int i) {
        Throwable th;
        Cursor cursor;
        List<C0747q> list;
        SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
        if (writableDatabase != null) {
            String str;
            Cursor cursor2;
            if (i >= 0) {
                try {
                    str = "_tp = " + i;
                } catch (Throwable th2) {
                    th = th2;
                    cursor2 = null;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            }
            str = null;
            cursor2 = writableDatabase.query("t_lr", null, str, null, null, null, null);
            if (cursor2 == null) {
                if (cursor2 != null) {
                    cursor2.close();
                }
                list = null;
            } else {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    List<C0747q> arrayList = new ArrayList();
                    while (cursor2.moveToNext()) {
                        C0747q a = C0745o.m382a(cursor2);
                        if (a != null) {
                            arrayList.add(a);
                        } else {
                            try {
                                stringBuilder.append(" or _id").append(" = ").append(cursor2.getLong(cursor2.getColumnIndex("_id")));
                            } catch (Throwable th3) {
                                th = th3;
                            }
                        }
                    }
                    str = stringBuilder.toString();
                    if (str.length() > 0) {
                        int delete = writableDatabase.delete("t_lr", str.substring(4), null);
                        C0757w.m461d("[Database] deleted %s illegal data %d", "t_lr", Integer.valueOf(delete));
                    }
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    list = arrayList;
                } catch (Throwable th32) {
                    th = th32;
                }
            }
        }
        list = null;
        return list;
    }

    public final synchronized void m399a(List<C0747q> list) {
        if (list != null) {
            if (list.size() != 0) {
                SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
                if (writableDatabase != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (C0747q c0747q : list) {
                        stringBuilder.append(" or _id").append(" = ").append(c0747q.f404a);
                    }
                    String stringBuilder2 = stringBuilder.toString();
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2 = stringBuilder2.substring(4);
                    }
                    stringBuilder.setLength(0);
                    try {
                        int delete = writableDatabase.delete("t_lr", stringBuilder2, null);
                        C0757w.m460c("[Database] deleted %s data %d", "t_lr", Integer.valueOf(delete));
                    } catch (Throwable th) {
                        if (!C0757w.m457a(th)) {
                            th.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public final synchronized void m403b(int i) {
        String str = null;
        synchronized (this) {
            SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
            if (writableDatabase != null) {
                if (i >= 0) {
                    try {
                        str = "_tp = " + i;
                    } catch (Throwable th) {
                        if (!C0757w.m457a(th)) {
                            th.printStackTrace();
                        }
                    }
                }
                int delete = writableDatabase.delete("t_lr", str, null);
                C0757w.m460c("[Database] deleted %s data %d", "t_lr", Integer.valueOf(delete));
            }
        }
    }

    private static ContentValues m391c(C0747q c0747q) {
        if (c0747q == null) {
            return null;
        }
        try {
            ContentValues contentValues = new ContentValues();
            if (c0747q.f404a > 0) {
                contentValues.put("_id", Long.valueOf(c0747q.f404a));
            }
            contentValues.put("_tp", Integer.valueOf(c0747q.f405b));
            contentValues.put("_pc", c0747q.f406c);
            contentValues.put("_th", c0747q.f407d);
            contentValues.put("_tm", Long.valueOf(c0747q.f408e));
            if (c0747q.f410g != null) {
                contentValues.put("_dt", c0747q.f410g);
            }
            return contentValues;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    private static C0747q m382a(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        try {
            C0747q c0747q = new C0747q();
            c0747q.f404a = cursor.getLong(cursor.getColumnIndex("_id"));
            c0747q.f405b = cursor.getInt(cursor.getColumnIndex("_tp"));
            c0747q.f406c = cursor.getString(cursor.getColumnIndex("_pc"));
            c0747q.f407d = cursor.getString(cursor.getColumnIndex("_th"));
            c0747q.f408e = cursor.getLong(cursor.getColumnIndex("_tm"));
            c0747q.f410g = cursor.getBlob(cursor.getColumnIndex("_dt"));
            return c0747q;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    private synchronized List<C0747q> m392c(int i) {
        Cursor query;
        List<C0747q> list;
        Throwable th;
        Cursor cursor;
        try {
            SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
            if (writableDatabase != null) {
                String str = "_id = " + i;
                query = writableDatabase.query("t_pf", null, str, null, null, null, null);
                if (query == null) {
                    if (query != null) {
                        query.close();
                    }
                    list = null;
                } else {
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        List<C0747q> arrayList = new ArrayList();
                        while (query.moveToNext()) {
                            C0747q b = C0745o.m389b(query);
                            if (b != null) {
                                arrayList.add(b);
                            } else {
                                try {
                                    stringBuilder.append(" or _tp").append(" = ").append(query.getString(query.getColumnIndex("_tp")));
                                } catch (Throwable th2) {
                                    th = th2;
                                }
                            }
                        }
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(" and _id").append(" = ").append(i);
                            int delete = writableDatabase.delete("t_pf", str.substring(4), null);
                            C0757w.m461d("[Database] deleted %s illegal data %d.", "t_pf", Integer.valueOf(delete));
                        }
                        if (query != null) {
                            query.close();
                        }
                        list = arrayList;
                    } catch (Throwable th22) {
                        th = th22;
                    }
                }
            }
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
        list = null;
        return list;
    }

    private synchronized boolean m385a(int i, String str, C0743n c0743n) {
        boolean z = true;
        boolean z2 = false;
        synchronized (this) {
            try {
                SQLiteDatabase writableDatabase = f399b.getWritableDatabase();
                if (writableDatabase != null) {
                    String str2;
                    if (C0761y.m501a(str)) {
                        str2 = "_id = " + i;
                    } else {
                        str2 = "_id = " + i + " and _tp" + " = \"" + str + "\"";
                    }
                    C0757w.m460c("[Database] deleted %s data %d", "t_pf", Integer.valueOf(writableDatabase.delete("t_pf", str2, null)));
                    if (writableDatabase.delete("t_pf", str2, null) <= 0) {
                        z = false;
                    }
                    z2 = z;
                }
                if (c0743n != null) {
                    Boolean.valueOf(z2);
                }
            } catch (Throwable th) {
                if (c0743n != null) {
                    Boolean.valueOf(false);
                }
            }
        }
        return z2;
    }

    private static ContentValues m393d(C0747q c0747q) {
        if (c0747q == null || C0761y.m501a(c0747q.f409f)) {
            return null;
        }
        try {
            ContentValues contentValues = new ContentValues();
            if (c0747q.f404a > 0) {
                contentValues.put("_id", Long.valueOf(c0747q.f404a));
            }
            contentValues.put("_tp", c0747q.f409f);
            contentValues.put("_tm", Long.valueOf(c0747q.f408e));
            if (c0747q.f410g == null) {
                return contentValues;
            }
            contentValues.put("_dt", c0747q.f410g);
            return contentValues;
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    private static C0747q m389b(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        try {
            C0747q c0747q = new C0747q();
            c0747q.f404a = cursor.getLong(cursor.getColumnIndex("_id"));
            c0747q.f408e = cursor.getLong(cursor.getColumnIndex("_tm"));
            c0747q.f409f = cursor.getString(cursor.getColumnIndex("_tp"));
            c0747q.f410g = cursor.getBlob(cursor.getColumnIndex("_dt"));
            return c0747q;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }
}
