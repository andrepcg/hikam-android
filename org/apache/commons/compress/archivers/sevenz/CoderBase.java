package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class CoderBase {
    private static final byte[] NONE = new byte[0];
    private final Class<?>[] acceptableOptions;

    abstract InputStream decode(String str, InputStream inputStream, long j, Coder coder, byte[] bArr) throws IOException;

    protected CoderBase(Class<?>... acceptableOptions) {
        this.acceptableOptions = acceptableOptions;
    }

    boolean canAcceptOptions(Object opts) {
        for (Class<?> c : this.acceptableOptions) {
            if (c.isInstance(opts)) {
                return true;
            }
        }
        return false;
    }

    byte[] getOptionsAsProperties(Object options) throws IOException {
        return NONE;
    }

    Object getOptionsFromCoder(Coder coder, InputStream in) throws IOException {
        return null;
    }

    OutputStream encode(OutputStream out, Object options) throws IOException {
        throw new UnsupportedOperationException("method doesn't support writing");
    }

    protected static int numberOptionOrDefault(Object options, int defaultValue) {
        return options instanceof Number ? ((Number) options).intValue() : defaultValue;
    }
}
