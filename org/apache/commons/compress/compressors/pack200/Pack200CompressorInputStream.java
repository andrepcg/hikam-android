package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.jboss.netty.handler.codec.http.HttpConstants;

public class Pack200CompressorInputStream extends CompressorInputStream {
    private static final byte[] CAFE_DOOD = new byte[]{(byte) -54, (byte) -2, (byte) -48, HttpConstants.CR};
    private static final int SIG_LENGTH = CAFE_DOOD.length;
    private final InputStream originalInput;
    private final StreamBridge streamBridge;

    public Pack200CompressorInputStream(InputStream in) throws IOException {
        this(in, Pack200Strategy.IN_MEMORY);
    }

    public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode) throws IOException {
        this(in, null, mode, null);
    }

    public Pack200CompressorInputStream(InputStream in, Map<String, String> props) throws IOException {
        this(in, Pack200Strategy.IN_MEMORY, (Map) props);
    }

    public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this(in, null, mode, props);
    }

    public Pack200CompressorInputStream(File f) throws IOException {
        this(f, Pack200Strategy.IN_MEMORY);
    }

    public Pack200CompressorInputStream(File f, Pack200Strategy mode) throws IOException {
        this(null, f, mode, null);
    }

    public Pack200CompressorInputStream(File f, Map<String, String> props) throws IOException {
        this(f, Pack200Strategy.IN_MEMORY, (Map) props);
    }

    public Pack200CompressorInputStream(File f, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this(null, f, mode, props);
    }

    private Pack200CompressorInputStream(InputStream in, File f, Pack200Strategy mode, Map<String, String> props) throws IOException {
        Throwable th;
        this.originalInput = in;
        this.streamBridge = mode.newStreamBridge();
        JarOutputStream jarOut = new JarOutputStream(this.streamBridge);
        Throwable th2 = null;
        try {
            Unpacker u = Pack200.newUnpacker();
            if (props != null) {
                u.properties().putAll(props);
            }
            if (f == null) {
                u.unpack(new FilterInputStream(in) {
                    public void close() {
                    }
                }, jarOut);
            } else {
                u.unpack(f, jarOut);
            }
            if (jarOut == null) {
                return;
            }
            if (th2 != null) {
                try {
                    jarOut.close();
                    return;
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                    return;
                }
            }
            jarOut.close();
            return;
        } catch (Throwable th22) {
            Throwable th4 = th22;
            th22 = th3;
            th3 = th4;
        }
        throw th3;
        if (jarOut != null) {
            if (th22 != null) {
                try {
                    jarOut.close();
                } catch (Throwable th5) {
                    th22.addSuppressed(th5);
                }
            } else {
                jarOut.close();
            }
        }
        throw th3;
    }

    public int read() throws IOException {
        return this.streamBridge.getInput().read();
    }

    public int read(byte[] b) throws IOException {
        return this.streamBridge.getInput().read(b);
    }

    public int read(byte[] b, int off, int count) throws IOException {
        return this.streamBridge.getInput().read(b, off, count);
    }

    public int available() throws IOException {
        return this.streamBridge.getInput().available();
    }

    public boolean markSupported() {
        try {
            return this.streamBridge.getInput().markSupported();
        } catch (IOException e) {
            return false;
        }
    }

    public void mark(int limit) {
        try {
            this.streamBridge.getInput().mark(limit);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void reset() throws IOException {
        this.streamBridge.getInput().reset();
    }

    public long skip(long count) throws IOException {
        return this.streamBridge.getInput().skip(count);
    }

    public void close() throws IOException {
        try {
            this.streamBridge.stop();
        } finally {
            if (this.originalInput != null) {
                this.originalInput.close();
            }
        }
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < SIG_LENGTH) {
            return false;
        }
        for (int i = 0; i < SIG_LENGTH; i++) {
            if (signature[i] != CAFE_DOOD[i]) {
                return false;
            }
        }
        return true;
    }
}
