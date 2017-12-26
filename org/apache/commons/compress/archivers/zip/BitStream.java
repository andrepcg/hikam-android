package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.apache.commons.compress.utils.BitInputStream;

class BitStream extends BitInputStream {
    BitStream(InputStream in) {
        super(in, ByteOrder.LITTLE_ENDIAN);
    }

    int nextBit() throws IOException {
        return (int) readBits(1);
    }

    long nextBits(int n) throws IOException {
        return readBits(n);
    }

    int nextByte() throws IOException {
        return (int) readBits(8);
    }
}
