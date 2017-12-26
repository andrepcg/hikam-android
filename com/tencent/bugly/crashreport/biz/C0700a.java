package com.tencent.bugly.crashreport.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v4.view.PointerIconCompat;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.strategy.C0709a;
import com.tencent.bugly.crashreport.common.strategy.StrategyBean;
import com.tencent.bugly.proguard.C0730a;
import com.tencent.bugly.proguard.C0737j;
import com.tencent.bugly.proguard.C0745o;
import com.tencent.bugly.proguard.C0749s;
import com.tencent.bugly.proguard.C0753t;
import com.tencent.bugly.proguard.C0756v;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import com.tencent.bugly.proguard.al;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* compiled from: BUGLY */
public final class C0700a {
    private Context f79a;
    private long f80b;
    private int f81c;
    private boolean f82d = true;

    /* compiled from: BUGLY */
    class C06962 implements Runnable {
        private /* synthetic */ C0700a f72a;

        C06962(C0700a c0700a) {
            this.f72a = c0700a;
        }

        public final void run() {
            try {
                this.f72a.m47c();
            } catch (Throwable th) {
                C0757w.m457a(th);
            }
        }
    }

    /* compiled from: BUGLY */
    class C0697a implements Runnable {
        private boolean f73a;
        private UserInfoBean f74b;
        private /* synthetic */ C0700a f75c;

        public C0697a(C0700a c0700a, UserInfoBean userInfoBean, boolean z) {
            this.f75c = c0700a;
            this.f74b = userInfoBean;
            this.f73a = z;
        }

        public final void run() {
            try {
                if (this.f74b != null) {
                    UserInfoBean userInfoBean = this.f74b;
                    if (userInfoBean != null) {
                        C0705a b = C0705a.m85b();
                        if (b != null) {
                            userInfoBean.f62j = b.m114e();
                        }
                    }
                    C0757w.m460c("[UserInfo] Record user info.", new Object[0]);
                    C0700a.m44a(this.f75c, this.f74b, false);
                }
                if (this.f73a) {
                    C0700a c0700a = this.f75c;
                    C0756v a = C0756v.m449a();
                    if (a != null) {
                        a.m450a(new C06962(c0700a));
                    }
                }
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
    }

    /* compiled from: BUGLY */
    class C0698b implements Runnable {
        private /* synthetic */ C0700a f76a;

        C0698b(C0700a c0700a) {
            this.f76a = c0700a;
        }

        public final void run() {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis < this.f76a.f80b) {
                C0756v.m449a().m451a(new C0698b(this.f76a), (this.f76a.f80b - currentTimeMillis) + 5000);
                return;
            }
            this.f76a.m50a(3, false, 0);
            this.f76a.m49a();
        }
    }

    /* compiled from: BUGLY */
    class C0699c implements Runnable {
        private long f77a = 21600000;
        private /* synthetic */ C0700a f78b;

        public C0699c(C0700a c0700a, long j) {
            this.f78b = c0700a;
            this.f77a = j;
        }

        public final void run() {
            C0700a c0700a = this.f78b;
            C0756v a = C0756v.m449a();
            if (a != null) {
                a.m450a(new C06962(c0700a));
            }
            c0700a = this.f78b;
            long j = this.f77a;
            C0756v.m449a().m451a(new C0699c(c0700a, j), j);
        }
    }

    static /* synthetic */ void m44a(C0700a c0700a, UserInfoBean userInfoBean, boolean z) {
        if (userInfoBean != null) {
            if (!(z || userInfoBean.f54b == 1)) {
                List a = c0700a.m48a(C0705a.m84a(c0700a.f79a).f131d);
                if (a != null && a.size() >= 20) {
                    C0757w.m456a("[UserInfo] There are too many user info in local: %d", Integer.valueOf(a.size()));
                    return;
                }
            }
            long a2 = C0745o.m380a().m395a("t_ui", C0700a.m41a(userInfoBean), null, true);
            if (a2 >= 0) {
                C0757w.m460c("[Database] insert %s success with ID: %d", "t_ui", Long.valueOf(a2));
                userInfoBean.f53a = a2;
            }
        }
    }

    public C0700a(Context context, boolean z) {
        this.f79a = context;
        this.f82d = z;
    }

    public final void m50a(int i, boolean z, long j) {
        int i2 = 1;
        C0709a a = C0709a.m169a();
        if (a == null || a.m177c().f163h || i == 1 || i == 3) {
            if (i == 1 || i == 3) {
                this.f81c++;
            }
            C0705a a2 = C0705a.m84a(this.f79a);
            UserInfoBean userInfoBean = new UserInfoBean();
            userInfoBean.f54b = i;
            userInfoBean.f55c = a2.f131d;
            userInfoBean.f56d = a2.m118g();
            userInfoBean.f57e = System.currentTimeMillis();
            userInfoBean.f58f = -1;
            userInfoBean.f66n = a2.f137j;
            if (i != 1) {
                i2 = 0;
            }
            userInfoBean.f67o = i2;
            userInfoBean.f64l = a2.m106a();
            userInfoBean.f65m = a2.f143p;
            userInfoBean.f59g = a2.f144q;
            userInfoBean.f60h = a2.f145r;
            userInfoBean.f61i = a2.f146s;
            userInfoBean.f63k = a2.f147t;
            userInfoBean.f70r = a2.m138z();
            userInfoBean.f71s = a2.m91E();
            userInfoBean.f68p = a2.m92F();
            userInfoBean.f69q = a2.m93G();
            C0756v.m449a().m451a(new C0697a(this, userInfoBean, z), 0);
            return;
        }
        C0757w.m462e("UserInfo is disable", new Object[0]);
    }

    public final void m49a() {
        this.f80b = C0761y.m509b() + 86400000;
        C0756v.m449a().m451a(new C0698b(this), (this.f80b - System.currentTimeMillis()) + 5000);
    }

    private synchronized void m47c() {
        boolean z = false;
        synchronized (this) {
            if (this.f82d) {
                C0753t a = C0753t.m412a();
                if (a != null) {
                    C0709a a2 = C0709a.m169a();
                    if (a2 != null && (!a2.m176b() || a.m442b((int) PointerIconCompat.TYPE_CONTEXT_MENU))) {
                        boolean z2;
                        List list;
                        String str = C0705a.m84a(this.f79a).f131d;
                        List arrayList = new ArrayList();
                        List a3 = m48a(str);
                        if (a3 != null) {
                            int i;
                            UserInfoBean userInfoBean;
                            int i2;
                            int size = a3.size() - 20;
                            if (size > 0) {
                                for (int i3 = 0; i3 < a3.size() - 1; i3++) {
                                    for (i = i3 + 1; i < a3.size(); i++) {
                                        if (((UserInfoBean) a3.get(i3)).f57e > ((UserInfoBean) a3.get(i)).f57e) {
                                            userInfoBean = (UserInfoBean) a3.get(i3);
                                            a3.set(i3, a3.get(i));
                                            a3.set(i, userInfoBean);
                                        }
                                    }
                                }
                                for (i2 = 0; i2 < size; i2++) {
                                    arrayList.add(a3.get(i2));
                                }
                            }
                            Iterator it = a3.iterator();
                            i = 0;
                            while (it.hasNext()) {
                                userInfoBean = (UserInfoBean) it.next();
                                if (userInfoBean.f58f != -1) {
                                    it.remove();
                                    if (userInfoBean.f57e < C0761y.m509b()) {
                                        arrayList.add(userInfoBean);
                                    }
                                }
                                if (userInfoBean.f57e <= System.currentTimeMillis() - 600000 || !(userInfoBean.f54b == 1 || userInfoBean.f54b == 4 || userInfoBean.f54b == 3)) {
                                    i2 = i;
                                } else {
                                    i2 = i + 1;
                                }
                                i = i2;
                            }
                            if (i > 15) {
                                C0757w.m461d("[UserInfo] Upload user info too many times in 10 min: %d", Integer.valueOf(i));
                                z2 = false;
                            } else {
                                z2 = true;
                            }
                            list = a3;
                        } else {
                            Object arrayList2 = new ArrayList();
                            z2 = true;
                        }
                        if (arrayList.size() > 0) {
                            C0700a.m45a(arrayList);
                        }
                        if (!z2 || list.size() == 0) {
                            C0757w.m460c("[UserInfo] There is no user info in local database.", new Object[0]);
                        } else {
                            C0757w.m460c("[UserInfo] Upload user info(size: %d)", Integer.valueOf(list.size()));
                            C0737j a4 = C0730a.m289a(list, this.f81c == 1 ? 1 : 2);
                            if (a4 == null) {
                                C0757w.m461d("[UserInfo] Failed to create UserInfoPackage.", new Object[0]);
                            } else {
                                byte[] a5 = C0730a.m294a(a4);
                                if (a5 == null) {
                                    C0757w.m461d("[UserInfo] Failed to encode data.", new Object[0]);
                                } else {
                                    al a6 = C0730a.m286a(this.f79a, a.f423a ? 840 : 640, a5);
                                    if (a6 == null) {
                                        C0757w.m461d("[UserInfo] Request package is null.", new Object[0]);
                                    } else {
                                        C0749s c11461 = new C0749s(this) {
                                            private /* synthetic */ C0700a f535b;

                                            public final void mo2269a(boolean z) {
                                                if (z) {
                                                    C0757w.m460c("[UserInfo] Successfully uploaded user info.", new Object[0]);
                                                    long currentTimeMillis = System.currentTimeMillis();
                                                    for (UserInfoBean userInfoBean : list) {
                                                        userInfoBean.f58f = currentTimeMillis;
                                                        C0700a.m44a(this.f535b, userInfoBean, true);
                                                    }
                                                }
                                            }
                                        };
                                        StrategyBean c = C0709a.m169a().m177c();
                                        String str2 = a.f423a ? c.f173r : c.f175t;
                                        String str3 = a.f423a ? StrategyBean.f157b : StrategyBean.f156a;
                                        C0753t a7 = C0753t.m412a();
                                        if (this.f81c == 1) {
                                            z = true;
                                        }
                                        a7.m435a(PointerIconCompat.TYPE_CONTEXT_MENU, a6, str2, str3, c11461, z);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public final void m51b() {
        C0756v a = C0756v.m449a();
        if (a != null) {
            a.m450a(new C06962(this));
        }
    }

    public final List<UserInfoBean> m48a(String str) {
        Cursor a;
        Throwable th;
        Cursor cursor;
        try {
            a = C0745o.m380a().m396a("t_ui", null, C0761y.m501a(str) ? null : "_pc = '" + str + "'", null, null, true);
            if (a == null) {
                if (a != null) {
                    a.close();
                }
                return null;
            }
            try {
                StringBuilder stringBuilder = new StringBuilder();
                List<UserInfoBean> arrayList = new ArrayList();
                while (a.moveToNext()) {
                    UserInfoBean a2 = C0700a.m42a(a);
                    if (a2 != null) {
                        arrayList.add(a2);
                    } else {
                        try {
                            stringBuilder.append(" or _id").append(" = ").append(a.getLong(a.getColumnIndex("_id")));
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                }
                String stringBuilder2 = stringBuilder.toString();
                if (stringBuilder2.length() > 0) {
                    int a3 = C0745o.m380a().m394a("t_ui", stringBuilder2.substring(4), null, null, true);
                    C0757w.m461d("[Database] deleted %s error data %d", "t_ui", Integer.valueOf(a3));
                }
                if (a != null) {
                    a.close();
                }
                return arrayList;
            } catch (Throwable th22) {
                th = th22;
            }
        } catch (Throwable th3) {
            th = th3;
            a = null;
            if (a != null) {
                a.close();
            }
            throw th;
        }
    }

    private static void m45a(List<UserInfoBean> list) {
        if (list != null && list.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            while (i < list.size() && i < 50) {
                stringBuilder.append(" or _id").append(" = ").append(((UserInfoBean) list.get(i)).f53a);
                i++;
            }
            String stringBuilder2 = stringBuilder.toString();
            if (stringBuilder2.length() > 0) {
                stringBuilder2 = stringBuilder2.substring(4);
            }
            stringBuilder.setLength(0);
            try {
                int a = C0745o.m380a().m394a("t_ui", stringBuilder2, null, null, true);
                C0757w.m460c("[Database] deleted %s data %d", "t_ui", Integer.valueOf(a));
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
    }

    private static ContentValues m41a(UserInfoBean userInfoBean) {
        if (userInfoBean == null) {
            return null;
        }
        try {
            ContentValues contentValues = new ContentValues();
            if (userInfoBean.f53a > 0) {
                contentValues.put("_id", Long.valueOf(userInfoBean.f53a));
            }
            contentValues.put("_tm", Long.valueOf(userInfoBean.f57e));
            contentValues.put("_ut", Long.valueOf(userInfoBean.f58f));
            contentValues.put("_tp", Integer.valueOf(userInfoBean.f54b));
            contentValues.put("_pc", userInfoBean.f55c);
            contentValues.put("_dt", C0761y.m504a((Parcelable) userInfoBean));
            return contentValues;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    private static UserInfoBean m42a(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        try {
            byte[] blob = cursor.getBlob(cursor.getColumnIndex("_dt"));
            if (blob == null) {
                return null;
            }
            long j = cursor.getLong(cursor.getColumnIndex("_id"));
            UserInfoBean userInfoBean = (UserInfoBean) C0761y.m485a(blob, UserInfoBean.CREATOR);
            if (userInfoBean == null) {
                return userInfoBean;
            }
            userInfoBean.f53a = j;
            return userInfoBean;
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }
}
