package org.apache.commons.compress.archivers.zip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipException;

public class ExtraFieldUtils {
    private static final int WORD = 4;
    private static final Map<ZipShort, Class<?>> implementations = new ConcurrentHashMap();

    public static final class UnparseableExtraField {
        public static final UnparseableExtraField READ = new UnparseableExtraField(2);
        public static final int READ_KEY = 2;
        public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
        public static final int SKIP_KEY = 1;
        public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
        public static final int THROW_KEY = 0;
        private final int key;

        private UnparseableExtraField(int k) {
            this.key = k;
        }

        public int getKey() {
            return this.key;
        }
    }

    static {
        register(AsiExtraField.class);
        register(X5455_ExtendedTimestamp.class);
        register(X7875_NewUnix.class);
        register(JarMarker.class);
        register(UnicodePathExtraField.class);
        register(UnicodeCommentExtraField.class);
        register(Zip64ExtendedInformationExtraField.class);
        register(X000A_NTFS.class);
        register(X0014_X509Certificates.class);
        register(X0015_CertificateIdForFile.class);
        register(X0016_CertificateIdForCentralDirectory.class);
        register(X0017_StrongEncryptionHeader.class);
        register(X0019_EncryptionRecipientCertificateList.class);
        register(ResourceAlignmentExtraField.class);
    }

    public static void register(Class<?> c) {
        try {
            implementations.put(((ZipExtraField) c.newInstance()).getHeaderId(), c);
        } catch (ClassCastException e) {
            throw new RuntimeException(c + " doesn't implement ZipExtraField");
        } catch (InstantiationException e2) {
            throw new RuntimeException(c + " is not a concrete class");
        } catch (IllegalAccessException e3) {
            throw new RuntimeException(c + "'s no-arg constructor is not public");
        }
    }

    public static ZipExtraField createExtraField(ZipShort headerId) throws InstantiationException, IllegalAccessException {
        Class<?> c = (Class) implementations.get(headerId);
        if (c != null) {
            return (ZipExtraField) c.newInstance();
        }
        UnrecognizedExtraField u = new UnrecognizedExtraField();
        u.setHeaderId(headerId);
        return u;
    }

    public static ZipExtraField[] parse(byte[] data) throws ZipException {
        return parse(data, true, UnparseableExtraField.THROW);
    }

    public static ZipExtraField[] parse(byte[] data, boolean local) throws ZipException {
        return parse(data, local, UnparseableExtraField.THROW);
    }

    public static ZipExtraField[] parse(byte[] data, boolean local, UnparseableExtraField onUnparseableData) throws ZipException {
        ReflectiveOperationException ie;
        List<ZipExtraField> v = new ArrayList();
        int start = 0;
        while (start <= data.length - 4) {
            ZipShort headerId = new ZipShort(data, start);
            int length = new ZipShort(data, start + 2).getValue();
            if ((start + 4) + length > data.length) {
                switch (onUnparseableData.getKey()) {
                    case 0:
                        throw new ZipException("bad extra field starting at " + start + ".  Block length of " + length + " bytes exceeds remaining data of " + ((data.length - start) - 4) + " bytes.");
                    case 1:
                        break;
                    case 2:
                        UnparseableExtraFieldData field = new UnparseableExtraFieldData();
                        if (local) {
                            field.parseFromLocalFileData(data, start, data.length - start);
                        } else {
                            field.parseFromCentralDirectoryData(data, start, data.length - start);
                        }
                        v.add(field);
                        break;
                    default:
                        throw new ZipException("unknown UnparseableExtraField key: " + onUnparseableData.getKey());
                }
                return (ZipExtraField[]) v.toArray(new ZipExtraField[v.size()]);
            }
            try {
                ZipExtraField ze = createExtraField(headerId);
                if (local) {
                    ze.parseFromLocalFileData(data, start + 4, length);
                } else {
                    ze.parseFromCentralDirectoryData(data, start + 4, length);
                }
                v.add(ze);
                start += length + 4;
            } catch (InstantiationException e) {
                ie = e;
            } catch (IllegalAccessException e2) {
                ie = e2;
            }
        }
        return (ZipExtraField[]) v.toArray(new ZipExtraField[v.size()]);
        throw ((ZipException) new ZipException(ie.getMessage()).initCause(ie));
    }

    public static byte[] mergeLocalFileDataData(ZipExtraField[] data) {
        boolean lastIsUnparseableHolder;
        if (data.length <= 0 || !(data[data.length - 1] instanceof UnparseableExtraFieldData)) {
            lastIsUnparseableHolder = false;
        } else {
            lastIsUnparseableHolder = true;
        }
        int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
        int sum = regularExtraFieldCount * 4;
        for (ZipExtraField element : data) {
            sum += element.getLocalFileDataLength().getValue();
        }
        byte[] result = new byte[sum];
        int start = 0;
        for (int i = 0; i < regularExtraFieldCount; i++) {
            System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i].getLocalFileDataLength().getBytes(), 0, result, start + 2, 2);
            start += 4;
            byte[] local = data[i].getLocalFileDataData();
            if (local != null) {
                System.arraycopy(local, 0, result, start, local.length);
                start += local.length;
            }
        }
        if (lastIsUnparseableHolder) {
            local = data[data.length - 1].getLocalFileDataData();
            if (local != null) {
                System.arraycopy(local, 0, result, start, local.length);
            }
        }
        return result;
    }

    public static byte[] mergeCentralDirectoryData(ZipExtraField[] data) {
        boolean lastIsUnparseableHolder;
        if (data.length <= 0 || !(data[data.length - 1] instanceof UnparseableExtraFieldData)) {
            lastIsUnparseableHolder = false;
        } else {
            lastIsUnparseableHolder = true;
        }
        int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
        int sum = regularExtraFieldCount * 4;
        for (ZipExtraField element : data) {
            sum += element.getCentralDirectoryLength().getValue();
        }
        byte[] result = new byte[sum];
        int start = 0;
        for (int i = 0; i < regularExtraFieldCount; i++) {
            System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i].getCentralDirectoryLength().getBytes(), 0, result, start + 2, 2);
            start += 4;
            byte[] local = data[i].getCentralDirectoryData();
            if (local != null) {
                System.arraycopy(local, 0, result, start, local.length);
                start += local.length;
            }
        }
        if (lastIsUnparseableHolder) {
            local = data[data.length - 1].getCentralDirectoryData();
            if (local != null) {
                System.arraycopy(local, 0, result, start, local.length);
            }
        }
        return result;
    }
}
