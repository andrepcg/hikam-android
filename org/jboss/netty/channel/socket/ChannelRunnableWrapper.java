package org.jboss.netty.channel.socket;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.DefaultChannelFuture;

public class ChannelRunnableWrapper extends DefaultChannelFuture implements Runnable {
    private boolean started;
    private final Runnable task;

    public ChannelRunnableWrapper(Channel channel, Runnable task) {
        super(channel, true);
        this.task = task;
    }

    public void run() {
        synchronized (this) {
            if (isCancelled()) {
                return;
            }
            this.started = true;
            try {
                this.task.run();
                setSuccess();
            } catch (Throwable t) {
                setFailure(t);
            }
        }
    }

    public synchronized boolean cancel() {
        boolean z;
        if (this.started) {
            z = false;
        } else {
            z = super.cancel();
        }
        return z;
    }
}
