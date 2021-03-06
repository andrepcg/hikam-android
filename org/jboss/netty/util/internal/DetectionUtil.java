package org.jboss.netty.util.internal;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class DetectionUtil {
    private static final boolean HAS_UNSAFE = hasUnsafe(AtomicInteger.class.getClassLoader());
    private static final boolean IS_WINDOWS = SystemPropertyUtil.get("os.name", "").toLowerCase().contains("win");
    private static final int JAVA_VERSION = javaVersion0();

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean hasUnsafe() {
        return HAS_UNSAFE;
    }

    public static int javaVersion() {
        return JAVA_VERSION;
    }

    private static boolean hasUnsafe(ClassLoader loader) {
        boolean z = false;
        if (!SystemPropertyUtil.getBoolean("io.netty.noUnsafe", z)) {
            boolean tryUnsafe;
            if (SystemPropertyUtil.contains("io.netty.tryUnsafe")) {
                tryUnsafe = SystemPropertyUtil.getBoolean("io.netty.tryUnsafe", true);
            } else {
                tryUnsafe = SystemPropertyUtil.getBoolean("org.jboss.netty.tryUnsafe", true);
            }
            if (tryUnsafe) {
                try {
                    z = hasUnsafeField(Class.forName("sun.misc.Unsafe", true, loader));
                } catch (Exception e) {
                }
            }
        }
        return z;
    }

    private static boolean hasUnsafeField(final Class<?> unsafeClass) throws PrivilegedActionException {
        return ((Boolean) AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
            public Boolean run() throws Exception {
                unsafeClass.getDeclaredField("theUnsafe");
                return Boolean.valueOf(true);
            }
        })).booleanValue();
    }

    private static int javaVersion0() {
        try {
            Class.forName("android.app.Application");
            return 6;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("java.util.concurrent.LinkedTransferQueue", false, BlockingQueue.class.getClassLoader());
                return 7;
            } catch (Exception e2) {
                try {
                    Class.forName("java.util.ArrayDeque", false, Queue.class.getClassLoader());
                    return 6;
                } catch (Exception e3) {
                    return 5;
                }
            }
        }
    }

    private DetectionUtil() {
    }
}
