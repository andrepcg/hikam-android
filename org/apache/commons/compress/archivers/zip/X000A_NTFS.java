package org.apache.commons.compress.archivers.zip;

import java.util.Date;
import java.util.zip.ZipException;

public class X000A_NTFS implements ZipExtraField {
    private static final long EPOCH_OFFSET = -116444736000000000L;
    private static final ZipShort HEADER_ID = new ZipShort(10);
    private static final ZipShort TIME_ATTR_SIZE = new ZipShort(24);
    private static final ZipShort TIME_ATTR_TAG = new ZipShort(1);
    private ZipEightByteInteger accessTime = ZipEightByteInteger.ZERO;
    private ZipEightByteInteger createTime = ZipEightByteInteger.ZERO;
    private ZipEightByteInteger modifyTime = ZipEightByteInteger.ZERO;

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public ZipShort getLocalFileDataLength() {
        return new ZipShort(32);
    }

    public ZipShort getCentralDirectoryLength() {
        return getLocalFileDataLength();
    }

    public byte[] getLocalFileDataData() {
        byte[] data = new byte[getLocalFileDataLength().getValue()];
        System.arraycopy(TIME_ATTR_TAG.getBytes(), 0, data, 4, 2);
        int pos = 4 + 2;
        System.arraycopy(TIME_ATTR_SIZE.getBytes(), 0, data, pos, 2);
        pos += 2;
        System.arraycopy(this.modifyTime.getBytes(), 0, data, pos, 8);
        pos += 8;
        System.arraycopy(this.accessTime.getBytes(), 0, data, pos, 8);
        System.arraycopy(this.createTime.getBytes(), 0, data, pos + 8, 8);
        return data;
    }

    public byte[] getCentralDirectoryData() {
        return getLocalFileDataData();
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
        int len = offset + length;
        offset += 4;
        while (offset + 4 <= len) {
            ZipShort tag = new ZipShort(data, offset);
            offset += 2;
            if (tag.equals(TIME_ATTR_TAG)) {
                readTimeAttr(data, offset, len - offset);
                return;
            }
            offset += new ZipShort(data, offset).getValue() + 2;
        }
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        reset();
        parseFromLocalFileData(buffer, offset, length);
    }

    public ZipEightByteInteger getModifyTime() {
        return this.modifyTime;
    }

    public ZipEightByteInteger getAccessTime() {
        return this.accessTime;
    }

    public ZipEightByteInteger getCreateTime() {
        return this.createTime;
    }

    public Date getModifyJavaTime() {
        return zipToDate(this.modifyTime);
    }

    public Date getAccessJavaTime() {
        return zipToDate(this.accessTime);
    }

    public Date getCreateJavaTime() {
        return zipToDate(this.createTime);
    }

    public void setModifyTime(ZipEightByteInteger t) {
        if (t == null) {
            t = ZipEightByteInteger.ZERO;
        }
        this.modifyTime = t;
    }

    public void setAccessTime(ZipEightByteInteger t) {
        if (t == null) {
            t = ZipEightByteInteger.ZERO;
        }
        this.accessTime = t;
    }

    public void setCreateTime(ZipEightByteInteger t) {
        if (t == null) {
            t = ZipEightByteInteger.ZERO;
        }
        this.createTime = t;
    }

    public void setModifyJavaTime(Date d) {
        setModifyTime(dateToZip(d));
    }

    public void setAccessJavaTime(Date d) {
        setAccessTime(dateToZip(d));
    }

    public void setCreateJavaTime(Date d) {
        setCreateTime(dateToZip(d));
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("0x000A Zip Extra Field:").append(" Modify:[").append(getModifyJavaTime()).append("] ").append(" Access:[").append(getAccessJavaTime()).append("] ").append(" Create:[").append(getCreateJavaTime()).append("] ");
        return buf.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof X000A_NTFS)) {
            return false;
        }
        X000A_NTFS xf = (X000A_NTFS) o;
        if (this.modifyTime != xf.modifyTime && (this.modifyTime == null || !this.modifyTime.equals(xf.modifyTime))) {
            return false;
        }
        if (this.accessTime != xf.accessTime && (this.accessTime == null || !this.accessTime.equals(xf.accessTime))) {
            return false;
        }
        if (this.createTime == xf.createTime || (this.createTime != null && this.createTime.equals(xf.createTime))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hc = -123;
        if (this.modifyTime != null) {
            hc = -123 ^ this.modifyTime.hashCode();
        }
        if (this.accessTime != null) {
            hc ^= Integer.rotateLeft(this.accessTime.hashCode(), 11);
        }
        if (this.createTime != null) {
            return hc ^ Integer.rotateLeft(this.createTime.hashCode(), 22);
        }
        return hc;
    }

    private void reset() {
        this.modifyTime = ZipEightByteInteger.ZERO;
        this.accessTime = ZipEightByteInteger.ZERO;
        this.createTime = ZipEightByteInteger.ZERO;
    }

    private void readTimeAttr(byte[] data, int offset, int length) {
        if (length >= 26) {
            if (TIME_ATTR_SIZE.equals(new ZipShort(data, offset))) {
                offset += 2;
                this.modifyTime = new ZipEightByteInteger(data, offset);
                offset += 8;
                this.accessTime = new ZipEightByteInteger(data, offset);
                this.createTime = new ZipEightByteInteger(data, offset + 8);
            }
        }
    }

    private static ZipEightByteInteger dateToZip(Date d) {
        if (d == null) {
            return null;
        }
        return new ZipEightByteInteger((d.getTime() * 10000) - EPOCH_OFFSET);
    }

    private static Date zipToDate(ZipEightByteInteger z) {
        if (z == null || ZipEightByteInteger.ZERO.equals(z)) {
            return null;
        }
        return new Date((z.getLongValue() + EPOCH_OFFSET) / 10000);
    }
}
