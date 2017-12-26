package com.jwkj.net;

import android.util.Log;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.HttpVersion;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;

public class HKHttpClient {
    public static final MediaType TYPE_ALTERNATIVE = MediaType.parse("multipart/alternative");
    public static final MediaType TYPE_DIGEST = MediaType.parse("multipart/digest");
    public static final MediaType TYPE_FORM = MediaType.parse(Values.MULTIPART_FORM_DATA);
    public static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TYPE_MIXED = MediaType.parse(HttpPostBodyUtil.MULTIPART_MIXED);
    public static final MediaType TYPE_PARALLEL = MediaType.parse("multipart/parallel");
    private static HKHttpClient instance;
    private OkHttpClient client;
    private Gson gson;

    class C05631 implements X509TrustManager {
        C05631() {
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    class C05642 implements HostnameVerifier {
        C05642() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public interface HKCallback {
        void onFailure(Call call, IOException iOException);

        void onResponse(Call call, Response response);
    }

    private HKHttpClient() {
        X509TrustManager xtm = new C05631();
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e2) {
            e2.printStackTrace();
        }
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        this.client = new Builder().sslSocketFactory(sslSocketFactory).hostnameVerifier(new C05642()).build();
        this.gson = new Gson();
    }

    public static synchronized HKHttpClient getInstance() {
        HKHttpClient hKHttpClient;
        synchronized (HKHttpClient.class) {
            if (instance == null) {
                instance = new HKHttpClient();
            }
            hKHttpClient = instance;
        }
        return hKHttpClient;
    }

    public <T> void asyncPost(T obj, String url, HKCallback callback) {
        final HKCallback mCallback = callback;
        byte[] data = new byte[0];
        try {
            data = this.gson.toJson((Object) obj).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Call call = this.client.newCall(new Request.Builder().url(url).post(RequestBody.create(TYPE_JSON, data)).build());
        Log.i(HttpVersion.HTTP, "Enqueue Req :" + this.gson.toJson((Object) obj));
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                if (mCallback != null) {
                    mCallback.onFailure(call, e);
                }
                Log.i(HttpVersion.HTTP, "Callback Failure error_string:[" + e.toString() + "] \n call_string:[" + call.toString() + "]");
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (mCallback != null) {
                    mCallback.onResponse(call, response);
                }
                Log.i(HttpVersion.HTTP, "Callback onResponse state_code:[" + response.code() + "]");
            }
        });
    }
}
