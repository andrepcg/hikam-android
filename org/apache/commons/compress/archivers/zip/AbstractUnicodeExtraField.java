package org.apache.commons.compress.archivers.zip;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;
import java.util.zip.ZipException;

public abstract class AbstractUnicodeExtraField implements ZipExtraField {
    private byte[] data;
    private long nameCRC32;
    private byte[] unicodeName;

    protected AbstractUnicodeExtraField() {
    }

    protected AbstractUnicodeExtraField(String text, byte[] bytes, int off, int len) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes, off, len);
        this.nameCRC32 = crc32.getValue();
        try {
            this.unicodeName = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("FATAL: UTF-8 encoding not supported.", e);
        }
    }

    protected AbstractUnicodeExtraField(String text, byte[] bytes) {
        this(text, bytes, 0, bytes.length);
    }

    private void assembleData() {
        if (this.unicodeName != null) {
            this.data = new byte[(this.unicodeName.length + 5)];
            this.data[0] = (byte) 1;
            System.arraycopy(ZipLong.getBytes(this.nameCRC32), 0, this.data, 1, 4);
            System.arraycopy(this.unicodeName, 0, this.data, 5, this.unicodeName.length);
        }
    }

    public long getNameCRC32() {
        return this.nameCRC32;
    }

    public void setNameCRC32(long nameCRC32) {
        this.nameCRC32 = nameCRC32;
        this.data = null;
    }

    public byte[] getUnicodeName() {
        if (this.unicodeName == null) {
            return null;
        }
        byte[] b = new byte[this.unicodeName.length];
        System.arraycopy(this.unicodeName, 0, b, 0, b.length);
        return b;
    }

    public void setUnicodeName(byte[] unicodeName) {
        if (unicodeName != null) {
            this.unicodeName = new byte[unicodeName.length];
            System.arraycopy(unicodeName, 0, this.unicodeName, 0, unicodeName.length);
        } else {
            this.unicodeName = null;
        }
        this.data = null;
    }

    public byte[] getCentralDirectoryData() {
        if (this.data == null) {
            assembleData();
        }
        if (this.data == null) {
            return null;
        }
        byte[] b = new byte[this.data.length];
        System.arraycopy(this.data, 0, b, 0, b.length);
        return b;
    }

    public ZipShort getCentralDirectoryLength() {
        if (this.data == null) {
            assembleData();
        }
        return new ZipShort(this.data != null ? this.data.length : 0);
    }

    public byte[] getLocalFileDataData() {
        return getCentralDirectoryData();
    }

    public ZipShort getLocalFileDataLength() {
        return getCentralDirectoryLength();
    }

    public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
        if (length < 5) {
            throw new ZipException("UniCode path extra data must have at least 5 bytes.");
        }
        int version = buffer[offset];
        if (version != 1) {
            throw new ZipException("Unsupported version [" + version + "] for UniCode path extra data.");
        }
        this.nameCRC32 = ZipLong.getValue(buffer, offset + 1);
        this.unicodeName = new byte[(length - 5)];
        System.arraycopy(buffer, offset + 5, this.unicodeName, 0, length - 5);
        this.data = null;
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        parseFromLocalFileData(buffer, offset, length);
    }
}
