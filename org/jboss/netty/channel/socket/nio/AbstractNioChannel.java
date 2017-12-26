package org.jboss.netty.channel.socket.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.AbstractChannel;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.internal.ThreadLocalBoolean;

abstract class AbstractNioChannel<C extends SelectableChannel & WritableByteChannel> extends AbstractChannel {
    final C channel;
    SendBuffer currentWriteBuffer;
    MessageEvent currentWriteEvent;
    final AtomicInteger highWaterMarkCounter = new AtomicInteger();
    boolean inWriteNowLoop;
    private volatile InetSocketAddress localAddress;
    volatile InetSocketAddress remoteAddress;
    final AbstractNioWorker worker;
    final Queue<MessageEvent> writeBufferQueue = new WriteRequestQueue();
    final AtomicInteger writeBufferSize = new AtomicInteger();
    final Object writeLock = new Object();
    boolean writeSuspended;
    final Runnable writeTask = new WriteTask();
    final AtomicBoolean writeTaskInTaskQueue = new AtomicBoolean();

    private final class WriteRequestQueue implements Queue<MessageEvent> {
        static final /* synthetic */ boolean $assertionsDisabled = (!AbstractNioChannel.class.desiredAssertionStatus());
        private final ThreadLocalBoolean notifying = new ThreadLocalBoolean();
        private final Queue<MessageEvent> queue = new ConcurrentLinkedQueue();

        public MessageEvent remove() {
            return (MessageEvent) this.queue.remove();
        }

        public MessageEvent element() {
            return (MessageEvent) this.queue.element();
        }

        public MessageEvent peek() {
            return (MessageEvent) this.queue.peek();
        }

        public int size() {
            return this.queue.size();
        }

        public boolean isEmpty() {
            return this.queue.isEmpty();
        }

        public Iterator<MessageEvent> iterator() {
            return this.queue.iterator();
        }

        public Object[] toArray() {
            return this.queue.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return this.queue.toArray(a);
        }

        public boolean containsAll(Collection<?> c) {
            return this.queue.containsAll(c);
        }

        public boolean addAll(Collection<? extends MessageEvent> c) {
            return this.queue.addAll(c);
        }

        public boolean removeAll(Collection<?> c) {
            return this.queue.removeAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            return this.queue.retainAll(c);
        }

        public void clear() {
            this.queue.clear();
        }

        public boolean add(MessageEvent e) {
            return this.queue.add(e);
        }

        public boolean remove(Object o) {
            return this.queue.remove(o);
        }

        public boolean contains(Object o) {
            return this.queue.contains(o);
        }

        public boolean offer(MessageEvent e) {
            boolean success = this.queue.offer(e);
            if ($assertionsDisabled || success) {
                int messageSize = getMessageSize(e);
                int newWriteBufferSize = AbstractNioChannel.this.writeBufferSize.addAndGet(messageSize);
                int highWaterMark = AbstractNioChannel.this.getConfig().getWriteBufferHighWaterMark();
                if (newWriteBufferSize >= highWaterMark && newWriteBufferSize - messageSize < highWaterMark) {
                    AbstractNioChannel.this.highWaterMarkCounter.incrementAndGet();
                    if (AbstractNioChannel.this.setUnwritable()) {
                        if (!AbstractNioWorker.isIoThread(AbstractNioChannel.this)) {
                            Channels.fireChannelInterestChangedLater(AbstractNioChannel.this);
                        } else if (!((Boolean) this.notifying.get()).booleanValue()) {
                            this.notifying.set(Boolean.TRUE);
                            Channels.fireChannelInterestChanged(AbstractNioChannel.this);
                            this.notifying.set(Boolean.FALSE);
                        }
                    }
                }
                return true;
            }
            throw new AssertionError();
        }

        public MessageEvent poll() {
            MessageEvent e = (MessageEvent) this.queue.poll();
            if (e != null) {
                int messageSize = getMessageSize(e);
                int newWriteBufferSize = AbstractNioChannel.this.writeBufferSize.addAndGet(-messageSize);
                int lowWaterMark = AbstractNioChannel.this.getConfig().getWriteBufferLowWaterMark();
                if ((newWriteBufferSize == 0 || newWriteBufferSize < lowWaterMark) && newWriteBufferSize + messageSize >= lowWaterMark) {
                    AbstractNioChannel.this.highWaterMarkCounter.decrementAndGet();
                    if (AbstractNioChannel.this.isConnected() && AbstractNioChannel.this.setWritable()) {
                        if (!AbstractNioWorker.isIoThread(AbstractNioChannel.this)) {
                            Channels.fireChannelInterestChangedLater(AbstractNioChannel.this);
                        } else if (!((Boolean) this.notifying.get()).booleanValue()) {
                            this.notifying.set(Boolean.TRUE);
                            Channels.fireChannelInterestChanged(AbstractNioChannel.this);
                            this.notifying.set(Boolean.FALSE);
                        }
                    }
                }
            }
            return e;
        }

        private int getMessageSize(MessageEvent e) {
            Object m = e.getMessage();
            if (m instanceof ChannelBuffer) {
                return ((ChannelBuffer) m).readableBytes();
            }
            return 0;
        }
    }

    private final class WriteTask implements Runnable {
        WriteTask() {
        }

        public void run() {
            AbstractNioChannel.this.writeTaskInTaskQueue.set(false);
            AbstractNioChannel.this.worker.writeFromTaskLoop(AbstractNioChannel.this);
        }
    }

    public abstract NioChannelConfig getConfig();

    abstract InetSocketAddress getLocalSocketAddress() throws Exception;

    abstract InetSocketAddress getRemoteSocketAddress() throws Exception;

    protected AbstractNioChannel(Integer id, Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, AbstractNioWorker worker, C ch) {
        super(id, parent, factory, pipeline, sink);
        this.worker = worker;
        this.channel = ch;
    }

    protected AbstractNioChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, AbstractNioWorker worker, C ch) {
        super(parent, factory, pipeline, sink);
        this.worker = worker;
        this.channel = ch;
    }

    public AbstractNioWorker getWorker() {
        return this.worker;
    }

    public InetSocketAddress getLocalAddress() {
        InetSocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            try {
                localAddress = getLocalSocketAddress();
                if (localAddress.getAddress().isAnyLocalAddress()) {
                    return localAddress;
                }
                this.localAddress = localAddress;
            } catch (Throwable th) {
                return null;
            }
        }
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        InetSocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress == null) {
            try {
                remoteAddress = getRemoteSocketAddress();
                this.remoteAddress = remoteAddress;
            } catch (Throwable th) {
                return null;
            }
        }
        return remoteAddress;
    }

    protected int getInternalInterestOps() {
        return super.getInternalInterestOps();
    }

    protected void setInternalInterestOps(int interestOps) {
        super.setInternalInterestOps(interestOps);
    }

    protected boolean setClosed() {
        return super.setClosed();
    }
}
