package org.apache.commons.compress.archivers.zip;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;

public class ScatterZipOutputStream implements Closeable {
    private final ScatterGatherBackingStore backingStore;
    private final Queue<CompressedEntry> items = new ConcurrentLinkedQueue();
    private final StreamCompressor streamCompressor;

    private static class CompressedEntry {
        final long compressedSize;
        final long crc;
        final long size;
        final ZipArchiveEntryRequest zipArchiveEntryRequest;

        public CompressedEntry(ZipArchiveEntryRequest zipArchiveEntryRequest, long crc, long compressedSize, long size) {
            this.zipArchiveEntryRequest = zipArchiveEntryRequest;
            this.crc = crc;
            this.compressedSize = compressedSize;
            this.size = size;
        }

        public ZipArchiveEntry transferToArchiveEntry() {
            ZipArchiveEntry entry = this.zipArchiveEntryRequest.getZipArchiveEntry();
            entry.setCompressedSize(this.compressedSize);
            entry.setSize(this.size);
            entry.setCrc(this.crc);
            entry.setMethod(this.zipArchiveEntryRequest.getMethod());
            return entry;
        }
    }

    public ScatterZipOutputStream(ScatterGatherBackingStore backingStore, StreamCompressor streamCompressor) {
        this.backingStore = backingStore;
        this.streamCompressor = streamCompressor;
    }

    public void addArchiveEntry(ZipArchiveEntryRequest zipArchiveEntryRequest) throws IOException {
        Throwable th;
        InputStream payloadStream = zipArchiveEntryRequest.getPayloadStream();
        Throwable th2 = null;
        try {
            this.streamCompressor.deflate(payloadStream, zipArchiveEntryRequest.getMethod());
            if (payloadStream != null) {
                if (th2 != null) {
                    try {
                        payloadStream.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                } else {
                    payloadStream.close();
                }
            }
            this.items.add(new CompressedEntry(zipArchiveEntryRequest, this.streamCompressor.getCrc32(), this.streamCompressor.getBytesWrittenForLastEntry(), this.streamCompressor.getBytesRead()));
            return;
        } catch (Throwable th22) {
            Throwable th4 = th22;
            th22 = th3;
            th3 = th4;
        }
        if (payloadStream != null) {
            if (th22 != null) {
                try {
                    payloadStream.close();
                } catch (Throwable th5) {
                    th22.addSuppressed(th5);
                }
            } else {
                payloadStream.close();
            }
        }
        throw th3;
        throw th3;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeTo(org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream r10) throws java.io.IOException {
        /*
        r9 = this;
        r5 = 0;
        r3 = r9.backingStore;
        r3.closeForWriting();
        r3 = r9.backingStore;
        r1 = r3.getInputStream();
        r3 = r9.items;	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        r3 = r3.iterator();	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
    L_0x0012:
        r4 = r3.hasNext();	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        if (r4 == 0) goto L_0x0064;
    L_0x0018:
        r0 = r3.next();	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        r0 = (org.apache.commons.compress.archivers.zip.ScatterZipOutputStream.CompressedEntry) r0;	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        r2 = new org.apache.commons.compress.utils.BoundedInputStream;	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        r6 = r0.compressedSize;	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        r2.<init>(r1, r6);	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        r4 = 0;
        r6 = r0.transferToArchiveEntry();	 Catch:{ Throwable -> 0x004d, all -> 0x007e }
        r10.addRawArchiveEntry(r6, r2);	 Catch:{ Throwable -> 0x004d, all -> 0x007e }
        if (r2 == 0) goto L_0x0012;
    L_0x002f:
        if (r5 == 0) goto L_0x0047;
    L_0x0031:
        r2.close();	 Catch:{ Throwable -> 0x0035, all -> 0x004b }
        goto L_0x0012;
    L_0x0035:
        r6 = move-exception;
        r4.addSuppressed(r6);	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        goto L_0x0012;
    L_0x003a:
        r3 = move-exception;
        throw r3;	 Catch:{ all -> 0x003c }
    L_0x003c:
        r4 = move-exception;
        r5 = r3;
        r3 = r4;
    L_0x003f:
        if (r1 == 0) goto L_0x0046;
    L_0x0041:
        if (r5 == 0) goto L_0x007a;
    L_0x0043:
        r1.close();	 Catch:{ Throwable -> 0x0075 }
    L_0x0046:
        throw r3;
    L_0x0047:
        r2.close();	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        goto L_0x0012;
    L_0x004b:
        r3 = move-exception;
        goto L_0x003f;
    L_0x004d:
        r3 = move-exception;
        throw r3;	 Catch:{ all -> 0x004f }
    L_0x004f:
        r4 = move-exception;
        r8 = r4;
        r4 = r3;
        r3 = r8;
    L_0x0053:
        if (r2 == 0) goto L_0x005a;
    L_0x0055:
        if (r4 == 0) goto L_0x0060;
    L_0x0057:
        r2.close();	 Catch:{ Throwable -> 0x005b, all -> 0x004b }
    L_0x005a:
        throw r3;	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
    L_0x005b:
        r6 = move-exception;
        r4.addSuppressed(r6);	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        goto L_0x005a;
    L_0x0060:
        r2.close();	 Catch:{ Throwable -> 0x003a, all -> 0x004b }
        goto L_0x005a;
    L_0x0064:
        if (r1 == 0) goto L_0x006b;
    L_0x0066:
        if (r5 == 0) goto L_0x0071;
    L_0x0068:
        r1.close();	 Catch:{ Throwable -> 0x006c }
    L_0x006b:
        return;
    L_0x006c:
        r3 = move-exception;
        r5.addSuppressed(r3);
        goto L_0x006b;
    L_0x0071:
        r1.close();
        goto L_0x006b;
    L_0x0075:
        r4 = move-exception;
        r5.addSuppressed(r4);
        goto L_0x0046;
    L_0x007a:
        r1.close();
        goto L_0x0046;
    L_0x007e:
        r3 = move-exception;
        r4 = r5;
        goto L_0x0053;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ScatterZipOutputStream.writeTo(org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream):void");
    }

    public void close() throws IOException {
        this.backingStore.close();
        this.streamCompressor.close();
    }

    public static ScatterZipOutputStream fileBased(File file) throws FileNotFoundException {
        return fileBased(file, -1);
    }

    public static ScatterZipOutputStream fileBased(File file, int compressionLevel) throws FileNotFoundException {
        ScatterGatherBackingStore bs = new FileBasedScatterGatherBackingStore(file);
        return new ScatterZipOutputStream(bs, StreamCompressor.create(compressionLevel, bs));
    }
}
