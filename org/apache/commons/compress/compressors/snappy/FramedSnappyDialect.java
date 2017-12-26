package org.apache.commons.compress.compressors.snappy;

public enum FramedSnappyDialect {
    STANDARD(true, true),
    IWORK_ARCHIVE(false, false);
    
    private final boolean checksumWithCompressedChunks;
    private final boolean streamIdentifier;

    private FramedSnappyDialect(boolean hasStreamIdentifier, boolean usesChecksumWithCompressedChunks) {
        this.streamIdentifier = hasStreamIdentifier;
        this.checksumWithCompressedChunks = usesChecksumWithCompressedChunks;
    }

    boolean hasStreamIdentifier() {
        return this.streamIdentifier;
    }

    boolean usesChecksumWithCompressedChunks() {
        return this.checksumWithCompressedChunks;
    }
}
