package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.Timer;

public class NioClientSocketChannelFactory implements ClientSocketChannelFactory {
    private static final int DEFAULT_BOSS_COUNT = 1;
    private final BossPool<NioClientBoss> bossPool;
    private boolean releasePools;
    private final NioClientSocketPipelineSink sink;
    private final WorkerPool<NioWorker> workerPool;

    public NioClientSocketChannelFactory() {
        this(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        this.releasePools = true;
    }

    public NioClientSocketChannelFactory(Executor bossExecutor, Executor workerExecutor) {
        this(bossExecutor, workerExecutor, 1, SelectorUtil.DEFAULT_IO_THREADS);
    }

    public NioClientSocketChannelFactory(Executor bossExecutor, Executor workerExecutor, int workerCount) {
        this(bossExecutor, workerExecutor, 1, workerCount);
    }

    public NioClientSocketChannelFactory(Executor bossExecutor, Executor workerExecutor, int bossCount, int workerCount) {
        this(bossExecutor, bossCount, new NioWorkerPool(workerExecutor, workerCount));
    }

    public NioClientSocketChannelFactory(Executor bossExecutor, int bossCount, WorkerPool<NioWorker> workerPool) {
        this(new NioClientBossPool(bossExecutor, bossCount), (WorkerPool) workerPool);
    }

    public NioClientSocketChannelFactory(Executor bossExecutor, int bossCount, WorkerPool<NioWorker> workerPool, Timer timer) {
        this(new NioClientBossPool(bossExecutor, bossCount, timer, null), (WorkerPool) workerPool);
    }

    public NioClientSocketChannelFactory(BossPool<NioClientBoss> bossPool, WorkerPool<NioWorker> workerPool) {
        if (bossPool == null) {
            throw new NullPointerException("bossPool");
        } else if (workerPool == null) {
            throw new NullPointerException("workerPool");
        } else {
            this.bossPool = bossPool;
            this.workerPool = workerPool;
            this.sink = new NioClientSocketPipelineSink(bossPool);
        }
    }

    public SocketChannel newChannel(ChannelPipeline pipeline) {
        return new NioClientSocketChannel(this, pipeline, this.sink, (NioWorker) this.workerPool.nextWorker());
    }

    public void shutdown() {
        this.bossPool.shutdown();
        this.workerPool.shutdown();
        if (this.releasePools) {
            releasePools();
        }
    }

    public void releaseExternalResources() {
        this.bossPool.shutdown();
        this.workerPool.shutdown();
        releasePools();
    }

    private void releasePools() {
        if (this.bossPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable) this.bossPool).releaseExternalResources();
        }
        if (this.workerPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable) this.workerPool).releaseExternalResources();
        }
    }
}
