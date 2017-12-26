package org.jboss.netty.handler.timeout;

import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

@Sharable
public class WriteTimeoutHandler extends SimpleChannelDownstreamHandler implements ExternalResourceReleasable {
    static final WriteTimeoutException EXCEPTION = new WriteTimeoutException();
    private final long timeoutMillis;
    private final Timer timer;

    private static final class TimeoutCanceller implements ChannelFutureListener {
        private final Timeout timeout;

        TimeoutCanceller(Timeout timeout) {
            this.timeout = timeout;
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            this.timeout.cancel();
        }
    }

    private final class WriteTimeoutTask implements TimerTask {
        private final ChannelHandlerContext ctx;
        private final ChannelFuture future;

        WriteTimeoutTask(ChannelHandlerContext ctx, ChannelFuture future) {
            this.ctx = ctx;
            this.future = future;
        }

        public void run(Timeout timeout) throws Exception {
            if (!timeout.isCancelled() && this.ctx.getChannel().isOpen() && this.future.setFailure(WriteTimeoutHandler.EXCEPTION)) {
                fireWriteTimeOut(this.ctx);
            }
        }

        private void fireWriteTimeOut(final ChannelHandlerContext ctx) {
            ctx.getPipeline().execute(new Runnable() {
                public void run() {
                    try {
                        WriteTimeoutHandler.this.writeTimedOut(ctx);
                    } catch (Throwable t) {
                        Channels.fireExceptionCaught(ctx, t);
                    }
                }
            });
        }
    }

    public WriteTimeoutHandler(Timer timer, int timeoutSeconds) {
        this(timer, (long) timeoutSeconds, TimeUnit.SECONDS);
    }

    public WriteTimeoutHandler(Timer timer, long timeout, TimeUnit unit) {
        if (timer == null) {
            throw new NullPointerException("timer");
        } else if (unit == null) {
            throw new NullPointerException("unit");
        } else {
            this.timer = timer;
            if (timeout <= 0) {
                this.timeoutMillis = 0;
            } else {
                this.timeoutMillis = Math.max(unit.toMillis(timeout), 1);
            }
        }
    }

    public void releaseExternalResources() {
        this.timer.stop();
    }

    protected long getTimeoutMillis(MessageEvent e) {
        return this.timeoutMillis;
    }

    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        long timeoutMillis = getTimeoutMillis(e);
        if (timeoutMillis > 0) {
            ChannelFuture future = e.getFuture();
            future.addListener(new TimeoutCanceller(this.timer.newTimeout(new WriteTimeoutTask(ctx, future), timeoutMillis, TimeUnit.MILLISECONDS)));
        }
        super.writeRequested(ctx, e);
    }

    protected void writeTimedOut(ChannelHandlerContext ctx) throws Exception {
        Channels.fireExceptionCaught(ctx, EXCEPTION);
    }
}
