package org.apache.http.impl.conn.tsccm;

import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

@Deprecated
public class ThreadSafeClientConnManager implements ClientConnectionManager {
    protected ClientConnectionOperator connOperator;
    protected final AbstractConnPool connectionPool;
    protected SchemeRegistry schemeRegistry;

    public ThreadSafeClientConnManager(HttpParams params, SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() throws Throwable {
        throw new RuntimeException("Stub!");
    }

    protected AbstractConnPool createConnectionPool(HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    public SchemeRegistry getSchemeRegistry() {
        throw new RuntimeException("Stub!");
    }

    public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
        throw new RuntimeException("Stub!");
    }

    public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
        throw new RuntimeException("Stub!");
    }

    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    public int getConnectionsInPool(HttpRoute route) {
        throw new RuntimeException("Stub!");
    }

    public int getConnectionsInPool() {
        throw new RuntimeException("Stub!");
    }

    public void closeIdleConnections(long idleTimeout, TimeUnit tunit) {
        throw new RuntimeException("Stub!");
    }

    public void closeExpiredConnections() {
        throw new RuntimeException("Stub!");
    }
}
