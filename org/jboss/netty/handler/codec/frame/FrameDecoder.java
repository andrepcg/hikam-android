package org.jboss.netty.handler.codec.frame;

import java.net.SocketAddress;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.CompositeChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public abstract class FrameDecoder extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler {
    static final /* synthetic */ boolean $assertionsDisabled = (!FrameDecoder.class.desiredAssertionStatus());
    public static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
    private int copyThreshold;
    private volatile ChannelHandlerContext ctx;
    protected ChannelBuffer cumulation;
    private int maxCumulationBufferComponents;
    private boolean unfold;

    protected abstract Object decode(ChannelHandlerContext channelHandlerContext, Channel channel, ChannelBuffer channelBuffer) throws Exception;

    protected FrameDecoder() {
        this(false);
    }

    protected FrameDecoder(boolean unfold) {
        this.maxCumulationBufferComponents = 1024;
        this.unfold = unfold;
    }

    public final boolean isUnfold() {
        return this.unfold;
    }

    public final void setUnfold(boolean unfold) {
        if (this.ctx == null) {
            this.unfold = unfold;
            return;
        }
        throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
    }

    public final int getMaxCumulationBufferCapacity() {
        return this.copyThreshold;
    }

    public final void setMaxCumulationBufferCapacity(int copyThreshold) {
        if (copyThreshold < 0) {
            throw new IllegalArgumentException("maxCumulationBufferCapacity must be >= 0");
        } else if (this.ctx == null) {
            this.copyThreshold = copyThreshold;
        } else {
            throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
        }
    }

    public final int getMaxCumulationBufferComponents() {
        return this.maxCumulationBufferComponents;
    }

    public final void setMaxCumulationBufferComponents(int maxCumulationBufferComponents) {
        if (maxCumulationBufferComponents < 2) {
            throw new IllegalArgumentException("maxCumulationBufferComponents: " + maxCumulationBufferComponents + " (expected: >= 2)");
        } else if (this.ctx == null) {
            this.maxCumulationBufferComponents = maxCumulationBufferComponents;
        } else {
            throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
        }
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ChannelBuffer m = e.getMessage();
        if (m instanceof ChannelBuffer) {
            ChannelBuffer input = m;
            if (!input.readable()) {
                return;
            }
            if (this.cumulation == null) {
                try {
                    callDecode(ctx, e.getChannel(), input, e.getRemoteAddress());
                } finally {
                    updateCumulation(ctx, input);
                }
            } else {
                input = appendToCumulation(input);
                try {
                    callDecode(ctx, e.getChannel(), input, e.getRemoteAddress());
                } finally {
                    updateCumulation(ctx, input);
                }
            }
        } else {
            ctx.sendUpstream(e);
        }
    }

    protected ChannelBuffer appendToCumulation(ChannelBuffer input) {
        ChannelBuffer cumulation = this.cumulation;
        if ($assertionsDisabled || cumulation.readable()) {
            if (cumulation instanceof CompositeChannelBuffer) {
                CompositeChannelBuffer composite = (CompositeChannelBuffer) cumulation;
                if (composite.numComponents() >= this.maxCumulationBufferComponents) {
                    cumulation = composite.copy();
                }
            }
            input = ChannelBuffers.wrappedBuffer(cumulation, input);
            this.cumulation = input;
            return input;
        }
        throw new AssertionError();
    }

    protected ChannelBuffer updateCumulation(ChannelHandlerContext ctx, ChannelBuffer input) {
        int readableBytes = input.readableBytes();
        if (readableBytes > 0) {
            int inputCapacity = input.capacity();
            ChannelBuffer newCumulation;
            if (readableBytes < inputCapacity && inputCapacity > this.copyThreshold) {
                newCumulation = newCumulationBuffer(ctx, input.readableBytes());
                this.cumulation = newCumulation;
                this.cumulation.writeBytes(input);
                return newCumulation;
            } else if (input.readerIndex() != 0) {
                newCumulation = input.slice();
                this.cumulation = newCumulation;
                return newCumulation;
            } else {
                newCumulation = input;
                this.cumulation = input;
                return newCumulation;
            }
        }
        this.cumulation = null;
        return null;
    }

    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        cleanup(ctx, e);
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        cleanup(ctx, e);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        ctx.sendUpstream(e);
    }

    protected Object decodeLast(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        return decode(ctx, channel, buffer);
    }

    private void callDecode(ChannelHandlerContext context, Channel channel, ChannelBuffer cumulation, SocketAddress remoteAddress) throws Exception {
        while (cumulation.readable()) {
            int oldReaderIndex = cumulation.readerIndex();
            Object frame = decode(context, channel, cumulation);
            if (frame == null) {
                if (oldReaderIndex == cumulation.readerIndex()) {
                    return;
                }
            } else if (oldReaderIndex == cumulation.readerIndex()) {
                throw new IllegalStateException("decode() method must read at least one byte if it returned a frame (caused by: " + getClass() + ')');
            } else {
                unfoldAndFireMessageReceived(context, remoteAddress, frame);
            }
        }
    }

    protected final void unfoldAndFireMessageReceived(ChannelHandlerContext context, SocketAddress remoteAddress, Object result) {
        if (!this.unfold) {
            Channels.fireMessageReceived(context, result, remoteAddress);
        } else if (result instanceof Object[]) {
            for (Object r : (Object[]) result) {
                Channels.fireMessageReceived(context, r, remoteAddress);
            }
        } else if (result instanceof Iterable) {
            for (Object r2 : (Iterable) result) {
                Channels.fireMessageReceived(context, r2, remoteAddress);
            }
        } else {
            Channels.fireMessageReceived(context, result, remoteAddress);
        }
    }

    protected void cleanup(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        try {
            ChannelBuffer cumulation = this.cumulation;
            if (cumulation != null) {
                this.cumulation = null;
                if (cumulation.readable()) {
                    callDecode(ctx, ctx.getChannel(), cumulation, null);
                }
                Object partialFrame = decodeLast(ctx, ctx.getChannel(), cumulation);
                if (partialFrame != null) {
                    unfoldAndFireMessageReceived(ctx, null, partialFrame);
                }
                ctx.sendUpstream(e);
            }
        } finally {
            ctx.sendUpstream(e);
        }
    }

    protected ChannelBuffer newCumulationBuffer(ChannelHandlerContext ctx, int minimumCapacity) {
        return ctx.getChannel().getConfig().getBufferFactory().getBuffer(Math.max(minimumCapacity, 256));
    }

    public void replace(String handlerName, ChannelHandler handler) {
        if (this.ctx == null) {
            throw new IllegalStateException("Replace cann only be called once the FrameDecoder is added to the ChannelPipeline");
        }
        ChannelPipeline pipeline = this.ctx.getPipeline();
        pipeline.addAfter(this.ctx.getName(), handlerName, handler);
        try {
            if (this.cumulation != null) {
                Channels.fireMessageReceived(this.ctx, this.cumulation.readBytes(actualReadableBytes()));
            }
            pipeline.remove((ChannelHandler) this);
        } catch (Throwable th) {
            pipeline.remove((ChannelHandler) this);
        }
    }

    protected int actualReadableBytes() {
        return internalBuffer().readableBytes();
    }

    protected ChannelBuffer internalBuffer() {
        ChannelBuffer buf = this.cumulation;
        if (buf == null) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        return buf;
    }

    protected ChannelBuffer extractFrame(ChannelBuffer buffer, int index, int length) {
        ChannelBuffer frame = buffer.factory().getBuffer(length);
        frame.writeBytes(buffer, index, length);
        return frame;
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    public void afterAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
    }

    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
    }
}
