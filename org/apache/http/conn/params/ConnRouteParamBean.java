package org.apache.http.conn.params;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpAbstractParamBean;
import org.apache.http.params.HttpParams;

@Deprecated
public class ConnRouteParamBean extends HttpAbstractParamBean {
    public ConnRouteParamBean(HttpParams params) {
        super((HttpParams) null);
        throw new RuntimeException("Stub!");
    }

    public void setDefaultProxy(HttpHost defaultProxy) {
        throw new RuntimeException("Stub!");
    }

    public void setLocalAddress(InetAddress address) {
        throw new RuntimeException("Stub!");
    }

    public void setForcedRoute(HttpRoute route) {
        throw new RuntimeException("Stub!");
    }
}
