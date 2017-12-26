package org.jboss.netty.channel.socket.nio;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ConcurrentModificationException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.util.internal.DeadLockProofWorker;

abstract class AbstractNioSelector implements NioSelector {
    static final /* synthetic */ boolean $assertionsDisabled = (!AbstractNioSelector.class.desiredAssertionStatus());
    private static final int CLEANUP_INTERVAL = 256;
    protected static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioSelector.class);
    private static final AtomicInteger nextId = new AtomicInteger();
    private volatile int cancelledKeys;
    private final Executor executor;
    private final int id;
    protected volatile Selector selector;
    private volatile boolean shutdown;
    private final CountDownLatch shutdownLatch;
    final CountDownLatch startupLatch;
    private final Queue<Runnable> taskQueue;
    protected volatile Thread thread;
    protected final AtomicBoolean wakenUp;

    class C08231 implements Runnable {
        C08231() {
        }

        public void run() {
            AbstractNioSelector.this.rebuildSelector();
        }
    }

    protected abstract void close(SelectionKey selectionKey);

    protected abstract Runnable createRegisterTask(Channel channel, ChannelFuture channelFuture);

    protected abstract ThreadRenamingRunnable newThreadRenamingRunnable(int i, ThreadNameDeterminer threadNameDeterminer);

    protected abstract void process(Selector selector) throws IOException;

    AbstractNioSelector(Executor executor) {
        this(executor, null);
    }

    AbstractNioSelector(Executor executor, ThreadNameDeterminer determiner) {
        this.id = nextId.incrementAndGet();
        this.startupLatch = new CountDownLatch(1);
        this.wakenUp = new AtomicBoolean();
        this.taskQueue = new ConcurrentLinkedQueue();
        this.shutdownLatch = new CountDownLatch(1);
        this.executor = executor;
        openSelector(determiner);
    }

    public void register(Channel channel, ChannelFuture future) {
        registerTask(createRegisterTask(channel, future));
    }

    protected final void registerTask(Runnable task) {
        this.taskQueue.add(task);
        Selector selector = this.selector;
        if (selector != null) {
            if (this.wakenUp.compareAndSet(false, true)) {
                selector.wakeup();
            }
        } else if (this.taskQueue.remove(task)) {
            throw new RejectedExecutionException("Worker has already been shutdown");
        }
    }

    protected final boolean isIoThread() {
        return Thread.currentThread() == this.thread;
    }

    public void rebuildSelector() {
        if (isIoThread()) {
            Selector oldSelector = this.selector;
            if (oldSelector != null) {
                try {
                    Selector newSelector = SelectorUtil.open();
                    int nChannels = 0;
                    loop0:
                    while (true) {
                        for (SelectionKey key : oldSelector.keys()) {
                            try {
                                if (key.channel().keyFor(newSelector) == null) {
                                    int interestOps = key.interestOps();
                                    key.cancel();
                                    key.channel().register(newSelector, interestOps, key.attachment());
                                    nChannels++;
                                }
                            } catch (Exception e) {
                                try {
                                    logger.warn("Failed to re-register a Channel to the new Selector,", e);
                                    close(key);
                                } catch (ConcurrentModificationException e2) {
                                }
                            }
                        }
                        break loop0;
                    }
                    this.selector = newSelector;
                    try {
                        oldSelector.close();
                    } catch (Throwable t) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Failed to close the old Selector.", t);
                        }
                    }
                    logger.info("Migrated " + nChannels + " channel(s) to the new Selector,");
                    return;
                } catch (Exception e3) {
                    logger.warn("Failed to create a new Selector.", e3);
                    return;
                }
            }
            return;
        }
        this.taskQueue.add(new C08231());
    }

    public void run() {
        this.thread = Thread.currentThread();
        this.startupLatch.countDown();
        int selectReturnsImmediately = 0;
        Selector selector = this.selector;
        if (selector != null) {
            long minSelectTimeout = (SelectorUtil.SELECT_TIMEOUT_NANOS * 80) / 100;
            boolean wakenupFromLoop = false;
            while (true) {
                this.wakenUp.set(false);
                long beforeSelect = System.nanoTime();
                if (select(selector) != 0 || wakenupFromLoop || this.wakenUp.get()) {
                    selectReturnsImmediately = 0;
                } else if (System.nanoTime() - beforeSelect < minSelectTimeout) {
                    boolean notConnected = false;
                    for (SelectionKey key : selector.keys()) {
                        SelectableChannel ch = key.channel();
                        try {
                            if (((ch instanceof DatagramChannel) && !ch.isOpen()) || !(!(ch instanceof SocketChannel) || ((SocketChannel) ch).isConnected() || ((SocketChannel) ch).isConnectionPending())) {
                                notConnected = true;
                                key.cancel();
                            }
                        } catch (CancelledKeyException e) {
                        }
                    }
                    if (notConnected) {
                        selectReturnsImmediately = 0;
                    } else if (!Thread.interrupted() || this.shutdown) {
                        selectReturnsImmediately++;
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Selector.select() returned prematurely because the I/O thread has been interrupted. Use shutdown() to shut the NioSelector down.");
                        }
                        selectReturnsImmediately = 0;
                    }
                } else {
                    selectReturnsImmediately = 0;
                }
                if (!SelectorUtil.EPOLL_BUG_WORKAROUND) {
                    selectReturnsImmediately = 0;
                } else if (selectReturnsImmediately == 1024) {
                    rebuildSelector();
                    selector = this.selector;
                    selectReturnsImmediately = 0;
                    wakenupFromLoop = false;
                }
                if (this.wakenUp.get()) {
                    wakenupFromLoop = true;
                    selector.wakeup();
                } else {
                    wakenupFromLoop = false;
                }
                this.cancelledKeys = 0;
                processTaskQueue();
                selector = this.selector;
                if (this.shutdown) {
                    this.selector = null;
                    processTaskQueue();
                    for (SelectionKey k : selector.keys()) {
                        close(k);
                    }
                    try {
                        selector.close();
                    } catch (IOException e2) {
                        logger.warn("Failed to close a selector.", e2);
                    } catch (Throwable t) {
                        logger.warn("Unexpected exception in the selector loop.", t);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e3) {
                        }
                    }
                    this.shutdownLatch.countDown();
                    return;
                }
                process(selector);
            }
        }
    }

    private void openSelector(ThreadNameDeterminer determiner) {
        try {
            this.selector = SelectorUtil.open();
            try {
                DeadLockProofWorker.start(this.executor, newThreadRenamingRunnable(this.id, determiner));
                if (!true) {
                    try {
                        this.selector.close();
                    } catch (Throwable t) {
                        logger.warn("Failed to close a selector.", t);
                    }
                    this.selector = null;
                }
                if (!$assertionsDisabled) {
                    if (this.selector == null || !this.selector.isOpen()) {
                        throw new AssertionError();
                    }
                    return;
                }
                return;
            } catch (Throwable t2) {
                logger.warn("Failed to close a selector.", t2);
            }
            this.selector = null;
        } catch (Throwable t22) {
            ChannelException channelException = new ChannelException("Failed to create a selector.", t22);
        }
    }

    private void processTaskQueue() {
        while (true) {
            Runnable task = (Runnable) this.taskQueue.poll();
            if (task != null) {
                task.run();
                try {
                    cleanUpCancelledKeys();
                } catch (IOException e) {
                }
            } else {
                return;
            }
        }
    }

    protected final void increaseCancelledKeys() {
        this.cancelledKeys++;
    }

    protected final boolean cleanUpCancelledKeys() throws IOException {
        if (this.cancelledKeys < 256) {
            return false;
        }
        this.cancelledKeys = 0;
        this.selector.selectNow();
        return true;
    }

    public void shutdown() {
        if (isIoThread()) {
            throw new IllegalStateException("Must not be called from a I/O-Thread to prevent deadlocks!");
        }
        Selector selector = this.selector;
        this.shutdown = true;
        if (selector != null) {
            selector.wakeup();
        }
        try {
            this.shutdownLatch.await();
        } catch (InterruptedException e) {
            logger.error("Interrupted while wait for resources to be released #" + this.id);
            Thread.currentThread().interrupt();
        }
    }

    protected int select(Selector selector) throws IOException {
        return SelectorUtil.select(selector);
    }
}
