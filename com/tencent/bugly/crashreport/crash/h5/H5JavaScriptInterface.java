package com.tencent.bugly.crashreport.crash.h5;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.tencent.bugly.crashreport.inner.InnerApi;
import com.tencent.bugly.proguard.C0757w;
import com.tencent.bugly.proguard.C0761y;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values;
import org.json.JSONObject;

/* compiled from: BUGLY */
public class H5JavaScriptInterface {
    private static HashSet<Integer> f330a = new HashSet();
    private String f331b = null;
    private Thread f332c = null;
    private String f333d = null;
    private Map<String, String> f334e = null;

    private H5JavaScriptInterface() {
    }

    public static H5JavaScriptInterface getInstance(WebView webView) {
        String str = null;
        if (webView == null || f330a.contains(Integer.valueOf(webView.hashCode()))) {
            return null;
        }
        H5JavaScriptInterface h5JavaScriptInterface = new H5JavaScriptInterface();
        f330a.add(Integer.valueOf(webView.hashCode()));
        h5JavaScriptInterface.f332c = Thread.currentThread();
        Thread thread = h5JavaScriptInterface.f332c;
        if (thread != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 2; i < thread.getStackTrace().length; i++) {
                StackTraceElement stackTraceElement = thread.getStackTrace()[i];
                if (!stackTraceElement.toString().contains("crashreport")) {
                    stringBuilder.append(stackTraceElement.toString()).append("\n");
                }
            }
            str = stringBuilder.toString();
        }
        h5JavaScriptInterface.f333d = str;
        Map hashMap = new HashMap();
        hashMap.put("[WebView] ContentDescription", webView.getContentDescription());
        h5JavaScriptInterface.f334e = hashMap;
        return h5JavaScriptInterface;
    }

    private static C0726a m252a(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            C0726a c0726a = new C0726a();
            c0726a.f335a = jSONObject.getString("projectRoot");
            if (c0726a.f335a == null) {
                return null;
            }
            c0726a.f336b = jSONObject.getString("context");
            if (c0726a.f336b == null) {
                return null;
            }
            c0726a.f337c = jSONObject.getString(Values.URL);
            if (c0726a.f337c == null) {
                return null;
            }
            c0726a.f338d = jSONObject.getString("userAgent");
            if (c0726a.f338d == null) {
                return null;
            }
            c0726a.f339e = jSONObject.getString("language");
            if (c0726a.f339e == null) {
                return null;
            }
            c0726a.f340f = jSONObject.getString(HttpPostBodyUtil.NAME);
            if (c0726a.f340f == null || c0726a.f340f.equals("null")) {
                return null;
            }
            String string = jSONObject.getString("stacktrace");
            if (string == null) {
                return null;
            }
            int indexOf = string.indexOf("\n");
            if (indexOf < 0) {
                C0757w.m461d("H5 crash stack's format is wrong!", new Object[0]);
                return null;
            }
            c0726a.f342h = string.substring(indexOf + 1);
            c0726a.f341g = string.substring(0, indexOf);
            int indexOf2 = c0726a.f341g.indexOf(":");
            if (indexOf2 > 0) {
                c0726a.f341g = c0726a.f341g.substring(indexOf2 + 1);
            }
            c0726a.f343i = jSONObject.getString(HttpPostBodyUtil.FILE);
            if (c0726a.f340f == null) {
                return null;
            }
            c0726a.f344j = jSONObject.getLong("lineNumber");
            if (c0726a.f344j < 0) {
                return null;
            }
            c0726a.f345k = jSONObject.getLong("columnNumber");
            if (c0726a.f345k < 0) {
                return null;
            }
            C0757w.m456a("H5 crash information is following: ", new Object[0]);
            C0757w.m456a("[projectRoot]: " + c0726a.f335a, new Object[0]);
            C0757w.m456a("[context]: " + c0726a.f336b, new Object[0]);
            C0757w.m456a("[url]: " + c0726a.f337c, new Object[0]);
            C0757w.m456a("[userAgent]: " + c0726a.f338d, new Object[0]);
            C0757w.m456a("[language]: " + c0726a.f339e, new Object[0]);
            C0757w.m456a("[name]: " + c0726a.f340f, new Object[0]);
            C0757w.m456a("[message]: " + c0726a.f341g, new Object[0]);
            C0757w.m456a("[stacktrace]: \n" + c0726a.f342h, new Object[0]);
            C0757w.m456a("[file]: " + c0726a.f343i, new Object[0]);
            C0757w.m456a("[lineNumber]: " + c0726a.f344j, new Object[0]);
            C0757w.m456a("[columnNumber]: " + c0726a.f345k, new Object[0]);
            return c0726a;
        } catch (Throwable th) {
            if (C0757w.m457a(th)) {
                return null;
            }
            th.printStackTrace();
            return null;
        }
    }

    @JavascriptInterface
    public void printLog(String str) {
        C0757w.m461d("Log from js: %s", str);
    }

    @JavascriptInterface
    public void reportJSException(String str) {
        if (str == null) {
            C0757w.m461d("Payload from JS is null.", new Object[0]);
            return;
        }
        String b = C0761y.m511b(str.getBytes());
        if (this.f331b == null || !this.f331b.equals(b)) {
            this.f331b = b;
            C0757w.m461d("Handling JS exception ...", new Object[0]);
            C0726a a = m252a(str);
            if (a == null) {
                C0757w.m461d("Failed to parse payload.", new Object[0]);
                return;
            }
            Map linkedHashMap = new LinkedHashMap();
            Map linkedHashMap2 = new LinkedHashMap();
            if (a.f335a != null) {
                linkedHashMap2.put("[JS] projectRoot", a.f335a);
            }
            if (a.f336b != null) {
                linkedHashMap2.put("[JS] context", a.f336b);
            }
            if (a.f337c != null) {
                linkedHashMap2.put("[JS] url", a.f337c);
            }
            if (a.f338d != null) {
                linkedHashMap2.put("[JS] userAgent", a.f338d);
            }
            if (a.f343i != null) {
                linkedHashMap2.put("[JS] file", a.f343i);
            }
            if (a.f344j != 0) {
                linkedHashMap2.put("[JS] lineNumber", Long.toString(a.f344j));
            }
            linkedHashMap.putAll(linkedHashMap2);
            linkedHashMap.putAll(this.f334e);
            linkedHashMap.put("Java Stack", this.f333d);
            Thread thread = this.f332c;
            if (a != null) {
                InnerApi.postH5CrashAsync(thread, a.f340f, a.f341g, a.f342h, linkedHashMap);
                return;
            }
            return;
        }
        C0757w.m461d("Same payload from js. Please check whether you've injected bugly.js more than one times.", new Object[0]);
    }
}
