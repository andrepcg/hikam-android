package org.apache.commons.compress.archivers.zip;

import android.support.v4.internal.view.SupportMenu;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class PKWareExtraHeader implements ZipExtraField {
    private byte[] centralData;
    private final ZipShort headerId;
    private byte[] localData;

    public enum EncryptionAlgorithm {
        DES(26113),
        RC2pre52(26114),
        TripleDES168(26115),
        TripleDES192(26121),
        AES128(26126),
        AES192(26127),
        AES256(26128),
        RC2(26370),
        RC4(26625),
        UNKNOWN(SupportMenu.USER_MASK);
        
        private static final Map<Integer, EncryptionAlgorithm> codeToEnum = null;
        private final int code;

        static {
            Map<Integer, EncryptionAlgorithm> cte = new HashMap();
            EncryptionAlgorithm[] values = values();
            int length = values.length;
            int i;
            while (i < length) {
                EncryptionAlgorithm method = values[i];
                cte.put(Integer.valueOf(method.getCode()), method);
                i++;
            }
            codeToEnum = Collections.unmodifiableMap(cte);
        }

        private EncryptionAlgorithm(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

        public static EncryptionAlgorithm getAlgorithmByCode(int code) {
            return (EncryptionAlgorithm) codeToEnum.get(Integer.valueOf(code));
        }
    }

    public enum HashAlgorithm {
        NONE(0),
        CRC32(1),
        MD5(32771),
        SHA1(32772),
        RIPEND160(32775),
        SHA256(32780),
        SHA384(32781),
        SHA512(32782);
        
        private static final Map<Integer, HashAlgorithm> codeToEnum = null;
        private final int code;

        static {
            Map<Integer, HashAlgorithm> cte = new HashMap();
            HashAlgorithm[] values = values();
            int length = values.length;
            int i;
            while (i < length) {
                HashAlgorithm method = values[i];
                cte.put(Integer.valueOf(method.getCode()), method);
                i++;
            }
            codeToEnum = Collections.unmodifiableMap(cte);
        }

        private HashAlgorithm(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

        public static HashAlgorithm getAlgorithmByCode(int code) {
            return (HashAlgorithm) codeToEnum.get(Integer.valueOf(code));
        }
    }

    protected PKWareExtraHeader(ZipShort headerId) {
        this.headerId = headerId;
    }

    public ZipShort getHeaderId() {
        return this.headerId;
    }

    public void setLocalFileDataData(byte[] data) {
        this.localData = ZipUtil.copy(data);
    }

    public ZipShort getLocalFileDataLength() {
        return new ZipShort(this.localData != null ? this.localData.length : 0);
    }

    public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localData);
    }

    public void setCentralDirectoryData(byte[] data) {
        this.centralData = ZipUtil.copy(data);
    }

    public ZipShort getCentralDirectoryLength() {
        if (this.centralData != null) {
            return new ZipShort(this.centralData.length);
        }
        return getLocalFileDataLength();
    }

    public byte[] getCentralDirectoryData() {
        if (this.centralData != null) {
            return ZipUtil.copy(this.centralData);
        }
        return getLocalFileDataData();
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) {
        byte[] tmp = new byte[length];
        System.arraycopy(data, offset, tmp, 0, length);
        setLocalFileDataData(tmp);
    }

    public void parseFromCentralDirectoryData(byte[] data, int offset, int length) {
        byte[] tmp = new byte[length];
        System.arraycopy(data, offset, tmp, 0, length);
        setCentralDirectoryData(tmp);
        if (this.localData == null) {
            setLocalFileDataData(tmp);
        }
    }
}
