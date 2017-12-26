package org.jboss.netty.handler.traffic;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.internal.ConcurrentHashMap;

@Sharable
public class GlobalChannelTrafficShapingHandler extends AbstractTrafficShapingHandler {
    private static final float DEFAULT_ACCELERATION = -0.1f;
    private static final float DEFAULT_DEVIATION = 0.1f;
    private static final float DEFAULT_SLOWDOWN = 0.4f;
    private static final float MAX_DEVIATION = 0.4f;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalChannelTrafficShapingHandler.class);
    private volatile float accelerationFactor;
    final ConcurrentMap<Integer, PerChannel> channelQueues = new ConcurrentHashMap();
    private final AtomicLong cumulativeReadBytes = new AtomicLong();
    private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
    private volatile float maxDeviation;
    long maxGlobalWriteSize = 419430400;
    private final AtomicLong queuesSize = new AtomicLong();
    private volatile long readChannelLimit;
    private volatile boolean readDeviationActive;
    private volatile float slowDownFactor;
    private volatile long writeChannelLimit;
    private volatile boolean writeDeviationActive;

    class C08751 extends AbstractCollection<TrafficCounter> {

        class C08741 implements Iterator<TrafficCounter> {
            final Iterator<PerChannel> iter = GlobalChannelTrafficShapingHandler.this.channelQueues.values().iterator();

            C08741() {
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public TrafficCounter next() {
                return ((PerChannel) this.iter.next()).channelTrafficCounter;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        C08751() {
        }

        public Iterator<TrafficCounter> iterator() {
            return new C08741();
        }

        public int size() {
            return GlobalChannelTrafficShapingHandler.this.channelQueues.size();
        }
    }

    static final class PerChannel {
        TrafficCounter channelTrafficCounter;
        long lastReadTimestamp;
        long lastWriteTimestamp;
        List<ToSend> messagesQueue;
        long queueSize;

        PerChannel() {
        }
    }

    private static final class ToSend {
        final long relativeTimeAction;
        final long size;
        final MessageEvent toSend;

        private ToSend(long delay, MessageEvent toSend, long size) {
            this.relativeTimeAction = delay;
            this.toSend = toSend;
            this.size = size;
        }
    }

    void createGlobalTrafficCounter(Timer timer) {
        setMaxDeviation(DEFAULT_DEVIATION, 0.4f, DEFAULT_ACCELERATION);
        if (timer == null) {
            throw new IllegalArgumentException("Timer must not be null");
        }
        TrafficCounter tc = new GlobalChannelTrafficCounter(this, timer, "GlobalChannelTC", this.checkInterval);
        setTrafficCounter(tc);
        tc.start();
    }

    int userDefinedWritabilityIndex() {
        return 3;
    }

    public GlobalChannelTrafficShapingHandler(Timer timer, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit, long checkInterval, long maxTime) {
        super(timer, writeGlobalLimit, readGlobalLimit, checkInterval, maxTime);
        createGlobalTrafficCounter(timer);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
    }

    public GlobalChannelTrafficShapingHandler(Timer timer, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit, long checkInterval) {
        super(timer, writeGlobalLimit, readGlobalLimit, checkInterval);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        createGlobalTrafficCounter(timer);
    }

    public GlobalChannelTrafficShapingHandler(Timer timer, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit) {
        super(timer, writeGlobalLimit, readGlobalLimit);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        createGlobalTrafficCounter(timer);
    }

    public GlobalChannelTrafficShapingHandler(Timer timer, long checkInterval) {
        super(timer, checkInterval);
        createGlobalTrafficCounter(timer);
    }

    public GlobalChannelTrafficShapingHandler(Timer timer) {
        super(timer);
        createGlobalTrafficCounter(timer);
    }

    public GlobalChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long writeChannelLimit, long readChannelLimit, long checkInterval, long maxTime) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, maxTime);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        createGlobalTrafficCounter(timer);
    }

    public GlobalChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long writeChannelLimit, long readChannelLimit, long checkInterval) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        createGlobalTrafficCounter(timer);
    }

    public GlobalChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long writeChannelLimit, long readChannelLimit) {
        super(objectSizeEstimator, timer, writeLimit, readLimit);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        createGlobalTrafficCounter(timer);
    }

    public GlobalChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long checkInterval) {
        super(objectSizeEstimator, timer, checkInterval);
        createGlobalTrafficCounter(timer);
    }

    public GlobalChannelTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer) {
        super(objectSizeEstimator, timer);
        createGlobalTrafficCounter(timer);
    }

    public float maxDeviation() {
        return this.maxDeviation;
    }

    public float accelerationFactor() {
        return this.accelerationFactor;
    }

    public float slowDownFactor() {
        return this.slowDownFactor;
    }

    public void setMaxDeviation(float maxDeviation, float slowDownFactor, float accelerationFactor) {
        if (maxDeviation > 0.4f) {
            throw new IllegalArgumentException("maxDeviation must be <= 0.4");
        } else if (slowDownFactor < 0.0f) {
            throw new IllegalArgumentException("slowDownFactor must be >= 0");
        } else if (accelerationFactor > 0.0f) {
            throw new IllegalArgumentException("accelerationFactor must be <= 0");
        } else {
            this.maxDeviation = maxDeviation;
            this.accelerationFactor = 1.0f + accelerationFactor;
            this.slowDownFactor = 1.0f + slowDownFactor;
        }
    }

    private void computeDeviationCumulativeBytes() {
        long maxWrittenBytes = 0;
        long maxReadBytes = 0;
        long minWrittenBytes = Long.MAX_VALUE;
        long minReadBytes = Long.MAX_VALUE;
        for (PerChannel perChannel : this.channelQueues.values()) {
            long value = perChannel.channelTrafficCounter.getCumulativeWrittenBytes();
            if (maxWrittenBytes < value) {
                maxWrittenBytes = value;
            }
            if (minWrittenBytes > value) {
                minWrittenBytes = value;
            }
            value = perChannel.channelTrafficCounter.getCumulativeReadBytes();
            if (maxReadBytes < value) {
                maxReadBytes = value;
            }
            if (minReadBytes > value) {
                minReadBytes = value;
            }
        }
        boolean multiple = this.channelQueues.size() > 1;
        boolean z = multiple && minReadBytes < maxReadBytes / 2;
        this.readDeviationActive = z;
        z = multiple && minWrittenBytes < maxWrittenBytes / 2;
        this.writeDeviationActive = z;
        this.cumulativeWrittenBytes.set(maxWrittenBytes);
        this.cumulativeReadBytes.set(maxReadBytes);
    }

    protected void doAccounting(TrafficCounter counter) {
        computeDeviationCumulativeBytes();
        super.doAccounting(counter);
    }

    private long computeBalancedWait(float maxLocal, float maxGlobal, long wait) {
        if (maxGlobal == 0.0f) {
            return wait;
        }
        float ratio = maxLocal / maxGlobal;
        if (ratio <= this.maxDeviation) {
            ratio = this.accelerationFactor;
        } else if (ratio < 1.0f - this.maxDeviation) {
            return wait;
        } else {
            ratio = this.slowDownFactor;
            if (wait < 10) {
                wait = 10;
            }
        }
        return (long) (((float) wait) * ratio);
    }

    public long getMaxGlobalWriteSize() {
        return this.maxGlobalWriteSize;
    }

    public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
        this.maxGlobalWriteSize = maxGlobalWriteSize;
    }

    public long queuesSize() {
        return this.queuesSize.get();
    }

    public void configureChannel(long newWriteLimit, long newReadLimit) {
        this.writeChannelLimit = newWriteLimit;
        this.readChannelLimit = newReadLimit;
        long now = TrafficCounter.milliSecondFromNano();
        for (PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }

    public long getWriteChannelLimit() {
        return this.writeChannelLimit;
    }

    public void setWriteChannelLimit(long writeLimit) {
        this.writeChannelLimit = writeLimit;
        long now = TrafficCounter.milliSecondFromNano();
        for (PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }

    public long getReadChannelLimit() {
        return this.readChannelLimit;
    }

    public void setReadChannelLimit(long readLimit) {
        this.readChannelLimit = readLimit;
        long now = TrafficCounter.milliSecondFromNano();
        for (PerChannel perChannel : this.channelQueues.values()) {
            perChannel.channelTrafficCounter.resetAccounting(now);
        }
    }

    public final void release() {
        this.trafficCounter.stop();
    }

    private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
        Integer key = Integer.valueOf(ctx.getChannel().hashCode());
        PerChannel perChannel = (PerChannel) this.channelQueues.get(key);
        if (perChannel != null) {
            return perChannel;
        }
        perChannel = new PerChannel();
        perChannel.messagesQueue = new LinkedList();
        perChannel.channelTrafficCounter = new TrafficCounter(this, null, "ChannelTC" + ctx.getChannel().hashCode(), this.checkInterval);
        perChannel.queueSize = 0;
        perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
        perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp;
        this.channelQueues.put(key, perChannel);
        return perChannel;
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        getOrSetPerChannel(ctx);
        this.trafficCounter.resetCumulativeTime();
        super.channelConnected(ctx, e);
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.trafficCounter.resetCumulativeTime();
        PerChannel perChannel = (PerChannel) this.channelQueues.remove(Integer.valueOf(ctx.getChannel().hashCode()));
        if (perChannel != null) {
            synchronized (perChannel) {
                this.queuesSize.addAndGet(-perChannel.queueSize);
                perChannel.messagesQueue.clear();
            }
        }
        releaseWriteSuspended(ctx);
        releaseReadSuspended(ctx);
        super.channelClosed(ctx, e);
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        long now = TrafficCounter.milliSecondFromNano();
        try {
            ReadWriteStatus rws = AbstractTrafficShapingHandler.checkAttachment(ctx);
            long size = calculateSize(evt.getMessage());
            if (size > 0) {
                long waitGlobal = this.trafficCounter.readTimeToWait(size, getReadLimit(), this.maxTime, now);
                PerChannel perChannel = (PerChannel) this.channelQueues.get(Integer.valueOf(ctx.getChannel().hashCode()));
                long wait = 0;
                if (perChannel != null) {
                    wait = perChannel.channelTrafficCounter.readTimeToWait(size, this.readChannelLimit, this.maxTime, now);
                    if (this.readDeviationActive) {
                        long maxLocalRead = perChannel.channelTrafficCounter.getCumulativeReadBytes();
                        long maxGlobalRead = this.cumulativeReadBytes.get();
                        if (maxLocalRead <= 0) {
                            maxLocalRead = 0;
                        }
                        if (maxGlobalRead < maxLocalRead) {
                            maxGlobalRead = maxLocalRead;
                        }
                        wait = computeBalancedWait((float) maxLocalRead, (float) maxGlobalRead, wait);
                    }
                }
                if (wait < waitGlobal) {
                    wait = waitGlobal;
                }
                wait = checkWaitReadTime(ctx, wait, now);
                if (wait >= 10) {
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

    protected long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        PerChannel perChannel = (PerChannel) this.channelQueues.get(Integer.valueOf(ctx.getChannel().hashCode()));
        if (perChannel == null || wait <= this.maxTime || (now + wait) - perChannel.lastReadTimestamp <= this.maxTime) {
            return wait;
        }
        return this.maxTime;
    }

    protected void informReadOperation(ChannelHandlerContext ctx, long now) {
        PerChannel perChannel = (PerChannel) this.channelQueues.get(Integer.valueOf(ctx.getChannel().hashCode()));
        if (perChannel != null) {
            perChannel.lastReadTimestamp = now;
        }
    }

    protected long maximumCumulativeWrittenBytes() {
        return this.cumulativeWrittenBytes.get();
    }

    protected long maximumCumulativeReadBytes() {
        return this.cumulativeReadBytes.get();
    }

    public Collection<TrafficCounter> channelTrafficCounters() {
        return new C08751();
    }

    public void writeRequested(ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        long wait = 0;
        long size = calculateSize(evt.getMessage());
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0) {
            try {
                long waitGlobal = this.trafficCounter.writeTimeToWait(size, getWriteLimit(), this.maxTime, now);
                PerChannel perChannel = (PerChannel) this.channelQueues.get(Integer.valueOf(ctx.getChannel().hashCode()));
                if (perChannel != null) {
                    wait = perChannel.channelTrafficCounter.writeTimeToWait(size, this.writeChannelLimit, this.maxTime, now);
                    if (this.writeDeviationActive) {
                        long maxLocalWrite = perChannel.channelTrafficCounter.getCumulativeWrittenBytes();
                        long maxGlobalWrite = this.cumulativeWrittenBytes.get();
                        if (maxLocalWrite <= 0) {
                            maxLocalWrite = 0;
                        }
                        if (maxGlobalWrite < maxLocalWrite) {
                            maxGlobalWrite = maxLocalWrite;
                        }
                        wait = computeBalancedWait((float) maxLocalWrite, (float) maxGlobalWrite, wait);
                    }
                }
                if (wait < waitGlobal) {
                    wait = waitGlobal;
                }
                if (wait < 10 || this.release.get()) {
                    wait = 0;
                }
            } catch (Throwable th) {
                submitWrite(ctx, evt, size, 0, now);
            }
        }
        submitWrite(ctx, evt, size, wait, now);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void submitWrite(org.jboss.netty.channel.ChannelHandlerContext r20, org.jboss.netty.channel.MessageEvent r21, long r22, long r24, long r26) throws java.lang.Exception {
        /*
        r19 = this;
        r10 = r20.getChannel();
        r3 = r10.hashCode();
        r14 = java.lang.Integer.valueOf(r3);
        r0 = r19;
        r3 = r0.channelQueues;
        r15 = r3.get(r14);
        r15 = (org.jboss.netty.handler.traffic.GlobalChannelTrafficShapingHandler.PerChannel) r15;
        if (r15 != 0) goto L_0x001c;
    L_0x0018:
        r15 = r19.getOrSetPerChannel(r20);
    L_0x001c:
        r12 = r24;
        r11 = 0;
        monitor-enter(r15);
        r4 = 0;
        r3 = (r24 > r4 ? 1 : (r24 == r4 ? 0 : -1));
        if (r3 != 0) goto L_0x0052;
    L_0x0026:
        r3 = r15.messagesQueue;	 Catch:{ all -> 0x004f }
        r3 = r3.isEmpty();	 Catch:{ all -> 0x004f }
        if (r3 == 0) goto L_0x0052;
    L_0x002e:
        r3 = r10.isConnected();	 Catch:{ all -> 0x004f }
        if (r3 != 0) goto L_0x0036;
    L_0x0034:
        monitor-exit(r15);	 Catch:{ all -> 0x004f }
    L_0x0035:
        return;
    L_0x0036:
        r0 = r19;
        r3 = r0.trafficCounter;	 Catch:{ all -> 0x004f }
        r0 = r22;
        r3.bytesRealWriteFlowControl(r0);	 Catch:{ all -> 0x004f }
        r3 = r15.channelTrafficCounter;	 Catch:{ all -> 0x004f }
        r0 = r22;
        r3.bytesRealWriteFlowControl(r0);	 Catch:{ all -> 0x004f }
        r20.sendDownstream(r21);	 Catch:{ all -> 0x004f }
        r0 = r26;
        r15.lastWriteTimestamp = r0;	 Catch:{ all -> 0x004f }
        monitor-exit(r15);	 Catch:{ all -> 0x004f }
        goto L_0x0035;
    L_0x004f:
        r3 = move-exception;
        monitor-exit(r15);	 Catch:{ all -> 0x004f }
        throw r3;
    L_0x0052:
        r0 = r19;
        r4 = r0.maxTime;	 Catch:{ all -> 0x004f }
        r3 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1));
        if (r3 <= 0) goto L_0x0070;
    L_0x005a:
        r4 = r26 + r12;
        r0 = r15.lastWriteTimestamp;	 Catch:{ all -> 0x004f }
        r16 = r0;
        r4 = r4 - r16;
        r0 = r19;
        r0 = r0.maxTime;	 Catch:{ all -> 0x004f }
        r16 = r0;
        r3 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1));
        if (r3 <= 0) goto L_0x0070;
    L_0x006c:
        r0 = r19;
        r12 = r0.maxTime;	 Catch:{ all -> 0x004f }
    L_0x0070:
        r0 = r19;
        r3 = r0.timer;	 Catch:{ all -> 0x004f }
        if (r3 != 0) goto L_0x009e;
    L_0x0076:
        java.lang.Thread.sleep(r12);	 Catch:{ all -> 0x004f }
        r3 = r20.getChannel();	 Catch:{ all -> 0x004f }
        r3 = r3.isConnected();	 Catch:{ all -> 0x004f }
        if (r3 != 0) goto L_0x0085;
    L_0x0083:
        monitor-exit(r15);	 Catch:{ all -> 0x004f }
        goto L_0x0035;
    L_0x0085:
        r0 = r19;
        r3 = r0.trafficCounter;	 Catch:{ all -> 0x004f }
        r0 = r22;
        r3.bytesRealWriteFlowControl(r0);	 Catch:{ all -> 0x004f }
        r3 = r15.channelTrafficCounter;	 Catch:{ all -> 0x004f }
        r0 = r22;
        r3.bytesRealWriteFlowControl(r0);	 Catch:{ all -> 0x004f }
        r20.sendDownstream(r21);	 Catch:{ all -> 0x004f }
        r0 = r26;
        r15.lastWriteTimestamp = r0;	 Catch:{ all -> 0x004f }
        monitor-exit(r15);	 Catch:{ all -> 0x004f }
        goto L_0x0035;
    L_0x009e:
        r3 = r20.getChannel();	 Catch:{ all -> 0x004f }
        r3 = r3.isConnected();	 Catch:{ all -> 0x004f }
        if (r3 != 0) goto L_0x00aa;
    L_0x00a8:
        monitor-exit(r15);	 Catch:{ all -> 0x004f }
        goto L_0x0035;
    L_0x00aa:
        r2 = new org.jboss.netty.handler.traffic.GlobalChannelTrafficShapingHandler$ToSend;	 Catch:{ all -> 0x004f }
        r3 = r12 + r26;
        r8 = 0;
        r5 = r21;
        r6 = r22;
        r2.<init>(r3, r5, r6);	 Catch:{ all -> 0x004f }
        r3 = r15.messagesQueue;	 Catch:{ all -> 0x004f }
        r3.add(r2);	 Catch:{ all -> 0x004f }
        r4 = r15.queueSize;	 Catch:{ all -> 0x004f }
        r4 = r4 + r22;
        r15.queueSize = r4;	 Catch:{ all -> 0x004f }
        r0 = r19;
        r3 = r0.queuesSize;	 Catch:{ all -> 0x004f }
        r0 = r22;
        r3.addAndGet(r0);	 Catch:{ all -> 0x004f }
        r8 = r15.queueSize;	 Catch:{ all -> 0x004f }
        r4 = r19;
        r5 = r20;
        r6 = r12;
        r4.checkWriteSuspend(r5, r6, r8);	 Catch:{ all -> 0x004f }
        r0 = r19;
        r3 = r0.queuesSize;	 Catch:{ all -> 0x004f }
        r4 = r3.get();	 Catch:{ all -> 0x004f }
        r0 = r19;
        r0 = r0.maxGlobalWriteSize;	 Catch:{ all -> 0x004f }
        r16 = r0;
        r3 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1));
        if (r3 <= 0) goto L_0x00e7;
    L_0x00e6:
        r11 = 1;
    L_0x00e7:
        monitor-exit(r15);	 Catch:{ all -> 0x004f }
        if (r11 == 0) goto L_0x00f2;
    L_0x00ea:
        r3 = 0;
        r0 = r19;
        r1 = r20;
        r0.setWritable(r1, r3);
    L_0x00f2:
        r8 = r2.relativeTimeAction;
        r7 = r15;
        r0 = r19;
        r3 = r0.timer;
        r4 = new org.jboss.netty.handler.traffic.GlobalChannelTrafficShapingHandler$2;
        r5 = r19;
        r6 = r20;
        r4.<init>(r6, r7, r8);
        r5 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r3.newTimeout(r4, r12, r5);
        goto L_0x0035;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.traffic.GlobalChannelTrafficShapingHandler.submitWrite(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent, long, long, long):void");
    }

    private void sendAllValid(ChannelHandlerContext ctx, PerChannel perChannel, long now) throws Exception {
        synchronized (perChannel) {
            while (!perChannel.messagesQueue.isEmpty()) {
                ToSend newToSend = (ToSend) perChannel.messagesQueue.remove(0);
                if (newToSend.relativeTimeAction > now) {
                    perChannel.messagesQueue.add(0, newToSend);
                    break;
                } else if (!ctx.getChannel().isConnected()) {
                    break;
                } else {
                    long size = newToSend.size;
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                    perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
                    perChannel.queueSize -= size;
                    this.queuesSize.addAndGet(-size);
                    ctx.sendDownstream(newToSend.toSend);
                    perChannel.lastWriteTimestamp = now;
                }
            }
            if (perChannel.messagesQueue.isEmpty()) {
                releaseWriteSuspended(ctx);
            }
        }
    }

    public String toString() {
        return super.toString() + " Write Channel Limit: " + this.writeChannelLimit + " Read Channel Limit: " + this.readChannelLimit;
    }
}
