package org.apache.http.params;

import org.apache.http.HttpVersion;

@Deprecated
public class HttpProtocolParamBean extends HttpAbstractParamBean {
    public HttpProtocolParamBean(HttpParams params) {
        super((HttpParams) null);
        throw new RuntimeException("Stub!");
    }

    public void setHttpElementCharset(String httpElementCharset) {
        throw new RuntimeException("Stub!");
    }

    public void setContentCharset(String contentCharset) {
        throw new RuntimeException("Stub!");
    }

    public void setVersion(HttpVersion version) {
        throw new RuntimeException("Stub!");
    }

    public void setUserAgent(String userAgent) {
        throw new RuntimeException("Stub!");
    }

    public void setUseExpectContinue(boolean useExpectContinue) {
        throw new RuntimeException("Stub!");
    }
}
