package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class SpdyFrameCodec extends FrameDecoder implements SpdyFrameDecoderDelegate, ChannelDownstreamHandler {
    private static final SpdyProtocolException INVALID_FRAME = new SpdyProtocolException("Received invalid frame");
    private volatile ChannelHandlerContext ctx;
    private final SpdyFrameDecoder spdyFrameDecoder;
    private final SpdyFrameEncoder spdyFrameEncoder;
    private final SpdyHeaderBlockDecoder spdyHeaderBlockDecoder;
    private final SpdyHeaderBlockEncoder spdyHeaderBlockEncoder;
    private SpdyHeadersFrame spdyHeadersFrame;
    private SpdySettingsFrame spdySettingsFrame;

    public SpdyFrameCodec(SpdyVersion version) {
        this(version, 8192, 16384, 6, 15, 8);
    }

    public SpdyFrameCodec(SpdyVersion version, int maxChunkSize, int maxHeaderSize, int compressionLevel, int windowBits, int memLevel) {
        this(version, maxChunkSize, SpdyHeaderBlockDecoder.newInstance(version, maxHeaderSize), SpdyHeaderBlockEncoder.newInstance(version, compressionLevel, windowBits, memLevel));
    }

    protected SpdyFrameCodec(SpdyVersion version, int maxChunkSize, SpdyHeaderBlockDecoder spdyHeaderBlockDecoder, SpdyHeaderBlockEncoder spdyHeaderBlockEncoder) {
        this.spdyFrameDecoder = new SpdyFrameDecoder(version, this, maxChunkSize);
        this.spdyFrameEncoder = new SpdyFrameEncoder(version);
        this.spdyHeaderBlockDecoder = spdyHeaderBlockDecoder;
        this.spdyHeaderBlockEncoder = spdyHeaderBlockEncoder;
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
        super.beforeAdd(ctx);
        this.ctx = ctx;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        this.spdyFrameDecoder.decode(buffer);
        return null;
    }

    protected void cleanup(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        try {
            super.cleanup(ctx, e);
            this.spdyHeaderBlockDecoder.end();
            synchronized (this.spdyHeaderBlockEncoder) {
                this.spdyHeaderBlockEncoder.end();
            }
        } catch (Throwable th) {
            this.spdyHeaderBlockDecoder.end();
            synchronized (this.spdyHeaderBlockEncoder) {
                this.spdyHeaderBlockEncoder.end();
            }
        }
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        if (evt instanceof ChannelStateEvent) {
            ChannelStateEvent e = (ChannelStateEvent) evt;
            switch (e.getState()) {
                case OPEN:
                case CONNECTED:
                case BOUND:
                    if (Boolean.FALSE.equals(e.getValue()) || e.getValue() == null) {
                        synchronized (this.spdyHeaderBlockEncoder) {
                            this.spdyHeaderBlockEncoder.end();
                        }
                        break;
                    }
            }
        }
        if (evt instanceof MessageEvent) {
            MessageEvent e2 = (MessageEvent) evt;
            SpdyDataFrame msg = e2.getMessage();
            ChannelHandlerContext channelHandlerContext;
            if (msg instanceof SpdyDataFrame) {
                SpdyDataFrame spdyDataFrame = msg;
                channelHandlerContext = ctx;
                Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodeDataFrame(spdyDataFrame.getStreamId(), spdyDataFrame.isLast(), spdyDataFrame.getData()), e2.getRemoteAddress());
                return;
            } else if (msg instanceof SpdySynStreamFrame) {
                synchronized (this.spdyHeaderBlockEncoder) {
                    SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame) msg;
                    channelHandlerContext = ctx;
                    Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodeSynStreamFrame(spdySynStreamFrame.getStreamId(), spdySynStreamFrame.getAssociatedToStreamId(), spdySynStreamFrame.getPriority(), spdySynStreamFrame.isLast(), spdySynStreamFrame.isUnidirectional(), this.spdyHeaderBlockEncoder.encode(spdySynStreamFrame)), e2.getRemoteAddress());
                }
                return;
            } else if (msg instanceof SpdySynReplyFrame) {
                synchronized (this.spdyHeaderBlockEncoder) {
                    SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame) msg;
                    channelHandlerContext = ctx;
                    Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodeSynReplyFrame(spdySynReplyFrame.getStreamId(), spdySynReplyFrame.isLast(), this.spdyHeaderBlockEncoder.encode(spdySynReplyFrame)), e2.getRemoteAddress());
                }
                return;
            } else if (msg instanceof SpdyRstStreamFrame) {
                SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame) msg;
                channelHandlerContext = ctx;
                Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodeRstStreamFrame(spdyRstStreamFrame.getStreamId(), spdyRstStreamFrame.getStatus().getCode()), e2.getRemoteAddress());
                return;
            } else if (msg instanceof SpdySettingsFrame) {
                channelHandlerContext = ctx;
                Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodeSettingsFrame((SpdySettingsFrame) msg), e2.getRemoteAddress());
                return;
            } else if (msg instanceof SpdyPingFrame) {
                channelHandlerContext = ctx;
                Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodePingFrame(((SpdyPingFrame) msg).getId()), e2.getRemoteAddress());
                return;
            } else if (msg instanceof SpdyGoAwayFrame) {
                SpdyGoAwayFrame spdyGoAwayFrame = (SpdyGoAwayFrame) msg;
                channelHandlerContext = ctx;
                Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodeGoAwayFrame(spdyGoAwayFrame.getLastGoodStreamId(), spdyGoAwayFrame.getStatus().getCode()), e2.getRemoteAddress());
                return;
            } else if (msg instanceof SpdyHeadersFrame) {
                synchronized (this.spdyHeaderBlockEncoder) {
                    SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame) msg;
                    channelHandlerContext = ctx;
                    Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodeHeadersFrame(spdyHeadersFrame.getStreamId(), spdyHeadersFrame.isLast(), this.spdyHeaderBlockEncoder.encode(spdyHeadersFrame)), e2.getRemoteAddress());
                }
                return;
            } else if (msg instanceof SpdyWindowUpdateFrame) {
                SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame) msg;
                channelHandlerContext = ctx;
                Channels.write(channelHandlerContext, e2.getFuture(), this.spdyFrameEncoder.encodeWindowUpdateFrame(spdyWindowUpdateFrame.getStreamId(), spdyWindowUpdateFrame.getDeltaWindowSize()), e2.getRemoteAddress());
                return;
            } else {
                ctx.sendDownstream(evt);
                return;
            }
        }
        ctx.sendDownstream(evt);
    }

    public void readDataFrame(int streamId, boolean last, ChannelBuffer data) {
        Object spdyDataFrame = new DefaultSpdyDataFrame(streamId);
        spdyDataFrame.setLast(last);
        spdyDataFrame.setData(data);
        Channels.fireMessageReceived(this.ctx, spdyDataFrame);
    }

    public void readSynStreamFrame(int streamId, int associatedToStreamId, byte priority, boolean last, boolean unidirectional) {
        SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, associatedToStreamId, priority);
        spdySynStreamFrame.setLast(last);
        spdySynStreamFrame.setUnidirectional(unidirectional);
        this.spdyHeadersFrame = spdySynStreamFrame;
    }

    public void readSynReplyFrame(int streamId, boolean last) {
        SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
        spdySynReplyFrame.setLast(last);
        this.spdyHeadersFrame = spdySynReplyFrame;
    }

    public void readRstStreamFrame(int streamId, int statusCode) {
        Channels.fireMessageReceived(this.ctx, new DefaultSpdyRstStreamFrame(streamId, statusCode));
    }

    public void readSettingsFrame(boolean clearPersisted) {
        this.spdySettingsFrame = new DefaultSpdySettingsFrame();
        this.spdySettingsFrame.setClearPreviouslyPersistedSettings(clearPersisted);
    }

    public void readSetting(int id, int value, boolean persistValue, boolean persisted) {
        this.spdySettingsFrame.setValue(id, value, persistValue, persisted);
    }

    public void readSettingsEnd() {
        Object frame = this.spdySettingsFrame;
        this.spdySettingsFrame = null;
        Channels.fireMessageReceived(this.ctx, frame);
    }

    public void readPingFrame(int id) {
        Channels.fireMessageReceived(this.ctx, new DefaultSpdyPingFrame(id));
    }

    public void readGoAwayFrame(int lastGoodStreamId, int statusCode) {
        Channels.fireMessageReceived(this.ctx, new DefaultSpdyGoAwayFrame(lastGoodStreamId, statusCode));
    }

    public void readHeadersFrame(int streamId, boolean last) {
        this.spdyHeadersFrame = new DefaultSpdyHeadersFrame(streamId);
        this.spdyHeadersFrame.setLast(last);
    }

    public void readWindowUpdateFrame(int streamId, int deltaWindowSize) {
        Channels.fireMessageReceived(this.ctx, new DefaultSpdyWindowUpdateFrame(streamId, deltaWindowSize));
    }

    public void readHeaderBlock(ChannelBuffer headerBlock) {
        try {
            this.spdyHeaderBlockDecoder.decode(headerBlock, this.spdyHeadersFrame);
        } catch (Throwable e) {
            Channels.fireExceptionCaught(this.ctx, e);
        }
    }

    public void readHeaderBlockEnd() {
        Object obj = null;
        try {
            this.spdyHeaderBlockDecoder.endHeaderBlock(this.spdyHeadersFrame);
            obj = this.spdyHeadersFrame;
            this.spdyHeadersFrame = null;
        } catch (Throwable e) {
            Channels.fireExceptionCaught(this.ctx, e);
        }
        if (obj != null) {
            Channels.fireMessageReceived(this.ctx, obj);
        }
    }

    public void readFrameError(String message) {
        Channels.fireExceptionCaught(this.ctx, INVALID_FRAME);
    }
}
