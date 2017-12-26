package org.jboss.netty.handler.codec.serialization;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class CompatibleObjectEncoder extends OneToOneEncoder {
    private final AtomicReference<ChannelBuffer> buffer;
    private volatile ObjectOutputStream oout;
    private final int resetInterval;
    private int writtenObjects;

    public CompatibleObjectEncoder() {
        this(16);
    }

    public CompatibleObjectEncoder(int resetInterval) {
        this.buffer = new AtomicReference();
        if (resetInterval < 0) {
            throw new IllegalArgumentException("resetInterval: " + resetInterval);
        }
        this.resetInterval = resetInterval;
    }

    protected ObjectOutputStream newObjectOutputStream(OutputStream out) throws Exception {
        return new ObjectOutputStream(out);
    }

    protected Object encode(ChannelHandlerContext context, Channel channel, Object msg) throws Exception {
        ChannelBuffer buffer = buffer(context);
        ObjectOutputStream oout = this.oout;
        if (this.resetInterval != 0) {
            this.writtenObjects++;
            if (this.writtenObjects % this.resetInterval == 0) {
                oout.reset();
                buffer.discardReadBytes();
            }
        }
        oout.writeObject(msg);
        oout.flush();
        return buffer.readBytes(buffer.readableBytes());
    }

    private ChannelBuffer buffer(ChannelHandlerContext ctx) throws Exception {
        ChannelBuffer buf = (ChannelBuffer) this.buffer.get();
        if (buf != null) {
            return buf;
        }
        buf = ChannelBuffers.dynamicBuffer(ctx.getChannel().getConfig().getBufferFactory());
        if (!this.buffer.compareAndSet(null, buf)) {
            return (ChannelBuffer) this.buffer.get();
        }
        boolean success = false;
        try {
            this.oout = newObjectOutputStream(new ChannelBufferOutputStream(buf));
            success = true;
            return buf;
        } finally {
            if (!success) {
                this.oout = null;
            }
        }
    }
}
