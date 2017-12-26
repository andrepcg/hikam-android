package org.jboss.netty.handler.codec.compression;

import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneStrictEncoder;
import org.jboss.netty.util.internal.jzlib.JZlib;
import org.jboss.netty.util.internal.jzlib.ZStream;

public class ZlibEncoder extends OneToOneStrictEncoder implements LifeCycleAwareChannelHandler {
    private static final byte[] EMPTY_ARRAY = new byte[0];
    private volatile ChannelHandlerContext ctx;
    private final AtomicBoolean finished;
    private final int wrapperOverhead;
    private final ZStream f707z;

    public ZlibEncoder() {
        this(6);
    }

    public ZlibEncoder(int compressionLevel) {
        this(ZlibWrapper.ZLIB, compressionLevel);
    }

    public ZlibEncoder(ZlibWrapper wrapper) {
        this(wrapper, 6);
    }

    public ZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        this(wrapper, compressionLevel, 15, 8);
    }

    public ZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
        this.f707z = new ZStream();
        this.finished = new AtomicBoolean();
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        } else if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        } else if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        } else if (wrapper == null) {
            throw new NullPointerException("wrapper");
        } else if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not " + "allowed for compression.");
        } else {
            this.wrapperOverhead = ZlibUtil.wrapperOverhead(wrapper);
            synchronized (this.f707z) {
                int resultCode = this.f707z.deflateInit(compressionLevel, windowBits, memLevel, ZlibUtil.convertWrapperType(wrapper));
                if (resultCode != 0) {
                    ZlibUtil.fail(this.f707z, "initialization failure", resultCode);
                }
            }
        }
    }

    public ZlibEncoder(byte[] dictionary) {
        this(6, dictionary);
    }

    public ZlibEncoder(int compressionLevel, byte[] dictionary) {
        this(compressionLevel, 15, 8, dictionary);
    }

    public ZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
        this.f707z = new ZStream();
        this.finished = new AtomicBoolean();
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        } else if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        } else if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        } else if (dictionary == null) {
            throw new NullPointerException("dictionary");
        } else {
            this.wrapperOverhead = ZlibUtil.wrapperOverhead(ZlibWrapper.ZLIB);
            synchronized (this.f707z) {
                int resultCode = this.f707z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
                if (resultCode != 0) {
                    ZlibUtil.fail(this.f707z, "initialization failure", resultCode);
                } else {
                    resultCode = this.f707z.deflateSetDictionary(dictionary, dictionary.length);
                    if (resultCode != 0) {
                        ZlibUtil.fail(this.f707z, "failed to set the dictionary", resultCode);
                    }
                }
            }
        }
    }

    public ChannelFuture close() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx != null) {
            return finishEncode(ctx, null);
        }
        throw new IllegalStateException("not added to a pipeline");
    }

    public boolean isClosed() {
        return this.finished.get();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected java.lang.Object encode(org.jboss.netty.channel.ChannelHandlerContext r15, org.jboss.netty.channel.Channel r16, java.lang.Object r17) throws java.lang.Exception {
        /*
        r14 = this;
        r0 = r17;
        r8 = r0 instanceof org.jboss.netty.buffer.ChannelBuffer;
        if (r8 == 0) goto L_0x000e;
    L_0x0006:
        r8 = r14.finished;
        r8 = r8.get();
        if (r8 == 0) goto L_0x0011;
    L_0x000e:
        r6 = r17;
    L_0x0010:
        return r6;
    L_0x0011:
        r9 = r14.f707z;
        monitor-enter(r9);
        r0 = r17;
        r0 = (org.jboss.netty.buffer.ChannelBuffer) r0;	 Catch:{ all -> 0x00a1 }
        r6 = r0;
        r7 = r6.readableBytes();	 Catch:{ all -> 0x00a1 }
        if (r7 != 0) goto L_0x002e;
    L_0x001f:
        r8 = r14.f707z;	 Catch:{ all -> 0x002b }
        r10 = 0;
        r8.next_in = r10;	 Catch:{ all -> 0x002b }
        r8 = r14.f707z;	 Catch:{ all -> 0x002b }
        r10 = 0;
        r8.next_out = r10;	 Catch:{ all -> 0x002b }
        monitor-exit(r9);	 Catch:{ all -> 0x002b }
        goto L_0x0010;
    L_0x002b:
        r8 = move-exception;
        monitor-exit(r9);	 Catch:{ all -> 0x002b }
        throw r8;
    L_0x002e:
        r2 = new byte[r7];	 Catch:{ all -> 0x00a1 }
        r6.readBytes(r2);	 Catch:{ all -> 0x00a1 }
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r8.next_in = r2;	 Catch:{ all -> 0x00a1 }
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r10 = 0;
        r8.next_in_index = r10;	 Catch:{ all -> 0x00a1 }
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r8.avail_in = r7;	 Catch:{ all -> 0x00a1 }
        r10 = (double) r7;	 Catch:{ all -> 0x00a1 }
        r12 = 4607186922399644778; // 0x3ff004189374bc6a float:-3.0890025E-27 double:1.001;
        r10 = r10 * r12;
        r10 = java.lang.Math.ceil(r10);	 Catch:{ all -> 0x00a1 }
        r8 = (int) r10;	 Catch:{ all -> 0x00a1 }
        r8 = r8 + 12;
        r10 = r14.wrapperOverhead;	 Catch:{ all -> 0x00a1 }
        r8 = r8 + r10;
        r3 = new byte[r8];	 Catch:{ all -> 0x00a1 }
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r8.next_out = r3;	 Catch:{ all -> 0x00a1 }
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r10 = 0;
        r8.next_out_index = r10;	 Catch:{ all -> 0x00a1 }
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r10 = r3.length;	 Catch:{ all -> 0x00a1 }
        r8.avail_out = r10;	 Catch:{ all -> 0x00a1 }
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r10 = 2;
        r5 = r8.deflate(r10);	 Catch:{ all -> 0x00a1 }
        if (r5 == 0) goto L_0x0071;
    L_0x006a:
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r10 = "compression failure";
        org.jboss.netty.handler.codec.compression.ZlibUtil.fail(r8, r10, r5);	 Catch:{ all -> 0x00a1 }
    L_0x0071:
        r8 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r8 = r8.next_out_index;	 Catch:{ all -> 0x00a1 }
        if (r8 == 0) goto L_0x009e;
    L_0x0077:
        r8 = r15.getChannel();	 Catch:{ all -> 0x00a1 }
        r8 = r8.getConfig();	 Catch:{ all -> 0x00a1 }
        r8 = r8.getBufferFactory();	 Catch:{ all -> 0x00a1 }
        r10 = r6.order();	 Catch:{ all -> 0x00a1 }
        r11 = 0;
        r12 = r14.f707z;	 Catch:{ all -> 0x00a1 }
        r12 = r12.next_out_index;	 Catch:{ all -> 0x00a1 }
        r4 = r8.getBuffer(r10, r3, r11, r12);	 Catch:{ all -> 0x00a1 }
    L_0x0090:
        r8 = r14.f707z;	 Catch:{ all -> 0x002b }
        r10 = 0;
        r8.next_in = r10;	 Catch:{ all -> 0x002b }
        r8 = r14.f707z;	 Catch:{ all -> 0x002b }
        r10 = 0;
        r8.next_out = r10;	 Catch:{ all -> 0x002b }
        monitor-exit(r9);	 Catch:{ all -> 0x002b }
        r6 = r4;
        goto L_0x0010;
    L_0x009e:
        r4 = org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;	 Catch:{ all -> 0x00a1 }
        goto L_0x0090;
    L_0x00a1:
        r8 = move-exception;
        r10 = r14.f707z;	 Catch:{ all -> 0x002b }
        r11 = 0;
        r10.next_in = r11;	 Catch:{ all -> 0x002b }
        r10 = r14.f707z;	 Catch:{ all -> 0x002b }
        r11 = 0;
        r10.next_out = r11;	 Catch:{ all -> 0x002b }
        throw r8;	 Catch:{ all -> 0x002b }
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.codec.compression.ZlibEncoder.encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object):java.lang.Object");
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
        if (this.finished.compareAndSet(false, true)) {
            ChannelFuture future;
            Object footer;
            synchronized (this.f707z) {
                try {
                    this.f707z.next_in = EMPTY_ARRAY;
                    this.f707z.next_in_index = 0;
                    this.f707z.avail_in = 0;
                    byte[] out = new byte[32];
                    this.f707z.next_out = out;
                    this.f707z.next_out_index = 0;
                    this.f707z.avail_out = out.length;
                    int resultCode = this.f707z.deflate(4);
                    if (resultCode != 0 && resultCode != 1) {
                        future = Channels.failedFuture(ctx.getChannel(), ZlibUtil.exception(this.f707z, "compression failure", resultCode));
                        footer = null;
                    } else if (this.f707z.next_out_index != 0) {
                        future = Channels.future(ctx.getChannel());
                        footer = ctx.getChannel().getConfig().getBufferFactory().getBuffer(out, 0, this.f707z.next_out_index);
                    } else {
                        future = Channels.future(ctx.getChannel());
                        footer = ChannelBuffers.EMPTY_BUFFER;
                    }
                    this.f707z.deflateEnd();
                    this.f707z.next_in = null;
                    this.f707z.next_out = null;
                } catch (Throwable th) {
                    this.f707z.deflateEnd();
                    this.f707z.next_in = null;
                    this.f707z.next_out = null;
                }
            }
            if (footer != null) {
                Channels.write(ctx, future, footer);
            }
            if (evt == null) {
                return future;
            }
            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    ctx.sendDownstream(evt);
                }
            });
            return future;
        }
        if (evt != null) {
            ctx.sendDownstream(evt);
        }
        return Channels.succeededFuture(ctx.getChannel());
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
