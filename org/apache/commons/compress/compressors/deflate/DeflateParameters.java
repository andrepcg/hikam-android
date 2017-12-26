package org.apache.commons.compress.compressors.deflate;

public class DeflateParameters {
    private int compressionLevel = -1;
    private boolean zlibHeader = true;

    public boolean withZlibHeader() {
        return this.zlibHeader;
    }

    public void setWithZlibHeader(boolean zlibHeader) {
        this.zlibHeader = zlibHeader;
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        if (compressionLevel < -1 || compressionLevel > 9) {
            throw new IllegalArgumentException("Invalid Deflate compression level: " + compressionLevel);
        }
        this.compressionLevel = compressionLevel;
    }
}
