package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;

public class NioDatagramWorkerPool extends AbstractNioWorkerPool<NioDatagramWorker> {
    public NioDatagramWorkerPool(Executor executor, int workerCount) {
        super(executor, workerCount);
    }

    protected NioDatagramWorker newWorker(Executor executor) {
        return new NioDatagramWorker(executor);
    }
}
