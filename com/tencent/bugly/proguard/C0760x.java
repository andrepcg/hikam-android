package com.tencent.bugly.proguard;

import android.content.Context;
import android.os.Process;
import com.tencent.bugly.crashreport.common.info.C0705a;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* compiled from: BUGLY */
public final class C0760x {
    public static boolean f472a = true;
    private static SimpleDateFormat f473b;
    private static int f474c = 5120;
    private static StringBuilder f475d;
    private static StringBuilder f476e;
    private static boolean f477f;
    private static C0759a f478g;
    private static String f479h;
    private static String f480i;
    private static Context f481j;
    private static String f482k;
    private static boolean f483l;
    private static int f484m;
    private static Object f485n = new Object();

    /* compiled from: BUGLY */
    public static class C0759a {
        private boolean f467a;
        private File f468b;
        private String f469c;
        private long f470d;
        private long f471e = 30720;

        public C0759a(String str) {
            if (str != null && !str.equals("")) {
                this.f469c = str;
                this.f467a = m464a();
            }
        }

        private synchronized boolean m464a() {
            boolean z = false;
            synchronized (this) {
                try {
                    this.f468b = new File(this.f469c);
                    if (!this.f468b.exists() || this.f468b.delete()) {
                        if (!this.f468b.createNewFile()) {
                            this.f467a = false;
                        }
                        z = true;
                    } else {
                        this.f467a = false;
                    }
                } catch (Throwable th) {
                    this.f467a = false;
                }
            }
            return z;
        }

        public final synchronized boolean m468a(String str) {
            FileOutputStream fileOutputStream;
            Throwable th;
            boolean z = false;
            synchronized (this) {
                if (this.f467a) {
                    FileOutputStream fileOutputStream2;
                    try {
                        fileOutputStream2 = new FileOutputStream(this.f468b, true);
                        try {
                            byte[] bytes = str.getBytes("UTF-8");
                            fileOutputStream2.write(bytes);
                            fileOutputStream2.flush();
                            fileOutputStream2.close();
                            this.f470d += (long) bytes.length;
                            try {
                                fileOutputStream2.close();
                            } catch (IOException e) {
                            }
                            z = true;
                        } catch (Throwable th2) {
                            th = th2;
                            if (fileOutputStream2 != null) {
                                fileOutputStream2.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        fileOutputStream2 = null;
                        if (fileOutputStream2 != null) {
                            fileOutputStream2.close();
                        }
                        throw th;
                    }
                }
            }
            return z;
        }
    }

    static {
        f473b = null;
        try {
            f473b = new SimpleDateFormat("MM-dd HH:mm:ss");
        } catch (Throwable th) {
        }
    }

    private static boolean m477b(String str, String str2, String str3) {
        try {
            C0705a b = C0705a.m85b();
            if (!(b == null || b.f105C == null)) {
                return b.f105C.appendLogToNative(str, str2, str3);
            }
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
        }
        return false;
    }

    public static synchronized void m472a(Context context) {
        synchronized (C0760x.class) {
            if (!(f483l || context == null || !f472a)) {
                try {
                    f476e = new StringBuilder(0);
                    f475d = new StringBuilder(0);
                    f481j = context;
                    C0705a a = C0705a.m84a(context);
                    f479h = a.f131d;
                    a.getClass();
                    f480i = "";
                    f482k = f481j.getFilesDir().getPath() + "/buglylog_" + f479h + "_" + f480i + ".txt";
                    f484m = Process.myPid();
                } catch (Throwable th) {
                }
                f483l = true;
            }
        }
    }

    public static void m471a(int i) {
        synchronized (f485n) {
            f474c = i;
            if (i < 0) {
                f474c = 0;
            } else if (i > 10240) {
                f474c = 10240;
            }
        }
    }

    public static void m474a(String str, String str2, Throwable th) {
        if (th != null) {
            String message = th.getMessage();
            if (message == null) {
                message = "";
            }
            C0760x.m473a(str, str2, message + '\n' + C0761y.m510b(th));
        }
    }

    public static synchronized void m473a(String str, String str2, String str3) {
        synchronized (C0760x.class) {
            if (f483l && f472a) {
                C0760x.m477b(str, str2, str3);
                long myTid = (long) Process.myTid();
                f475d.setLength(0);
                if (str3.length() > 30720) {
                    str3 = str3.substring(str3.length() - 30720, str3.length() - 1);
                }
                Date date = new Date();
                f475d.append(f473b != null ? f473b.format(date) : date.toString()).append(" ").append(f484m).append(" ").append(myTid).append(" ").append(str).append(" ").append(str2).append(": ").append(str3).append("\u0001\r\n");
                final String stringBuilder = f475d.toString();
                synchronized (f485n) {
                    f476e.append(stringBuilder);
                    if (f476e.length() <= f474c) {
                    } else if (f477f) {
                    } else {
                        f477f = true;
                        C0756v.m449a().m450a(new Runnable() {
                            public final void run() {
                                synchronized (C0760x.f485n) {
                                    try {
                                        if (C0760x.f478g == null) {
                                            C0760x.f478g = new C0759a(C0760x.f482k);
                                        } else if (C0760x.f478g.f468b == null || C0760x.f478g.f468b.length() + ((long) C0760x.f476e.length()) > C0760x.f478g.f471e) {
                                            C0760x.f478g.m464a();
                                        }
                                        if (C0760x.f478g.f467a) {
                                            C0760x.f478g.m468a(C0760x.f476e.toString());
                                            C0760x.f476e.setLength(0);
                                        } else {
                                            C0760x.f476e.setLength(0);
                                            C0760x.f476e.append(stringBuilder);
                                        }
                                        C0760x.f477f = false;
                                    } catch (Throwable th) {
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public static byte[] m475a(boolean z) {
        byte[] bArr = null;
        if (f472a) {
            synchronized (f485n) {
                File a;
                if (z) {
                    try {
                        if (f478g != null && f478g.f467a) {
                            a = f478g.f468b;
                            if (f476e.length() == 0 || a != null) {
                                bArr = C0761y.m505a(a, f476e.toString());
                            }
                        }
                    } catch (Throwable th) {
                    }
                }
                a = bArr;
                if (f476e.length() == 0) {
                }
                bArr = C0761y.m505a(a, f476e.toString());
            }
        }
        return bArr;
    }
}
