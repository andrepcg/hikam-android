package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.AbstractChannelSink;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.ChannelRunnableWrapper;

public abstract class AbstractNioChannelSink extends AbstractChannelSink {
    public ChannelFuture execute(ChannelPipeline pipeline, Runnable task) {
        Channel ch = pipeline.getChannel();
        if (!(ch instanceof AbstractNioChannel)) {
            return super.execute(pipeline, task);
        }
        AbstractNioChannel<?> channel = (AbstractNioChannel) ch;
        ChannelRunnableWrapper wrapper = new ChannelRunnableWrapper(pipeline.getChannel(), task);
        channel.worker.executeInIoThread(wrapper);
        return wrapper;
    }

    protected boolean isFireExceptionCaughtLater(ChannelEvent event, Throwable actualCause) {
        Channel channel = event.getChannel();
        if (channel instanceof AbstractNioChannel) {
            return !AbstractNioWorker.isIoThread((AbstractNioChannel) channel);
        } else {
            return false;
        }
    }
}
