package org.jboss.netty.channel;

import java.util.EventListener;

public interface ChannelFutureListener extends EventListener {
    public static final ChannelFutureListener CLOSE = new C12081();
    public static final ChannelFutureListener CLOSE_ON_FAILURE = new C12092();

    static class C12081 implements ChannelFutureListener {
        C12081() {
        }

        public void operationComplete(ChannelFuture future) {
            future.getChannel().close();
        }
    }

    static class C12092 implements ChannelFutureListener {
        C12092() {
        }

        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                future.getChannel().close();
            }
        }
    }

    void operationComplete(ChannelFuture channelFuture) throws Exception;
}
