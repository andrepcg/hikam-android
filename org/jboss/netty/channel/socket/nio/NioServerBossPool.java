package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import org.jboss.netty.util.ThreadNameDeterminer;

public class NioServerBossPool extends AbstractNioBossPool<NioServerBoss> {
    private final ThreadNameDeterminer determiner;

    public NioServerBossPool(Executor bossExecutor, int bossCount, ThreadNameDeterminer determiner) {
        super(bossExecutor, bossCount, false);
        this.determiner = determiner;
        init();
    }

    public NioServerBossPool(Executor bossExecutor, int bossCount) {
        this(bossExecutor, bossCount, null);
    }

    protected NioServerBoss newBoss(Executor executor) {
        return new NioServerBoss(executor, this.determiner);
    }
}
