package org.apache.http.auth;

import java.util.List;
import java.util.Map;
import org.apache.http.params.HttpParams;

@Deprecated
public final class AuthSchemeRegistry {
    public AuthSchemeRegistry() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void register(String name, AuthSchemeFactory factory) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void unregister(String name) {
        throw new RuntimeException("Stub!");
    }

    public synchronized AuthScheme getAuthScheme(String name, HttpParams params) throws IllegalStateException {
        throw new RuntimeException("Stub!");
    }

    public synchronized List<String> getSchemeNames() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void setItems(Map<String, AuthSchemeFactory> map) {
        throw new RuntimeException("Stub!");
    }
}
