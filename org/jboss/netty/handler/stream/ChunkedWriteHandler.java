package org.jboss.netty.handler.stream;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class ChunkedWriteHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler, LifeCycleAwareChannelHandler {
    static final /* synthetic */ boolean $assertionsDisabled = (!ChunkedWriteHandler.class.desiredAssertionStatus());
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
    private volatile ChannelHandlerContext ctx;
    private MessageEvent currentEvent;
    private final AtomicBoolean flush = new AtomicBoolean(false);
    private volatile boolean flushNeeded;
    private final Queue<MessageEvent> queue = new ConcurrentLinkedQueue();

    public void resumeTransfer() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx != null) {
            try {
                flush(ctx, false);
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Unexpected exception while sending chunks.", e);
                }
            }
        }
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {
            boolean offered = this.queue.offer((MessageEvent) e);
            if ($assertionsDisabled || offered) {
                Channel channel = ctx.getChannel();
                if (channel.isWritable() || !channel.isConnected()) {
                    this.ctx = ctx;
                    flush(ctx, false);
                    return;
                }
                return;
            }
            throw new AssertionError();
        }
        ctx.sendDownstream(e);
    }

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent cse = (ChannelStateEvent) e;
            switch (cse.getState()) {
                case INTEREST_OPS:
                    flush(ctx, true);
                    break;
                case OPEN:
                    if (!Boolean.TRUE.equals(cse.getValue())) {
                        flush(ctx, true);
                        break;
                    }
                    break;
            }
        }
        ctx.sendUpstream(e);
    }

    private void discard(ChannelHandlerContext ctx, boolean fireNow) {
        Throwable cause = null;
        while (true) {
            MessageEvent currentEvent = this.currentEvent;
            if (this.currentEvent == null) {
                currentEvent = (MessageEvent) this.queue.poll();
            } else {
                this.currentEvent = null;
            }
            if (currentEvent == null) {
                break;
            }
            Object m = currentEvent.getMessage();
            if (m instanceof ChunkedInput) {
                closeInput((ChunkedInput) m);
            }
            if (cause == null) {
                cause = new ClosedChannelException();
            }
            currentEvent.getFuture().setFailure(cause);
        }
        if (cause == null) {
            return;
        }
        if (fireNow) {
            Channels.fireExceptionCaught(ctx.getChannel(), cause);
        } else {
            Channels.fireExceptionCaughtLater(ctx.getChannel(), cause);
        }
    }

    private void flush(ChannelHandlerContext ctx, boolean fireNow) throws Exception {
        Channel channel = ctx.getChannel();
        boolean suspend = false;
        this.flushNeeded = true;
        boolean acquired = this.flush.compareAndSet(false, true);
        if (acquired) {
            AtomicBoolean atomicBoolean;
            this.flushNeeded = false;
            if (channel.isConnected()) {
                do {
                    final MessageEvent currentEvent;
                    final ChunkedInput chunks;
                    try {
                        if (channel.isWritable()) {
                            if (this.currentEvent == null) {
                                this.currentEvent = (MessageEvent) this.queue.poll();
                            }
                            if (this.currentEvent != null) {
                                if (this.currentEvent.getFuture().isDone()) {
                                    this.currentEvent = null;
                                } else {
                                    currentEvent = this.currentEvent;
                                    Object m = currentEvent.getMessage();
                                    if (m instanceof ChunkedInput) {
                                        chunks = (ChunkedInput) m;
                                        Object chunk = chunks.nextChunk();
                                        boolean endOfInput = chunks.isEndOfInput();
                                        if (chunk == null) {
                                            chunk = ChannelBuffers.EMPTY_BUFFER;
                                            if (endOfInput) {
                                                suspend = false;
                                            } else {
                                                suspend = true;
                                            }
                                        } else {
                                            suspend = false;
                                        }
                                        if (!suspend) {
                                            ChannelFuture writeFuture;
                                            if (endOfInput) {
                                                this.currentEvent = null;
                                                writeFuture = currentEvent.getFuture();
                                                writeFuture.addListener(new ChannelFutureListener() {
                                                    public void operationComplete(ChannelFuture future) throws Exception {
                                                        ChunkedWriteHandler.closeInput(chunks);
                                                    }
                                                });
                                            } else {
                                                writeFuture = Channels.future(channel);
                                                writeFuture.addListener(new ChannelFutureListener() {
                                                    public void operationComplete(ChannelFuture future) throws Exception {
                                                        if (!future.isSuccess()) {
                                                            currentEvent.getFuture().setFailure(future.getCause());
                                                            ChunkedWriteHandler.closeInput((ChunkedInput) currentEvent.getMessage());
                                                        }
                                                    }
                                                });
                                            }
                                            Channels.write(ctx, writeFuture, chunk, currentEvent.getRemoteAddress());
                                        }
                                    } else {
                                        this.currentEvent = null;
                                        ctx.sendDownstream(currentEvent);
                                    }
                                }
                            }
                        }
                    } catch (Throwable th) {
                        this.flush.set(false);
                    }
                    this.flush.set(false);
                } while (channel.isConnected());
                discard(ctx, fireNow);
                atomicBoolean = this.flush;
            } else {
                discard(ctx, fireNow);
                atomicBoolean = this.flush;
            }
            atomicBoolean.set(false);
            return;
        }
        if (!acquired) {
            return;
        }
        if (!channel.isConnected() || (!(!channel.isWritable() || this.queue.isEmpty() || suspend) || this.flushNeeded)) {
            flush(ctx, fireNow);
        }
    }

    static void closeInput(ChunkedInput chunks) {
        try {
            chunks.close();
        } catch (Throwable t) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to close a chunked input.", t);
            }
        }
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void afterAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
        flush(ctx, false);
    }

    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
        Throwable cause = null;
        boolean fireExceptionCaught = false;
        while (true) {
            MessageEvent currentEvent = this.currentEvent;
            if (this.currentEvent == null) {
                currentEvent = (MessageEvent) this.queue.poll();
            } else {
                this.currentEvent = null;
            }
            if (currentEvent == null) {
                break;
            }
            Object m = currentEvent.getMessage();
            if (m instanceof ChunkedInput) {
                closeInput((ChunkedInput) m);
            }
            if (cause == null) {
                cause = new IOException("Unable to flush event, discarding");
            }
            currentEvent.getFuture().setFailure(cause);
            fireExceptionCaught = true;
        }
        if (fireExceptionCaught) {
            Channels.fireExceptionCaughtLater(ctx.getChannel(), cause);
        }
    }
}
