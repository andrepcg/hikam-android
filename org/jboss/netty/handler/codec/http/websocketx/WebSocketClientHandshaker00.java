package org.jboss.netty.handler.codec.http.websocketx;

import android.support.v4.media.TransportMediator;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.netty.buffer.ChannelBuffer;
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

public class WebSocketClientHandshaker00 extends WebSocketClientHandshaker {
    private ChannelBuffer expectedChallengeResponseBytes;

    public WebSocketClientHandshaker00(URI webSocketURL, WebSocketVersion version, String subprotocol, Map<String, String> customHeaders) {
        this(webSocketURL, version, subprotocol, customHeaders, Long.MAX_VALUE);
    }

    public WebSocketClientHandshaker00(URI webSocketURL, WebSocketVersion version, String subprotocol, Map<String, String> customHeaders, long maxFramePayloadLength) {
        super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength);
    }

    public ChannelFuture handshake(Channel channel) {
        int spaces1 = WebSocketUtil.randomNumber(1, 12);
        int spaces2 = WebSocketUtil.randomNumber(1, 12);
        int max2 = Integer.MAX_VALUE / spaces2;
        int number1 = WebSocketUtil.randomNumber(0, Integer.MAX_VALUE / spaces1);
        int number2 = WebSocketUtil.randomNumber(0, max2);
        int product2 = number2 * spaces2;
        String key1 = Integer.toString(number1 * spaces1);
        String key2 = Integer.toString(product2);
        key1 = insertRandomCharacters(key1);
        key2 = insertRandomCharacters(key2);
        Object key12 = insertSpaces(key1, spaces1);
        Object key22 = insertSpaces(key2, spaces2);
        byte[] key3 = WebSocketUtil.randomBytes(8);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(number1);
        Object number1Array = buffer.array();
        buffer = ByteBuffer.allocate(4);
        buffer.putInt(number2);
        Object number2Array = buffer.array();
        byte[] challenge = new byte[16];
        System.arraycopy(number1Array, 0, challenge, 0, 4);
        System.arraycopy(number2Array, 0, challenge, 4, 4);
        System.arraycopy(key3, 0, challenge, 8, 8);
        this.expectedChallengeResponseBytes = WebSocketUtil.md5(ChannelBuffers.wrappedBuffer(challenge));
        URI wsURL = getWebSocketUrl();
        String path = wsURL.getPath();
        if (wsURL.getQuery() != null && wsURL.getQuery().length() > 0) {
            path = wsURL.getPath() + '?' + wsURL.getQuery();
        }
        if (path == null || path.length() == 0) {
            path = "/";
        }
        HttpRequest defaultHttpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        defaultHttpRequest.headers().add("Upgrade", Values.WEBSOCKET);
        defaultHttpRequest.headers().add("Connection", (Object) "Upgrade");
        defaultHttpRequest.headers().add("Host", wsURL.getHost());
        int wsPort = wsURL.getPort();
        String originValue = "http://" + wsURL.getHost();
        if (!(wsPort == 80 || wsPort == 443)) {
            originValue = originValue + ':' + wsPort;
        }
        defaultHttpRequest.headers().add(Names.ORIGIN, (Object) originValue);
        defaultHttpRequest.headers().add(Names.SEC_WEBSOCKET_KEY1, key12);
        defaultHttpRequest.headers().add(Names.SEC_WEBSOCKET_KEY2, key22);
        Object expectedSubprotocol = getExpectedSubprotocol();
        if (!(expectedSubprotocol == null || expectedSubprotocol.length() == 0)) {
            defaultHttpRequest.headers().add(Names.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
        }
        if (this.customHeaders != null) {
            for (Entry<String, String> e : this.customHeaders.entrySet()) {
                defaultHttpRequest.headers().add((String) e.getKey(), e.getValue());
            }
        }
        defaultHttpRequest.headers().set("Content-Length", Integer.valueOf(key3.length));
        defaultHttpRequest.setContent(ChannelBuffers.copiedBuffer(key3));
        final ChannelFuture handshakeFuture = new DefaultChannelFuture(channel, false);
        channel.write(defaultHttpRequest).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                future.getChannel().getPipeline().replace(HttpRequestEncoder.class, "ws-encoder", new WebSocket00FrameEncoder());
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
        if (response.getStatus().equals(new HttpResponseStatus(101, "WebSocket Protocol Handshake"))) {
            String upgrade = response.headers().get("Upgrade");
            if (Values.WEBSOCKET.equals(upgrade)) {
                String connection = response.headers().get("Connection");
                if (!"Upgrade".equals(connection)) {
                    throw new WebSocketHandshakeException("Invalid handshake response connection: " + connection);
                } else if (response.getContent().equals(this.expectedChallengeResponseBytes)) {
                    setActualSubprotocol(response.headers().get(Names.SEC_WEBSOCKET_PROTOCOL));
                    setHandshakeComplete();
                    WebSocketClientHandshaker.replaceDecoder(channel, new WebSocket00FrameDecoder(getMaxFramePayloadLength()));
                    return;
                } else {
                    throw new WebSocketHandshakeException("Invalid challenge");
                }
            }
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
        }
        throw new WebSocketHandshakeException("Invalid handshake response status: " + response.getStatus());
    }

    private static String insertRandomCharacters(String key) {
        int count = WebSocketUtil.randomNumber(1, 12);
        char[] randomChars = new char[count];
        int randCount = 0;
        while (randCount < count) {
            int rand = (int) ((Math.random() * 126.0d) + 33.0d);
            if ((33 < rand && rand < 47) || (58 < rand && rand < TransportMediator.KEYCODE_MEDIA_PLAY)) {
                randomChars[randCount] = (char) rand;
                randCount++;
            }
        }
        for (int i = 0; i < count; i++) {
            int split = WebSocketUtil.randomNumber(0, key.length());
            String part1 = key.substring(0, split);
            key = part1 + randomChars[i] + key.substring(split);
        }
        return key;
    }

    private static String insertSpaces(String key, int spaces) {
        for (int i = 0; i < spaces; i++) {
            int split = WebSocketUtil.randomNumber(1, key.length() - 1);
            String part1 = key.substring(0, split);
            key = part1 + ' ' + key.substring(split);
        }
        return key;
    }
}
