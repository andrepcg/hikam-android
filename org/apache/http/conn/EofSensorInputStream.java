package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;

@Deprecated
public class EofSensorInputStream extends InputStream implements ConnectionReleaseTrigger {
    protected InputStream wrappedStream;

    public EofSensorInputStream(InputStream in, EofSensorWatcher watcher) {
        throw new RuntimeException("Stub!");
    }

    protected boolean isReadAllowed() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read(byte[] b, int off, int len) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read(byte[] b) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void checkEOF(int eof) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void checkClose() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void checkAbort() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void releaseConnection() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void abortConnection() throws IOException {
        throw new RuntimeException("Stub!");
    }
}
