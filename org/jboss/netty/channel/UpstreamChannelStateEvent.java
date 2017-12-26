package org.jboss.netty.channel;

public class UpstreamChannelStateEvent implements ChannelStateEvent {
    private final Channel channel;
    private final ChannelState state;
    private final Object value;

    public UpstreamChannelStateEvent(Channel channel, ChannelState state, Object value) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else if (state == null) {
            throw new NullPointerException("state");
        } else {
            this.channel = channel;
            this.state = state;
            this.value = value;
        }
    }

    public Channel getChannel() {
        return this.channel;
    }

    public ChannelFuture getFuture() {
        return Channels.succeededFuture(getChannel());
    }

    public ChannelState getState() {
        return this.state;
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        String channelString = getChannel().toString();
        StringBuilder buf = new StringBuilder(channelString.length() + 64);
        buf.append(channelString);
        switch (getState()) {
            case OPEN:
                if (!Boolean.TRUE.equals(getValue())) {
                    buf.append(" CLOSED");
                    break;
                }
                buf.append(" OPEN");
                break;
            case BOUND:
                if (getValue() == null) {
                    buf.append(" UNBOUND");
                    break;
                }
                buf.append(" BOUND: ");
                buf.append(getValue());
                break;
            case CONNECTED:
                if (getValue() == null) {
                    buf.append(" DISCONNECTED");
                    break;
                }
                buf.append(" CONNECTED: ");
                buf.append(getValue());
                break;
            case INTEREST_OPS:
                buf.append(" INTEREST_CHANGED");
                break;
            default:
                buf.append(getState().name());
                buf.append(": ");
                buf.append(getValue());
                break;
        }
        return buf.toString();
    }
}
