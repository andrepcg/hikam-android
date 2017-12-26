package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.zip.ZipException;

public class X7875_NewUnix implements ZipExtraField, Cloneable, Serializable {
    private static final ZipShort HEADER_ID = new ZipShort(30837);
    private static final BigInteger ONE_THOUSAND = BigInteger.valueOf(1000);
    private static final ZipShort ZERO = new ZipShort(0);
    private static final long serialVersionUID = 1;
    private BigInteger gid;
    private BigInteger uid;
    private int version = 1;

    public X7875_NewUnix() {
        reset();
    }

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public long getUID() {
        return ZipUtil.bigToLong(this.uid);
    }

    public long getGID() {
        return ZipUtil.bigToLong(this.gid);
    }

    public void setUID(long l) {
        this.uid = ZipUtil.longToBig(l);
    }

    public void setGID(long l) {
        this.gid = ZipUtil.longToBig(l);
    }

    public ZipShort getLocalFileDataLength() {
        return new ZipShort((trimLeadingZeroesForceMinLength(this.uid.toByteArray()).length + 3) + trimLeadingZeroesForceMinLength(this.gid.toByteArray()).length);
    }

    public ZipShort getCentralDirectoryLength() {
        return ZERO;
    }

    public byte[] getLocalFileDataData() {
        byte[] uidBytes = this.uid.toByteArray();
        byte[] gidBytes = this.gid.toByteArray();
        uidBytes = trimLeadingZeroesForceMinLength(uidBytes);
        gidBytes = trimLeadingZeroesForceMinLength(gidBytes);
        byte[] data = new byte[((uidBytes.length + 3) + gidBytes.length)];
        ZipUtil.reverse(uidBytes);
        ZipUtil.reverse(gidBytes);
        int i = 0 + 1;
        data[0] = ZipUtil.unsignedIntToSignedByte(this.version);
        int pos = i + 1;
        data[i] = ZipUtil.unsignedIntToSignedByte(uidBytes.length);
        System.arraycopy(uidBytes, 0, data, pos, uidBytes.length);
        pos = uidBytes.length + 2;
        i = pos + 1;
        data[pos] = ZipUtil.unsignedIntToSignedByte(gidBytes.length);
        System.arraycopy(gidBytes, 0, data, i, gidBytes.length);
        return data;
    }

    public byte[] getCentralDirectoryData() {
        return new byte[0];
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
        reset();
        int offset2 = offset + 1;
        this.version = ZipUtil.signedByteToUnsignedInt(data[offset]);
        offset = offset2 + 1;
        int uidSize = ZipUtil.signedByteToUnsignedInt(data[offset2]);
        byte[] uidBytes = new byte[uidSize];
        System.arraycopy(data, offset, uidBytes, 0, uidSize);
        offset += uidSize;
        this.uid = new BigInteger(1, ZipUtil.reverse(uidBytes));
        offset2 = offset + 1;
        int gidSize = ZipUtil.signedByteToUnsignedInt(data[offset]);
        byte[] gidBytes = new byte[gidSize];
        System.arraycopy(data, offset2, gidBytes, 0, gidSize);
        this.gid = new BigInteger(1, ZipUtil.reverse(gidBytes));
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
    }

    private void reset() {
        this.uid = ONE_THOUSAND;
        this.gid = ONE_THOUSAND;
    }

    public String toString() {
        return "0x7875 Zip Extra Field: UID=" + this.uid + " GID=" + this.gid;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object o) {
        if (!(o instanceof X7875_NewUnix)) {
            return false;
        }
        X7875_NewUnix xf = (X7875_NewUnix) o;
        if (this.version == xf.version && this.uid.equals(xf.uid) && this.gid.equals(xf.gid)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((-1234567 * this.version) ^ Integer.rotateLeft(this.uid.hashCode(), 16)) ^ this.gid.hashCode();
    }

    static byte[] trimLeadingZeroesForceMinLength(byte[] array) {
        if (array == null) {
            return array;
        }
        int pos = 0;
        int length = array.length;
        int i = 0;
        while (i < length && array[i] == (byte) 0) {
            pos++;
            i++;
        }
        byte[] trimmedArray = new byte[Math.max(1, array.length - pos)];
        int startPos = trimmedArray.length - (array.length - pos);
        System.arraycopy(array, pos, trimmedArray, startPos, trimmedArray.length - startPos);
        return trimmedArray;
    }
}
