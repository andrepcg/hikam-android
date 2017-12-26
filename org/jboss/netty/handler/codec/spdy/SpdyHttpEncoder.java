package org.jboss.netty.handler.codec.spdy;

import android.support.v4.view.ViewCompat;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.http.protocol.HTTP;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.spdy.SpdyHttpHeaders.Names;

public class SpdyHttpEncoder implements ChannelDownstreamHandler {
    private volatile int currentStreamId;
    private final int spdyVersion;

    private static class SpdyFrameWriter implements ChannelFutureListener {
        private final ChannelHandlerContext ctx;
        private final MessageEvent f693e;

        SpdyFrameWriter(ChannelHandlerContext ctx, MessageEvent e) {
            this.ctx = ctx;
            this.f693e = e;
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                this.ctx.sendDownstream(this.f693e);
            } else if (future.isCancelled()) {
                this.f693e.getFuture().cancel();
            } else {
                this.f693e.getFuture().setFailure(future.getCause());
            }
        }
    }

    public SpdyHttpEncoder(SpdyVersion spdyVersion) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        this.spdyVersion = spdyVersion.getVersion();
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        if (evt instanceof MessageEvent) {
            MessageEvent e = (MessageEvent) evt;
            HttpRequest msg = e.getMessage();
            SpdySynStreamFrame spdySynStreamFrame;
            if (msg instanceof HttpRequest) {
                HttpRequest httpRequest = msg;
                spdySynStreamFrame = createSynStreamFrame(httpRequest);
                this.currentStreamId = spdySynStreamFrame.getStreamId();
                Channels.write(ctx, getMessageFuture(ctx, e, this.currentStreamId, httpRequest), spdySynStreamFrame, e.getRemoteAddress());
                return;
            } else if (msg instanceof HttpResponse) {
                HttpResponse httpResponse = (HttpResponse) msg;
                if (httpResponse.headers().contains(Names.ASSOCIATED_TO_STREAM_ID)) {
                    spdySynStreamFrame = createSynStreamFrame(httpResponse);
                    this.currentStreamId = spdySynStreamFrame.getStreamId();
                    Channels.write(ctx, getMessageFuture(ctx, e, this.currentStreamId, httpResponse), spdySynStreamFrame, e.getRemoteAddress());
                    return;
                }
                SpdySynReplyFrame spdySynReplyFrame = createSynReplyFrame(httpResponse);
                this.currentStreamId = spdySynReplyFrame.getStreamId();
                Channels.write(ctx, getMessageFuture(ctx, e, this.currentStreamId, httpResponse), spdySynReplyFrame, e.getRemoteAddress());
                return;
            } else if (msg instanceof HttpChunk) {
                ChannelHandlerContext channelHandlerContext = ctx;
                writeChunk(channelHandlerContext, e.getFuture(), this.currentStreamId, (HttpChunk) msg, e.getRemoteAddress());
                return;
            } else {
                ctx.sendDownstream(evt);
                return;
            }
        }
        ctx.sendDownstream(evt);
    }

    protected void writeChunk(ChannelHandlerContext ctx, ChannelFuture future, int streamId, HttpChunk chunk, SocketAddress remoteAddress) {
        if (!chunk.isLast()) {
            getDataFuture(ctx, future, createSpdyDataFrames(streamId, chunk.getContent()), remoteAddress).setSuccess();
        } else if (chunk instanceof HttpChunkTrailer) {
            HttpHeaders trailers = ((HttpChunkTrailer) chunk).trailingHeaders();
            if (trailers.isEmpty()) {
                spdyDataFrame = new DefaultSpdyDataFrame(streamId);
                spdyDataFrame.setLast(true);
                Channels.write(ctx, future, spdyDataFrame, remoteAddress);
                return;
            }
            SpdyHeadersFrame spdyHeadersFrame = new DefaultSpdyHeadersFrame(streamId);
            spdyHeadersFrame.setLast(true);
            Iterator i$ = trailers.iterator();
            while (i$.hasNext()) {
                Entry<String, String> entry = (Entry) i$.next();
                spdyHeadersFrame.headers().add((String) entry.getKey(), entry.getValue());
            }
            Channels.write(ctx, future, spdyHeadersFrame, remoteAddress);
        } else {
            spdyDataFrame = new DefaultSpdyDataFrame(streamId);
            spdyDataFrame.setLast(true);
            Channels.write(ctx, future, spdyDataFrame, remoteAddress);
        }
    }

    private ChannelFuture getMessageFuture(ChannelHandlerContext ctx, MessageEvent e, int streamId, HttpMessage httpMessage) {
        if (!httpMessage.getContent().readable()) {
            return e.getFuture();
        }
        SpdyDataFrame[] spdyDataFrames = createSpdyDataFrames(streamId, httpMessage.getContent());
        if (spdyDataFrames.length > 0) {
            spdyDataFrames[spdyDataFrames.length - 1].setLast(true);
        }
        return getDataFuture(ctx, e.getFuture(), spdyDataFrames, e.getRemoteAddress());
    }

    private static ChannelFuture getDataFuture(ChannelHandlerContext ctx, ChannelFuture future, SpdyDataFrame[] spdyDataFrames, SocketAddress remoteAddress) {
        ChannelFuture dataFuture = future;
        int i = spdyDataFrames.length;
        while (true) {
            i--;
            if (i < 0) {
                return dataFuture;
            }
            future = Channels.future(ctx.getChannel());
            future.addListener(new SpdyFrameWriter(ctx, new DownstreamMessageEvent(ctx.getChannel(), dataFuture, spdyDataFrames[i], remoteAddress)));
            dataFuture = future;
        }
    }

    private SpdySynStreamFrame createSynStreamFrame(HttpMessage httpMessage) throws Exception {
        boolean chunked = httpMessage.isChunked();
        int streamId = SpdyHttpHeaders.getStreamId(httpMessage);
        int associatedToStreamId = SpdyHttpHeaders.getAssociatedToStreamId(httpMessage);
        byte priority = SpdyHttpHeaders.getPriority(httpMessage);
        String URL = SpdyHttpHeaders.getUrl(httpMessage);
        String scheme = SpdyHttpHeaders.getScheme(httpMessage);
        SpdyHttpHeaders.removeStreamId(httpMessage);
        SpdyHttpHeaders.removeAssociatedToStreamId(httpMessage);
        SpdyHttpHeaders.removePriority(httpMessage);
        SpdyHttpHeaders.removeUrl(httpMessage);
        SpdyHttpHeaders.removeScheme(httpMessage);
        httpMessage.headers().remove("Connection");
        httpMessage.headers().remove(HTTP.CONN_KEEP_ALIVE);
        httpMessage.headers().remove("Proxy-Connection");
        httpMessage.headers().remove("Transfer-Encoding");
        SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, associatedToStreamId, priority);
        boolean z = (chunked || httpMessage.getContent().readable()) ? false : true;
        spdySynStreamFrame.setLast(z);
        if (httpMessage instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) httpMessage;
            SpdyHeaders.setMethod(this.spdyVersion, spdySynStreamFrame, httpRequest.getMethod());
            SpdyHeaders.setUrl(this.spdyVersion, spdySynStreamFrame, httpRequest.getUri());
            SpdyHeaders.setVersion(this.spdyVersion, spdySynStreamFrame, httpMessage.getProtocolVersion());
        }
        if (httpMessage instanceof HttpResponse) {
            SpdyHeaders.setStatus(this.spdyVersion, spdySynStreamFrame, ((HttpResponse) httpMessage).getStatus());
            SpdyHeaders.setUrl(this.spdyVersion, spdySynStreamFrame, URL);
            SpdyHeaders.setVersion(this.spdyVersion, spdySynStreamFrame, httpMessage.getProtocolVersion());
            spdySynStreamFrame.setUnidirectional(true);
        }
        String host = HttpHeaders.getHost(httpMessage);
        httpMessage.headers().remove("Host");
        SpdyHeaders.setHost(spdySynStreamFrame, host);
        if (scheme == null) {
            scheme = "https";
        }
        SpdyHeaders.setScheme(this.spdyVersion, spdySynStreamFrame, scheme);
        Iterator i$ = httpMessage.headers().iterator();
        while (i$.hasNext()) {
            Entry<String, String> entry = (Entry) i$.next();
            spdySynStreamFrame.headers().add((String) entry.getKey(), entry.getValue());
        }
        return spdySynStreamFrame;
    }

    private SpdySynReplyFrame createSynReplyFrame(HttpResponse httpResponse) throws Exception {
        boolean chunked = httpResponse.isChunked();
        int streamId = SpdyHttpHeaders.getStreamId(httpResponse);
        SpdyHttpHeaders.removeStreamId(httpResponse);
        httpResponse.headers().remove("Connection");
        httpResponse.headers().remove(HTTP.CONN_KEEP_ALIVE);
        httpResponse.headers().remove("Proxy-Connection");
        httpResponse.headers().remove("Transfer-Encoding");
        SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
        boolean z = (chunked || httpResponse.getContent().readable()) ? false : true;
        spdySynReplyFrame.setLast(z);
        SpdyHeaders.setStatus(this.spdyVersion, spdySynReplyFrame, httpResponse.getStatus());
        SpdyHeaders.setVersion(this.spdyVersion, spdySynReplyFrame, httpResponse.getProtocolVersion());
        Iterator i$ = httpResponse.headers().iterator();
        while (i$.hasNext()) {
            Entry<String, String> entry = (Entry) i$.next();
            spdySynReplyFrame.headers().add((String) entry.getKey(), entry.getValue());
        }
        return spdySynReplyFrame;
    }

    private SpdyDataFrame[] createSpdyDataFrames(int streamId, ChannelBuffer content) {
        int readableBytes = content.readableBytes();
        int count = readableBytes / ViewCompat.MEASURED_SIZE_MASK;
        if (readableBytes % ViewCompat.MEASURED_SIZE_MASK > 0) {
            count++;
        }
        SpdyDataFrame[] spdyDataFrames = new SpdyDataFrame[count];
        for (int i = 0; i < count; i++) {
            SpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame(streamId);
            spdyDataFrame.setData(content.readSlice(Math.min(content.readableBytes(), ViewCompat.MEASURED_SIZE_MASK)));
            spdyDataFrames[i] = spdyDataFrame;
        }
        return spdyDataFrames;
    }
}
