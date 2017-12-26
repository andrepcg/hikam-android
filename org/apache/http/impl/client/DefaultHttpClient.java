package org.apache.http.impl.client;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

@Deprecated
public class DefaultHttpClient extends AbstractHttpClient {
    public DefaultHttpClient(ClientConnectionManager conman, HttpParams params) {
        super((ClientConnectionManager) null, (HttpParams) null);
        throw new RuntimeException("Stub!");
    }

    public DefaultHttpClient(HttpParams params) {
        super((ClientConnectionManager) null, (HttpParams) null);
        throw new RuntimeException("Stub!");
    }

    public DefaultHttpClient() {
        super((ClientConnectionManager) null, (HttpParams) null);
        throw new RuntimeException("Stub!");
    }

    protected HttpParams createHttpParams() {
        throw new RuntimeException("Stub!");
    }

    protected HttpRequestExecutor createRequestExecutor() {
        throw new RuntimeException("Stub!");
    }

    protected ClientConnectionManager createClientConnectionManager() {
        throw new RuntimeException("Stub!");
    }

    protected HttpContext createHttpContext() {
        throw new RuntimeException("Stub!");
    }

    protected ConnectionReuseStrategy createConnectionReuseStrategy() {
        throw new RuntimeException("Stub!");
    }

    protected ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
        throw new RuntimeException("Stub!");
    }

    protected AuthSchemeRegistry createAuthSchemeRegistry() {
        throw new RuntimeException("Stub!");
    }

    protected CookieSpecRegistry createCookieSpecRegistry() {
        throw new RuntimeException("Stub!");
    }

    protected BasicHttpProcessor createHttpProcessor() {
        throw new RuntimeException("Stub!");
    }

    protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
        throw new RuntimeException("Stub!");
    }

    protected RedirectHandler createRedirectHandler() {
        throw new RuntimeException("Stub!");
    }

    protected AuthenticationHandler createTargetAuthenticationHandler() {
        throw new RuntimeException("Stub!");
    }

    protected AuthenticationHandler createProxyAuthenticationHandler() {
        throw new RuntimeException("Stub!");
    }

    protected CookieStore createCookieStore() {
        throw new RuntimeException("Stub!");
    }

    protected CredentialsProvider createCredentialsProvider() {
        throw new RuntimeException("Stub!");
    }

    protected HttpRoutePlanner createHttpRoutePlanner() {
        throw new RuntimeException("Stub!");
    }

    protected UserTokenHandler createUserTokenHandler() {
        throw new RuntimeException("Stub!");
    }
}
