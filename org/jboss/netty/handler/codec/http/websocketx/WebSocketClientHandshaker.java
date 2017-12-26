package org.jboss.netty.handler.codec.http.websocketx;

import java.net.URI;
import java.util.Map;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;

public abstract class WebSocketClientHandshaker {
    private volatile String actualSubprotocol;
    protected final Map<String, String> customHeaders;
    private final String expectedSubprotocol;
    private volatile boolean handshakeComplete;
    private final long maxFramePayloadLength;
    private final WebSocketVersion version;
    private final URI webSocketUrl;

    public abstract void finishHandshake(Channel channel, HttpResponse httpResponse);

    public abstract ChannelFuture handshake(Channel channel) throws Exception;

    protected WebSocketClientHandshaker(URI webSocketUrl, WebSocketVersion version, String subprotocol, Map<String, String> customHeaders) {
        this(webSocketUrl, version, subprotocol, customHeaders, Long.MAX_VALUE);
    }

    protected WebSocketClientHandshaker(URI webSocketUrl, WebSocketVersion version, String subprotocol, Map<String, String> customHeaders, long maxFramePayloadLength) {
        this.webSocketUrl = webSocketUrl;
        this.version = version;
        this.expectedSubprotocol = subprotocol;
        this.customHeaders = customHeaders;
        this.maxFramePayloadLength = maxFramePayloadLength;
    }

    public URI getWebSocketUrl() {
        return this.webSocketUrl;
    }

    public WebSocketVersion getVersion() {
        return this.version;
    }

    public long getMaxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }

    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }

    protected void setHandshakeComplete() {
        this.handshakeComplete = true;
    }

    public String getExpectedSubprotocol() {
        return this.expectedSubprotocol;
    }

    public String getActualSubprotocol() {
        return this.actualSubprotocol;
    }

    protected void setActualSubprotocol(String actualSubprotocol) {
        this.actualSubprotocol = actualSubprotocol;
    }

    static void replaceDecoder(Channel channel, ChannelHandler wsDecoder) {
        ChannelPipeline p = channel.getPipeline();
        ChannelHandlerContext httpDecoderCtx = p.getContext(HttpResponseDecoder.class);
        if (httpDecoderCtx == null) {
            throw new IllegalStateException("can't find an HTTP decoder from the pipeline");
        }
        p.addAfter(httpDecoderCtx.getName(), "ws-decoder", wsDecoder);
        p.remove(httpDecoderCtx.getName());
    }
}
