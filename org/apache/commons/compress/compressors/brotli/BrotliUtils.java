package org.apache.commons.compress.compressors.brotli;

public class BrotliUtils {
    private static volatile CachedAvailability cachedBrotliAvailability = CachedAvailability.DONT_CACHE;

    enum CachedAvailability {
        DONT_CACHE,
        CACHED_AVAILABLE,
        CACHED_UNAVAILABLE
    }

    static {
        try {
            Class.forName("org.osgi.framework.BundleEvent");
        } catch (Exception e) {
            setCacheBrotliAvailablity(true);
        }
    }

    private BrotliUtils() {
    }

    public static boolean isBrotliCompressionAvailable() {
        CachedAvailability cachedResult = cachedBrotliAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        } else {
            return internalIsBrotliCompressionAvailable();
        }
    }

    private static boolean internalIsBrotliCompressionAvailable() {
        try {
            Class.forName("org.brotli.dec.BrotliInputStream");
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        } catch (Exception e2) {
            return false;
        }
    }

    public static void setCacheBrotliAvailablity(boolean doCache) {
        if (!doCache) {
            cachedBrotliAvailability = CachedAvailability.DONT_CACHE;
        } else if (cachedBrotliAvailability == CachedAvailability.DONT_CACHE) {
            cachedBrotliAvailability = internalIsBrotliCompressionAvailable() ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
        }
    }

    static CachedAvailability getCachedBrotliAvailability() {
        return cachedBrotliAvailability;
    }
}
