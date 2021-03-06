package org.jboss.netty.handler.codec.http.websocketx;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.CharsetUtil;

public class WebSocketClientHandshaker13 extends WebSocketClientHandshaker {
    public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker13.class);
    private final boolean allowExtensions;
    private String expectedChallengeResponseString;

    public WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, Map<String, String> customHeaders) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, Long.MAX_VALUE);
    }

    public WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, Map<String, String> customHeaders, long maxFramePayloadLength) {
        super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength);
        this.allowExtensions = allowExtensions;
    }

    public ChannelFuture handshake(Channel channel) throws Exception {
        URI wsURL = getWebSocketUrl();
        String path = wsURL.getPath();
        if (wsURL.getQuery() != null && wsURL.getQuery().length() > 0) {
            path = wsURL.getPath() + '?' + wsURL.getQuery();
        }
        if (path == null || path.length() == 0) {
            path = "/";
        }
        Object key = WebSocketUtil.base64(ChannelBuffers.wrappedBuffer(WebSocketUtil.randomBytes(16)));
        this.expectedChallengeResponseString = WebSocketUtil.base64(WebSocketUtil.sha1(ChannelBuffers.copiedBuffer(key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11", CharsetUtil.US_ASCII)));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("WS Version 13 Client Handshake key: %s. Expected response: %s.", new Object[]{key, this.expectedChallengeResponseString}));
        }
        int wsPort = wsURL.getPort();
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        request.headers().add("Upgrade", Values.WEBSOCKET.toLowerCase());
        request.headers().add("Connection", (Object) "Upgrade");
        request.headers().add(Names.SEC_WEBSOCKET_KEY, key);
        request.headers().add("Host", wsURL.getHost() + ':' + wsPort);
        Object originValue = "http://" + wsURL.getHost();
        if (!(wsPort == 80 || wsPort == 443)) {
            originValue = originValue + ':' + wsPort;
        }
        request.headers().add(Names.ORIGIN, originValue);
        Object expectedSubprotocol = getExpectedSubprotocol();
        if (!(expectedSubprotocol == null || expectedSubprotocol.length() == 0)) {
            request.headers().add(Names.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
        }
        request.headers().add(Names.SEC_WEBSOCKET_VERSION, (Object) "13");
        if (this.customHeaders != null) {
            for (Entry<String, String> e : this.customHeaders.entrySet()) {
                request.headers().add((String) e.getKey(), e.getValue());
            }
        }
        ChannelFuture future = channel.write(request);
        final ChannelFuture handshakeFuture = new DefaultChannelFuture(channel, false);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                future.getChannel().getPipeline().replace(HttpRequestEncoder.class, "ws-encoder", new WebSocket13FrameEncoder(true));
                if (future.isSuccess()) {
                    handshakeFuture.setSuccess();
                } else {
                    handshakeFuture.setFailure(future.getCause());
                }
            }
        });
        return handshakeFuture;
    }

    public void finishHandshake(Channel channel, HttpResponse response) {
        if (response.getStatus().equals(HttpResponseStatus.SWITCHING_PROTOCOLS)) {
            String upgrade = response.headers().get("Upgrade");
            if (upgrade == null || !upgrade.toLowerCase().equals(Values.WEBSOCKET.toLowerCase())) {
                throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + response.headers().get("Upgrade"));
            }
            String connection = response.headers().get("Connection");
            if (connection == null || !connection.toLowerCase().equals("Upgrade".toLowerCase())) {
                throw new WebSocketHandshakeException("Invalid handshake response connection: " + response.headers().get("Connection"));
            }
            String accept = response.headers().get(Names.SEC_WEBSOCKET_ACCEPT);
            if (accept == null || !accept.equals(this.expectedChallengeResponseString)) {
                throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", new Object[]{accept, this.expectedChallengeResponseString}));
            }
            setActualSubprotocol(response.headers().get(Names.SEC_WEBSOCKET_PROTOCOL));
            setHandshakeComplete();
            WebSocketClientHandshaker.replaceDecoder(channel, new WebSocket13FrameDecoder(false, this.allowExtensions, getMaxFramePayloadLength()));
            return;
        }
        throw new WebSocketHandshakeException("Invalid handshake response status: " + response.getStatus());
    }
}
