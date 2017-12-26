package org.jboss.netty.channel.socket.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.Executor;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;

public class NioDatagramWorker extends AbstractNioWorker {
    private final SocketReceiveBufferAllocator bufferAllocator = new SocketReceiveBufferAllocator();

    private final class ChannelRegistionTask implements Runnable {
        private final NioDatagramChannel channel;
        private final ChannelFuture future;

        ChannelRegistionTask(NioDatagramChannel channel, ChannelFuture future) {
            this.channel = channel;
            this.future = future;
        }

        public void run() {
            if (this.channel.getLocalAddress() == null) {
                if (this.future != null) {
                    this.future.setFailure(new ClosedChannelException());
                }
                NioDatagramWorker.this.close(this.channel, Channels.succeededFuture(this.channel));
                return;
            }
            try {
                this.channel.getDatagramChannel().register(NioDatagramWorker.this.selector, this.channel.getInternalInterestOps(), this.channel);
                if (this.future != null) {
                    this.future.setSuccess();
                }
            } catch (IOException e) {
                if (this.future != null) {
                    this.future.setFailure(e);
                }
                NioDatagramWorker.this.close(this.channel, Channels.succeededFuture(this.channel));
                if (!(e instanceof ClosedChannelException)) {
                    throw new ChannelException("Failed to register a socket to the selector.", e);
                }
            }
        }
    }

    public /* bridge */ /* synthetic */ void executeInIoThread(Runnable x0) {
        super.executeInIoThread(x0);
    }

    public /* bridge */ /* synthetic */ void executeInIoThread(Runnable x0, boolean x1) {
        super.executeInIoThread(x0, x1);
    }

    public /* bridge */ /* synthetic */ void rebuildSelector() {
        super.rebuildSelector();
    }

    public /* bridge */ /* synthetic */ void register(Channel x0, ChannelFuture x1) {
        super.register(x0, x1);
    }

    public /* bridge */ /* synthetic */ void shutdown() {
        super.shutdown();
    }

    NioDatagramWorker(Executor executor) {
        super(executor);
    }

    protected boolean read(SelectionKey key) {
        Channel channel = (NioDatagramChannel) key.attachment();
        ReceiveBufferSizePredictor predictor = channel.getConfig().getReceiveBufferSizePredictor();
        ChannelBufferFactory bufferFactory = channel.getConfig().getBufferFactory();
        DatagramChannel nioChannel = (DatagramChannel) key.channel();
        ByteBuffer byteBuffer = this.bufferAllocator.get(predictor.nextReceiveBufferSize()).order(bufferFactory.getDefaultOrder());
        boolean failure = true;
        SocketAddress remoteAddress = null;
        try {
            remoteAddress = nioChannel.receive(byteBuffer);
            failure = false;
        } catch (ClosedChannelException e) {
        } catch (Throwable t) {
            Channels.fireExceptionCaught(channel, t);
        }
        if (remoteAddress != null) {
            byteBuffer.flip();
            int readBytes = byteBuffer.remaining();
            if (readBytes > 0) {
                predictor.previousReceiveBufferSize(readBytes);
                Object buffer = bufferFactory.getBuffer(readBytes);
                buffer.setBytes(0, byteBuffer);
                buffer.writerIndex(readBytes);
                predictor.previousReceiveBufferSize(readBytes);
                Channels.fireMessageReceived(channel, buffer, remoteAddress);
            }
        }
        if (!failure) {
            return true;
        }
        key.cancel();
        close(channel, Channels.succeededFuture(channel));
        return false;
    }

    protected boolean scheduleWriteIfNecessary(AbstractNioChannel<?> channel) {
        Thread workerThread = this.thread;
        if (workerThread != null && Thread.currentThread() == workerThread) {
            return false;
        }
        if (channel.writeTaskInTaskQueue.compareAndSet(false, true)) {
            registerTask(channel.writeTask);
        }
        return true;
    }

    static void disconnect(NioDatagramChannel channel, ChannelFuture future) {
        boolean connected = channel.isConnected();
        boolean iothread = AbstractNioWorker.isIoThread(channel);
        try {
            channel.getDatagramChannel().disconnect();
            future.setSuccess();
            if (!connected) {
                return;
            }
            if (iothread) {
                Channels.fireChannelDisconnected((Channel) channel);
            } else {
                Channels.fireChannelDisconnectedLater(channel);
            }
        } catch (Throwable t) {
            future.setFailure(t);
            if (iothread) {
                Channels.fireExceptionCaught((Channel) channel, t);
            } else {
                Channels.fireExceptionCaughtLater((Channel) channel, t);
            }
        }
    }

    protected Runnable createRegisterTask(Channel channel, ChannelFuture future) {
        return new ChannelRegistionTask((NioDatagramChannel) channel, future);
    }

    public void writeFromUserCode(AbstractNioChannel<?> channel) {
        if (!channel.isBound()) {
            AbstractNioWorker.cleanUpWriteBuffer(channel);
        } else if (!scheduleWriteIfNecessary(channel) && !channel.writeSuspended && !channel.inWriteNowLoop) {
            write0(channel);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void write0(org.jboss.netty.channel.socket.nio.AbstractNioChannel<?> r25) {
        /*
        r24 = this;
        r4 = 0;
        r13 = 0;
        r18 = 0;
        r0 = r24;
        r14 = r0.sendBufferPool;
        r20 = r25;
        r20 = (org.jboss.netty.channel.socket.nio.NioDatagramChannel) r20;
        r6 = r20.getDatagramChannel();
        r0 = r25;
        r0 = r0.writeBufferQueue;
        r16 = r0;
        r20 = r25.getConfig();
        r17 = r20.getWriteSpinCount();
        r0 = r25;
        r0 = r0.writeLock;
        r21 = r0;
        monitor-enter(r21);
        r20 = 1;
        r0 = r20;
        r1 = r25;
        r1.inWriteNowLoop = r0;	 Catch:{ all -> 0x0107 }
    L_0x002d:
        r0 = r25;
        r7 = r0.currentWriteEvent;	 Catch:{ all -> 0x0107 }
        if (r7 != 0) goto L_0x00b0;
    L_0x0033:
        r7 = r16.poll();	 Catch:{ all -> 0x0107 }
        r7 = (org.jboss.netty.channel.MessageEvent) r7;	 Catch:{ all -> 0x0107 }
        r0 = r25;
        r0.currentWriteEvent = r7;	 Catch:{ all -> 0x0107 }
        if (r7 != 0) goto L_0x005e;
    L_0x003f:
        r13 = 1;
        r20 = 0;
        r0 = r20;
        r1 = r25;
        r1.writeSuspended = r0;	 Catch:{ all -> 0x0107 }
    L_0x0048:
        r20 = 0;
        r0 = r20;
        r1 = r25;
        r1.inWriteNowLoop = r0;	 Catch:{ all -> 0x0107 }
        if (r4 == 0) goto L_0x010a;
    L_0x0052:
        r24.setOpWrite(r25);	 Catch:{ all -> 0x0107 }
    L_0x0055:
        monitor-exit(r21);	 Catch:{ all -> 0x0107 }
        r0 = r25;
        r1 = r18;
        org.jboss.netty.channel.Channels.fireWriteComplete(r0, r1);
        return;
    L_0x005e:
        r20 = r7.getMessage();	 Catch:{ all -> 0x0107 }
        r0 = r20;
        r5 = r14.acquire(r0);	 Catch:{ all -> 0x0107 }
        r0 = r25;
        r0.currentWriteBuffer = r5;	 Catch:{ all -> 0x0107 }
    L_0x006c:
        r10 = 0;
        r12 = r7.getRemoteAddress();	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        if (r12 != 0) goto L_0x00be;
    L_0x0074:
        r9 = r17;
    L_0x0076:
        if (r9 <= 0) goto L_0x0084;
    L_0x0078:
        r10 = r5.transferTo(r6);	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        r22 = 0;
        r20 = (r10 > r22 ? 1 : (r10 == r22 ? 0 : -1));
        if (r20 == 0) goto L_0x00b5;
    L_0x0082:
        r18 = r18 + r10;
    L_0x0084:
        r22 = 0;
        r20 = (r10 > r22 ? 1 : (r10 == r22 ? 0 : -1));
        if (r20 > 0) goto L_0x0090;
    L_0x008a:
        r20 = r5.finished();	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        if (r20 == 0) goto L_0x00d8;
    L_0x0090:
        r5.release();	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        r8 = r7.getFuture();	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        r20 = 0;
        r0 = r20;
        r1 = r25;
        r1.currentWriteEvent = r0;	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        r20 = 0;
        r0 = r20;
        r1 = r25;
        r1.currentWriteBuffer = r0;	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        r7 = 0;
        r5 = 0;
        r8.setSuccess();	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        goto L_0x002d;
    L_0x00ad:
        r20 = move-exception;
        goto L_0x002d;
    L_0x00b0:
        r0 = r25;
        r5 = r0.currentWriteBuffer;	 Catch:{ all -> 0x0107 }
        goto L_0x006c;
    L_0x00b5:
        r20 = r5.finished();	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        if (r20 != 0) goto L_0x0084;
    L_0x00bb:
        r9 = r9 + -1;
        goto L_0x0076;
    L_0x00be:
        r9 = r17;
    L_0x00c0:
        if (r9 <= 0) goto L_0x0084;
    L_0x00c2:
        r10 = r5.transferTo(r6, r12);	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        r22 = 0;
        r20 = (r10 > r22 ? 1 : (r10 == r22 ? 0 : -1));
        if (r20 == 0) goto L_0x00cf;
    L_0x00cc:
        r18 = r18 + r10;
        goto L_0x0084;
    L_0x00cf:
        r20 = r5.finished();	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        if (r20 != 0) goto L_0x0084;
    L_0x00d5:
        r9 = r9 + -1;
        goto L_0x00c0;
    L_0x00d8:
        r4 = 1;
        r20 = 1;
        r0 = r20;
        r1 = r25;
        r1.writeSuspended = r0;	 Catch:{ AsynchronousCloseException -> 0x00ad, Throwable -> 0x00e3 }
        goto L_0x0048;
    L_0x00e3:
        r15 = move-exception;
        r5.release();	 Catch:{ all -> 0x0107 }
        r8 = r7.getFuture();	 Catch:{ all -> 0x0107 }
        r20 = 0;
        r0 = r20;
        r1 = r25;
        r1.currentWriteEvent = r0;	 Catch:{ all -> 0x0107 }
        r20 = 0;
        r0 = r20;
        r1 = r25;
        r1.currentWriteBuffer = r0;	 Catch:{ all -> 0x0107 }
        r5 = 0;
        r7 = 0;
        r8.setFailure(r15);	 Catch:{ all -> 0x0107 }
        r0 = r25;
        org.jboss.netty.channel.Channels.fireExceptionCaught(r0, r15);	 Catch:{ all -> 0x0107 }
        goto L_0x002d;
    L_0x0107:
        r20 = move-exception;
        monitor-exit(r21);	 Catch:{ all -> 0x0107 }
        throw r20;
    L_0x010a:
        if (r13 == 0) goto L_0x0055;
    L_0x010c:
        r24.clearOpWrite(r25);	 Catch:{ all -> 0x0107 }
        goto L_0x0055;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.channel.socket.nio.NioDatagramWorker.write0(org.jboss.netty.channel.socket.nio.AbstractNioChannel):void");
    }

    public void run() {
        super.run();
        this.bufferAllocator.releaseExternalResources();
    }
}
