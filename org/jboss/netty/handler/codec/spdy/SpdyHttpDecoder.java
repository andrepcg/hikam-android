package org.jboss.netty.handler.codec.spdy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class SpdyHttpDecoder extends OneToOneDecoder {
    private final int maxContentLength;
    private final Map<Integer, HttpMessage> messageMap;
    private final int spdyVersion;

    public SpdyHttpDecoder(SpdyVersion spdyVersion, int maxContentLength) {
        this(spdyVersion, maxContentLength, new HashMap());
    }

    protected SpdyHttpDecoder(SpdyVersion spdyVersion, int maxContentLength, Map<Integer, HttpMessage> messageMap) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        } else if (maxContentLength <= 0) {
            throw new IllegalArgumentException("maxContentLength must be a positive integer: " + maxContentLength);
        } else {
            this.spdyVersion = spdyVersion.getVersion();
            this.maxContentLength = maxContentLength;
            this.messageMap = messageMap;
        }
    }

    protected HttpMessage putMessage(int streamId, HttpMessage message) {
        return (HttpMessage) this.messageMap.put(Integer.valueOf(streamId), message);
    }

    protected HttpMessage getMessage(int streamId) {
        return (HttpMessage) this.messageMap.get(Integer.valueOf(streamId));
    }

    protected HttpMessage removeMessage(int streamId) {
        return (HttpMessage) this.messageMap.remove(Integer.valueOf(streamId));
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        Object spdySynReplyFrame;
        int streamId;
        if (msg instanceof SpdySynStreamFrame) {
            SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame) msg;
            streamId = spdySynStreamFrame.getStreamId();
            HttpRequest httpRequest;
            if (SpdyCodecUtil.isServerId(streamId)) {
                int associatedToStreamId = spdySynStreamFrame.getAssociatedToStreamId();
                if (associatedToStreamId == 0) {
                    Channels.write(ctx, Channels.future(channel), new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INVALID_STREAM));
                    return null;
                } else if (spdySynStreamFrame.isLast()) {
                    Channels.write(ctx, Channels.future(channel), new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR));
                    return null;
                } else if (spdySynStreamFrame.isTruncated()) {
                    Channels.write(ctx, Channels.future(channel), new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR));
                    return null;
                } else {
                    try {
                        httpRequest = createHttpRequest(this.spdyVersion, spdySynStreamFrame);
                        SpdyHttpHeaders.setStreamId(httpRequest, streamId);
                        SpdyHttpHeaders.setAssociatedToStreamId(httpRequest, associatedToStreamId);
                        SpdyHttpHeaders.setPriority(httpRequest, spdySynStreamFrame.getPriority());
                        return httpRequest;
                    } catch (Exception e) {
                        Channels.write(ctx, Channels.future(channel), new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR));
                    }
                }
            } else if (spdySynStreamFrame.isTruncated()) {
                spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
                spdySynReplyFrame.setLast(true);
                SpdyHeaders.setStatus(this.spdyVersion, spdySynReplyFrame, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
                SpdyHeaders.setVersion(this.spdyVersion, spdySynReplyFrame, HttpVersion.HTTP_1_0);
                Channels.write(ctx, Channels.future(channel), spdySynReplyFrame);
                return null;
            } else {
                try {
                    httpRequest = createHttpRequest(this.spdyVersion, spdySynStreamFrame);
                    SpdyHttpHeaders.setStreamId(httpRequest, streamId);
                    if (spdySynStreamFrame.isLast()) {
                        return httpRequest;
                    }
                    putMessage(streamId, httpRequest);
                } catch (Exception e2) {
                    spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
                    spdySynReplyFrame.setLast(true);
                    SpdyHeaders.setStatus(this.spdyVersion, spdySynReplyFrame, HttpResponseStatus.BAD_REQUEST);
                    SpdyHeaders.setVersion(this.spdyVersion, spdySynReplyFrame, HttpVersion.HTTP_1_0);
                    Channels.write(ctx, Channels.future(channel), spdySynReplyFrame);
                }
            }
        } else if (msg instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame spdySynReplyFrame2 = (SpdySynReplyFrame) msg;
            streamId = spdySynReplyFrame2.getStreamId();
            if (spdySynReplyFrame2.isTruncated()) {
                Channels.write(ctx, Channels.future(channel), new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR));
                return null;
            }
            try {
                HttpResponse httpResponse = createHttpResponse(this.spdyVersion, spdySynReplyFrame2);
                SpdyHttpHeaders.setStreamId(httpResponse, streamId);
                if (spdySynReplyFrame2.isLast()) {
                    HttpHeaders.setContentLength(httpResponse, 0);
                    return httpResponse;
                }
                putMessage(streamId, httpResponse);
            } catch (Exception e3) {
                Channels.write(ctx, Channels.future(channel), new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR));
            }
        } else if (msg instanceof SpdyHeadersFrame) {
            SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame) msg;
            streamId = spdyHeadersFrame.getStreamId();
            httpMessage = getMessage(streamId);
            if (httpMessage == null) {
                if (SpdyCodecUtil.isServerId(streamId)) {
                    if (spdyHeadersFrame.isTruncated()) {
                        Channels.write(ctx, Channels.future(channel), new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR));
                        return null;
                    }
                    try {
                        httpMessage = createHttpResponse(this.spdyVersion, spdyHeadersFrame);
                        SpdyHttpHeaders.setStreamId(httpMessage, streamId);
                        if (spdyHeadersFrame.isLast()) {
                            HttpHeaders.setContentLength(httpMessage, 0);
                            return httpMessage;
                        }
                        putMessage(streamId, httpMessage);
                    } catch (Exception e4) {
                        Channels.write(ctx, Channels.future(channel), new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR));
                        return null;
                    }
                }
                return null;
            }
            if (!spdyHeadersFrame.isTruncated()) {
                Iterator i$ = spdyHeadersFrame.headers().iterator();
                while (i$.hasNext()) {
                    Entry<String, String> e5 = (Entry) i$.next();
                    httpMessage.headers().add((String) e5.getKey(), e5.getValue());
                }
            }
            if (spdyHeadersFrame.isLast()) {
                HttpHeaders.setContentLength(httpMessage, (long) httpMessage.getContent().readableBytes());
                removeMessage(streamId);
                return httpMessage;
            }
        } else if (msg instanceof SpdyDataFrame) {
            SpdyDataFrame spdyDataFrame = (SpdyDataFrame) msg;
            streamId = spdyDataFrame.getStreamId();
            httpMessage = getMessage(streamId);
            if (httpMessage == null) {
                return null;
            }
            ChannelBuffer content = httpMessage.getContent();
            if (content.readableBytes() > this.maxContentLength - spdyDataFrame.getData().readableBytes()) {
                removeMessage(streamId);
                throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
            }
            if (content == ChannelBuffers.EMPTY_BUFFER) {
                content = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
                content.writeBytes(spdyDataFrame.getData());
                httpMessage.setContent(content);
            } else {
                content.writeBytes(spdyDataFrame.getData());
            }
            if (spdyDataFrame.isLast()) {
                HttpHeaders.setContentLength(httpMessage, (long) content.readableBytes());
                removeMessage(streamId);
                return httpMessage;
            }
        } else if (msg instanceof SpdyRstStreamFrame) {
            removeMessage(((SpdyRstStreamFrame) msg).getStreamId());
        }
        return null;
    }

    private static HttpRequest createHttpRequest(int spdyVersion, SpdyHeadersFrame requestFrame) throws Exception {
        HttpMethod method = SpdyHeaders.getMethod(spdyVersion, requestFrame);
        String url = SpdyHeaders.getUrl(spdyVersion, requestFrame);
        HttpVersion httpVersion = SpdyHeaders.getVersion(spdyVersion, requestFrame);
        SpdyHeaders.removeMethod(spdyVersion, requestFrame);
        SpdyHeaders.removeUrl(spdyVersion, requestFrame);
        SpdyHeaders.removeVersion(spdyVersion, requestFrame);
        HttpRequest httpRequest = new DefaultHttpRequest(httpVersion, method, url);
        SpdyHeaders.removeScheme(spdyVersion, requestFrame);
        String host = SpdyHeaders.getHost(requestFrame);
        SpdyHeaders.removeHost(requestFrame);
        HttpHeaders.setHost(httpRequest, host);
        Iterator i$ = requestFrame.headers().iterator();
        while (i$.hasNext()) {
            Entry<String, String> e = (Entry) i$.next();
            httpRequest.headers().add((String) e.getKey(), e.getValue());
        }
        HttpHeaders.setKeepAlive(httpRequest, true);
        httpRequest.headers().remove("Transfer-Encoding");
        return httpRequest;
    }

    private static HttpResponse createHttpResponse(int spdyVersion, SpdyHeadersFrame responseFrame) throws Exception {
        HttpResponseStatus status = SpdyHeaders.getStatus(spdyVersion, responseFrame);
        HttpVersion version = SpdyHeaders.getVersion(spdyVersion, responseFrame);
        SpdyHeaders.removeStatus(spdyVersion, responseFrame);
        SpdyHeaders.removeVersion(spdyVersion, responseFrame);
        HttpResponse httpResponse = new DefaultHttpResponse(version, status);
        Iterator i$ = responseFrame.headers().iterator();
        while (i$.hasNext()) {
            Entry<String, String> e = (Entry) i$.next();
            httpResponse.headers().add((String) e.getKey(), e.getValue());
        }
        HttpHeaders.setKeepAlive(httpResponse, true);
        httpResponse.headers().remove("Transfer-Encoding");
        httpResponse.headers().remove(Names.TRAILER);
        return httpResponse;
    }
}
