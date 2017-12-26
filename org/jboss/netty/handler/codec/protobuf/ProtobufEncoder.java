package org.jboss.netty.handler.codec.protobuf;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@Sharable
public class ProtobufEncoder extends OneToOneEncoder {
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        byte[] array;
        if (msg instanceof MessageLite) {
            array = ((MessageLite) msg).toByteArray();
            return ctx.getChannel().getConfig().getBufferFactory().getBuffer(array, 0, array.length);
        } else if (!(msg instanceof Builder)) {
            return msg;
        } else {
            array = ((Builder) msg).build().toByteArray();
            return ctx.getChannel().getConfig().getBufferFactory().getBuffer(array, 0, array.length);
        }
    }
}
