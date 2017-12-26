package org.jboss.netty.handler.codec.spdy;

import javax.net.ssl.SSLEngine;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;

public abstract class SpdyOrHttpChooser implements ChannelUpstreamHandler {
    private final int maxHttpContentLength;
    private final int maxSpdyContentLength;

    public enum SelectedProtocol {
        SpdyVersion3_1,
        HttpVersion1_1,
        HttpVersion1_0,
        None
    }

    protected abstract ChannelUpstreamHandler createHttpRequestHandlerForHttp();

    protected abstract SelectedProtocol getProtocol(SSLEngine sSLEngine);

    protected SpdyOrHttpChooser(int maxSpdyContentLength, int maxHttpContentLength) {
        this.maxSpdyContentLength = maxSpdyContentLength;
        this.maxHttpContentLength = maxHttpContentLength;
    }

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        SslHandler handler = (SslHandler) ctx.getPipeline().get(SslHandler.class);
        if (handler == null) {
            throw new IllegalStateException("SslHandler is needed for SPDY");
        }
        ChannelPipeline pipeline = ctx.getPipeline();
        switch (getProtocol(handler.getEngine())) {
            case None:
                return;
            case SpdyVersion3_1:
                addSpdyHandlers(ctx, SpdyVersion.SPDY_3_1);
                break;
            case HttpVersion1_0:
            case HttpVersion1_1:
                addHttpHandlers(ctx);
                break;
            default:
                throw new IllegalStateException("Unknown SelectedProtocol");
        }
        pipeline.remove((ChannelHandler) this);
        ctx.sendUpstream(e);
    }

    protected void addSpdyHandlers(ChannelHandlerContext ctx, SpdyVersion version) {
        ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.addLast("spdyFrameCodec", new SpdyFrameCodec(version));
        pipeline.addLast("spdySessionHandler", new SpdySessionHandler(version, true));
        pipeline.addLast("spdyHttpEncoder", new SpdyHttpEncoder(version));
        pipeline.addLast("spdyHttpDecoder", new SpdyHttpDecoder(version, this.maxSpdyContentLength));
        pipeline.addLast("spdyStreamIdHandler", new SpdyHttpResponseStreamIdHandler());
        pipeline.addLast("httpRequestHandler", createHttpRequestHandlerForSpdy());
    }

    protected void addHttpHandlers(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.addLast("httpRequestDecoder", new HttpRequestDecoder());
        pipeline.addLast("httpResponseEncoder", new HttpResponseEncoder());
        pipeline.addLast("httpChunkAggregator", new HttpChunkAggregator(this.maxHttpContentLength));
        pipeline.addLast("httpRequestHandler", createHttpRequestHandlerForHttp());
    }

    protected ChannelUpstreamHandler createHttpRequestHandlerForSpdy() {
        return createHttpRequestHandlerForHttp();
    }
}
