package org.jboss.netty.channel;

public class DownstreamChannelStateEvent implements ChannelStateEvent {
    private final Channel channel;
    private final ChannelFuture future;
    private final ChannelState state;
    private final Object value;

    public DownstreamChannelStateEvent(Channel channel, ChannelFuture future, ChannelState state, Object value) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else if (future == null) {
            throw new NullPointerException("future");
        } else if (state == null) {
            throw new NullPointerException("state");
        } else {
            this.channel = channel;
            this.future = future;
            this.state = state;
            this.value = value;
        }
    }

    public Channel getChannel() {
        return this.channel;
    }

    public ChannelFuture getFuture() {
        return this.future;
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
                    buf.append(" CLOSE");
                    break;
                }
                buf.append(" OPEN");
                break;
            case BOUND:
                if (getValue() == null) {
                    buf.append(" UNBIND");
                    break;
                }
                buf.append(" BIND: ");
                buf.append(getValue());
                break;
            case CONNECTED:
                if (getValue() == null) {
                    buf.append(" DISCONNECT");
                    break;
                }
                buf.append(" CONNECT: ");
                buf.append(getValue());
                break;
            case INTEREST_OPS:
                buf.append(" CHANGE_INTEREST: ");
                buf.append(getValue());
                break;
            default:
                buf.append(' ');
                buf.append(getState().name());
                buf.append(": ");
                buf.append(getValue());
                break;
        }
        return buf.toString();
    }
}
