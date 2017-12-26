package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.archivers.zip.PKWareExtraHeader.EncryptionAlgorithm;
import org.apache.commons.compress.archivers.zip.PKWareExtraHeader.HashAlgorithm;

public class X0017_StrongEncryptionHeader extends PKWareExtraHeader {
    private EncryptionAlgorithm algId;
    private int bitlen;
    private byte[] erdData;
    private int flags;
    private int format;
    private HashAlgorithm hashAlg;
    private int hashSize;
    private byte[] ivData;
    private byte[] keyBlob;
    private long rcount;
    private byte[] recipientKeyHash;
    private byte[] vCRC32;
    private byte[] vData;

    public X0017_StrongEncryptionHeader() {
        super(new ZipShort(23));
    }

    public long getRecordCount() {
        return this.rcount;
    }

    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlg;
    }

    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return this.algId;
    }

    public void parseCentralDirectoryFormat(byte[] data, int offset, int length) {
        this.format = ZipShort.getValue(data, offset);
        this.algId = EncryptionAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 2));
        this.bitlen = ZipShort.getValue(data, offset + 4);
        this.flags = ZipShort.getValue(data, offset + 6);
        this.rcount = ZipLong.getValue(data, offset + 8);
        if (this.rcount > 0) {
            this.hashAlg = HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, offset + 12));
            this.hashSize = ZipShort.getValue(data, offset + 14);
            for (int i = 0; ((long) i) < this.rcount; i++) {
                for (int j = 0; j < this.hashSize; j++) {
                }
            }
        }
    }

    public void parseFileFormat(byte[] data, int offset, int length) {
        int ivSize = ZipShort.getValue(data, offset);
        this.ivData = new byte[ivSize];
        System.arraycopy(data, offset + 4, this.ivData, 0, ivSize);
        this.format = ZipShort.getValue(data, (offset + ivSize) + 6);
        this.algId = EncryptionAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, (offset + ivSize) + 8));
        this.bitlen = ZipShort.getValue(data, (offset + ivSize) + 10);
        this.flags = ZipShort.getValue(data, (offset + ivSize) + 12);
        int erdSize = ZipShort.getValue(data, (offset + ivSize) + 14);
        this.erdData = new byte[erdSize];
        System.arraycopy(data, (offset + ivSize) + 16, this.erdData, 0, erdSize);
        this.rcount = ZipLong.getValue(data, ((offset + ivSize) + 16) + erdSize);
        System.out.println("rcount: " + this.rcount);
        if (this.rcount == 0) {
            int vSize = ZipShort.getValue(data, ((offset + ivSize) + 20) + erdSize);
            this.vData = new byte[(vSize - 4)];
            this.vCRC32 = new byte[4];
            System.arraycopy(data, ((offset + ivSize) + 22) + erdSize, this.vData, 0, vSize - 4);
            System.arraycopy(data, ((((offset + ivSize) + 22) + erdSize) + vSize) - 4, this.vCRC32, 0, 4);
            return;
        }
        this.hashAlg = HashAlgorithm.getAlgorithmByCode(ZipShort.getValue(data, ((offset + ivSize) + 20) + erdSize));
        this.hashSize = ZipShort.getValue(data, ((offset + ivSize) + 22) + erdSize);
        int resize = ZipShort.getValue(data, ((offset + ivSize) + 24) + erdSize);
        this.recipientKeyHash = new byte[this.hashSize];
        this.keyBlob = new byte[(resize - this.hashSize)];
        System.arraycopy(data, ((offset + ivSize) + 24) + erdSize, this.recipientKeyHash, 0, this.hashSize);
        System.arraycopy(data, (((offset + ivSize) + 24) + erdSize) + this.hashSize, this.keyBlob, 0, resize - this.hashSize);
        vSize = ZipShort.getValue(data, (((offset + ivSize) + 26) + erdSize) + resize);
        this.vData = new byte[(vSize - 4)];
        this.vCRC32 = new byte[4];
        System.arraycopy(data, (((offset + ivSize) + 22) + erdSize) + resize, this.vData, 0, vSize - 4);
        System.arraycopy(data, (((((offset + ivSize) + 22) + erdSize) + resize) + vSize) - 4, this.vCRC32, 0, 4);
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) {
        super.parseFromLocalFileData(data, offset, length);
        parseFileFormat(data, offset, length);
    }

    public void parseFromCentralDirectoryData(byte[] data, int offset, int length) {
        super.parseFromCentralDirectoryData(data, offset, length);
        parseCentralDirectoryFormat(data, offset, length);
    }
}
