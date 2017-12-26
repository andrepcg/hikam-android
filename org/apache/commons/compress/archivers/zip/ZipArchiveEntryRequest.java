package org.apache.commons.compress.archivers.zip;

import java.io.InputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;

public class ZipArchiveEntryRequest {
    private final int method;
    private final InputStreamSupplier payloadSupplier;
    private final ZipArchiveEntry zipArchiveEntry;

    private ZipArchiveEntryRequest(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier payloadSupplier) {
        this.zipArchiveEntry = zipArchiveEntry;
        this.payloadSupplier = payloadSupplier;
        this.method = zipArchiveEntry.getMethod();
    }

    public static ZipArchiveEntryRequest createZipArchiveEntryRequest(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier payloadSupplier) {
        return new ZipArchiveEntryRequest(zipArchiveEntry, payloadSupplier);
    }

    public InputStream getPayloadStream() {
        return this.payloadSupplier.get();
    }

    public int getMethod() {
        return this.method;
    }

    ZipArchiveEntry getZipArchiveEntry() {
        return this.zipArchiveEntry;
    }
}
