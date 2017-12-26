package org.jboss.netty.handler.codec.spdy;

import java.nio.ByteOrder;
import java.util.Set;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class SpdyFrameEncoder {
    private final int version;

    public SpdyFrameEncoder(SpdyVersion spdyVersion) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        this.version = spdyVersion.getVersion();
    }

    private void writeControlFrameHeader(ChannelBuffer buffer, int type, byte flags, int length) {
        buffer.writeShort(this.version | 32768);
        buffer.writeShort(type);
        buffer.writeByte(flags);
        buffer.writeMedium(length);
    }

    public ChannelBuffer encodeDataFrame(int streamId, boolean last, ChannelBuffer data) {
        byte flags;
        if (last) {
            flags = (byte) 1;
        } else {
            flags = (byte) 0;
        }
        ChannelBuffer header = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 8);
        header.writeInt(Integer.MAX_VALUE & streamId);
        header.writeByte(flags);
        header.writeMedium(data.readableBytes());
        return ChannelBuffers.wrappedBuffer(header, data);
    }

    public ChannelBuffer encodeSynStreamFrame(int streamId, int associatedToStreamId, byte priority, boolean last, boolean unidirectional, ChannelBuffer headerBlock) {
        byte flags;
        if (last) {
            flags = (byte) 1;
        } else {
            flags = (byte) 0;
        }
        if (unidirectional) {
            flags = (byte) (flags | 2);
        }
        int length = headerBlock.readableBytes() + 10;
        ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 18);
        writeControlFrameHeader(frame, 1, flags, length);
        frame.writeInt(streamId);
        frame.writeInt(associatedToStreamId);
        frame.writeShort((priority & 255) << 13);
        return ChannelBuffers.wrappedBuffer(frame, headerBlock);
    }

    public ChannelBuffer encodeSynReplyFrame(int streamId, boolean last, ChannelBuffer headerBlock) {
        byte flags;
        if (last) {
            flags = (byte) 1;
        } else {
            flags = (byte) 0;
        }
        int length = headerBlock.readableBytes() + 4;
        ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 12);
        writeControlFrameHeader(frame, 2, flags, length);
        frame.writeInt(streamId);
        return ChannelBuffers.wrappedBuffer(frame, headerBlock);
    }

    public ChannelBuffer encodeRstStreamFrame(int streamId, int statusCode) {
        ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 16);
        writeControlFrameHeader(frame, 3, (byte) 0, 8);
        frame.writeInt(streamId);
        frame.writeInt(statusCode);
        return frame;
    }

    public ChannelBuffer encodeSettingsFrame(SpdySettingsFrame spdySettingsFrame) {
        Set<Integer> ids = spdySettingsFrame.getIds();
        int numSettings = ids.size();
        byte flags = spdySettingsFrame.clearPreviouslyPersistedSettings() ? (byte) 1 : (byte) 0;
        int length = (numSettings * 8) + 4;
        ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, length + 8);
        writeControlFrameHeader(frame, 4, flags, length);
        frame.writeInt(numSettings);
        for (Integer id : ids) {
            flags = (byte) 0;
            if (spdySettingsFrame.isPersistValue(id.intValue())) {
                flags = (byte) 1;
            }
            if (spdySettingsFrame.isPersisted(id.intValue())) {
                flags = (byte) (flags | 2);
            }
            frame.writeByte(flags);
            frame.writeMedium(id.intValue());
            frame.writeInt(spdySettingsFrame.getValue(id.intValue()));
        }
        return frame;
    }

    public ChannelBuffer encodePingFrame(int id) {
        ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 12);
        writeControlFrameHeader(frame, 6, (byte) 0, 4);
        frame.writeInt(id);
        return frame;
    }

    public ChannelBuffer encodeGoAwayFrame(int lastGoodStreamId, int statusCode) {
        ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 16);
        writeControlFrameHeader(frame, 7, (byte) 0, 8);
        frame.writeInt(lastGoodStreamId);
        frame.writeInt(statusCode);
        return frame;
    }

    public ChannelBuffer encodeHeadersFrame(int streamId, boolean last, ChannelBuffer headerBlock) {
        byte flags;
        if (last) {
            flags = (byte) 1;
        } else {
            flags = (byte) 0;
        }
        int length = headerBlock.readableBytes() + 4;
        ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 12);
        writeControlFrameHeader(frame, 8, flags, length);
        frame.writeInt(streamId);
        return ChannelBuffers.wrappedBuffer(frame, headerBlock);
    }

    public ChannelBuffer encodeWindowUpdateFrame(int streamId, int deltaWindowSize) {
        ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 16);
        writeControlFrameHeader(frame, 9, (byte) 0, 8);
        frame.writeInt(streamId);
        frame.writeInt(deltaWindowSize);
        return frame;
    }
}
