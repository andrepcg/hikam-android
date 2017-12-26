package org.jboss.netty.handler.codec.spdy;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.channel.MessageEvent;

final class SpdySession {
    private static final SpdyProtocolException STREAM_CLOSED = new SpdyProtocolException("Stream closed");
    private final AtomicInteger activeLocalStreams = new AtomicInteger();
    private final AtomicInteger activeRemoteStreams = new AtomicInteger();
    private final Map<Integer, StreamState> activeStreams = new ConcurrentHashMap();
    private final AtomicInteger receiveWindowSize;
    private final AtomicInteger sendWindowSize;
    private final StreamComparator streamComparator = new StreamComparator();

    private final class StreamComparator implements Comparator<Integer>, Serializable {
        private static final long serialVersionUID = 1161471649740544848L;

        StreamComparator() {
        }

        public int compare(Integer id1, Integer id2) {
            int result = ((StreamState) SpdySession.this.activeStreams.get(id1)).getPriority() - ((StreamState) SpdySession.this.activeStreams.get(id2)).getPriority();
            return result != 0 ? result : id1.intValue() - id2.intValue();
        }
    }

    private static final class StreamState {
        private volatile boolean localSideClosed;
        private final ConcurrentLinkedQueue<MessageEvent> pendingWriteQueue = new ConcurrentLinkedQueue();
        private final byte priority;
        private final AtomicInteger receiveWindowSize;
        private volatile int receiveWindowSizeLowerBound;
        private boolean receivedReply;
        private volatile boolean remoteSideClosed;
        private final AtomicInteger sendWindowSize;

        StreamState(byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize) {
            this.priority = priority;
            this.remoteSideClosed = remoteSideClosed;
            this.localSideClosed = localSideClosed;
            this.sendWindowSize = new AtomicInteger(sendWindowSize);
            this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
        }

        byte getPriority() {
            return this.priority;
        }

        boolean isRemoteSideClosed() {
            return this.remoteSideClosed;
        }

        void closeRemoteSide() {
            this.remoteSideClosed = true;
        }

        boolean isLocalSideClosed() {
            return this.localSideClosed;
        }

        void closeLocalSide() {
            this.localSideClosed = true;
        }

        boolean hasReceivedReply() {
            return this.receivedReply;
        }

        void receivedReply() {
            this.receivedReply = true;
        }

        int getSendWindowSize() {
            return this.sendWindowSize.get();
        }

        int updateSendWindowSize(int deltaWindowSize) {
            return this.sendWindowSize.addAndGet(deltaWindowSize);
        }

        int updateReceiveWindowSize(int deltaWindowSize) {
            return this.receiveWindowSize.addAndGet(deltaWindowSize);
        }

        int getReceiveWindowSizeLowerBound() {
            return this.receiveWindowSizeLowerBound;
        }

        void setReceiveWindowSizeLowerBound(int receiveWindowSizeLowerBound) {
            this.receiveWindowSizeLowerBound = receiveWindowSizeLowerBound;
        }

        boolean putPendingWrite(MessageEvent evt) {
            return this.pendingWriteQueue.offer(evt);
        }

        MessageEvent getPendingWrite() {
            return (MessageEvent) this.pendingWriteQueue.peek();
        }

        MessageEvent removePendingWrite() {
            return (MessageEvent) this.pendingWriteQueue.poll();
        }
    }

    public SpdySession(int sendWindowSize, int receiveWindowSize) {
        this.sendWindowSize = new AtomicInteger(sendWindowSize);
        this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
    }

    int numActiveStreams(boolean remote) {
        if (remote) {
            return this.activeRemoteStreams.get();
        }
        return this.activeLocalStreams.get();
    }

    boolean noActiveStreams() {
        return this.activeStreams.isEmpty();
    }

    boolean isActiveStream(int streamId) {
        return this.activeStreams.containsKey(Integer.valueOf(streamId));
    }

    Map<Integer, StreamState> activeStreams() {
        Map<Integer, StreamState> streams = new TreeMap(this.streamComparator);
        streams.putAll(this.activeStreams);
        return streams;
    }

    void acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize, boolean remote) {
        if ((remoteSideClosed && localSideClosed) || ((StreamState) this.activeStreams.put(Integer.valueOf(streamId), new StreamState(priority, remoteSideClosed, localSideClosed, sendWindowSize, receiveWindowSize))) != null) {
            return;
        }
        if (remote) {
            this.activeRemoteStreams.incrementAndGet();
        } else {
            this.activeLocalStreams.incrementAndGet();
        }
    }

    private StreamState removeActiveStream(int streamId, boolean remote) {
        StreamState state = (StreamState) this.activeStreams.remove(Integer.valueOf(streamId));
        if (state != null) {
            if (remote) {
                this.activeRemoteStreams.decrementAndGet();
            } else {
                this.activeLocalStreams.decrementAndGet();
            }
        }
        return state;
    }

    void removeStream(int streamId, boolean remote) {
        StreamState state = removeActiveStream(streamId, remote);
        if (state != null) {
            for (MessageEvent e = state.removePendingWrite(); e != null; e = state.removePendingWrite()) {
                e.getFuture().setFailure(STREAM_CLOSED);
            }
        }
    }

    boolean isRemoteSideClosed(int streamId) {
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        return state == null || state.isRemoteSideClosed();
    }

    void closeRemoteSide(int streamId, boolean remote) {
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        if (state != null) {
            state.closeRemoteSide();
            if (state.isLocalSideClosed()) {
                removeActiveStream(streamId, remote);
            }
        }
    }

    boolean isLocalSideClosed(int streamId) {
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        return state == null || state.isLocalSideClosed();
    }

    void closeLocalSide(int streamId, boolean remote) {
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        if (state != null) {
            state.closeLocalSide();
            if (state.isRemoteSideClosed()) {
                removeActiveStream(streamId, remote);
            }
        }
    }

    boolean hasReceivedReply(int streamId) {
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        return state != null && state.hasReceivedReply();
    }

    void receivedReply(int streamId) {
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        if (state != null) {
            state.receivedReply();
        }
    }

    int getSendWindowSize(int streamId) {
        if (streamId == 0) {
            return this.sendWindowSize.get();
        }
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        return state != null ? state.getSendWindowSize() : -1;
    }

    int updateSendWindowSize(int streamId, int deltaWindowSize) {
        if (streamId == 0) {
            return this.sendWindowSize.addAndGet(deltaWindowSize);
        }
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        return state != null ? state.updateSendWindowSize(deltaWindowSize) : -1;
    }

    int updateReceiveWindowSize(int streamId, int deltaWindowSize) {
        if (streamId == 0) {
            return this.receiveWindowSize.addAndGet(deltaWindowSize);
        }
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        if (deltaWindowSize > 0) {
            state.setReceiveWindowSizeLowerBound(0);
        }
        return state != null ? state.updateReceiveWindowSize(deltaWindowSize) : -1;
    }

    int getReceiveWindowSizeLowerBound(int streamId) {
        if (streamId == 0) {
            return 0;
        }
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        if (state != null) {
            return state.getReceiveWindowSizeLowerBound();
        }
        return 0;
    }

    void updateAllSendWindowSizes(int deltaWindowSize) {
        for (StreamState state : this.activeStreams.values()) {
            state.updateSendWindowSize(deltaWindowSize);
        }
    }

    void updateAllReceiveWindowSizes(int deltaWindowSize) {
        for (StreamState state : this.activeStreams.values()) {
            state.updateReceiveWindowSize(deltaWindowSize);
            if (deltaWindowSize < 0) {
                state.setReceiveWindowSizeLowerBound(deltaWindowSize);
            }
        }
    }

    boolean putPendingWrite(int streamId, MessageEvent evt) {
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        return state != null && state.putPendingWrite(evt);
    }

    MessageEvent getPendingWrite(int streamId) {
        StreamState state;
        if (streamId == 0) {
            for (Entry<Integer, StreamState> e : activeStreams().entrySet()) {
                state = (StreamState) e.getValue();
                if (state.getSendWindowSize() > 0) {
                    MessageEvent evt = state.getPendingWrite();
                    if (evt != null) {
                        return evt;
                    }
                }
            }
            return null;
        }
        state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        if (state != null) {
            return state.getPendingWrite();
        }
        return null;
    }

    MessageEvent removePendingWrite(int streamId) {
        StreamState state = (StreamState) this.activeStreams.get(Integer.valueOf(streamId));
        return state != null ? state.removePendingWrite() : null;
    }
}
