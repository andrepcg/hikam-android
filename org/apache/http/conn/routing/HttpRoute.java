package org.apache.http.conn.routing;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.RouteInfo.LayerType;
import org.apache.http.conn.routing.RouteInfo.TunnelType;

@Deprecated
public final class HttpRoute implements RouteInfo {
    public HttpRoute(HttpHost target, InetAddress local, HttpHost[] proxies, boolean secure, TunnelType tunnelled, LayerType layered) {
        throw new RuntimeException("Stub!");
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure, TunnelType tunnelled, LayerType layered) {
        throw new RuntimeException("Stub!");
    }

    public HttpRoute(HttpHost target, InetAddress local, boolean secure) {
        throw new RuntimeException("Stub!");
    }

    public HttpRoute(HttpHost target) {
        throw new RuntimeException("Stub!");
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure) {
        throw new RuntimeException("Stub!");
    }

    public final HttpHost getTargetHost() {
        throw new RuntimeException("Stub!");
    }

    public final InetAddress getLocalAddress() {
        throw new RuntimeException("Stub!");
    }

    public final int getHopCount() {
        throw new RuntimeException("Stub!");
    }

    public final HttpHost getHopTarget(int hop) {
        throw new RuntimeException("Stub!");
    }

    public final HttpHost getProxyHost() {
        throw new RuntimeException("Stub!");
    }

    public final TunnelType getTunnelType() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isTunnelled() {
        throw new RuntimeException("Stub!");
    }

    public final LayerType getLayerType() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isLayered() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isSecure() {
        throw new RuntimeException("Stub!");
    }

    public final boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    public final int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final String toString() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}
