package org.jboss.netty.handler.execution;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.util.ExternalResourceReleasable;

@Sharable
public class ExecutionHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler, ExternalResourceReleasable {
    private final Executor executor;
    private final boolean handleDownstream;
    private final boolean handleUpstream;

    public ExecutionHandler(Executor executor) {
        this(executor, false, true);
    }

    public ExecutionHandler(Executor executor, boolean handleDownstream, boolean handleUpstream) {
        if (executor == null) {
            throw new NullPointerException("executor");
        } else if (handleDownstream || handleUpstream) {
            this.executor = executor;
            this.handleDownstream = handleDownstream;
            this.handleUpstream = handleUpstream;
        } else {
            throw new IllegalArgumentException("You must handle at least handle one event type");
        }
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public void releaseExternalResources() {
        Executor executor = getExecutor();
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdown();
        }
        if (executor instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable) executor).releaseExternalResources();
        }
    }

    public void handleUpstream(ChannelHandlerContext context, ChannelEvent e) throws Exception {
        if (this.handleUpstream) {
            this.executor.execute(new ChannelUpstreamEventRunnable(context, e, this.executor));
        } else {
            context.sendUpstream(e);
        }
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (!handleReadSuspend(ctx, e)) {
            if (this.handleDownstream) {
                this.executor.execute(new ChannelDownstreamEventRunnable(ctx, e, this.executor));
            } else {
                ctx.sendDownstream(e);
            }
        }
    }

    protected boolean handleReadSuspend(ChannelHandlerContext ctx, ChannelEvent e) {
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent cse = (ChannelStateEvent) e;
            if (cse.getState() == ChannelState.INTEREST_OPS && (((Integer) cse.getValue()).intValue() & 1) != 0) {
                boolean readSuspended;
                if (ctx.getAttachment() != null) {
                    readSuspended = true;
                } else {
                    readSuspended = false;
                }
                if (readSuspended) {
                    e.getFuture().setSuccess();
                    return true;
                }
            }
        }
        return false;
    }
}
