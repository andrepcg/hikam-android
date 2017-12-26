package org.jboss.netty.handler.timeout;

import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

@Sharable
public class IdleStateHandler extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler, ExternalResourceReleasable {
    final long allIdleTimeMillis;
    final long readerIdleTimeMillis;
    final Timer timer;
    final long writerIdleTimeMillis;

    private static final class State {
        volatile Timeout allIdleTimeout;
        volatile long lastReadTime;
        volatile long lastWriteTime;
        volatile Timeout readerIdleTimeout;
        int state;
        volatile Timeout writerIdleTimeout;

        State() {
        }
    }

    private final class AllIdleTimeoutTask implements TimerTask {
        private final ChannelHandlerContext ctx;

        AllIdleTimeoutTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run(Timeout timeout) throws Exception {
            if (!timeout.isCancelled() && this.ctx.getChannel().isOpen()) {
                State state = (State) this.ctx.getAttachment();
                long currentTime = System.currentTimeMillis();
                long lastIoTime = Math.max(state.lastReadTime, state.lastWriteTime);
                long nextDelay = IdleStateHandler.this.allIdleTimeMillis - (currentTime - lastIoTime);
                if (nextDelay <= 0) {
                    state.allIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, IdleStateHandler.this.allIdleTimeMillis, TimeUnit.MILLISECONDS);
                    IdleStateHandler.this.fireChannelIdle(this.ctx, IdleState.ALL_IDLE, lastIoTime);
                    return;
                }
                state.allIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }

    private final class ReaderIdleTimeoutTask implements TimerTask {
        private final ChannelHandlerContext ctx;

        ReaderIdleTimeoutTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run(Timeout timeout) throws Exception {
            if (!timeout.isCancelled() && this.ctx.getChannel().isOpen()) {
                State state = (State) this.ctx.getAttachment();
                long currentTime = System.currentTimeMillis();
                long lastReadTime = state.lastReadTime;
                long nextDelay = IdleStateHandler.this.readerIdleTimeMillis - (currentTime - lastReadTime);
                if (nextDelay <= 0) {
                    state.readerIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, IdleStateHandler.this.readerIdleTimeMillis, TimeUnit.MILLISECONDS);
                    IdleStateHandler.this.fireChannelIdle(this.ctx, IdleState.READER_IDLE, lastReadTime);
                    return;
                }
                state.readerIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }

    private final class WriterIdleTimeoutTask implements TimerTask {
        private final ChannelHandlerContext ctx;

        WriterIdleTimeoutTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run(Timeout timeout) throws Exception {
            if (!timeout.isCancelled() && this.ctx.getChannel().isOpen()) {
                State state = (State) this.ctx.getAttachment();
                long currentTime = System.currentTimeMillis();
                long lastWriteTime = state.lastWriteTime;
                long nextDelay = IdleStateHandler.this.writerIdleTimeMillis - (currentTime - lastWriteTime);
                if (nextDelay <= 0) {
                    state.writerIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, IdleStateHandler.this.writerIdleTimeMillis, TimeUnit.MILLISECONDS);
                    IdleStateHandler.this.fireChannelIdle(this.ctx, IdleState.WRITER_IDLE, lastWriteTime);
                    return;
                }
                state.writerIdleTimeout = IdleStateHandler.this.timer.newTimeout(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }

    public IdleStateHandler(Timer timer, int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        this(timer, (long) readerIdleTimeSeconds, (long) writerIdleTimeSeconds, (long) allIdleTimeSeconds, TimeUnit.SECONDS);
    }

    public IdleStateHandler(Timer timer, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        if (timer == null) {
            throw new NullPointerException("timer");
        } else if (unit == null) {
            throw new NullPointerException("unit");
        } else {
            this.timer = timer;
            if (readerIdleTime <= 0) {
                this.readerIdleTimeMillis = 0;
            } else {
                this.readerIdleTimeMillis = Math.max(unit.toMillis(readerIdleTime), 1);
            }
            if (writerIdleTime <= 0) {
                this.writerIdleTimeMillis = 0;
            } else {
                this.writerIdleTimeMillis = Math.max(unit.toMillis(writerIdleTime), 1);
            }
            if (allIdleTime <= 0) {
                this.allIdleTimeMillis = 0;
            } else {
                this.allIdleTimeMillis = Math.max(unit.toMillis(allIdleTime), 1);
            }
        }
    }

    public long getReaderIdleTimeInMillis() {
        return this.readerIdleTimeMillis;
    }

    public long getWriterIdleTimeInMillis() {
        return this.writerIdleTimeMillis;
    }

    public long getAllIdleTimeInMillis() {
        return this.allIdleTimeMillis;
    }

    public void releaseExternalResources() {
        this.timer.stop();
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
        if (ctx.getPipeline().isAttached()) {
            initialize(ctx);
        }
    }

    public void afterAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
        destroy(ctx);
    }

    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
    }

    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        initialize(ctx);
        ctx.sendUpstream(e);
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        destroy(ctx);
        ctx.sendUpstream(e);
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ((State) ctx.getAttachment()).lastReadTime = System.currentTimeMillis();
        ctx.sendUpstream(e);
    }

    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        if (e.getWrittenAmount() > 0) {
            ((State) ctx.getAttachment()).lastWriteTime = System.currentTimeMillis();
        }
        ctx.sendUpstream(e);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initialize(org.jboss.netty.channel.ChannelHandlerContext r9) {
        /*
        r8 = this;
        r6 = 0;
        r0 = state(r9);
        monitor-enter(r0);
        r1 = r0.state;	 Catch:{ all -> 0x0060 }
        switch(r1) {
            case 1: goto L_0x005e;
            case 2: goto L_0x005e;
            default: goto L_0x000c;
        };	 Catch:{ all -> 0x0060 }
    L_0x000c:
        r1 = 1;
        r0.state = r1;	 Catch:{ all -> 0x0060 }
        monitor-exit(r0);	 Catch:{ all -> 0x0060 }
        r2 = java.lang.System.currentTimeMillis();
        r0.lastWriteTime = r2;
        r0.lastReadTime = r2;
        r2 = r8.readerIdleTimeMillis;
        r1 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r1 <= 0) goto L_0x002f;
    L_0x001e:
        r1 = r8.timer;
        r2 = new org.jboss.netty.handler.timeout.IdleStateHandler$ReaderIdleTimeoutTask;
        r2.<init>(r9);
        r4 = r8.readerIdleTimeMillis;
        r3 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r1 = r1.newTimeout(r2, r4, r3);
        r0.readerIdleTimeout = r1;
    L_0x002f:
        r2 = r8.writerIdleTimeMillis;
        r1 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r1 <= 0) goto L_0x0046;
    L_0x0035:
        r1 = r8.timer;
        r2 = new org.jboss.netty.handler.timeout.IdleStateHandler$WriterIdleTimeoutTask;
        r2.<init>(r9);
        r4 = r8.writerIdleTimeMillis;
        r3 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r1 = r1.newTimeout(r2, r4, r3);
        r0.writerIdleTimeout = r1;
    L_0x0046:
        r2 = r8.allIdleTimeMillis;
        r1 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r1 <= 0) goto L_0x005d;
    L_0x004c:
        r1 = r8.timer;
        r2 = new org.jboss.netty.handler.timeout.IdleStateHandler$AllIdleTimeoutTask;
        r2.<init>(r9);
        r4 = r8.allIdleTimeMillis;
        r3 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r1 = r1.newTimeout(r2, r4, r3);
        r0.allIdleTimeout = r1;
    L_0x005d:
        return;
    L_0x005e:
        monitor-exit(r0);	 Catch:{ all -> 0x0060 }
        goto L_0x005d;
    L_0x0060:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0060 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.timeout.IdleStateHandler.initialize(org.jboss.netty.channel.ChannelHandlerContext):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void destroy(org.jboss.netty.channel.ChannelHandlerContext r4) {
        /*
        r3 = 0;
        r0 = state(r4);
        monitor-enter(r0);
        r1 = r0.state;	 Catch:{ all -> 0x0033 }
        r2 = 1;
        if (r1 == r2) goto L_0x000d;
    L_0x000b:
        monitor-exit(r0);	 Catch:{ all -> 0x0033 }
    L_0x000c:
        return;
    L_0x000d:
        r1 = 2;
        r0.state = r1;	 Catch:{ all -> 0x0033 }
        monitor-exit(r0);	 Catch:{ all -> 0x0033 }
        r1 = r0.readerIdleTimeout;
        if (r1 == 0) goto L_0x001c;
    L_0x0015:
        r1 = r0.readerIdleTimeout;
        r1.cancel();
        r0.readerIdleTimeout = r3;
    L_0x001c:
        r1 = r0.writerIdleTimeout;
        if (r1 == 0) goto L_0x0027;
    L_0x0020:
        r1 = r0.writerIdleTimeout;
        r1.cancel();
        r0.writerIdleTimeout = r3;
    L_0x0027:
        r1 = r0.allIdleTimeout;
        if (r1 == 0) goto L_0x000c;
    L_0x002b:
        r1 = r0.allIdleTimeout;
        r1.cancel();
        r0.allIdleTimeout = r3;
        goto L_0x000c;
    L_0x0033:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0033 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.timeout.IdleStateHandler.destroy(org.jboss.netty.channel.ChannelHandlerContext):void");
    }

    private static State state(ChannelHandlerContext ctx) {
        synchronized (ctx) {
            State state = (State) ctx.getAttachment();
            if (state != null) {
                return state;
            }
            state = new State();
            ctx.setAttachment(state);
            return state;
        }
    }

    private void fireChannelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis) {
        final ChannelHandlerContext channelHandlerContext = ctx;
        final IdleState idleState = state;
        final long j = lastActivityTimeMillis;
        ctx.getPipeline().execute(new Runnable() {
            public void run() {
                try {
                    IdleStateHandler.this.channelIdle(channelHandlerContext, idleState, j);
                } catch (Throwable t) {
                    Channels.fireExceptionCaught(channelHandlerContext, t);
                }
            }
        });
    }

    protected void channelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis) throws Exception {
        ctx.sendUpstream(new DefaultIdleStateEvent(ctx.getChannel(), state, lastActivityTimeMillis));
    }
}
