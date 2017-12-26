package org.apache.commons.compress.compressors.pack200;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class StreamBridge extends FilterOutputStream {
    private InputStream input;
    private final Object inputLock;

    abstract InputStream getInputView() throws IOException;

    protected StreamBridge(OutputStream out) {
        super(out);
        this.inputLock = new Object();
    }

    protected StreamBridge() {
        this(null);
    }

    InputStream getInput() throws IOException {
        synchronized (this.inputLock) {
            if (this.input == null) {
                this.input = getInputView();
            }
        }
        return this.input;
    }

    void stop() throws IOException {
        close();
        synchronized (this.inputLock) {
            if (this.input != null) {
                this.input.close();
                this.input = null;
            }
        }
    }
}
