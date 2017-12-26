package org.apache.commons.compress.archivers.tar;

import java.io.IOException;

public class TarArchiveSparseEntry implements TarConstants {
    private final boolean isExtended;

    public TarArchiveSparseEntry(byte[] headerBuf) throws IOException {
        this.isExtended = TarUtils.parseBoolean(headerBuf, 0 + 504);
    }

    public boolean isExtended() {
        return this.isExtended;
    }
}
