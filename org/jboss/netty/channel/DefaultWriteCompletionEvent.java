package org.jboss.netty.channel;

public class DefaultWriteCompletionEvent implements WriteCompletionEvent {
    private final Channel channel;
    private final long writtenAmount;

    public DefaultWriteCompletionEvent(Channel channel, long writtenAmount) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else if (writtenAmount <= 0) {
            throw new IllegalArgumentException("writtenAmount must be a positive integer: " + writtenAmount);
        } else {
            this.channel = channel;
            this.writtenAmount = writtenAmount;
        }
    }

    public Channel getChannel() {
        return this.channel;
    }

    public ChannelFuture getFuture() {
        return Channels.succeededFuture(getChannel());
    }

    public long getWrittenAmount() {
        return this.writtenAmount;
    }

    public String toString() {
        String channelString = getChannel().toString();
        StringBuilder buf = new StringBuilder(channelString.length() + 32);
        buf.append(channelString);
        buf.append(" WRITTEN_AMOUNT: ");
        buf.append(getWrittenAmount());
        return buf.toString();
    }
}
