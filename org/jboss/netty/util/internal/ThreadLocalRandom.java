package org.jboss.netty.util.internal;

import java.util.Random;

public final class ThreadLocalRandom extends Random {
    private static final long addend = 11;
    private static final ThreadLocal<ThreadLocalRandom> localRandom = new C08831();
    private static final long mask = 281474976710655L;
    private static final long multiplier = 25214903917L;
    private static final long serialVersionUID = -5851777807851030925L;
    private boolean initialized;
    private long pad0;
    private long pad1;
    private long pad2;
    private long pad3;
    private long pad4;
    private long pad5;
    private long pad6;
    private long pad7;
    private long rnd;

    static class C08831 extends ThreadLocal<ThreadLocalRandom> {
        C08831() {
        }

        protected ThreadLocalRandom initialValue() {
            return new ThreadLocalRandom();
        }
    }

    public static ThreadLocalRandom current() {
        return (ThreadLocalRandom) localRandom.get();
    }

    public void setSeed(long seed) {
        if (this.initialized) {
            throw new UnsupportedOperationException();
        }
        this.initialized = true;
        this.rnd = (multiplier ^ seed) & mask;
    }

    protected int next(int bits) {
        this.rnd = ((this.rnd * multiplier) + addend) & mask;
        return (int) (this.rnd >>> (48 - bits));
    }
}
