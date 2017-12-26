package org.jboss.netty.handler.codec.compression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.jboss.netty.util.internal.jzlib.JZlib;
import org.jboss.netty.util.internal.jzlib.ZStream;

public class ZlibDecoder extends OneToOneDecoder {
    private byte[] dictionary;
    private volatile boolean finished;
    private final ZStream f706z;

    public ZlibDecoder() {
        this(ZlibWrapper.ZLIB);
    }

    public ZlibDecoder(ZlibWrapper wrapper) {
        this.f706z = new ZStream();
        if (wrapper == null) {
            throw new NullPointerException("wrapper");
        }
        synchronized (this.f706z) {
            int resultCode = this.f706z.inflateInit(ZlibUtil.convertWrapperType(wrapper));
            if (resultCode != 0) {
                ZlibUtil.fail(this.f706z, "initialization failure", resultCode);
            }
        }
    }

    public ZlibDecoder(byte[] dictionary) {
        this.f706z = new ZStream();
        if (dictionary == null) {
            throw new NullPointerException("dictionary");
        }
        this.dictionary = dictionary;
        synchronized (this.f706z) {
            int resultCode = this.f706z.inflateInit(JZlib.W_ZLIB);
            if (resultCode != 0) {
                ZlibUtil.fail(this.f706z, "initialization failure", resultCode);
            }
        }
    }

    public boolean isClosed() {
        return this.finished;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer) || this.finished) {
            return msg;
        }
        synchronized (this.f706z) {
            ChannelBuffer compressed = (ChannelBuffer) msg;
            byte[] in = new byte[compressed.readableBytes()];
            compressed.readBytes(in);
            this.f706z.next_in = in;
            this.f706z.next_in_index = 0;
            this.f706z.avail_in = in.length;
            byte[] out = new byte[(in.length << 1)];
            Object decompressed = ChannelBuffers.dynamicBuffer(compressed.order(), out.length, ctx.getChannel().getConfig().getBufferFactory());
            this.f706z.next_out = out;
            this.f706z.next_out_index = 0;
            this.f706z.avail_out = out.length;
            while (true) {
                int resultCode = this.f706z.inflate(2);
                if (this.f706z.next_out_index > 0) {
                    decompressed.writeBytes(out, 0, this.f706z.next_out_index);
                    this.f706z.avail_out = out.length;
                }
                this.f706z.next_out_index = 0;
                switch (resultCode) {
                    case -5:
                        try {
                            if (this.f706z.avail_in <= 0) {
                                break;
                            }
                            continue;
                        } catch (Throwable th) {
                            this.f706z.next_in = null;
                            this.f706z.next_out = null;
                        }
                    case 0:
                        break;
                    case 1:
                        this.finished = true;
                        this.f706z.inflateEnd();
                        break;
                    case 2:
                        if (this.dictionary != null) {
                            resultCode = this.f706z.inflateSetDictionary(this.dictionary, this.dictionary.length);
                            if (resultCode == 0) {
                                break;
                            }
                            ZlibUtil.fail(this.f706z, "failed to set the dictionary", resultCode);
                            break;
                        }
                        ZlibUtil.fail(this.f706z, "decompression failure", resultCode);
                        continue;
                    default:
                        ZlibUtil.fail(this.f706z, "decompression failure", resultCode);
                        continue;
                }
                if (decompressed.writerIndex() != 0) {
                    this.f706z.next_in = null;
                    this.f706z.next_out = null;
                    return decompressed;
                }
                this.f706z.next_in = null;
                this.f706z.next_out = null;
                return null;
            }
        }
    }
}
