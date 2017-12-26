package org.jboss.netty.channel.socket.nio;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ConnectTimeoutException;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

public final class NioClientBoss extends AbstractNioSelector implements Boss {
    private final Timer timer;
    private final TimerTask wakeupTask = new C12261();

    private final class RegisterTask implements Runnable {
        private final NioClientBoss boss;
        private final NioClientSocketChannel channel;

        RegisterTask(NioClientBoss boss, NioClientSocketChannel channel) {
            this.boss = boss;
            this.channel = channel;
        }

        public void run() {
            int timeout = this.channel.getConfig().getConnectTimeoutMillis();
            if (timeout > 0 && !this.channel.isConnected()) {
                this.channel.timoutTimer = NioClientBoss.this.timer.newTimeout(NioClientBoss.this.wakeupTask, (long) timeout, TimeUnit.MILLISECONDS);
            }
            try {
                ((SocketChannel) this.channel.channel).register(this.boss.selector, 8, this.channel);
            } catch (ClosedChannelException e) {
                this.channel.worker.close(this.channel, Channels.succeededFuture(this.channel));
            }
            int connectTimeout = this.channel.getConfig().getConnectTimeoutMillis();
            if (connectTimeout > 0) {
                this.channel.connectDeadlineNanos = System.nanoTime() + (((long) connectTimeout) * 1000000);
            }
        }
    }

    class C12261 implements TimerTask {
        C12261() {
        }

        public void run(Timeout timeout) throws Exception {
            Selector selector = NioClientBoss.this.selector;
            if (selector != null && NioClientBoss.this.wakenUp.compareAndSet(false, true)) {
                selector.wakeup();
            }
        }
    }

    public /* bridge */ /* synthetic */ void rebuildSelector() {
        super.rebuildSelector();
    }

    public /* bridge */ /* synthetic */ void register(Channel x0, ChannelFuture x1) {
        super.register(x0, x1);
    }

    public /* bridge */ /* synthetic */ void run() {
        super.run();
    }

    public /* bridge */ /* synthetic */ void shutdown() {
        super.shutdown();
    }

    NioClientBoss(Executor bossExecutor, Timer timer, ThreadNameDeterminer determiner) {
        super(bossExecutor, determiner);
        this.timer = timer;
    }

    protected ThreadRenamingRunnable newThreadRenamingRunnable(int id, ThreadNameDeterminer determiner) {
        return new ThreadRenamingRunnable(this, "New I/O boss #" + id, determiner);
    }

    protected Runnable createRegisterTask(Channel channel, ChannelFuture future) {
        return new RegisterTask(this, (NioClientSocketChannel) channel);
    }

    protected void process(Selector selector) {
        processSelectedKeys(selector.selectedKeys());
        processConnectTimeout(selector.keys(), System.nanoTime());
    }

    private void processSelectedKeys(Set<SelectionKey> selectedKeys) {
        if (!selectedKeys.isEmpty()) {
            Iterator<SelectionKey> i = selectedKeys.iterator();
            while (i.hasNext()) {
                SelectionKey k = (SelectionKey) i.next();
                i.remove();
                if (k.isValid()) {
                    try {
                        if (k.isConnectable()) {
                            connect(k);
                        }
                    } catch (Throwable t) {
                        Channel ch = (NioClientSocketChannel) k.attachment();
                        ch.connectFuture.setFailure(t);
                        Channels.fireExceptionCaught(ch, t);
                        k.cancel();
                        ch.worker.close(ch, Channels.succeededFuture(ch));
                    }
                } else {
                    close(k);
                }
            }
        }
    }

    private static void processConnectTimeout(Set<SelectionKey> keys, long currentTimeNanos) {
        for (SelectionKey k : keys) {
            if (k.isValid()) {
                Channel ch = (NioClientSocketChannel) k.attachment();
                if (ch.connectDeadlineNanos > 0 && currentTimeNanos >= ch.connectDeadlineNanos) {
                    Throwable cause = new ConnectTimeoutException("connection timed out: " + ch.requestedRemoteAddress);
                    ch.connectFuture.setFailure(cause);
                    Channels.fireExceptionCaught(ch, cause);
                    ch.worker.close(ch, Channels.succeededFuture(ch));
                }
            }
        }
    }

    private static void connect(SelectionKey k) throws IOException {
        NioClientSocketChannel ch = (NioClientSocketChannel) k.attachment();
        try {
            if (((SocketChannel) ch.channel).finishConnect()) {
                k.cancel();
                if (ch.timoutTimer != null) {
                    ch.timoutTimer.cancel();
                }
                ch.worker.register(ch, ch.connectFuture);
            }
        } catch (ConnectException e) {
            ConnectException newE = new ConnectException(e.getMessage() + ": " + ch.requestedRemoteAddress);
            newE.setStackTrace(e.getStackTrace());
            throw newE;
        }
    }

    protected void close(SelectionKey k) {
        NioClientSocketChannel ch = (NioClientSocketChannel) k.attachment();
        ch.worker.close(ch, Channels.succeededFuture(ch));
    }
}
