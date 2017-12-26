package org.jboss.netty.handler.ssl;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SslBufferPool {
    private static final int DEFAULT_POOL_SIZE = 19267584;
    private static final int MAX_PACKET_SIZE_ALIGNED = 18816;
    private final boolean allocateDirect;
    private final int maxBufferCount;
    private final AtomicInteger numAllocations;
    private final BlockingQueue<ByteBuffer> pool;
    private final ByteBuffer preallocated;

    public SslBufferPool() {
        this(DEFAULT_POOL_SIZE);
    }

    public SslBufferPool(boolean preallocate, boolean allocateDirect) {
        this(DEFAULT_POOL_SIZE, preallocate, allocateDirect);
    }

    public SslBufferPool(int maxPoolSize) {
        this(maxPoolSize, false, false);
    }

    public SslBufferPool(int maxPoolSize, boolean preallocate, boolean allocateDirect) {
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("maxPoolSize: " + maxPoolSize);
        }
        int maxBufferCount = maxPoolSize / MAX_PACKET_SIZE_ALIGNED;
        if (maxPoolSize % MAX_PACKET_SIZE_ALIGNED != 0) {
            maxBufferCount++;
        }
        this.maxBufferCount = maxBufferCount;
        this.allocateDirect = allocateDirect;
        this.pool = new ArrayBlockingQueue(maxBufferCount);
        if (preallocate) {
            this.preallocated = allocate(maxBufferCount * MAX_PACKET_SIZE_ALIGNED);
            this.numAllocations = null;
            for (int i = 0; i < maxBufferCount; i++) {
                int pos = i * MAX_PACKET_SIZE_ALIGNED;
                this.preallocated.clear().position(pos).limit(pos + MAX_PACKET_SIZE_ALIGNED);
                this.pool.add(this.preallocated.slice());
            }
            return;
        }
        this.preallocated = null;
        this.numAllocations = new AtomicInteger();
    }

    public int getMaxPoolSize() {
        return this.maxBufferCount * MAX_PACKET_SIZE_ALIGNED;
    }

    public int getUnacquiredPoolSize() {
        return this.pool.size() * MAX_PACKET_SIZE_ALIGNED;
    }

    public ByteBuffer acquireBuffer() {
        ByteBuffer buf;
        if (this.preallocated != null || this.numAllocations.get() >= this.maxBufferCount) {
            boolean interrupted = false;
            while (true) {
                try {
                    buf = (ByteBuffer) this.pool.take();
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        } else {
            buf = (ByteBuffer) this.pool.poll();
            if (buf == null) {
                this.numAllocations.incrementAndGet();
                buf = allocate(18713);
            }
        }
        buf.clear();
        return buf;
    }

    public void releaseBuffer(ByteBuffer buffer) {
        this.pool.offer(buffer);
    }

    private ByteBuffer allocate(int capacity) {
        if (this.allocateDirect) {
            return ByteBuffer.allocateDirect(capacity);
        }
        return ByteBuffer.allocate(capacity);
    }
}
