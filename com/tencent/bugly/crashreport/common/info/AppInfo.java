package com.tencent.bugly.crashreport.common.info;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.support.v4.os.EnvironmentCompat;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* compiled from: BUGLY */
public class AppInfo {
    private static ActivityManager f98a;

    static {
        "@buglyAllChannel@".split(",");
        "@buglyAllChannelPriority@".split(",");
    }

    public static String m74a(Context context) {
        if (context == null) {
            return null;
        }
        try {
            return context.getPackageName();
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return "fail";
        }
    }

    public static PackageInfo m78b(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(m74a(context), 0);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    public static boolean m77a(Context context, String str) {
        if (context == null || str == null || str.trim().length() <= 0) {
            return false;
        }
        try {
            String[] strArr = context.getPackageManager().getPackageInfo(context.getPackageName(), 4096).requestedPermissions;
            if (strArr == null) {
                return false;
            }
            for (Object equals : strArr) {
                if (str.equals(equals)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return false;
            }
            th.printStackTrace();
            return false;
        }
    }

    public static String m73a(int i) {
        String substring;
        Throwable th;
        int i2 = 0;
        FileReader fileReader;
        try {
            fileReader = new FileReader("/proc/" + i + "/cmdline");
            try {
                char[] cArr = new char[512];
                fileReader.read(cArr);
                while (i2 < cArr.length && cArr[i2] != '\u0000') {
                    i2++;
                }
                substring = new String(cArr).substring(0, i2);
                try {
                    fileReader.close();
                } catch (Throwable th2) {
                }
            } catch (Throwable th3) {
                th = th3;
                try {
                    if (!C0757w.m457a(th)) {
                        th.printStackTrace();
                    }
                    substring = String.valueOf(i);
                    if (fileReader != null) {
                        try {
                            fileReader.close();
                        } catch (Throwable th4) {
                        }
                    }
                    return substring;
                } catch (Throwable th5) {
                    th = th5;
                    if (fileReader != null) {
                        try {
                            fileReader.close();
                        } catch (Throwable th6) {
                        }
                    }
                    throw th;
                }
            }
        } catch (Throwable th7) {
            th = th7;
            fileReader = null;
            if (fileReader != null) {
                fileReader.close();
            }
            throw th;
        }
        return substring;
    }

    public static String m79c(Context context) {
        String str = null;
        if (context != null) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = context.getApplicationInfo();
                if (!(packageManager == null || applicationInfo == null)) {
                    str = packageManager.getApplicationLabel(applicationInfo).toString();
                }
            } catch (Throwable th) {
                if (!C0757w.m457a(th)) {
                    th.printStackTrace();
                }
            }
        }
        return str;
    }

    public static Map<String, String> m80d(Context context) {
        if (context == null) {
            return null;
        }
        try {
            HashMap hashMap;
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            if (applicationInfo.metaData != null) {
                hashMap = new HashMap();
                Object obj = applicationInfo.metaData.get("BUGLY_DISABLE");
                if (obj != null) {
                    hashMap.put("BUGLY_DISABLE", obj.toString());
                }
                obj = applicationInfo.metaData.get("BUGLY_APPID");
                if (obj != null) {
                    hashMap.put("BUGLY_APPID", obj.toString());
                }
                obj = applicationInfo.metaData.get("BUGLY_APP_CHANNEL");
                if (obj != null) {
                    hashMap.put("BUGLY_APP_CHANNEL", obj.toString());
                }
                obj = applicationInfo.metaData.get("BUGLY_APP_VERSION");
                if (obj != null) {
                    hashMap.put("BUGLY_APP_VERSION", obj.toString());
                }
                obj = applicationInfo.metaData.get("BUGLY_ENABLE_DEBUG");
                if (obj != null) {
                    hashMap.put("BUGLY_ENABLE_DEBUG", obj.toString());
                }
                Object obj2 = applicationInfo.metaData.get("com.tencent.rdm.uuid");
                if (obj2 != null) {
                    hashMap.put("com.tencent.rdm.uuid", obj2.toString());
                }
            } else {
                hashMap = null;
            }
            return hashMap;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    public static List<String> m76a(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        try {
            String str = (String) map.get("BUGLY_DISABLE");
            if (str == null || str.length() == 0) {
                return null;
            }
            String[] split = str.split(",");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
            }
            return Arrays.asList(split);
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }

    private static String m75a(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bArr != null && bArr.length > 0) {
            try {
                CertificateFactory instance = CertificateFactory.getInstance("X.509");
                if (instance == null) {
                    return null;
                }
                X509Certificate x509Certificate = (X509Certificate) instance.generateCertificate(new ByteArrayInputStream(bArr));
                if (x509Certificate == null) {
                    return null;
                }
                stringBuilder.append("Issuer|");
                Principal issuerDN = x509Certificate.getIssuerDN();
                if (issuerDN != null) {
                    stringBuilder.append(issuerDN.toString());
                } else {
                    stringBuilder.append(EnvironmentCompat.MEDIA_UNKNOWN);
                }
                stringBuilder.append("\n");
                stringBuilder.append("SerialNumber|");
                BigInteger serialNumber = x509Certificate.getSerialNumber();
                if (issuerDN != null) {
                    stringBuilder.append(serialNumber.toString(16));
                } else {
                    stringBuilder.append(EnvironmentCompat.MEDIA_UNKNOWN);
                }
                stringBuilder.append("\n");
                stringBuilder.append("NotBefore|");
                Date notBefore = x509Certificate.getNotBefore();
                if (issuerDN != null) {
                    stringBuilder.append(notBefore.toString());
                } else {
                    stringBuilder.append(EnvironmentCompat.MEDIA_UNKNOWN);
                }
                stringBuilder.append("\n");
                stringBuilder.append("NotAfter|");
                notBefore = x509Certificate.getNotAfter();
                if (issuerDN != null) {
                    stringBuilder.append(notBefore.toString());
                } else {
                    stringBuilder.append(EnvironmentCompat.MEDIA_UNKNOWN);
                }
                stringBuilder.append("\n");
                stringBuilder.append("SHA1|");
                String a = C0761y.m492a(MessageDigest.getInstance("SHA1").digest(x509Certificate.getEncoded()));
                if (a == null || a.length() <= 0) {
                    stringBuilder.append(EnvironmentCompat.MEDIA_UNKNOWN);
                } else {
                    stringBuilder.append(a.toString());
                }
                stringBuilder.append("\n");
                stringBuilder.append("MD5|");
                String a2 = C0761y.m492a(MessageDigest.getInstance("MD5").digest(x509Certificate.getEncoded()));
                if (a2 == null || a2.length() <= 0) {
                    stringBuilder.append(EnvironmentCompat.MEDIA_UNKNOWN);
                } else {
                    stringBuilder.append(a2.toString());
                }
            } catch (Throwable e) {
                if (!C0757w.m457a(e)) {
                    e.printStackTrace();
                }
            } catch (Throwable e2) {
                if (!C0757w.m457a(e2)) {
                    e2.printStackTrace();
                }
            }
        }
        if (stringBuilder.length() == 0) {
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
        return stringBuilder.toString();
    }

    public static String m81e(Context context) {
        String a = m74a(context);
        if (a == null) {
            return null;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(a, 64);
            if (packageInfo == null) {
                return null;
            }
            Signature[] signatureArr = packageInfo.signatures;
            if (signatureArr == null || signatureArr.length == 0) {
                return null;
            }
            return m75a(packageInfo.signatures[0].toByteArray());
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static boolean m82f(Context context) {
        if (context == null) {
            return false;
        }
        if (f98a == null) {
            f98a = (ActivityManager) context.getSystemService("activity");
        }
        try {
            MemoryInfo memoryInfo = new MemoryInfo();
            f98a.getMemoryInfo(memoryInfo);
            if (!memoryInfo.lowMemory) {
                return false;
            }
            C0757w.m460c("Memory is low.", new Object[0]);
            return true;
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return false;
        }
    }
}
