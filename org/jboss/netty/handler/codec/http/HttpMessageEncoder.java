package org.jboss.netty.handler.codec.http;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map.Entry;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.netty.util.CharsetUtil;

public abstract class HttpMessageEncoder extends OneToOneEncoder {
    private static final byte[] CRLF = new byte[]{HttpConstants.CR, (byte) 10};
    private static final ChannelBuffer LAST_CHUNK = ChannelBuffers.copiedBuffer((CharSequence) "0\r\n\r\n", CharsetUtil.US_ASCII);
    private volatile boolean transferEncodingChunked;

    protected abstract void encodeInitialLine(ChannelBuffer channelBuffer, HttpMessage httpMessage) throws Exception;

    protected HttpMessageEncoder() {
    }

    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof HttpMessage) {
            boolean contentMustBeEmpty;
            HttpMessage m = (HttpMessage) msg;
            if (!m.isChunked()) {
                contentMustBeEmpty = HttpCodecUtil.isTransferEncodingChunked(m);
                this.transferEncodingChunked = contentMustBeEmpty;
            } else if (HttpCodecUtil.isContentLengthSet(m)) {
                contentMustBeEmpty = false;
                this.transferEncodingChunked = false;
                HttpCodecUtil.removeTransferEncodingChunked(m);
            } else {
                if (!HttpCodecUtil.isTransferEncodingChunked(m)) {
                    m.headers().add("Transfer-Encoding", (Object) "chunked");
                }
                contentMustBeEmpty = true;
                this.transferEncodingChunked = true;
            }
            ChannelBuffer header = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
            encodeInitialLine(header, m);
            encodeHeaders(header, m);
            header.writeByte(13);
            header.writeByte(10);
            if (!m.getContent().readable()) {
                return header;
            }
            if (contentMustBeEmpty) {
                throw new IllegalArgumentException("HttpMessage.content must be empty if Transfer-Encoding is chunked.");
            }
            return ChannelBuffers.wrappedBuffer(header, content);
        } else if (!(msg instanceof HttpChunk)) {
            return msg;
        } else {
            HttpChunk chunk = (HttpChunk) msg;
            if (!this.transferEncodingChunked) {
                return chunk.getContent();
            }
            if (chunk.isLast()) {
                this.transferEncodingChunked = false;
                if (!(chunk instanceof HttpChunkTrailer)) {
                    return LAST_CHUNK.duplicate();
                }
                ChannelBuffer trailer = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
                trailer.writeByte(48);
                trailer.writeByte(13);
                trailer.writeByte(10);
                encodeTrailingHeaders(trailer, (HttpChunkTrailer) chunk);
                trailer.writeByte(13);
                trailer.writeByte(10);
                return trailer;
            }
            int contentLength = chunk.getContent().readableBytes();
            return ChannelBuffers.wrappedBuffer(ChannelBuffers.copiedBuffer(Integer.toHexString(contentLength), CharsetUtil.US_ASCII), ChannelBuffers.wrappedBuffer(CRLF), content.slice(content.readerIndex(), contentLength), ChannelBuffers.wrappedBuffer(CRLF));
        }
    }

    private static void encodeHeaders(ChannelBuffer buf, HttpMessage message) {
        try {
            Iterator i$ = message.headers().iterator();
            while (i$.hasNext()) {
                Entry<String, String> h = (Entry) i$.next();
                encodeHeader(buf, (String) h.getKey(), (String) h.getValue());
            }
        } catch (UnsupportedEncodingException e) {
            throw ((Error) new Error().initCause(e));
        }
    }

    private static void encodeTrailingHeaders(ChannelBuffer buf, HttpChunkTrailer trailer) {
        try {
            Iterator i$ = trailer.trailingHeaders().iterator();
            while (i$.hasNext()) {
                Entry<String, String> h = (Entry) i$.next();
                encodeHeader(buf, (String) h.getKey(), (String) h.getValue());
            }
        } catch (UnsupportedEncodingException e) {
            throw ((Error) new Error().initCause(e));
        }
    }

    private static void encodeHeader(ChannelBuffer buf, String header, String value) throws UnsupportedEncodingException {
        encodeAscii(header, buf);
        buf.writeByte(58);
        buf.writeByte(32);
        encodeAscii(value, buf);
        buf.writeByte(13);
        buf.writeByte(10);
    }

    protected static void encodeAscii(String s, ChannelBuffer buf) {
        for (int i = 0; i < s.length(); i++) {
            buf.writeByte(c2b(s.charAt(i)));
        }
    }

    private static byte c2b(char c) {
        if (c > 'ÿ') {
            return (byte) 63;
        }
        return (byte) c;
    }
}
