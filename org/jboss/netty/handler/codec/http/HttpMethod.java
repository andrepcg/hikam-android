package org.jboss.netty.handler.codec.http;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;

public class HttpMethod implements Comparable<HttpMethod> {
    public static final HttpMethod CONNECT = new HttpMethod("CONNECT");
    public static final HttpMethod DELETE = new HttpMethod(HttpDelete.METHOD_NAME);
    public static final HttpMethod GET = new HttpMethod(HttpGet.METHOD_NAME);
    public static final HttpMethod HEAD = new HttpMethod(HttpHead.METHOD_NAME);
    public static final HttpMethod OPTIONS = new HttpMethod(HttpOptions.METHOD_NAME);
    public static final HttpMethod PATCH = new HttpMethod("PATCH");
    public static final HttpMethod POST = new HttpMethod(HttpPost.METHOD_NAME);
    public static final HttpMethod PUT = new HttpMethod(HttpPut.METHOD_NAME);
    public static final HttpMethod TRACE = new HttpMethod(HttpTrace.METHOD_NAME);
    private static final Map<String, HttpMethod> methodMap = new HashMap();
    private final String name;

    static {
        methodMap.put(OPTIONS.toString(), OPTIONS);
        methodMap.put(GET.toString(), GET);
        methodMap.put(HEAD.toString(), HEAD);
        methodMap.put(POST.toString(), POST);
        methodMap.put(PUT.toString(), PUT);
        methodMap.put(PATCH.toString(), PATCH);
        methodMap.put(DELETE.toString(), DELETE);
        methodMap.put(TRACE.toString(), TRACE);
        methodMap.put(CONNECT.toString(), CONNECT);
    }

    public static HttpMethod valueOf(String name) {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
        }
        name = name.trim();
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }
        HttpMethod result = (HttpMethod) methodMap.get(name);
        return result != null ? result : new HttpMethod(name);
    }

    public HttpMethod(String name) {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
        }
        name = name.trim();
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }
        int i = 0;
        while (i < name.length()) {
            if (Character.isISOControl(name.charAt(i)) || Character.isWhitespace(name.charAt(i))) {
                throw new IllegalArgumentException("invalid character in name");
            }
            i++;
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof HttpMethod)) {
            return false;
        }
        return getName().equals(((HttpMethod) o).getName());
    }

    public String toString() {
        return getName();
    }

    public int compareTo(HttpMethod o) {
        return getName().compareTo(o.getName());
    }
}
