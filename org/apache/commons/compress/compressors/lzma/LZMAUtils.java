package org.apache.commons.compress.compressors.lzma;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.compress.compressors.FileNameUtil;

public class LZMAUtils {
    private static final byte[] HEADER_MAGIC = new byte[]{(byte) 93, (byte) 0, (byte) 0};
    private static volatile CachedAvailability cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
    private static final FileNameUtil fileNameUtil;

    enum CachedAvailability {
        DONT_CACHE,
        CACHED_AVAILABLE,
        CACHED_UNAVAILABLE
    }

    static {
        Map<String, String> uncompressSuffix = new HashMap();
        uncompressSuffix.put(".lzma", "");
        uncompressSuffix.put("-lzma", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".lzma");
        try {
            Class.forName("org.osgi.framework.BundleEvent");
        } catch (Exception e) {
            setCacheLZMAAvailablity(true);
        }
    }

    private LZMAUtils() {
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < HEADER_MAGIC.length) {
            return false;
        }
        for (int i = 0; i < HEADER_MAGIC.length; i++) {
            if (signature[i] != HEADER_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLZMACompressionAvailable() {
        CachedAvailability cachedResult = cachedLZMAAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        } else {
            return internalIsLZMACompressionAvailable();
        }
    }

    private static boolean internalIsLZMACompressionAvailable() {
        try {
            LZMACompressorInputStream.matches(null, 0);
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public static boolean isCompressedFilename(String filename) {
        return fileNameUtil.isCompressedFilename(filename);
    }

    public static String getUncompressedFilename(String filename) {
        return fileNameUtil.getUncompressedFilename(filename);
    }

    public static String getCompressedFilename(String filename) {
        return fileNameUtil.getCompressedFilename(filename);
    }

    public static void setCacheLZMAAvailablity(boolean doCache) {
        if (!doCache) {
            cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
        } else if (cachedLZMAAvailability == CachedAvailability.DONT_CACHE) {
            cachedLZMAAvailability = internalIsLZMACompressionAvailable() ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
        }
    }

    static CachedAvailability getCachedLZMAAvailability() {
        return cachedLZMAAvailability;
    }
}
