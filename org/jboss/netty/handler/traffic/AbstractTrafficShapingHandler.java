package org.jboss.netty.handler.traffic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.DefaultObjectSizeEstimator;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

public abstract class AbstractTrafficShapingHandler extends SimpleChannelHandler implements ExternalResourceReleasable {
    static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;
    public static final long DEFAULT_CHECK_INTERVAL = 1000;
    static final long DEFAULT_MAX_SIZE = 4194304;
    public static final long DEFAULT_MAX_TIME = 15000;
    static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;
    static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;
    static final long MINIMAL_WAIT = 10;
    static InternalLogger logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
    protected volatile long checkInterval = 1000;
    final int index = userDefinedWritabilityIndex();
    protected volatile long maxTime = DEFAULT_MAX_TIME;
    volatile long maxWriteDelay = 4000;
    volatile long maxWriteSize = DEFAULT_MAX_SIZE;
    private ObjectSizeEstimator objectSizeEstimator;
    private volatile long readLimit;
    final AtomicBoolean release = new AtomicBoolean(false);
    volatile Timeout timeout;
    protected Timer timer;
    protected TrafficCounter trafficCounter;
    private volatile long writeLimit;

    static final class ReadWriteStatus {
        volatile boolean readSuspend;
        volatile TimerTask reopenReadTimerTask;

        ReadWriteStatus() {
        }
    }

    class ReopenReadTimerTask implements TimerTask {
        final ChannelHandlerContext ctx;

        ReopenReadTimerTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run(Timeout timeoutArg) throws Exception {
            if (!AbstractTrafficShapingHandler.this.release.get()) {
                ReadWriteStatus rws = AbstractTrafficShapingHandler.checkAttachment(this.ctx);
                Channel channel = this.ctx.getChannel();
                if (channel.isConnected()) {
                    if (channel.isReadable() || rws.readSuspend) {
                        if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                            if (channel.isReadable() && rws.readSuspend) {
                                AbstractTrafficShapingHandler.logger.debug("Unsuspend: " + channel.isReadable() + ':' + rws.readSuspend);
                            } else {
                                AbstractTrafficShapingHandler.logger.debug("Normal unsuspend: " + channel.isReadable() + ':' + rws.readSuspend);
                            }
                        }
                        rws.readSuspend = false;
                        channel.setReadable(true);
                    } else {
                        if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                            AbstractTrafficShapingHandler.logger.debug("Not unsuspend: " + channel.isReadable() + ':' + rws.readSuspend);
                        }
                        rws.readSuspend = false;
                    }
                    if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
                        AbstractTrafficShapingHandler.logger.debug("Unsupsend final status => " + channel.isReadable() + ':' + rws.readSuspend);
                    }
                }
            }
        }
    }

    public static class SimpleObjectSizeEstimator extends DefaultObjectSizeEstimator {
        public int estimateSize(Object o) {
            if (o instanceof ChannelBuffer) {
                return ((ChannelBuffer) o).readableBytes();
            }
            return super.estimateSize(o);
        }
    }

    abstract void submitWrite(ChannelHandlerContext channelHandlerContext, MessageEvent messageEvent, long j, long j2, long j3) throws Exception;

    int userDefinedWritabilityIndex() {
        if (this instanceof GlobalChannelTrafficShapingHandler) {
            return 3;
        }
        if (this instanceof GlobalTrafficShapingHandler) {
            return 2;
        }
        return 1;
    }

    private void init(ObjectSizeEstimator newObjectSizeEstimator, Timer newTimer, long newWriteLimit, long newReadLimit, long newCheckInterval, long newMaxTime) {
        if (newMaxTime <= 0) {
            throw new IllegalArgumentException("maxTime must be positive");
        }
        this.objectSizeEstimator = newObjectSizeEstimator;
        this.timer = newTimer;
        this.writeLimit = newWriteLimit;
        this.readLimit = newReadLimit;
        this.checkInterval = newCheckInterval;
        this.maxTime = newMaxTime;
    }

    void setTrafficCounter(TrafficCounter newTrafficCounter) {
        this.trafficCounter = newTrafficCounter;
    }

    protected AbstractTrafficShapingHandler(Timer timer, long writeLimit, long readLimit, long checkInterval) {
        init(new SimpleObjectSizeEstimator(), timer, writeLimit, readLimit, checkInterval, DEFAULT_MAX_TIME);
    }

    protected AbstractTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long checkInterval) {
        init(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, DEFAULT_MAX_TIME);
    }

    protected AbstractTrafficShapingHandler(Timer timer, long writeLimit, long readLimit) {
        init(new SimpleObjectSizeEstimator(), timer, writeLimit, readLimit, 1000, DEFAULT_MAX_TIME);
    }

    protected AbstractTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit) {
        init(objectSizeEstimator, timer, writeLimit, readLimit, 1000, DEFAULT_MAX_TIME);
    }

    protected AbstractTrafficShapingHandler(Timer timer) {
        init(new SimpleObjectSizeEstimator(), timer, 0, 0, 1000, DEFAULT_MAX_TIME);
    }

    protected AbstractTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer) {
        init(objectSizeEstimator, timer, 0, 0, 1000, DEFAULT_MAX_TIME);
    }

    protected AbstractTrafficShapingHandler(Timer timer, long checkInterval) {
        init(new SimpleObjectSizeEstimator(), timer, 0, 0, checkInterval, DEFAULT_MAX_TIME);
    }

    protected AbstractTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long checkInterval) {
        init(objectSizeEstimator, timer, 0, 0, checkInterval, DEFAULT_MAX_TIME);
    }

    protected AbstractTrafficShapingHandler(Timer timer, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        init(new SimpleObjectSizeEstimator(), timer, writeLimit, readLimit, checkInterval, maxTime);
    }

    protected AbstractTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        init(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, maxTime);
    }

    public void configure(long newWriteLimit, long newReadLimit, long newCheckInterval) {
        configure(newWriteLimit, newReadLimit);
        configure(newCheckInterval);
    }

    public void configure(long newWriteLimit, long newReadLimit) {
        this.writeLimit = newWriteLimit;
        this.readLimit = newReadLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }

    public void configure(long newCheckInterval) {
        setCheckInterval(newCheckInterval);
    }

    public long getWriteLimit() {
        return this.writeLimit;
    }

    public void setWriteLimit(long writeLimit) {
        this.writeLimit = writeLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }

    public long getReadLimit() {
        return this.readLimit;
    }

    public void setReadLimit(long readLimit) {
        this.readLimit = readLimit;
        if (this.trafficCounter != null) {
            this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
        }
    }

    public long getCheckInterval() {
        return this.checkInterval;
    }

    public void setCheckInterval(long newCheckInterval) {
        this.checkInterval = newCheckInterval;
        if (this.trafficCounter != null) {
            this.trafficCounter.configure(this.checkInterval);
        }
    }

    public long getMaxTimeWait() {
        return this.maxTime;
    }

    public void setMaxTimeWait(long maxTime) {
        if (maxTime <= 0) {
            throw new IllegalArgumentException("maxTime must be positive");
        }
        this.maxTime = maxTime;
    }

    public long getMaxWriteDelay() {
        return this.maxWriteDelay;
    }

    public void setMaxWriteDelay(long maxWriteDelay) {
        if (maxWriteDelay <= 0) {
            throw new IllegalArgumentException("maxWriteDelay must be positive");
        }
        this.maxWriteDelay = maxWriteDelay;
    }

    public long getMaxWriteSize() {
        return this.maxWriteSize;
    }

    public void setMaxWriteSize(long maxWriteSize) {
        this.maxWriteSize = maxWriteSize;
    }

    protected void doAccounting(TrafficCounter counter) {
    }

    void releaseReadSuspended(ChannelHandlerContext ctx) {
        checkAttachment(ctx).readSuspend = false;
        ctx.getChannel().setReadable(true);
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        long now = TrafficCounter.milliSecondFromNano();
        try {
            ReadWriteStatus rws = checkAttachment(ctx);
            long size = calculateSize(evt.getMessage());
            if (size > 0 && this.trafficCounter != null) {
                long wait = checkWaitReadTime(ctx, this.trafficCounter.readTimeToWait(size, this.readLimit, this.maxTime, now), now);
                if (wait >= MINIMAL_WAIT) {
                    if (this.release.get()) {
                        informReadOperation(ctx, now);
                    } else {
                        Channel channel = ctx.getChannel();
                        if (channel != null && channel.isConnected()) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Read suspend: " + wait + ':' + channel.isReadable() + ':' + rws.readSuspend);
                            }
                            if (this.timer == null) {
                                Thread.sleep(wait);
                                informReadOperation(ctx, now);
                            } else if (channel.isReadable() && !rws.readSuspend) {
                                rws.readSuspend = true;
                                channel.setReadable(false);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Suspend final status => " + channel.isReadable() + ':' + rws.readSuspend);
                                }
                                if (rws.reopenReadTimerTask == null) {
                                    rws.reopenReadTimerTask = new ReopenReadTimerTask(ctx);
                                }
                                this.timeout = this.timer.newTimeout(rws.reopenReadTimerTask, wait, TimeUnit.MILLISECONDS);
                            }
                        }
                    }
                    ctx.sendUpstream(evt);
                }
            }
            informReadOperation(ctx, now);
            ctx.sendUpstream(evt);
        } catch (Throwable th) {
            informReadOperation(ctx, now);
            ctx.sendUpstream(evt);
        }
    }

    long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        return wait;
    }

    void informReadOperation(ChannelHandlerContext ctx, long now) {
    }

    public void writeRequested(ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        long wait = 0;
        long size = calculateSize(evt.getMessage());
        long now = TrafficCounter.milliSecondFromNano();
        Channel channel = ctx.getChannel();
        if (size > 0) {
            try {
                if (this.trafficCounter != null) {
                    wait = this.trafficCounter.writeTimeToWait(size, this.writeLimit, this.maxTime, now);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Write suspend: " + wait + ':' + channel.isWritable() + ':' + channel.getUserDefinedWritability(this.index));
                    }
                    if (wait < MINIMAL_WAIT || this.release.get()) {
                        wait = 0;
                    }
                }
            } catch (Throwable th) {
                submitWrite(ctx, evt, size, 0, now);
            }
        }
        submitWrite(ctx, evt, size, wait, now);
    }

    @Deprecated
    protected void internalSubmitWrite(ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        ctx.sendDownstream(evt);
    }

    @Deprecated
    protected void submitWrite(ChannelHandlerContext ctx, MessageEvent evt, long delay) throws Exception {
        submitWrite(ctx, evt, calculateSize(evt.getMessage()), delay, TrafficCounter.milliSecondFromNano());
    }

    void setWritable(ChannelHandlerContext ctx, boolean writable) {
        Channel channel = ctx.getChannel();
        if (channel.isConnected()) {
            channel.setUserDefinedWritability(this.index, writable);
        }
    }

    void checkWriteSuspend(ChannelHandlerContext ctx, long delay, long queueSize) {
        if (queueSize > this.maxWriteSize || delay > this.maxWriteDelay) {
            setWritable(ctx, false);
        }
    }

    void releaseWriteSuspended(ChannelHandlerContext ctx) {
        setWritable(ctx, true);
    }

    public TrafficCounter getTrafficCounter() {
        return this.trafficCounter;
    }

    public void releaseExternalResources() {
        if (this.trafficCounter != null) {
            this.trafficCounter.stop();
        }
        this.release.set(true);
        if (this.timeout != null) {
            this.timeout.cancel();
        }
    }

    static ReadWriteStatus checkAttachment(ChannelHandlerContext ctx) {
        ReadWriteStatus rws = (ReadWriteStatus) ctx.getAttachment();
        if (rws != null) {
            return rws;
        }
        rws = new ReadWriteStatus();
        ctx.setAttachment(rws);
        return rws;
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        checkAttachment(ctx);
        setWritable(ctx, true);
        super.channelConnected(ctx, e);
    }

    protected long calculateSize(Object obj) {
        return (long) this.objectSizeEstimator.estimateSize(obj);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(290).append("TrafficShaping with Write Limit: ").append(this.writeLimit).append(" Read Limit: ").append(this.readLimit).append(" CheckInterval: ").append(this.checkInterval).append(" maxDelay: ").append(this.maxWriteDelay).append(" maxSize: ").append(this.maxWriteSize).append(" and Counter: ");
        if (this.trafficCounter != null) {
            builder.append(this.trafficCounter);
        } else {
            builder.append("none");
        }
        return builder.toString();
    }
}
