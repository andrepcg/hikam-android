package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.internal.ExecutorUtil;

public abstract class AbstractNioBossPool<E extends Boss> implements BossPool<E>, ExternalResourceReleasable {
    private static final int INITIALIZATION_TIMEOUT = 10;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioBossPool.class);
    private final Executor bossExecutor;
    private final AtomicInteger bossIndex;
    private final Boss[] bosses;
    private final AtomicBoolean initialized;

    protected abstract E newBoss(Executor executor);

    AbstractNioBossPool(Executor bossExecutor, int bossCount) {
        this(bossExecutor, bossCount, true);
    }

    AbstractNioBossPool(Executor bossExecutor, int bossCount, boolean autoInit) {
        this.bossIndex = new AtomicInteger();
        this.initialized = new AtomicBoolean(false);
        if (bossExecutor == null) {
            throw new NullPointerException("bossExecutor");
        } else if (bossCount <= 0) {
            throw new IllegalArgumentException("bossCount (" + bossCount + ") " + "must be a positive integer.");
        } else {
            this.bosses = new Boss[bossCount];
            this.bossExecutor = bossExecutor;
            if (autoInit) {
                init();
            }
        }
    }

    protected void init() {
        if (this.initialized.compareAndSet(false, true)) {
            for (int i = 0; i < this.bosses.length; i++) {
                this.bosses[i] = newBoss(this.bossExecutor);
            }
            waitForBossThreads();
            return;
        }
        throw new IllegalStateException("initialized already");
    }

    private void waitForBossThreads() {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        boolean warn = false;
        for (Boss boss : this.bosses) {
            if (boss instanceof AbstractNioSelector) {
                AbstractNioSelector selector = (AbstractNioSelector) boss;
                long waitTime = deadline - System.nanoTime();
                if (waitTime > 0) {
                    try {
                        if (!selector.startupLatch.await(waitTime, TimeUnit.NANOSECONDS)) {
                            warn = true;
                            break;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else if (selector.thread == null) {
                    warn = true;
                    break;
                }
            }
        }
        if (warn) {
            logger.warn("Failed to get all boss threads ready within 10 second(s). Make sure to specify the executor which has more threads than the requested bossCount. If unsure, use Executors.newCachedThreadPool().");
        }
    }

    public E nextBoss() {
        return this.bosses[Math.abs(this.bossIndex.getAndIncrement() % this.bosses.length)];
    }

    public void rebuildSelectors() {
        for (Boss boss : this.bosses) {
            boss.rebuildSelector();
        }
    }

    public void releaseExternalResources() {
        shutdown();
        ExecutorUtil.shutdownNow(this.bossExecutor);
    }

    public void shutdown() {
        for (Boss boss : this.bosses) {
            boss.shutdown();
        }
    }
}
