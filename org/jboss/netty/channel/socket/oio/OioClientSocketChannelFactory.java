package org.jboss.netty.channel.socket.oio;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.internal.ExecutorUtil;

public class OioClientSocketChannelFactory implements ClientSocketChannelFactory {
    private boolean shutdownExecutor;
    final OioClientSocketPipelineSink sink;
    private final Executor workerExecutor;

    public OioClientSocketChannelFactory() {
        this(Executors.newCachedThreadPool());
        this.shutdownExecutor = true;
    }

    public OioClientSocketChannelFactory(Executor workerExecutor) {
        this(workerExecutor, null);
    }

    public OioClientSocketChannelFactory(Executor workerExecutor, ThreadNameDeterminer determiner) {
        if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        }
        this.workerExecutor = workerExecutor;
        this.sink = new OioClientSocketPipelineSink(workerExecutor, determiner);
    }

    public SocketChannel newChannel(ChannelPipeline pipeline) {
        return new OioClientSocketChannel(this, pipeline, this.sink);
    }

    public void shutdown() {
        if (this.shutdownExecutor) {
            ExecutorUtil.shutdownNow(this.workerExecutor);
        }
    }

    public void releaseExternalResources() {
        ExecutorUtil.shutdownNow(this.workerExecutor);
    }
}
