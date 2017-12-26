package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpConstants;

public class DelimiterBasedFrameDecoder extends FrameDecoder {
    private final ChannelBuffer[] delimiters;
    private boolean discardingTooLongFrame;
    private final boolean failFast;
    private final LineBasedFrameDecoder lineBasedDecoder;
    private final int maxFrameLength;
    private final boolean stripDelimiter;
    private int tooLongFrameLength;

    public DelimiterBasedFrameDecoder(int maxFrameLength, ChannelBuffer delimiter) {
        this(maxFrameLength, true, delimiter);
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, ChannelBuffer delimiter) {
        this(maxFrameLength, stripDelimiter, false, delimiter);
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ChannelBuffer delimiter) {
        this(maxFrameLength, stripDelimiter, failFast, delimiter.slice(delimiter.readerIndex(), delimiter.readableBytes()));
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, ChannelBuffer... delimiters) {
        this(maxFrameLength, true, delimiters);
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, ChannelBuffer... delimiters) {
        this(maxFrameLength, stripDelimiter, false, delimiters);
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ChannelBuffer... delimiters) {
        validateMaxFrameLength(maxFrameLength);
        if (delimiters == null) {
            throw new NullPointerException("delimiters");
        } else if (delimiters.length == 0) {
            throw new IllegalArgumentException("empty delimiters");
        } else {
            if (!isLineBased(delimiters) || isSubclass()) {
                this.delimiters = new ChannelBuffer[delimiters.length];
                for (int i = 0; i < delimiters.length; i++) {
                    ChannelBuffer d = delimiters[i];
                    validateDelimiter(d);
                    this.delimiters[i] = d.slice(d.readerIndex(), d.readableBytes());
                }
                this.lineBasedDecoder = null;
            } else {
                this.lineBasedDecoder = new LineBasedFrameDecoder(maxFrameLength, stripDelimiter, failFast);
                this.delimiters = null;
            }
            this.maxFrameLength = maxFrameLength;
            this.stripDelimiter = stripDelimiter;
            this.failFast = failFast;
        }
    }

    private static boolean isLineBased(ChannelBuffer[] delimiters) {
        boolean z = true;
        if (delimiters.length != 2) {
            return false;
        }
        ChannelBuffer a = delimiters[0];
        ChannelBuffer b = delimiters[1];
        if (a.capacity() < b.capacity()) {
            a = delimiters[1];
            b = delimiters[0];
        }
        if (!(a.capacity() == 2 && b.capacity() == 1 && a.getByte(0) == HttpConstants.CR && a.getByte(1) == (byte) 10 && b.getByte(0) == (byte) 10)) {
            z = false;
        }
        return z;
    }

    private boolean isSubclass() {
        return getClass() != DelimiterBasedFrameDecoder.class;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (this.lineBasedDecoder != null) {
            return this.lineBasedDecoder.decode(ctx, channel, buffer);
        }
        int minFrameLength = Integer.MAX_VALUE;
        ChannelBuffer minDelim = null;
        for (ChannelBuffer delim : this.delimiters) {
            int frameLength = indexOf(buffer, delim);
            if (frameLength >= 0 && frameLength < minFrameLength) {
                minFrameLength = frameLength;
                minDelim = delim;
            }
        }
        if (minDelim != null) {
            int minDelimLength = minDelim.capacity();
            if (this.discardingTooLongFrame) {
                this.discardingTooLongFrame = false;
                buffer.skipBytes(minFrameLength + minDelimLength);
                int tooLongFrameLength = this.tooLongFrameLength;
                this.tooLongFrameLength = 0;
                if (this.failFast) {
                    return null;
                }
                fail(ctx, (long) tooLongFrameLength);
                return null;
            } else if (minFrameLength > this.maxFrameLength) {
                buffer.skipBytes(minFrameLength + minDelimLength);
                fail(ctx, (long) minFrameLength);
                return null;
            } else {
                Object frame;
                if (this.stripDelimiter) {
                    frame = extractFrame(buffer, buffer.readerIndex(), minFrameLength);
                } else {
                    frame = extractFrame(buffer, buffer.readerIndex(), minFrameLength + minDelimLength);
                }
                buffer.skipBytes(minFrameLength + minDelimLength);
                return frame;
            }
        } else if (this.discardingTooLongFrame) {
            this.tooLongFrameLength += buffer.readableBytes();
            buffer.skipBytes(buffer.readableBytes());
            return null;
        } else if (buffer.readableBytes() <= this.maxFrameLength) {
            return null;
        } else {
            this.tooLongFrameLength = buffer.readableBytes();
            buffer.skipBytes(buffer.readableBytes());
            this.discardingTooLongFrame = true;
            if (!this.failFast) {
                return null;
            }
            fail(ctx, (long) this.tooLongFrameLength);
            return null;
        }
    }

    private void fail(ChannelHandlerContext ctx, long frameLength) {
        if (frameLength > 0) {
            Channels.fireExceptionCaught(ctx.getChannel(), new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded"));
        } else {
            Channels.fireExceptionCaught(ctx.getChannel(), new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding"));
        }
    }

    private static int indexOf(ChannelBuffer haystack, ChannelBuffer needle) {
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i++) {
            int haystackIndex = i;
            int needleIndex = 0;
            while (needleIndex < needle.capacity() && haystack.getByte(haystackIndex) == needle.getByte(needleIndex)) {
                haystackIndex++;
                if (haystackIndex == haystack.writerIndex() && needleIndex != needle.capacity() - 1) {
                    return -1;
                }
                needleIndex++;
            }
            if (needleIndex == needle.capacity()) {
                return i - haystack.readerIndex();
            }
        }
        return -1;
    }

    private static void validateDelimiter(ChannelBuffer delimiter) {
        if (delimiter == null) {
            throw new NullPointerException("delimiter");
        } else if (!delimiter.readable()) {
            throw new IllegalArgumentException("empty delimiter");
        }
    }

    private static void validateMaxFrameLength(int maxFrameLength) {
        if (maxFrameLength <= 0) {
            throw new IllegalArgumentException("maxFrameLength must be a positive integer: " + maxFrameLength);
        }
    }
}
