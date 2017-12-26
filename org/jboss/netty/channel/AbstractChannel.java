package org.jboss.netty.channel;

import java.net.SocketAddress;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.jboss.netty.util.internal.ConcurrentHashMap;

public abstract class AbstractChannel implements Channel {
    static final /* synthetic */ boolean $assertionsDisabled = (!AbstractChannel.class.desiredAssertionStatus());
    private static final AtomicIntegerFieldUpdater<AbstractChannel> UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractChannel.class, "unwritable");
    static final ConcurrentMap<Integer, Channel> allChannels = new ConcurrentHashMap();
    private static final Random random = new Random();
    private volatile Object attachment;
    private final ChannelCloseFuture closeFuture = new ChannelCloseFuture();
    private final ChannelFactory factory;
    private final Integer id;
    private volatile int interestOps = 1;
    private final Channel parent;
    private final ChannelPipeline pipeline;
    private String strVal;
    private boolean strValConnected;
    private final ChannelFuture succeededFuture = new SucceededChannelFuture(this);
    private volatile int unwritable;

    private final class ChannelCloseFuture extends DefaultChannelFuture {
        ChannelCloseFuture() {
            super(AbstractChannel.this, false);
        }

        public boolean setSuccess() {
            return false;
        }

        public boolean setFailure(Throwable cause) {
            return false;
        }

        boolean setClosed() {
            return super.setSuccess();
        }
    }

    private static Integer allocateId(Channel channel) {
        Integer id = Integer.valueOf(random.nextInt());
        while (allChannels.putIfAbsent(id, channel) != null) {
            id = Integer.valueOf(id.intValue() + 1);
        }
        return id;
    }

    protected AbstractChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        this.parent = parent;
        this.factory = factory;
        this.pipeline = pipeline;
        this.id = allocateId(this);
        pipeline.attach(this, sink);
    }

    protected AbstractChannel(Integer id, Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        this.id = id;
        this.parent = parent;
        this.factory = factory;
        this.pipeline = pipeline;
        pipeline.attach(this, sink);
    }

    public final Integer getId() {
        return this.id;
    }

    public Channel getParent() {
        return this.parent;
    }

    public ChannelFactory getFactory() {
        return this.factory;
    }

    public ChannelPipeline getPipeline() {
        return this.pipeline;
    }

    protected ChannelFuture getSucceededFuture() {
        return this.succeededFuture;
    }

    protected ChannelFuture getUnsupportedOperationFuture() {
        return new FailedChannelFuture(this, new UnsupportedOperationException());
    }

    public final int hashCode() {
        return this.id.intValue();
    }

    public final boolean equals(Object o) {
        return this == o;
    }

    public final int compareTo(Channel o) {
        return getId().compareTo(o.getId());
    }

    public boolean isOpen() {
        return !this.closeFuture.isDone();
    }

    protected boolean setClosed() {
        allChannels.remove(this.id);
        return this.closeFuture.setClosed();
    }

    public ChannelFuture bind(SocketAddress localAddress) {
        return Channels.bind(this, localAddress);
    }

    public ChannelFuture unbind() {
        return Channels.unbind(this);
    }

    public ChannelFuture close() {
        ChannelCloseFuture returnedCloseFuture = Channels.close(this);
        if ($assertionsDisabled || this.closeFuture == returnedCloseFuture) {
            return this.closeFuture;
        }
        throw new AssertionError();
    }

    public ChannelFuture getCloseFuture() {
        return this.closeFuture;
    }

    public ChannelFuture connect(SocketAddress remoteAddress) {
        return Channels.connect(this, remoteAddress);
    }

    public ChannelFuture disconnect() {
        return Channels.disconnect(this);
    }

    public int getInterestOps() {
        if (!isOpen()) {
            return 4;
        }
        int interestOps = getInternalInterestOps() & -5;
        if (isWritable()) {
            return interestOps;
        }
        return interestOps | 4;
    }

    public ChannelFuture setInterestOps(int interestOps) {
        return Channels.setInterestOps(this, interestOps);
    }

    protected int getInternalInterestOps() {
        return this.interestOps;
    }

    protected void setInternalInterestOps(int interestOps) {
        this.interestOps = interestOps;
    }

    public boolean isReadable() {
        return (getInternalInterestOps() & 1) != 0;
    }

    public boolean isWritable() {
        return this.unwritable == 0;
    }

    public final boolean getUserDefinedWritability(int index) {
        return (this.unwritable & writabilityMask(index)) == 0;
    }

    public final void setUserDefinedWritability(int index, boolean writable) {
        if (writable) {
            setUserDefinedWritability(index);
        } else {
            clearUserDefinedWritability(index);
        }
    }

    private void setUserDefinedWritability(int index) {
        int mask = writabilityMask(index) ^ -1;
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = oldValue & mask;
        } while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue != 0 && newValue == 0) {
            getPipeline().sendUpstream(new UpstreamChannelStateEvent(this, ChannelState.INTEREST_OPS, Integer.valueOf(getInterestOps())));
        }
    }

    private void clearUserDefinedWritability(int index) {
        int mask = writabilityMask(index);
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = oldValue | mask;
        } while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue == 0 && newValue != 0) {
            getPipeline().sendUpstream(new UpstreamChannelStateEvent(this, ChannelState.INTEREST_OPS, Integer.valueOf(getInterestOps())));
        }
    }

    private static int writabilityMask(int index) {
        if (index >= 1 && index <= 31) {
            return 1 << index;
        }
        throw new IllegalArgumentException("index: " + index + " (expected: 1~31)");
    }

    protected boolean setWritable() {
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = oldValue & -2;
        } while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue == 0 || newValue != 0) {
            return false;
        }
        return true;
    }

    protected boolean setUnwritable() {
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = oldValue | 1;
        } while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue != 0 || newValue == 0) {
            return false;
        }
        return true;
    }

    public ChannelFuture setReadable(boolean readable) {
        if (readable) {
            return setInterestOps(getInterestOps() | 1);
        }
        return setInterestOps(getInterestOps() & -2);
    }

    public ChannelFuture write(Object message) {
        return Channels.write(this, message);
    }

    public ChannelFuture write(Object message, SocketAddress remoteAddress) {
        return Channels.write((Channel) this, message, remoteAddress);
    }

    public Object getAttachment() {
        return this.attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public String toString() {
        boolean connected = isConnected();
        if (this.strValConnected == connected && this.strVal != null) {
            return this.strVal;
        }
        StringBuilder buf = new StringBuilder(128);
        buf.append("[id: 0x");
        buf.append(getIdString());
        SocketAddress localAddress = getLocalAddress();
        SocketAddress remoteAddress = getRemoteAddress();
        if (remoteAddress != null) {
            buf.append(", ");
            if (getParent() == null) {
                buf.append(localAddress);
                buf.append(connected ? " => " : " :> ");
                buf.append(remoteAddress);
            } else {
                buf.append(remoteAddress);
                buf.append(connected ? " => " : " :> ");
                buf.append(localAddress);
            }
        } else if (localAddress != null) {
            buf.append(", ");
            buf.append(localAddress);
        }
        buf.append(']');
        String strVal = buf.toString();
        this.strVal = strVal;
        this.strValConnected = connected;
        return strVal;
    }

    private String getIdString() {
        String answer = Integer.toHexString(this.id.intValue());
        switch (answer.length()) {
            case 0:
                return "00000000";
            case 1:
                return "0000000" + answer;
            case 2:
                return "000000" + answer;
            case 3:
                return "00000" + answer;
            case 4:
                return "0000" + answer;
            case 5:
                return "000" + answer;
            case 6:
                return TarConstants.VERSION_POSIX + answer;
            case 7:
                return '0' + answer;
            default:
                return answer;
        }
    }
}
