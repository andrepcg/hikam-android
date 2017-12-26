package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class WebSocketFrameAggregator extends OneToOneDecoder {
    private WebSocketFrame currentFrame;
    private final int maxFrameSize;
    private boolean tooLongFrameFound;

    public WebSocketFrameAggregator(int maxFrameSize) {
        if (maxFrameSize < 1) {
            throw new IllegalArgumentException("maxFrameSize must be > 0");
        }
        this.maxFrameSize = maxFrameSize;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object message) throws Exception {
        if (!(message instanceof WebSocketFrame)) {
            return message;
        }
        WebSocketFrame msg = (WebSocketFrame) message;
        if (this.currentFrame == null) {
            this.tooLongFrameFound = false;
            if (msg.isFinalFragment()) {
                return msg;
            }
            ChannelBuffer buf = msg.getBinaryData();
            if (msg instanceof TextWebSocketFrame) {
                this.currentFrame = new TextWebSocketFrame(true, msg.getRsv(), buf);
            } else if (msg instanceof BinaryWebSocketFrame) {
                this.currentFrame = new BinaryWebSocketFrame(true, msg.getRsv(), buf);
            } else {
                throw new IllegalStateException("WebSocket frame was not of type TextWebSocketFrame or BinaryWebSocketFrame");
            }
            return null;
        } else if (!(msg instanceof ContinuationWebSocketFrame)) {
            return msg;
        } else {
            if (this.tooLongFrameFound) {
                if (msg.isFinalFragment()) {
                    this.currentFrame = null;
                }
                return null;
            }
            ChannelBuffer content = this.currentFrame.getBinaryData();
            if (content.readableBytes() > this.maxFrameSize - msg.getBinaryData().readableBytes()) {
                this.tooLongFrameFound = true;
                throw new TooLongFrameException("WebSocketFrame length exceeded " + content + " bytes.");
            }
            this.currentFrame.setBinaryData(ChannelBuffers.wrappedBuffer(content, msg.getBinaryData()));
            if (!msg.isFinalFragment()) {
                return null;
            }
            WebSocketFrame currentFrame = this.currentFrame;
            this.currentFrame = null;
            return currentFrame;
        }
    }
}
