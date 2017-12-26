package org.jboss.netty.channel.socket.nio;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.Worker;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;

abstract class AbstractNioWorker extends AbstractNioSelector implements Worker {
    protected final SocketSendBufferPool sendBufferPool = new SocketSendBufferPool();

    protected abstract boolean read(SelectionKey selectionKey);

    protected abstract boolean scheduleWriteIfNecessary(AbstractNioChannel<?> abstractNioChannel);

    AbstractNioWorker(Executor executor) {
        super(executor);
    }

    AbstractNioWorker(Executor executor, ThreadNameDeterminer determiner) {
        super(executor, determiner);
    }

    public void executeInIoThread(Runnable task) {
        executeInIoThread(task, false);
    }

    public void executeInIoThread(Runnable task, boolean alwaysAsync) {
        if (alwaysAsync || !isIoThread()) {
            registerTask(task);
        } else {
            task.run();
        }
    }

    protected void close(SelectionKey k) {
        AbstractNioChannel<?> ch = (AbstractNioChannel) k.attachment();
        close(ch, Channels.succeededFuture(ch));
    }

    protected ThreadRenamingRunnable newThreadRenamingRunnable(int id, ThreadNameDeterminer determiner) {
        return new ThreadRenamingRunnable(this, "New I/O worker #" + id, determiner);
    }

    public void run() {
        super.run();
        this.sendBufferPool.releaseExternalResources();
    }

    protected void process(Selector selector) throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        if (!selectedKeys.isEmpty()) {
            Iterator<SelectionKey> i = selectedKeys.iterator();
            while (i.hasNext()) {
                SelectionKey k = (SelectionKey) i.next();
                i.remove();
                try {
                    int readyOps = k.readyOps();
                    if (((readyOps & 1) == 0 && readyOps != 0) || read(k)) {
                        if ((readyOps & 4) != 0) {
                            writeFromSelectorLoop(k);
                        }
                        if (cleanUpCancelledKeys()) {
                            return;
                        }
                    }
                } catch (CancelledKeyException e) {
                    close(k);
                }
            }
        }
    }

    void writeFromUserCode(AbstractNioChannel<?> channel) {
        if (!channel.isConnected()) {
            cleanUpWriteBuffer(channel);
        } else if (!scheduleWriteIfNecessary(channel) && !channel.writeSuspended && !channel.inWriteNowLoop) {
            write0(channel);
        }
    }

    void writeFromTaskLoop(AbstractNioChannel<?> ch) {
        if (!ch.writeSuspended) {
            write0(ch);
        }
    }

    void writeFromSelectorLoop(SelectionKey k) {
        AbstractNioChannel<?> ch = (AbstractNioChannel) k.attachment();
        ch.writeSuspended = false;
        write0(ch);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void write0(org.jboss.netty.channel.socket.nio.AbstractNioChannel<?> r31) {
        /*
        r30 = this;
        r21 = 1;
        r4 = 0;
        r22 = 0;
        r20 = isIoThread(r31);
        r28 = 0;
        r0 = r30;
        r0 = r0.sendBufferPool;
        r23 = r0;
        r0 = r31;
        r0 = r0.channel;
        r16 = r0;
        r16 = (java.nio.channels.WritableByteChannel) r16;
        r0 = r31;
        r0 = r0.writeBufferQueue;
        r25 = r0;
        r8 = r31.getConfig();
        r26 = r8.getWriteSpinCount();
        r14 = 0;
        r0 = r31;
        r0 = r0.writeLock;
        r27 = r0;
        monitor-enter(r27);
        r8 = 1;
        r0 = r31;
        r0.inWriteNowLoop = r8;	 Catch:{ all -> 0x0152 }
        r15 = r14;
    L_0x0035:
        r0 = r31;
        r0 = r0.currentWriteEvent;	 Catch:{ all -> 0x012f }
        r17 = r0;
        r12 = 0;
        r5 = 0;
        if (r17 != 0) goto L_0x00bf;
    L_0x003f:
        r8 = r25.poll();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r0 = r8;
        r0 = (org.jboss.netty.channel.MessageEvent) r0;	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r17 = r0;
        r0 = r17;
        r1 = r31;
        r1.currentWriteEvent = r0;	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        if (r17 != 0) goto L_0x007c;
    L_0x0050:
        r22 = 1;
        r8 = 0;
        r0 = r31;
        r0.writeSuspended = r8;	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
    L_0x0057:
        r8 = 0;
        r0 = r31;
        r0.inWriteNowLoop = r8;	 Catch:{ all -> 0x012f }
        if (r21 == 0) goto L_0x0063;
    L_0x005e:
        if (r4 == 0) goto L_0x0128;
    L_0x0060:
        r30.setOpWrite(r31);	 Catch:{ all -> 0x012f }
    L_0x0063:
        monitor-exit(r27);	 Catch:{ all -> 0x012f }
        if (r15 == 0) goto L_0x0133;
    L_0x0066:
        r19 = r15.iterator();
    L_0x006a:
        r8 = r19.hasNext();
        if (r8 == 0) goto L_0x0133;
    L_0x0070:
        r13 = r19.next();
        r13 = (java.lang.Throwable) r13;
        r0 = r31;
        org.jboss.netty.channel.Channels.fireExceptionCaught(r0, r13);
        goto L_0x006a;
    L_0x007c:
        r5 = r17.getFuture();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r8 = r17.getMessage();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r0 = r23;
        r12 = r0.acquire(r8);	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r0 = r31;
        r0.currentWriteBuffer = r12;	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
    L_0x008e:
        r6 = 0;
        r18 = r26;
    L_0x0092:
        if (r18 <= 0) goto L_0x00a2;
    L_0x0094:
        r0 = r16;
        r6 = r12.transferTo(r0);	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r8 = 0;
        r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r8 == 0) goto L_0x00c8;
    L_0x00a0:
        r28 = r28 + r6;
    L_0x00a2:
        r8 = r12.finished();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        if (r8 == 0) goto L_0x00d1;
    L_0x00a8:
        r12.release();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r8 = 0;
        r0 = r31;
        r0.currentWriteEvent = r8;	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r8 = 0;
        r0 = r31;
        r0.currentWriteBuffer = r8;	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r17 = 0;
        r12 = 0;
        r5.setSuccess();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r14 = r15;
    L_0x00bc:
        r15 = r14;
        goto L_0x0035;
    L_0x00bf:
        r5 = r17.getFuture();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r0 = r31;
        r12 = r0.currentWriteBuffer;	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        goto L_0x008e;
    L_0x00c8:
        r8 = r12.finished();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        if (r8 != 0) goto L_0x00a2;
    L_0x00ce:
        r18 = r18 + -1;
        goto L_0x0092;
    L_0x00d1:
        r4 = 1;
        r8 = 1;
        r0 = r31;
        r0.writeSuspended = r8;	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r8 = 0;
        r8 = (r28 > r8 ? 1 : (r28 == r8 ? 0 : -1));
        if (r8 <= 0) goto L_0x0057;
    L_0x00dd:
        r8 = r12.writtenBytes();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r10 = r12.totalBytes();	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        r5.setProgress(r6, r8, r10);	 Catch:{ AsynchronousCloseException -> 0x00ea, Throwable -> 0x00ed }
        goto L_0x0057;
    L_0x00ea:
        r8 = move-exception;
        r14 = r15;
        goto L_0x00bc;
    L_0x00ed:
        r24 = move-exception;
        if (r12 == 0) goto L_0x00f3;
    L_0x00f0:
        r12.release();	 Catch:{ all -> 0x012f }
    L_0x00f3:
        r8 = 0;
        r0 = r31;
        r0.currentWriteEvent = r8;	 Catch:{ all -> 0x012f }
        r8 = 0;
        r0 = r31;
        r0.currentWriteBuffer = r8;	 Catch:{ all -> 0x012f }
        r12 = 0;
        r17 = 0;
        if (r5 == 0) goto L_0x0107;
    L_0x0102:
        r0 = r24;
        r5.setFailure(r0);	 Catch:{ all -> 0x012f }
    L_0x0107:
        if (r20 == 0) goto L_0x011f;
    L_0x0109:
        if (r15 != 0) goto L_0x0154;
    L_0x010b:
        r14 = new java.util.ArrayList;	 Catch:{ all -> 0x012f }
        r8 = 1;
        r14.<init>(r8);	 Catch:{ all -> 0x012f }
    L_0x0111:
        r0 = r24;
        r14.add(r0);	 Catch:{ all -> 0x0152 }
    L_0x0116:
        r0 = r24;
        r8 = r0 instanceof java.io.IOException;	 Catch:{ all -> 0x0152 }
        if (r8 == 0) goto L_0x00bc;
    L_0x011c:
        r21 = 0;
        goto L_0x00bc;
    L_0x011f:
        r0 = r31;
        r1 = r24;
        org.jboss.netty.channel.Channels.fireExceptionCaughtLater(r0, r1);	 Catch:{ all -> 0x012f }
        r14 = r15;
        goto L_0x0116;
    L_0x0128:
        if (r22 == 0) goto L_0x0063;
    L_0x012a:
        r30.clearOpWrite(r31);	 Catch:{ all -> 0x012f }
        goto L_0x0063;
    L_0x012f:
        r8 = move-exception;
        r14 = r15;
    L_0x0131:
        monitor-exit(r27);	 Catch:{ all -> 0x0152 }
        throw r8;
    L_0x0133:
        if (r21 != 0) goto L_0x0140;
    L_0x0135:
        r8 = org.jboss.netty.channel.Channels.succeededFuture(r31);
        r0 = r30;
        r1 = r31;
        r0.close(r1, r8);
    L_0x0140:
        if (r20 == 0) goto L_0x014a;
    L_0x0142:
        r0 = r31;
        r1 = r28;
        org.jboss.netty.channel.Channels.fireWriteComplete(r0, r1);
    L_0x0149:
        return;
    L_0x014a:
        r0 = r31;
        r1 = r28;
        org.jboss.netty.channel.Channels.fireWriteCompleteLater(r0, r1);
        goto L_0x0149;
    L_0x0152:
        r8 = move-exception;
        goto L_0x0131;
    L_0x0154:
        r14 = r15;
        goto L_0x0111;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.channel.socket.nio.AbstractNioWorker.write0(org.jboss.netty.channel.socket.nio.AbstractNioChannel):void");
    }

    static boolean isIoThread(AbstractNioChannel<?> channel) {
        return Thread.currentThread() == channel.worker.thread;
    }

    protected void setOpWrite(AbstractNioChannel<?> channel) {
        SelectionKey key = channel.channel.keyFor(this.selector);
        if (key != null) {
            if (key.isValid()) {
                int interestOps = channel.getInternalInterestOps();
                if ((interestOps & 4) == 0) {
                    interestOps |= 4;
                    key.interestOps(interestOps);
                    channel.setInternalInterestOps(interestOps);
                    return;
                }
                return;
            }
            close(key);
        }
    }

    protected void clearOpWrite(AbstractNioChannel<?> channel) {
        SelectionKey key = channel.channel.keyFor(this.selector);
        if (key != null) {
            if (key.isValid()) {
                int interestOps = channel.getInternalInterestOps();
                if ((interestOps & 4) != 0) {
                    interestOps &= -5;
                    key.interestOps(interestOps);
                    channel.setInternalInterestOps(interestOps);
                    return;
                }
                return;
            }
            close(key);
        }
    }

    protected void close(AbstractNioChannel<?> channel, ChannelFuture future) {
        boolean connected = channel.isConnected();
        boolean bound = channel.isBound();
        boolean iothread = isIoThread(channel);
        try {
            channel.channel.close();
            increaseCancelledKeys();
            if (channel.setClosed()) {
                future.setSuccess();
                if (connected) {
                    if (iothread) {
                        Channels.fireChannelDisconnected((Channel) channel);
                    } else {
                        Channels.fireChannelDisconnectedLater(channel);
                    }
                }
                if (bound) {
                    if (iothread) {
                        Channels.fireChannelUnbound((Channel) channel);
                    } else {
                        Channels.fireChannelUnboundLater(channel);
                    }
                }
                cleanUpWriteBuffer(channel);
                if (iothread) {
                    Channels.fireChannelClosed((Channel) channel);
                    return;
                } else {
                    Channels.fireChannelClosedLater(channel);
                    return;
                }
            }
            future.setSuccess();
        } catch (Throwable t) {
            future.setFailure(t);
            if (iothread) {
                Channels.fireExceptionCaught((Channel) channel, t);
            } else {
                Channels.fireExceptionCaughtLater((Channel) channel, t);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void cleanUpWriteBuffer(org.jboss.netty.channel.socket.nio.AbstractNioChannel<?> r8) {
        /*
        r0 = 0;
        r3 = 0;
        r7 = r8.writeLock;
        monitor-enter(r7);
        r2 = r8.currentWriteEvent;	 Catch:{ all -> 0x0069 }
        if (r2 == 0) goto L_0x002d;
    L_0x0009:
        r6 = r8.isOpen();	 Catch:{ all -> 0x0069 }
        if (r6 == 0) goto L_0x0045;
    L_0x000f:
        r1 = new java.nio.channels.NotYetConnectedException;	 Catch:{ all -> 0x0069 }
        r1.<init>();	 Catch:{ all -> 0x0069 }
        r0 = r1;
    L_0x0015:
        r4 = r2.getFuture();	 Catch:{ all -> 0x0069 }
        r6 = r8.currentWriteBuffer;	 Catch:{ all -> 0x0069 }
        if (r6 == 0) goto L_0x0025;
    L_0x001d:
        r6 = r8.currentWriteBuffer;	 Catch:{ all -> 0x0069 }
        r6.release();	 Catch:{ all -> 0x0069 }
        r6 = 0;
        r8.currentWriteBuffer = r6;	 Catch:{ all -> 0x0069 }
    L_0x0025:
        r6 = 0;
        r8.currentWriteEvent = r6;	 Catch:{ all -> 0x0069 }
        r2 = 0;
        r4.setFailure(r0);	 Catch:{ all -> 0x0069 }
        r3 = 1;
    L_0x002d:
        r5 = r8.writeBufferQueue;	 Catch:{ all -> 0x0069 }
        r1 = r0;
    L_0x0030:
        r2 = r5.poll();	 Catch:{ all -> 0x0070 }
        r2 = (org.jboss.netty.channel.MessageEvent) r2;	 Catch:{ all -> 0x0070 }
        if (r2 != 0) goto L_0x004c;
    L_0x0038:
        monitor-exit(r7);	 Catch:{ all -> 0x0070 }
        if (r3 == 0) goto L_0x0044;
    L_0x003b:
        r6 = isIoThread(r8);
        if (r6 == 0) goto L_0x006c;
    L_0x0041:
        org.jboss.netty.channel.Channels.fireExceptionCaught(r8, r1);
    L_0x0044:
        return;
    L_0x0045:
        r1 = new java.nio.channels.ClosedChannelException;	 Catch:{ all -> 0x0069 }
        r1.<init>();	 Catch:{ all -> 0x0069 }
        r0 = r1;
        goto L_0x0015;
    L_0x004c:
        if (r1 != 0) goto L_0x0073;
    L_0x004e:
        r6 = r8.isOpen();	 Catch:{ all -> 0x0070 }
        if (r6 == 0) goto L_0x0063;
    L_0x0054:
        r0 = new java.nio.channels.NotYetConnectedException;	 Catch:{ all -> 0x0070 }
        r0.<init>();	 Catch:{ all -> 0x0070 }
    L_0x0059:
        r3 = 1;
    L_0x005a:
        r6 = r2.getFuture();	 Catch:{ all -> 0x0069 }
        r6.setFailure(r0);	 Catch:{ all -> 0x0069 }
        r1 = r0;
        goto L_0x0030;
    L_0x0063:
        r0 = new java.nio.channels.ClosedChannelException;	 Catch:{ all -> 0x0070 }
        r0.<init>();	 Catch:{ all -> 0x0070 }
        goto L_0x0059;
    L_0x0069:
        r6 = move-exception;
    L_0x006a:
        monitor-exit(r7);	 Catch:{ all -> 0x0069 }
        throw r6;
    L_0x006c:
        org.jboss.netty.channel.Channels.fireExceptionCaughtLater(r8, r1);
        goto L_0x0044;
    L_0x0070:
        r6 = move-exception;
        r0 = r1;
        goto L_0x006a;
    L_0x0073:
        r0 = r1;
        goto L_0x005a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.channel.socket.nio.AbstractNioWorker.cleanUpWriteBuffer(org.jboss.netty.channel.socket.nio.AbstractNioChannel):void");
    }

    void setInterestOps(final AbstractNioChannel<?> channel, final ChannelFuture future, final int interestOps) {
        boolean iothread = isIoThread(channel);
        if (iothread) {
            boolean changed = false;
            try {
                Selector selector = this.selector;
                SelectionKey key = channel.channel.keyFor(selector);
                int newInterestOps = (interestOps & -5) | (channel.getInternalInterestOps() & 4);
                if (key == null || selector == null) {
                    if (channel.getInternalInterestOps() != newInterestOps) {
                        changed = true;
                    }
                    channel.setInternalInterestOps(newInterestOps);
                    future.setSuccess();
                    if (!changed) {
                        return;
                    }
                    if (iothread) {
                        Channels.fireChannelInterestChanged((Channel) channel);
                        return;
                    } else {
                        Channels.fireChannelInterestChangedLater(channel);
                        return;
                    }
                }
                if (channel.getInternalInterestOps() != newInterestOps) {
                    changed = true;
                    key.interestOps(newInterestOps);
                    if (Thread.currentThread() != this.thread && this.wakenUp.compareAndSet(false, true)) {
                        selector.wakeup();
                    }
                    channel.setInternalInterestOps(newInterestOps);
                }
                future.setSuccess();
                if (changed) {
                    Channels.fireChannelInterestChanged((Channel) channel);
                    return;
                }
                return;
            } catch (CancelledKeyException e) {
                Throwable cce = new ClosedChannelException();
                future.setFailure(cce);
                Channels.fireExceptionCaught((Channel) channel, cce);
                return;
            } catch (Throwable t) {
                future.setFailure(t);
                Channels.fireExceptionCaught((Channel) channel, t);
                return;
            }
        }
        channel.getPipeline().execute(new Runnable() {
            public void run() {
                AbstractNioWorker.this.setInterestOps(channel, future, interestOps);
            }
        });
    }
}
