package org.jboss.netty.handler.codec.http.websocketx;

import android.support.v4.media.TransportMediator;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.PointerIconCompat;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class WebSocket08FrameDecoder extends ReplayingDecoder<State> {
    private static final byte OPCODE_BINARY = (byte) 2;
    private static final byte OPCODE_CLOSE = (byte) 8;
    private static final byte OPCODE_CONT = (byte) 0;
    private static final byte OPCODE_PING = (byte) 9;
    private static final byte OPCODE_PONG = (byte) 10;
    private static final byte OPCODE_TEXT = (byte) 1;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
    private final boolean allowExtensions;
    private int fragmentedFramesCount;
    private boolean frameFinalFlag;
    private int frameOpcode;
    private ChannelBuffer framePayload;
    private int framePayloadBytesRead;
    private long framePayloadLength;
    private int frameRsv;
    private final boolean maskedPayload;
    private ChannelBuffer maskingKey;
    private final long maxFramePayloadLength;
    private boolean receivedClosingHandshake;
    private Utf8Validator utf8Validator;

    public enum State {
        FRAME_START,
        MASKING_KEY,
        PAYLOAD,
        CORRUPT
    }

    public WebSocket08FrameDecoder(boolean maskedPayload, boolean allowExtensions) {
        this(maskedPayload, allowExtensions, Long.MAX_VALUE);
    }

    public WebSocket08FrameDecoder(boolean maskedPayload, boolean allowExtensions, long maxFramePayloadLength) {
        super(State.FRAME_START);
        this.maskedPayload = maskedPayload;
        this.allowExtensions = allowExtensions;
        this.maxFramePayloadLength = maxFramePayloadLength;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {
        if (this.receivedClosingHandshake) {
            buffer.skipBytes(actualReadableBytes());
            return null;
        }
        switch (state) {
            case FRAME_START:
                this.framePayloadBytesRead = 0;
                this.framePayloadLength = -1;
                this.framePayload = null;
                byte b = buffer.readByte();
                this.frameFinalFlag = (b & 128) != 0;
                this.frameRsv = (b & 112) >> 4;
                this.frameOpcode = b & 15;
                if (logger.isDebugEnabled()) {
                    logger.debug("Decoding WebSocket Frame opCode=" + this.frameOpcode);
                }
                b = buffer.readByte();
                boolean frameMasked = (b & 128) != 0;
                int framePayloadLen1 = b & TransportMediator.KEYCODE_MEDIA_PAUSE;
                if (this.frameRsv != 0 && !this.allowExtensions) {
                    protocolViolation(channel, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
                    return null;
                } else if (!this.maskedPayload || frameMasked) {
                    if (this.frameOpcode > 7) {
                        if (!this.frameFinalFlag) {
                            protocolViolation(channel, "fragmented control frame");
                            return null;
                        } else if (framePayloadLen1 > 125) {
                            protocolViolation(channel, "control frame with payload length > 125 octets");
                            return null;
                        } else if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                            protocolViolation(channel, "control frame using reserved opcode " + this.frameOpcode);
                            return null;
                        } else if (this.frameOpcode == 8 && framePayloadLen1 == 1) {
                            protocolViolation(channel, "received close control frame with payload len 1");
                            return null;
                        }
                    } else if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                        protocolViolation(channel, "data frame using reserved opcode " + this.frameOpcode);
                        return null;
                    } else if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                        protocolViolation(channel, "received continuation data frame outside fragmented message");
                        return null;
                    } else if (!(this.fragmentedFramesCount == 0 || this.frameOpcode == 0 || this.frameOpcode == 9)) {
                        protocolViolation(channel, "received non-continuation data frame while inside fragmented message");
                        return null;
                    }
                    if (framePayloadLen1 == TransportMediator.KEYCODE_MEDIA_PLAY) {
                        this.framePayloadLength = (long) buffer.readUnsignedShort();
                        if (this.framePayloadLength < 126) {
                            protocolViolation(channel, "invalid data frame length (not using minimal length encoding)");
                            return null;
                        }
                    } else if (framePayloadLen1 == TransportMediator.KEYCODE_MEDIA_PAUSE) {
                        this.framePayloadLength = buffer.readLong();
                        if (this.framePayloadLength < PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH) {
                            protocolViolation(channel, "invalid data frame length (not using minimal length encoding)");
                            return null;
                        }
                    } else {
                        this.framePayloadLength = (long) framePayloadLen1;
                    }
                    if (this.framePayloadLength <= this.maxFramePayloadLength) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Decoding WebSocket Frame length=" + this.framePayloadLength);
                        }
                        checkpoint(State.MASKING_KEY);
                        break;
                    }
                    protocolViolation(channel, "Max frame length of " + this.maxFramePayloadLength + " has been exceeded.");
                    return null;
                } else {
                    protocolViolation(channel, "unmasked client to server frame");
                    return null;
                }
                break;
            case MASKING_KEY:
                break;
            case PAYLOAD:
                break;
            case CORRUPT:
                buffer.readByte();
                return null;
            default:
                throw new Error("Shouldn't reach here.");
        }
        if (this.maskedPayload) {
            this.maskingKey = buffer.readBytes(4);
        }
        checkpoint(State.PAYLOAD);
        int rbytes = actualReadableBytes();
        ChannelBuffer payloadBuffer = null;
        long willHaveReadByteCount = (long) (this.framePayloadBytesRead + rbytes);
        if (willHaveReadByteCount == this.framePayloadLength) {
            payloadBuffer = buffer.readBytes(rbytes);
        } else if (willHaveReadByteCount < this.framePayloadLength) {
            payloadBuffer = buffer.readBytes(rbytes);
            if (this.framePayload == null) {
                this.framePayload = channel.getConfig().getBufferFactory().getBuffer(toFrameLength(this.framePayloadLength));
            }
            this.framePayload.writeBytes(payloadBuffer);
            this.framePayloadBytesRead += rbytes;
            return null;
        } else if (willHaveReadByteCount > this.framePayloadLength) {
            payloadBuffer = buffer.readBytes(toFrameLength(this.framePayloadLength - ((long) this.framePayloadBytesRead)));
        }
        checkpoint(State.FRAME_START);
        if (this.framePayload == null) {
            this.framePayload = payloadBuffer;
        } else {
            this.framePayload.writeBytes(payloadBuffer);
        }
        if (this.maskedPayload) {
            unmask(this.framePayload);
        }
        if (this.frameOpcode == 9) {
            return new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
        }
        if (this.frameOpcode == 10) {
            return new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
        }
        if (this.frameOpcode == 8) {
            checkCloseFrameBody(channel, this.framePayload);
            this.receivedClosingHandshake = true;
            return new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
        }
        if (!this.frameFinalFlag) {
            if (this.fragmentedFramesCount == 0) {
                if (this.frameOpcode == 1) {
                    checkUTF8String(channel, this.framePayload.array());
                }
            } else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
                checkUTF8String(channel, this.framePayload.array());
            }
            this.fragmentedFramesCount++;
        } else if (this.frameOpcode != 9) {
            this.fragmentedFramesCount = 0;
            if (this.frameOpcode == 1 || (this.utf8Validator != null && this.utf8Validator.isChecking())) {
                checkUTF8String(channel, this.framePayload.array());
                this.utf8Validator.finish();
            }
        }
        if (this.frameOpcode == 1) {
            return new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
        }
        if (this.frameOpcode == 2) {
            return new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
        }
        if (this.frameOpcode == 0) {
            return new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
        }
        throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
    }

    private void unmask(ChannelBuffer frame) {
        byte[] bytes = frame.array();
        for (int i = 0; i < bytes.length; i++) {
            frame.setByte(i, frame.getByte(i) ^ this.maskingKey.getByte(i % 4));
        }
    }

    private void protocolViolation(Channel channel, String reason) throws CorruptedFrameException {
        protocolViolation(channel, new CorruptedFrameException(reason));
    }

    private void protocolViolation(Channel channel, CorruptedFrameException ex) throws CorruptedFrameException {
        checkpoint(State.CORRUPT);
        if (channel.isConnected()) {
            channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
        throw ex;
    }

    private static int toFrameLength(long l) throws TooLongFrameException {
        if (l <= 2147483647L) {
            return (int) l;
        }
        throw new TooLongFrameException("Length:" + l);
    }

    private void checkUTF8String(Channel channel, byte[] bytes) throws CorruptedFrameException {
        try {
            if (this.utf8Validator == null) {
                this.utf8Validator = new Utf8Validator();
            }
            this.utf8Validator.check(bytes);
        } catch (CorruptedFrameException ex) {
            protocolViolation(channel, ex);
        }
    }

    protected void checkCloseFrameBody(Channel channel, ChannelBuffer buffer) throws CorruptedFrameException {
        if (buffer != null && buffer.capacity() != 0) {
            if (buffer.capacity() == 1) {
                protocolViolation(channel, "Invalid close frame body");
            }
            int idx = buffer.readerIndex();
            buffer.readerIndex(0);
            int statusCode = buffer.readShort();
            if ((statusCode >= 0 && statusCode <= 999) || ((statusCode >= 1004 && statusCode <= PointerIconCompat.TYPE_CELL) || (statusCode >= PointerIconCompat.TYPE_NO_DROP && statusCode <= 2999))) {
                protocolViolation(channel, "Invalid close frame status code: " + statusCode);
            }
            if (buffer.readableBytes() > 0) {
                byte[] b = new byte[buffer.readableBytes()];
                buffer.readBytes(b);
                try {
                    new Utf8Validator().check(b);
                } catch (CorruptedFrameException ex) {
                    protocolViolation(channel, ex);
                }
            }
            buffer.readerIndex(idx);
        }
    }
}
