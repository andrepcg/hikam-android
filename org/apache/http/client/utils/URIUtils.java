package org.apache.http.client.utils;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;

@Deprecated
public class URIUtils {
    URIUtils() {
        throw new RuntimeException("Stub!");
    }

    public static URI createURI(String scheme, String host, int port, String path, String query, String fragment) throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    public static URI rewriteURI(URI uri, HttpHost target, boolean dropFragment) throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    public static URI rewriteURI(URI uri, HttpHost target) throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    public static URI resolve(URI baseURI, String reference) {
        throw new RuntimeException("Stub!");
    }

    public static URI resolve(URI baseURI, URI reference) {
        throw new RuntimeException("Stub!");
    }
}
