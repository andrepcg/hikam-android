package org.jboss.netty.util.internal;

public class ThreadLocalBoolean extends ThreadLocal<Boolean> {
    private final boolean defaultValue;

    public ThreadLocalBoolean() {
        this(false);
    }

    public ThreadLocalBoolean(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected Boolean initialValue() {
        return this.defaultValue ? Boolean.TRUE : Boolean.FALSE;
    }
}
