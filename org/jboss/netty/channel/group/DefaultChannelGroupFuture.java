package org.jboss.netty.channel.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.DeadLockProofWorker;

public class DefaultChannelGroupFuture implements ChannelGroupFuture {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelGroupFuture.class);
    private final ChannelFutureListener childListener = new C12131();
    private boolean done;
    int failureCount;
    private ChannelGroupFutureListener firstListener;
    final Map<Integer, ChannelFuture> futures;
    private final ChannelGroup group;
    private List<ChannelGroupFutureListener> otherListeners;
    int successCount;
    private int waiters;

    class C12131 implements ChannelFutureListener {
        static final /* synthetic */ boolean $assertionsDisabled = (!DefaultChannelGroupFuture.class.desiredAssertionStatus());

        C12131() {
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            boolean success = future.isSuccess();
            synchronized (DefaultChannelGroupFuture.this) {
                DefaultChannelGroupFuture defaultChannelGroupFuture;
                if (success) {
                    defaultChannelGroupFuture = DefaultChannelGroupFuture.this;
                    defaultChannelGroupFuture.successCount++;
                } else {
                    defaultChannelGroupFuture = DefaultChannelGroupFuture.this;
                    defaultChannelGroupFuture.failureCount++;
                }
                boolean callSetDone = DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount == DefaultChannelGroupFuture.this.futures.size();
                if ($assertionsDisabled || DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount <= DefaultChannelGroupFuture.this.futures.size()) {
                } else {
                    throw new AssertionError();
                }
            }
            if (callSetDone) {
                DefaultChannelGroupFuture.this.setDone();
            }
        }
    }

    public DefaultChannelGroupFuture(ChannelGroup group, Collection<ChannelFuture> futures) {
        if (group == null) {
            throw new NullPointerException("group");
        } else if (futures == null) {
            throw new NullPointerException("futures");
        } else {
            this.group = group;
            Map<Integer, ChannelFuture> futureMap = new LinkedHashMap();
            for (ChannelFuture f : futures) {
                futureMap.put(f.getChannel().getId(), f);
            }
            this.futures = Collections.unmodifiableMap(futureMap);
            for (ChannelFuture f2 : this.futures.values()) {
                f2.addListener(this.childListener);
            }
            if (this.futures.isEmpty()) {
                setDone();
            }
        }
    }

    DefaultChannelGroupFuture(ChannelGroup group, Map<Integer, ChannelFuture> futures) {
        this.group = group;
        this.futures = Collections.unmodifiableMap(futures);
        for (ChannelFuture f : this.futures.values()) {
            f.addListener(this.childListener);
        }
        if (this.futures.isEmpty()) {
            setDone();
        }
    }

    public ChannelGroup getGroup() {
        return this.group;
    }

    public ChannelFuture find(Integer channelId) {
        return (ChannelFuture) this.futures.get(channelId);
    }

    public ChannelFuture find(Channel channel) {
        return (ChannelFuture) this.futures.get(channel.getId());
    }

    public Iterator<ChannelFuture> iterator() {
        return this.futures.values().iterator();
    }

    public synchronized boolean isDone() {
        return this.done;
    }

    public synchronized boolean isCompleteSuccess() {
        return this.successCount == this.futures.size();
    }

    public synchronized boolean isPartialSuccess() {
        boolean z;
        z = (this.successCount == 0 || this.successCount == this.futures.size()) ? false : true;
        return z;
    }

    public synchronized boolean isPartialFailure() {
        boolean z;
        z = (this.failureCount == 0 || this.failureCount == this.futures.size()) ? false : true;
        return z;
    }

    public synchronized boolean isCompleteFailure() {
        boolean z;
        int futureCnt = this.futures.size();
        z = futureCnt != 0 && this.failureCount == futureCnt;
        return z;
    }

    public void addListener(ChannelGroupFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        boolean notifyNow = false;
        synchronized (this) {
            if (this.done) {
                notifyNow = true;
            } else if (this.firstListener == null) {
                this.firstListener = listener;
            } else {
                if (this.otherListeners == null) {
                    this.otherListeners = new ArrayList(1);
                }
                this.otherListeners.add(listener);
            }
        }
        if (notifyNow) {
            notifyListener(listener);
        }
    }

    public void removeListener(ChannelGroupFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        synchronized (this) {
            if (!this.done) {
                if (listener == this.firstListener) {
                    if (this.otherListeners == null || this.otherListeners.isEmpty()) {
                        this.firstListener = null;
                    } else {
                        this.firstListener = (ChannelGroupFutureListener) this.otherListeners.remove(0);
                    }
                } else if (this.otherListeners != null) {
                    this.otherListeners.remove(listener);
                }
            }
        }
    }

    public ChannelGroupFuture await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            while (!this.done) {
                checkDeadLock();
                this.waiters++;
                try {
                    wait();
                    this.waiters--;
                } catch (Throwable th) {
                    this.waiters--;
                }
            }
        }
        return this;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return await0(unit.toNanos(timeout), true);
    }

    public boolean await(long timeoutMillis) throws InterruptedException {
        return await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), true);
    }

    public ChannelGroupFuture awaitUninterruptibly() {
        boolean interrupted = false;
        synchronized (this) {
            while (!this.done) {
                checkDeadLock();
                this.waiters++;
                try {
                    wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                } finally {
                    this.waiters--;
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        try {
            return await0(unit.toNanos(timeout), false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        try {
            return await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException();
        }
        long startTime = timeoutNanos <= 0 ? 0 : System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;
        try {
            boolean z;
            Thread currentThread;
            synchronized (this) {
                if (this.done || waitTime <= 0) {
                    z = this.done;
                    if (null != null) {
                        currentThread = Thread.currentThread();
                    }
                    return z;
                }
                checkDeadLock();
                this.waiters++;
                do {
                    try {
                        wait(waitTime / 1000000, (int) (waitTime % 1000000));
                    } catch (InterruptedException e) {
                        if (interruptable) {
                            throw e;
                        } else {
                            interrupted = true;
                        }
                    } catch (Throwable th) {
                        this.waiters--;
                    }
                    if (this.done) {
                        z = true;
                        this.waiters--;
                        if (interrupted) {
                            currentThread = Thread.currentThread();
                        }
                        return z;
                    }
                    waitTime = timeoutNanos - (System.nanoTime() - startTime);
                } while (waitTime > 0);
                z = this.done;
                this.waiters--;
                if (interrupted) {
                    currentThread = Thread.currentThread();
                }
                return z;
            }
            currentThread.interrupt();
            return z;
        } catch (Throwable th2) {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void checkDeadLock() {
        if (DeadLockProofWorker.PARENT.get() != null) {
            throw new IllegalStateException("await*() in I/O thread causes a dead lock or sudden performance drop. Use addListener() instead or call await*() from a different thread.");
        }
    }

    boolean setDone() {
        boolean z = true;
        synchronized (this) {
            if (this.done) {
                z = false;
            } else {
                this.done = true;
                if (this.waiters > 0) {
                    notifyAll();
                }
                notifyListeners();
            }
        }
        return z;
    }

    private void notifyListeners() {
        if (this.firstListener != null) {
            notifyListener(this.firstListener);
            this.firstListener = null;
            if (this.otherListeners != null) {
                for (ChannelGroupFutureListener l : this.otherListeners) {
                    notifyListener(l);
                }
                this.otherListeners = null;
            }
        }
    }

    private void notifyListener(ChannelGroupFutureListener l) {
        try {
            l.operationComplete(this);
        } catch (Throwable t) {
            if (logger.isWarnEnabled()) {
                logger.warn("An exception was thrown by " + ChannelFutureListener.class.getSimpleName() + '.', t);
            }
        }
    }
}
