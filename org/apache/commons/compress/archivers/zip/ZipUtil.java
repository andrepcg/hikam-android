package org.apache.commons.compress.archivers.zip;

import android.support.v4.view.InputDeviceCompat;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException.Feature;

public abstract class ZipUtil {
    private static final byte[] DOS_TIME_MIN = ZipLong.getBytes(8448);

    public static ZipLong toDosTime(Date time) {
        return new ZipLong(toDosTime(time.getTime()));
    }

    public static byte[] toDosTime(long t) {
        byte[] result = new byte[4];
        toDosTime(t, result, 0);
        return result;
    }

    public static void toDosTime(long t, byte[] buf, int offset) {
        toDosTime(Calendar.getInstance(), t, buf, offset);
    }

    static void toDosTime(Calendar c, long t, byte[] buf, int offset) {
        c.setTimeInMillis(t);
        int year = c.get(1);
        if (year < 1980) {
            System.arraycopy(DOS_TIME_MIN, 0, buf, offset, DOS_TIME_MIN.length);
            return;
        }
        ZipLong.putLong((long) (((((((year - 1980) << 25) | ((c.get(2) + 1) << 21)) | (c.get(5) << 16)) | (c.get(11) << 11)) | (c.get(12) << 5)) | (c.get(13) >> 1)), buf, offset);
    }

    public static long adjustToLong(int i) {
        if (i < 0) {
            return 4294967296L + ((long) i);
        }
        return (long) i;
    }

    public static byte[] reverse(byte[] array) {
        int z = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            byte x = array[i];
            array[i] = array[z - i];
            array[z - i] = x;
        }
        return array;
    }

    static long bigToLong(BigInteger big) {
        if (big.bitLength() <= 63) {
            return big.longValue();
        }
        throw new NumberFormatException("The BigInteger cannot fit inside a 64 bit java long: [" + big + "]");
    }

    static BigInteger longToBig(long l) {
        if (l < -2147483648L) {
            throw new IllegalArgumentException("Negative longs < -2^31 not permitted: [" + l + "]");
        }
        if (l < 0 && l >= -2147483648L) {
            l = adjustToLong((int) l);
        }
        return BigInteger.valueOf(l);
    }

    public static int signedByteToUnsignedInt(byte b) {
        return b >= (byte) 0 ? b : b + 256;
    }

    public static byte unsignedIntToSignedByte(int i) {
        if (i > 255 || i < 0) {
            throw new IllegalArgumentException("Can only convert non-negative integers between [0,255] to byte: [" + i + "]");
        } else if (i < 128) {
            return (byte) i;
        } else {
            return (byte) (i + InputDeviceCompat.SOURCE_ANY);
        }
    }

    public static Date fromDosTime(ZipLong zipDosTime) {
        return new Date(dosToJavaTime(zipDosTime.getValue()));
    }

    public static long dosToJavaTime(long dosTime) {
        Calendar cal = Calendar.getInstance();
        cal.set(1, ((int) ((dosTime >> 25) & 127)) + 1980);
        cal.set(2, ((int) ((dosTime >> 21) & 15)) - 1);
        cal.set(5, ((int) (dosTime >> 16)) & 31);
        cal.set(11, ((int) (dosTime >> 11)) & 31);
        cal.set(12, ((int) (dosTime >> 5)) & 63);
        cal.set(13, ((int) (dosTime << 1)) & 62);
        cal.set(14, 0);
        return cal.getTime().getTime();
    }

    static void setNameAndCommentFromExtraFields(ZipArchiveEntry ze, byte[] originalNameBytes, byte[] commentBytes) {
        UnicodePathExtraField name = (UnicodePathExtraField) ze.getExtraField(UnicodePathExtraField.UPATH_ID);
        String originalName = ze.getName();
        String newName = getUnicodeStringIfOriginalMatches(name, originalNameBytes);
        if (!(newName == null || originalName.equals(newName))) {
            ze.setName(newName);
        }
        if (commentBytes != null && commentBytes.length > 0) {
            String newComment = getUnicodeStringIfOriginalMatches((UnicodeCommentExtraField) ze.getExtraField(UnicodeCommentExtraField.UCOM_ID), commentBytes);
            if (newComment != null) {
                ze.setComment(newComment);
            }
        }
    }

    private static String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField f, byte[] orig) {
        String str = null;
        if (f != null) {
            CRC32 crc32 = new CRC32();
            crc32.update(orig);
            if (crc32.getValue() == f.getNameCRC32()) {
                try {
                    str = ZipEncodingHelper.UTF8_ZIP_ENCODING.decode(f.getUnicodeName());
                } catch (IOException e) {
                }
            }
        }
        return str;
    }

    static byte[] copy(byte[] from) {
        if (from == null) {
            return null;
        }
        byte[] to = new byte[from.length];
        System.arraycopy(from, 0, to, 0, to.length);
        return to;
    }

    static void copy(byte[] from, byte[] to, int offset) {
        if (from != null) {
            System.arraycopy(from, 0, to, offset, from.length);
        }
    }

    static boolean canHandleEntryData(ZipArchiveEntry entry) {
        return supportsEncryptionOf(entry) && supportsMethodOf(entry);
    }

    private static boolean supportsEncryptionOf(ZipArchiveEntry entry) {
        return !entry.getGeneralPurposeBit().usesEncryption();
    }

    private static boolean supportsMethodOf(ZipArchiveEntry entry) {
        return entry.getMethod() == 0 || entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || entry.getMethod() == ZipMethod.IMPLODING.getCode() || entry.getMethod() == 8 || entry.getMethod() == ZipMethod.BZIP2.getCode();
    }

    static void checkRequestedFeatures(ZipArchiveEntry ze) throws UnsupportedZipFeatureException {
        if (!supportsEncryptionOf(ze)) {
            throw new UnsupportedZipFeatureException(Feature.ENCRYPTION, ze);
        } else if (!supportsMethodOf(ze)) {
            ZipMethod m = ZipMethod.getMethodByCode(ze.getMethod());
            if (m == null) {
                throw new UnsupportedZipFeatureException(Feature.METHOD, ze);
            }
            throw new UnsupportedZipFeatureException(m, ze);
        }
    }
}
