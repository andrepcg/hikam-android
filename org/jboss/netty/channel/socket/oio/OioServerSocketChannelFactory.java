package org.jboss.netty.channel.socket.oio;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.socket.ServerSocketChannel;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.internal.ExecutorUtil;

public class OioServerSocketChannelFactory implements ServerSocketChannelFactory {
    final Executor bossExecutor;
    private boolean shutdownExecutor;
    private final ChannelSink sink;
    private final Executor workerExecutor;

    public OioServerSocketChannelFactory() {
        this(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        this.shutdownExecutor = true;
    }

    public OioServerSocketChannelFactory(Executor bossExecutor, Executor workerExecutor) {
        this(bossExecutor, workerExecutor, null);
    }

    public OioServerSocketChannelFactory(Executor bossExecutor, Executor workerExecutor, ThreadNameDeterminer determiner) {
        if (bossExecutor == null) {
            throw new NullPointerException("bossExecutor");
        } else if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        } else {
            this.bossExecutor = bossExecutor;
            this.workerExecutor = workerExecutor;
            this.sink = new OioServerSocketPipelineSink(workerExecutor, determiner);
        }
    }

    public ServerSocketChannel newChannel(ChannelPipeline pipeline) {
        return new OioServerSocketChannel(this, pipeline, this.sink);
    }

    public void shutdown() {
        if (this.shutdownExecutor) {
            ExecutorUtil.shutdownNow(this.bossExecutor);
            ExecutorUtil.shutdownNow(this.workerExecutor);
        }
    }

    public void releaseExternalResources() {
        ExecutorUtil.shutdownNow(this.bossExecutor);
        ExecutorUtil.shutdownNow(this.workerExecutor);
    }
}
