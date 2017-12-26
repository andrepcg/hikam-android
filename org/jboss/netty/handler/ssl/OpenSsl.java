package org.jboss.netty.handler.ssl;

import org.apache.tomcat.jni.Library;
import org.apache.tomcat.jni.SSL;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.NativeLibraryLoader;

public final class OpenSsl {
    static final String IGNORABLE_ERROR_PREFIX = "error:00000000:";
    private static final Throwable UNAVAILABILITY_CAUSE;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);

    static {
        Throwable cause = null;
        try {
            NativeLibraryLoader.load("netty-tcnative", SSL.class.getClassLoader());
            Library.initialize("provided");
            SSL.initialize(null);
        } catch (Throwable t) {
            cause = t;
            logger.debug("Failed to load netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.", t);
        }
        UNAVAILABILITY_CAUSE = cause;
    }

    public static boolean isAvailable() {
        return UNAVAILABILITY_CAUSE == null;
    }

    public static void ensureAvailability() {
        if (UNAVAILABILITY_CAUSE != null) {
            throw ((Error) new UnsatisfiedLinkError("failed to load the required native library").initCause(UNAVAILABILITY_CAUSE));
        }
    }

    public static Throwable unavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    private OpenSsl() {
    }
}
