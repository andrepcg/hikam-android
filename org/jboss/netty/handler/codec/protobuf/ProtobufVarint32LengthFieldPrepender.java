package org.jboss.netty.handler.codec.protobuf;

import com.google.protobuf.CodedOutputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@Sharable
public class ProtobufVarint32LengthFieldPrepender extends OneToOneEncoder {
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
        ChannelBuffer body = (ChannelBuffer) msg;
        int length = body.readableBytes();
        CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(new ChannelBufferOutputStream(channel.getConfig().getBufferFactory().getBuffer(body.order(), CodedOutputStream.computeRawVarint32Size(length))));
        codedOutputStream.writeRawVarint32(length);
        codedOutputStream.flush();
        return ChannelBuffers.wrappedBuffer(header, body);
    }
}
