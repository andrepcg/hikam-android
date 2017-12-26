package android.net.http;

import android.content.Context;
import android.net.compatibility.WebAddress;
import java.io.InputStream;
import java.util.Map;
import org.apache.http.HttpHost;

public class RequestQueue implements RequestFeeder {
    public RequestQueue(Context context) {
        throw new RuntimeException("Stub!");
    }

    public RequestQueue(Context context, int connectionCount) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void enablePlatformNotifications() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void disablePlatformNotifications() {
        throw new RuntimeException("Stub!");
    }

    public HttpHost getProxyHost() {
        throw new RuntimeException("Stub!");
    }

    public RequestHandle queueRequest(String url, String method, Map<String, String> map, EventHandler eventHandler, InputStream bodyProvider, int bodyLength) {
        throw new RuntimeException("Stub!");
    }

    public RequestHandle queueRequest(String url, WebAddress uri, String method, Map<String, String> map, EventHandler eventHandler, InputStream bodyProvider, int bodyLength) {
        throw new RuntimeException("Stub!");
    }

    public RequestHandle queueSynchronousRequest(String url, WebAddress uri, String method, Map<String, String> map, EventHandler eventHandler, InputStream bodyProvider, int bodyLength) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Request getRequest() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Request getRequest(HttpHost host) {
        throw new RuntimeException("Stub!");
    }

    public synchronized boolean haveRequest(HttpHost host) {
        throw new RuntimeException("Stub!");
    }

    public void requeueRequest(Request request) {
        throw new RuntimeException("Stub!");
    }

    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    protected synchronized void queueRequest(Request request, boolean head) {
        throw new RuntimeException("Stub!");
    }

    public void startTiming() {
        throw new RuntimeException("Stub!");
    }

    public void stopTiming() {
        throw new RuntimeException("Stub!");
    }
}
