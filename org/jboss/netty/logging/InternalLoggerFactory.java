package org.jboss.netty.logging;

public abstract class InternalLoggerFactory {
    private static volatile InternalLoggerFactory defaultFactory = new JdkLoggerFactory();

    public abstract InternalLogger newInstance(String str);

    public static InternalLoggerFactory getDefaultFactory() {
        return defaultFactory;
    }

    public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
        if (defaultFactory == null) {
            throw new NullPointerException("defaultFactory");
        }
        defaultFactory = defaultFactory;
    }

    public static InternalLogger getInstance(Class<?> clazz) {
        return getInstance(clazz.getName());
    }

    public static InternalLogger getInstance(String name) {
        return getDefaultFactory().newInstance(name);
    }
}
