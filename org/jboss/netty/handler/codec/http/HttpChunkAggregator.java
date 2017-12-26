package org.jboss.netty.handler.codec.http;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.CompositeChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.util.CharsetUtil;

public class HttpChunkAggregator extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler {
    private static final ChannelBuffer CONTINUE = ChannelBuffers.copiedBuffer((CharSequence) "HTTP/1.1 100 Continue\r\n\r\n", CharsetUtil.US_ASCII);
    public static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
    private ChannelHandlerContext ctx;
    private HttpMessage currentMessage;
    private final int maxContentLength;
    private int maxCumulationBufferComponents = 1024;
    private boolean tooLongFrameFound;

    public HttpChunkAggregator(int maxContentLength) {
        if (maxContentLength <= 0) {
            throw new IllegalArgumentException("maxContentLength must be a positive integer: " + maxContentLength);
        }
        this.maxContentLength = maxContentLength;
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
        HttpMessage msg = e.getMessage();
        Object currentMessage = this.currentMessage;
        if (msg instanceof HttpMessage) {
            HttpMessage m = msg;
            this.tooLongFrameFound = false;
            if (HttpHeaders.is100ContinueExpected(m)) {
                Channels.write(ctx, Channels.succeededFuture(ctx.getChannel()), CONTINUE.duplicate());
            }
            if (m.isChunked()) {
                HttpCodecUtil.removeTransferEncodingChunked(m);
                m.setChunked(false);
                this.currentMessage = m;
                return;
            }
            this.currentMessage = null;
            ctx.sendUpstream(e);
        } else if (!(msg instanceof HttpChunk)) {
            ctx.sendUpstream(e);
        } else if (currentMessage == null) {
            throw new IllegalStateException("received " + HttpChunk.class.getSimpleName() + " without " + HttpMessage.class.getSimpleName());
        } else {
            HttpChunk chunk = (HttpChunk) msg;
            if (!this.tooLongFrameFound) {
                ChannelBuffer content = currentMessage.getContent();
                if (content.readableBytes() > this.maxContentLength - chunk.getContent().readableBytes()) {
                    this.tooLongFrameFound = true;
                    throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
                }
                appendToCumulation(chunk.getContent());
                if (chunk.isLast()) {
                    this.currentMessage = null;
                    if (chunk instanceof HttpChunkTrailer) {
                        Iterator i$ = ((HttpChunkTrailer) chunk).trailingHeaders().iterator();
                        while (i$.hasNext()) {
                            Entry<String, String> header = (Entry) i$.next();
                            currentMessage.headers().set((String) header.getKey(), header.getValue());
                        }
                    }
                    currentMessage.headers().set("Content-Length", String.valueOf(content.readableBytes()));
                    Channels.fireMessageReceived(ctx, currentMessage, e.getRemoteAddress());
                }
            } else if (chunk.isLast()) {
                this.currentMessage = null;
            }
        }
    }

    protected void appendToCumulation(ChannelBuffer input) {
        ChannelBuffer cumulation = this.currentMessage.getContent();
        if (cumulation instanceof CompositeChannelBuffer) {
            CompositeChannelBuffer composite = (CompositeChannelBuffer) cumulation;
            if (composite.numComponents() >= this.maxCumulationBufferComponents) {
                this.currentMessage.setContent(ChannelBuffers.wrappedBuffer(composite.copy(), input));
                return;
            }
            List<ChannelBuffer> decomposed = composite.decompose(0, composite.readableBytes());
            ChannelBuffer[] buffers = (ChannelBuffer[]) decomposed.toArray(new ChannelBuffer[(decomposed.size() + 1)]);
            buffers[buffers.length - 1] = input;
            this.currentMessage.setContent(ChannelBuffers.wrappedBuffer(buffers));
            return;
        }
        this.currentMessage.setContent(ChannelBuffers.wrappedBuffer(cumulation, input));
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
