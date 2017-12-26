package org.jboss.netty.handler.codec.http.websocketx;

import android.support.v4.media.TransportMediator;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class WebSocket08FrameEncoder extends OneToOneEncoder {
    private static final byte OPCODE_BINARY = (byte) 2;
    private static final byte OPCODE_CLOSE = (byte) 8;
    private static final byte OPCODE_CONT = (byte) 0;
    private static final byte OPCODE_PING = (byte) 9;
    private static final byte OPCODE_PONG = (byte) 10;
    private static final byte OPCODE_TEXT = (byte) 1;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameEncoder.class);
    private final boolean maskPayload;

    public WebSocket08FrameEncoder(boolean maskPayload) {
        this.maskPayload = maskPayload;
    }

    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof WebSocketFrame)) {
            return msg;
        }
        byte opcode;
        WebSocketFrame frame = (WebSocketFrame) msg;
        ChannelBuffer data = frame.getBinaryData();
        if (data == null) {
            data = ChannelBuffers.EMPTY_BUFFER;
        }
        if (frame instanceof TextWebSocketFrame) {
            opcode = (byte) 1;
        } else if (frame instanceof PingWebSocketFrame) {
            opcode = (byte) 9;
        } else if (frame instanceof PongWebSocketFrame) {
            opcode = (byte) 10;
        } else if (frame instanceof CloseWebSocketFrame) {
            opcode = OPCODE_CLOSE;
        } else if (frame instanceof BinaryWebSocketFrame) {
            opcode = (byte) 2;
        } else if (frame instanceof ContinuationWebSocketFrame) {
            opcode = (byte) 0;
        } else {
            throw new UnsupportedOperationException("Cannot encode frame of type: " + frame.getClass().getName());
        }
        int length = data.readableBytes();
        if (logger.isDebugEnabled()) {
            logger.debug("Encoding WebSocket Frame opCode=" + opcode + " length=" + length);
        }
        int b0 = 0;
        if (frame.isFinalFragment()) {
            b0 = 0 | 128;
        }
        b0 = (b0 | ((frame.getRsv() % 8) << 4)) | (opcode % 128);
        if (opcode != (byte) 9 || length <= 125) {
            ChannelBuffer header;
            ChannelBuffer body;
            int maskLength = this.maskPayload ? 4 : 0;
            if (length <= 125) {
                int i;
                header = ChannelBuffers.buffer(maskLength + 2);
                header.writeByte(b0);
                if (this.maskPayload) {
                    i = ((byte) length) | 128;
                } else {
                    byte b = (byte) length;
                }
                header.writeByte((byte) i);
            } else if (length <= 65535) {
                header = ChannelBuffers.buffer(maskLength + 4);
                header.writeByte(b0);
                header.writeByte(this.maskPayload ? 254 : TransportMediator.KEYCODE_MEDIA_PLAY);
                header.writeByte((length >>> 8) & 255);
                header.writeByte(length & 255);
            } else {
                header = ChannelBuffers.buffer(maskLength + 10);
                header.writeByte(b0);
                header.writeByte(this.maskPayload ? 255 : TransportMediator.KEYCODE_MEDIA_PAUSE);
                header.writeLong((long) length);
            }
            if (this.maskPayload) {
                byte[] mask = ByteBuffer.allocate(4).putInt(Integer.valueOf((int) (Math.random() * 2.147483647E9d)).intValue()).array();
                header.writeBytes(mask);
                body = ChannelBuffers.buffer(length);
                int counter = 0;
                while (data.readableBytes() > 0) {
                    int counter2 = counter + 1;
                    body.writeByte(mask[counter % 4] ^ data.readByte());
                    counter = counter2;
                }
            } else {
                body = data;
            }
            return ChannelBuffers.wrappedBuffer(header, body);
        }
        throw new TooLongFrameException("invalid payload for PING (payload length must be <= 125, was " + length);
    }
}
