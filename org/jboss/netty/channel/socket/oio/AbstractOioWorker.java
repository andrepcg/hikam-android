package org.jboss.netty.channel.socket.oio;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.Worker;

abstract class AbstractOioWorker<C extends AbstractOioChannel> implements Worker {
    protected final C channel;
    private volatile boolean done;
    private final Queue<Runnable> eventQueue = new ConcurrentLinkedQueue();
    protected volatile Thread thread;

    abstract boolean process() throws IOException;

    protected AbstractOioWorker(C channel) {
        this.channel = channel;
        channel.worker = this;
    }

    public void run() {
        AbstractOioChannel abstractOioChannel = this.channel;
        Thread currentThread = Thread.currentThread();
        abstractOioChannel.workerThread = currentThread;
        this.thread = currentThread;
        while (this.channel.isOpen()) {
            synchronized (this.channel.interestOpsLock) {
                while (!this.channel.isReadable()) {
                    try {
                        this.channel.interestOpsLock.wait();
                    } catch (InterruptedException e) {
                        if (!this.channel.isOpen()) {
                            break;
                        }
                    }
                }
            }
            boolean cont = false;
            try {
                cont = process();
            } catch (Throwable th) {
                processEventQueue();
            }
            processEventQueue();
            if (!cont) {
                break;
            }
        }
        synchronized (this.channel.interestOpsLock) {
            this.channel.workerThread = null;
        }
        close(this.channel, Channels.succeededFuture(this.channel), true);
        this.done = true;
        processEventQueue();
    }

    static boolean isIoThread(AbstractOioChannel channel) {
        return Thread.currentThread() == channel.workerThread;
    }

    public void executeInIoThread(Runnable task) {
        if (Thread.currentThread() == this.thread || this.done) {
            task.run();
        } else if (!this.eventQueue.offer(task)) {
        }
    }

    private void processEventQueue() {
        while (true) {
            Runnable task = (Runnable) this.eventQueue.poll();
            if (task != null) {
                task.run();
            } else {
                return;
            }
        }
    }

    static void setInterestOps(AbstractOioChannel channel, ChannelFuture future, int interestOps) {
        boolean iothread = isIoThread(channel);
        interestOps = (interestOps & -5) | (channel.getInternalInterestOps() & 4);
        boolean changed = false;
        try {
            if (channel.getInternalInterestOps() != interestOps) {
                if ((interestOps & 1) != 0) {
                    channel.setInternalInterestOps(1);
                } else {
                    channel.setInternalInterestOps(0);
                }
                changed = true;
            }
            future.setSuccess();
            if (changed) {
                synchronized (channel.interestOpsLock) {
                    channel.setInternalInterestOps(interestOps);
                    Thread currentThread = Thread.currentThread();
                    Thread workerThread = channel.workerThread;
                    if (!(workerThread == null || currentThread == workerThread)) {
                        workerThread.interrupt();
                    }
                }
                if (iothread) {
                    Channels.fireChannelInterestChanged((Channel) channel);
                } else {
                    Channels.fireChannelInterestChangedLater(channel);
                }
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

    static void close(AbstractOioChannel channel, ChannelFuture future) {
        close(channel, future, isIoThread(channel));
    }

    private static void close(AbstractOioChannel channel, ChannelFuture future, boolean iothread) {
        boolean connected = channel.isConnected();
        boolean bound = channel.isBound();
        try {
            channel.closeSocket();
            if (channel.setClosed()) {
                future.setSuccess();
                if (connected) {
                    Thread currentThread = Thread.currentThread();
                    synchronized (channel.interestOpsLock) {
                        Thread workerThread = channel.workerThread;
                        if (!(workerThread == null || currentThread == workerThread)) {
                            workerThread.interrupt();
                        }
                    }
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
}
