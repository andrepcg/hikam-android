package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.internal.ExecutorUtil;

public abstract class AbstractNioWorkerPool<E extends AbstractNioWorker> implements WorkerPool<E>, ExternalResourceReleasable {
    private static final int INITIALIZATION_TIMEOUT = 10;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioWorkerPool.class);
    private final AtomicBoolean initialized;
    private final Executor workerExecutor;
    private final AtomicInteger workerIndex;
    private final AbstractNioWorker[] workers;

    protected abstract E newWorker(Executor executor);

    AbstractNioWorkerPool(Executor workerExecutor, int workerCount) {
        this(workerExecutor, workerCount, true);
    }

    AbstractNioWorkerPool(Executor workerExecutor, int workerCount, boolean autoInit) {
        this.workerIndex = new AtomicInteger();
        this.initialized = new AtomicBoolean(false);
        if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        } else if (workerCount <= 0) {
            throw new IllegalArgumentException("workerCount (" + workerCount + ") " + "must be a positive integer.");
        } else {
            this.workers = new AbstractNioWorker[workerCount];
            this.workerExecutor = workerExecutor;
            if (autoInit) {
                init();
            }
        }
    }

    protected void init() {
        if (this.initialized.compareAndSet(false, true)) {
            for (int i = 0; i < this.workers.length; i++) {
                this.workers[i] = newWorker(this.workerExecutor);
            }
            waitForWorkerThreads();
            return;
        }
        throw new IllegalStateException("initialized already");
    }

    private void waitForWorkerThreads() {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        boolean warn = false;
        for (AbstractNioSelector worker : this.workers) {
            long waitTime = deadline - System.nanoTime();
            if (waitTime > 0) {
                try {
                    if (!worker.startupLatch.await(waitTime, TimeUnit.NANOSECONDS)) {
                        warn = true;
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else if (worker.thread == null) {
                warn = true;
                break;
            }
        }
        if (warn) {
            logger.warn("Failed to get all worker threads ready within 10 second(s). Make sure to specify the executor which has more threads than the requested workerCount. If unsure, use Executors.newCachedThreadPool().");
        }
    }

    public E nextWorker() {
        return this.workers[Math.abs(this.workerIndex.getAndIncrement() % this.workers.length)];
    }

    public void rebuildSelectors() {
        for (AbstractNioWorker worker : this.workers) {
            worker.rebuildSelector();
        }
    }

    public void releaseExternalResources() {
        shutdown();
        ExecutorUtil.shutdownNow(this.workerExecutor);
    }

    public void shutdown() {
        for (AbstractNioWorker worker : this.workers) {
            worker.shutdown();
        }
    }
}
