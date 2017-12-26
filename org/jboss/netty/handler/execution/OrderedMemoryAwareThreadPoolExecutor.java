package org.jboss.netty.handler.execution;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.util.internal.ConcurrentIdentityWeakKeyHashMap;

public class OrderedMemoryAwareThreadPoolExecutor extends MemoryAwareThreadPoolExecutor {
    protected final ConcurrentMap<Object, Executor> childExecutors = newChildExecutorMap();

    protected final class ChildExecutor implements Executor, Runnable {
        private final AtomicBoolean isRunning = new AtomicBoolean();
        private final Queue<Runnable> tasks = new ConcurrentLinkedQueue();

        protected ChildExecutor() {
        }

        public void execute(Runnable command) {
            this.tasks.add(command);
            if (!this.isRunning.get()) {
                OrderedMemoryAwareThreadPoolExecutor.this.doUnorderedExecute(this);
            }
        }

        public void run() {
            boolean ran;
            if (this.isRunning.compareAndSet(false, true)) {
                Runnable task;
                try {
                    Thread thread = Thread.currentThread();
                    while (true) {
                        task = (Runnable) this.tasks.poll();
                        if (task == null) {
                            break;
                        }
                        ran = false;
                        OrderedMemoryAwareThreadPoolExecutor.this.beforeExecute(thread, task);
                        task.run();
                        ran = true;
                        OrderedMemoryAwareThreadPoolExecutor.this.onAfterExecute(task, null);
                    }
                    this.isRunning.set(false);
                    if (true && !this.isRunning.get() && this.tasks.peek() != null) {
                        OrderedMemoryAwareThreadPoolExecutor.this.doUnorderedExecute(this);
                    }
                } catch (RuntimeException e) {
                    if (!ran) {
                        OrderedMemoryAwareThreadPoolExecutor.this.onAfterExecute(task, e);
                    }
                    throw e;
                } catch (Throwable th) {
                    this.isRunning.set(false);
                }
            }
        }
    }

    public OrderedMemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize);
    }

    public OrderedMemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit);
    }

    public OrderedMemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, threadFactory);
    }

    public OrderedMemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit, ObjectSizeEstimator objectSizeEstimator, ThreadFactory threadFactory) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, objectSizeEstimator, threadFactory);
    }

    protected ConcurrentMap<Object, Executor> newChildExecutorMap() {
        return new ConcurrentIdentityWeakKeyHashMap();
    }

    protected Object getChildExecutorKey(ChannelEvent e) {
        return e.getChannel();
    }

    protected Set<Object> getChildExecutorKeySet() {
        return this.childExecutors.keySet();
    }

    protected boolean removeChildExecutor(Object key) {
        return this.childExecutors.remove(key) != null;
    }

    protected void doExecute(Runnable task) {
        if (task instanceof ChannelEventRunnable) {
            getChildExecutor(((ChannelEventRunnable) task).getEvent()).execute(task);
        } else {
            doUnorderedExecute(task);
        }
    }

    protected Executor getChildExecutor(ChannelEvent e) {
        Object key = getChildExecutorKey(e);
        Executor executor = (Executor) this.childExecutors.get(key);
        if (executor == null) {
            executor = new ChildExecutor();
            Executor oldExecutor = (Executor) this.childExecutors.putIfAbsent(key, executor);
            if (oldExecutor != null) {
                executor = oldExecutor;
            }
        }
        if (e instanceof ChannelStateEvent) {
            Channel channel = e.getChannel();
            if (((ChannelStateEvent) e).getState() == ChannelState.OPEN && !channel.isOpen()) {
                removeChildExecutor(key);
            }
        }
        return executor;
    }

    protected boolean shouldCount(Runnable task) {
        if (task instanceof ChildExecutor) {
            return false;
        }
        return super.shouldCount(task);
    }

    void onAfterExecute(Runnable r, Throwable t) {
        afterExecute(r, t);
    }
}
