package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.AbstractChannelSink;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.ChannelRunnableWrapper;

public abstract class AbstractOioChannelSink extends AbstractChannelSink {
    public ChannelFuture execute(ChannelPipeline pipeline, Runnable task) {
        Channel ch = pipeline.getChannel();
        if (ch instanceof AbstractOioChannel) {
            AbstractOioChannel channel = (AbstractOioChannel) ch;
            if (channel.worker != null) {
                ChannelRunnableWrapper wrapper = new ChannelRunnableWrapper(pipeline.getChannel(), task);
                channel.worker.executeInIoThread(wrapper);
                return wrapper;
            }
        }
        return super.execute(pipeline, task);
    }

    protected boolean isFireExceptionCaughtLater(ChannelEvent event, Throwable actualCause) {
        Channel channel = event.getChannel();
        if (channel instanceof AbstractOioChannel) {
            return !AbstractOioWorker.isIoThread((AbstractOioChannel) channel);
        } else {
            return false;
        }
    }
}
