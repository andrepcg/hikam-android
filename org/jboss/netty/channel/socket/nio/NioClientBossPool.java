package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.Timer;

public class NioClientBossPool extends AbstractNioBossPool<NioClientBoss> {
    private final ThreadNameDeterminer determiner;
    private boolean stopTimer;
    private final Timer timer;

    public NioClientBossPool(Executor bossExecutor, int bossCount, Timer timer, ThreadNameDeterminer determiner) {
        super(bossExecutor, bossCount, false);
        this.determiner = determiner;
        this.timer = timer;
        init();
    }

    public NioClientBossPool(Executor bossExecutor, int bossCount) {
        this(bossExecutor, bossCount, new HashedWheelTimer(), null);
        this.stopTimer = true;
    }

    protected NioClientBoss newBoss(Executor executor) {
        return new NioClientBoss(executor, this.timer, this.determiner);
    }

    public void shutdown() {
        super.shutdown();
        if (this.stopTimer) {
            this.timer.stop();
        }
    }

    public void releaseExternalResources() {
        super.releaseExternalResources();
        this.timer.stop();
    }
}
