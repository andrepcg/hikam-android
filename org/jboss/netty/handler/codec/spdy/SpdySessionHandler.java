package org.jboss.netty.handler.codec.spdy;

import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class SpdySessionHandler extends SimpleChannelUpstreamHandler implements ChannelDownstreamHandler {
    private static final int DEFAULT_MAX_CONCURRENT_STREAMS = Integer.MAX_VALUE;
    private static final int DEFAULT_WINDOW_SIZE = 65536;
    private static final SpdyProtocolException PROTOCOL_EXCEPTION = new SpdyProtocolException();
    private volatile ChannelFutureListener closeSessionFutureListener;
    private final Object flowControlLock = new Object();
    private volatile int initialReceiveWindowSize = 65536;
    private volatile int initialSendWindowSize = 65536;
    private volatile int initialSessionReceiveWindowSize = 65536;
    private volatile int lastGoodStreamId;
    private volatile int localConcurrentStreams = Integer.MAX_VALUE;
    private final int minorVersion;
    private final AtomicInteger pings = new AtomicInteger();
    private volatile boolean receivedGoAwayFrame;
    private volatile int remoteConcurrentStreams = Integer.MAX_VALUE;
    private volatile boolean sentGoAwayFrame;
    private final boolean server;
    private final SpdySession spdySession = new SpdySession(this.initialSendWindowSize, this.initialReceiveWindowSize);

    private static final class ClosingChannelFutureListener implements ChannelFutureListener {
        private final ChannelHandlerContext ctx;
        private final ChannelStateEvent f694e;

        ClosingChannelFutureListener(ChannelHandlerContext ctx, ChannelStateEvent e) {
            this.ctx = ctx;
            this.f694e = e;
        }

        public void operationComplete(ChannelFuture sentGoAwayFuture) throws Exception {
            if (sentGoAwayFuture.getCause() instanceof ClosedChannelException) {
                this.f694e.getFuture().setSuccess();
            } else {
                Channels.close(this.ctx, this.f694e.getFuture());
            }
        }
    }

    public SpdySessionHandler(SpdyVersion spdyVersion, boolean server) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        this.server = server;
        this.minorVersion = spdyVersion.getMinorVersion();
    }

    public void setSessionReceiveWindowSize(int sessionReceiveWindowSize) {
        if (sessionReceiveWindowSize < 0) {
            throw new IllegalArgumentException("sessionReceiveWindowSize");
        }
        this.initialSessionReceiveWindowSize = sessionReceiveWindowSize;
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        SpdyDataFrame msg = e.getMessage();
        int streamId;
        int deltaWindowSize;
        if (msg instanceof SpdyDataFrame) {
            SpdyDataFrame spdyDataFrame = msg;
            streamId = spdyDataFrame.getStreamId();
            deltaWindowSize = spdyDataFrame.getData().readableBytes() * -1;
            int newSessionWindowSize = this.spdySession.updateReceiveWindowSize(0, deltaWindowSize);
            if (newSessionWindowSize < 0) {
                issueSessionError(ctx, e.getChannel(), e.getRemoteAddress(), SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
            SpdyWindowUpdateFrame defaultSpdyWindowUpdateFrame;
            if (newSessionWindowSize <= this.initialSessionReceiveWindowSize / 2) {
                int sessionDeltaWindowSize = this.initialSessionReceiveWindowSize - newSessionWindowSize;
                this.spdySession.updateReceiveWindowSize(0, sessionDeltaWindowSize);
                defaultSpdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(0, sessionDeltaWindowSize);
                Channels.write(ctx, Channels.future(e.getChannel()), defaultSpdyWindowUpdateFrame, e.getRemoteAddress());
            }
            if (!this.spdySession.isActiveStream(streamId)) {
                if (streamId <= this.lastGoodStreamId) {
                    issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                    return;
                } else if (!this.sentGoAwayFrame) {
                    issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.INVALID_STREAM);
                    return;
                } else {
                    return;
                }
            } else if (this.spdySession.isRemoteSideClosed(streamId)) {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.STREAM_ALREADY_CLOSED);
                return;
            } else if (isRemoteInitiatedId(streamId) || this.spdySession.hasReceivedReply(streamId)) {
                int newWindowSize = this.spdySession.updateReceiveWindowSize(streamId, deltaWindowSize);
                if (newWindowSize < this.spdySession.getReceiveWindowSizeLowerBound(streamId)) {
                    issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.FLOW_CONTROL_ERROR);
                    return;
                }
                if (newWindowSize < 0) {
                    while (spdyDataFrame.getData().readableBytes() > this.initialReceiveWindowSize) {
                        Object partialDataFrame = new DefaultSpdyDataFrame(streamId);
                        partialDataFrame.setData(spdyDataFrame.getData().readSlice(this.initialReceiveWindowSize));
                        Channels.fireMessageReceived(ctx, partialDataFrame, e.getRemoteAddress());
                    }
                }
                if (newWindowSize <= this.initialReceiveWindowSize / 2 && !spdyDataFrame.isLast()) {
                    int streamDeltaWindowSize = this.initialReceiveWindowSize - newWindowSize;
                    this.spdySession.updateReceiveWindowSize(streamId, streamDeltaWindowSize);
                    defaultSpdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(streamId, streamDeltaWindowSize);
                    Channels.write(ctx, Channels.future(e.getChannel()), defaultSpdyWindowUpdateFrame, e.getRemoteAddress());
                }
                if (spdyDataFrame.isLast()) {
                    halfCloseStream(streamId, true, e.getFuture());
                }
            } else {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }
        } else if (msg instanceof SpdySynStreamFrame) {
            SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame) msg;
            streamId = spdySynStreamFrame.getStreamId();
            if (spdySynStreamFrame.isInvalid() || !isRemoteInitiatedId(streamId) || this.spdySession.isActiveStream(streamId)) {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }
            if (streamId <= this.lastGoodStreamId) {
                issueSessionError(ctx, e.getChannel(), e.getRemoteAddress(), SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
            if (!acceptStream(streamId, spdySynStreamFrame.getPriority(), spdySynStreamFrame.isLast(), spdySynStreamFrame.isUnidirectional())) {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.REFUSED_STREAM);
                return;
            }
        } else if (msg instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame) msg;
            streamId = spdySynReplyFrame.getStreamId();
            if (spdySynReplyFrame.isInvalid() || isRemoteInitiatedId(streamId) || this.spdySession.isRemoteSideClosed(streamId)) {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.INVALID_STREAM);
                return;
            } else if (this.spdySession.hasReceivedReply(streamId)) {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.STREAM_IN_USE);
                return;
            } else {
                this.spdySession.receivedReply(streamId);
                if (spdySynReplyFrame.isLast()) {
                    halfCloseStream(streamId, true, e.getFuture());
                }
            }
        } else if (msg instanceof SpdyRstStreamFrame) {
            removeStream(((SpdyRstStreamFrame) msg).getStreamId(), e.getFuture());
        } else if (msg instanceof SpdySettingsFrame) {
            SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame) msg;
            int settingsMinorVersion = spdySettingsFrame.getValue(0);
            if (settingsMinorVersion < 0 || settingsMinorVersion == this.minorVersion) {
                int newConcurrentStreams = spdySettingsFrame.getValue(4);
                if (newConcurrentStreams >= 0) {
                    this.remoteConcurrentStreams = newConcurrentStreams;
                }
                if (spdySettingsFrame.isPersisted(7)) {
                    spdySettingsFrame.removeValue(7);
                }
                spdySettingsFrame.setPersistValue(7, false);
                int newInitialWindowSize = spdySettingsFrame.getValue(7);
                if (newInitialWindowSize >= 0) {
                    updateInitialSendWindowSize(newInitialWindowSize);
                }
            } else {
                issueSessionError(ctx, e.getChannel(), e.getRemoteAddress(), SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
        } else if (msg instanceof SpdyPingFrame) {
            SpdyPingFrame spdyPingFrame = (SpdyPingFrame) msg;
            if (isRemoteInitiatedId(spdyPingFrame.getId())) {
                Channels.write(ctx, Channels.future(e.getChannel()), spdyPingFrame, e.getRemoteAddress());
                return;
            } else if (this.pings.get() != 0) {
                this.pings.getAndDecrement();
            } else {
                return;
            }
        } else if (msg instanceof SpdyGoAwayFrame) {
            this.receivedGoAwayFrame = true;
        } else if (msg instanceof SpdyHeadersFrame) {
            SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame) msg;
            streamId = spdyHeadersFrame.getStreamId();
            if (spdyHeadersFrame.isInvalid()) {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            } else if (this.spdySession.isRemoteSideClosed(streamId)) {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.INVALID_STREAM);
                return;
            } else if (spdyHeadersFrame.isLast()) {
                halfCloseStream(streamId, true, e.getFuture());
            }
        } else if (msg instanceof SpdyWindowUpdateFrame) {
            SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame) msg;
            streamId = spdyWindowUpdateFrame.getStreamId();
            deltaWindowSize = spdyWindowUpdateFrame.getDeltaWindowSize();
            if (streamId != 0 && this.spdySession.isLocalSideClosed(streamId)) {
                return;
            }
            if (this.spdySession.getSendWindowSize(streamId) <= Integer.MAX_VALUE - deltaWindowSize) {
                updateSendWindowSize(ctx, streamId, deltaWindowSize);
                return;
            } else if (streamId == 0) {
                issueSessionError(ctx, e.getChannel(), e.getRemoteAddress(), SpdySessionStatus.PROTOCOL_ERROR);
                return;
            } else {
                issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.FLOW_CONTROL_ERROR);
                return;
            }
        }
        super.messageReceived(ctx, e);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (e.getCause() instanceof SpdyProtocolException) {
            issueSessionError(ctx, e.getChannel(), null, SpdySessionStatus.PROTOCOL_ERROR);
        }
        super.exceptionCaught(ctx, e);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleDownstream(org.jboss.netty.channel.ChannelHandlerContext r32, org.jboss.netty.channel.ChannelEvent r33) throws java.lang.Exception {
        /*
        r31 = this;
        r0 = r33;
        r0 = r0 instanceof org.jboss.netty.channel.ChannelStateEvent;
        r27 = r0;
        if (r27 == 0) goto L_0x001b;
    L_0x0008:
        r6 = r33;
        r6 = (org.jboss.netty.channel.ChannelStateEvent) r6;
        r27 = org.jboss.netty.handler.codec.spdy.SpdySessionHandler.C08565.$SwitchMap$org$jboss$netty$channel$ChannelState;
        r28 = r6.getState();
        r28 = r28.ordinal();
        r27 = r27[r28];
        switch(r27) {
            case 1: goto L_0x0027;
            case 2: goto L_0x0027;
            case 3: goto L_0x0027;
            default: goto L_0x001b;
        };
    L_0x001b:
        r0 = r33;
        r0 = r0 instanceof org.jboss.netty.channel.MessageEvent;
        r27 = r0;
        if (r27 != 0) goto L_0x0041;
    L_0x0023:
        r32.sendDownstream(r33);
    L_0x0026:
        return;
    L_0x0027:
        r27 = java.lang.Boolean.FALSE;
        r28 = r6.getValue();
        r27 = r27.equals(r28);
        if (r27 != 0) goto L_0x0039;
    L_0x0033:
        r27 = r6.getValue();
        if (r27 != 0) goto L_0x001b;
    L_0x0039:
        r0 = r31;
        r1 = r32;
        r0.sendGoAwayFrame(r1, r6);
        goto L_0x0026;
    L_0x0041:
        r6 = r33;
        r6 = (org.jboss.netty.channel.MessageEvent) r6;
        r8 = r6.getMessage();
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdyDataFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x0187;
    L_0x004f:
        r18 = r8;
        r18 = (org.jboss.netty.handler.codec.spdy.SpdyDataFrame) r18;
        r25 = r18.getStreamId();
        r0 = r31;
        r0 = r0.spdySession;
        r27 = r0;
        r0 = r27;
        r1 = r25;
        r27 = r0.isLocalSideClosed(r1);
        if (r27 == 0) goto L_0x0071;
    L_0x0067:
        r27 = r6.getFuture();
        r28 = PROTOCOL_EXCEPTION;
        r27.setFailure(r28);
        goto L_0x0026;
    L_0x0071:
        r0 = r31;
        r0 = r0.flowControlLock;
        r28 = r0;
        monitor-enter(r28);
        r27 = r18.getData();	 Catch:{ all -> 0x00b4 }
        r5 = r27.readableBytes();	 Catch:{ all -> 0x00b4 }
        r0 = r31;
        r0 = r0.spdySession;	 Catch:{ all -> 0x00b4 }
        r27 = r0;
        r0 = r27;
        r1 = r25;
        r15 = r0.getSendWindowSize(r1);	 Catch:{ all -> 0x00b4 }
        r0 = r31;
        r0 = r0.spdySession;	 Catch:{ all -> 0x00b4 }
        r27 = r0;
        r29 = 0;
        r0 = r27;
        r1 = r29;
        r16 = r0.getSendWindowSize(r1);	 Catch:{ all -> 0x00b4 }
        r15 = java.lang.Math.min(r15, r16);	 Catch:{ all -> 0x00b4 }
        if (r15 > 0) goto L_0x00b7;
    L_0x00a4:
        r0 = r31;
        r0 = r0.spdySession;	 Catch:{ all -> 0x00b4 }
        r27 = r0;
        r0 = r27;
        r1 = r25;
        r0.putPendingWrite(r1, r6);	 Catch:{ all -> 0x00b4 }
        monitor-exit(r28);	 Catch:{ all -> 0x00b4 }
        goto L_0x0026;
    L_0x00b4:
        r27 = move-exception;
        monitor-exit(r28);	 Catch:{ all -> 0x00b4 }
        throw r27;
    L_0x00b7:
        if (r15 >= r5) goto L_0x012c;
    L_0x00b9:
        r0 = r31;
        r0 = r0.spdySession;	 Catch:{ all -> 0x00b4 }
        r27 = r0;
        r29 = r15 * -1;
        r0 = r27;
        r1 = r25;
        r2 = r29;
        r0.updateSendWindowSize(r1, r2);	 Catch:{ all -> 0x00b4 }
        r0 = r31;
        r0 = r0.spdySession;	 Catch:{ all -> 0x00b4 }
        r27 = r0;
        r29 = 0;
        r30 = r15 * -1;
        r0 = r27;
        r1 = r29;
        r2 = r30;
        r0.updateSendWindowSize(r1, r2);	 Catch:{ all -> 0x00b4 }
        r11 = new org.jboss.netty.handler.codec.spdy.DefaultSpdyDataFrame;	 Catch:{ all -> 0x00b4 }
        r0 = r25;
        r11.<init>(r0);	 Catch:{ all -> 0x00b4 }
        r27 = r18.getData();	 Catch:{ all -> 0x00b4 }
        r0 = r27;
        r27 = r0.readSlice(r15);	 Catch:{ all -> 0x00b4 }
        r0 = r27;
        r11.setData(r0);	 Catch:{ all -> 0x00b4 }
        r0 = r31;
        r0 = r0.spdySession;	 Catch:{ all -> 0x00b4 }
        r27 = r0;
        r0 = r27;
        r1 = r25;
        r0.putPendingWrite(r1, r6);	 Catch:{ all -> 0x00b4 }
        r27 = r6.getChannel();	 Catch:{ all -> 0x00b4 }
        r26 = org.jboss.netty.channel.Channels.future(r27);	 Catch:{ all -> 0x00b4 }
        r13 = r6.getRemoteAddress();	 Catch:{ all -> 0x00b4 }
        r4 = r32;
        r27 = r6.getFuture();	 Catch:{ all -> 0x00b4 }
        r29 = new org.jboss.netty.handler.codec.spdy.SpdySessionHandler$1;	 Catch:{ all -> 0x00b4 }
        r0 = r29;
        r1 = r31;
        r0.<init>(r4, r13);	 Catch:{ all -> 0x00b4 }
        r0 = r27;
        r1 = r29;
        r0.addListener(r1);	 Catch:{ all -> 0x00b4 }
        r0 = r32;
        r1 = r26;
        org.jboss.netty.channel.Channels.write(r0, r1, r11, r13);	 Catch:{ all -> 0x00b4 }
        monitor-exit(r28);	 Catch:{ all -> 0x00b4 }
        goto L_0x0026;
    L_0x012c:
        r0 = r31;
        r0 = r0.spdySession;	 Catch:{ all -> 0x00b4 }
        r27 = r0;
        r29 = r5 * -1;
        r0 = r27;
        r1 = r25;
        r2 = r29;
        r0.updateSendWindowSize(r1, r2);	 Catch:{ all -> 0x00b4 }
        r0 = r31;
        r0 = r0.spdySession;	 Catch:{ all -> 0x00b4 }
        r27 = r0;
        r29 = 0;
        r30 = r5 * -1;
        r0 = r27;
        r1 = r29;
        r2 = r30;
        r0.updateSendWindowSize(r1, r2);	 Catch:{ all -> 0x00b4 }
        r13 = r6.getRemoteAddress();	 Catch:{ all -> 0x00b4 }
        r4 = r32;
        r27 = r6.getFuture();	 Catch:{ all -> 0x00b4 }
        r29 = new org.jboss.netty.handler.codec.spdy.SpdySessionHandler$2;	 Catch:{ all -> 0x00b4 }
        r0 = r29;
        r1 = r31;
        r0.<init>(r4, r13);	 Catch:{ all -> 0x00b4 }
        r0 = r27;
        r1 = r29;
        r0.addListener(r1);	 Catch:{ all -> 0x00b4 }
        monitor-exit(r28);	 Catch:{ all -> 0x00b4 }
        r27 = r18.isLast();
        if (r27 == 0) goto L_0x0182;
    L_0x0171:
        r27 = 0;
        r28 = r6.getFuture();
        r0 = r31;
        r1 = r25;
        r2 = r27;
        r3 = r28;
        r0.halfCloseStream(r1, r2, r3);
    L_0x0182:
        r32.sendDownstream(r33);
        goto L_0x0026;
    L_0x0187:
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdySynStreamFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x01cb;
    L_0x018d:
        r24 = r8;
        r24 = (org.jboss.netty.handler.codec.spdy.SpdySynStreamFrame) r24;
        r25 = r24.getStreamId();
        r0 = r31;
        r1 = r25;
        r27 = r0.isRemoteInitiatedId(r1);
        if (r27 == 0) goto L_0x01aa;
    L_0x019f:
        r27 = r6.getFuture();
        r28 = PROTOCOL_EXCEPTION;
        r27.setFailure(r28);
        goto L_0x0026;
    L_0x01aa:
        r12 = r24.getPriority();
        r14 = r24.isUnidirectional();
        r7 = r24.isLast();
        r0 = r31;
        r1 = r25;
        r27 = r0.acceptStream(r1, r12, r14, r7);
        if (r27 != 0) goto L_0x0182;
    L_0x01c0:
        r27 = r6.getFuture();
        r28 = PROTOCOL_EXCEPTION;
        r27.setFailure(r28);
        goto L_0x0026;
    L_0x01cb:
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdySynReplyFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x0217;
    L_0x01d1:
        r23 = r8;
        r23 = (org.jboss.netty.handler.codec.spdy.SpdySynReplyFrame) r23;
        r25 = r23.getStreamId();
        r0 = r31;
        r1 = r25;
        r27 = r0.isRemoteInitiatedId(r1);
        if (r27 == 0) goto L_0x01f3;
    L_0x01e3:
        r0 = r31;
        r0 = r0.spdySession;
        r27 = r0;
        r0 = r27;
        r1 = r25;
        r27 = r0.isLocalSideClosed(r1);
        if (r27 == 0) goto L_0x01fe;
    L_0x01f3:
        r27 = r6.getFuture();
        r28 = PROTOCOL_EXCEPTION;
        r27.setFailure(r28);
        goto L_0x0026;
    L_0x01fe:
        r27 = r23.isLast();
        if (r27 == 0) goto L_0x0182;
    L_0x0204:
        r27 = 0;
        r28 = r6.getFuture();
        r0 = r31;
        r1 = r25;
        r2 = r27;
        r3 = r28;
        r0.halfCloseStream(r1, r2, r3);
        goto L_0x0182;
    L_0x0217:
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdyRstStreamFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x0234;
    L_0x021d:
        r21 = r8;
        r21 = (org.jboss.netty.handler.codec.spdy.SpdyRstStreamFrame) r21;
        r27 = r21.getStreamId();
        r28 = r6.getFuture();
        r0 = r31;
        r1 = r27;
        r2 = r28;
        r0.removeStream(r1, r2);
        goto L_0x0182;
    L_0x0234:
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdySettingsFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x02a6;
    L_0x023a:
        r22 = r8;
        r22 = (org.jboss.netty.handler.codec.spdy.SpdySettingsFrame) r22;
        r27 = 0;
        r0 = r22;
        r1 = r27;
        r17 = r0.getValue(r1);
        if (r17 < 0) goto L_0x0261;
    L_0x024a:
        r0 = r31;
        r0 = r0.minorVersion;
        r27 = r0;
        r0 = r17;
        r1 = r27;
        if (r0 == r1) goto L_0x0261;
    L_0x0256:
        r27 = r6.getFuture();
        r28 = PROTOCOL_EXCEPTION;
        r27.setFailure(r28);
        goto L_0x0026;
    L_0x0261:
        r27 = 4;
        r0 = r22;
        r1 = r27;
        r9 = r0.getValue(r1);
        if (r9 < 0) goto L_0x0271;
    L_0x026d:
        r0 = r31;
        r0.localConcurrentStreams = r9;
    L_0x0271:
        r27 = 7;
        r0 = r22;
        r1 = r27;
        r27 = r0.isPersisted(r1);
        if (r27 == 0) goto L_0x0286;
    L_0x027d:
        r27 = 7;
        r0 = r22;
        r1 = r27;
        r0.removeValue(r1);
    L_0x0286:
        r27 = 7;
        r28 = 0;
        r0 = r22;
        r1 = r27;
        r2 = r28;
        r0.setPersistValue(r1, r2);
        r27 = 7;
        r0 = r22;
        r1 = r27;
        r10 = r0.getValue(r1);
        if (r10 < 0) goto L_0x0182;
    L_0x029f:
        r0 = r31;
        r0.updateInitialReceiveWindowSize(r10);
        goto L_0x0182;
    L_0x02a6:
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdyPingFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x02ee;
    L_0x02ac:
        r20 = r8;
        r20 = (org.jboss.netty.handler.codec.spdy.SpdyPingFrame) r20;
        r27 = r20.getId();
        r0 = r31;
        r1 = r27;
        r27 = r0.isRemoteInitiatedId(r1);
        if (r27 == 0) goto L_0x02e3;
    L_0x02be:
        r27 = r6.getFuture();
        r28 = new java.lang.IllegalArgumentException;
        r29 = new java.lang.StringBuilder;
        r29.<init>();
        r30 = "invalid PING ID: ";
        r29 = r29.append(r30);
        r30 = r20.getId();
        r29 = r29.append(r30);
        r29 = r29.toString();
        r28.<init>(r29);
        r27.setFailure(r28);
        goto L_0x0026;
    L_0x02e3:
        r0 = r31;
        r0 = r0.pings;
        r27 = r0;
        r27.getAndIncrement();
        goto L_0x0182;
    L_0x02ee:
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdyGoAwayFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x02ff;
    L_0x02f4:
        r27 = r6.getFuture();
        r28 = PROTOCOL_EXCEPTION;
        r27.setFailure(r28);
        goto L_0x0026;
    L_0x02ff:
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdyHeadersFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x0341;
    L_0x0305:
        r19 = r8;
        r19 = (org.jboss.netty.handler.codec.spdy.SpdyHeadersFrame) r19;
        r25 = r19.getStreamId();
        r0 = r31;
        r0 = r0.spdySession;
        r27 = r0;
        r0 = r27;
        r1 = r25;
        r27 = r0.isLocalSideClosed(r1);
        if (r27 == 0) goto L_0x0328;
    L_0x031d:
        r27 = r6.getFuture();
        r28 = PROTOCOL_EXCEPTION;
        r27.setFailure(r28);
        goto L_0x0026;
    L_0x0328:
        r27 = r19.isLast();
        if (r27 == 0) goto L_0x0182;
    L_0x032e:
        r27 = 0;
        r28 = r6.getFuture();
        r0 = r31;
        r1 = r25;
        r2 = r27;
        r3 = r28;
        r0.halfCloseStream(r1, r2, r3);
        goto L_0x0182;
    L_0x0341:
        r0 = r8 instanceof org.jboss.netty.handler.codec.spdy.SpdyWindowUpdateFrame;
        r27 = r0;
        if (r27 == 0) goto L_0x0182;
    L_0x0347:
        r27 = r6.getFuture();
        r28 = PROTOCOL_EXCEPTION;
        r27.setFailure(r28);
        goto L_0x0026;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.codec.spdy.SpdySessionHandler.handleDownstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent):void");
    }

    private void issueSessionError(ChannelHandlerContext ctx, Channel channel, SocketAddress remoteAddress, SpdySessionStatus status) {
        sendGoAwayFrame(ctx, channel, remoteAddress, status).addListener(ChannelFutureListener.CLOSE);
    }

    private void issueStreamError(ChannelHandlerContext ctx, SocketAddress remoteAddress, int streamId, SpdyStreamStatus status) {
        boolean fireMessageReceived = !this.spdySession.isRemoteSideClosed(streamId);
        ChannelFuture future = Channels.future(ctx.getChannel());
        removeStream(streamId, future);
        Object spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, status);
        Channels.write(ctx, future, spdyRstStreamFrame, remoteAddress);
        if (fireMessageReceived) {
            Channels.fireMessageReceived(ctx, spdyRstStreamFrame, remoteAddress);
        }
    }

    private boolean isRemoteInitiatedId(int id) {
        boolean serverId = SpdyCodecUtil.isServerId(id);
        return (this.server && !serverId) || (!this.server && serverId);
    }

    private synchronized void updateInitialSendWindowSize(int newInitialWindowSize) {
        int deltaWindowSize = newInitialWindowSize - this.initialSendWindowSize;
        this.initialSendWindowSize = newInitialWindowSize;
        this.spdySession.updateAllSendWindowSizes(deltaWindowSize);
    }

    private synchronized void updateInitialReceiveWindowSize(int newInitialWindowSize) {
        int deltaWindowSize = newInitialWindowSize - this.initialReceiveWindowSize;
        this.initialReceiveWindowSize = newInitialWindowSize;
        this.spdySession.updateAllReceiveWindowSizes(deltaWindowSize);
    }

    private synchronized boolean acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed) {
        boolean z = false;
        synchronized (this) {
            if (!(this.receivedGoAwayFrame || this.sentGoAwayFrame)) {
                boolean remote = isRemoteInitiatedId(streamId);
                if (this.spdySession.numActiveStreams(remote) < (remote ? this.localConcurrentStreams : this.remoteConcurrentStreams)) {
                    this.spdySession.acceptStream(streamId, priority, remoteSideClosed, localSideClosed, this.initialSendWindowSize, this.initialReceiveWindowSize, remote);
                    if (remote) {
                        this.lastGoodStreamId = streamId;
                    }
                    z = true;
                }
            }
        }
        return z;
    }

    private void halfCloseStream(int streamId, boolean remote, ChannelFuture future) {
        if (remote) {
            this.spdySession.closeRemoteSide(streamId, isRemoteInitiatedId(streamId));
        } else {
            this.spdySession.closeLocalSide(streamId, isRemoteInitiatedId(streamId));
        }
        if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
            future.addListener(this.closeSessionFutureListener);
        }
    }

    private void removeStream(int streamId, ChannelFuture future) {
        this.spdySession.removeStream(streamId, isRemoteInitiatedId(streamId));
        if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
            future.addListener(this.closeSessionFutureListener);
        }
    }

    private void updateSendWindowSize(ChannelHandlerContext ctx, int streamId, int deltaWindowSize) {
        synchronized (this.flowControlLock) {
            int newWindowSize = this.spdySession.updateSendWindowSize(streamId, deltaWindowSize);
            if (streamId != 0) {
                newWindowSize = Math.min(newWindowSize, this.spdySession.getSendWindowSize(0));
            }
            while (newWindowSize > 0) {
                MessageEvent e = this.spdySession.getPendingWrite(streamId);
                if (e == null) {
                    break;
                }
                SpdyDataFrame spdyDataFrame = (SpdyDataFrame) e.getMessage();
                int dataFrameSize = spdyDataFrame.getData().readableBytes();
                int writeStreamId = spdyDataFrame.getStreamId();
                if (streamId == 0) {
                    newWindowSize = Math.min(newWindowSize, this.spdySession.getSendWindowSize(writeStreamId));
                }
                final SocketAddress remoteAddress;
                final ChannelHandlerContext context;
                if (newWindowSize >= dataFrameSize) {
                    this.spdySession.removePendingWrite(writeStreamId);
                    newWindowSize = Math.min(this.spdySession.updateSendWindowSize(writeStreamId, dataFrameSize * -1), this.spdySession.updateSendWindowSize(0, dataFrameSize * -1));
                    remoteAddress = e.getRemoteAddress();
                    context = ctx;
                    e.getFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                SpdySessionHandler.this.issueSessionError(context, future.getChannel(), remoteAddress, SpdySessionStatus.INTERNAL_ERROR);
                            }
                        }
                    });
                    if (spdyDataFrame.isLast()) {
                        halfCloseStream(writeStreamId, false, e.getFuture());
                    }
                    Channels.write(ctx, e.getFuture(), spdyDataFrame, e.getRemoteAddress());
                } else {
                    this.spdySession.updateSendWindowSize(writeStreamId, newWindowSize * -1);
                    this.spdySession.updateSendWindowSize(0, newWindowSize * -1);
                    SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(writeStreamId);
                    partialDataFrame.setData(spdyDataFrame.getData().readSlice(newWindowSize));
                    ChannelFuture writeFuture = Channels.future(e.getChannel());
                    remoteAddress = e.getRemoteAddress();
                    context = ctx;
                    e.getFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                SpdySessionHandler.this.issueSessionError(context, future.getChannel(), remoteAddress, SpdySessionStatus.INTERNAL_ERROR);
                            }
                        }
                    });
                    Channels.write(ctx, writeFuture, partialDataFrame, remoteAddress);
                    newWindowSize = 0;
                }
            }
        }
    }

    private void sendGoAwayFrame(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (e.getChannel().isConnected()) {
            ChannelFuture future = sendGoAwayFrame(ctx, e.getChannel(), null, SpdySessionStatus.OK);
            if (this.spdySession.noActiveStreams()) {
                future.addListener(new ClosingChannelFutureListener(ctx, e));
                return;
            } else {
                this.closeSessionFutureListener = new ClosingChannelFutureListener(ctx, e);
                return;
            }
        }
        ctx.sendDownstream(e);
    }

    private synchronized ChannelFuture sendGoAwayFrame(ChannelHandlerContext ctx, Channel channel, SocketAddress remoteAddress, SpdySessionStatus status) {
        ChannelFuture succeededFuture;
        if (this.sentGoAwayFrame) {
            succeededFuture = Channels.succeededFuture(channel);
        } else {
            this.sentGoAwayFrame = true;
            SpdyGoAwayFrame spdyGoAwayFrame = new DefaultSpdyGoAwayFrame(this.lastGoodStreamId, status);
            succeededFuture = Channels.future(channel);
            Channels.write(ctx, succeededFuture, spdyGoAwayFrame, remoteAddress);
        }
        return succeededFuture;
    }
}
