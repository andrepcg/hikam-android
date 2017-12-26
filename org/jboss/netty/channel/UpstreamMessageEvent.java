package org.jboss.netty.channel;

import com.jwkj.data.MessageDB;
import java.net.SocketAddress;
import org.jboss.netty.util.internal.StringUtil;

public class UpstreamMessageEvent implements MessageEvent {
    private final Channel channel;
    private final Object message;
    private final SocketAddress remoteAddress;

    public UpstreamMessageEvent(Channel channel, Object message, SocketAddress remoteAddress) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else if (message == null) {
            throw new NullPointerException(MessageDB.TABLE_NAME);
        } else {
            this.channel = channel;
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
        return Channels.succeededFuture(getChannel());
    }

    public Object getMessage() {
        return this.message;
    }

    public SocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public String toString() {
        if (getRemoteAddress() == getChannel().getRemoteAddress()) {
            return getChannel().toString() + " RECEIVED: " + StringUtil.stripControlCharacters(getMessage());
        }
        return getChannel().toString() + " RECEIVED: " + StringUtil.stripControlCharacters(getMessage()) + " from " + getRemoteAddress();
    }
}
