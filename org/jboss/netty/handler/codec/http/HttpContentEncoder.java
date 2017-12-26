package org.jboss.netty.handler.codec.http;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;

public abstract class HttpContentEncoder extends SimpleChannelHandler implements LifeCycleAwareChannelHandler {
    static final /* synthetic */ boolean $assertionsDisabled = (!HttpContentEncoder.class.desiredAssertionStatus());
    private final Queue<String> acceptEncodingQueue = new ConcurrentLinkedQueue();
    private volatile EncoderEmbedder<ChannelBuffer> encoder;
    private volatile boolean offerred;

    protected abstract String getTargetContentEncoding(String str) throws Exception;

    protected abstract EncoderEmbedder<ChannelBuffer> newContentEncoder(HttpMessage httpMessage, String str) throws Exception;

    protected HttpContentEncoder() {
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpMessage msg = e.getMessage();
        if (msg instanceof HttpMessage) {
            String acceptedEncoding = msg.headers().get("Accept-Encoding");
            if (acceptedEncoding == null) {
                acceptedEncoding = "identity";
            }
            boolean offered = this.acceptEncodingQueue.offer(acceptedEncoding);
            if ($assertionsDisabled || offered) {
                ctx.sendUpstream(e);
                return;
            }
            throw new AssertionError();
        }
        ctx.sendUpstream(e);
    }

    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpMessage msg = e.getMessage();
        if ((msg instanceof HttpResponse) && ((HttpResponse) msg).getStatus().getCode() == 100) {
            ctx.sendDownstream(e);
        } else if (msg instanceof HttpMessage) {
            HttpMessage m = msg;
            finishEncode();
            String acceptEncoding = (String) this.acceptEncodingQueue.poll();
            if (acceptEncoding == null) {
                throw new IllegalStateException("cannot send more responses than requests");
            }
            String contentEncoding = m.headers().get("Content-Encoding");
            if (contentEncoding == null || "identity".equalsIgnoreCase(contentEncoding)) {
                boolean hasContent;
                if (m.isChunked() || m.getContent().readable()) {
                    hasContent = true;
                } else {
                    hasContent = false;
                }
                if (hasContent) {
                    EncoderEmbedder newContentEncoder = newContentEncoder(m, acceptEncoding);
                    this.encoder = newContentEncoder;
                    if (newContentEncoder != null) {
                        m.headers().set("Content-Encoding", getTargetContentEncoding(acceptEncoding));
                        if (m.isChunked()) {
                            m.headers().remove("Content-Length");
                        } else {
                            content = ChannelBuffers.wrappedBuffer(encode(m.getContent()), finishEncode());
                            m.setContent(content);
                            if (m.headers().contains("Content-Length")) {
                                m.headers().set("Content-Length", Integer.toString(content.readableBytes()));
                            }
                        }
                    }
                }
                ctx.sendDownstream(e);
                return;
            }
            ctx.sendDownstream(e);
        } else if (msg instanceof HttpChunk) {
            HttpChunk c = (HttpChunk) msg;
            content = c.getContent();
            if (this.encoder == null) {
                ctx.sendDownstream(e);
            } else if (c.isLast()) {
                ChannelBuffer lastProduct = finishEncode();
                if (lastProduct.readable()) {
                    Channels.write(ctx, Channels.succeededFuture(e.getChannel()), new DefaultHttpChunk(lastProduct), e.getRemoteAddress());
                }
                ctx.sendDownstream(e);
            } else {
                content = encode(content);
                if (content.readable()) {
                    c.setContent(content);
                    ctx.sendDownstream(e);
                }
            }
        } else {
            ctx.sendDownstream(e);
        }
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        finishEncode();
        super.channelClosed(ctx, e);
    }

    private ChannelBuffer encode(ChannelBuffer buf) {
        this.offerred = true;
        this.encoder.offer(buf);
        return ChannelBuffers.wrappedBuffer((ChannelBuffer[]) this.encoder.pollAll(new ChannelBuffer[this.encoder.size()]));
    }

    private ChannelBuffer finishEncode() {
        if (this.encoder == null) {
            this.offerred = false;
            return ChannelBuffers.EMPTY_BUFFER;
        }
        ChannelBuffer result;
        if (!this.offerred) {
            this.offerred = false;
            this.encoder.offer(ChannelBuffers.EMPTY_BUFFER);
        }
        if (this.encoder.finish()) {
            result = ChannelBuffers.wrappedBuffer((ChannelBuffer[]) this.encoder.pollAll(new ChannelBuffer[this.encoder.size()]));
        } else {
            result = ChannelBuffers.EMPTY_BUFFER;
        }
        this.encoder = null;
        return result;
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void afterAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
    }

    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
        finishEncode();
    }
}
