package org.jboss.netty.handler.traffic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.ObjectSizeEstimator;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.internal.ConcurrentHashMap;

@Sharable
public class GlobalTrafficShapingHandler extends AbstractTrafficShapingHandler {
    private final ConcurrentMap<Integer, PerChannel> channelQueues = new ConcurrentHashMap();
    long maxGlobalWriteSize = 419430400;
    private final AtomicLong queuesSize = new AtomicLong();

    private static final class PerChannel {
        ChannelHandlerContext ctx;
        long lastReadTimestamp;
        long lastWriteTimestamp;
        List<ToSend> messagesQueue;
        long queueSize;

        private PerChannel() {
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

    void createGlobalTrafficCounter() {
        if (this.timer != null) {
            TrafficCounter tc = new TrafficCounter(this, this.timer, "GlobalTC", this.checkInterval);
            setTrafficCounter(tc);
            tc.start();
        }
    }

    public GlobalTrafficShapingHandler(Timer timer, long writeLimit, long readLimit, long checkInterval) {
        super(timer, writeLimit, readLimit, checkInterval);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(Timer timer, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(timer, writeLimit, readLimit, checkInterval, maxTime);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(Timer timer, long writeLimit, long readLimit) {
        super(timer, writeLimit, readLimit);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(Timer timer, long checkInterval) {
        super(timer, checkInterval);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(Timer timer) {
        super(timer);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long checkInterval) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval, maxTime);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long writeLimit, long readLimit) {
        super(objectSizeEstimator, timer, writeLimit, readLimit);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer, long checkInterval) {
        super(objectSizeEstimator, timer, checkInterval);
        createGlobalTrafficCounter();
    }

    public GlobalTrafficShapingHandler(ObjectSizeEstimator objectSizeEstimator, Timer timer) {
        super(objectSizeEstimator, timer);
        createGlobalTrafficCounter();
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

    private synchronized PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
        PerChannel perChannel;
        Integer key = Integer.valueOf(ctx.getChannel().hashCode());
        perChannel = (PerChannel) this.channelQueues.get(key);
        if (perChannel == null) {
            perChannel = new PerChannel();
            perChannel.messagesQueue = new LinkedList();
            perChannel.ctx = ctx;
            perChannel.queueSize = 0;
            perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
            perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp;
            this.channelQueues.put(key, perChannel);
        }
        return perChannel;
    }

    long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        PerChannel perChannel = (PerChannel) this.channelQueues.get(Integer.valueOf(ctx.getChannel().hashCode()));
        if (perChannel == null || wait <= this.maxTime || (now + wait) - perChannel.lastReadTimestamp <= this.maxTime) {
            return wait;
        }
        return this.maxTime;
    }

    void informReadOperation(ChannelHandlerContext ctx, long now) {
        PerChannel perChannel = (PerChannel) this.channelQueues.get(Integer.valueOf(ctx.getChannel().hashCode()));
        if (perChannel != null) {
            perChannel.lastReadTimestamp = now;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void submitWrite(org.jboss.netty.channel.ChannelHandlerContext r20, org.jboss.netty.channel.MessageEvent r21, long r22, long r24, long r26) throws java.lang.Exception {
        /*
        r19 = this;
        r14 = r19.getOrSetPerChannel(r20);
        r11 = 0;
        r10 = r20.getChannel();
        monitor-enter(r14);
        r4 = 0;
        r3 = (r24 > r4 ? 1 : (r24 == r4 ? 0 : -1));
        if (r3 != 0) goto L_0x003b;
    L_0x0010:
        r3 = r14.messagesQueue;	 Catch:{ all -> 0x0038 }
        r3 = r3.isEmpty();	 Catch:{ all -> 0x0038 }
        if (r3 == 0) goto L_0x003b;
    L_0x0018:
        r3 = r10.isConnected();	 Catch:{ all -> 0x0038 }
        if (r3 != 0) goto L_0x0020;
    L_0x001e:
        monitor-exit(r14);	 Catch:{ all -> 0x0038 }
    L_0x001f:
        return;
    L_0x0020:
        r0 = r19;
        r3 = r0.trafficCounter;	 Catch:{ all -> 0x0038 }
        if (r3 == 0) goto L_0x002f;
    L_0x0026:
        r0 = r19;
        r3 = r0.trafficCounter;	 Catch:{ all -> 0x0038 }
        r0 = r22;
        r3.bytesRealWriteFlowControl(r0);	 Catch:{ all -> 0x0038 }
    L_0x002f:
        r20.sendDownstream(r21);	 Catch:{ all -> 0x0038 }
        r0 = r26;
        r14.lastWriteTimestamp = r0;	 Catch:{ all -> 0x0038 }
        monitor-exit(r14);	 Catch:{ all -> 0x0038 }
        goto L_0x001f;
    L_0x0038:
        r3 = move-exception;
        monitor-exit(r14);	 Catch:{ all -> 0x0038 }
        throw r3;
    L_0x003b:
        r12 = r24;
        r0 = r19;
        r4 = r0.maxTime;	 Catch:{ all -> 0x0038 }
        r3 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1));
        if (r3 <= 0) goto L_0x005b;
    L_0x0045:
        r4 = r26 + r12;
        r0 = r14.lastWriteTimestamp;	 Catch:{ all -> 0x0038 }
        r16 = r0;
        r4 = r4 - r16;
        r0 = r19;
        r0 = r0.maxTime;	 Catch:{ all -> 0x0038 }
        r16 = r0;
        r3 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1));
        if (r3 <= 0) goto L_0x005b;
    L_0x0057:
        r0 = r19;
        r12 = r0.maxTime;	 Catch:{ all -> 0x0038 }
    L_0x005b:
        r0 = r19;
        r3 = r0.timer;	 Catch:{ all -> 0x0038 }
        if (r3 != 0) goto L_0x0088;
    L_0x0061:
        java.lang.Thread.sleep(r12);	 Catch:{ all -> 0x0038 }
        r3 = r20.getChannel();	 Catch:{ all -> 0x0038 }
        r3 = r3.isConnected();	 Catch:{ all -> 0x0038 }
        if (r3 != 0) goto L_0x0070;
    L_0x006e:
        monitor-exit(r14);	 Catch:{ all -> 0x0038 }
        goto L_0x001f;
    L_0x0070:
        r0 = r19;
        r3 = r0.trafficCounter;	 Catch:{ all -> 0x0038 }
        if (r3 == 0) goto L_0x007f;
    L_0x0076:
        r0 = r19;
        r3 = r0.trafficCounter;	 Catch:{ all -> 0x0038 }
        r0 = r22;
        r3.bytesRealWriteFlowControl(r0);	 Catch:{ all -> 0x0038 }
    L_0x007f:
        r20.sendDownstream(r21);	 Catch:{ all -> 0x0038 }
        r0 = r26;
        r14.lastWriteTimestamp = r0;	 Catch:{ all -> 0x0038 }
        monitor-exit(r14);	 Catch:{ all -> 0x0038 }
        goto L_0x001f;
    L_0x0088:
        r3 = r20.getChannel();	 Catch:{ all -> 0x0038 }
        r3 = r3.isConnected();	 Catch:{ all -> 0x0038 }
        if (r3 != 0) goto L_0x0094;
    L_0x0092:
        monitor-exit(r14);	 Catch:{ all -> 0x0038 }
        goto L_0x001f;
    L_0x0094:
        r2 = new org.jboss.netty.handler.traffic.GlobalTrafficShapingHandler$ToSend;	 Catch:{ all -> 0x0038 }
        r3 = r12 + r26;
        r8 = 0;
        r5 = r21;
        r6 = r22;
        r2.<init>(r3, r5, r6);	 Catch:{ all -> 0x0038 }
        r3 = r14.messagesQueue;	 Catch:{ all -> 0x0038 }
        r3.add(r2);	 Catch:{ all -> 0x0038 }
        r4 = r14.queueSize;	 Catch:{ all -> 0x0038 }
        r4 = r4 + r22;
        r14.queueSize = r4;	 Catch:{ all -> 0x0038 }
        r0 = r19;
        r3 = r0.queuesSize;	 Catch:{ all -> 0x0038 }
        r0 = r22;
        r3.addAndGet(r0);	 Catch:{ all -> 0x0038 }
        r8 = r14.queueSize;	 Catch:{ all -> 0x0038 }
        r4 = r19;
        r5 = r20;
        r6 = r12;
        r4.checkWriteSuspend(r5, r6, r8);	 Catch:{ all -> 0x0038 }
        r0 = r19;
        r3 = r0.queuesSize;	 Catch:{ all -> 0x0038 }
        r4 = r3.get();	 Catch:{ all -> 0x0038 }
        r0 = r19;
        r0 = r0.maxGlobalWriteSize;	 Catch:{ all -> 0x0038 }
        r16 = r0;
        r3 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1));
        if (r3 <= 0) goto L_0x00d1;
    L_0x00d0:
        r11 = 1;
    L_0x00d1:
        monitor-exit(r14);	 Catch:{ all -> 0x0038 }
        if (r11 == 0) goto L_0x00dc;
    L_0x00d4:
        r3 = 0;
        r0 = r19;
        r1 = r20;
        r0.setWritable(r1, r3);
    L_0x00dc:
        r8 = r2.relativeTimeAction;
        r7 = r14;
        r0 = r19;
        r3 = r0.timer;
        r4 = new org.jboss.netty.handler.traffic.GlobalTrafficShapingHandler$1;
        r5 = r19;
        r6 = r20;
        r4.<init>(r6, r7, r8);
        r5 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r3.newTimeout(r4, r12, r5);
        goto L_0x001f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.traffic.GlobalTrafficShapingHandler.submitWrite(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent, long, long, long):void");
    }

    private void sendAllValid(ChannelHandlerContext ctx, PerChannel perChannel, long now) throws Exception {
        Channel channel = ctx.getChannel();
        if (channel.isConnected()) {
            synchronized (perChannel) {
                while (!perChannel.messagesQueue.isEmpty()) {
                    ToSend newToSend = (ToSend) perChannel.messagesQueue.remove(0);
                    if (newToSend.relativeTimeAction > now) {
                        perChannel.messagesQueue.add(0, newToSend);
                        break;
                    } else if (!channel.isConnected()) {
                        break;
                    } else {
                        long size = newToSend.size;
                        if (this.trafficCounter != null) {
                            this.trafficCounter.bytesRealWriteFlowControl(size);
                        }
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
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        getOrSetPerChannel(ctx);
        super.channelConnected(ctx, e);
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        PerChannel perChannel = (PerChannel) this.channelQueues.remove(Integer.valueOf(ctx.getChannel().hashCode()));
        if (perChannel != null) {
            synchronized (perChannel) {
                this.queuesSize.addAndGet(-perChannel.queueSize);
                perChannel.messagesQueue.clear();
            }
        }
        super.channelClosed(ctx, e);
    }

    public void releaseExternalResources() {
        for (PerChannel perChannel : this.channelQueues.values()) {
            if (!(perChannel == null || perChannel.ctx == null || !perChannel.ctx.getChannel().isConnected())) {
                Channel channel = perChannel.ctx.getChannel();
                synchronized (perChannel) {
                    for (ToSend toSend : perChannel.messagesQueue) {
                        if (!channel.isConnected()) {
                            break;
                        }
                        perChannel.ctx.sendDownstream(toSend.toSend);
                    }
                    perChannel.messagesQueue.clear();
                }
            }
        }
        this.channelQueues.clear();
        this.queuesSize.set(0);
        super.releaseExternalResources();
    }
}
