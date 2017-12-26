package org.jboss.netty.handler.timeout;

import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

@Sharable
public class ReadTimeoutHandler extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler, ExternalResourceReleasable {
    static final ReadTimeoutException EXCEPTION = new ReadTimeoutException();
    final long timeoutMillis;
    final Timer timer;

    private static final class State {
        volatile long lastReadTime = System.currentTimeMillis();
        int state;
        volatile Timeout timeout;

        State() {
        }
    }

    private final class ReadTimeoutTask implements TimerTask {
        private final ChannelHandlerContext ctx;

        ReadTimeoutTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run(Timeout timeout) throws Exception {
            if (!timeout.isCancelled() && this.ctx.getChannel().isOpen()) {
                State state = (State) this.ctx.getAttachment();
                long nextDelay = ReadTimeoutHandler.this.timeoutMillis - (System.currentTimeMillis() - state.lastReadTime);
                if (nextDelay <= 0) {
                    state.timeout = ReadTimeoutHandler.this.timer.newTimeout(this, ReadTimeoutHandler.this.timeoutMillis, TimeUnit.MILLISECONDS);
                    fireReadTimedOut(this.ctx);
                    return;
                }
                state.timeout = ReadTimeoutHandler.this.timer.newTimeout(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }

        private void fireReadTimedOut(final ChannelHandlerContext ctx) throws Exception {
            ctx.getPipeline().execute(new Runnable() {
                public void run() {
                    try {
                        ReadTimeoutHandler.this.readTimedOut(ctx);
                    } catch (Throwable t) {
                        Channels.fireExceptionCaught(ctx, t);
                    }
                }
            });
        }
    }

    public ReadTimeoutHandler(Timer timer, int timeoutSeconds) {
        this(timer, (long) timeoutSeconds, TimeUnit.SECONDS);
    }

    public ReadTimeoutHandler(Timer timer, long timeout, TimeUnit unit) {
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initialize(org.jboss.netty.channel.ChannelHandlerContext r7) {
        /*
        r6 = this;
        r0 = state(r7);
        monitor-enter(r0);
        r1 = r0.state;	 Catch:{ all -> 0x002a }
        switch(r1) {
            case 1: goto L_0x0028;
            case 2: goto L_0x0028;
            default: goto L_0x000a;
        };	 Catch:{ all -> 0x002a }
    L_0x000a:
        r1 = 1;
        r0.state = r1;	 Catch:{ all -> 0x002a }
        monitor-exit(r0);	 Catch:{ all -> 0x002a }
        r2 = r6.timeoutMillis;
        r4 = 0;
        r1 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r1 <= 0) goto L_0x0027;
    L_0x0016:
        r1 = r6.timer;
        r2 = new org.jboss.netty.handler.timeout.ReadTimeoutHandler$ReadTimeoutTask;
        r2.<init>(r7);
        r4 = r6.timeoutMillis;
        r3 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r1 = r1.newTimeout(r2, r4, r3);
        r0.timeout = r1;
    L_0x0027:
        return;
    L_0x0028:
        monitor-exit(r0);	 Catch:{ all -> 0x002a }
        goto L_0x0027;
    L_0x002a:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x002a }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.timeout.ReadTimeoutHandler.initialize(org.jboss.netty.channel.ChannelHandlerContext):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void destroy(org.jboss.netty.channel.ChannelHandlerContext r3) {
        /*
        r0 = state(r3);
        monitor-enter(r0);
        r1 = r0.state;	 Catch:{ all -> 0x001d }
        r2 = 1;
        if (r1 == r2) goto L_0x000c;
    L_0x000a:
        monitor-exit(r0);	 Catch:{ all -> 0x001d }
    L_0x000b:
        return;
    L_0x000c:
        r1 = 2;
        r0.state = r1;	 Catch:{ all -> 0x001d }
        monitor-exit(r0);	 Catch:{ all -> 0x001d }
        r1 = r0.timeout;
        if (r1 == 0) goto L_0x000b;
    L_0x0014:
        r1 = r0.timeout;
        r1.cancel();
        r1 = 0;
        r0.timeout = r1;
        goto L_0x000b;
    L_0x001d:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x001d }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.timeout.ReadTimeoutHandler.destroy(org.jboss.netty.channel.ChannelHandlerContext):void");
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

    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        Channels.fireExceptionCaught(ctx, EXCEPTION);
    }
}
