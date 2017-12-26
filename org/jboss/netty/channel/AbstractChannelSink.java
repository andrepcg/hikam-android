package org.jboss.netty.channel;

public abstract class AbstractChannelSink implements ChannelSink {
    protected AbstractChannelSink() {
    }

    public void exceptionCaught(ChannelPipeline pipeline, ChannelEvent event, ChannelPipelineException cause) throws Exception {
        Throwable actualCause = cause.getCause();
        if (actualCause == null) {
            actualCause = cause;
        }
        if (isFireExceptionCaughtLater(event, actualCause)) {
            Channels.fireExceptionCaughtLater(event.getChannel(), actualCause);
        } else {
            Channels.fireExceptionCaught(event.getChannel(), actualCause);
        }
    }

    protected boolean isFireExceptionCaughtLater(ChannelEvent event, Throwable actualCause) {
        return false;
    }

    public ChannelFuture execute(ChannelPipeline pipeline, Runnable task) {
        try {
            task.run();
            return Channels.succeededFuture(pipeline.getChannel());
        } catch (Throwable t) {
            return Channels.failedFuture(pipeline.getChannel(), t);
        }
    }
}
