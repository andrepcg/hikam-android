package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;

@Deprecated
public class BasicHeaderElement implements HeaderElement {
    public BasicHeaderElement(String name, String value, NameValuePair[] parameters) {
        throw new RuntimeException("Stub!");
    }

    public BasicHeaderElement(String name, String value) {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public String getValue() {
        throw new RuntimeException("Stub!");
    }

    public NameValuePair[] getParameters() {
        throw new RuntimeException("Stub!");
    }

    public int getParameterCount() {
        throw new RuntimeException("Stub!");
    }

    public NameValuePair getParameter(int index) {
        throw new RuntimeException("Stub!");
    }

    public NameValuePair getParameterByName(String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}
