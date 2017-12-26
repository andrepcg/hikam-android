package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.InternetProtocolFamily;
import org.jboss.netty.util.ExternalResourceReleasable;

public class NioDatagramChannelFactory implements DatagramChannelFactory {
    private final InternetProtocolFamily family;
    private boolean releasePool;
    private final NioDatagramPipelineSink sink;
    private final WorkerPool<NioDatagramWorker> workerPool;

    public NioDatagramChannelFactory() {
        this((InternetProtocolFamily) null);
    }

    public NioDatagramChannelFactory(InternetProtocolFamily family) {
        this.workerPool = new NioDatagramWorkerPool(Executors.newCachedThreadPool(), SelectorUtil.DEFAULT_IO_THREADS);
        this.family = family;
        this.sink = new NioDatagramPipelineSink(this.workerPool);
        this.releasePool = true;
    }

    public NioDatagramChannelFactory(Executor workerExecutor) {
        this(workerExecutor, SelectorUtil.DEFAULT_IO_THREADS);
    }

    public NioDatagramChannelFactory(Executor workerExecutor, int workerCount) {
        this(new NioDatagramWorkerPool(workerExecutor, workerCount));
    }

    public NioDatagramChannelFactory(WorkerPool<NioDatagramWorker> workerPool) {
        this((WorkerPool) workerPool, null);
    }

    public NioDatagramChannelFactory(Executor workerExecutor, InternetProtocolFamily family) {
        this(workerExecutor, SelectorUtil.DEFAULT_IO_THREADS, family);
    }

    public NioDatagramChannelFactory(Executor workerExecutor, int workerCount, InternetProtocolFamily family) {
        this(new NioDatagramWorkerPool(workerExecutor, workerCount), family);
    }

    public NioDatagramChannelFactory(WorkerPool<NioDatagramWorker> workerPool, InternetProtocolFamily family) {
        this.workerPool = workerPool;
        this.family = family;
        this.sink = new NioDatagramPipelineSink(workerPool);
    }

    public DatagramChannel newChannel(ChannelPipeline pipeline) {
        return new NioDatagramChannel(this, pipeline, this.sink, this.sink.nextWorker(), this.family);
    }

    public void shutdown() {
        this.workerPool.shutdown();
        if (this.releasePool) {
            releasePool();
        }
    }

    public void releaseExternalResources() {
        this.workerPool.shutdown();
        releasePool();
    }

    private void releasePool() {
        if (this.workerPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable) this.workerPool).releaseExternalResources();
        }
    }
}
