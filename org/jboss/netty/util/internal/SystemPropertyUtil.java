package org.jboss.netty.util.internal;

import com.tencent.bugly.Bugly;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public final class SystemPropertyUtil {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("-?[0-9]+");
    private static boolean initializedLogger = true;
    private static boolean loggedException;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SystemPropertyUtil.class);

    public static boolean contains(String key) {
        return get(key) != null;
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String def) {
        if (key == null) {
            throw new NullPointerException("key");
        } else if (key.length() == 0) {
            throw new IllegalArgumentException("key must not be empty.");
        } else {
            String value = null;
            try {
                value = System.getProperty(key);
            } catch (Exception e) {
                if (!loggedException) {
                    log("Unable to retrieve a system property '" + key + "'; default values will be used.", e);
                    loggedException = true;
                }
            }
            if (value == null) {
                return def;
            }
            return value;
        }
    }

    public static boolean getBoolean(String key, boolean def) {
        String value = get(key);
        if (value == null) {
            return def;
        }
        value = value.trim().toLowerCase();
        if (value.length() == 0) {
            return true;
        }
        if ("true".equals(value) || "yes".equals(value) || "1".equals(value)) {
            return true;
        }
        if (Bugly.SDK_IS_DEV.equals(value) || "no".equals(value) || "0".equals(value)) {
            return false;
        }
        log("Unable to parse the boolean system property '" + key + "':" + value + " - " + "using the default value: " + def);
        return def;
    }

    public static int getInt(String key, int def) {
        String value = get(key);
        if (value != null) {
            value = value.trim().toLowerCase();
            if (INTEGER_PATTERN.matcher(value).matches()) {
                try {
                    def = Integer.parseInt(value);
                } catch (Exception e) {
                }
            }
            log("Unable to parse the integer system property '" + key + "':" + value + " - " + "using the default value: " + def);
        }
        return def;
    }

    public static long getLong(String key, long def) {
        String value = get(key);
        if (value != null) {
            value = value.trim().toLowerCase();
            if (INTEGER_PATTERN.matcher(value).matches()) {
                try {
                    def = Long.parseLong(value);
                } catch (Exception e) {
                }
            }
            log("Unable to parse the long integer system property '" + key + "':" + value + " - " + "using the default value: " + def);
        }
        return def;
    }

    private static void log(String msg) {
        if (initializedLogger) {
            logger.warn(msg);
        } else {
            Logger.getLogger(SystemPropertyUtil.class.getName()).log(Level.WARNING, msg);
        }
    }

    private static void log(String msg, Exception e) {
        if (initializedLogger) {
            logger.warn(msg, e);
        } else {
            Logger.getLogger(SystemPropertyUtil.class.getName()).log(Level.WARNING, msg, e);
        }
    }

    private SystemPropertyUtil() {
    }
}
