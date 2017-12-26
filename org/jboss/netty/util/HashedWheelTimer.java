package org.jboss.netty.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.DetectionUtil;
import org.jboss.netty.util.internal.SharedResourceMisuseDetector;

public class HashedWheelTimer implements Timer {
    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    public static final int WORKER_STATE_STARTED = 1;
    private static final AtomicIntegerFieldUpdater<HashedWheelTimer> WORKER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimer.class, "workerState");
    private static final AtomicInteger id = new AtomicInteger();
    static final InternalLogger logger = InternalLoggerFactory.getInstance(HashedWheelTimer.class);
    private static final SharedResourceMisuseDetector misuseDetector = new SharedResourceMisuseDetector(HashedWheelTimer.class);
    private final int mask;
    private volatile long startTime;
    private final CountDownLatch startTimeInitialized;
    private final long tickDuration;
    private final Queue<HashedWheelTimeout> timeouts;
    private final HashedWheelBucket[] wheel;
    private final Worker worker;
    private volatile int workerState;
    private final Thread workerThread;

    private static final class HashedWheelBucket {
        static final /* synthetic */ boolean $assertionsDisabled = (!HashedWheelTimer.class.desiredAssertionStatus());
        private HashedWheelTimeout head;
        private HashedWheelTimeout tail;

        private HashedWheelBucket() {
        }

        public void addTimeout(HashedWheelTimeout timeout) {
            if ($assertionsDisabled || timeout.bucket == null) {
                timeout.bucket = this;
                if (this.head == null) {
                    this.tail = timeout;
                    this.head = timeout;
                    return;
                }
                this.tail.next = timeout;
                timeout.prev = this.tail;
                this.tail = timeout;
                return;
            }
            throw new AssertionError();
        }

        public void expireTimeouts(long deadline) {
            HashedWheelTimeout timeout = this.head;
            while (timeout != null) {
                boolean remove = false;
                if (timeout.remainingRounds <= 0) {
                    if (timeout.deadline <= deadline) {
                        timeout.expire();
                        remove = true;
                    } else {
                        throw new IllegalStateException(String.format("timeout.deadline (%d) > deadline (%d)", new Object[]{Long.valueOf(timeout.deadline), Long.valueOf(deadline)}));
                    }
                } else if (timeout.isCancelled()) {
                    remove = true;
                } else {
                    timeout.remainingRounds--;
                }
                HashedWheelTimeout next = timeout.next;
                if (remove) {
                    remove(timeout);
                }
                timeout = next;
            }
        }

        public void remove(HashedWheelTimeout timeout) {
            HashedWheelTimeout next = timeout.next;
            if (timeout.prev != null) {
                timeout.prev.next = next;
            }
            if (timeout.next != null) {
                timeout.next.prev = timeout.prev;
            }
            if (timeout == this.head) {
                if (timeout == this.tail) {
                    this.tail = null;
                    this.head = null;
                } else {
                    this.head = next;
                }
            } else if (timeout == this.tail) {
                this.tail = timeout.prev;
            }
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
        }

        public void clearTimeouts(Set<Timeout> set) {
            while (true) {
                HashedWheelTimeout timeout = pollTimeout();
                if (timeout != null) {
                    if (!(timeout.isExpired() || timeout.isCancelled())) {
                        set.add(timeout);
                    }
                } else {
                    return;
                }
            }
        }

        private HashedWheelTimeout pollTimeout() {
            HashedWheelTimeout head = this.head;
            if (head == null) {
                return null;
            }
            HashedWheelTimeout next = head.next;
            if (next == null) {
                this.head = null;
                this.tail = null;
            } else {
                this.head = next;
                next.prev = null;
            }
            head.next = null;
            head.prev = null;
            return head;
        }
    }

    private final class Worker implements Runnable {
        private long tick;
        private final Set<Timeout> unprocessedTimeouts;

        private Worker() {
            this.unprocessedTimeouts = new HashSet();
        }

        public void run() {
            HashedWheelTimer.this.startTime = System.nanoTime();
            if (HashedWheelTimer.this.startTime == 0) {
                HashedWheelTimer.this.startTime = 1;
            }
            HashedWheelTimer.this.startTimeInitialized.countDown();
            do {
                long deadline = waitForNextTick();
                if (deadline > 0) {
                    transferTimeoutsToBuckets();
                    HashedWheelTimer.this.wheel[(int) (this.tick & ((long) HashedWheelTimer.this.mask))].expireTimeouts(deadline);
                    this.tick++;
                }
            } while (HashedWheelTimer.WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == 1);
            for (HashedWheelBucket bucket : HashedWheelTimer.this.wheel) {
                bucket.clearTimeouts(this.unprocessedTimeouts);
            }
            while (true) {
                HashedWheelTimeout timeout = (HashedWheelTimeout) HashedWheelTimer.this.timeouts.poll();
                if (timeout != null) {
                    this.unprocessedTimeouts.add(timeout);
                } else {
                    return;
                }
            }
        }

        private void transferTimeoutsToBuckets() {
            int i = 0;
            while (i < BZip2Constants.BASEBLOCKSIZE) {
                HashedWheelTimeout timeout = (HashedWheelTimeout) HashedWheelTimer.this.timeouts.poll();
                if (timeout != null) {
                    if (timeout.state() == 2 || !timeout.compareAndSetState(0, 1)) {
                        timeout.remove();
                    } else {
                        long calculated = timeout.deadline / HashedWheelTimer.this.tickDuration;
                        timeout.remainingRounds = (calculated - this.tick) / ((long) HashedWheelTimer.this.wheel.length);
                        HashedWheelTimer.this.wheel[(int) (((long) HashedWheelTimer.this.mask) & Math.max(calculated, this.tick))].addTimeout(timeout);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }

        private long waitForNextTick() {
            long deadline = HashedWheelTimer.this.tickDuration * (this.tick + 1);
            while (true) {
                long currentTime = System.nanoTime() - HashedWheelTimer.this.startTime;
                long sleepTimeMs = ((deadline - currentTime) + 999999) / 1000000;
                if (sleepTimeMs <= 0) {
                    break;
                }
                if (DetectionUtil.isWindows()) {
                    sleepTimeMs = (sleepTimeMs / 10) * 10;
                }
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException e) {
                    if (HashedWheelTimer.WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == 2) {
                        return Long.MIN_VALUE;
                    }
                }
            }
            if (currentTime == Long.MIN_VALUE) {
                return -9223372036854775807L;
            }
            return currentTime;
        }

        public Set<Timeout> unprocessedTimeouts() {
            return Collections.unmodifiableSet(this.unprocessedTimeouts);
        }
    }

    private static final class HashedWheelTimeout implements Timeout {
        static final /* synthetic */ boolean $assertionsDisabled = (!HashedWheelTimer.class.desiredAssertionStatus());
        private static final AtomicIntegerFieldUpdater<HashedWheelTimeout> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimeout.class, "state");
        private static final int ST_CANCELLED = 2;
        private static final int ST_EXPIRED = 3;
        private static final int ST_INIT = 0;
        private static final int ST_IN_BUCKET = 1;
        HashedWheelBucket bucket;
        private final long deadline;
        HashedWheelTimeout next;
        HashedWheelTimeout prev;
        long remainingRounds;
        private volatile int state = 0;
        private final TimerTask task;
        private final HashedWheelTimer timer;

        HashedWheelTimeout(HashedWheelTimer timer, TimerTask task, long deadline) {
            this.timer = timer;
            this.task = task;
            this.deadline = deadline;
        }

        public Timer getTimer() {
            return this.timer;
        }

        public TimerTask getTask() {
            return this.task;
        }

        public void cancel() {
            int state = state();
            if (state < 2) {
                if ((state == 1 || !compareAndSetState(0, 2)) && compareAndSetState(1, 2)) {
                    this.timer.timeouts.add(this);
                }
            }
        }

        public void remove() {
            if (this.bucket != null) {
                this.bucket.remove(this);
            }
        }

        public boolean compareAndSetState(int expected, int state) {
            return STATE_UPDATER.compareAndSet(this, expected, state);
        }

        public int state() {
            return this.state;
        }

        public boolean isCancelled() {
            return this.state == 2;
        }

        public boolean isExpired() {
            return this.state > 1;
        }

        public HashedWheelTimeout value() {
            return this;
        }

        public void expire() {
            if (compareAndSetState(1, 3)) {
                try {
                    this.task.run(this);
                } catch (Throwable t) {
                    if (HashedWheelTimer.logger.isWarnEnabled()) {
                        HashedWheelTimer.logger.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
                    }
                }
            } else if (!$assertionsDisabled && state() == 0) {
                throw new AssertionError();
            }
        }

        public String toString() {
            long remaining = (this.deadline - System.nanoTime()) + this.timer.startTime;
            StringBuilder buf = new StringBuilder(192);
            buf.append(getClass().getSimpleName());
            buf.append('(');
            buf.append("deadline: ");
            if (remaining > 0) {
                buf.append(remaining);
                buf.append(" ns later");
            } else if (remaining < 0) {
                buf.append(-remaining);
                buf.append(" ns ago");
            } else {
                buf.append("now");
            }
            if (isCancelled()) {
                buf.append(", cancelled");
            }
            buf.append(", task: ");
            buf.append(getTask());
            return buf.append(')').toString();
        }
    }

    public HashedWheelTimer() {
        this(Executors.defaultThreadFactory());
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit) {
        this(Executors.defaultThreadFactory(), tickDuration, unit);
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(Executors.defaultThreadFactory(), tickDuration, unit, ticksPerWheel);
    }

    public HashedWheelTimer(ThreadFactory threadFactory) {
        this(threadFactory, 100, TimeUnit.MILLISECONDS);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit) {
        this(threadFactory, tickDuration, unit, 512);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(threadFactory, null, tickDuration, unit, ticksPerWheel);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, ThreadNameDeterminer determiner, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this.worker = new Worker();
        this.workerState = 0;
        this.startTimeInitialized = new CountDownLatch(1);
        this.timeouts = new ConcurrentLinkedQueue();
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        } else if (unit == null) {
            throw new NullPointerException("unit");
        } else if (tickDuration <= 0) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        } else if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        } else {
            this.wheel = createWheel(ticksPerWheel);
            this.mask = this.wheel.length - 1;
            this.tickDuration = unit.toNanos(tickDuration);
            if (this.tickDuration >= Long.MAX_VALUE / ((long) this.wheel.length)) {
                throw new IllegalArgumentException(String.format("tickDuration: %d (expected: 0 < tickDuration in nanos < %d", new Object[]{Long.valueOf(tickDuration), Long.valueOf(Long.MAX_VALUE / ((long) this.wheel.length))}));
            }
            this.workerThread = threadFactory.newThread(new ThreadRenamingRunnable(this.worker, "Hashed wheel timer #" + id.incrementAndGet(), determiner));
            misuseDetector.increase();
        }
    }

    private static HashedWheelBucket[] createWheel(int ticksPerWheel) {
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        } else if (ticksPerWheel > 1073741824) {
            throw new IllegalArgumentException("ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
        } else {
            HashedWheelBucket[] wheel = new HashedWheelBucket[normalizeTicksPerWheel(ticksPerWheel)];
            for (int i = 0; i < wheel.length; i++) {
                wheel[i] = new HashedWheelBucket();
            }
            return wheel;
        }
    }

    private static int normalizeTicksPerWheel(int ticksPerWheel) {
        int normalizedTicksPerWheel = 1;
        while (normalizedTicksPerWheel < ticksPerWheel) {
            normalizedTicksPerWheel <<= 1;
        }
        return normalizedTicksPerWheel;
    }

    public void start() {
        switch (WORKER_STATE_UPDATER.get(this)) {
            case 0:
                if (WORKER_STATE_UPDATER.compareAndSet(this, 0, 1)) {
                    this.workerThread.start();
                    break;
                }
                break;
            case 1:
                break;
            case 2:
                throw new IllegalStateException("cannot be started once stopped");
            default:
                throw new Error("Invalid WorkerState");
        }
        while (this.startTime == 0) {
            try {
                this.startTimeInitialized.await();
            } catch (InterruptedException e) {
            }
        }
    }

    public Set<Timeout> stop() {
        if (Thread.currentThread() == this.workerThread) {
            throw new IllegalStateException(HashedWheelTimer.class.getSimpleName() + ".stop() cannot be called from " + TimerTask.class.getSimpleName());
        } else if (WORKER_STATE_UPDATER.compareAndSet(this, 1, 2)) {
            boolean interrupted = false;
            while (this.workerThread.isAlive()) {
                this.workerThread.interrupt();
                try {
                    this.workerThread.join(100);
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
            misuseDetector.decrease();
            return this.worker.unprocessedTimeouts();
        } else {
            WORKER_STATE_UPDATER.set(this, 2);
            misuseDetector.decrease();
            return Collections.emptySet();
        }
    }

    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        if (task == null) {
            throw new NullPointerException("task");
        } else if (unit == null) {
            throw new NullPointerException("unit");
        } else {
            start();
            HashedWheelTimeout timeout = new HashedWheelTimeout(this, task, (System.nanoTime() + unit.toNanos(delay)) - this.startTime);
            this.timeouts.add(timeout);
            return timeout;
        }
    }
}
