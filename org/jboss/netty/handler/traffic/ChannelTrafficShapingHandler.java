package org.jboss.netty.handler.traffic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

public class ChannelTrafficShapingHandler extends AbstractTrafficShapingHandler {
    private volatile ChannelHandlerContext ctx;
    private final List<ToSend> messagesQueue = new LinkedList();
    private long queueSize;
    private volatile Timeout writeTimeout;

    private static final class ToSend {
        final long relativeTimeAction;
        final MessageEvent toSend;

        private ToSend(long delay, MessageEvent toSend) {
            this.relativeTimeAction = delay;
            this.toSend = toSend;
        }
    }

    public ChannelTrafficShapingHandler(Timer timer, long writeLimit, long readLimit, long checkInterval) {
        super(timer, writeLimit, readLimit, checkInterval);
    }

    public ChannelTrafficShapingHandler(Timer timer, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(timer, writeLimit, readLimit, checkInterval, maxTime);
    }

    public ChannelTrafficShapingHandler(Timer timer, long writeLimit, long readLimit) {
        super(timer, writeLimit, readLimit);
    }

    public ChannelTrafficShapingHandler(Timer timer, long checkInterval) {
        super(timer, checkInterval);
    }

    public ChannelTrafficShapingHandler(Timer timer) {
        super(timer);
    }

    public ChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long checkInterval) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval);
    }

    public ChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, maxTime);
    }

    public ChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit) {
        super(objectSizeEstimator, timer, writeLimit, readLimit);
    }

    public ChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long checkInterval) {
        super(objectSizeEstimator, timer, checkInterval);
    }

    public ChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer) {
        super(objectSizeEstimator, timer);
    }

    void submitWrite(final ChannelHandlerContext ctx, MessageEvent evt, long size, long delay, long now) throws Exception {
        if (ctx == null) {
            this.ctx = ctx;
        }
        Channel channel = ctx.getChannel();
        synchronized (this) {
            if (delay == 0) {
                if (this.messagesQueue.isEmpty()) {
                    if (channel.isConnected()) {
                        if (this.trafficCounter != null) {
                            this.trafficCounter.bytesRealWriteFlowControl(size);
                        }
                        ctx.sendDownstream(evt);
                        return;
                    }
                    return;
                }
            }
            if (this.timer == null) {
                Thread.sleep(delay);
                if (channel.isConnected()) {
                    if (this.trafficCounter != null) {
                        this.trafficCounter.bytesRealWriteFlowControl(size);
                    }
                    ctx.sendDownstream(evt);
                    return;
                }
            } else if (channel.isConnected()) {
                ToSend newToSend = new ToSend(delay + now, evt);
                this.messagesQueue.add(newToSend);
                this.queueSize += size;
                checkWriteSuspend(ctx, delay, this.queueSize);
                final long futureNow = newToSend.relativeTimeAction;
                this.writeTimeout = this.timer.newTimeout(new TimerTask() {
                    public void run(Timeout timeout) throws Exception {
                        ChannelTrafficShapingHandler.this.sendAllValid(ctx, futureNow);
                    }
                }, 1 + delay, TimeUnit.MILLISECONDS);
            }
        }
    }

    private void sendAllValid(ChannelHandlerContext ctx, long now) throws Exception {
        Channel channel = ctx.getChannel();
        if (channel.isConnected()) {
            synchronized (this) {
                while (!this.messagesQueue.isEmpty()) {
                    ToSend newToSend = (ToSend) this.messagesQueue.remove(0);
                    if (newToSend.relativeTimeAction > now) {
                        this.messagesQueue.add(0, newToSend);
                        break;
                    }
                    long size = calculateSize(newToSend.toSend.getMessage());
                    if (this.trafficCounter != null) {
                        this.trafficCounter.bytesRealWriteFlowControl(size);
                    }
                    this.queueSize -= size;
                    if (!channel.isConnected()) {
                        break;
                    }
                    ctx.sendDownstream(newToSend.toSend);
                }
                if (this.messagesQueue.isEmpty()) {
                    releaseWriteSuspended(ctx);
                }
            }
        }
    }

    public long queueSize() {
        return this.queueSize;
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (this.trafficCounter != null) {
            this.trafficCounter.stop();
        }
        synchronized (this) {
            this.messagesQueue.clear();
        }
        if (this.writeTimeout != null) {
            this.writeTimeout.cancel();
        }
        super.channelClosed(ctx, e);
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.ctx = ctx;
        ReadWriteStatus rws = AbstractTrafficShapingHandler.checkAttachment(ctx);
        rws.readSuspend = true;
        ctx.getChannel().setReadable(false);
        if (this.trafficCounter == null && this.timer != null) {
            this.trafficCounter = new TrafficCounter(this, this.timer, "ChannelTC" + ctx.getChannel().getId(), this.checkInterval);
        }
        if (this.trafficCounter != null) {
            this.trafficCounter.start();
        }
        rws.readSuspend = false;
        ctx.getChannel().setReadable(true);
        super.channelConnected(ctx, e);
    }

    public void releaseExternalResources() {
        Channel channel = this.ctx.getChannel();
        synchronized (this) {
            if (this.ctx != null && this.ctx.getChannel().isConnected()) {
                for (ToSend toSend : this.messagesQueue) {
                    if (!channel.isConnected()) {
                        break;
                    }
                    this.ctx.sendDownstream(toSend.toSend);
                }
            }
            this.messagesQueue.clear();
        }
        if (this.writeTimeout != null) {
            this.writeTimeout.cancel();
        }
        super.releaseExternalResources();
    }
}
