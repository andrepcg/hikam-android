package org.apache.commons.compress.compressors.xz;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.compressors.FileNameUtil;

public class XZUtils {
    private static final byte[] HEADER_MAGIC = new byte[]{(byte) -3, TarConstants.LF_CONTIG, (byte) 122, TarConstants.LF_PAX_EXTENDED_HEADER_UC, (byte) 90, (byte) 0};
    private static volatile CachedAvailability cachedXZAvailability = CachedAvailability.DONT_CACHE;
    private static final FileNameUtil fileNameUtil;

    enum CachedAvailability {
        DONT_CACHE,
        CACHED_AVAILABLE,
        CACHED_UNAVAILABLE
    }

    static {
        Map<String, String> uncompressSuffix = new HashMap();
        uncompressSuffix.put(".txz", ".tar");
        uncompressSuffix.put(".xz", "");
        uncompressSuffix.put("-xz", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".xz");
        try {
            Class.forName("org.osgi.framework.BundleEvent");
        } catch (Exception e) {
            setCacheXZAvailablity(true);
        }
    }

    private XZUtils() {
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

    public static boolean isXZCompressionAvailable() {
        CachedAvailability cachedResult = cachedXZAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        } else {
            return internalIsXZCompressionAvailable();
        }
    }

    private static boolean internalIsXZCompressionAvailable() {
        try {
            XZCompressorInputStream.matches(null, 0);
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

    public static void setCacheXZAvailablity(boolean doCache) {
        if (!doCache) {
            cachedXZAvailability = CachedAvailability.DONT_CACHE;
        } else if (cachedXZAvailability == CachedAvailability.DONT_CACHE) {
            cachedXZAvailability = internalIsXZCompressionAvailable() ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
        }
    }

    static CachedAvailability getCachedXZAvailability() {
        return cachedXZAvailability;
    }
}
