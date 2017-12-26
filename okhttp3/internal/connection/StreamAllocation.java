package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.Socket;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.EventListener;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RouteSelector.Selection;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.StreamResetException;

public final class StreamAllocation {
    static final /* synthetic */ boolean $assertionsDisabled = (!StreamAllocation.class.desiredAssertionStatus());
    public final Address address;
    public final Call call;
    private final Object callStackTrace;
    private boolean canceled;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    public final EventListener eventListener;
    private int refusedStreamCount;
    private boolean released;
    private boolean reportedAcquired;
    private Route route;
    private Selection routeSelection;
    private final RouteSelector routeSelector;

    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation referent, Object callStackTrace) {
            super(referent);
            this.callStackTrace = callStackTrace;
        }
    }

    public StreamAllocation(ConnectionPool connectionPool, Address address, Call call, EventListener eventListener, Object callStackTrace) {
        this.connectionPool = connectionPool;
        this.address = address;
        this.call = call;
        this.eventListener = eventListener;
        this.routeSelector = new RouteSelector(address, routeDatabase(), call, eventListener);
        this.callStackTrace = callStackTrace;
    }

    public HttpCodec newStream(OkHttpClient client, Chain chain, boolean doExtensiveHealthChecks) {
        try {
            HttpCodec resultCodec = findHealthyConnection(chain.connectTimeoutMillis(), chain.readTimeoutMillis(), chain.writeTimeoutMillis(), client.retryOnConnectionFailure(), doExtensiveHealthChecks).newCodec(client, chain, this);
            synchronized (this.connectionPool) {
                this.codec = resultCodec;
            }
            return resultCodec;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    private RealConnection findHealthyConnection(int connectTimeout, int readTimeout, int writeTimeout, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks) throws IOException {
        RealConnection candidate;
        while (true) {
            candidate = findConnection(connectTimeout, readTimeout, writeTimeout, connectionRetryEnabled);
            synchronized (this.connectionPool) {
                if (candidate.successCount != 0) {
                    if (candidate.isHealthy(doExtensiveHealthChecks)) {
                        break;
                    }
                    noNewStreams();
                } else {
                    break;
                }
            }
        }
        return candidate;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findConnection(int r20, int r21, int r22, boolean r23) throws java.io.IOException {
        /*
        r19 = this;
        r8 = 0;
        r1 = 0;
        r15 = 0;
        r0 = r19;
        r3 = r0.connectionPool;
        monitor-enter(r3);
        r0 = r19;
        r2 = r0.released;	 Catch:{ all -> 0x0016 }
        if (r2 == 0) goto L_0x0019;
    L_0x000e:
        r2 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0016 }
        r4 = "released";
        r2.<init>(r4);	 Catch:{ all -> 0x0016 }
        throw r2;	 Catch:{ all -> 0x0016 }
    L_0x0016:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0016 }
        throw r2;
    L_0x0019:
        r0 = r19;
        r2 = r0.codec;	 Catch:{ all -> 0x0016 }
        if (r2 == 0) goto L_0x0027;
    L_0x001f:
        r2 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0016 }
        r4 = "codec != null";
        r2.<init>(r4);	 Catch:{ all -> 0x0016 }
        throw r2;	 Catch:{ all -> 0x0016 }
    L_0x0027:
        r0 = r19;
        r2 = r0.canceled;	 Catch:{ all -> 0x0016 }
        if (r2 == 0) goto L_0x0035;
    L_0x002d:
        r2 = new java.io.IOException;	 Catch:{ all -> 0x0016 }
        r4 = "Canceled";
        r2.<init>(r4);	 Catch:{ all -> 0x0016 }
        throw r2;	 Catch:{ all -> 0x0016 }
    L_0x0035:
        r0 = r19;
        r11 = r0.connection;	 Catch:{ all -> 0x0016 }
        r18 = r19.releaseIfNoNewStreams();	 Catch:{ all -> 0x0016 }
        r0 = r19;
        r2 = r0.connection;	 Catch:{ all -> 0x0016 }
        if (r2 == 0) goto L_0x0048;
    L_0x0043:
        r0 = r19;
        r1 = r0.connection;	 Catch:{ all -> 0x0016 }
        r11 = 0;
    L_0x0048:
        r0 = r19;
        r2 = r0.reportedAcquired;	 Catch:{ all -> 0x0016 }
        if (r2 != 0) goto L_0x004f;
    L_0x004e:
        r11 = 0;
    L_0x004f:
        if (r1 != 0) goto L_0x006c;
    L_0x0051:
        r2 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x0016 }
        r0 = r19;
        r4 = r0.connectionPool;	 Catch:{ all -> 0x0016 }
        r0 = r19;
        r5 = r0.address;	 Catch:{ all -> 0x0016 }
        r6 = 0;
        r0 = r19;
        r2.get(r4, r5, r0, r6);	 Catch:{ all -> 0x0016 }
        r0 = r19;
        r2 = r0.connection;	 Catch:{ all -> 0x0016 }
        if (r2 == 0) goto L_0x008e;
    L_0x0067:
        r8 = 1;
        r0 = r19;
        r1 = r0.connection;	 Catch:{ all -> 0x0016 }
    L_0x006c:
        monitor-exit(r3);	 Catch:{ all -> 0x0016 }
        okhttp3.internal.Util.closeQuietly(r18);
        if (r11 == 0) goto L_0x007d;
    L_0x0072:
        r0 = r19;
        r2 = r0.eventListener;
        r0 = r19;
        r3 = r0.call;
        r2.connectionReleased(r3, r11);
    L_0x007d:
        if (r8 == 0) goto L_0x008a;
    L_0x007f:
        r0 = r19;
        r2 = r0.eventListener;
        r0 = r19;
        r3 = r0.call;
        r2.connectionAcquired(r3, r1);
    L_0x008a:
        if (r1 == 0) goto L_0x0093;
    L_0x008c:
        r12 = r1;
    L_0x008d:
        return r12;
    L_0x008e:
        r0 = r19;
        r15 = r0.route;	 Catch:{ all -> 0x0016 }
        goto L_0x006c;
    L_0x0093:
        r10 = 0;
        if (r15 != 0) goto L_0x00b3;
    L_0x0096:
        r0 = r19;
        r2 = r0.routeSelection;
        if (r2 == 0) goto L_0x00a6;
    L_0x009c:
        r0 = r19;
        r2 = r0.routeSelection;
        r2 = r2.hasNext();
        if (r2 != 0) goto L_0x00b3;
    L_0x00a6:
        r10 = 1;
        r0 = r19;
        r2 = r0.routeSelector;
        r2 = r2.next();
        r0 = r19;
        r0.routeSelection = r2;
    L_0x00b3:
        r0 = r19;
        r3 = r0.connectionPool;
        monitor-enter(r3);
        r0 = r19;
        r2 = r0.canceled;	 Catch:{ all -> 0x00c6 }
        if (r2 == 0) goto L_0x00c9;
    L_0x00be:
        r2 = new java.io.IOException;	 Catch:{ all -> 0x00c6 }
        r4 = "Canceled";
        r2.<init>(r4);	 Catch:{ all -> 0x00c6 }
        throw r2;	 Catch:{ all -> 0x00c6 }
    L_0x00c6:
        r2 = move-exception;
    L_0x00c7:
        monitor-exit(r3);	 Catch:{ all -> 0x00c6 }
        throw r2;
    L_0x00c9:
        if (r10 == 0) goto L_0x01a1;
    L_0x00cb:
        r0 = r19;
        r2 = r0.routeSelection;	 Catch:{ all -> 0x00c6 }
        r14 = r2.getAll();	 Catch:{ all -> 0x00c6 }
        r9 = 0;
        r16 = r14.size();	 Catch:{ all -> 0x00c6 }
    L_0x00d8:
        r0 = r16;
        if (r9 >= r0) goto L_0x01a1;
    L_0x00dc:
        r13 = r14.get(r9);	 Catch:{ all -> 0x00c6 }
        r13 = (okhttp3.Route) r13;	 Catch:{ all -> 0x00c6 }
        r2 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x00c6 }
        r0 = r19;
        r4 = r0.connectionPool;	 Catch:{ all -> 0x00c6 }
        r0 = r19;
        r5 = r0.address;	 Catch:{ all -> 0x00c6 }
        r0 = r19;
        r2.get(r4, r5, r0, r13);	 Catch:{ all -> 0x00c6 }
        r0 = r19;
        r2 = r0.connection;	 Catch:{ all -> 0x00c6 }
        if (r2 == 0) goto L_0x0136;
    L_0x00f7:
        r8 = 1;
        r0 = r19;
        r1 = r0.connection;	 Catch:{ all -> 0x00c6 }
        r0 = r19;
        r0.route = r13;	 Catch:{ all -> 0x00c6 }
        r12 = r1;
    L_0x0101:
        if (r8 != 0) goto L_0x019f;
    L_0x0103:
        if (r15 != 0) goto L_0x010d;
    L_0x0105:
        r0 = r19;
        r2 = r0.routeSelection;	 Catch:{ all -> 0x019b }
        r15 = r2.next();	 Catch:{ all -> 0x019b }
    L_0x010d:
        r0 = r19;
        r0.route = r15;	 Catch:{ all -> 0x019b }
        r2 = 0;
        r0 = r19;
        r0.refusedStreamCount = r2;	 Catch:{ all -> 0x019b }
        r1 = new okhttp3.internal.connection.RealConnection;	 Catch:{ all -> 0x019b }
        r0 = r19;
        r2 = r0.connectionPool;	 Catch:{ all -> 0x019b }
        r1.<init>(r2, r15);	 Catch:{ all -> 0x019b }
        r2 = 0;
        r0 = r19;
        r0.acquire(r1, r2);	 Catch:{ all -> 0x00c6 }
    L_0x0125:
        monitor-exit(r3);	 Catch:{ all -> 0x00c6 }
        if (r8 == 0) goto L_0x0139;
    L_0x0128:
        r0 = r19;
        r2 = r0.eventListener;
        r0 = r19;
        r3 = r0.call;
        r2.connectionAcquired(r3, r1);
        r12 = r1;
        goto L_0x008d;
    L_0x0136:
        r9 = r9 + 1;
        goto L_0x00d8;
    L_0x0139:
        r0 = r19;
        r6 = r0.call;
        r0 = r19;
        r7 = r0.eventListener;
        r2 = r20;
        r3 = r21;
        r4 = r22;
        r5 = r23;
        r1.connect(r2, r3, r4, r5, r6, r7);
        r2 = r19.routeDatabase();
        r3 = r1.route();
        r2.connected(r3);
        r17 = 0;
        r0 = r19;
        r3 = r0.connectionPool;
        monitor-enter(r3);
        r2 = 1;
        r0 = r19;
        r0.reportedAcquired = r2;	 Catch:{ all -> 0x0198 }
        r2 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x0198 }
        r0 = r19;
        r4 = r0.connectionPool;	 Catch:{ all -> 0x0198 }
        r2.put(r4, r1);	 Catch:{ all -> 0x0198 }
        r2 = r1.isMultiplexed();	 Catch:{ all -> 0x0198 }
        if (r2 == 0) goto L_0x0186;
    L_0x0172:
        r2 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x0198 }
        r0 = r19;
        r4 = r0.connectionPool;	 Catch:{ all -> 0x0198 }
        r0 = r19;
        r5 = r0.address;	 Catch:{ all -> 0x0198 }
        r0 = r19;
        r17 = r2.deduplicate(r4, r5, r0);	 Catch:{ all -> 0x0198 }
        r0 = r19;
        r1 = r0.connection;	 Catch:{ all -> 0x0198 }
    L_0x0186:
        monitor-exit(r3);	 Catch:{ all -> 0x0198 }
        okhttp3.internal.Util.closeQuietly(r17);
        r0 = r19;
        r2 = r0.eventListener;
        r0 = r19;
        r3 = r0.call;
        r2.connectionAcquired(r3, r1);
        r12 = r1;
        goto L_0x008d;
    L_0x0198:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0198 }
        throw r2;
    L_0x019b:
        r2 = move-exception;
        r1 = r12;
        goto L_0x00c7;
    L_0x019f:
        r1 = r12;
        goto L_0x0125;
    L_0x01a1:
        r12 = r1;
        goto L_0x0101;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findConnection(int, int, int, boolean):okhttp3.internal.connection.RealConnection");
    }

    private Socket releaseIfNoNewStreams() {
        if ($assertionsDisabled || Thread.holdsLock(this.connectionPool)) {
            RealConnection allocatedConnection = this.connection;
            if (allocatedConnection == null || !allocatedConnection.noNewStreams) {
                return null;
            }
            return deallocate(false, false, true);
        }
        throw new AssertionError();
    }

    public void streamFinished(boolean noNewStreams, HttpCodec codec, long bytesRead, IOException e) {
        Socket socket;
        this.eventListener.responseBodyEnd(this.call, bytesRead);
        synchronized (this.connectionPool) {
            if (codec != null) {
                if (codec == this.codec) {
                    if (!noNewStreams) {
                        RealConnection realConnection = this.connection;
                        realConnection.successCount++;
                    }
                    Connection releasedConnection = this.connection;
                    socket = deallocate(noNewStreams, false, true);
                    if (this.connection != null) {
                        releasedConnection = null;
                    }
                    boolean callEnd = this.released;
                }
            }
            throw new IllegalStateException("expected " + this.codec + " but was " + codec);
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
        if (e != null) {
            this.eventListener.callFailed(this.call, e);
        } else if (callEnd) {
            this.eventListener.callEnd(this.call);
        }
    }

    public HttpCodec codec() {
        HttpCodec httpCodec;
        synchronized (this.connectionPool) {
            httpCodec = this.codec;
        }
        return httpCodec;
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public synchronized RealConnection connection() {
        return this.connection;
    }

    public void release() {
        Socket socket;
        synchronized (this.connectionPool) {
            Connection releasedConnection = this.connection;
            socket = deallocate(false, true, false);
            if (this.connection != null) {
                releasedConnection = null;
            }
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
    }

    public void noNewStreams() {
        Socket socket;
        synchronized (this.connectionPool) {
            Connection releasedConnection = this.connection;
            socket = deallocate(true, false, false);
            if (this.connection != null) {
                releasedConnection = null;
            }
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
    }

    private Socket deallocate(boolean noNewStreams, boolean released, boolean streamFinished) {
        if ($assertionsDisabled || Thread.holdsLock(this.connectionPool)) {
            if (streamFinished) {
                this.codec = null;
            }
            if (released) {
                this.released = true;
            }
            Socket socket = null;
            if (this.connection != null) {
                if (noNewStreams) {
                    this.connection.noNewStreams = true;
                }
                if (this.codec == null && (this.released || this.connection.noNewStreams)) {
                    release(this.connection);
                    if (this.connection.allocations.isEmpty()) {
                        this.connection.idleAtNanos = System.nanoTime();
                        if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                            socket = this.connection.socket();
                        }
                    }
                    this.connection = null;
                }
            }
            return socket;
        }
        throw new AssertionError();
    }

    public void cancel() {
        synchronized (this.connectionPool) {
            this.canceled = true;
            HttpCodec codecToCancel = this.codec;
            RealConnection connectionToCancel = this.connection;
        }
        if (codecToCancel != null) {
            codecToCancel.cancel();
        } else if (connectionToCancel != null) {
            connectionToCancel.cancel();
        }
    }

    public void streamFailed(IOException e) {
        Socket socket;
        boolean noNewStreams = false;
        synchronized (this.connectionPool) {
            if (e instanceof StreamResetException) {
                StreamResetException streamResetException = (StreamResetException) e;
                if (streamResetException.errorCode == ErrorCode.REFUSED_STREAM) {
                    this.refusedStreamCount++;
                }
                if (streamResetException.errorCode != ErrorCode.REFUSED_STREAM || this.refusedStreamCount > 1) {
                    noNewStreams = true;
                    this.route = null;
                }
            } else if (this.connection != null && (!this.connection.isMultiplexed() || (e instanceof ConnectionShutdownException))) {
                noNewStreams = true;
                if (this.connection.successCount == 0) {
                    if (!(this.route == null || e == null)) {
                        this.routeSelector.connectFailed(this.route, e);
                    }
                    this.route = null;
                }
            }
            Connection releasedConnection = this.connection;
            socket = deallocate(noNewStreams, false, true);
            if (!(this.connection == null && this.reportedAcquired)) {
                releasedConnection = null;
            }
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
    }

    public void acquire(RealConnection connection, boolean reportedAcquired) {
        if (!$assertionsDisabled && !Thread.holdsLock(this.connectionPool)) {
            throw new AssertionError();
        } else if (this.connection != null) {
            throw new IllegalStateException();
        } else {
            this.connection = connection;
            this.reportedAcquired = reportedAcquired;
            connection.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
        }
    }

    private void release(RealConnection connection) {
        int size = connection.allocations.size();
        for (int i = 0; i < size; i++) {
            if (((Reference) connection.allocations.get(i)).get() == this) {
                connection.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }

    public Socket releaseAndAcquire(RealConnection newConnection) {
        if (!$assertionsDisabled && !Thread.holdsLock(this.connectionPool)) {
            throw new AssertionError();
        } else if (this.codec == null && this.connection.allocations.size() == 1) {
            Reference<StreamAllocation> onlyAllocation = (Reference) this.connection.allocations.get(0);
            Socket socket = deallocate(true, false, false);
            this.connection = newConnection;
            newConnection.allocations.add(onlyAllocation);
            return socket;
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean hasMoreRoutes() {
        return this.route != null || ((this.routeSelection != null && this.routeSelection.hasNext()) || this.routeSelector.hasNext());
    }

    public String toString() {
        RealConnection connection = connection();
        return connection != null ? connection.toString() : this.address.toString();
    }
}
