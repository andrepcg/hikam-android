package org.jboss.netty.channel;

import com.jwkj.data.MessageDB;
import java.net.SocketAddress;
import org.jboss.netty.util.internal.StringUtil;

public class DownstreamMessageEvent implements MessageEvent {
    private final Channel channel;
    private final ChannelFuture future;
    private final Object message;
    private final SocketAddress remoteAddress;

    public DownstreamMessageEvent(Channel channel, ChannelFuture future, Object message, SocketAddress remoteAddress) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else if (future == null) {
            throw new NullPointerException("future");
        } else if (message == null) {
            throw new NullPointerException(MessageDB.TABLE_NAME);
        } else {
            this.channel = channel;
            this.future = future;
            this.message = message;
            if (remoteAddress != null) {
                this.remoteAddress = remoteAddress;
            } else {
                this.remoteAddress = channel.getRemoteAddress();
            }
        }
    }

    public Channel getChannel() {
        return this.channel;
    }

    public ChannelFuture getFuture() {
        return this.future;
    }

    public Object getMessage() {
        return this.message;
    }

    public SocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public String toString() {
        if (getRemoteAddress() == getChannel().getRemoteAddress()) {
            return getChannel().toString() + " WRITE: " + StringUtil.stripControlCharacters(getMessage());
        }
        return getChannel().toString() + " WRITE: " + StringUtil.stripControlCharacters(getMessage()) + " to " + getRemoteAddress();
    }
}
