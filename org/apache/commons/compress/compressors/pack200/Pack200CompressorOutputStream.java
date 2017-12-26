package org.apache.commons.compress.compressors.pack200;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class Pack200CompressorOutputStream extends CompressorOutputStream {
    private boolean finished;
    private final OutputStream originalOutput;
    private final Map<String, String> properties;
    private final StreamBridge streamBridge;

    public Pack200CompressorOutputStream(OutputStream out) throws IOException {
        this(out, Pack200Strategy.IN_MEMORY);
    }

    public Pack200CompressorOutputStream(OutputStream out, Pack200Strategy mode) throws IOException {
        this(out, mode, null);
    }

    public Pack200CompressorOutputStream(OutputStream out, Map<String, String> props) throws IOException {
        this(out, Pack200Strategy.IN_MEMORY, props);
    }

    public Pack200CompressorOutputStream(OutputStream out, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this.finished = false;
        this.originalOutput = out;
        this.streamBridge = mode.newStreamBridge();
        this.properties = props;
    }

    public void write(int b) throws IOException {
        this.streamBridge.write(b);
    }

    public void write(byte[] b) throws IOException {
        this.streamBridge.write(b);
    }

    public void write(byte[] b, int from, int length) throws IOException {
        this.streamBridge.write(b, from, length);
    }

    public void close() throws IOException {
        finish();
        try {
            this.streamBridge.stop();
        } finally {
            this.originalOutput.close();
        }
    }

    public void finish() throws IOException {
        JarInputStream ji;
        Throwable th;
        Throwable th2;
        if (!this.finished) {
            this.finished = true;
            Packer p = Pack200.newPacker();
            if (this.properties != null) {
                p.properties().putAll(this.properties);
            }
            ji = new JarInputStream(this.streamBridge.getInput());
            th = null;
            try {
                p.pack(ji, this.originalOutput);
                if (ji == null) {
                    return;
                }
                if (th != null) {
                    try {
                        ji.close();
                        return;
                    } catch (Throwable th22) {
                        th.addSuppressed(th22);
                        return;
                    }
                }
                ji.close();
                return;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                th3 = th22;
                th22 = th4;
            }
        } else {
            return;
        }
        if (ji != null) {
            if (th3 != null) {
                try {
                    ji.close();
                } catch (Throwable th5) {
                    th3.addSuppressed(th5);
                }
            } else {
                ji.close();
            }
        }
        throw th22;
        throw th22;
    }
}
