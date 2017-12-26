package org.apache.commons.compress.archivers.zip;

public final class GeneralPurposeBit implements Cloneable {
    private static final int DATA_DESCRIPTOR_FLAG = 8;
    private static final int ENCRYPTION_FLAG = 1;
    private static final int NUMBER_OF_SHANNON_FANO_TREES_FLAG = 4;
    private static final int SLIDING_DICTIONARY_SIZE_FLAG = 2;
    private static final int STRONG_ENCRYPTION_FLAG = 64;
    public static final int UFT8_NAMES_FLAG = 2048;
    private boolean dataDescriptorFlag = false;
    private boolean encryptionFlag = false;
    private boolean languageEncodingFlag = false;
    private int numberOfShannonFanoTrees;
    private int slidingDictionarySize;
    private boolean strongEncryptionFlag = false;

    public boolean usesUTF8ForNames() {
        return this.languageEncodingFlag;
    }

    public void useUTF8ForNames(boolean b) {
        this.languageEncodingFlag = b;
    }

    public boolean usesDataDescriptor() {
        return this.dataDescriptorFlag;
    }

    public void useDataDescriptor(boolean b) {
        this.dataDescriptorFlag = b;
    }

    public boolean usesEncryption() {
        return this.encryptionFlag;
    }

    public void useEncryption(boolean b) {
        this.encryptionFlag = b;
    }

    public boolean usesStrongEncryption() {
        return this.encryptionFlag && this.strongEncryptionFlag;
    }

    public void useStrongEncryption(boolean b) {
        this.strongEncryptionFlag = b;
        if (b) {
            useEncryption(true);
        }
    }

    int getSlidingDictionarySize() {
        return this.slidingDictionarySize;
    }

    int getNumberOfShannonFanoTrees() {
        return this.numberOfShannonFanoTrees;
    }

    public byte[] encode() {
        byte[] result = new byte[2];
        encode(result, 0);
        return result;
    }

    public void encode(byte[] buf, int offset) {
        int i = 0;
        int i2 = (this.encryptionFlag ? 1 : 0) | ((this.languageEncodingFlag ? 2048 : 0) | (this.dataDescriptorFlag ? 8 : 0));
        if (this.strongEncryptionFlag) {
            i = 64;
        }
        ZipShort.putShort(i2 | i, buf, offset);
    }

    public static GeneralPurposeBit parse(byte[] data, int offset) {
        boolean z;
        boolean z2 = true;
        int generalPurposeFlag = ZipShort.getValue(data, offset);
        GeneralPurposeBit b = new GeneralPurposeBit();
        b.useDataDescriptor((generalPurposeFlag & 8) != 0);
        if ((generalPurposeFlag & 2048) != 0) {
            z = true;
        } else {
            z = false;
        }
        b.useUTF8ForNames(z);
        if ((generalPurposeFlag & 64) != 0) {
            z = true;
        } else {
            z = false;
        }
        b.useStrongEncryption(z);
        if ((generalPurposeFlag & 1) == 0) {
            z2 = false;
        }
        b.useEncryption(z2);
        b.slidingDictionarySize = (generalPurposeFlag & 2) != 0 ? 8192 : 4096;
        b.numberOfShannonFanoTrees = (generalPurposeFlag & 4) != 0 ? 3 : 2;
        return b;
    }

    public int hashCode() {
        int i = 1;
        int i2 = ((this.languageEncodingFlag ? 1 : 0) + (((this.strongEncryptionFlag ? 1 : 0) + ((this.encryptionFlag ? 1 : 0) * 17)) * 13)) * 7;
        if (!this.dataDescriptorFlag) {
            i = 0;
        }
        return (i2 + i) * 3;
    }

    public boolean equals(Object o) {
        if (!(o instanceof GeneralPurposeBit)) {
            return false;
        }
        GeneralPurposeBit g = (GeneralPurposeBit) o;
        if (g.encryptionFlag == this.encryptionFlag && g.strongEncryptionFlag == this.strongEncryptionFlag && g.languageEncodingFlag == this.languageEncodingFlag && g.dataDescriptorFlag == this.dataDescriptorFlag) {
            return true;
        }
        return false;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("GeneralPurposeBit is not Cloneable?", ex);
        }
    }
}
