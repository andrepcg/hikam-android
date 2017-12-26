package org.jboss.netty.handler.codec.http.websocketx;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.util.internal.StringUtil;

public abstract class WebSocketServerHandshaker {
    public static final ChannelFutureListener HANDSHAKE_LISTENER = new C12351();
    public static final String SUB_PROTOCOL_WILDCARD = "*";
    private final long maxFramePayloadLength;
    private String selectedSubprotocol;
    private final String[] subprotocols;
    private final WebSocketVersion version;
    private final String webSocketUrl;

    static class C12351 implements ChannelFutureListener {
        C12351() {
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                Channels.fireExceptionCaught(future.getChannel(), future.getCause());
            }
        }
    }

    public abstract ChannelFuture close(Channel channel, CloseWebSocketFrame closeWebSocketFrame);

    public abstract ChannelFuture handshake(Channel channel, HttpRequest httpRequest);

    protected WebSocketServerHandshaker(WebSocketVersion version, String webSocketUrl, String subprotocols) {
        this(version, webSocketUrl, subprotocols, Long.MAX_VALUE);
    }

    protected WebSocketServerHandshaker(WebSocketVersion version, String webSocketUrl, String subprotocols, long maxFramePayloadLength) {
        this.version = version;
        this.webSocketUrl = webSocketUrl;
        if (subprotocols != null) {
            String[] subprotocolArray = StringUtil.split(subprotocols, ',');
            for (int i = 0; i < subprotocolArray.length; i++) {
                subprotocolArray[i] = subprotocolArray[i].trim();
            }
            this.subprotocols = subprotocolArray;
        } else {
            this.subprotocols = new String[0];
        }
        this.maxFramePayloadLength = maxFramePayloadLength;
    }

    public String getWebSocketUrl() {
        return this.webSocketUrl;
    }

    public Set<String> getSubprotocols() {
        Set<String> ret = new LinkedHashSet();
        Collections.addAll(ret, this.subprotocols);
        return ret;
    }

    public WebSocketVersion getVersion() {
        return this.version;
    }

    public long getMaxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }

    protected ChannelFuture writeHandshakeResponse(Channel channel, HttpResponse res, ChannelHandler encoder, ChannelHandler decoder) {
        final ChannelPipeline p = channel.getPipeline();
        if (p.get(HttpChunkAggregator.class) != null) {
            p.remove(HttpChunkAggregator.class);
        }
        final String httpEncoderName = p.getContext(HttpResponseEncoder.class).getName();
        p.addAfter(httpEncoderName, "wsencoder", encoder);
        ((HttpRequestDecoder) p.get(HttpRequestDecoder.class)).replace("wsdecoder", decoder);
        ChannelFuture future = channel.write(res);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                p.remove(httpEncoderName);
            }
        });
        return future;
    }

    protected String selectSubprotocol(String requestedSubprotocols) {
        if (requestedSubprotocols == null || this.subprotocols.length == 0) {
            return null;
        }
        for (String p : StringUtil.split(requestedSubprotocols, ',')) {
            String requestedSubprotocol = p.trim();
            for (String supportedSubprotocol : this.subprotocols) {
                if (SUB_PROTOCOL_WILDCARD.equals(supportedSubprotocol) || requestedSubprotocol.equals(supportedSubprotocol)) {
                    return requestedSubprotocol;
                }
            }
        }
        return null;
    }

    public String getSelectedSubprotocol() {
        return this.selectedSubprotocol;
    }

    protected void setSelectedSubprotocol(String value) {
        this.selectedSubprotocol = value;
    }
}
