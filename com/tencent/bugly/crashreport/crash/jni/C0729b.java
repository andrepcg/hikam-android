package com.tencent.bugly.crashreport.crash.jni;

import android.content.Context;
import android.support.v4.os.EnvironmentCompat;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.crash.CrashDetailBean;
import com.tencent.bugly.proguard.C0731b;
import com.tencent.bugly.proguard.C0737j;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/* compiled from: BUGLY */
public class C0729b {
    private StringBuilder f349a;
    private int f350b = 0;

    private void m264d(String str) {
        for (int i = 0; i < this.f350b; i++) {
            this.f349a.append('\t');
        }
        if (str != null) {
            this.f349a.append(str).append(": ");
        }
    }

    public C0729b(StringBuilder stringBuilder, int i) {
        this.f349a = stringBuilder;
        this.f350b = i;
    }

    public C0729b m276a(boolean z, String str) {
        m264d(str);
        this.f349a.append(z ? 'T' : 'F').append('\n');
        return this;
    }

    private static Map<String, Integer> m262c(String str) {
        if (str == null) {
            return null;
        }
        try {
            Map<String, Integer> hashMap = new HashMap();
            for (String split : str.split(",")) {
                String[] split2 = split.split(":");
                if (split2.length != 2) {
                    C0757w.m462e("error format at %s", split);
                    return null;
                }
                hashMap.put(split2[0], Integer.valueOf(Integer.parseInt(split2[1])));
            }
            return hashMap;
        } catch (Exception e) {
            C0757w.m462e("error format intStateStr %s", str);
            e.printStackTrace();
            return null;
        }
    }

    public C0729b m265a(byte b, String str) {
        m264d(str);
        this.f349a.append(b).append('\n');
        return this;
    }

    public C0729b m266a(char c, String str) {
        m264d(str);
        this.f349a.append(c).append('\n');
        return this;
    }

    public C0729b m275a(short s, String str) {
        m264d(str);
        this.f349a.append(s).append('\n');
        return this;
    }

    public C0729b m269a(int i, String str) {
        m264d(str);
        this.f349a.append(i).append('\n');
        return this;
    }

    protected static String m258a(String str) {
        if (str == null) {
            return "";
        }
        String[] split = str.split("\n");
        if (split == null || split.length == 0) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str2 : split) {
            if (!str2.contains("java.lang.Thread.getStackTrace(")) {
                stringBuilder.append(str2).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public C0729b m270a(long j, String str) {
        m264d(str);
        this.f349a.append(j).append('\n');
        return this;
    }

    public C0729b m268a(float f, String str) {
        m264d(str);
        this.f349a.append(f).append('\n');
        return this;
    }

    public C0729b m267a(double d, String str) {
        m264d(str);
        this.f349a.append(d).append('\n');
        return this;
    }

    private static CrashDetailBean m256a(Context context, Map<String, String> map, NativeExceptionHandler nativeExceptionHandler) {
        if (map == null) {
            return null;
        }
        if (C0705a.m84a(context) == null) {
            C0757w.m462e("abnormal com info not created", new Object[0]);
            return null;
        }
        String str = (String) map.get("intStateStr");
        if (str == null || str.trim().length() <= 0) {
            C0757w.m462e("no intStateStr", new Object[0]);
            return null;
        }
        Map c = C0729b.m262c(str);
        if (c == null) {
            C0757w.m462e("parse intSateMap fail", Integer.valueOf(map.size()));
            return null;
        }
        try {
            ((Integer) c.get("sino")).intValue();
            ((Integer) c.get("sud")).intValue();
            String str2 = (String) map.get("soVersion");
            if (str2 == null) {
                C0757w.m462e("error format at version", new Object[0]);
                return null;
            }
            String str3;
            String str4;
            String str5;
            String str6;
            String str7;
            String str8;
            String str9;
            str = (String) map.get("errorAddr");
            String str10 = str == null ? EnvironmentCompat.MEDIA_UNKNOWN : str;
            str = (String) map.get("codeMsg");
            if (str == null) {
                str3 = EnvironmentCompat.MEDIA_UNKNOWN;
            } else {
                str3 = str;
            }
            str = (String) map.get("tombPath");
            if (str == null) {
                str4 = EnvironmentCompat.MEDIA_UNKNOWN;
            } else {
                str4 = str;
            }
            str = (String) map.get("signalName");
            if (str == null) {
                str5 = EnvironmentCompat.MEDIA_UNKNOWN;
            } else {
                str5 = str;
            }
            map.get("errnoMsg");
            str = (String) map.get("stack");
            if (str == null) {
                str6 = EnvironmentCompat.MEDIA_UNKNOWN;
            } else {
                str6 = str;
            }
            str = (String) map.get("jstack");
            if (str != null) {
                str6 = str6 + "java:\n" + str;
            }
            Integer num = (Integer) c.get("sico");
            if (num != null && num.intValue() > 0) {
                str5 = str5 + "(" + str3 + ")";
                str3 = "KERNEL";
            }
            str = (String) map.get("nativeLog");
            byte[] bArr = null;
            if (!(str == null || str.isEmpty())) {
                bArr = C0761y.m505a(null, str);
            }
            str = (String) map.get("sendingProcess");
            if (str == null) {
                str7 = EnvironmentCompat.MEDIA_UNKNOWN;
            } else {
                str7 = str;
            }
            num = (Integer) c.get("spd");
            if (num != null) {
                str7 = str7 + "(" + num + ")";
            }
            str = (String) map.get("threadName");
            if (str == null) {
                str8 = EnvironmentCompat.MEDIA_UNKNOWN;
            } else {
                str8 = str;
            }
            num = (Integer) c.get("et");
            if (num != null) {
                str8 = str8 + "(" + num + ")";
            }
            str = (String) map.get("processName");
            if (str == null) {
                str9 = EnvironmentCompat.MEDIA_UNKNOWN;
            } else {
                str9 = str;
            }
            num = (Integer) c.get("ep");
            if (num != null) {
                str9 = str9 + "(" + num + ")";
            }
            Map map2 = null;
            str = (String) map.get("key-value");
            if (str != null) {
                map2 = new HashMap();
                for (String split : str.split("\n")) {
                    String[] split2 = split.split("=");
                    if (split2.length == 2) {
                        map2.put(split2[0], split2[1]);
                    }
                }
            }
            CrashDetailBean packageCrashDatas = nativeExceptionHandler.packageCrashDatas(str9, str8, (((long) ((Integer) c.get("etms")).intValue()) / 1000) + (((long) ((Integer) c.get("ets")).intValue()) * 1000), str5, str10, C0729b.m258a(str6), str3, str7, str4, str2, bArr, map2, false);
            if (packageCrashDatas != null) {
                str = (String) map.get("userId");
                if (str != null) {
                    C0757w.m460c("[Native record info] userId: %s", str);
                    packageCrashDatas.f228m = str;
                }
                str = (String) map.get("sysLog");
                if (str != null) {
                    packageCrashDatas.f238w = str;
                }
                str = (String) map.get("appVersion");
                if (str != null) {
                    C0757w.m460c("[Native record info] appVersion: %s", str);
                    packageCrashDatas.f221f = str;
                }
                str = (String) map.get("isAppForeground");
                if (str != null) {
                    C0757w.m460c("[Native record info] isAppForeground: %s", str);
                    packageCrashDatas.f206M = str.equalsIgnoreCase("true");
                }
                str = (String) map.get("launchTime");
                if (str != null) {
                    C0757w.m460c("[Native record info] launchTime: %s", str);
                    packageCrashDatas.f205L = Long.parseLong(str);
                }
                packageCrashDatas.f240y = null;
                packageCrashDatas.f226k = true;
            }
            return packageCrashDatas;
        } catch (Throwable e) {
            if (!C0757w.m457a(e)) {
                e.printStackTrace();
            }
        } catch (Throwable e2) {
            C0757w.m462e("error format", new Object[0]);
            e2.printStackTrace();
            return null;
        }
    }

    public C0729b m284b(String str, String str2) {
        m264d(str2);
        if (str == null) {
            this.f349a.append("null\n");
        } else {
            this.f349a.append(str).append('\n');
        }
        return this;
    }

    public C0729b m277a(byte[] bArr, String str) {
        m264d(str);
        if (bArr == null) {
            this.f349a.append("null\n");
        } else if (bArr.length == 0) {
            this.f349a.append(bArr.length).append(", []\n");
        } else {
            this.f349a.append(bArr.length).append(", [\n");
            C0729b c0729b = new C0729b(this.f349a, this.f350b + 1);
            for (byte a : bArr) {
                c0729b.m265a(a, null);
            }
            m266a(']', null);
        }
        return this;
    }

    public C0729b m283a(short[] sArr, String str) {
        m264d(str);
        if (sArr == null) {
            this.f349a.append("null\n");
        } else if (sArr.length == 0) {
            this.f349a.append(sArr.length).append(", []\n");
        } else {
            this.f349a.append(sArr.length).append(", [\n");
            C0729b c0729b = new C0729b(this.f349a, this.f350b + 1);
            for (short a : sArr) {
                c0729b.m275a(a, null);
            }
            m266a(']', null);
        }
        return this;
    }

    public C0729b m280a(int[] iArr, String str) {
        m264d(str);
        if (iArr == null) {
            this.f349a.append("null\n");
        } else if (iArr.length == 0) {
            this.f349a.append(iArr.length).append(", []\n");
        } else {
            this.f349a.append(iArr.length).append(", [\n");
            C0729b c0729b = new C0729b(this.f349a, this.f350b + 1);
            for (int a : iArr) {
                c0729b.m269a(a, null);
            }
            m266a(']', null);
        }
        return this;
    }

    public C0729b m281a(long[] jArr, String str) {
        m264d(str);
        if (jArr == null) {
            this.f349a.append("null\n");
        } else if (jArr.length == 0) {
            this.f349a.append(jArr.length).append(", []\n");
        } else {
            this.f349a.append(jArr.length).append(", [\n");
            C0729b c0729b = new C0729b(this.f349a, this.f350b + 1);
            for (long a : jArr) {
                c0729b.m270a(a, null);
            }
            m266a(']', null);
        }
        return this;
    }

    public C0729b m279a(float[] fArr, String str) {
        m264d(str);
        if (fArr == null) {
            this.f349a.append("null\n");
        } else if (fArr.length == 0) {
            this.f349a.append(fArr.length).append(", []\n");
        } else {
            this.f349a.append(fArr.length).append(", [\n");
            C0729b c0729b = new C0729b(this.f349a, this.f350b + 1);
            for (float a : fArr) {
                c0729b.m268a(a, null);
            }
            m266a(']', null);
        }
        return this;
    }

    public C0729b m278a(double[] dArr, String str) {
        m264d(str);
        if (dArr == null) {
            this.f349a.append("null\n");
        } else if (dArr.length == 0) {
            this.f349a.append(dArr.length).append(", []\n");
        } else {
            this.f349a.append(dArr.length).append(", [\n");
            C0729b c0729b = new C0729b(this.f349a, this.f350b + 1);
            for (double a : dArr) {
                c0729b.m267a(a, null);
            }
            m266a(']', null);
        }
        return this;
    }

    public <K, V> C0729b m274a(Map<K, V> map, String str) {
        m264d(str);
        if (map == null) {
            this.f349a.append("null\n");
        } else if (map.isEmpty()) {
            this.f349a.append(map.size()).append(", {}\n");
        } else {
            this.f349a.append(map.size()).append(", {\n");
            C0729b c0729b = new C0729b(this.f349a, this.f350b + 1);
            C0729b c0729b2 = new C0729b(this.f349a, this.f350b + 2);
            for (Entry entry : map.entrySet()) {
                c0729b.m266a('(', null);
                c0729b2.m272a(entry.getKey(), null);
                c0729b2.m272a(entry.getValue(), null);
                c0729b.m266a(')', null);
            }
            m266a('}', null);
        }
        return this;
    }

    private static String m257a(BufferedInputStream bufferedInputStream) throws IOException {
        if (bufferedInputStream == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            int read = bufferedInputStream.read();
            if (read == -1) {
                return null;
            }
            if (read == 0) {
                return stringBuilder.toString();
            }
            stringBuilder.append((char) read);
        }
    }

    public <T> C0729b m282a(T[] tArr, String str) {
        m264d(str);
        if (tArr == null) {
            this.f349a.append("null\n");
        } else if (tArr.length == 0) {
            this.f349a.append(tArr.length).append(", []\n");
        } else {
            this.f349a.append(tArr.length).append(", [\n");
            C0729b c0729b = new C0729b(this.f349a, this.f350b + 1);
            for (Object a : tArr) {
                c0729b.m272a(a, null);
            }
            m266a(']', null);
        }
        return this;
    }

    public <T> C0729b m273a(Collection<T> collection, String str) {
        if (collection != null) {
            return m282a(collection.toArray(), str);
        }
        m264d(str);
        this.f349a.append("null\t");
        return this;
    }

    public static CrashDetailBean m255a(Context context, String str, NativeExceptionHandler nativeExceptionHandler) {
        IOException e;
        Throwable th;
        CrashDetailBean crashDetailBean = null;
        if (context == null || str == null || nativeExceptionHandler == null) {
            C0757w.m462e("get eup record file args error", new Object[0]);
        } else {
            File file = new File(str, "rqd_record.eup");
            if (file.exists() && file.canRead()) {
                BufferedInputStream bufferedInputStream;
                try {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    try {
                        String a = C0729b.m257a(bufferedInputStream);
                        if (a == null || !a.equals("NATIVE_RQD_REPORT")) {
                            C0757w.m462e("record read fail! %s", a);
                            try {
                                bufferedInputStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        } else {
                            Map hashMap = new HashMap();
                            Object obj = crashDetailBean;
                            while (true) {
                                String a2 = C0729b.m257a(bufferedInputStream);
                                if (a2 == null) {
                                    break;
                                } else if (obj == null) {
                                    obj = a2;
                                } else {
                                    hashMap.put(obj, a2);
                                    obj = crashDetailBean;
                                }
                            }
                            if (obj != null) {
                                C0757w.m462e("record not pair! drop! %s", obj);
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException e22) {
                                    e22.printStackTrace();
                                }
                            } else {
                                crashDetailBean = C0729b.m256a(context, hashMap, nativeExceptionHandler);
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException e222) {
                                    e222.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e3) {
                        e222 = e3;
                        try {
                            e222.printStackTrace();
                            if (bufferedInputStream != null) {
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException e2222) {
                                    e2222.printStackTrace();
                                }
                            }
                            return crashDetailBean;
                        } catch (Throwable th2) {
                            th = th2;
                            if (bufferedInputStream != null) {
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException e22222) {
                                    e22222.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    }
                } catch (IOException e4) {
                    e22222 = e4;
                    bufferedInputStream = crashDetailBean;
                    e22222.printStackTrace();
                    if (bufferedInputStream != null) {
                        bufferedInputStream.close();
                    }
                    return crashDetailBean;
                } catch (Throwable th3) {
                    bufferedInputStream = crashDetailBean;
                    th = th3;
                    if (bufferedInputStream != null) {
                        bufferedInputStream.close();
                    }
                    throw th;
                }
            }
        }
        return crashDetailBean;
    }

    public <T> C0729b m272a(T t, String str) {
        if (t == null) {
            this.f349a.append("null\n");
        } else if (t instanceof Byte) {
            m265a(((Byte) t).byteValue(), str);
        } else if (t instanceof Boolean) {
            m276a(((Boolean) t).booleanValue(), str);
        } else if (t instanceof Short) {
            m275a(((Short) t).shortValue(), str);
        } else if (t instanceof Integer) {
            m269a(((Integer) t).intValue(), str);
        } else if (t instanceof Long) {
            m270a(((Long) t).longValue(), str);
        } else if (t instanceof Float) {
            m268a(((Float) t).floatValue(), str);
        } else if (t instanceof Double) {
            m267a(((Double) t).doubleValue(), str);
        } else if (t instanceof String) {
            m284b((String) t, str);
        } else if (t instanceof Map) {
            m274a((Map) t, str);
        } else if (t instanceof List) {
            m273a((List) t, str);
        } else if (t instanceof C0737j) {
            m271a((C0737j) t, str);
        } else if (t instanceof byte[]) {
            m277a((byte[]) t, str);
        } else if (t instanceof boolean[]) {
            m272a((boolean[]) t, str);
        } else if (t instanceof short[]) {
            m283a((short[]) t, str);
        } else if (t instanceof int[]) {
            m280a((int[]) t, str);
        } else if (t instanceof long[]) {
            m281a((long[]) t, str);
        } else if (t instanceof float[]) {
            m279a((float[]) t, str);
        } else if (t instanceof double[]) {
            m278a((double[]) t, str);
        } else if (t.getClass().isArray()) {
            m282a((Object[]) t, str);
        } else {
            throw new C0731b("write object error: unsupport type.");
        }
        return this;
    }

    private static String m261c(String str, String str2) {
        String str3 = null;
        BufferedReader a = C0761y.m483a(str, "reg_record.txt");
        if (a != null) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                String readLine = a.readLine();
                if (readLine != null && readLine.startsWith(str2)) {
                    String str4 = "                ";
                    int i = 0;
                    int i2 = 18;
                    int i3 = 0;
                    while (true) {
                        String readLine2 = a.readLine();
                        if (readLine2 == null) {
                            break;
                        }
                        if (i3 % 4 == 0) {
                            if (i3 > 0) {
                                stringBuilder.append("\n");
                            }
                            stringBuilder.append("  ");
                        } else {
                            if (readLine2.length() > 16) {
                                i2 = 28;
                            }
                            stringBuilder.append(str4.substring(0, i2 - i));
                        }
                        i = readLine2.length();
                        stringBuilder.append(readLine2);
                        i3++;
                    }
                    stringBuilder.append("\n");
                    str3 = stringBuilder.toString();
                    if (a != null) {
                        try {
                            a.close();
                        } catch (Throwable e) {
                            C0757w.m457a(e);
                        }
                    }
                } else if (a != null) {
                    try {
                        a.close();
                    } catch (Throwable e2) {
                        C0757w.m457a(e2);
                    }
                }
            } catch (Throwable th) {
                if (a != null) {
                    try {
                        a.close();
                    } catch (Throwable e22) {
                        C0757w.m457a(e22);
                    }
                }
            }
        }
        return str3;
    }

    public C0729b m271a(C0737j c0737j, String str) {
        m266a('{', str);
        if (c0737j == null) {
            this.f349a.append('\t').append("null");
        } else {
            c0737j.mo2285a(this.f349a, this.f350b + 1);
        }
        m266a('}', null);
        return this;
    }

    private static String m263d(String str, String str2) {
        String str3 = null;
        BufferedReader a = C0761y.m483a(str, "map_record.txt");
        if (a != null) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                String readLine = a.readLine();
                if (readLine != null && readLine.startsWith(str2)) {
                    while (true) {
                        readLine = a.readLine();
                        if (readLine == null) {
                            break;
                        }
                        stringBuilder.append("  ");
                        stringBuilder.append(readLine);
                        stringBuilder.append("\n");
                    }
                    str3 = stringBuilder.toString();
                    if (a != null) {
                        try {
                            a.close();
                        } catch (Throwable e) {
                            C0757w.m457a(e);
                        }
                    }
                } else if (a != null) {
                    try {
                        a.close();
                    } catch (Throwable e2) {
                        C0757w.m457a(e2);
                    }
                }
            } catch (Throwable th) {
                if (a != null) {
                    try {
                        a.close();
                    } catch (Throwable e22) {
                        C0757w.m457a(e22);
                    }
                }
            }
        }
        return str3;
    }

    public static String m259a(String str, String str2) {
        if (str == null || str2 == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String c = C0729b.m261c(str, str2);
        if (!(c == null || c.isEmpty())) {
            stringBuilder.append("Register infos:\n");
            stringBuilder.append(c);
        }
        c = C0729b.m263d(str, str2);
        if (!(c == null || c.isEmpty())) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append("System SO infos:\n");
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static void m260b(String str) {
        File file = new File(str, "rqd_record.eup");
        if (file.exists() && file.canWrite()) {
            file.delete();
            C0757w.m460c("delete record file %s", file.getAbsoluteFile());
        }
        file = new File(str, "reg_record.txt");
        if (file.exists() && file.canWrite()) {
            file.delete();
            C0757w.m460c("delete record file %s", file.getAbsoluteFile());
        }
        file = new File(str, "map_record.txt");
        if (file.exists() && file.canWrite()) {
            file.delete();
            C0757w.m460c("delete record file %s", file.getAbsoluteFile());
        }
        file = new File(str, "backup_record.txt");
        if (file.exists() && file.canWrite()) {
            file.delete();
            C0757w.m460c("delete record file %s", file.getAbsoluteFile());
        }
    }
}
