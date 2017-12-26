package org.jboss.netty.handler.execution;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.DefaultObjectSizeEstimator;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.util.internal.ConcurrentIdentityHashMap;
import org.jboss.netty.util.internal.SharedResourceMisuseDetector;

public class MemoryAwareThreadPoolExecutor extends ThreadPoolExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MemoryAwareThreadPoolExecutor.class);
    private static final SharedResourceMisuseDetector misuseDetector = new SharedResourceMisuseDetector(MemoryAwareThreadPoolExecutor.class);
    private final ConcurrentMap<Channel, AtomicLong> channelCounters;
    private volatile boolean notifyOnShutdown;
    private volatile Settings settings;
    private final Limiter totalLimiter;

    private static class Limiter {
        private long counter;
        final long limit;
        private int waiters;

        Limiter(long limit) {
            this.limit = limit;
        }

        synchronized void increase(long amount) {
            while (this.counter >= this.limit) {
                this.waiters++;
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    this.waiters--;
                }
            }
            this.counter += amount;
        }

        synchronized void decrease(long amount) {
            this.counter -= amount;
            if (this.counter < this.limit && this.waiters > 0) {
                notifyAll();
            }
        }
    }

    private static final class MemoryAwareRunnable implements Runnable {
        int estimatedSize;
        final Runnable task;

        MemoryAwareRunnable(Runnable task) {
            this.task = task;
        }

        public void run() {
            this.task.run();
        }
    }

    private static final class NewThreadRunsPolicy implements RejectedExecutionHandler {
        private NewThreadRunsPolicy() {
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                new Thread(r, "Temporary task executor").start();
            } catch (Throwable e) {
                RejectedExecutionException rejectedExecutionException = new RejectedExecutionException("Failed to start a new thread", e);
            }
        }
    }

    private static final class Settings {
        final long maxChannelMemorySize;
        final ObjectSizeEstimator objectSizeEstimator;

        Settings(ObjectSizeEstimator objectSizeEstimator, long maxChannelMemorySize) {
            this.objectSizeEstimator = objectSizeEstimator;
            this.maxChannelMemorySize = maxChannelMemorySize;
        }
    }

    public MemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize) {
        this(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, 30, TimeUnit.SECONDS);
    }

    public MemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit) {
        this(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, Executors.defaultThreadFactory());
    }

    public MemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        this(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, new DefaultObjectSizeEstimator(), threadFactory);
    }

    public MemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit, ObjectSizeEstimator objectSizeEstimator, ThreadFactory threadFactory) {
        super(corePoolSize, corePoolSize, keepAliveTime, unit, new LinkedBlockingQueue(), threadFactory, new NewThreadRunsPolicy());
        this.channelCounters = new ConcurrentIdentityHashMap();
        if (objectSizeEstimator == null) {
            throw new NullPointerException("objectSizeEstimator");
        } else if (maxChannelMemorySize < 0) {
            throw new IllegalArgumentException("maxChannelMemorySize: " + maxChannelMemorySize);
        } else if (maxTotalMemorySize < 0) {
            throw new IllegalArgumentException("maxTotalMemorySize: " + maxTotalMemorySize);
        } else {
            try {
                getClass().getMethod("allowCoreThreadTimeOut", new Class[]{Boolean.TYPE}).invoke(this, new Object[]{Boolean.TRUE});
            } catch (Throwable th) {
                logger.debug("ThreadPoolExecutor.allowCoreThreadTimeOut() is not supported in this platform.");
            }
            this.settings = new Settings(objectSizeEstimator, maxChannelMemorySize);
            if (maxTotalMemorySize == 0) {
                this.totalLimiter = null;
            } else {
                this.totalLimiter = new Limiter(maxTotalMemorySize);
            }
            misuseDetector.increase();
        }
    }

    protected void terminated() {
        super.terminated();
        misuseDetector.decrease();
    }

    public List<Runnable> shutdownNow() {
        return shutdownNow(this.notifyOnShutdown);
    }

    public List<Runnable> shutdownNow(boolean notify) {
        if (!notify) {
            return super.shutdownNow();
        }
        Throwable cause = null;
        Set<Channel> channels = null;
        List<Runnable> tasks = super.shutdownNow();
        for (Runnable task : tasks) {
            if (task instanceof ChannelEventRunnable) {
                if (cause == null) {
                    cause = new IOException("Unable to process queued event");
                }
                ChannelEvent event = ((ChannelEventRunnable) task).getEvent();
                event.getFuture().setFailure(cause);
                if (channels == null) {
                    channels = new HashSet();
                }
                channels.add(event.getChannel());
            }
        }
        if (channels == null) {
            return tasks;
        }
        for (Channel channel : channels) {
            Channels.fireExceptionCaughtLater(channel, cause);
        }
        return tasks;
    }

    public ObjectSizeEstimator getObjectSizeEstimator() {
        return this.settings.objectSizeEstimator;
    }

    public void setObjectSizeEstimator(ObjectSizeEstimator objectSizeEstimator) {
        if (objectSizeEstimator == null) {
            throw new NullPointerException("objectSizeEstimator");
        }
        this.settings = new Settings(objectSizeEstimator, this.settings.maxChannelMemorySize);
    }

    public long getMaxChannelMemorySize() {
        return this.settings.maxChannelMemorySize;
    }

    public void setMaxChannelMemorySize(long maxChannelMemorySize) {
        if (maxChannelMemorySize < 0) {
            throw new IllegalArgumentException("maxChannelMemorySize: " + maxChannelMemorySize);
        } else if (getTaskCount() > 0) {
            throw new IllegalStateException("can't be changed after a task is executed");
        } else {
            this.settings = new Settings(this.settings.objectSizeEstimator, maxChannelMemorySize);
        }
    }

    public long getMaxTotalMemorySize() {
        if (this.totalLimiter == null) {
            return 0;
        }
        return this.totalLimiter.limit;
    }

    public void setNotifyChannelFuturesOnShutdown(boolean notifyOnShutdown) {
        this.notifyOnShutdown = notifyOnShutdown;
    }

    public boolean getNotifyChannelFuturesOnShutdown() {
        return this.notifyOnShutdown;
    }

    public void execute(Runnable command) {
        if (command instanceof ChannelDownstreamEventRunnable) {
            throw new RejectedExecutionException("command must be enclosed with an upstream event.");
        }
        if (!(command instanceof ChannelEventRunnable)) {
            command = new MemoryAwareRunnable(command);
        }
        increaseCounter(command);
        doExecute(command);
    }

    protected void doExecute(Runnable task) {
        doUnorderedExecute(task);
    }

    protected final void doUnorderedExecute(Runnable task) {
        super.execute(task);
    }

    public boolean remove(Runnable task) {
        boolean removed = super.remove(task);
        if (removed) {
            decreaseCounter(task);
        }
        return removed;
    }

    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        decreaseCounter(r);
    }

    protected void increaseCounter(Runnable task) {
        if (shouldCount(task)) {
            Settings settings = this.settings;
            long maxChannelMemorySize = settings.maxChannelMemorySize;
            int increment = settings.objectSizeEstimator.estimateSize(task);
            if (task instanceof ChannelEventRunnable) {
                ChannelEventRunnable eventTask = (ChannelEventRunnable) task;
                eventTask.estimatedSize = increment;
                Channel channel = eventTask.getEvent().getChannel();
                long channelCounter = getChannelCounter(channel).addAndGet((long) increment);
                if (maxChannelMemorySize != 0 && channelCounter >= maxChannelMemorySize && channel.isOpen() && channel.isReadable()) {
                    ChannelHandlerContext ctx = eventTask.getContext();
                    if (ctx.getHandler() instanceof ExecutionHandler) {
                        ctx.setAttachment(Boolean.TRUE);
                    }
                    channel.setReadable(false);
                }
            } else {
                ((MemoryAwareRunnable) task).estimatedSize = increment;
            }
            if (this.totalLimiter != null) {
                this.totalLimiter.increase((long) increment);
            }
        }
    }

    protected void decreaseCounter(Runnable task) {
        if (shouldCount(task)) {
            int increment;
            long maxChannelMemorySize = this.settings.maxChannelMemorySize;
            if (task instanceof ChannelEventRunnable) {
                increment = ((ChannelEventRunnable) task).estimatedSize;
            } else {
                increment = ((MemoryAwareRunnable) task).estimatedSize;
            }
            if (this.totalLimiter != null) {
                this.totalLimiter.decrease((long) increment);
            }
            if (task instanceof ChannelEventRunnable) {
                ChannelEventRunnable eventTask = (ChannelEventRunnable) task;
                Channel channel = eventTask.getEvent().getChannel();
                long channelCounter = getChannelCounter(channel).addAndGet((long) (-increment));
                if (maxChannelMemorySize != 0 && channelCounter < maxChannelMemorySize && channel.isOpen() && !channel.isReadable()) {
                    ChannelHandlerContext ctx = eventTask.getContext();
                    if (!(ctx.getHandler() instanceof ExecutionHandler)) {
                        channel.setReadable(true);
                    } else if (ctx.getAttachment() != null) {
                        ctx.setAttachment(null);
                        channel.setReadable(true);
                    }
                }
            }
        }
    }

    private AtomicLong getChannelCounter(Channel channel) {
        AtomicLong counter = (AtomicLong) this.channelCounters.get(channel);
        if (counter == null) {
            counter = new AtomicLong();
            AtomicLong oldCounter = (AtomicLong) this.channelCounters.putIfAbsent(channel, counter);
            if (oldCounter != null) {
                counter = oldCounter;
            }
        }
        if (!channel.isOpen()) {
            this.channelCounters.remove(channel);
        }
        return counter;
    }

    protected boolean shouldCount(Runnable task) {
        if (task instanceof ChannelUpstreamEventRunnable) {
            ChannelEvent e = ((ChannelUpstreamEventRunnable) task).getEvent();
            if (e instanceof WriteCompletionEvent) {
                return false;
            }
            if ((e instanceof ChannelStateEvent) && ((ChannelStateEvent) e).getState() == ChannelState.INTEREST_OPS) {
                return false;
            }
        }
        return true;
    }
}
