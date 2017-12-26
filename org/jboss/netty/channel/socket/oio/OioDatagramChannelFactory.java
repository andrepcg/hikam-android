package org.jboss.netty.channel.socket.oio;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.internal.ExecutorUtil;

public class OioDatagramChannelFactory implements DatagramChannelFactory {
    private boolean shutdownExecutor;
    final OioDatagramPipelineSink sink;
    private final Executor workerExecutor;

    public OioDatagramChannelFactory() {
        this(Executors.newCachedThreadPool());
        this.shutdownExecutor = true;
    }

    public OioDatagramChannelFactory(Executor workerExecutor) {
        this(workerExecutor, null);
    }

    public OioDatagramChannelFactory(Executor workerExecutor, ThreadNameDeterminer determiner) {
        if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        }
        this.workerExecutor = workerExecutor;
        this.sink = new OioDatagramPipelineSink(workerExecutor, determiner);
    }

    public DatagramChannel newChannel(ChannelPipeline pipeline) {
        return new OioDatagramChannel(this, pipeline, this.sink);
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
