package org.jboss.netty.handler.codec.compression;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneStrictEncoder;

public class JdkZlibEncoder extends OneToOneStrictEncoder implements LifeCycleAwareChannelHandler {
    private static final byte[] gzipHeader = new byte[]{(byte) 31, (byte) -117, (byte) 8, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
    private final CRC32 crc;
    private volatile ChannelHandlerContext ctx;
    private final Deflater deflater;
    private final AtomicBoolean finished;
    private byte[] out;
    private final ZlibWrapper wrapper;
    private boolean writeHeader;

    public JdkZlibEncoder() {
        this(6);
    }

    public JdkZlibEncoder(int compressionLevel) {
        this(ZlibWrapper.ZLIB, compressionLevel);
    }

    public JdkZlibEncoder(ZlibWrapper wrapper) {
        this(wrapper, 6);
    }

    public JdkZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        boolean z = true;
        this.finished = new AtomicBoolean();
        this.writeHeader = true;
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        } else if (wrapper == null) {
            throw new NullPointerException("wrapper");
        } else if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not " + "allowed for compression.");
        } else {
            this.wrapper = wrapper;
            if (wrapper == ZlibWrapper.ZLIB) {
                z = false;
            }
            this.deflater = new Deflater(compressionLevel, z);
            if (wrapper == ZlibWrapper.GZIP) {
                this.crc = new CRC32();
            } else {
                this.crc = null;
            }
        }
    }

    public JdkZlibEncoder(byte[] dictionary) {
        this(6, dictionary);
    }

    public JdkZlibEncoder(int compressionLevel, byte[] dictionary) {
        this.finished = new AtomicBoolean();
        this.writeHeader = true;
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        } else if (dictionary == null) {
            throw new NullPointerException("dictionary");
        } else {
            this.wrapper = ZlibWrapper.ZLIB;
            this.crc = null;
            this.deflater = new Deflater(compressionLevel);
            this.deflater.setDictionary(dictionary);
        }
    }

    public ChannelFuture close() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx != null) {
            return finishEncode(ctx, null);
        }
        throw new IllegalStateException("not added to a pipeline");
    }

    private boolean isGzip() {
        return this.wrapper == ZlibWrapper.GZIP;
    }

    public boolean isClosed() {
        return this.finished.get();
    }

    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer) || this.finished.get()) {
            return msg;
        }
        ChannelBuffer uncompressed = (ChannelBuffer) msg;
        int uncompressedLen = uncompressed.readableBytes();
        if (uncompressedLen == 0) {
            return uncompressed;
        }
        byte[] in = new byte[uncompressedLen];
        uncompressed.readBytes(in);
        ChannelBuffer compressed = ChannelBuffers.dynamicBuffer(estimateCompressedSize(uncompressedLen), channel.getConfig().getBufferFactory());
        synchronized (this.deflater) {
            if (isGzip()) {
                this.crc.update(in);
                if (this.writeHeader) {
                    compressed.writeBytes(gzipHeader);
                    this.writeHeader = false;
                }
            }
            this.deflater.setInput(in);
            while (!this.deflater.needsInput()) {
                deflate(compressed);
            }
        }
        return compressed;
    }

    private int estimateCompressedSize(int originalSize) {
        int sizeEstimate = ((int) Math.ceil(((double) originalSize) * 1.001d)) + 12;
        if (!this.writeHeader) {
            return sizeEstimate;
        }
        switch (this.wrapper) {
            case GZIP:
                return sizeEstimate + gzipHeader.length;
            case ZLIB:
                return sizeEstimate + 2;
            default:
                return sizeEstimate;
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
                        finishEncode(ctx, evt);
                        return;
                    }
            }
        }
        super.handleDownstream(ctx, evt);
    }

    private ChannelFuture finishEncode(final ChannelHandlerContext ctx, final ChannelEvent evt) {
        ChannelFuture future = Channels.succeededFuture(ctx.getChannel());
        if (this.finished.compareAndSet(false, true)) {
            Object footer = ChannelBuffers.dynamicBuffer(ctx.getChannel().getConfig().getBufferFactory());
            boolean gzip = isGzip();
            synchronized (this.deflater) {
                if (gzip) {
                    if (this.writeHeader) {
                        this.writeHeader = false;
                        footer.writeBytes(gzipHeader);
                    }
                }
                this.deflater.finish();
                while (!this.deflater.finished()) {
                    deflate(footer);
                }
                if (gzip) {
                    int crcValue = (int) this.crc.getValue();
                    int uncBytes = this.deflater.getTotalIn();
                    footer.writeByte(crcValue);
                    footer.writeByte(crcValue >>> 8);
                    footer.writeByte(crcValue >>> 16);
                    footer.writeByte(crcValue >>> 24);
                    footer.writeByte(uncBytes);
                    footer.writeByte(uncBytes >>> 8);
                    footer.writeByte(uncBytes >>> 16);
                    footer.writeByte(uncBytes >>> 24);
                }
                this.deflater.end();
            }
            if (footer.readable()) {
                future = Channels.future(ctx.getChannel());
                Channels.write(ctx, future, footer);
            }
            if (evt != null) {
                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        ctx.sendDownstream(evt);
                    }
                });
            }
            return future;
        }
        if (evt != null) {
            ctx.sendDownstream(evt);
        }
        return future;
    }

    private void deflate(ChannelBuffer out) {
        int numBytes;
        if (out.hasArray()) {
            do {
                int writerIndex = out.writerIndex();
                numBytes = this.deflater.deflate(out.array(), out.arrayOffset() + writerIndex, out.writableBytes(), 2);
                out.writerIndex(writerIndex + numBytes);
            } while (numBytes > 0);
            return;
        }
        byte[] tmpOut = this.out;
        if (tmpOut == null) {
            tmpOut = new byte[8192];
            this.out = tmpOut;
        }
        do {
            numBytes = this.deflater.deflate(tmpOut, 0, tmpOut.length, 2);
            out.writeBytes(tmpOut, 0, numBytes);
        } while (numBytes > 0);
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    public void afterAdd(ChannelHandlerContext ctx) throws Exception {
    }

    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
    }

    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
    }
}
