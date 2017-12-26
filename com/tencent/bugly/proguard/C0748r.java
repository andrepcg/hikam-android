package com.tencent.bugly.proguard;

import android.content.Context;
import android.os.Process;
import android.os.SystemClock;
import com.tencent.bugly.BuglyStrategy.C0691a;
import com.tencent.bugly.crashreport.common.info.C0706b;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;

/* compiled from: BUGLY */
public final class C0748r {
    private static C0748r f411b;
    public Map<String, String> f412a = null;
    private Context f413c;

    private C0748r(Context context) {
        this.f413c = context;
    }

    public static C0748r m405a(Context context) {
        if (f411b == null) {
            f411b = new C0748r(context);
        }
        return f411b;
    }

    public final byte[] m410a(String str, byte[] bArr, C0754u c0754u, Map<String, String> map) {
        Throwable th;
        int i;
        int i2;
        if (str == null) {
            C0757w.m462e("Failed for no URL.", new Object[0]);
            return null;
        }
        long j;
        int i3 = 0;
        int i4 = 0;
        if (bArr == null) {
            j = 0;
        } else {
            j = (long) bArr.length;
        }
        C0757w.m460c("request: %s, send: %d (pid=%d | tid=%d)", str, Long.valueOf(j), Integer.valueOf(Process.myPid()), Integer.valueOf(Process.myTid()));
        Object obj = null;
        String str2 = str;
        while (i3 <= 0 && i4 <= 0) {
            if (obj != null) {
                obj = null;
            } else {
                i3++;
                if (i3 > 1) {
                    C0757w.m460c("try time: " + i3, new Object[0]);
                    SystemClock.sleep(((long) new Random(System.currentTimeMillis()).nextInt(10000)) + 10000);
                }
            }
            String e = C0706b.m149e(this.f413c);
            if (e == null) {
                C0757w.m461d("Failed to request for network not avail", new Object[0]);
            } else {
                c0754u.m447a(j);
                HttpURLConnection a = m407a(str2, bArr, e, (Map) map);
                if (a != null) {
                    try {
                        int responseCode = a.getResponseCode();
                        if (responseCode == 200) {
                            long j2;
                            this.f412a = C0748r.m408a(a);
                            byte[] b = C0748r.m409b(a);
                            if (b == null) {
                                j2 = 0;
                            } else {
                                j2 = (long) b.length;
                            }
                            c0754u.m448b(j2);
                            try {
                                a.disconnect();
                            } catch (Throwable th2) {
                                if (!C0757w.m457a(th2)) {
                                    th2.printStackTrace();
                                }
                            }
                            return b;
                        }
                        Object obj2 = (responseCode == HttpStatus.SC_MOVED_PERMANENTLY || responseCode == HttpStatus.SC_MOVED_TEMPORARILY || responseCode == HttpStatus.SC_SEE_OTHER || responseCode == 307) ? 1 : null;
                        if (obj2 != null) {
                            try {
                                String headerField = a.getHeaderField(Names.LOCATION);
                                if (headerField == null) {
                                    C0757w.m462e("Failed to redirect: %d" + responseCode, new Object[0]);
                                    try {
                                        a.disconnect();
                                    } catch (Throwable th22) {
                                        if (!C0757w.m457a(th22)) {
                                            th22.printStackTrace();
                                        }
                                    }
                                    return null;
                                }
                                i4++;
                                try {
                                    C0757w.m460c("redirect code: %d ,to:%s", Integer.valueOf(responseCode), headerField);
                                    obj = 1;
                                    str2 = headerField;
                                    i3 = i4;
                                    i4 = 0;
                                } catch (Throwable e2) {
                                    str2 = headerField;
                                    th = e2;
                                    i = 1;
                                    i3 = i4;
                                    i4 = 0;
                                    try {
                                        if (!C0757w.m457a(th)) {
                                            th.printStackTrace();
                                        }
                                        try {
                                            a.disconnect();
                                        } catch (Throwable th3) {
                                            if (!C0757w.m457a(th3)) {
                                                th3.printStackTrace();
                                            }
                                        }
                                        i2 = i3;
                                        i3 = i4;
                                        i4 = i2;
                                    } catch (Throwable th4) {
                                        if (!C0757w.m457a(th4)) {
                                            th4.printStackTrace();
                                        }
                                    }
                                }
                            } catch (Throwable e22) {
                                Throwable th5 = e22;
                                i = 1;
                                th3 = th5;
                                int i5 = i4;
                                i4 = i3;
                                i3 = i5;
                                if (C0757w.m457a(th3)) {
                                    th3.printStackTrace();
                                }
                                a.disconnect();
                                i2 = i3;
                                i3 = i4;
                                i4 = i2;
                            }
                        } else {
                            i2 = i4;
                            i4 = i3;
                            i3 = i2;
                        }
                        try {
                            C0757w.m461d("response code " + responseCode, new Object[0]);
                            long contentLength = (long) a.getContentLength();
                            if (contentLength < 0) {
                                contentLength = 0;
                            }
                            c0754u.m448b(contentLength);
                            try {
                                a.disconnect();
                            } catch (Throwable th32) {
                                if (!C0757w.m457a(th32)) {
                                    th32.printStackTrace();
                                }
                            }
                        } catch (IOException e3) {
                            th32 = e3;
                            if (C0757w.m457a(th32)) {
                                th32.printStackTrace();
                            }
                            a.disconnect();
                            i2 = i3;
                            i3 = i4;
                            i4 = i2;
                        }
                    } catch (IOException e4) {
                        th32 = e4;
                        i2 = i4;
                        i4 = i3;
                        i3 = i2;
                    }
                } else {
                    C0757w.m460c("Failed to execute post.", new Object[0]);
                    c0754u.m448b(0);
                    i2 = i4;
                    i4 = i3;
                    i3 = i2;
                }
                i2 = i3;
                i3 = i4;
                i4 = i2;
            }
        }
        return null;
    }

    private static Map<String, String> m408a(HttpURLConnection httpURLConnection) {
        HashMap hashMap = new HashMap();
        Map headerFields = httpURLConnection.getHeaderFields();
        if (headerFields == null || headerFields.size() == 0) {
            return null;
        }
        for (String str : headerFields.keySet()) {
            List list = (List) headerFields.get(str);
            if (list.size() > 0) {
                hashMap.put(str, list.get(0));
            }
        }
        return hashMap;
    }

    private static byte[] m409b(HttpURLConnection httpURLConnection) {
        BufferedInputStream bufferedInputStream;
        Throwable th;
        Throwable th2;
        byte[] bArr = null;
        if (httpURLConnection != null) {
            try {
                bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] bArr2 = new byte[1024];
                    while (true) {
                        int read = bufferedInputStream.read(bArr2);
                        if (read <= 0) {
                            break;
                        }
                        byteArrayOutputStream.write(bArr2, 0, read);
                    }
                    byteArrayOutputStream.flush();
                    bArr = byteArrayOutputStream.toByteArray();
                    try {
                        bufferedInputStream.close();
                    } catch (Throwable th3) {
                        th3.printStackTrace();
                    }
                } catch (Throwable th4) {
                    th3 = th4;
                    try {
                        if (!C0757w.m457a(th3)) {
                            th3.printStackTrace();
                        }
                        if (bufferedInputStream != null) {
                            try {
                                bufferedInputStream.close();
                            } catch (Throwable th32) {
                                th32.printStackTrace();
                            }
                        }
                        return bArr;
                    } catch (Throwable th5) {
                        th2 = th5;
                        if (bufferedInputStream != null) {
                            try {
                                bufferedInputStream.close();
                            } catch (Throwable th322) {
                                th322.printStackTrace();
                            }
                        }
                        throw th2;
                    }
                }
            } catch (Throwable th3222) {
                bufferedInputStream = bArr;
                th2 = th3222;
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                throw th2;
            }
        }
        return bArr;
    }

    private HttpURLConnection m407a(String str, byte[] bArr, String str2, Map<String, String> map) {
        if (str == null) {
            C0757w.m462e("destUrl is null.", new Object[0]);
            return null;
        }
        HttpURLConnection a = C0748r.m406a(str2, str);
        if (a == null) {
            C0757w.m462e("Failed to get HttpURLConnection object.", new Object[0]);
            return null;
        }
        try {
            a.setRequestProperty("wup_version", "3.0");
            if (map != null && map.size() > 0) {
                for (Entry entry : map.entrySet()) {
                    a.setRequestProperty((String) entry.getKey(), URLEncoder.encode((String) entry.getValue(), "utf-8"));
                }
            }
            a.setRequestProperty("A37", URLEncoder.encode(str2, "utf-8"));
            a.setRequestProperty("A38", URLEncoder.encode(str2, "utf-8"));
            a.connect();
            OutputStream outputStream = a.getOutputStream();
            if (bArr == null) {
                outputStream.write(0);
            } else {
                outputStream.write(bArr);
            }
            return a;
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            C0757w.m462e("Failed to upload crash, please check your network.", new Object[0]);
            return null;
        }
    }

    private static HttpURLConnection m406a(String str, String str2) {
        try {
            HttpURLConnection httpURLConnection;
            URL url = new URL(str2);
            if (str == null || !str.toLowerCase(Locale.US).contains("wap")) {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } else {
                httpURLConnection = (HttpURLConnection) url.openConnection(new Proxy(Type.HTTP, new InetSocketAddress(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")))));
            }
            httpURLConnection.setConnectTimeout(C0691a.MAX_USERDATA_VALUE_LENGTH);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod(HttpPost.METHOD_NAME);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setInstanceFollowRedirects(false);
            return httpURLConnection;
        } catch (Throwable th) {
            if (!C0757w.m457a(th)) {
                th.printStackTrace();
            }
            return null;
        }
    }
}
