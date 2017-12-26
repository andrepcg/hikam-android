package org.jboss.netty.handler.codec.http.websocketx;

import android.support.v4.media.TransportMediator;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@Sharable
public class WebSocket00FrameEncoder extends OneToOneEncoder {
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof WebSocketFrame)) {
            return msg;
        }
        WebSocketFrame frame = (WebSocketFrame) msg;
        ChannelBuffer data;
        ChannelBuffer encoded;
        if (frame instanceof TextWebSocketFrame) {
            data = frame.getBinaryData();
            encoded = channel.getConfig().getBufferFactory().getBuffer(data.order(), data.readableBytes() + 2);
            encoded.writeByte(0);
            encoded.writeBytes(data, data.readerIndex(), data.readableBytes());
            encoded.writeByte(-1);
            return encoded;
        } else if (frame instanceof CloseWebSocketFrame) {
            encoded = channel.getConfig().getBufferFactory().getBuffer(frame.getBinaryData().order(), 2);
            encoded.writeByte(-1);
            encoded.writeByte(0);
            return encoded;
        } else {
            data = frame.getBinaryData();
            int dataLen = data.readableBytes();
            encoded = channel.getConfig().getBufferFactory().getBuffer(data.order(), dataLen + 5);
            encoded.writeByte(-128);
            int b1 = (dataLen >>> 28) & TransportMediator.KEYCODE_MEDIA_PAUSE;
            int b2 = (dataLen >>> 14) & TransportMediator.KEYCODE_MEDIA_PAUSE;
            int b3 = (dataLen >>> 7) & TransportMediator.KEYCODE_MEDIA_PAUSE;
            int b4 = dataLen & TransportMediator.KEYCODE_MEDIA_PAUSE;
            if (b1 != 0) {
                encoded.writeByte(b1 | 128);
                encoded.writeByte(b2 | 128);
                encoded.writeByte(b3 | 128);
                encoded.writeByte(b4);
            } else if (b2 != 0) {
                encoded.writeByte(b2 | 128);
                encoded.writeByte(b3 | 128);
                encoded.writeByte(b4);
            } else if (b3 == 0) {
                encoded.writeByte(b4);
            } else {
                encoded.writeByte(b3 | 128);
                encoded.writeByte(b4);
            }
            encoded.writeBytes(data, data.readerIndex(), dataLen);
            return encoded;
        }
    }
}
