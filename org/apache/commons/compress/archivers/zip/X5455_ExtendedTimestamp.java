package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.util.Date;
import java.util.zip.ZipException;

public class X5455_ExtendedTimestamp implements ZipExtraField, Cloneable, Serializable {
    public static final byte ACCESS_TIME_BIT = (byte) 2;
    public static final byte CREATE_TIME_BIT = (byte) 4;
    private static final ZipShort HEADER_ID = new ZipShort(21589);
    public static final byte MODIFY_TIME_BIT = (byte) 1;
    private static final long serialVersionUID = 1;
    private ZipLong accessTime;
    private boolean bit0_modifyTimePresent;
    private boolean bit1_accessTimePresent;
    private boolean bit2_createTimePresent;
    private ZipLong createTime;
    private byte flags;
    private ZipLong modifyTime;

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public ZipShort getLocalFileDataLength() {
        int i = 4;
        int i2 = (this.bit0_modifyTimePresent ? 4 : 0) + 1;
        int i3 = (!this.bit1_accessTimePresent || this.accessTime == null) ? 0 : 4;
        i3 += i2;
        if (!this.bit2_createTimePresent || this.createTime == null) {
            i = 0;
        }
        return new ZipShort(i3 + i);
    }

    public ZipShort getCentralDirectoryLength() {
        return new ZipShort((this.bit0_modifyTimePresent ? 4 : 0) + 1);
    }

    public byte[] getLocalFileDataData() {
        int pos;
        byte[] data = new byte[getLocalFileDataLength().getValue()];
        int pos2 = 0 + 1;
        data[0] = (byte) 0;
        if (this.bit0_modifyTimePresent) {
            data[0] = (byte) (data[0] | 1);
            System.arraycopy(this.modifyTime.getBytes(), 0, data, pos2, 4);
            pos = pos2 + 4;
        } else {
            pos = pos2;
        }
        if (this.bit1_accessTimePresent && this.accessTime != null) {
            data[0] = (byte) (data[0] | 2);
            System.arraycopy(this.accessTime.getBytes(), 0, data, pos, 4);
            pos += 4;
        }
        if (this.bit2_createTimePresent && this.createTime != null) {
            data[0] = (byte) (data[0] | 4);
            System.arraycopy(this.createTime.getBytes(), 0, data, pos, 4);
            pos += 4;
        }
        return data;
    }

    public byte[] getCentralDirectoryData() {
        byte[] centralData = new byte[getCentralDirectoryLength().getValue()];
        System.arraycopy(getLocalFileDataData(), 0, centralData, 0, centralData.length);
        return centralData;
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
        reset();
        int len = offset + length;
        int offset2 = offset + 1;
        setFlags(data[offset]);
        if (this.bit0_modifyTimePresent) {
            this.modifyTime = new ZipLong(data, offset2);
            offset = offset2 + 4;
        } else {
            offset = offset2;
        }
        if (this.bit1_accessTimePresent && offset + 4 <= len) {
            this.accessTime = new ZipLong(data, offset);
            offset += 4;
        }
        if (this.bit2_createTimePresent && offset + 4 <= len) {
            this.createTime = new ZipLong(data, offset);
            offset += 4;
        }
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        reset();
        parseFromLocalFileData(buffer, offset, length);
    }

    private void reset() {
        setFlags((byte) 0);
        this.modifyTime = null;
        this.accessTime = null;
        this.createTime = null;
    }

    public void setFlags(byte flags) {
        boolean z;
        boolean z2 = true;
        this.flags = flags;
        if ((flags & 1) == 1) {
            z = true;
        } else {
            z = false;
        }
        this.bit0_modifyTimePresent = z;
        if ((flags & 2) == 2) {
            z = true;
        } else {
            z = false;
        }
        this.bit1_accessTimePresent = z;
        if ((flags & 4) != 4) {
            z2 = false;
        }
        this.bit2_createTimePresent = z2;
    }

    public byte getFlags() {
        return this.flags;
    }

    public boolean isBit0_modifyTimePresent() {
        return this.bit0_modifyTimePresent;
    }

    public boolean isBit1_accessTimePresent() {
        return this.bit1_accessTimePresent;
    }

    public boolean isBit2_createTimePresent() {
        return this.bit2_createTimePresent;
    }

    public ZipLong getModifyTime() {
        return this.modifyTime;
    }

    public ZipLong getAccessTime() {
        return this.accessTime;
    }

    public ZipLong getCreateTime() {
        return this.createTime;
    }

    public Date getModifyJavaTime() {
        return this.modifyTime != null ? new Date(this.modifyTime.getValue() * 1000) : null;
    }

    public Date getAccessJavaTime() {
        return this.accessTime != null ? new Date(this.accessTime.getValue() * 1000) : null;
    }

    public Date getCreateJavaTime() {
        return this.createTime != null ? new Date(this.createTime.getValue() * 1000) : null;
    }

    public void setModifyTime(ZipLong l) {
        this.bit0_modifyTimePresent = l != null;
        this.flags = (byte) (l != null ? this.flags | 1 : this.flags & -2);
        this.modifyTime = l;
    }

    public void setAccessTime(ZipLong l) {
        this.bit1_accessTimePresent = l != null;
        this.flags = (byte) (l != null ? this.flags | 2 : this.flags & -3);
        this.accessTime = l;
    }

    public void setCreateTime(ZipLong l) {
        this.bit2_createTimePresent = l != null;
        this.flags = (byte) (l != null ? this.flags | 4 : this.flags & -5);
        this.createTime = l;
    }

    public void setModifyJavaTime(Date d) {
        setModifyTime(dateToZipLong(d));
    }

    public void setAccessJavaTime(Date d) {
        setAccessTime(dateToZipLong(d));
    }

    public void setCreateJavaTime(Date d) {
        setCreateTime(dateToZipLong(d));
    }

    private static ZipLong dateToZipLong(Date d) {
        if (d == null) {
            return null;
        }
        long l = d.getTime() / 1000;
        if (l < 4294967296L) {
            return new ZipLong(l);
        }
        throw new IllegalArgumentException("Cannot set an X5455 timestamp larger than 2^32: " + l);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("0x5455 Zip Extra Field: Flags=");
        buf.append(Integer.toBinaryString(ZipUtil.unsignedIntToSignedByte(this.flags))).append(" ");
        if (this.bit0_modifyTimePresent && this.modifyTime != null) {
            buf.append(" Modify:[").append(getModifyJavaTime()).append("] ");
        }
        if (this.bit1_accessTimePresent && this.accessTime != null) {
            buf.append(" Access:[").append(getAccessJavaTime()).append("] ");
        }
        if (this.bit2_createTimePresent && this.createTime != null) {
            buf.append(" Create:[").append(getCreateJavaTime()).append("] ");
        }
        return buf.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object o) {
        if (!(o instanceof X5455_ExtendedTimestamp)) {
            return false;
        }
        X5455_ExtendedTimestamp xf = (X5455_ExtendedTimestamp) o;
        if ((this.flags & 7) != (xf.flags & 7)) {
            return false;
        }
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
        int hc = (this.flags & 7) * -123;
        if (this.modifyTime != null) {
            hc ^= this.modifyTime.hashCode();
        }
        if (this.accessTime != null) {
            hc ^= Integer.rotateLeft(this.accessTime.hashCode(), 11);
        }
        if (this.createTime != null) {
            return hc ^ Integer.rotateLeft(this.createTime.hashCode(), 22);
        }
        return hc;
    }
}
