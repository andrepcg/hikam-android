package org.jboss.netty.handler.execution;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.util.ObjectSizeEstimator;

public final class FairOrderedDownstreamThreadPoolExecutor extends FairOrderedMemoryAwareThreadPoolExecutor {
    public FairOrderedDownstreamThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize, 0, 0);
    }

    public FairOrderedDownstreamThreadPoolExecutor(int corePoolSize, long keepAliveTime, TimeUnit unit) {
        super(corePoolSize, 0, 0, keepAliveTime, unit);
    }

    public FairOrderedDownstreamThreadPoolExecutor(int corePoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        super(corePoolSize, 0, 0, keepAliveTime, unit, threadFactory);
    }

    public ObjectSizeEstimator getObjectSizeEstimator() {
        return null;
    }

    public void setObjectSizeEstimator(ObjectSizeEstimator objectSizeEstimator) {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }

    public long getMaxChannelMemorySize() {
        return 0;
    }

    public void setMaxChannelMemorySize(long maxChannelMemorySize) {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }

    public long getMaxTotalMemorySize() {
        return 0;
    }

    protected boolean shouldCount(Runnable task) {
        return false;
    }

    public void execute(Runnable command) {
        if (command instanceof ChannelUpstreamEventRunnable) {
            throw new RejectedExecutionException("command must be enclosed with an downstream event.");
        }
        doExecute(command);
    }

    protected void doExecute(Runnable task) {
        if (task instanceof ChannelEventRunnable) {
            ChannelEventRunnable eventRunnable = (ChannelEventRunnable) task;
            ChannelEvent event = eventRunnable.getEvent();
            EventTask newEventTask = new EventTask(eventRunnable);
            final Object key = getKey(event);
            EventTask previousEventTask = (EventTask) this.map.put(key, newEventTask);
            if (previousEventTask == null) {
                event.getChannel().getCloseFuture().addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        FairOrderedDownstreamThreadPoolExecutor.this.removeKey(key);
                    }
                });
            } else if (compareAndSetNext(previousEventTask, null, newEventTask)) {
                return;
            }
            doUnorderedExecute(newEventTask);
            return;
        }
        doUnorderedExecute(task);
    }
}
