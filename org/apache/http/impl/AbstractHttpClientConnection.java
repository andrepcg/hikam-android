package org.apache.http.impl;

import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;

@Deprecated
public abstract class AbstractHttpClientConnection implements HttpClientConnection {
    protected abstract void assertOpen() throws IllegalStateException;

    public AbstractHttpClientConnection() {
        throw new RuntimeException("Stub!");
    }

    protected EntityDeserializer createEntityDeserializer() {
        throw new RuntimeException("Stub!");
    }

    protected EntitySerializer createEntitySerializer() {
        throw new RuntimeException("Stub!");
    }

    protected HttpResponseFactory createHttpResponseFactory() {
        throw new RuntimeException("Stub!");
    }

    protected HttpMessageParser createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    protected HttpMessageWriter createRequestWriter(SessionOutputBuffer buffer, HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    protected void init(SessionInputBuffer inbuffer, SessionOutputBuffer outbuffer, HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    public boolean isResponseAvailable(int timeout) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        throw new RuntimeException("Stub!");
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        throw new RuntimeException("Stub!");
    }

    protected void doFlush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        throw new RuntimeException("Stub!");
    }

    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        throw new RuntimeException("Stub!");
    }

    public boolean isStale() {
        throw new RuntimeException("Stub!");
    }

    public HttpConnectionMetrics getMetrics() {
        throw new RuntimeException("Stub!");
    }
}
