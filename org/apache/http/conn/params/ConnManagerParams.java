package org.apache.http.conn.params;

import org.apache.http.params.HttpParams;

@Deprecated
public final class ConnManagerParams implements ConnManagerPNames {
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;

    public ConnManagerParams() {
        throw new RuntimeException("Stub!");
    }

    public static long getTimeout(HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    public static void setTimeout(HttpParams params, long timeout) {
        throw new RuntimeException("Stub!");
    }

    public static void setMaxConnectionsPerRoute(HttpParams params, ConnPerRoute connPerRoute) {
        throw new RuntimeException("Stub!");
    }

    public static ConnPerRoute getMaxConnectionsPerRoute(HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    public static void setMaxTotalConnections(HttpParams params, int maxTotalConnections) {
        throw new RuntimeException("Stub!");
    }

    public static int getMaxTotalConnections(HttpParams params) {
        throw new RuntimeException("Stub!");
    }
}
