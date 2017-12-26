package org.jboss.netty.handler.ssl;

import android.support.v4.os.EnvironmentCompat;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;
import org.apache.tomcat.jni.Buffer;
import org.apache.tomcat.jni.SSL;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.EmptyArrays;

public final class OpenSslEngine extends SSLEngine {
    static final /* synthetic */ boolean $assertionsDisabled = (!OpenSslEngine.class.desiredAssertionStatus());
    private static final AtomicIntegerFieldUpdater<OpenSslEngine> DESTROYED_UPDATER = AtomicIntegerFieldUpdater.newUpdater(OpenSslEngine.class, "destroyed");
    private static final Certificate[] EMPTY_CERTIFICATES = new Certificate[0];
    private static final X509Certificate[] EMPTY_X509_CERTIFICATES = new X509Certificate[0];
    private static final SSLException ENCRYPTED_PACKET_OVERSIZED = new SSLException("encrypted packet oversized");
    private static final SSLException ENGINE_CLOSED = new SSLException("engine closed");
    private static final int MAX_CIPHERTEXT_LENGTH = 18432;
    private static final int MAX_COMPRESSED_LENGTH = 17408;
    static final int MAX_ENCRYPTED_PACKET_LENGTH = 18713;
    private static final int MAX_PLAINTEXT_LENGTH = 16384;
    private static final SSLException RENEGOTIATION_UNSUPPORTED = new SSLException("renegotiation unsupported");
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSslEngine.class);
    private int accepted;
    private volatile String applicationProtocol;
    private final SslBufferPool bufPool;
    private String cipher;
    private volatile int destroyed;
    private boolean engineClosed;
    private final String fallbackApplicationProtocol;
    private boolean handshakeFinished;
    private boolean isInboundDone;
    private boolean isOutboundDone;
    private int lastPrimingReadResult;
    private long networkBIO;
    private boolean receivedShutdown;
    private SSLSession session;
    private long ssl;

    class C08611 implements SSLSession {
        C08611() {
        }

        public byte[] getId() {
            return String.valueOf(OpenSslEngine.this.ssl).getBytes();
        }

        public SSLSessionContext getSessionContext() {
            return null;
        }

        public long getCreationTime() {
            return 0;
        }

        public long getLastAccessedTime() {
            return 0;
        }

        public void invalidate() {
        }

        public boolean isValid() {
            return false;
        }

        public void putValue(String s, Object o) {
        }

        public Object getValue(String s) {
            return null;
        }

        public void removeValue(String s) {
        }

        public String[] getValueNames() {
            return EmptyArrays.EMPTY_STRINGS;
        }

        public Certificate[] getPeerCertificates() {
            return OpenSslEngine.EMPTY_CERTIFICATES;
        }

        public Certificate[] getLocalCertificates() {
            return OpenSslEngine.EMPTY_CERTIFICATES;
        }

        public X509Certificate[] getPeerCertificateChain() {
            return OpenSslEngine.EMPTY_X509_CERTIFICATES;
        }

        public Principal getPeerPrincipal() {
            return null;
        }

        public Principal getLocalPrincipal() {
            return null;
        }

        public String getCipherSuite() {
            return OpenSslEngine.this.cipher;
        }

        public String getProtocol() {
            String applicationProtocol = OpenSslEngine.this.applicationProtocol;
            if (applicationProtocol == null) {
                return EnvironmentCompat.MEDIA_UNKNOWN;
            }
            return "unknown:" + applicationProtocol;
        }

        public String getPeerHost() {
            return null;
        }

        public int getPeerPort() {
            return 0;
        }

        public int getPacketBufferSize() {
            return OpenSslEngine.MAX_ENCRYPTED_PACKET_LENGTH;
        }

        public int getApplicationBufferSize() {
            return 16384;
        }
    }

    static {
        ENGINE_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        RENEGOTIATION_UNSUPPORTED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        ENCRYPTED_PACKET_OVERSIZED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    }

    public OpenSslEngine(long sslCtx, SslBufferPool bufPool, String fallbackApplicationProtocol) {
        OpenSsl.ensureAvailability();
        if (sslCtx == 0) {
            throw new NullPointerException("sslContext");
        } else if (bufPool == null) {
            throw new NullPointerException("bufPool");
        } else {
            this.bufPool = bufPool;
            this.ssl = SSL.newSSL(sslCtx, true);
            this.networkBIO = SSL.makeNetworkBIO(this.ssl);
            this.fallbackApplicationProtocol = fallbackApplicationProtocol;
        }
    }

    public synchronized void shutdown() {
        if (DESTROYED_UPDATER.compareAndSet(this, 0, 1)) {
            SSL.freeSSL(this.ssl);
            SSL.freeBIO(this.networkBIO);
            this.networkBIO = 0;
            this.ssl = 0;
            this.engineClosed = true;
            this.isOutboundDone = true;
            this.isInboundDone = true;
        }
    }

    private int writePlaintextData(ByteBuffer src) {
        int sslWrote;
        int pos = src.position();
        int limit = src.limit();
        int len = Math.min(limit - pos, 16384);
        if (src.isDirect()) {
            sslWrote = SSL.writeToSSL(this.ssl, Buffer.address(src) + ((long) pos), len);
            if (sslWrote > 0) {
                src.position(pos + sslWrote);
                return sslWrote;
            }
        }
        ByteBuffer buf = this.bufPool.acquireBuffer();
        try {
            if (!$assertionsDisabled && !buf.isDirect()) {
                throw new AssertionError();
            } else if ($assertionsDisabled || len <= buf.capacity()) {
                long addr = Buffer.address(buf);
                src.limit(pos + len);
                buf.put(src);
                src.limit(limit);
                sslWrote = SSL.writeToSSL(this.ssl, addr, len);
                if (sslWrote > 0) {
                    src.position(pos + sslWrote);
                    return sslWrote;
                }
                src.position(pos);
                this.bufPool.releaseBuffer(buf);
            } else {
                throw new AssertionError("buffer pool write overflow");
            }
        } finally {
            this.bufPool.releaseBuffer(buf);
        }
        throw new IllegalStateException("SSL.writeToSSL() returned a non-positive value: " + sslWrote);
    }

    private int writeEncryptedData(ByteBuffer src) {
        int pos = src.position();
        int len = src.remaining();
        long addr;
        int netWrote;
        if (src.isDirect()) {
            addr = Buffer.address(src) + ((long) pos);
            netWrote = SSL.writeToBIO(this.networkBIO, addr, len);
            if (netWrote >= 0) {
                src.position(pos + netWrote);
                this.lastPrimingReadResult = SSL.readFromSSL(this.ssl, addr, 0);
                return netWrote;
            }
        }
        ByteBuffer buf = this.bufPool.acquireBuffer();
        try {
            if (!$assertionsDisabled && !buf.isDirect()) {
                throw new AssertionError();
            } else if ($assertionsDisabled || len <= buf.capacity()) {
                addr = Buffer.address(buf);
                buf.put(src);
                netWrote = SSL.writeToBIO(this.networkBIO, addr, len);
                if (netWrote >= 0) {
                    src.position(pos + netWrote);
                    this.lastPrimingReadResult = SSL.readFromSSL(this.ssl, addr, 0);
                    return netWrote;
                }
                src.position(pos);
                this.bufPool.releaseBuffer(buf);
            } else {
                throw new AssertionError();
            }
        } finally {
            this.bufPool.releaseBuffer(buf);
        }
        return 0;
    }

    private int readPlaintextData(ByteBuffer dst) {
        int sslRead;
        if (dst.isDirect()) {
            int pos = dst.position();
            sslRead = SSL.readFromSSL(this.ssl, Buffer.address(dst) + ((long) pos), dst.limit() - pos);
            if (sslRead > 0) {
                dst.position(pos + sslRead);
                return sslRead;
            }
        }
        ByteBuffer buf = this.bufPool.acquireBuffer();
        try {
            if ($assertionsDisabled || buf.isDirect()) {
                long addr = Buffer.address(buf);
                int len = Math.min(buf.capacity(), dst.remaining());
                buf.limit(len);
                sslRead = SSL.readFromSSL(this.ssl, addr, len);
                if (sslRead > 0) {
                    buf.limit(sslRead);
                    dst.put(buf);
                    return sslRead;
                }
                this.bufPool.releaseBuffer(buf);
            } else {
                throw new AssertionError();
            }
        } finally {
            this.bufPool.releaseBuffer(buf);
        }
        return 0;
    }

    private int readEncryptedData(ByteBuffer dst, int pending) {
        int bioRead;
        if (!dst.isDirect() || dst.remaining() < pending) {
            ByteBuffer buf = this.bufPool.acquireBuffer();
            try {
                if ($assertionsDisabled || buf.isDirect()) {
                    long addr = Buffer.address(buf);
                    if ($assertionsDisabled || buf.capacity() >= pending) {
                        bioRead = SSL.readFromBIO(this.networkBIO, addr, pending);
                        if (bioRead > 0) {
                            buf.limit(bioRead);
                            dst.put(buf);
                            return bioRead;
                        }
                        this.bufPool.releaseBuffer(buf);
                    } else {
                        throw new AssertionError("network BIO read overflow (pending: " + pending + ", capacity: " + buf.capacity() + ')');
                    }
                }
                throw new AssertionError();
            } finally {
                this.bufPool.releaseBuffer(buf);
            }
        } else {
            int pos = dst.position();
            bioRead = SSL.readFromBIO(this.networkBIO, Buffer.address(dst) + ((long) pos), pending);
            if (bioRead > 0) {
                dst.position(pos + bioRead);
                return bioRead;
            }
        }
        return 0;
    }

    public synchronized javax.net.ssl.SSLEngineResult wrap(java.nio.ByteBuffer[] r16, int r17, int r18, java.nio.ByteBuffer r19) throws javax.net.ssl.SSLException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:org.jboss.netty.handler.ssl.OpenSslEngine.wrap(java.nio.ByteBuffer[], int, int, java.nio.ByteBuffer):javax.net.ssl.SSLEngineResult. bs: [B:44:0x00bc, B:62:0x00f0, B:74:0x0117]
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:86)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r15 = this;
        monitor-enter(r15);
        r10 = r15.destroyed;	 Catch:{ all -> 0x001c }
        if (r10 == 0) goto L_0x0012;	 Catch:{ all -> 0x001c }
    L_0x0005:
        r10 = new javax.net.ssl.SSLEngineResult;	 Catch:{ all -> 0x001c }
        r11 = javax.net.ssl.SSLEngineResult.Status.CLOSED;	 Catch:{ all -> 0x001c }
        r12 = javax.net.ssl.SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;	 Catch:{ all -> 0x001c }
        r13 = 0;	 Catch:{ all -> 0x001c }
        r14 = 0;	 Catch:{ all -> 0x001c }
        r10.<init>(r11, r12, r13, r14);	 Catch:{ all -> 0x001c }
    L_0x0010:
        monitor-exit(r15);
        return r10;
    L_0x0012:
        if (r16 != 0) goto L_0x001f;
    L_0x0014:
        r10 = new java.lang.NullPointerException;	 Catch:{ all -> 0x001c }
        r11 = "srcs";	 Catch:{ all -> 0x001c }
        r10.<init>(r11);	 Catch:{ all -> 0x001c }
        throw r10;	 Catch:{ all -> 0x001c }
    L_0x001c:
        r10 = move-exception;
        monitor-exit(r15);
        throw r10;
    L_0x001f:
        if (r19 != 0) goto L_0x0029;
    L_0x0021:
        r10 = new java.lang.NullPointerException;	 Catch:{ all -> 0x001c }
        r11 = "dst";	 Catch:{ all -> 0x001c }
        r10.<init>(r11);	 Catch:{ all -> 0x001c }
        throw r10;	 Catch:{ all -> 0x001c }
    L_0x0029:
        r0 = r16;	 Catch:{ all -> 0x001c }
        r10 = r0.length;	 Catch:{ all -> 0x001c }
        r0 = r17;	 Catch:{ all -> 0x001c }
        if (r0 >= r10) goto L_0x0037;	 Catch:{ all -> 0x001c }
    L_0x0030:
        r10 = r17 + r18;	 Catch:{ all -> 0x001c }
        r0 = r16;	 Catch:{ all -> 0x001c }
        r11 = r0.length;	 Catch:{ all -> 0x001c }
        if (r10 <= r11) goto L_0x0071;	 Catch:{ all -> 0x001c }
    L_0x0037:
        r10 = new java.lang.IndexOutOfBoundsException;	 Catch:{ all -> 0x001c }
        r11 = new java.lang.StringBuilder;	 Catch:{ all -> 0x001c }
        r11.<init>();	 Catch:{ all -> 0x001c }
        r12 = "offset: ";	 Catch:{ all -> 0x001c }
        r11 = r11.append(r12);	 Catch:{ all -> 0x001c }
        r0 = r17;	 Catch:{ all -> 0x001c }
        r11 = r11.append(r0);	 Catch:{ all -> 0x001c }
        r12 = ", length: ";	 Catch:{ all -> 0x001c }
        r11 = r11.append(r12);	 Catch:{ all -> 0x001c }
        r0 = r18;	 Catch:{ all -> 0x001c }
        r11 = r11.append(r0);	 Catch:{ all -> 0x001c }
        r12 = " (expected: offset <= offset + length <= srcs.length (";	 Catch:{ all -> 0x001c }
        r11 = r11.append(r12);	 Catch:{ all -> 0x001c }
        r0 = r16;	 Catch:{ all -> 0x001c }
        r12 = r0.length;	 Catch:{ all -> 0x001c }
        r11 = r11.append(r12);	 Catch:{ all -> 0x001c }
        r12 = "))";	 Catch:{ all -> 0x001c }
        r11 = r11.append(r12);	 Catch:{ all -> 0x001c }
        r11 = r11.toString();	 Catch:{ all -> 0x001c }
        r10.<init>(r11);	 Catch:{ all -> 0x001c }
        throw r10;	 Catch:{ all -> 0x001c }
    L_0x0071:
        r10 = r19.isReadOnly();	 Catch:{ all -> 0x001c }
        if (r10 == 0) goto L_0x007d;	 Catch:{ all -> 0x001c }
    L_0x0077:
        r10 = new java.nio.ReadOnlyBufferException;	 Catch:{ all -> 0x001c }
        r10.<init>();	 Catch:{ all -> 0x001c }
        throw r10;	 Catch:{ all -> 0x001c }
    L_0x007d:
        r10 = r15.accepted;	 Catch:{ all -> 0x001c }
        if (r10 != 0) goto L_0x0084;	 Catch:{ all -> 0x001c }
    L_0x0081:
        r15.beginHandshakeImplicitly();	 Catch:{ all -> 0x001c }
    L_0x0084:
        r6 = r15.getHandshakeStatus();	 Catch:{ all -> 0x001c }
        r10 = r15.handshakeFinished;	 Catch:{ all -> 0x001c }
        if (r10 == 0) goto L_0x0090;	 Catch:{ all -> 0x001c }
    L_0x008c:
        r10 = r15.engineClosed;	 Catch:{ all -> 0x001c }
        if (r10 == 0) goto L_0x00a3;	 Catch:{ all -> 0x001c }
    L_0x0090:
        r10 = javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_UNWRAP;	 Catch:{ all -> 0x001c }
        if (r6 != r10) goto L_0x00a3;	 Catch:{ all -> 0x001c }
    L_0x0094:
        r10 = new javax.net.ssl.SSLEngineResult;	 Catch:{ all -> 0x001c }
        r11 = r15.getEngineStatus();	 Catch:{ all -> 0x001c }
        r12 = javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_UNWRAP;	 Catch:{ all -> 0x001c }
        r13 = 0;	 Catch:{ all -> 0x001c }
        r14 = 0;	 Catch:{ all -> 0x001c }
        r10.<init>(r11, r12, r13, r14);	 Catch:{ all -> 0x001c }
        goto L_0x0010;	 Catch:{ all -> 0x001c }
    L_0x00a3:
        r3 = 0;	 Catch:{ all -> 0x001c }
        r10 = r15.networkBIO;	 Catch:{ all -> 0x001c }
        r8 = org.apache.tomcat.jni.SSL.pendingWrittenBytesInBIO(r10);	 Catch:{ all -> 0x001c }
        if (r8 <= 0) goto L_0x00e1;	 Catch:{ all -> 0x001c }
    L_0x00ac:
        r4 = r19.remaining();	 Catch:{ all -> 0x001c }
        if (r4 >= r8) goto L_0x00bc;	 Catch:{ all -> 0x001c }
    L_0x00b2:
        r10 = new javax.net.ssl.SSLEngineResult;	 Catch:{ all -> 0x001c }
        r11 = javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW;	 Catch:{ all -> 0x001c }
        r12 = 0;	 Catch:{ all -> 0x001c }
        r10.<init>(r11, r6, r12, r3);	 Catch:{ all -> 0x001c }
        goto L_0x0010;
    L_0x00bc:
        r0 = r19;	 Catch:{ Exception -> 0x00da }
        r10 = r15.readEncryptedData(r0, r8);	 Catch:{ Exception -> 0x00da }
        r3 = r3 + r10;
        r10 = r15.isOutboundDone;	 Catch:{ all -> 0x001c }
        if (r10 == 0) goto L_0x00ca;	 Catch:{ all -> 0x001c }
    L_0x00c7:
        r15.shutdown();	 Catch:{ all -> 0x001c }
    L_0x00ca:
        r10 = new javax.net.ssl.SSLEngineResult;	 Catch:{ all -> 0x001c }
        r11 = r15.getEngineStatus();	 Catch:{ all -> 0x001c }
        r12 = r15.getHandshakeStatus();	 Catch:{ all -> 0x001c }
        r13 = 0;	 Catch:{ all -> 0x001c }
        r10.<init>(r11, r12, r13, r3);	 Catch:{ all -> 0x001c }
        goto L_0x0010;	 Catch:{ all -> 0x001c }
    L_0x00da:
        r5 = move-exception;	 Catch:{ all -> 0x001c }
        r10 = new javax.net.ssl.SSLException;	 Catch:{ all -> 0x001c }
        r10.<init>(r5);	 Catch:{ all -> 0x001c }
        throw r10;	 Catch:{ all -> 0x001c }
    L_0x00e1:
        r2 = 0;	 Catch:{ all -> 0x001c }
        r7 = r17;	 Catch:{ all -> 0x001c }
    L_0x00e4:
        r0 = r18;	 Catch:{ all -> 0x001c }
        if (r7 >= r0) goto L_0x0137;	 Catch:{ all -> 0x001c }
    L_0x00e8:
        r9 = r16[r7];	 Catch:{ all -> 0x001c }
    L_0x00ea:
        r10 = r9.hasRemaining();	 Catch:{ all -> 0x001c }
        if (r10 == 0) goto L_0x0134;
    L_0x00f0:
        r10 = r15.writePlaintextData(r9);	 Catch:{ Exception -> 0x0110 }
        r2 = r2 + r10;
        r10 = r15.networkBIO;	 Catch:{ all -> 0x001c }
        r8 = org.apache.tomcat.jni.SSL.pendingWrittenBytesInBIO(r10);	 Catch:{ all -> 0x001c }
        if (r8 <= 0) goto L_0x00ea;	 Catch:{ all -> 0x001c }
    L_0x00fd:
        r4 = r19.remaining();	 Catch:{ all -> 0x001c }
        if (r4 >= r8) goto L_0x0117;	 Catch:{ all -> 0x001c }
    L_0x0103:
        r10 = new javax.net.ssl.SSLEngineResult;	 Catch:{ all -> 0x001c }
        r11 = javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW;	 Catch:{ all -> 0x001c }
        r12 = r15.getHandshakeStatus();	 Catch:{ all -> 0x001c }
        r10.<init>(r11, r12, r2, r3);	 Catch:{ all -> 0x001c }
        goto L_0x0010;	 Catch:{ all -> 0x001c }
    L_0x0110:
        r5 = move-exception;	 Catch:{ all -> 0x001c }
        r10 = new javax.net.ssl.SSLException;	 Catch:{ all -> 0x001c }
        r10.<init>(r5);	 Catch:{ all -> 0x001c }
        throw r10;	 Catch:{ all -> 0x001c }
    L_0x0117:
        r0 = r19;	 Catch:{ Exception -> 0x012d }
        r10 = r15.readEncryptedData(r0, r8);	 Catch:{ Exception -> 0x012d }
        r3 = r3 + r10;
        r10 = new javax.net.ssl.SSLEngineResult;	 Catch:{ all -> 0x001c }
        r11 = r15.getEngineStatus();	 Catch:{ all -> 0x001c }
        r12 = r15.getHandshakeStatus();	 Catch:{ all -> 0x001c }
        r10.<init>(r11, r12, r2, r3);	 Catch:{ all -> 0x001c }
        goto L_0x0010;	 Catch:{ all -> 0x001c }
    L_0x012d:
        r5 = move-exception;	 Catch:{ all -> 0x001c }
        r10 = new javax.net.ssl.SSLException;	 Catch:{ all -> 0x001c }
        r10.<init>(r5);	 Catch:{ all -> 0x001c }
        throw r10;	 Catch:{ all -> 0x001c }
    L_0x0134:
        r7 = r7 + 1;	 Catch:{ all -> 0x001c }
        goto L_0x00e4;	 Catch:{ all -> 0x001c }
    L_0x0137:
        r10 = new javax.net.ssl.SSLEngineResult;	 Catch:{ all -> 0x001c }
        r11 = r15.getEngineStatus();	 Catch:{ all -> 0x001c }
        r12 = r15.getHandshakeStatus();	 Catch:{ all -> 0x001c }
        r10.<init>(r11, r12, r2, r3);	 Catch:{ all -> 0x001c }
        goto L_0x0010;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.ssl.OpenSslEngine.wrap(java.nio.ByteBuffer[], int, int, java.nio.ByteBuffer):javax.net.ssl.SSLEngineResult");
    }

    public synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
        SSLEngineResult sSLEngineResult;
        if (this.destroyed != 0) {
            sSLEngineResult = new SSLEngineResult(Status.CLOSED, HandshakeStatus.NOT_HANDSHAKING, 0, 0);
        } else if (src == null) {
            throw new NullPointerException("src");
        } else if (dsts == null) {
            throw new NullPointerException("dsts");
        } else if (offset >= dsts.length || offset + length > dsts.length) {
            throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= dsts.length (" + dsts.length + "))");
        } else {
            ByteBuffer dst;
            int capacity = 0;
            int endOffset = offset + length;
            int i = offset;
            while (i < endOffset) {
                dst = dsts[i];
                if (dst == null) {
                    throw new IllegalArgumentException();
                } else if (dst.isReadOnly()) {
                    throw new ReadOnlyBufferException();
                } else {
                    capacity += dst.remaining();
                    i++;
                }
            }
            if (this.accepted == 0) {
                beginHandshakeImplicitly();
            }
            HandshakeStatus handshakeStatus = getHandshakeStatus();
            if ((!this.handshakeFinished || this.engineClosed) && handshakeStatus == HandshakeStatus.NEED_WRAP) {
                sSLEngineResult = new SSLEngineResult(getEngineStatus(), HandshakeStatus.NEED_WRAP, 0, 0);
            } else if (src.remaining() > MAX_ENCRYPTED_PACKET_LENGTH) {
                this.isInboundDone = true;
                this.isOutboundDone = true;
                this.engineClosed = true;
                shutdown();
                throw ENCRYPTED_PACKET_OVERSIZED;
            } else {
                this.lastPrimingReadResult = 0;
                try {
                    int bytesConsumed = 0 + writeEncryptedData(src);
                    String error = SSL.getLastError();
                    if (error == null || error.startsWith("error:00000000:")) {
                        int pendingApp = SSL.isInInit(this.ssl) == 0 ? SSL.pendingReadableBytesInSSL(this.ssl) : 0;
                        if (capacity < pendingApp) {
                            sSLEngineResult = new SSLEngineResult(Status.BUFFER_OVERFLOW, getHandshakeStatus(), bytesConsumed, 0);
                        } else {
                            int bytesProduced = 0;
                            int idx = offset;
                            while (idx < endOffset) {
                                dst = dsts[idx];
                                if (dst.hasRemaining()) {
                                    if (pendingApp > 0) {
                                        int bytesRead = readPlaintextData(dst);
                                        if (bytesRead == 0) {
                                            break;
                                        }
                                        bytesProduced += bytesRead;
                                        pendingApp -= bytesRead;
                                        if (!dst.hasRemaining()) {
                                            idx++;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                                idx++;
                            }
                            if (!this.receivedShutdown && (SSL.getShutdown(this.ssl) & 2) == 2) {
                                this.receivedShutdown = true;
                                closeOutbound();
                                closeInbound();
                            }
                            sSLEngineResult = new SSLEngineResult(getEngineStatus(), getHandshakeStatus(), bytesConsumed, bytesProduced);
                        }
                    } else {
                        if (logger.isInfoEnabled()) {
                            logger.info("SSL_read failed: primingReadResult: " + this.lastPrimingReadResult + "; OpenSSL error: '" + error + '\'');
                        }
                        shutdown();
                        throw new SSLException(error);
                    }
                } catch (Exception e) {
                    throw new SSLException(e);
                } catch (Exception e2) {
                    throw new SSLException(e2);
                }
            }
        }
        return sSLEngineResult;
    }

    public Runnable getDelegatedTask() {
        return null;
    }

    public synchronized void closeInbound() throws SSLException {
        if (!this.isInboundDone) {
            this.isInboundDone = true;
            this.engineClosed = true;
            if (this.accepted == 0) {
                shutdown();
            } else if (!this.receivedShutdown) {
                shutdown();
                throw new SSLException("close_notify has not been received");
            }
        }
    }

    public synchronized boolean isInboundDone() {
        boolean z;
        z = this.isInboundDone || this.engineClosed;
        return z;
    }

    public synchronized void closeOutbound() {
        if (!this.isOutboundDone) {
            this.isOutboundDone = true;
            this.engineClosed = true;
            if (this.accepted == 0 || this.destroyed != 0) {
                shutdown();
            } else if ((SSL.getShutdown(this.ssl) & 1) != 1) {
                SSL.shutdownSSL(this.ssl);
            }
        }
    }

    public synchronized boolean isOutboundDone() {
        return this.isOutboundDone;
    }

    public String[] getSupportedCipherSuites() {
        return EmptyArrays.EMPTY_STRINGS;
    }

    public String[] getEnabledCipherSuites() {
        return EmptyArrays.EMPTY_STRINGS;
    }

    public void setEnabledCipherSuites(String[] strings) {
        throw new UnsupportedOperationException();
    }

    public String[] getSupportedProtocols() {
        return EmptyArrays.EMPTY_STRINGS;
    }

    public String[] getEnabledProtocols() {
        return EmptyArrays.EMPTY_STRINGS;
    }

    public void setEnabledProtocols(String[] strings) {
        throw new UnsupportedOperationException();
    }

    public SSLSession getSession() {
        SSLSession session = this.session;
        if (session != null) {
            return session;
        }
        session = new C08611();
        this.session = session;
        return session;
    }

    public synchronized void beginHandshake() throws SSLException {
        if (this.engineClosed) {
            throw ENGINE_CLOSED;
        }
        switch (this.accepted) {
            case 0:
                SSL.doHandshake(this.ssl);
                this.accepted = 2;
                break;
            case 1:
                this.accepted = 2;
                break;
            case 2:
                throw RENEGOTIATION_UNSUPPORTED;
            default:
                throw new Error();
        }
    }

    private synchronized void beginHandshakeImplicitly() throws SSLException {
        if (this.engineClosed) {
            throw ENGINE_CLOSED;
        } else if (this.accepted == 0) {
            SSL.doHandshake(this.ssl);
            this.accepted = 1;
        }
    }

    private Status getEngineStatus() {
        return this.engineClosed ? Status.CLOSED : Status.OK;
    }

    public synchronized HandshakeStatus getHandshakeStatus() {
        HandshakeStatus handshakeStatus;
        if (this.accepted == 0 || this.destroyed != 0) {
            handshakeStatus = HandshakeStatus.NOT_HANDSHAKING;
        } else if (this.handshakeFinished) {
            if (!this.engineClosed) {
                handshakeStatus = HandshakeStatus.NOT_HANDSHAKING;
            } else if (SSL.pendingWrittenBytesInBIO(this.networkBIO) != 0) {
                handshakeStatus = HandshakeStatus.NEED_WRAP;
            } else {
                handshakeStatus = HandshakeStatus.NEED_UNWRAP;
            }
        } else if (SSL.pendingWrittenBytesInBIO(this.networkBIO) != 0) {
            handshakeStatus = HandshakeStatus.NEED_WRAP;
        } else if (SSL.isInInit(this.ssl) == 0) {
            this.handshakeFinished = true;
            this.cipher = SSL.getCipherForSSL(this.ssl);
            String applicationProtocol = SSL.getNextProtoNegotiated(this.ssl);
            if (applicationProtocol == null) {
                applicationProtocol = this.fallbackApplicationProtocol;
            }
            if (applicationProtocol != null) {
                this.applicationProtocol = applicationProtocol.replace(':', '_');
            } else {
                this.applicationProtocol = null;
            }
            handshakeStatus = HandshakeStatus.FINISHED;
        } else {
            handshakeStatus = HandshakeStatus.NEED_UNWRAP;
        }
        return handshakeStatus;
    }

    public void setUseClientMode(boolean clientMode) {
        if (clientMode) {
            throw new UnsupportedOperationException();
        }
    }

    public boolean getUseClientMode() {
        return false;
    }

    public void setNeedClientAuth(boolean b) {
        if (b) {
            throw new UnsupportedOperationException();
        }
    }

    public boolean getNeedClientAuth() {
        return false;
    }

    public void setWantClientAuth(boolean b) {
        if (b) {
            throw new UnsupportedOperationException();
        }
    }

    public boolean getWantClientAuth() {
        return false;
    }

    public void setEnableSessionCreation(boolean b) {
        if (b) {
            throw new UnsupportedOperationException();
        }
    }

    public boolean getEnableSessionCreation() {
        return false;
    }
}
