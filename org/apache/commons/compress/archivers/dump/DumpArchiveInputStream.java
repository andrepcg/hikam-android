package org.apache.commons.compress.archivers.dump;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants.SEGMENT_TYPE;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

public class DumpArchiveInputStream extends ArchiveInputStream {
    private DumpArchiveEntry active;
    private byte[] blockBuffer;
    final String encoding;
    private long entryOffset;
    private long entrySize;
    private long filepos;
    private boolean hasHitEOF;
    private boolean isClosed;
    private final Map<Integer, Dirent> names;
    private final Map<Integer, DumpArchiveEntry> pending;
    private Queue<DumpArchiveEntry> queue;
    protected TapeInputStream raw;
    private final byte[] readBuf;
    private int readIdx;
    private int recordOffset;
    private DumpArchiveSummary summary;
    private final ZipEncoding zipEncoding;

    class C07861 implements Comparator<DumpArchiveEntry> {
        C07861() {
        }

        public int compare(DumpArchiveEntry p, DumpArchiveEntry q) {
            if (p.getOriginalName() == null || q.getOriginalName() == null) {
                return Integer.MAX_VALUE;
            }
            return p.getOriginalName().compareTo(q.getOriginalName());
        }
    }

    public DumpArchiveInputStream(InputStream is) throws ArchiveException {
        this(is, null);
    }

    public DumpArchiveInputStream(InputStream is, String encoding) throws ArchiveException {
        this.readBuf = new byte[1024];
        this.names = new HashMap();
        this.pending = new HashMap();
        this.raw = new TapeInputStream(is);
        this.hasHitEOF = false;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        try {
            byte[] headerBytes = this.raw.readRecord();
            if (DumpArchiveUtil.verify(headerBytes)) {
                this.summary = new DumpArchiveSummary(headerBytes, this.zipEncoding);
                this.raw.resetBlockSize(this.summary.getNTRec(), this.summary.isCompressed());
                this.blockBuffer = new byte[4096];
                readCLRI();
                readBITS();
                this.names.put(Integer.valueOf(2), new Dirent(2, 2, 4, "."));
                this.queue = new PriorityQueue(10, new C07861());
                return;
            }
            throw new UnrecognizedFormatException();
        } catch (IOException ex) {
            throw new ArchiveException(ex.getMessage(), ex);
        }
    }

    @Deprecated
    public int getCount() {
        return (int) getBytesRead();
    }

    public long getBytesRead() {
        return this.raw.getBytesRead();
    }

    public DumpArchiveSummary getSummary() {
        return this.summary;
    }

    private void readCLRI() throws IOException {
        byte[] buffer = this.raw.readRecord();
        if (DumpArchiveUtil.verify(buffer)) {
            this.active = DumpArchiveEntry.parse(buffer);
            if (SEGMENT_TYPE.CLRI != this.active.getHeaderType()) {
                throw new InvalidFormatException();
            } else if (this.raw.skip(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID * ((long) this.active.getHeaderCount())) == -1) {
                throw new EOFException();
            } else {
                this.readIdx = this.active.getHeaderCount();
                return;
            }
        }
        throw new InvalidFormatException();
    }

    private void readBITS() throws IOException {
        byte[] buffer = this.raw.readRecord();
        if (DumpArchiveUtil.verify(buffer)) {
            this.active = DumpArchiveEntry.parse(buffer);
            if (SEGMENT_TYPE.BITS != this.active.getHeaderType()) {
                throw new InvalidFormatException();
            } else if (this.raw.skip(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID * ((long) this.active.getHeaderCount())) == -1) {
                throw new EOFException();
            } else {
                this.readIdx = this.active.getHeaderCount();
                return;
            }
        }
        throw new InvalidFormatException();
    }

    public DumpArchiveEntry getNextDumpEntry() throws IOException {
        return getNextEntry();
    }

    public DumpArchiveEntry getNextEntry() throws IOException {
        DumpArchiveEntry entry = null;
        String path = null;
        if (!this.queue.isEmpty()) {
            return (DumpArchiveEntry) this.queue.remove();
        }
        while (entry == null) {
            if (this.hasHitEOF) {
                return null;
            }
            while (this.readIdx < this.active.getHeaderCount()) {
                DumpArchiveEntry dumpArchiveEntry = this.active;
                int i = this.readIdx;
                this.readIdx = i + 1;
                if (!dumpArchiveEntry.isSparseRecord(i) && this.raw.skip(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) == -1) {
                    throw new EOFException();
                }
            }
            this.readIdx = 0;
            this.filepos = this.raw.getBytesRead();
            byte[] headerBytes = this.raw.readRecord();
            if (DumpArchiveUtil.verify(headerBytes)) {
                this.active = DumpArchiveEntry.parse(headerBytes);
                while (SEGMENT_TYPE.ADDR == this.active.getHeaderType()) {
                    if (this.raw.skip(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID * ((long) (this.active.getHeaderCount() - this.active.getHeaderHoles()))) == -1) {
                        throw new EOFException();
                    }
                    this.filepos = this.raw.getBytesRead();
                    headerBytes = this.raw.readRecord();
                    if (DumpArchiveUtil.verify(headerBytes)) {
                        this.active = DumpArchiveEntry.parse(headerBytes);
                    } else {
                        throw new InvalidFormatException();
                    }
                }
                if (SEGMENT_TYPE.END == this.active.getHeaderType()) {
                    this.hasHitEOF = true;
                    return null;
                }
                entry = this.active;
                if (entry.isDirectory()) {
                    readDirectoryEntry(this.active);
                    this.entryOffset = 0;
                    this.entrySize = 0;
                    this.readIdx = this.active.getHeaderCount();
                } else {
                    this.entryOffset = 0;
                    this.entrySize = this.active.getEntrySize();
                    this.readIdx = 0;
                }
                this.recordOffset = this.readBuf.length;
                path = getPath(entry);
                if (path == null) {
                    entry = null;
                }
            } else {
                throw new InvalidFormatException();
            }
        }
        entry.setName(path);
        entry.setSimpleName(((Dirent) this.names.get(Integer.valueOf(entry.getIno()))).getName());
        entry.setOffset(this.filepos);
        return entry;
    }

    private void readDirectoryEntry(DumpArchiveEntry entry) throws IOException {
        long size = entry.getEntrySize();
        boolean first = true;
        while (true) {
            if (first || SEGMENT_TYPE.ADDR == entry.getHeaderType()) {
                if (!first) {
                    this.raw.readRecord();
                }
                if (!this.names.containsKey(Integer.valueOf(entry.getIno())) && SEGMENT_TYPE.INODE == entry.getHeaderType()) {
                    this.pending.put(Integer.valueOf(entry.getIno()), entry);
                }
                int datalen = entry.getHeaderCount() * 1024;
                if (this.blockBuffer.length < datalen) {
                    this.blockBuffer = new byte[datalen];
                }
                if (this.raw.read(this.blockBuffer, 0, datalen) != datalen) {
                    throw new EOFException();
                }
                int i = 0;
                while (i < datalen - 8 && ((long) i) < size - 8) {
                    int ino = DumpArchiveUtil.convert32(this.blockBuffer, i);
                    int reclen = DumpArchiveUtil.convert16(this.blockBuffer, i + 4);
                    byte type = this.blockBuffer[i + 6];
                    String name = DumpArchiveUtil.decode(this.zipEncoding, this.blockBuffer, i + 8, this.blockBuffer[i + 7]);
                    if (!(".".equals(name) || "..".equals(name))) {
                        Dirent d = new Dirent(ino, entry.getIno(), type, name);
                        this.names.put(Integer.valueOf(ino), d);
                        for (Entry<Integer, DumpArchiveEntry> e : this.pending.entrySet()) {
                            String path = getPath((DumpArchiveEntry) e.getValue());
                            if (path != null) {
                                ((DumpArchiveEntry) e.getValue()).setName(path);
                                ((DumpArchiveEntry) e.getValue()).setSimpleName(((Dirent) this.names.get(e.getKey())).getName());
                                this.queue.add(e.getValue());
                            }
                        }
                        for (DumpArchiveEntry e2 : this.queue) {
                            this.pending.remove(Integer.valueOf(e2.getIno()));
                        }
                    }
                    i += reclen;
                }
                byte[] peekBytes = this.raw.peek();
                if (DumpArchiveUtil.verify(peekBytes)) {
                    entry = DumpArchiveEntry.parse(peekBytes);
                    first = false;
                    size -= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                } else {
                    throw new InvalidFormatException();
                }
            }
            return;
        }
    }

    private String getPath(DumpArchiveEntry entry) {
        Stack<String> elements = new Stack();
        int i = entry.getIno();
        while (this.names.containsKey(Integer.valueOf(i))) {
            Dirent dirent = (Dirent) this.names.get(Integer.valueOf(i));
            elements.push(dirent.getName());
            if (dirent.getIno() == dirent.getParentIno()) {
                break;
            }
            i = dirent.getParentIno();
        }
        elements.clear();
        if (elements.isEmpty()) {
            this.pending.put(Integer.valueOf(entry.getIno()), entry);
            return null;
        }
        StringBuilder sb = new StringBuilder((String) elements.pop());
        while (!elements.isEmpty()) {
            sb.append('/');
            sb.append((String) elements.pop());
        }
        return sb.toString();
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        int totalRead = 0;
        if (this.hasHitEOF || this.isClosed || this.entryOffset >= this.entrySize) {
            return -1;
        }
        if (this.active == null) {
            throw new IllegalStateException("No current dump entry");
        }
        if (((long) len) + this.entryOffset > this.entrySize) {
            len = (int) (this.entrySize - this.entryOffset);
        }
        while (len > 0) {
            int sz;
            if (len > this.readBuf.length - this.recordOffset) {
                sz = this.readBuf.length - this.recordOffset;
            } else {
                sz = len;
            }
            if (this.recordOffset + sz <= this.readBuf.length) {
                System.arraycopy(this.readBuf, this.recordOffset, buf, off, sz);
                totalRead += sz;
                this.recordOffset += sz;
                len -= sz;
                off += sz;
            }
            if (len > 0) {
                if (this.readIdx >= 512) {
                    byte[] headerBytes = this.raw.readRecord();
                    if (DumpArchiveUtil.verify(headerBytes)) {
                        this.active = DumpArchiveEntry.parse(headerBytes);
                        this.readIdx = 0;
                    } else {
                        throw new InvalidFormatException();
                    }
                }
                DumpArchiveEntry dumpArchiveEntry = this.active;
                int i = this.readIdx;
                this.readIdx = i + 1;
                if (dumpArchiveEntry.isSparseRecord(i)) {
                    Arrays.fill(this.readBuf, (byte) 0);
                } else if (this.raw.read(this.readBuf, 0, this.readBuf.length) != this.readBuf.length) {
                    throw new EOFException();
                }
                this.recordOffset = 0;
            }
        }
        this.entryOffset += (long) totalRead;
        return totalRead;
    }

    public void close() throws IOException {
        if (!this.isClosed) {
            this.isClosed = true;
            this.raw.close();
        }
    }

    public static boolean matches(byte[] buffer, int length) {
        if (length < 32) {
            return false;
        }
        if (length >= 1024) {
            return DumpArchiveUtil.verify(buffer);
        }
        if (DumpArchiveConstants.NFS_MAGIC == DumpArchiveUtil.convert32(buffer, 24)) {
            return true;
        }
        return false;
    }
}
