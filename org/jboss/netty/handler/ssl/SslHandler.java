package org.jboss.netty.handler.ssl;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.util.internal.DetectionUtil;
import org.jboss.netty.util.internal.NonReentrantLock;

public class SslHandler extends FrameDecoder implements ChannelDownstreamHandler {
    static final /* synthetic */ boolean $assertionsDisabled = (!SslHandler.class.desiredAssertionStatus());
    private static final AtomicIntegerFieldUpdater<SslHandler> CLOSED_OUTBOUND_AND_CHANNEL_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SslHandler.class, "closedOutboundAndChannel");
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
    private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
    private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
    private static final AtomicIntegerFieldUpdater<SslHandler> SENT_CLOSE_NOTIFY_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SslHandler.class, "sentCloseNotify");
    private static final AtomicIntegerFieldUpdater<SslHandler> SENT_FIRST_MESSAGE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SslHandler.class, "sentFirstMessage");
    private static SslBufferPool defaultBufferPool;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
    private final SslBufferPool bufferPool;
    private boolean closeOnSslException;
    private volatile int closedOutboundAndChannel;
    private volatile ChannelHandlerContext ctx;
    private volatile boolean enableRenegotiation;
    private final SSLEngine engine;
    private volatile ChannelFuture handshakeFuture;
    final Object handshakeLock;
    private Timeout handshakeTimeout;
    private final long handshakeTimeoutInMillis;
    private volatile boolean handshaken;
    private boolean handshaking;
    int ignoreClosedChannelException;
    final Object ignoreClosedChannelExceptionLock;
    private volatile boolean issueHandshake;
    private int packetLength;
    private final Queue<MessageEvent> pendingEncryptedWrites;
    private final NonReentrantLock pendingEncryptedWritesLock;
    private final Queue<PendingWrite> pendingUnencryptedWrites;
    private final NonReentrantLock pendingUnencryptedWritesLock;
    private volatile int sentCloseNotify;
    private volatile int sentFirstMessage;
    private final SSLEngineInboundCloseFuture sslEngineCloseFuture;
    private final boolean startTls;
    private final Timer timer;
    private volatile boolean writeBeforeHandshakeDone;

    static /* synthetic */ class C08647 {
        static final /* synthetic */ int[] $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus = new int[HandshakeStatus.values().length];
        static final /* synthetic */ int[] $SwitchMap$javax$net$ssl$SSLEngineResult$Status = new int[Status.values().length];

        static {
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$Status[Status.CLOSED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$Status[Status.BUFFER_OVERFLOW.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[HandshakeStatus.NEED_WRAP.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[HandshakeStatus.NEED_UNWRAP.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[HandshakeStatus.NEED_TASK.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[HandshakeStatus.FINISHED.ordinal()] = 4;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[HandshakeStatus.NOT_HANDSHAKING.ordinal()] = 5;
            } catch (NoSuchFieldError e7) {
            }
            $SwitchMap$org$jboss$netty$channel$ChannelState = new int[ChannelState.values().length];
            try {
                $SwitchMap$org$jboss$netty$channel$ChannelState[ChannelState.OPEN.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$jboss$netty$channel$ChannelState[ChannelState.CONNECTED.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$jboss$netty$channel$ChannelState[ChannelState.BOUND.ordinal()] = 3;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    private static final class PendingWrite {
        final ChannelFuture future;
        final ByteBuffer outAppBuf;

        PendingWrite(ChannelFuture future, ByteBuffer outAppBuf) {
            this.future = future;
            this.outAppBuf = outAppBuf;
        }
    }

    class C12483 implements ChannelFutureListener {
        C12483() {
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.getCause() instanceof ClosedChannelException) {
                synchronized (SslHandler.this.ignoreClosedChannelExceptionLock) {
                    SslHandler sslHandler = SslHandler.this;
                    sslHandler.ignoreClosedChannelException++;
                }
            }
        }
    }

    private static final class ClosingChannelFutureListener implements ChannelFutureListener {
        private final ChannelHandlerContext context;
        private final ChannelStateEvent f696e;

        ClosingChannelFutureListener(ChannelHandlerContext context, ChannelStateEvent e) {
            this.context = context;
            this.f696e = e;
        }

        public void operationComplete(ChannelFuture closeNotifyFuture) throws Exception {
            if (closeNotifyFuture.getCause() instanceof ClosedChannelException) {
                this.f696e.getFuture().setSuccess();
            } else {
                Channels.close(this.context, this.f696e.getFuture());
            }
        }
    }

    private final class SSLEngineInboundCloseFuture extends DefaultChannelFuture {
        SSLEngineInboundCloseFuture() {
            super(null, true);
        }

        void setClosed() {
            super.setSuccess();
        }

        public Channel getChannel() {
            if (SslHandler.this.ctx == null) {
                return null;
            }
            return SslHandler.this.ctx.getChannel();
        }

        public boolean setSuccess() {
            return false;
        }

        public boolean setFailure(Throwable cause) {
            return false;
        }
    }

    private void wrap(org.jboss.netty.channel.ChannelHandlerContext r24, org.jboss.netty.channel.Channel r25) throws javax.net.ssl.SSLException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r23 = this;
        r0 = r23;
        r0 = r0.bufferPool;
        r19 = r0;
        r14 = r19.acquireBuffer();
        r18 = 1;
        r12 = 0;
        r11 = 0;
        r15 = 0;
    L_0x000f:
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19.lock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r0.pendingUnencryptedWrites;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r19.peek();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = (org.jboss.netty.handler.ssl.SslHandler.PendingWrite) r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r15 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r15 != 0) goto L_0x008e;
    L_0x0029:
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19.unlock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
    L_0x0032:
        r0 = r23;
        r0 = r0.bufferPool;
        r19 = r0;
        r0 = r19;
        r0.releaseBuffer(r14);
        if (r12 == 0) goto L_0x0042;
    L_0x003f:
        r23.flushPendingEncryptedWrites(r24);
    L_0x0042:
        if (r18 != 0) goto L_0x0078;
    L_0x0044:
        r5 = new java.lang.IllegalStateException;
        r19 = "SSLEngine already closed";
        r0 = r19;
        r5.<init>(r0);
        if (r15 == 0) goto L_0x0058;
    L_0x004f:
        r0 = r15.future;
        r19 = r0;
        r0 = r19;
        r0.setFailure(r5);
    L_0x0058:
        r0 = r23;
        r0 = r0.pendingUnencryptedWritesLock;
        r19 = r0;
        r19.lock();
        r0 = r23;	 Catch:{ all -> 0x0283 }
        r0 = r0.pendingUnencryptedWrites;	 Catch:{ all -> 0x0283 }
        r19 = r0;	 Catch:{ all -> 0x0283 }
        r15 = r19.poll();	 Catch:{ all -> 0x0283 }
        r15 = (org.jboss.netty.handler.ssl.SslHandler.PendingWrite) r15;	 Catch:{ all -> 0x0283 }
        if (r15 != 0) goto L_0x028e;
    L_0x006f:
        r0 = r23;
        r0 = r0.pendingUnencryptedWritesLock;
        r19 = r0;
        r19.unlock();
    L_0x0078:
        if (r11 == 0) goto L_0x008d;
    L_0x007a:
        r0 = r23;
        r0 = r0.ctx;
        r19 = r0;
        r20 = 1;
        r0 = r23;
        r1 = r19;
        r2 = r25;
        r3 = r20;
        r0.unwrapNonAppData(r1, r2, r3);
    L_0x008d:
        return;
    L_0x008e:
        r13 = r15.outAppBuf;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r13 != 0) goto L_0x011a;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0092:
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r0.pendingUnencryptedWrites;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19.remove();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = new org.jboss.netty.channel.DownstreamMessageEvent;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r15.future;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r20 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r22 = r25.getRemoteAddress();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r25;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r2 = r20;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r3 = r21;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r4 = r22;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0.<init>(r1, r2, r3, r4);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0.offerEncryptedWriteRequest(r1);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r12 = 1;
    L_0x00bc:
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19.unlock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        goto L_0x000f;
    L_0x00c7:
        r6 = move-exception;
        r18 = 0;
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r1 = r25;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0.setHandshakeFailure(r1, r6);	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        throw r6;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
    L_0x00d2:
        r19 = move-exception;
        r0 = r23;
        r0 = r0.bufferPool;
        r20 = r0;
        r0 = r20;
        r0.releaseBuffer(r14);
        if (r12 == 0) goto L_0x00e3;
    L_0x00e0:
        r23.flushPendingEncryptedWrites(r24);
    L_0x00e3:
        if (r18 != 0) goto L_0x0119;
    L_0x00e5:
        r5 = new java.lang.IllegalStateException;
        r20 = "SSLEngine already closed";
        r0 = r20;
        r5.<init>(r0);
        if (r15 == 0) goto L_0x00f9;
    L_0x00f0:
        r0 = r15.future;
        r20 = r0;
        r0 = r20;
        r0.setFailure(r5);
    L_0x00f9:
        r0 = r23;
        r0 = r0.pendingUnencryptedWritesLock;
        r20 = r0;
        r20.lock();
        r0 = r23;	 Catch:{ all -> 0x0264 }
        r0 = r0.pendingUnencryptedWrites;	 Catch:{ all -> 0x0264 }
        r20 = r0;	 Catch:{ all -> 0x0264 }
        r15 = r20.poll();	 Catch:{ all -> 0x0264 }
        r15 = (org.jboss.netty.handler.ssl.SslHandler.PendingWrite) r15;	 Catch:{ all -> 0x0264 }
        if (r15 != 0) goto L_0x026f;
    L_0x0110:
        r0 = r23;
        r0 = r0.pendingUnencryptedWritesLock;
        r20 = r0;
        r20.unlock();
    L_0x0119:
        throw r19;
    L_0x011a:
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r0.handshakeLock;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r20 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        monitor-enter(r20);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r17 = 0;
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r0.engine;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r17 = r0.wrap(r13, r14);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r13.hasRemaining();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r19 != 0) goto L_0x013e;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0135:
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r0.pendingUnencryptedWrites;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19.remove();	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x013e:
        r19 = r17.bytesProduced();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r19 <= 0) goto L_0x01b1;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0144:
        r14.flip();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r16 = r14.remaining();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r0.ctx;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r19.getChannel();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r19.getConfig();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r19.getBufferFactory();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r16;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r10 = r0.getBuffer(r1);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r10.writeBytes(r14);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r14.clear();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r15.outAppBuf;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r19.hasRemaining();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r19 == 0) goto L_0x01ae;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0175:
        r8 = org.jboss.netty.channel.Channels.succeededFuture(r25);	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0179:
        r7 = new org.jboss.netty.channel.DownstreamMessageEvent;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r25.getRemoteAddress();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r25;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r7.<init>(r0, r8, r10, r1);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0.offerEncryptedWriteRequest(r7);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r12 = 1;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x018c:
        monitor-exit(r20);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        goto L_0x00bc;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x018f:
        r19 = move-exception;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        monitor-exit(r20);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        throw r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0192:
        r19 = move-exception;
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r20 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r20.unlock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        throw r19;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
    L_0x019d:
        r19 = move-exception;
        r21 = r13.hasRemaining();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r21 != 0) goto L_0x01ad;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x01a4:
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r0.pendingUnencryptedWrites;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = r0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21.remove();	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x01ad:
        throw r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x01ae:
        r8 = r15.future;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        goto L_0x0179;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x01b1:
        r19 = r17.getStatus();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = javax.net.ssl.SSLEngineResult.Status.CLOSED;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r21;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r0 != r1) goto L_0x01cb;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x01bd:
        r18 = 0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        monitor-exit(r20);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19.unlock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        goto L_0x0032;
    L_0x01cb:
        r9 = r17.getHandshakeStatus();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0.handleRenegotiation(r9);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = org.jboss.netty.handler.ssl.SslHandler.C08647.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = r9.ordinal();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r19[r21];	 Catch:{ all -> 0x019d, all -> 0x0192 }
        switch(r19) {
            case 1: goto L_0x01fe;
            case 2: goto L_0x0210;
            case 3: goto L_0x021d;
            case 4: goto L_0x0222;
            case 5: goto L_0x0243;
            default: goto L_0x01df;
        };	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x01df:
        r19 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = new java.lang.StringBuilder;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21.<init>();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r22 = "Unknown handshake status: ";	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = r21.append(r22);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r21;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = r0.append(r9);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = r21.toString();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r21;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0.<init>(r1);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        throw r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x01fe:
        r19 = r13.hasRemaining();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r19 != 0) goto L_0x018c;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0204:
        monitor-exit(r20);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19.unlock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        goto L_0x0032;
    L_0x0210:
        r11 = 1;
        monitor-exit(r20);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19.unlock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        goto L_0x0032;
    L_0x021d:
        r23.runDelegatedTasks();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        goto L_0x018c;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0222:
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r25;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0.setHandshakeSuccess(r1);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r17.getStatus();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = javax.net.ssl.SSLEngineResult.Status.CLOSED;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r21;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r0 != r1) goto L_0x0237;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0235:
        r18 = 0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0237:
        monitor-exit(r20);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19.unlock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        goto L_0x0032;
    L_0x0243:
        r0 = r23;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r25;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0.setHandshakeSuccessIfStillHandshaking(r1);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r19 = r17.getStatus();	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r21 = javax.net.ssl.SSLEngineResult.Status.CLOSED;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r19;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r1 = r21;	 Catch:{ all -> 0x019d, all -> 0x0192 }
        if (r0 != r1) goto L_0x0258;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0256:
        r18 = 0;	 Catch:{ all -> 0x019d, all -> 0x0192 }
    L_0x0258:
        monitor-exit(r20);	 Catch:{ all -> 0x019d, all -> 0x0192 }
        r0 = r23;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r0 = r0.pendingUnencryptedWritesLock;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19 = r0;	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        r19.unlock();	 Catch:{ SSLException -> 0x00c7, all -> 0x00d2 }
        goto L_0x0032;
    L_0x0264:
        r19 = move-exception;
        r0 = r23;
        r0 = r0.pendingUnencryptedWritesLock;
        r20 = r0;
        r20.unlock();
        throw r19;
    L_0x026f:
        r0 = r23;
        r0 = r0.pendingUnencryptedWritesLock;
        r20 = r0;
        r20.unlock();
        r0 = r15.future;
        r20 = r0;
        r0 = r20;
        r0.setFailure(r5);
        goto L_0x00f9;
    L_0x0283:
        r19 = move-exception;
        r0 = r23;
        r0 = r0.pendingUnencryptedWritesLock;
        r20 = r0;
        r20.unlock();
        throw r19;
    L_0x028e:
        r0 = r23;
        r0 = r0.pendingUnencryptedWritesLock;
        r19 = r0;
        r19.unlock();
        r0 = r15.future;
        r19 = r0;
        r0 = r19;
        r0.setFailure(r5);
        goto L_0x0058;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.ssl.SslHandler.wrap(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel):void");
    }

    public static synchronized SslBufferPool getDefaultBufferPool() {
        SslBufferPool sslBufferPool;
        synchronized (SslHandler.class) {
            if (defaultBufferPool == null) {
                defaultBufferPool = new SslBufferPool();
            }
            sslBufferPool = defaultBufferPool;
        }
        return sslBufferPool;
    }

    public SslHandler(SSLEngine engine) {
        this(engine, getDefaultBufferPool(), false, null, 0);
    }

    public SslHandler(SSLEngine engine, SslBufferPool bufferPool) {
        this(engine, bufferPool, false, null, 0);
    }

    public SslHandler(SSLEngine engine, boolean startTls) {
        this(engine, getDefaultBufferPool(), startTls);
    }

    public SslHandler(SSLEngine engine, SslBufferPool bufferPool, boolean startTls) {
        this(engine, bufferPool, startTls, null, 0);
    }

    public SslHandler(SSLEngine engine, SslBufferPool bufferPool, boolean startTls, Timer timer, long handshakeTimeoutInMillis) {
        this.enableRenegotiation = true;
        this.handshakeLock = new Object();
        this.ignoreClosedChannelExceptionLock = new Object();
        this.pendingUnencryptedWrites = new LinkedList();
        this.pendingUnencryptedWritesLock = new NonReentrantLock();
        this.pendingEncryptedWrites = new ConcurrentLinkedQueue();
        this.pendingEncryptedWritesLock = new NonReentrantLock();
        this.sslEngineCloseFuture = new SSLEngineInboundCloseFuture();
        if (engine == null) {
            throw new NullPointerException("engine");
        } else if (bufferPool == null) {
            throw new NullPointerException("bufferPool");
        } else if (timer != null || handshakeTimeoutInMillis <= 0) {
            this.engine = engine;
            this.bufferPool = bufferPool;
            this.startTls = startTls;
            this.timer = timer;
            this.handshakeTimeoutInMillis = handshakeTimeoutInMillis;
        } else {
            throw new IllegalArgumentException("No Timer was given but a handshakeTimeoutInMillis, need both or none");
        }
    }

    public SSLEngine getEngine() {
        return this.engine;
    }

    public ChannelFuture handshake() {
        ChannelFuture channelFuture;
        synchronized (this.handshakeLock) {
            if (!this.handshaken || isEnableRenegotiation()) {
                final ChannelHandlerContext ctx = this.ctx;
                final Channel channel = ctx.getChannel();
                Throwable exception = null;
                if (this.handshaking) {
                    channelFuture = this.handshakeFuture;
                } else {
                    this.handshaking = true;
                    try {
                        this.engine.beginHandshake();
                        runDelegatedTasks();
                        channelFuture = Channels.future(channel);
                        this.handshakeFuture = channelFuture;
                        if (this.handshakeTimeoutInMillis > 0) {
                            this.handshakeTimeout = this.timer.newTimeout(new TimerTask() {
                                public void run(Timeout timeout) throws Exception {
                                    ChannelFuture future = SslHandler.this.handshakeFuture;
                                    if (future == null || !future.isDone()) {
                                        SslHandler.this.setHandshakeFailure(channel, new SSLException("Handshake did not complete within " + SslHandler.this.handshakeTimeoutInMillis + "ms"));
                                    }
                                }
                            }, this.handshakeTimeoutInMillis, TimeUnit.MILLISECONDS);
                        }
                    } catch (Throwable e) {
                        channelFuture = Channels.failedFuture(channel, e);
                        this.handshakeFuture = channelFuture;
                        exception = e;
                    }
                    if (exception == null) {
                        final ChannelFuture hsFuture = channelFuture;
                        try {
                            wrapNonAppData(ctx, channel).addListener(new ChannelFutureListener() {
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    if (!future.isSuccess()) {
                                        Throwable cause = future.getCause();
                                        hsFuture.setFailure(cause);
                                        Channels.fireExceptionCaught(ctx, cause);
                                        if (SslHandler.this.closeOnSslException) {
                                            Channels.close(ctx, Channels.future(channel));
                                        }
                                    }
                                }
                            });
                        } catch (Throwable e2) {
                            channelFuture.setFailure(e2);
                            Channels.fireExceptionCaught(ctx, e2);
                            if (this.closeOnSslException) {
                                Channels.close(ctx, Channels.future(channel));
                            }
                        }
                    } else {
                        Channels.fireExceptionCaught(ctx, exception);
                        if (this.closeOnSslException) {
                            Channels.close(ctx, Channels.future(channel));
                        }
                    }
                }
            } else {
                throw new IllegalStateException("renegotiation disabled");
            }
        }
        return channelFuture;
    }

    public ChannelFuture close() {
        ChannelHandlerContext ctx = this.ctx;
        Channel channel = ctx.getChannel();
        try {
            this.engine.closeOutbound();
            return wrapNonAppData(ctx, channel);
        } catch (Throwable e) {
            Channels.fireExceptionCaught(ctx, e);
            if (this.closeOnSslException) {
                Channels.close(ctx, Channels.future(channel));
            }
            return Channels.failedFuture(channel, e);
        }
    }

    public boolean isEnableRenegotiation() {
        return this.enableRenegotiation;
    }

    public void setEnableRenegotiation(boolean enableRenegotiation) {
        this.enableRenegotiation = enableRenegotiation;
    }

    public void setIssueHandshake(boolean issueHandshake) {
        this.issueHandshake = issueHandshake;
    }

    public boolean isIssueHandshake() {
        return this.issueHandshake;
    }

    public ChannelFuture getSSLEngineInboundCloseFuture() {
        return this.sslEngineCloseFuture;
    }

    public long getHandshakeTimeout() {
        return this.handshakeTimeoutInMillis;
    }

    public void setCloseOnSSLException(boolean closeOnSslException) {
        if (this.ctx != null) {
            throw new IllegalStateException("Can only get changed before attached to ChannelPipeline");
        }
        this.closeOnSslException = closeOnSslException;
    }

    public boolean getCloseOnSSLException() {
        return this.closeOnSslException;
    }

    public void handleDownstream(ChannelHandlerContext context, ChannelEvent evt) throws Exception {
        if (evt instanceof ChannelStateEvent) {
            ChannelStateEvent e = (ChannelStateEvent) evt;
            switch (e.getState()) {
                case OPEN:
                case CONNECTED:
                case BOUND:
                    if (Boolean.FALSE.equals(e.getValue()) || e.getValue() == null) {
                        closeOutboundAndChannel(context, e);
                        return;
                    }
            }
        }
        if (evt instanceof MessageEvent) {
            MessageEvent e2 = (MessageEvent) evt;
            if (!(e2.getMessage() instanceof ChannelBuffer)) {
                context.sendDownstream(evt);
            } else if (this.startTls && SENT_FIRST_MESSAGE_UPDATER.compareAndSet(this, 0, 1)) {
                context.sendDownstream(evt);
            } else {
                PendingWrite pendingWrite;
                ChannelBuffer msg = (ChannelBuffer) e2.getMessage();
                if (msg.readable()) {
                    pendingWrite = new PendingWrite(evt.getFuture(), msg.toByteBuffer(msg.readerIndex(), msg.readableBytes()));
                } else {
                    pendingWrite = new PendingWrite(evt.getFuture(), null);
                }
                this.pendingUnencryptedWritesLock.lock();
                try {
                    this.pendingUnencryptedWrites.add(pendingWrite);
                    if (this.handshakeFuture == null || !this.handshakeFuture.isDone()) {
                        this.writeBeforeHandshakeDone = true;
                    }
                    wrap(context, evt.getChannel());
                } finally {
                    this.pendingUnencryptedWritesLock.unlock();
                }
            }
        } else {
            context.sendDownstream(evt);
        }
    }

    private void cancelHandshakeTimeout() {
        if (this.handshakeTimeout != null) {
            this.handshakeTimeout.cancel();
        }
    }

    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        synchronized (this.handshakeLock) {
            if (this.handshaking) {
                cancelHandshakeTimeout();
                this.handshakeFuture.setFailure(new ClosedChannelException());
            }
        }
        try {
            super.channelDisconnected(ctx, e);
        } finally {
            unwrapNonAppData(ctx, e.getChannel(), false);
            closeEngine();
        }
    }

    private void closeEngine() {
        this.engine.closeOutbound();
        if (this.sentCloseNotify == 0 && this.handshaken) {
            try {
                this.engine.closeInbound();
            } catch (SSLException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to clean up SSLEngine.", ex);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext r5, org.jboss.netty.channel.ExceptionEvent r6) throws java.lang.Exception {
        /*
        r4 = this;
        r0 = r6.getCause();
        r1 = r0 instanceof java.io.IOException;
        if (r1 == 0) goto L_0x002b;
    L_0x0008:
        r1 = r0 instanceof java.nio.channels.ClosedChannelException;
        if (r1 == 0) goto L_0x0032;
    L_0x000c:
        r2 = r4.ignoreClosedChannelExceptionLock;
        monitor-enter(r2);
        r1 = r4.ignoreClosedChannelException;	 Catch:{ all -> 0x002f }
        if (r1 <= 0) goto L_0x002a;
    L_0x0013:
        r1 = r4.ignoreClosedChannelException;	 Catch:{ all -> 0x002f }
        r1 = r1 + -1;
        r4.ignoreClosedChannelException = r1;	 Catch:{ all -> 0x002f }
        r1 = logger;	 Catch:{ all -> 0x002f }
        r1 = r1.isDebugEnabled();	 Catch:{ all -> 0x002f }
        if (r1 == 0) goto L_0x0028;
    L_0x0021:
        r1 = logger;	 Catch:{ all -> 0x002f }
        r3 = "Swallowing an exception raised while writing non-app data";
        r1.debug(r3, r0);	 Catch:{ all -> 0x002f }
    L_0x0028:
        monitor-exit(r2);	 Catch:{ all -> 0x002f }
    L_0x0029:
        return;
    L_0x002a:
        monitor-exit(r2);	 Catch:{ all -> 0x002f }
    L_0x002b:
        r5.sendUpstream(r6);
        goto L_0x0029;
    L_0x002f:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x002f }
        throw r1;
    L_0x0032:
        r1 = r4.ignoreException(r0);
        if (r1 == 0) goto L_0x002b;
    L_0x0038:
        goto L_0x0029;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.ssl.SslHandler.exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent):void");
    }

    private boolean ignoreException(Throwable t) {
        if (!(t instanceof SSLException) && (t instanceof IOException) && this.engine.isOutboundDone()) {
            if (IGNORABLE_ERROR_MESSAGE.matcher(String.valueOf(t.getMessage()).toLowerCase()).matches()) {
                return true;
            }
            for (StackTraceElement element : t.getStackTrace()) {
                String classname = element.getClassName();
                String methodname = element.getMethodName();
                if (!classname.startsWith("org.jboss.netty.") && "read".equals(methodname)) {
                    if (IGNORABLE_CLASS_IN_STACK.matcher(classname).matches()) {
                        return true;
                    }
                    try {
                        Class<?> clazz = getClass().getClassLoader().loadClass(classname);
                        if (SocketChannel.class.isAssignableFrom(clazz) || DatagramChannel.class.isAssignableFrom(clazz)) {
                            return true;
                        }
                        if (DetectionUtil.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(clazz.getSuperclass().getName())) {
                            return true;
                        }
                    } catch (ClassNotFoundException e) {
                    }
                }
            }
        }
        return false;
    }

    public static boolean isEncrypted(ChannelBuffer buffer) {
        return getEncryptedPacketLength(buffer, buffer.readerIndex()) != -1;
    }

    private static int getEncryptedPacketLength(ChannelBuffer buffer, int offset) {
        boolean tls;
        int packetLength = 0;
        switch (buffer.getUnsignedByte(offset)) {
            case (short) 20:
            case (short) 21:
            case (short) 22:
            case (short) 23:
                tls = true;
                break;
            default:
                tls = false;
                break;
        }
        if (tls) {
            if (buffer.getUnsignedByte(offset + 1) == 3) {
                packetLength = (getShort(buffer, offset + 3) & SupportMenu.USER_MASK) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            } else {
                tls = false;
            }
        }
        if (!tls) {
            int headerLength;
            boolean sslv2 = true;
            if ((buffer.getUnsignedByte(offset) & 128) != 0) {
                headerLength = 2;
            } else {
                headerLength = 3;
            }
            int majorVersion = buffer.getUnsignedByte((offset + headerLength) + 1);
            if (majorVersion == 2 || majorVersion == 3) {
                if (headerLength == 2) {
                    packetLength = (getShort(buffer, offset) & 32767) + 2;
                } else {
                    packetLength = (getShort(buffer, offset) & 16383) + 3;
                }
                if (packetLength <= headerLength) {
                    sslv2 = false;
                }
            } else {
                sslv2 = false;
            }
            if (!sslv2) {
                return -1;
            }
        }
        return packetLength;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer in) throws Exception {
        int startOffset = in.readerIndex();
        int endOffset = in.writerIndex();
        int offset = startOffset;
        int totalLength = 0;
        if (this.packetLength > 0) {
            if (endOffset - startOffset < this.packetLength) {
                return null;
            }
            offset += this.packetLength;
            totalLength = this.packetLength;
            this.packetLength = 0;
        }
        boolean nonSslRecord = false;
        while (totalLength < 18713) {
            int readableBytes = endOffset - offset;
            if (readableBytes < 5) {
                break;
            }
            int packetLength = getEncryptedPacketLength(in, offset);
            if (packetLength == -1) {
                nonSslRecord = true;
                break;
            } else if ($assertionsDisabled || packetLength > 0) {
                if (packetLength <= readableBytes) {
                    int newTotalLength = totalLength + packetLength;
                    if (newTotalLength > 18713) {
                        break;
                    }
                    offset += packetLength;
                    totalLength = newTotalLength;
                } else {
                    this.packetLength = packetLength;
                    break;
                }
            } else {
                throw new AssertionError();
            }
        }
        Object unwrapped = null;
        if (totalLength > 0) {
            in.skipBytes(totalLength);
            unwrapped = unwrap(ctx, channel, in.toByteBuffer(startOffset, totalLength), totalLength, true);
        }
        if (!nonSslRecord) {
            return unwrapped;
        }
        Throwable e = new NotSslRecordException("not an SSL/TLS record: " + ChannelBuffers.hexDump(in));
        in.skipBytes(in.readableBytes());
        if (this.closeOnSslException) {
            Channels.fireExceptionCaught(ctx, e);
            Channels.close(ctx, Channels.future(channel));
            return null;
        }
        throw e;
    }

    private static short getShort(ChannelBuffer buf, int offset) {
        return (short) ((buf.getByte(offset) << 8) | (buf.getByte(offset + 1) & 255));
    }

    private void offerEncryptedWriteRequest(MessageEvent encryptedWrite) {
        boolean locked = this.pendingEncryptedWritesLock.tryLock();
        try {
            this.pendingEncryptedWrites.add(encryptedWrite);
        } finally {
            if (locked) {
                this.pendingEncryptedWritesLock.unlock();
            }
        }
    }

    private void flushPendingEncryptedWrites(ChannelHandlerContext ctx) {
        while (!this.pendingEncryptedWrites.isEmpty() && this.pendingEncryptedWritesLock.tryLock()) {
            while (true) {
                try {
                    MessageEvent e = (MessageEvent) this.pendingEncryptedWrites.poll();
                    if (e == null) {
                        break;
                    }
                    ctx.sendDownstream(e);
                } catch (Throwable th) {
                    this.pendingEncryptedWritesLock.unlock();
                }
            }
            this.pendingEncryptedWritesLock.unlock();
        }
    }

    private ChannelFuture wrapNonAppData(ChannelHandlerContext ctx, Channel channel) throws SSLException {
        ChannelFuture future = null;
        ByteBuffer outNetBuf = this.bufferPool.acquireBuffer();
        SSLEngineResult result;
        do {
            synchronized (this.handshakeLock) {
                result = this.engine.wrap(EMPTY_BUFFER, outNetBuf);
            }
            try {
                if (result.bytesProduced() > 0) {
                    outNetBuf.flip();
                    Object msg = ctx.getChannel().getConfig().getBufferFactory().getBuffer(outNetBuf.remaining());
                    msg.writeBytes(outNetBuf);
                    outNetBuf.clear();
                    future = Channels.future(channel);
                    future.addListener(new C12483());
                    Channels.write(ctx, future, msg);
                }
                HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                handleRenegotiation(handshakeStatus);
                switch (C08647.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[handshakeStatus.ordinal()]) {
                    case 1:
                        break;
                    case 2:
                        if (!Thread.holdsLock(this.handshakeLock)) {
                            unwrapNonAppData(ctx, channel, true);
                            break;
                        }
                        break;
                    case 3:
                        runDelegatedTasks();
                        break;
                    case 4:
                        setHandshakeSuccess(channel);
                        runDelegatedTasks();
                        break;
                    case 5:
                        if (setHandshakeSuccessIfStillHandshaking(channel)) {
                            runDelegatedTasks();
                            break;
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected handshake status: " + handshakeStatus);
                }
            } catch (SSLException e) {
                setHandshakeFailure(channel, e);
                throw e;
            } catch (Throwable th) {
                this.bufferPool.releaseBuffer(outNetBuf);
            }
        } while (result.bytesProduced() != 0);
        this.bufferPool.releaseBuffer(outNetBuf);
        if (future == null) {
            return Channels.succeededFuture(channel);
        }
        return future;
    }

    private void unwrapNonAppData(ChannelHandlerContext ctx, Channel channel, boolean mightNeedHandshake) throws SSLException {
        unwrap(ctx, channel, EMPTY_BUFFER, -1, mightNeedHandshake);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.jboss.netty.buffer.ChannelBuffer unwrap(org.jboss.netty.channel.ChannelHandlerContext r18, org.jboss.netty.channel.Channel r19, java.nio.ByteBuffer r20, int r21, boolean r22) throws javax.net.ssl.SSLException {
        /*
        r17 = this;
        r8 = r20.position();
        r0 = r17;
        r13 = r0.bufferPool;
        r9 = r13.acquireBuffer();
        r7 = 0;
        r6 = 0;
    L_0x000e:
        r5 = 0;
        if (r22 == 0) goto L_0x0042;
    L_0x0011:
        r0 = r17;
        r14 = r0.handshakeLock;	 Catch:{ SSLException -> 0x00ce }
        monitor-enter(r14);	 Catch:{ SSLException -> 0x00ce }
        r0 = r17;
        r13 = r0.handshaken;	 Catch:{ all -> 0x00e0 }
        if (r13 != 0) goto L_0x0041;
    L_0x001c:
        r0 = r17;
        r13 = r0.handshaking;	 Catch:{ all -> 0x00e0 }
        if (r13 != 0) goto L_0x0041;
    L_0x0022:
        r0 = r17;
        r13 = r0.engine;	 Catch:{ all -> 0x00e0 }
        r13 = r13.getUseClientMode();	 Catch:{ all -> 0x00e0 }
        if (r13 != 0) goto L_0x0041;
    L_0x002c:
        r0 = r17;
        r13 = r0.engine;	 Catch:{ all -> 0x00e0 }
        r13 = r13.isInboundDone();	 Catch:{ all -> 0x00e0 }
        if (r13 != 0) goto L_0x0041;
    L_0x0036:
        r0 = r17;
        r13 = r0.engine;	 Catch:{ all -> 0x00e0 }
        r13 = r13.isOutboundDone();	 Catch:{ all -> 0x00e0 }
        if (r13 != 0) goto L_0x0041;
    L_0x0040:
        r5 = 1;
    L_0x0041:
        monitor-exit(r14);	 Catch:{ all -> 0x00e0 }
    L_0x0042:
        if (r5 == 0) goto L_0x0047;
    L_0x0044:
        r17.handshake();	 Catch:{ SSLException -> 0x00ce }
    L_0x0047:
        r0 = r17;
        r14 = r0.handshakeLock;	 Catch:{ SSLException -> 0x00ce }
        monitor-enter(r14);	 Catch:{ SSLException -> 0x00ce }
    L_0x004c:
        r0 = r17;
        r13 = r0.engine;	 Catch:{ all -> 0x00cb }
        r13 = r13.getSession();	 Catch:{ all -> 0x00cb }
        r11 = r13.getApplicationBufferSize();	 Catch:{ all -> 0x00cb }
        r13 = r9.capacity();	 Catch:{ all -> 0x00cb }
        if (r13 >= r11) goto L_0x00e3;
    L_0x005e:
        r10 = java.nio.ByteBuffer.allocate(r11);	 Catch:{ all -> 0x00cb }
    L_0x0062:
        r0 = r17;
        r13 = r0.engine;	 Catch:{ all -> 0x00ee }
        r0 = r20;
        r12 = r13.unwrap(r0, r10);	 Catch:{ all -> 0x00ee }
        r13 = org.jboss.netty.handler.ssl.SslHandler.C08647.$SwitchMap$javax$net$ssl$SSLEngineResult$Status;	 Catch:{ all -> 0x00ee }
        r15 = r12.getStatus();	 Catch:{ all -> 0x00ee }
        r15 = r15.ordinal();	 Catch:{ all -> 0x00ee }
        r13 = r13[r15];	 Catch:{ all -> 0x00ee }
        switch(r13) {
            case 1: goto L_0x00e6;
            case 2: goto L_0x01fb;
            default: goto L_0x007b;
        };
    L_0x007b:
        r10.flip();	 Catch:{ all -> 0x00cb }
        r13 = r10.hasRemaining();	 Catch:{ all -> 0x00cb }
        if (r13 == 0) goto L_0x009b;
    L_0x0084:
        if (r7 != 0) goto L_0x0098;
    L_0x0086:
        r13 = r18.getChannel();	 Catch:{ all -> 0x00cb }
        r13 = r13.getConfig();	 Catch:{ all -> 0x00cb }
        r3 = r13.getBufferFactory();	 Catch:{ all -> 0x00cb }
        r0 = r21;
        r7 = r3.getBuffer(r0);	 Catch:{ all -> 0x00cb }
    L_0x0098:
        r7.writeBytes(r10);	 Catch:{ all -> 0x00cb }
    L_0x009b:
        r10.clear();	 Catch:{ all -> 0x00cb }
        r4 = r12.getHandshakeStatus();	 Catch:{ all -> 0x00cb }
        r0 = r17;
        r0.handleRenegotiation(r4);	 Catch:{ all -> 0x00cb }
        r13 = org.jboss.netty.handler.ssl.SslHandler.C08647.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus;	 Catch:{ all -> 0x00cb }
        r15 = r4.ordinal();	 Catch:{ all -> 0x00cb }
        r13 = r13[r15];	 Catch:{ all -> 0x00cb }
        switch(r13) {
            case 1: goto L_0x0113;
            case 2: goto L_0x0116;
            case 3: goto L_0x01ca;
            case 4: goto L_0x01cf;
            case 5: goto L_0x01da;
            default: goto L_0x00b2;
        };	 Catch:{ all -> 0x00cb }
    L_0x00b2:
        r13 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x00cb }
        r15 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00cb }
        r15.<init>();	 Catch:{ all -> 0x00cb }
        r16 = "Unknown handshake status: ";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r15 = r15.append(r4);	 Catch:{ all -> 0x00cb }
        r15 = r15.toString();	 Catch:{ all -> 0x00cb }
        r13.<init>(r15);	 Catch:{ all -> 0x00cb }
        throw r13;	 Catch:{ all -> 0x00cb }
    L_0x00cb:
        r13 = move-exception;
        monitor-exit(r14);	 Catch:{ all -> 0x00cb }
        throw r13;	 Catch:{ SSLException -> 0x00ce }
    L_0x00ce:
        r2 = move-exception;
        r0 = r17;
        r1 = r19;
        r0.setHandshakeFailure(r1, r2);	 Catch:{ all -> 0x00d7 }
        throw r2;	 Catch:{ all -> 0x00d7 }
    L_0x00d7:
        r13 = move-exception;
        r0 = r17;
        r14 = r0.bufferPool;
        r14.releaseBuffer(r9);
        throw r13;
    L_0x00e0:
        r13 = move-exception;
        monitor-exit(r14);	 Catch:{ all -> 0x00e0 }
        throw r13;	 Catch:{ SSLException -> 0x00ce }
    L_0x00e3:
        r10 = r9;
        goto L_0x0062;
    L_0x00e6:
        r0 = r17;
        r13 = r0.sslEngineCloseFuture;	 Catch:{ all -> 0x00ee }
        r13.setClosed();	 Catch:{ all -> 0x00ee }
        goto L_0x007b;
    L_0x00ee:
        r13 = move-exception;
        r10.flip();	 Catch:{ all -> 0x00cb }
        r15 = r10.hasRemaining();	 Catch:{ all -> 0x00cb }
        if (r15 == 0) goto L_0x010f;
    L_0x00f8:
        if (r7 != 0) goto L_0x010c;
    L_0x00fa:
        r15 = r18.getChannel();	 Catch:{ all -> 0x00cb }
        r15 = r15.getConfig();	 Catch:{ all -> 0x00cb }
        r3 = r15.getBufferFactory();	 Catch:{ all -> 0x00cb }
        r0 = r21;
        r7 = r3.getBuffer(r0);	 Catch:{ all -> 0x00cb }
    L_0x010c:
        r7.writeBytes(r10);	 Catch:{ all -> 0x00cb }
    L_0x010f:
        r10.clear();	 Catch:{ all -> 0x00cb }
        throw r13;	 Catch:{ all -> 0x00cb }
    L_0x0113:
        r17.wrapNonAppData(r18, r19);	 Catch:{ all -> 0x00cb }
    L_0x0116:
        r13 = r12.getStatus();	 Catch:{ all -> 0x00cb }
        r15 = javax.net.ssl.SSLEngineResult.Status.BUFFER_UNDERFLOW;	 Catch:{ all -> 0x00cb }
        if (r13 == r15) goto L_0x012a;
    L_0x011e:
        r13 = r12.bytesConsumed();	 Catch:{ all -> 0x00cb }
        if (r13 != 0) goto L_0x01f6;
    L_0x0124:
        r13 = r12.bytesProduced();	 Catch:{ all -> 0x00cb }
        if (r13 != 0) goto L_0x01f6;
    L_0x012a:
        r13 = r20.hasRemaining();	 Catch:{ all -> 0x00cb }
        if (r13 == 0) goto L_0x01a0;
    L_0x0130:
        r0 = r17;
        r13 = r0.engine;	 Catch:{ all -> 0x00cb }
        r13 = r13.isInboundDone();	 Catch:{ all -> 0x00cb }
        if (r13 != 0) goto L_0x01a0;
    L_0x013a:
        r13 = logger;	 Catch:{ all -> 0x00cb }
        r15 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00cb }
        r15.<init>();	 Catch:{ all -> 0x00cb }
        r16 = "Unexpected leftover data after SSLEngine.unwrap(): status=";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = r12.getStatus();	 Catch:{ all -> 0x00cb }
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = " handshakeStatus=";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = r12.getHandshakeStatus();	 Catch:{ all -> 0x00cb }
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = " consumed=";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = r12.bytesConsumed();	 Catch:{ all -> 0x00cb }
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = " produced=";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = r12.bytesProduced();	 Catch:{ all -> 0x00cb }
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = " remaining=";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = r20.remaining();	 Catch:{ all -> 0x00cb }
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = " data=";
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r16 = org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer(r20);	 Catch:{ all -> 0x00cb }
        r16 = org.jboss.netty.buffer.ChannelBuffers.hexDump(r16);	 Catch:{ all -> 0x00cb }
        r15 = r15.append(r16);	 Catch:{ all -> 0x00cb }
        r15 = r15.toString();	 Catch:{ all -> 0x00cb }
        r13.warn(r15);	 Catch:{ all -> 0x00cb }
    L_0x01a0:
        monitor-exit(r14);	 Catch:{ all -> 0x00cb }
        if (r6 == 0) goto L_0x01ba;
    L_0x01a3:
        r0 = r17;
        r13 = r0.handshakeLock;	 Catch:{ SSLException -> 0x00ce }
        r13 = java.lang.Thread.holdsLock(r13);	 Catch:{ SSLException -> 0x00ce }
        if (r13 != 0) goto L_0x01ba;
    L_0x01ad:
        r0 = r17;
        r13 = r0.pendingEncryptedWritesLock;	 Catch:{ SSLException -> 0x00ce }
        r13 = r13.isHeldByCurrentThread();	 Catch:{ SSLException -> 0x00ce }
        if (r13 != 0) goto L_0x01ba;
    L_0x01b7:
        r17.wrap(r18, r19);	 Catch:{ SSLException -> 0x00ce }
    L_0x01ba:
        r0 = r17;
        r13 = r0.bufferPool;
        r13.releaseBuffer(r9);
        if (r7 == 0) goto L_0x01f9;
    L_0x01c3:
        r13 = r7.readable();
        if (r13 == 0) goto L_0x01f9;
    L_0x01c9:
        return r7;
    L_0x01ca:
        r17.runDelegatedTasks();	 Catch:{ all -> 0x00cb }
        goto L_0x0116;
    L_0x01cf:
        r0 = r17;
        r1 = r19;
        r0.setHandshakeSuccess(r1);	 Catch:{ all -> 0x00cb }
        r6 = 1;
        monitor-exit(r14);	 Catch:{ all -> 0x00cb }
        goto L_0x000e;
    L_0x01da:
        r0 = r17;
        r1 = r19;
        r13 = r0.setHandshakeSuccessIfStillHandshaking(r1);	 Catch:{ all -> 0x00cb }
        if (r13 == 0) goto L_0x01e8;
    L_0x01e4:
        r6 = 1;
        monitor-exit(r14);	 Catch:{ all -> 0x00cb }
        goto L_0x000e;
    L_0x01e8:
        r0 = r17;
        r13 = r0.writeBeforeHandshakeDone;	 Catch:{ all -> 0x00cb }
        if (r13 == 0) goto L_0x0116;
    L_0x01ee:
        r13 = 0;
        r0 = r17;
        r0.writeBeforeHandshakeDone = r13;	 Catch:{ all -> 0x00cb }
        r6 = 1;
        goto L_0x0116;
    L_0x01f6:
        monitor-exit(r14);	 Catch:{ all -> 0x00cb }
        goto L_0x000e;
    L_0x01f9:
        r7 = 0;
        goto L_0x01c9;
    L_0x01fb:
        r10.flip();	 Catch:{ all -> 0x00cb }
        r13 = r10.hasRemaining();	 Catch:{ all -> 0x00cb }
        if (r13 == 0) goto L_0x021b;
    L_0x0204:
        if (r7 != 0) goto L_0x0218;
    L_0x0206:
        r13 = r18.getChannel();	 Catch:{ all -> 0x00cb }
        r13 = r13.getConfig();	 Catch:{ all -> 0x00cb }
        r3 = r13.getBufferFactory();	 Catch:{ all -> 0x00cb }
        r0 = r21;
        r7 = r3.getBuffer(r0);	 Catch:{ all -> 0x00cb }
    L_0x0218:
        r7.writeBytes(r10);	 Catch:{ all -> 0x00cb }
    L_0x021b:
        r10.clear();	 Catch:{ all -> 0x00cb }
        goto L_0x004c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.ssl.SslHandler.unwrap(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.nio.ByteBuffer, int, boolean):org.jboss.netty.buffer.ChannelBuffer");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleRenegotiation(javax.net.ssl.SSLEngineResult.HandshakeStatus r6) {
        /*
        r5 = this;
        r2 = r5.handshakeLock;
        monitor-enter(r2);
        r1 = javax.net.ssl.SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;	 Catch:{ all -> 0x0013 }
        if (r6 == r1) goto L_0x000b;
    L_0x0007:
        r1 = javax.net.ssl.SSLEngineResult.HandshakeStatus.FINISHED;	 Catch:{ all -> 0x0013 }
        if (r6 != r1) goto L_0x000d;
    L_0x000b:
        monitor-exit(r2);	 Catch:{ all -> 0x0013 }
    L_0x000c:
        return;
    L_0x000d:
        r1 = r5.handshaken;	 Catch:{ all -> 0x0013 }
        if (r1 != 0) goto L_0x0016;
    L_0x0011:
        monitor-exit(r2);	 Catch:{ all -> 0x0013 }
        goto L_0x000c;
    L_0x0013:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0013 }
        throw r1;
    L_0x0016:
        r1 = r5.handshaking;	 Catch:{ all -> 0x0013 }
        if (r1 == 0) goto L_0x001c;
    L_0x001a:
        monitor-exit(r2);	 Catch:{ all -> 0x0013 }
        goto L_0x000c;
    L_0x001c:
        r1 = r5.engine;	 Catch:{ all -> 0x0013 }
        r1 = r1.isInboundDone();	 Catch:{ all -> 0x0013 }
        if (r1 != 0) goto L_0x002c;
    L_0x0024:
        r1 = r5.engine;	 Catch:{ all -> 0x0013 }
        r1 = r1.isOutboundDone();	 Catch:{ all -> 0x0013 }
        if (r1 == 0) goto L_0x002e;
    L_0x002c:
        monitor-exit(r2);	 Catch:{ all -> 0x0013 }
        goto L_0x000c;
    L_0x002e:
        r1 = r5.isEnableRenegotiation();	 Catch:{ all -> 0x0013 }
        if (r1 == 0) goto L_0x003c;
    L_0x0034:
        r0 = 1;
    L_0x0035:
        if (r0 == 0) goto L_0x0041;
    L_0x0037:
        r5.handshake();	 Catch:{ all -> 0x0013 }
    L_0x003a:
        monitor-exit(r2);	 Catch:{ all -> 0x0013 }
        goto L_0x000c;
    L_0x003c:
        r0 = 0;
        r1 = 1;
        r5.handshaking = r1;	 Catch:{ all -> 0x0013 }
        goto L_0x0035;
    L_0x0041:
        r1 = r5.ctx;	 Catch:{ all -> 0x0013 }
        r3 = new javax.net.ssl.SSLException;	 Catch:{ all -> 0x0013 }
        r4 = "renegotiation attempted by peer; closing the connection";
        r3.<init>(r4);	 Catch:{ all -> 0x0013 }
        org.jboss.netty.channel.Channels.fireExceptionCaught(r1, r3);	 Catch:{ all -> 0x0013 }
        r1 = r5.ctx;	 Catch:{ all -> 0x0013 }
        r3 = r5.ctx;	 Catch:{ all -> 0x0013 }
        r3 = r3.getChannel();	 Catch:{ all -> 0x0013 }
        r3 = org.jboss.netty.channel.Channels.succeededFuture(r3);	 Catch:{ all -> 0x0013 }
        org.jboss.netty.channel.Channels.close(r1, r3);	 Catch:{ all -> 0x0013 }
        goto L_0x003a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.ssl.SslHandler.handleRenegotiation(javax.net.ssl.SSLEngineResult$HandshakeStatus):void");
    }

    private void runDelegatedTasks() {
        while (true) {
            synchronized (this.handshakeLock) {
                Runnable task = this.engine.getDelegatedTask();
            }
            if (task != null) {
                task.run();
            } else {
                return;
            }
        }
    }

    private boolean setHandshakeSuccessIfStillHandshaking(Channel channel) {
        if (!this.handshaking || this.handshakeFuture.isDone()) {
            return false;
        }
        setHandshakeSuccess(channel);
        return true;
    }

    private void setHandshakeSuccess(Channel channel) {
        synchronized (this.handshakeLock) {
            this.handshaking = false;
            this.handshaken = true;
            if (this.handshakeFuture == null) {
                this.handshakeFuture = Channels.future(channel);
            }
            cancelHandshakeTimeout();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(channel + " HANDSHAKEN: " + this.engine.getSession().getCipherSuite());
        }
        this.handshakeFuture.setSuccess();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setHandshakeFailure(org.jboss.netty.channel.Channel r5, javax.net.ssl.SSLException r6) {
        /*
        r4 = this;
        r2 = r4.handshakeLock;
        monitor-enter(r2);
        r1 = r4.handshaking;	 Catch:{ all -> 0x004b }
        if (r1 != 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r2);	 Catch:{ all -> 0x004b }
    L_0x0008:
        return;
    L_0x0009:
        r1 = 0;
        r4.handshaking = r1;	 Catch:{ all -> 0x004b }
        r1 = 0;
        r4.handshaken = r1;	 Catch:{ all -> 0x004b }
        r1 = r4.handshakeFuture;	 Catch:{ all -> 0x004b }
        if (r1 != 0) goto L_0x0019;
    L_0x0013:
        r1 = org.jboss.netty.channel.Channels.future(r5);	 Catch:{ all -> 0x004b }
        r4.handshakeFuture = r1;	 Catch:{ all -> 0x004b }
    L_0x0019:
        r4.cancelHandshakeTimeout();	 Catch:{ all -> 0x004b }
        r1 = r4.engine;	 Catch:{ all -> 0x004b }
        r1.closeOutbound();	 Catch:{ all -> 0x004b }
        r1 = r4.engine;	 Catch:{ SSLException -> 0x003a }
        r1.closeInbound();	 Catch:{ SSLException -> 0x003a }
    L_0x0026:
        monitor-exit(r2);	 Catch:{ all -> 0x004b }
        r1 = r4.handshakeFuture;
        r1.setFailure(r6);
        r1 = r4.closeOnSslException;
        if (r1 == 0) goto L_0x0008;
    L_0x0030:
        r1 = r4.ctx;
        r2 = org.jboss.netty.channel.Channels.future(r5);
        org.jboss.netty.channel.Channels.close(r1, r2);
        goto L_0x0008;
    L_0x003a:
        r0 = move-exception;
        r1 = logger;	 Catch:{ all -> 0x004b }
        r1 = r1.isDebugEnabled();	 Catch:{ all -> 0x004b }
        if (r1 == 0) goto L_0x0026;
    L_0x0043:
        r1 = logger;	 Catch:{ all -> 0x004b }
        r3 = "SSLEngine.closeInbound() raised an exception after a handshake failure.";
        r1.debug(r3, r0);	 Catch:{ all -> 0x004b }
        goto L_0x0026;
    L_0x004b:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x004b }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.ssl.SslHandler.setHandshakeFailure(org.jboss.netty.channel.Channel, javax.net.ssl.SSLException):void");
    }

    private void closeOutboundAndChannel(final ChannelHandlerContext context, final ChannelStateEvent e) {
        if (!e.getChannel().isConnected()) {
            context.sendDownstream(e);
        } else if (CLOSED_OUTBOUND_AND_CHANNEL_UPDATER.compareAndSet(this, 0, 1)) {
            boolean passthrough = true;
            try {
                unwrapNonAppData(this.ctx, e.getChannel(), false);
            } catch (SSLException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to unwrap before sending a close_notify message", ex);
                }
            } catch (Throwable th) {
                if (1 != null) {
                    context.sendDownstream(e);
                }
            }
            if (!this.engine.isOutboundDone() && SENT_CLOSE_NOTIFY_UPDATER.compareAndSet(this, 0, 1)) {
                this.engine.closeOutbound();
                try {
                    wrapNonAppData(context, e.getChannel()).addListener(new ClosingChannelFutureListener(context, e));
                    passthrough = false;
                } catch (SSLException ex2) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to encode a close_notify message", ex2);
                    }
                }
            }
            if (passthrough) {
                context.sendDownstream(e);
            }
        } else {
            e.getChannel().getCloseFuture().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    context.sendDownstream(e);
                }
            });
        }
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
        super.beforeAdd(ctx);
        this.ctx = ctx;
    }

    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
        closeEngine();
        Throwable cause = null;
        while (true) {
            PendingWrite pw = (PendingWrite) this.pendingUnencryptedWrites.poll();
            if (pw == null) {
                break;
            }
            if (cause == null) {
                cause = new IOException("Unable to write data");
            }
            pw.future.setFailure(cause);
        }
        while (true) {
            MessageEvent ev = (MessageEvent) this.pendingEncryptedWrites.poll();
            if (ev == null) {
                break;
            }
            if (cause == null) {
                cause = new IOException("Unable to write data");
            }
            ev.getFuture().setFailure(cause);
        }
        if (cause != null) {
            Channels.fireExceptionCaughtLater(ctx, cause);
        }
    }

    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        if (this.issueHandshake) {
            handshake().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ctx.sendUpstream(e);
                    }
                }
            });
        } else {
            super.channelConnected(ctx, e);
        }
    }

    public void channelClosed(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ctx.getPipeline().execute(new Runnable() {
            public void run() {
                Throwable th;
                if (SslHandler.this.pendingUnencryptedWritesLock.tryLock()) {
                    List<ChannelFuture> futures;
                    List<ChannelFuture> futures2 = null;
                    while (true) {
                        PendingWrite pw = (PendingWrite) SslHandler.this.pendingUnencryptedWrites.poll();
                        if (pw == null) {
                            break;
                        }
                        if (futures2 == null) {
                            try {
                                futures = new ArrayList();
                            } catch (Throwable th2) {
                                th = th2;
                                futures = futures2;
                            }
                        } else {
                            futures = futures2;
                        }
                        try {
                            futures.add(pw.future);
                            futures2 = futures;
                        } catch (Throwable th3) {
                            th = th3;
                        }
                    }
                    while (true) {
                        MessageEvent ev = (MessageEvent) SslHandler.this.pendingEncryptedWrites.poll();
                        if (ev == null) {
                            break;
                        }
                        if (futures2 == null) {
                            futures = new ArrayList();
                        } else {
                            futures = futures2;
                        }
                        futures.add(ev.getFuture());
                        futures2 = futures;
                    }
                    SslHandler.this.pendingUnencryptedWritesLock.unlock();
                    if (futures2 != null) {
                        Throwable cause = new ClosedChannelException();
                        int size = futures2.size();
                        for (int i = 0; i < size; i++) {
                            ((ChannelFuture) futures2.get(i)).setFailure(cause);
                        }
                        Channels.fireExceptionCaught(ctx, cause);
                        return;
                    }
                    return;
                }
                return;
                SslHandler.this.pendingUnencryptedWritesLock.unlock();
                throw th;
            }
        });
        super.channelClosed(ctx, e);
    }
}
