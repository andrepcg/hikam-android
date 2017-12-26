package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpConstants;

public class LineBasedFrameDecoder extends FrameDecoder {
    private int discardedBytes;
    private boolean discarding;
    private final boolean failFast;
    private final int maxLength;
    private final boolean stripDelimiter;

    public LineBasedFrameDecoder(int maxLength) {
        this(maxLength, true, false);
    }

    public LineBasedFrameDecoder(int maxLength, boolean stripDelimiter, boolean failFast) {
        this.maxLength = maxLength;
        this.failFast = failFast;
        this.stripDelimiter = stripDelimiter;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        int delimLength = 2;
        Object obj = null;
        int eol = findEndOfLine(buffer);
        int length;
        if (this.discarding) {
            if (eol >= 0) {
                length = (this.discardedBytes + eol) - buffer.readerIndex();
                if (buffer.getByte(eol) != HttpConstants.CR) {
                    delimLength = 1;
                }
                buffer.readerIndex(eol + delimLength);
                this.discardedBytes = 0;
                this.discarding = false;
                if (!this.failFast) {
                    fail(ctx, length);
                }
            } else {
                this.discardedBytes = buffer.readableBytes();
                buffer.readerIndex(buffer.writerIndex());
            }
        } else if (eol >= 0) {
            length = eol - buffer.readerIndex();
            if (buffer.getByte(eol) != HttpConstants.CR) {
                delimLength = 1;
            }
            if (length > this.maxLength) {
                buffer.readerIndex(eol + delimLength);
                fail(ctx, length);
            } else {
                try {
                    if (this.stripDelimiter) {
                        obj = extractFrame(buffer, buffer.readerIndex(), length);
                    } else {
                        obj = extractFrame(buffer, buffer.readerIndex(), length + delimLength);
                    }
                    buffer.skipBytes(length + delimLength);
                } catch (Throwable th) {
                    buffer.skipBytes(length + delimLength);
                }
            }
        } else {
            length = buffer.readableBytes();
            if (length > this.maxLength) {
                this.discardedBytes = length;
                buffer.readerIndex(buffer.writerIndex());
                this.discarding = true;
                if (this.failFast) {
                    fail(ctx, "over " + this.discardedBytes);
                }
            }
        }
        return obj;
    }

    private void fail(ChannelHandlerContext ctx, int length) {
        fail(ctx, String.valueOf(length));
    }

    private void fail(ChannelHandlerContext ctx, String length) {
        Channels.fireExceptionCaught(ctx.getChannel(), new TooLongFrameException("frame length (" + length + ") exceeds the allowed maximum (" + this.maxLength + ')'));
    }

    private static int findEndOfLine(ChannelBuffer buffer) {
        int n = buffer.writerIndex();
        int i = buffer.readerIndex();
        while (i < n) {
            byte b = buffer.getByte(i);
            if (b == (byte) 10) {
                return i;
            }
            if (b == HttpConstants.CR && i < n - 1 && buffer.getByte(i + 1) == (byte) 10) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
