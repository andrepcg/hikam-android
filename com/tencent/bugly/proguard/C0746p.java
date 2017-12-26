package com.tencent.bugly.proguard;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tencent.bugly.C0692a;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.C0706b;
import java.io.File;
import java.util.List;

/* compiled from: BUGLY */
public final class C0746p extends SQLiteOpenHelper {
    private static int f401a = 13;
    private Context f402b;
    private List<C0692a> f403c;

    public C0746p(Context context, List<C0692a> list) {
        StringBuilder stringBuilder = new StringBuilder("bugly_db_");
        C0705a.m84a(context).getClass();
        super(context, stringBuilder.toString(), null, f401a);
        this.f402b = context;
        this.f403c = list;
    }

    public final synchronized void onCreate(SQLiteDatabase sQLiteDatabase) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.setLength(0);
            stringBuilder.append(" CREATE TABLE IF NOT EXISTS t_ui").append(" ( _id").append(" INTEGER PRIMARY KEY").append(" , _tm").append(" int").append(" , _ut").append(" int").append(" , _tp").append(" int").append(" , _dt").append(" blob").append(" , _pc").append(" text").append(" ) ");
            C0757w.m460c(stringBuilder.toString(), new Object[0]);
            sQLiteDatabase.execSQL(stringBuilder.toString(), new String[0]);
            stringBuilder.setLength(0);
            stringBuilder.append(" CREATE TABLE IF NOT EXISTS t_lr").append(" ( _id").append(" INTEGER PRIMARY KEY").append(" , _tp").append(" int").append(" , _tm").append(" int").append(" , _pc").append(" text").append(" , _th").append(" text").append(" , _dt").append(" blob").append(" ) ");
            C0757w.m460c(stringBuilder.toString(), new Object[0]);
            sQLiteDatabase.execSQL(stringBuilder.toString(), new String[0]);
            stringBuilder.setLength(0);
            stringBuilder.append(" CREATE TABLE IF NOT EXISTS t_pf").append(" ( _id").append(" integer").append(" , _tp").append(" text").append(" , _tm").append(" int").append(" , _dt").append(" blob").append(",primary key(_id").append(",_tp").append(" )) ");
            C0757w.m460c(stringBuilder.toString(), new Object[0]);
            sQLiteDatabase.execSQL(stringBuilder.toString(), new String[0]);
            stringBuilder.setLength(0);
            stringBuilder.append(" CREATE TABLE IF NOT EXISTS t_cr").append(" ( _id").append(" INTEGER PRIMARY KEY").append(" , _tm").append(" int").append(" , _s1").append(" text").append(" , _up").append(" int").append(" , _me").append(" int").append(" , _uc").append(" int").append(" , _dt").append(" blob").append(" ) ");
            C0757w.m460c(stringBuilder.toString(), new Object[0]);
            sQLiteDatabase.execSQL(stringBuilder.toString(), new String[0]);
            stringBuilder.setLength(0);
            stringBuilder.append(" CREATE TABLE IF NOT EXISTS dl_1002").append(" (_id").append(" integer primary key autoincrement, _dUrl").append(" varchar(100), _sFile").append(" varchar(100), _sLen").append(" INTEGER, _tLen").append(" INTEGER, _MD5").append(" varchar(100), _DLTIME").append(" INTEGER)");
            C0757w.m460c(stringBuilder.toString(), new Object[0]);
            sQLiteDatabase.execSQL(stringBuilder.toString(), new String[0]);
            stringBuilder.setLength(0);
            stringBuilder.append("CREATE TABLE IF NOT EXISTS ge_1002").append(" (_id").append(" integer primary key autoincrement, _time").append(" INTEGER, _datas").append(" blob)");
            C0757w.m460c(stringBuilder.toString(), new Object[0]);
            sQLiteDatabase.execSQL(stringBuilder.toString(), new String[0]);
            stringBuilder.setLength(0);
            stringBuilder.append(" CREATE TABLE IF NOT EXISTS st_1002").append(" ( _id").append(" integer").append(" , _tp").append(" text").append(" , _tm").append(" int").append(" , _dt").append(" blob").append(",primary key(_id").append(",_tp").append(" )) ");
            C0757w.m460c(stringBuilder.toString(), new Object[0]);
            sQLiteDatabase.execSQL(stringBuilder.toString(), new String[0]);
        } catch (Throwable th) {
            if (!C0757w.m459b(th)) {
                th.printStackTrace();
            }
        }
        if (this.f403c != null) {
            for (C0692a onDbCreate : this.f403c) {
                try {
                    onDbCreate.onDbCreate(sQLiteDatabase);
                } catch (Throwable th2) {
                    if (!C0757w.m459b(th2)) {
                        th2.printStackTrace();
                    }
                }
            }
        }
    }

    private synchronized boolean m404a(SQLiteDatabase sQLiteDatabase) {
        boolean z = true;
        synchronized (this) {
            try {
                for (String str : new String[]{"t_lr", "t_ui", "t_pf"}) {
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + str, new String[0]);
                }
            } catch (Throwable th) {
                if (!C0757w.m459b(th)) {
                    th.printStackTrace();
                }
                z = false;
            }
        }
        return z;
    }

    public final synchronized void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        C0757w.m461d("[Database] Upgrade %d to %d , drop tables!", Integer.valueOf(i), Integer.valueOf(i2));
        if (this.f403c != null) {
            for (C0692a onDbUpgrade : this.f403c) {
                try {
                    onDbUpgrade.onDbUpgrade(sQLiteDatabase, i, i2);
                } catch (Throwable th) {
                    if (!C0757w.m459b(th)) {
                        th.printStackTrace();
                    }
                }
            }
        }
        if (m404a(sQLiteDatabase)) {
            onCreate(sQLiteDatabase);
        } else {
            C0757w.m461d("[Database] Failed to drop, delete db.", new Object[0]);
            File databasePath = this.f402b.getDatabasePath("bugly_db");
            if (databasePath != null && databasePath.canWrite()) {
                databasePath.delete();
            }
        }
    }

    @TargetApi(11)
    public final synchronized void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (C0706b.m144c() >= 11) {
            C0757w.m461d("[Database] Downgrade %d to %d drop tables.", Integer.valueOf(i), Integer.valueOf(i2));
            if (this.f403c != null) {
                for (C0692a onDbDowngrade : this.f403c) {
                    try {
                        onDbDowngrade.onDbDowngrade(sQLiteDatabase, i, i2);
                    } catch (Throwable th) {
                        if (!C0757w.m459b(th)) {
                            th.printStackTrace();
                        }
                    }
                }
            }
            if (m404a(sQLiteDatabase)) {
                onCreate(sQLiteDatabase);
            } else {
                C0757w.m461d("[Database] Failed to drop, delete db.", new Object[0]);
                File databasePath = this.f402b.getDatabasePath("bugly_db");
                if (databasePath != null && databasePath.canWrite()) {
                    databasePath.delete();
                }
            }
        }
    }

    public final synchronized SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase sQLiteDatabase;
        int i = 0;
        synchronized (this) {
            sQLiteDatabase = null;
            while (sQLiteDatabase == null && i < 5) {
                i++;
                try {
                    sQLiteDatabase = super.getReadableDatabase();
                } catch (Throwable th) {
                    C0757w.m461d("[Database] Try to get db(count: %d).", Integer.valueOf(i));
                    if (i == 5) {
                        C0757w.m462e("[Database] Failed to get db.", new Object[0]);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sQLiteDatabase;
    }

    public final synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase sQLiteDatabase;
        int i = 0;
        synchronized (this) {
            sQLiteDatabase = null;
            while (sQLiteDatabase == null && i < 5) {
                i++;
                try {
                    sQLiteDatabase = super.getWritableDatabase();
                } catch (Throwable th) {
                    C0757w.m461d("[Database] Try to get db(count: %d).", Integer.valueOf(i));
                    if (i == 5) {
                        C0757w.m462e("[Database] Failed to get db.", new Object[0]);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (sQLiteDatabase == null) {
                C0757w.m461d("[Database] db error delay error record 1min.", new Object[0]);
            }
        }
        return sQLiteDatabase;
    }
}
