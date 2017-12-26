package org.jboss.netty.handler.execution;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.util.internal.ConcurrentIdentityWeakKeyHashMap;

public class FairOrderedMemoryAwareThreadPoolExecutor extends MemoryAwareThreadPoolExecutor {
    private final EventTask end = new EventTask(null);
    private final AtomicReferenceFieldUpdater<EventTask, EventTask> fieldUpdater = AtomicReferenceFieldUpdater.newUpdater(EventTask.class, EventTask.class, "next");
    protected final ConcurrentMap<Object, EventTask> map = newMap();

    protected final class EventTask implements Runnable {
        volatile EventTask next;
        private final ChannelEventRunnable runnable;

        EventTask(ChannelEventRunnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            try {
                this.runnable.run();
            } finally {
                if (!FairOrderedMemoryAwareThreadPoolExecutor.this.compareAndSetNext(this, null, FairOrderedMemoryAwareThreadPoolExecutor.this.end)) {
                    FairOrderedMemoryAwareThreadPoolExecutor.this.doUnorderedExecute(this.next);
                }
            }
        }
    }

    public FairOrderedMemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize);
    }

    public FairOrderedMemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit);
    }

    public FairOrderedMemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, threadFactory);
    }

    public FairOrderedMemoryAwareThreadPoolExecutor(int corePoolSize, long maxChannelMemorySize, long maxTotalMemorySize, long keepAliveTime, TimeUnit unit, ObjectSizeEstimator objectSizeEstimator, ThreadFactory threadFactory) {
        super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, unit, objectSizeEstimator, threadFactory);
    }

    protected ConcurrentMap<Object, EventTask> newMap() {
        return new ConcurrentIdentityWeakKeyHashMap();
    }

    protected void doExecute(Runnable task) {
        if (task instanceof ChannelEventRunnable) {
            ChannelEventRunnable eventRunnable = (ChannelEventRunnable) task;
            EventTask newEventTask = new EventTask(eventRunnable);
            Object key = getKey(eventRunnable.getEvent());
            EventTask previousEventTask = (EventTask) this.map.put(key, newEventTask);
            removeIfClosed(eventRunnable, key);
            if (previousEventTask == null || !compareAndSetNext(previousEventTask, null, newEventTask)) {
                doUnorderedExecute(newEventTask);
                return;
            }
            return;
        }
        doUnorderedExecute(task);
    }

    private void removeIfClosed(ChannelEventRunnable eventRunnable, Object key) {
        ChannelEvent event = eventRunnable.getEvent();
        if ((event instanceof ChannelStateEvent) && ((ChannelStateEvent) event).getState() == ChannelState.OPEN && !event.getChannel().isOpen()) {
            removeKey(key);
        }
    }

    protected boolean removeKey(Object key) {
        return this.map.remove(key) != null;
    }

    protected Object getKey(ChannelEvent e) {
        return e.getChannel();
    }

    protected boolean shouldCount(Runnable task) {
        return !(task instanceof EventTask) && super.shouldCount(task);
    }

    protected final boolean compareAndSetNext(EventTask eventTask, EventTask expect, EventTask update) {
        return this.fieldUpdater.compareAndSet(eventTask, expect, update);
    }
}
