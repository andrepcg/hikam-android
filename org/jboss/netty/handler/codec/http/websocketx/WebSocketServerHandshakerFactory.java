package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class WebSocketServerHandshakerFactory {
    private final boolean allowExtensions;
    private final long maxFramePayloadLength;
    private final String subprotocols;
    private final String webSocketURL;

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions) {
        this(webSocketURL, subprotocols, allowExtensions, Long.MAX_VALUE);
    }

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, long maxFramePayloadLength) {
        this.webSocketURL = webSocketURL;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        this.maxFramePayloadLength = maxFramePayloadLength;
    }

    public WebSocketServerHandshaker newHandshaker(HttpRequest req) {
        String version = req.headers().get(Names.SEC_WEBSOCKET_VERSION);
        if (version == null) {
            return new WebSocketServerHandshaker00(this.webSocketURL, this.subprotocols, this.maxFramePayloadLength);
        }
        if (version.equals(WebSocketVersion.V13.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker13(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength);
        }
        if (version.equals(WebSocketVersion.V08.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker08(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength);
        }
        if (version.equals(WebSocketVersion.V07.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker07(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength);
        }
        return null;
    }

    public ChannelFuture sendUnsupportedWebSocketVersionResponse(Channel channel) {
        HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS);
        res.setStatus(HttpResponseStatus.UPGRADE_REQUIRED);
        res.headers().set(Names.SEC_WEBSOCKET_VERSION, WebSocketVersion.V13.toHttpHeaderValue());
        return channel.write(res);
    }
}
