package org.jboss.netty.handler.codec.serialization;

import java.io.FilterInputStream;
import java.io.InputStream;

final class SwitchableInputStream extends FilterInputStream {
    SwitchableInputStream() {
        super(null);
    }

    void switchStream(InputStream in) {
        this.in = in;
    }
}
