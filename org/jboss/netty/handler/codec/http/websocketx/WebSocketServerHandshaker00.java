package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class WebSocketServerHandshaker00 extends WebSocketServerHandshaker {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker00.class);

    public WebSocketServerHandshaker00(String webSocketURL, String subprotocols) {
        this(webSocketURL, subprotocols, Long.MAX_VALUE);
    }

    public WebSocketServerHandshaker00(String webSocketURL, String subprotocols, long maxFramePayloadLength) {
        super(WebSocketVersion.V00, webSocketURL, subprotocols, maxFramePayloadLength);
    }

    public ChannelFuture handshake(Channel channel, HttpRequest req) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Channel %s WS Version 00 server handshake", new Object[]{channel.getId()}));
        }
        if ("Upgrade".equalsIgnoreCase(req.headers().get("Connection")) && Values.WEBSOCKET.equalsIgnoreCase(req.headers().get("Upgrade"))) {
            boolean isHixie76 = req.headers().contains(Names.SEC_WEBSOCKET_KEY1) && req.headers().contains(Names.SEC_WEBSOCKET_KEY2);
            HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, isHixie76 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"));
            res.headers().add("Upgrade", Values.WEBSOCKET);
            res.headers().add("Connection", (Object) "Upgrade");
            if (isHixie76) {
                res.headers().add(Names.SEC_WEBSOCKET_ORIGIN, req.headers().get(Names.ORIGIN));
                res.headers().add(Names.SEC_WEBSOCKET_LOCATION, getWebSocketUrl());
                String subprotocols = req.headers().get(Names.SEC_WEBSOCKET_PROTOCOL);
                if (subprotocols != null) {
                    Object selectedSubprotocol = selectSubprotocol(subprotocols);
                    if (selectedSubprotocol == null) {
                        throw new WebSocketHandshakeException("Requested subprotocol(s) not supported: " + subprotocols);
                    }
                    res.headers().add(Names.SEC_WEBSOCKET_PROTOCOL, selectedSubprotocol);
                    setSelectedSubprotocol(selectedSubprotocol);
                }
                String key1 = req.headers().get(Names.SEC_WEBSOCKET_KEY1);
                String key2 = req.headers().get(Names.SEC_WEBSOCKET_KEY2);
                int a = (int) (Long.parseLong(key1.replaceAll("[^0-9]", "")) / ((long) key1.replaceAll("[^ ]", "").length()));
                int b = (int) (Long.parseLong(key2.replaceAll("[^0-9]", "")) / ((long) key2.replaceAll("[^ ]", "").length()));
                long c = req.getContent().readLong();
                ChannelBuffer input = ChannelBuffers.buffer(16);
                input.writeInt(a);
                input.writeInt(b);
                input.writeLong(c);
                res.setContent(WebSocketUtil.md5(input));
            } else {
                res.headers().add(Names.WEBSOCKET_ORIGIN, req.headers().get(Names.ORIGIN));
                res.headers().add(Names.WEBSOCKET_LOCATION, getWebSocketUrl());
                String protocol = req.headers().get(Names.WEBSOCKET_PROTOCOL);
                if (protocol != null) {
                    res.headers().add(Names.WEBSOCKET_PROTOCOL, selectSubprotocol(protocol));
                }
            }
            return writeHandshakeResponse(channel, res, new WebSocket00FrameEncoder(), new WebSocket00FrameDecoder(getMaxFramePayloadLength()));
        }
        throw new WebSocketHandshakeException("not a WebSocket handshake request: missing upgrade");
    }

    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
        return channel.write(frame);
    }
}
