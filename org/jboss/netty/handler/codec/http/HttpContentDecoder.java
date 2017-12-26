package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;

public abstract class HttpContentDecoder extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler {
    private DecoderEmbedder<ChannelBuffer> decoder;

    protected abstract DecoderEmbedder<ChannelBuffer> newContentDecoder(String str) throws Exception;

    protected HttpContentDecoder() {
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpMessage msg = e.getMessage();
        if ((msg instanceof HttpResponse) && ((HttpResponse) msg).getStatus().getCode() == 100) {
            ctx.sendUpstream(e);
        } else if (msg instanceof HttpMessage) {
            boolean hasContent;
            HttpMessage m = msg;
            finishDecode();
            String contentEncoding = m.headers().get("Content-Encoding");
            if (contentEncoding != null) {
                contentEncoding = contentEncoding.trim();
            } else {
                contentEncoding = "identity";
            }
            if (m.isChunked() || m.getContent().readable()) {
                hasContent = true;
            } else {
                hasContent = false;
            }
            if (hasContent) {
                DecoderEmbedder newContentDecoder = newContentDecoder(contentEncoding);
                this.decoder = newContentDecoder;
                if (newContentDecoder != null) {
                    Object targetContentEncoding = getTargetContentEncoding(contentEncoding);
                    if ("identity".equals(targetContentEncoding)) {
                        m.headers().remove("Content-Encoding");
                    } else {
                        m.headers().set("Content-Encoding", targetContentEncoding);
                    }
                    if (!m.isChunked()) {
                        content = ChannelBuffers.wrappedBuffer(decode(m.getContent()), finishDecode());
                        m.setContent(content);
                        if (m.headers().contains("Content-Length")) {
                            m.headers().set("Content-Length", Integer.toString(content.readableBytes()));
                        }
                    }
                }
            }
            ctx.sendUpstream(e);
        } else if (msg instanceof HttpChunk) {
            HttpChunk c = (HttpChunk) msg;
            content = c.getContent();
            if (this.decoder == null) {
                ctx.sendUpstream(e);
            } else if (c.isLast()) {
                ChannelBuffer lastProduct = finishDecode();
                if (lastProduct.readable()) {
                    Channels.fireMessageReceived(ctx, new DefaultHttpChunk(lastProduct), e.getRemoteAddress());
                }
                ctx.sendUpstream(e);
            } else {
                content = decode(content);
                if (content.readable()) {
                    c.setContent(content);
                    ctx.sendUpstream(e);
                }
            }
        } else {
            ctx.sendUpstream(e);
        }
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        finishDecode();
        super.channelClosed(ctx, e);
    }

    protected String getTargetContentEncoding(String contentEncoding) throws Exception {
        return "identity";
    }

    private ChannelBuffer decode(ChannelBuffer buf) {
        this.decoder.offer(buf);
        return ChannelBuffers.wrappedBuffer((ChannelBuffer[]) this.decoder.pollAll(new ChannelBuffer[this.decoder.size()]));
    }

    private ChannelBuffer finishDecode() {
        if (this.decoder == null) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        ChannelBuffer result;
        if (this.decoder.finish()) {
            result = ChannelBuffers.wrappedBuffer((ChannelBuffer[]) this.decoder.pollAll(new ChannelBuffer[this.decoder.size()]));
        } else {
            result = ChannelBuffers.EMPTY_BUFFER;
        }
        this.decoder = null;
        return result;
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void afterAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
    }

    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
        finishDecode();
    }
}
