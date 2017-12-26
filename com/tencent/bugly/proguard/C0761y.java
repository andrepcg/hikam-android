package com.tencent.bugly.proguard;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import com.tencent.bugly.crashreport.common.info.AppInfo;
import com.tencent.bugly.crashreport.common.info.C0705a;
import com.tencent.bugly.crashreport.common.info.PlugInBean;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.http.HttpHost;

/* compiled from: BUGLY */
public class C0761y {
    private static Map<String, String> f486a = null;

    public static <T> T m485a(byte[] r3, android.os.Parcelable.Creator<T> r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0015 in list [B:5:0x0012]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r2 = 0;
        r1 = android.os.Parcel.obtain();
        r0 = r3.length;
        r1.unmarshall(r3, r2, r0);
        r1.setDataPosition(r2);
        r0 = r4.createFromParcel(r1);	 Catch:{ Throwable -> 0x0016, all -> 0x0021 }
        if (r1 == 0) goto L_0x0015;
    L_0x0012:
        r1.recycle();
    L_0x0015:
        return r0;
    L_0x0016:
        r0 = move-exception;
        r0.printStackTrace();	 Catch:{ Throwable -> 0x0016, all -> 0x0021 }
        if (r1 == 0) goto L_0x001f;
    L_0x001c:
        r1.recycle();
    L_0x001f:
        r0 = 0;
        goto L_0x0015;
    L_0x0021:
        r0 = move-exception;
        if (r1 == 0) goto L_0x0027;
    L_0x0024:
        r1.recycle();
    L_0x0027:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.bugly.proguard.y.a(byte[], android.os.Parcelable$Creator):T");
    }

    public static String m490a(Throwable th) {
        if (th == null) {
            return "";
        }
        try {
            Writer stringWriter = new StringWriter();
            th.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.getBuffer().toString();
        } catch (Throwable th2) {
            if (!C0757w.m457a(th2)) {
                th2.printStackTrace();
            }
            return "fail";
        }
    }

    public static String m486a() {
        return C0761y.m487a(System.currentTimeMillis());
    }

    public static String m487a(long j) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(j));
        } catch (Exception e) {
            return new Date().toString();
        }
    }

    public static String m491a(Date date) {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
        } catch (Exception e) {
            return new Date().toString();
        }
    }

    private static byte[] m508a(byte[] bArr, int i, String str) {
        if (bArr == null || i == -1) {
            return bArr;
        }
        C0757w.m460c("rqdp{  enD:} %d %d", Integer.valueOf(bArr.length), Integer.valueOf(i));
        try {
            af a = C0730a.m285a(i);
            if (a == null) {
                return null;
            }
            a.mo2280a(str);
            return a.mo2282b(bArr);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    private static byte[] m520b(byte[] bArr, int i, String str) {
        if (bArr == null || i == -1) {
            return bArr;
        }
        try {
            af a = C0730a.m285a(i);
            if (a == null) {
                return null;
            }
            a.mo2280a(str);
            return a.mo2281a(bArr);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            C0757w.m461d("encrytype %d %s", Integer.valueOf(i), str);
            return null;
        }
    }

    public static byte[] m505a(File file, String str) {
        FileInputStream fileInputStream;
        ByteArrayInputStream byteArrayInputStream;
        OutputStream byteArrayOutputStream;
        ZipOutputStream zipOutputStream;
        byte[] bArr;
        int read;
        Throwable th;
        Throwable th2;
        byte[] bArr2 = null;
        C0757w.m460c("rqdp{  ZF start}", new Object[0]);
        String str2 = "buglyCacheLog.txt";
        if (file != null) {
            try {
                if (file.exists() && file.canRead()) {
                    FileInputStream fileInputStream2 = new FileInputStream(file);
                    try {
                        str2 = file.getName();
                        fileInputStream = fileInputStream2;
                        byteArrayInputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
                        try {
                            zipOutputStream.setMethod(8);
                            zipOutputStream.putNextEntry(new ZipEntry(str2));
                            bArr = new byte[1024];
                            if (fileInputStream != null) {
                                while (true) {
                                    read = fileInputStream.read(bArr);
                                    if (read > 0) {
                                        zipOutputStream.write(bArr, 0, read);
                                    }
                                }
                                read = byteArrayInputStream.read(bArr);
                                if (read <= 0) {
                                    zipOutputStream.write(bArr, 0, read);
                                } else {
                                    zipOutputStream.closeEntry();
                                    zipOutputStream.flush();
                                    zipOutputStream.finish();
                                    bArr2 = byteArrayOutputStream.toByteArray();
                                    if (fileInputStream != null) {
                                        try {
                                            fileInputStream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    zipOutputStream.close();
                                    C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                                    return bArr2;
                                }
                            }
                            while (true) {
                                read = byteArrayInputStream.read(bArr);
                                if (read <= 0) {
                                    break;
                                }
                                zipOutputStream.write(bArr, 0, read);
                            }
                            zipOutputStream.closeEntry();
                            zipOutputStream.flush();
                            zipOutputStream.finish();
                            bArr2 = byteArrayOutputStream.toByteArray();
                            if (fileInputStream != null) {
                                fileInputStream.close();
                            }
                            zipOutputStream.close();
                            C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                        } catch (Throwable th3) {
                            th = th3;
                        }
                    } catch (Throwable th4) {
                        fileInputStream = fileInputStream2;
                        zipOutputStream = bArr2;
                        th2 = th4;
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        if (zipOutputStream != null) {
                            zipOutputStream.close();
                        }
                        C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                        throw th2;
                    }
                    return bArr2;
                }
            } catch (Throwable th42) {
                zipOutputStream = bArr2;
                fileInputStream = bArr2;
                th2 = th42;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
                C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                throw th2;
            }
        }
        fileInputStream = bArr2;
        try {
            byteArrayInputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
            byteArrayOutputStream = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            zipOutputStream.setMethod(8);
            zipOutputStream.putNextEntry(new ZipEntry(str2));
            bArr = new byte[1024];
            if (fileInputStream != null) {
                while (true) {
                    read = fileInputStream.read(bArr);
                    if (read > 0) {
                        zipOutputStream.write(bArr, 0, read);
                    }
                }
                read = byteArrayInputStream.read(bArr);
                if (read <= 0) {
                    zipOutputStream.write(bArr, 0, read);
                } else {
                    zipOutputStream.closeEntry();
                    zipOutputStream.flush();
                    zipOutputStream.finish();
                    bArr2 = byteArrayOutputStream.toByteArray();
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    zipOutputStream.close();
                    C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                    return bArr2;
                }
            }
            while (true) {
                read = byteArrayInputStream.read(bArr);
                if (read <= 0) {
                    break;
                }
                zipOutputStream.write(bArr, 0, read);
            }
            zipOutputStream.closeEntry();
            zipOutputStream.flush();
            zipOutputStream.finish();
            bArr2 = byteArrayOutputStream.toByteArray();
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            try {
                zipOutputStream.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            C0757w.m460c("rqdp{  ZF end}", new Object[0]);
        } catch (Throwable th422) {
            zipOutputStream = bArr2;
            th2 = th422;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (zipOutputStream != null) {
                zipOutputStream.close();
            }
            C0757w.m460c("rqdp{  ZF end}", new Object[0]);
            throw th2;
        }
        return bArr2;
    }

    public static byte[] m506a(byte[] bArr, int i) {
        if (bArr == null || i == -1) {
            return bArr;
        }
        String str = "[Util] Zip %d bytes data with type %s";
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(bArr.length);
        objArr[1] = i == 2 ? "Gzip" : ArchiveStreamFactory.ZIP;
        C0757w.m460c(str, objArr);
        try {
            aa a = C0762z.m524a(i);
            if (a == null) {
                return null;
            }
            return a.mo2278a(bArr);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    public static byte[] m518b(byte[] bArr, int i) {
        if (bArr == null || i == -1) {
            return bArr;
        }
        String str = "[Util] Unzip %d bytes data with type %s";
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(bArr.length);
        objArr[1] = i == 2 ? "Gzip" : ArchiveStreamFactory.ZIP;
        C0757w.m460c(str, objArr);
        try {
            aa a = C0762z.m524a(i);
            if (a == null) {
                return null;
            }
            return a.mo2279b(bArr);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    public static byte[] m507a(byte[] bArr, int i, int i2, String str) {
        byte[] bArr2 = null;
        if (bArr != null) {
            try {
                bArr2 = C0761y.m508a(C0761y.m506a(bArr, 2), 1, str);
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
        return bArr2;
    }

    public static byte[] m519b(byte[] bArr, int i, int i2, String str) {
        try {
            return C0761y.m518b(C0761y.m520b(bArr, 1, str), 2);
        } catch (Throwable e) {
            if (!C0757w.m457a(e)) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static long m509b() {
        try {
            return (((System.currentTimeMillis() + ((long) TimeZone.getDefault().getRawOffset())) / 86400000) * 86400000) - ((long) TimeZone.getDefault().getRawOffset());
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return -1;
        }
    }

    public static String m492a(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            String toHexString = Integer.toHexString(b & 255);
            if (toHexString.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(toHexString);
        }
        return stringBuffer.toString().toUpperCase();
    }

    public static String m511b(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return "NULL";
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(bArr);
            return C0761y.m492a(instance.digest());
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    public static boolean m500a(File file, File file2, int i) {
        Throwable th;
        FileInputStream fileInputStream;
        ZipOutputStream zipOutputStream;
        FileInputStream fileInputStream2 = null;
        C0757w.m460c("rqdp{  ZF start}", new Object[0]);
        if (file == null || file2 == null || file.equals(file2)) {
            C0757w.m461d("rqdp{  err ZF 1R!}", new Object[0]);
            return false;
        } else if (file.exists() && file.canRead()) {
            try {
                if (!(file2.getParentFile() == null || file2.getParentFile().exists())) {
                    file2.getParentFile().mkdirs();
                }
                if (!file2.exists()) {
                    file2.createNewFile();
                }
            } catch (Throwable th2) {
                if (!C0757w.m457a(th2)) {
                    th2.printStackTrace();
                }
            }
            if (!file2.exists() || !file2.canRead()) {
                return false;
            }
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file2)));
                    try {
                        zipOutputStream.setMethod(8);
                        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                        byte[] bArr = new byte[5000];
                        while (true) {
                            int read = fileInputStream.read(bArr);
                            if (read <= 0) {
                                break;
                            }
                            zipOutputStream.write(bArr, 0, read);
                        }
                        zipOutputStream.flush();
                        zipOutputStream.closeEntry();
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            zipOutputStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                        return true;
                    } catch (Throwable th3) {
                        th2 = th3;
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        if (zipOutputStream != null) {
                            zipOutputStream.close();
                        }
                        C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                        throw th2;
                    }
                } catch (Throwable th4) {
                    th2 = th4;
                    zipOutputStream = null;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (zipOutputStream != null) {
                        zipOutputStream.close();
                    }
                    C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                    throw th2;
                }
            } catch (Throwable th5) {
                th2 = th5;
                zipOutputStream = null;
                fileInputStream = null;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
                C0757w.m460c("rqdp{  ZF end}", new Object[0]);
                throw th2;
            }
        } else {
            C0757w.m461d("rqdp{  !sFile.exists() || !sFile.canRead(),pls check ,return!}", new Object[0]);
            return false;
        }
    }

    public static ArrayList<String> m494a(Context context, String[] strArr) {
        BufferedReader bufferedReader;
        Throwable th;
        BufferedReader bufferedReader2;
        BufferedReader bufferedReader3 = null;
        ArrayList<String> arrayList = new ArrayList();
        if (C0705a.m84a(context).m94H()) {
            arrayList = new ArrayList();
            arrayList.add(new String("unknown(low memory)"));
            return arrayList;
        }
        try {
            Process exec = Runtime.getRuntime().exec(strArr);
            bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            while (true) {
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    arrayList.add(readLine);
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            bufferedReader2 = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
            while (true) {
                try {
                    String readLine2 = bufferedReader2.readLine();
                    if (readLine2 != null) {
                        arrayList.add(readLine2);
                    } else {
                        try {
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            }
            bufferedReader.close();
            try {
                bufferedReader2.close();
                return arrayList;
            } catch (IOException e2) {
                e2.printStackTrace();
                return arrayList;
            }
        } catch (Throwable th4) {
            th = th4;
            bufferedReader = null;
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedReader3 != null) {
                bufferedReader3.close();
            }
            throw th;
        }
    }

    public static String m489a(Context context, String str) {
        if (str == null || str.trim().equals("")) {
            return "";
        }
        if (f486a == null) {
            f486a = new HashMap();
            List<String> a = C0761y.m494a(context, new String[]{"/system/bin/sh", "-c", "getprop"});
            if (a != null && a.size() > 0) {
                C0757w.m455a(C0761y.class, "Successfully get 'getprop' list.", new Object[0]);
                Pattern compile = Pattern.compile("\\[(.+)\\]: \\[(.*)\\]");
                for (String matcher : a) {
                    Matcher matcher2 = compile.matcher(matcher);
                    if (matcher2.find()) {
                        f486a.put(matcher2.group(1), matcher2.group(2));
                    }
                }
                C0757w.m455a(C0761y.class, "System properties number: %dffffdsfsdfff.", Integer.valueOf(f486a.size()));
            }
        }
        if (f486a.containsKey(str)) {
            return (String) f486a.get(str);
        }
        return "fail";
    }

    public static void m513b(long j) {
        try {
            Thread.sleep(j);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean m501a(String str) {
        if (str == null || str.trim().length() <= 0) {
            return true;
        }
        return false;
    }

    public static void m515b(String str) {
        if (str != null) {
            File file = new File(str);
            if (file.isFile() && file.exists() && file.canWrite()) {
                file.delete();
            }
        }
    }

    public static byte[] m523c(long j) {
        try {
            return (j).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long m521c(byte[] bArr) {
        long j = -1;
        if (bArr != null) {
            try {
                j = Long.parseLong(new String(bArr, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return j;
    }

    public static Context m481a(Context context) {
        if (context == null) {
            return context;
        }
        Context applicationContext = context.getApplicationContext();
        return applicationContext != null ? applicationContext : context;
    }

    public static String m510b(Throwable th) {
        if (th == null) {
            return "";
        }
        Writer stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

    public static void m498a(Class<?> cls, String str, Object obj, Object obj2) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            declaredField.set(null, obj);
        } catch (Exception e) {
        }
    }

    public static Object m484a(String str, String str2, Object obj, Class<?>[] clsArr, Object[] objArr) {
        Object obj2 = null;
        try {
            Method declaredMethod = Class.forName(str).getDeclaredMethod(str2, clsArr);
            declaredMethod.setAccessible(true);
            obj2 = declaredMethod.invoke(null, objArr);
        } catch (Exception e) {
        }
        return obj2;
    }

    public static void m497a(Parcel parcel, Map<String, PlugInBean> map) {
        int i = 0;
        if (map == null || map.size() <= 0) {
            parcel.writeBundle(null);
            return;
        }
        int size = map.size();
        ArrayList arrayList = new ArrayList(size);
        ArrayList arrayList2 = new ArrayList(size);
        for (Entry entry : map.entrySet()) {
            arrayList.add(entry.getKey());
            arrayList2.add(entry.getValue());
        }
        Bundle bundle = new Bundle();
        bundle.putInt("pluginNum", arrayList.size());
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            bundle.putString("pluginKey" + i2, (String) arrayList.get(i2));
        }
        while (i < arrayList.size()) {
            bundle.putString("pluginVal" + i + "plugInId", ((PlugInBean) arrayList2.get(i)).f99a);
            bundle.putString("pluginVal" + i + "plugInUUID", ((PlugInBean) arrayList2.get(i)).f101c);
            bundle.putString("pluginVal" + i + "plugInVersion", ((PlugInBean) arrayList2.get(i)).f100b);
            i++;
        }
        parcel.writeBundle(bundle);
    }

    public static Map<String, PlugInBean> m496a(Parcel parcel) {
        Bundle readBundle = parcel.readBundle();
        if (readBundle == null) {
            return null;
        }
        int i;
        HashMap hashMap;
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        int intValue = ((Integer) readBundle.get("pluginNum")).intValue();
        for (i = 0; i < intValue; i++) {
            arrayList.add(readBundle.getString("pluginKey" + i));
        }
        for (i = 0; i < intValue; i++) {
            arrayList2.add(new PlugInBean(readBundle.getString("pluginVal" + i + "plugInId"), readBundle.getString("pluginVal" + i + "plugInVersion"), readBundle.getString("pluginVal" + i + "plugInUUID")));
        }
        if (arrayList.size() == arrayList2.size()) {
            HashMap hashMap2 = new HashMap(arrayList.size());
            for (i = 0; i < arrayList.size(); i++) {
                hashMap2.put(arrayList.get(i), PlugInBean.class.cast(arrayList2.get(i)));
            }
            hashMap = hashMap2;
        } else {
            C0757w.m462e("map plugin parcel error!", new Object[0]);
            Map map = null;
        }
        return hashMap;
    }

    public static void m514b(Parcel parcel, Map<String, String> map) {
        if (map == null || map.size() <= 0) {
            parcel.writeBundle(null);
            return;
        }
        int size = map.size();
        ArrayList arrayList = new ArrayList(size);
        ArrayList arrayList2 = new ArrayList(size);
        for (Entry entry : map.entrySet()) {
            arrayList.add(entry.getKey());
            arrayList2.add(entry.getValue());
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("keys", arrayList);
        bundle.putStringArrayList("values", arrayList2);
        parcel.writeBundle(bundle);
    }

    public static Map<String, String> m512b(Parcel parcel) {
        int i = 0;
        Bundle readBundle = parcel.readBundle();
        if (readBundle == null) {
            return null;
        }
        HashMap hashMap;
        List stringArrayList = readBundle.getStringArrayList("keys");
        List stringArrayList2 = readBundle.getStringArrayList("values");
        if (stringArrayList == null || stringArrayList2 == null || stringArrayList.size() != stringArrayList2.size()) {
            C0757w.m462e("map parcel error!", new Object[0]);
            Map map = null;
        } else {
            HashMap hashMap2 = new HashMap(stringArrayList.size());
            while (i < stringArrayList.size()) {
                hashMap2.put(stringArrayList.get(i), stringArrayList2.get(i));
                i++;
            }
            hashMap = hashMap2;
        }
        return hashMap;
    }

    public static byte[] m504a(Parcelable parcelable) {
        Parcel obtain = Parcel.obtain();
        parcelable.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        return marshall;
    }

    public static String m488a(Context context, int i, String str) {
        String stringBuilder;
        Throwable th;
        if (AppInfo.m77a(context, "android.permission.READ_LOGS")) {
            String[] strArr = str == null ? new String[]{"logcat", "-d", "-v", "threadtime"} : new String[]{"logcat", "-d", "-v", "threadtime", "-s", str};
            Process process = null;
            StringBuilder stringBuilder2 = new StringBuilder();
            try {
                Process exec = Runtime.getRuntime().exec(strArr);
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        stringBuilder2.append(readLine).append("\n");
                        if (i > 0 && stringBuilder2.length() > i) {
                            stringBuilder2.delete(0, stringBuilder2.length() - i);
                        }
                    }
                    stringBuilder = stringBuilder2.toString();
                    if (exec == null) {
                        return stringBuilder;
                    }
                    try {
                        exec.getOutputStream().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        exec.getInputStream().close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    try {
                        exec.getErrorStream().close();
                        return stringBuilder;
                    } catch (IOException e22) {
                        e22.printStackTrace();
                        return stringBuilder;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    process = exec;
                    if (process != null) {
                        process.getOutputStream().close();
                        process.getInputStream().close();
                        process.getErrorStream().close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (C0757w.m457a(th)) {
                    th.printStackTrace();
                }
                stringBuilder = stringBuilder2.append("\n[error:" + th.toString() + "]").toString();
                if (process != null) {
                    return stringBuilder;
                }
                process.getOutputStream().close();
                process.getInputStream().close();
                process.getErrorStream().close();
                return stringBuilder;
            }
        }
        C0757w.m461d("no read_log permission!", new Object[0]);
        return null;
    }

    public static Map<String, String> m495a(int i, boolean z) {
        Map<String, String> hashMap = new HashMap(12);
        Map allStackTraces = Thread.getAllStackTraces();
        if (allStackTraces == null) {
            return null;
        }
        Thread.currentThread().getId();
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : allStackTraces.entrySet()) {
            stringBuilder.setLength(0);
            if (!(entry.getValue() == null || ((StackTraceElement[]) entry.getValue()).length == 0)) {
                for (StackTraceElement stackTraceElement : (StackTraceElement[]) entry.getValue()) {
                    if (i > 0 && stringBuilder.length() >= i) {
                        stringBuilder.append("\n[Stack over limit size :" + i + " , has been cutted !]");
                        break;
                    }
                    stringBuilder.append(stackTraceElement.toString()).append("\n");
                }
                hashMap.put(((Thread) entry.getKey()).getName() + "(" + ((Thread) entry.getKey()).getId() + ")", stringBuilder.toString());
            }
        }
        return hashMap;
    }

    public static synchronized byte[] m502a(int i) {
        byte[] bArr;
        Throwable e;
        Exception e2;
        KeyGenerator instance;
        synchronized (C0761y.class) {
            DataInputStream dataInputStream;
            try {
                bArr = new byte[16];
                dataInputStream = new DataInputStream(new FileInputStream(new File("/dev/urandom")));
                try {
                    dataInputStream.readFully(bArr);
                    try {
                        dataInputStream.close();
                    } catch (Throwable e3) {
                        if (!C0757w.m459b(e3)) {
                            e3.printStackTrace();
                        }
                        bArr = null;
                    }
                } catch (Exception e4) {
                    e2 = e4;
                    try {
                        C0757w.m462e("Failed to read from /dev/urandom : %s", e2);
                        if (dataInputStream != null) {
                            dataInputStream.close();
                        }
                        instance = KeyGenerator.getInstance("AES");
                        instance.init(128, new SecureRandom());
                        bArr = instance.generateKey().getEncoded();
                        return bArr;
                    } catch (Throwable th) {
                        e3 = th;
                        if (dataInputStream != null) {
                            dataInputStream.close();
                        }
                        throw e3;
                    }
                }
            } catch (Exception e5) {
                e2 = e5;
                dataInputStream = null;
                C0757w.m462e("Failed to read from /dev/urandom : %s", e2);
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                instance = KeyGenerator.getInstance("AES");
                instance.init(128, new SecureRandom());
                bArr = instance.generateKey().getEncoded();
                return bArr;
            } catch (Throwable th2) {
                e3 = th2;
                dataInputStream = null;
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                throw e3;
            }
        }
        return bArr;
    }

    public static byte[] m503a(int i, byte[] bArr, byte[] bArr2) {
        try {
            Key secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher instance = Cipher.getInstance("AES/GCM/NoPadding");
            instance.init(i, secretKeySpec, new IvParameterSpec(bArr2));
            return instance.doFinal(bArr);
        } catch (Throwable e) {
            if (!C0757w.m459b(e)) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static byte[] m517b(int i, byte[] bArr, byte[] bArr2) {
        try {
            Key generatePublic = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bArr2));
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(1, generatePublic);
            return instance.doFinal(bArr);
        } catch (Throwable e) {
            if (!C0757w.m459b(e)) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static boolean m499a(Context context, String str, long j) {
        C0757w.m460c("[Util] try to lock file:%s (pid=%d | tid=%d)", str, Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        try {
            File file = new File(context.getFilesDir() + File.separator + str);
            if (file.exists()) {
                if (System.currentTimeMillis() - file.lastModified() < j) {
                    return false;
                }
                C0757w.m460c("[Util] lock file(%s) is expired, unlock it", str);
                C0761y.m516b(context, str);
            }
            if (file.createNewFile()) {
                C0757w.m460c("[Util] successfully locked file:%s (pid=%d | tid=%d)", str, Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
                return true;
            }
            C0757w.m460c("[Util] Failed to locked file:%s (pid=%d | tid=%d)", str, Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
            return false;
        } catch (Throwable th) {
            C0757w.m457a(th);
            return false;
        }
    }

    public static boolean m516b(Context context, String str) {
        C0757w.m460c("[Util] try to unlock file:%s (pid=%d | tid=%d)", str, Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        try {
            File file = new File(context.getFilesDir() + File.separator + str);
            if (!file.exists()) {
                return true;
            }
            if (!file.delete()) {
                return false;
            }
            C0757w.m460c("[Util] successfully unlocked file:%s (pid=%d | tid=%d)", str, Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
            return true;
        } catch (Throwable th) {
            C0757w.m457a(th);
            return false;
        }
    }

    private static BufferedReader m482a(File file) {
        if (file == null || !file.exists() || !file.canRead()) {
            return null;
        }
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        } catch (Throwable th) {
            C0757w.m457a(th);
            return null;
        }
    }

    public static BufferedReader m483a(String str, String str2) {
        BufferedReader bufferedReader = null;
        if (str != null) {
            try {
                File file = new File(str, str2);
                if (file.exists() && file.canRead()) {
                    bufferedReader = C0761y.m482a(file);
                }
            } catch (Throwable e) {
                C0757w.m457a(e);
            }
        }
        return bufferedReader;
    }

    public static Thread m493a(Runnable runnable, String str) {
        try {
            Thread thread = new Thread(runnable);
            thread.setName(str);
            thread.start();
            return thread;
        } catch (Throwable th) {
            C0757w.m462e("[Util] Failed to start a thread to execute task with message: %s", th.getMessage());
            return null;
        }
    }

    public static boolean m522c(String str) {
        boolean z;
        if (str == null || str.trim().length() <= 0) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            return false;
        }
        if (str.length() > 255) {
            C0757w.m456a("URL's length is larger than 255.", new Object[0]);
            return false;
        } else if (!str.toLowerCase().startsWith(HttpHost.DEFAULT_SCHEME_NAME)) {
            C0757w.m456a("URL is not start with \"http\".", new Object[0]);
            return false;
        } else if (str.toLowerCase().contains("qq.com")) {
            return true;
        } else {
            C0757w.m456a("URL does not contain \"qq.com\".", new Object[0]);
            return false;
        }
    }
}
