package org.apache.http.client.protocol;

import java.util.List;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.protocol.HttpContext;

@Deprecated
public class ClientContextConfigurer implements ClientContext {
    public ClientContextConfigurer(HttpContext context) {
        throw new RuntimeException("Stub!");
    }

    public void setCookieSpecRegistry(CookieSpecRegistry registry) {
        throw new RuntimeException("Stub!");
    }

    public void setAuthSchemeRegistry(AuthSchemeRegistry registry) {
        throw new RuntimeException("Stub!");
    }

    public void setCookieStore(CookieStore store) {
        throw new RuntimeException("Stub!");
    }

    public void setCredentialsProvider(CredentialsProvider provider) {
        throw new RuntimeException("Stub!");
    }

    public void setAuthSchemePref(List<String> list) {
        throw new RuntimeException("Stub!");
    }
}
