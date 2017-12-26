package org.apache.commons.compress.archivers.cpio;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.File;
import java.util.Date;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class CpioArchiveEntry implements CpioConstants, ArchiveEntry {
    private final int alignmentBoundary;
    private long chksum;
    private final short fileFormat;
    private long filesize;
    private long gid;
    private final int headerSize;
    private long inode;
    private long maj;
    private long min;
    private long mode;
    private long mtime;
    private String name;
    private long nlink;
    private long rmaj;
    private long rmin;
    private long uid;

    public CpioArchiveEntry(short format) {
        this.chksum = 0;
        this.filesize = 0;
        this.gid = 0;
        this.inode = 0;
        this.maj = 0;
        this.min = 0;
        this.mode = 0;
        this.mtime = 0;
        this.nlink = 0;
        this.rmaj = 0;
        this.rmin = 0;
        this.uid = 0;
        switch (format) {
            case (short) 1:
                this.headerSize = 110;
                this.alignmentBoundary = 4;
                break;
            case (short) 2:
                this.headerSize = 110;
                this.alignmentBoundary = 4;
                break;
            case (short) 4:
                this.headerSize = 76;
                this.alignmentBoundary = 0;
                break;
            case (short) 8:
                this.headerSize = 26;
                this.alignmentBoundary = 2;
                break;
            default:
                throw new IllegalArgumentException("Unknown header type");
        }
        this.fileFormat = format;
    }

    public CpioArchiveEntry(String name) {
        this((short) 1, name);
    }

    public CpioArchiveEntry(short format, String name) {
        this(format);
        this.name = name;
    }

    public CpioArchiveEntry(String name, long size) {
        this(name);
        setSize(size);
    }

    public CpioArchiveEntry(short format, String name, long size) {
        this(format, name);
        setSize(size);
    }

    public CpioArchiveEntry(File inputFile, String entryName) {
        this((short) 1, inputFile, entryName);
    }

    public CpioArchiveEntry(short format, File inputFile, String entryName) {
        this(format, entryName, inputFile.isFile() ? inputFile.length() : 0);
        if (inputFile.isDirectory()) {
            setMode(16384);
        } else if (inputFile.isFile()) {
            setMode(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID);
        } else {
            throw new IllegalArgumentException("Cannot determine type of file " + inputFile.getName());
        }
        setTime(inputFile.lastModified() / 1000);
    }

    private void checkNewFormat() {
        if ((this.fileFormat & 3) == 0) {
            throw new UnsupportedOperationException();
        }
    }

    private void checkOldFormat() {
        if ((this.fileFormat & 12) == 0) {
            throw new UnsupportedOperationException();
        }
    }

    public long getChksum() {
        checkNewFormat();
        return this.chksum & 4294967295L;
    }

    public long getDevice() {
        checkOldFormat();
        return this.min;
    }

    public long getDeviceMaj() {
        checkNewFormat();
        return this.maj;
    }

    public long getDeviceMin() {
        checkNewFormat();
        return this.min;
    }

    public long getSize() {
        return this.filesize;
    }

    public short getFormat() {
        return this.fileFormat;
    }

    public long getGID() {
        return this.gid;
    }

    public int getHeaderSize() {
        return this.headerSize;
    }

    public int getAlignmentBoundary() {
        return this.alignmentBoundary;
    }

    public int getHeaderPadCount() {
        if (this.alignmentBoundary == 0) {
            return 0;
        }
        int size = this.headerSize + 1;
        if (this.name != null) {
            size += this.name.length();
        }
        int remain = size % this.alignmentBoundary;
        if (remain > 0) {
            return this.alignmentBoundary - remain;
        }
        return 0;
    }

    public int getDataPadCount() {
        if (this.alignmentBoundary == 0) {
            return 0;
        }
        int remain = (int) (this.filesize % ((long) this.alignmentBoundary));
        if (remain > 0) {
            return this.alignmentBoundary - remain;
        }
        return 0;
    }

    public long getInode() {
        return this.inode;
    }

    public long getMode() {
        return (this.mode != 0 || CpioConstants.CPIO_TRAILER.equals(this.name)) ? this.mode : PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID;
    }

    public String getName() {
        return this.name;
    }

    public long getNumberOfLinks() {
        if (this.nlink == 0) {
            return isDirectory() ? 2 : 1;
        } else {
            return this.nlink;
        }
    }

    public long getRemoteDevice() {
        checkOldFormat();
        return this.rmin;
    }

    public long getRemoteDeviceMaj() {
        checkNewFormat();
        return this.rmaj;
    }

    public long getRemoteDeviceMin() {
        checkNewFormat();
        return this.rmin;
    }

    public long getTime() {
        return this.mtime;
    }

    public Date getLastModifiedDate() {
        return new Date(1000 * getTime());
    }

    public long getUID() {
        return this.uid;
    }

    public boolean isBlockDevice() {
        return CpioUtil.fileType(this.mode) == 24576;
    }

    public boolean isCharacterDevice() {
        return CpioUtil.fileType(this.mode) == PlaybackStateCompat.ACTION_PLAY_FROM_URI;
    }

    public boolean isDirectory() {
        return CpioUtil.fileType(this.mode) == 16384;
    }

    public boolean isNetwork() {
        return CpioUtil.fileType(this.mode) == 36864;
    }

    public boolean isPipe() {
        return CpioUtil.fileType(this.mode) == PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
    }

    public boolean isRegularFile() {
        return CpioUtil.fileType(this.mode) == PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID;
    }

    public boolean isSocket() {
        return CpioUtil.fileType(this.mode) == 49152;
    }

    public boolean isSymbolicLink() {
        return CpioUtil.fileType(this.mode) == 40960;
    }

    public void setChksum(long chksum) {
        checkNewFormat();
        this.chksum = 4294967295L & chksum;
    }

    public void setDevice(long device) {
        checkOldFormat();
        this.min = device;
    }

    public void setDeviceMaj(long maj) {
        checkNewFormat();
        this.maj = maj;
    }

    public void setDeviceMin(long min) {
        checkNewFormat();
        this.min = min;
    }

    public void setSize(long size) {
        if (size < 0 || size > 4294967295L) {
            throw new IllegalArgumentException("invalid entry size <" + size + ">");
        }
        this.filesize = size;
    }

    public void setGID(long gid) {
        this.gid = gid;
    }

    public void setInode(long inode) {
        this.inode = inode;
    }

    public void setMode(long mode) {
        long maskedMode = mode & 61440;
        switch ((int) maskedMode) {
            case 4096:
            case 8192:
            case 16384:
            case CpioConstants.C_ISBLK /*24576*/:
            case 32768:
            case CpioConstants.C_ISNWK /*36864*/:
            case 40960:
            case CpioConstants.C_ISSOCK /*49152*/:
                this.mode = mode;
                return;
            default:
                throw new IllegalArgumentException("Unknown mode. Full: " + Long.toHexString(mode) + " Masked: " + Long.toHexString(maskedMode));
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfLinks(long nlink) {
        this.nlink = nlink;
    }

    public void setRemoteDevice(long device) {
        checkOldFormat();
        this.rmin = device;
    }

    public void setRemoteDeviceMaj(long rmaj) {
        checkNewFormat();
        this.rmaj = rmaj;
    }

    public void setRemoteDeviceMin(long rmin) {
        checkNewFormat();
        this.rmin = rmin;
    }

    public void setTime(long time) {
        this.mtime = time;
    }

    public void setUID(long uid) {
        this.uid = uid;
    }

    public int hashCode() {
        return (this.name == null ? 0 : this.name.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CpioArchiveEntry other = (CpioArchiveEntry) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
            return true;
        } else if (this.name.equals(other.name)) {
            return true;
        } else {
            return false;
        }
    }
}
