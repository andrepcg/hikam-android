package org.jboss.netty.handler.execution;

import java.util.concurrent.Executor;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.util.EstimatableObjectWrapper;

public abstract class ChannelEventRunnable implements Runnable, EstimatableObjectWrapper {
    protected static final ThreadLocal<Executor> PARENT = new ThreadLocal();
    protected final ChannelHandlerContext ctx;
    protected final ChannelEvent f695e;
    int estimatedSize;
    private final Executor executor;

    protected abstract void doRun();

    protected ChannelEventRunnable(ChannelHandlerContext ctx, ChannelEvent e, Executor executor) {
        this.ctx = ctx;
        this.f695e = e;
        this.executor = executor;
    }

    public ChannelHandlerContext getContext() {
        return this.ctx;
    }

    public ChannelEvent getEvent() {
        return this.f695e;
    }

    public Object unwrap() {
        return this.f695e;
    }

    public final void run() {
        doRun();
    }
}
