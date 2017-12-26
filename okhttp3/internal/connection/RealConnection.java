package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Connection.Builder;
import okhttp3.internal.http2.Http2Connection.Listener;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket.Streams;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;

public final class RealConnection extends Listener implements Connection {
    private static final int MAX_TUNNEL_ATTEMPTS = 21;
    private static final String NPE_THROW_WITH_NULL = "throw with null exception";
    public int allocationLimit = 1;
    public final List<Reference<StreamAllocation>> allocations = new ArrayList();
    private final ConnectionPool connectionPool;
    private Handshake handshake;
    private Http2Connection http2Connection;
    public long idleAtNanos = Long.MAX_VALUE;
    public boolean noNewStreams;
    private Protocol protocol;
    private Socket rawSocket;
    private final Route route;
    private BufferedSink sink;
    private Socket socket;
    private BufferedSource source;
    public int successCount;

    public RealConnection(ConnectionPool connectionPool, Route route) {
        this.connectionPool = connectionPool;
        this.route = route;
    }

    public static RealConnection testConnection(ConnectionPool connectionPool, Route route, Socket socket, long idleAtNanos) {
        RealConnection result = new RealConnection(connectionPool, route);
        result.socket = socket;
        result.idleAtNanos = idleAtNanos;
        return result;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(int r14, int r15, int r16, boolean r17, okhttp3.Call r18, okhttp3.EventListener r19) {
        /*
        r13 = this;
        r2 = r13.protocol;
        if (r2 == 0) goto L_0x000c;
    L_0x0004:
        r2 = new java.lang.IllegalStateException;
        r3 = "already connected";
        r2.<init>(r3);
        throw r2;
    L_0x000c:
        r12 = 0;
        r2 = r13.route;
        r2 = r2.address();
        r9 = r2.connectionSpecs();
        r8 = new okhttp3.internal.connection.ConnectionSpecSelector;
        r8.<init>(r9);
        r2 = r13.route;
        r2 = r2.address();
        r2 = r2.sslSocketFactory();
        if (r2 != 0) goto L_0x0079;
    L_0x0028:
        r2 = okhttp3.ConnectionSpec.CLEARTEXT;
        r2 = r9.contains(r2);
        if (r2 != 0) goto L_0x003d;
    L_0x0030:
        r2 = new okhttp3.internal.connection.RouteException;
        r3 = new java.net.UnknownServiceException;
        r4 = "CLEARTEXT communication not enabled for client";
        r3.<init>(r4);
        r2.<init>(r3);
        throw r2;
    L_0x003d:
        r2 = r13.route;
        r2 = r2.address();
        r2 = r2.url();
        r11 = r2.host();
        r2 = okhttp3.internal.platform.Platform.get();
        r2 = r2.isCleartextTrafficPermitted(r11);
        if (r2 != 0) goto L_0x0079;
    L_0x0055:
        r2 = new okhttp3.internal.connection.RouteException;
        r3 = new java.net.UnknownServiceException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "CLEARTEXT communication to ";
        r4 = r4.append(r5);
        r4 = r4.append(r11);
        r5 = " not permitted by network security policy";
        r4 = r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        r2.<init>(r3);
        throw r2;
    L_0x0079:
        r2 = r13.route;	 Catch:{ IOException -> 0x00ce }
        r2 = r2.requiresTunnel();	 Catch:{ IOException -> 0x00ce }
        if (r2 == 0) goto L_0x00aa;
    L_0x0081:
        r2 = r13;
        r3 = r14;
        r4 = r15;
        r5 = r16;
        r6 = r18;
        r7 = r19;
        r2.connectTunnel(r3, r4, r5, r6, r7);	 Catch:{ IOException -> 0x00ce }
        r2 = r13.rawSocket;	 Catch:{ IOException -> 0x00ce }
        if (r2 != 0) goto L_0x00b1;
    L_0x0091:
        r2 = r13.route;
        r2 = r2.requiresTunnel();
        if (r2 == 0) goto L_0x0116;
    L_0x0099:
        r2 = r13.rawSocket;
        if (r2 != 0) goto L_0x0116;
    L_0x009d:
        r10 = new java.net.ProtocolException;
        r2 = "Too many tunnel connections attempted: 21";
        r10.<init>(r2);
        r2 = new okhttp3.internal.connection.RouteException;
        r2.<init>(r10);
        throw r2;
    L_0x00aa:
        r0 = r18;
        r1 = r19;
        r13.connectSocket(r14, r15, r0, r1);	 Catch:{ IOException -> 0x00ce }
    L_0x00b1:
        r0 = r18;
        r1 = r19;
        r13.establishProtocol(r8, r0, r1);	 Catch:{ IOException -> 0x00ce }
        r2 = r13.route;	 Catch:{ IOException -> 0x00ce }
        r2 = r2.socketAddress();	 Catch:{ IOException -> 0x00ce }
        r3 = r13.route;	 Catch:{ IOException -> 0x00ce }
        r3 = r3.proxy();	 Catch:{ IOException -> 0x00ce }
        r4 = r13.protocol;	 Catch:{ IOException -> 0x00ce }
        r0 = r19;
        r1 = r18;
        r0.connectEnd(r1, r2, r3, r4);	 Catch:{ IOException -> 0x00ce }
        goto L_0x0091;
    L_0x00ce:
        r7 = move-exception;
        r2 = r13.socket;
        okhttp3.internal.Util.closeQuietly(r2);
        r2 = r13.rawSocket;
        okhttp3.internal.Util.closeQuietly(r2);
        r2 = 0;
        r13.socket = r2;
        r2 = 0;
        r13.rawSocket = r2;
        r2 = 0;
        r13.source = r2;
        r2 = 0;
        r13.sink = r2;
        r2 = 0;
        r13.handshake = r2;
        r2 = 0;
        r13.protocol = r2;
        r2 = 0;
        r13.http2Connection = r2;
        r2 = r13.route;
        r4 = r2.socketAddress();
        r2 = r13.route;
        r5 = r2.proxy();
        r6 = 0;
        r2 = r19;
        r3 = r18;
        r2.connectFailed(r3, r4, r5, r6, r7);
        if (r12 != 0) goto L_0x0112;
    L_0x0104:
        r12 = new okhttp3.internal.connection.RouteException;
        r12.<init>(r7);
    L_0x0109:
        if (r17 == 0) goto L_0x0111;
    L_0x010b:
        r2 = r8.connectionFailed(r7);
        if (r2 != 0) goto L_0x0079;
    L_0x0111:
        throw r12;
    L_0x0112:
        r12.addConnectException(r7);
        goto L_0x0109;
    L_0x0116:
        r2 = r13.http2Connection;
        if (r2 == 0) goto L_0x0126;
    L_0x011a:
        r3 = r13.connectionPool;
        monitor-enter(r3);
        r2 = r13.http2Connection;	 Catch:{ all -> 0x0127 }
        r2 = r2.maxConcurrentStreams();	 Catch:{ all -> 0x0127 }
        r13.allocationLimit = r2;	 Catch:{ all -> 0x0127 }
        monitor-exit(r3);	 Catch:{ all -> 0x0127 }
    L_0x0126:
        return;
    L_0x0127:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0127 }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connect(int, int, int, boolean, okhttp3.Call, okhttp3.EventListener):void");
    }

    private void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout, Call call, EventListener eventListener) throws IOException {
        Request tunnelRequest = createTunnelRequest();
        HttpUrl url = tunnelRequest.url();
        int i = 0;
        while (i < 21) {
            connectSocket(connectTimeout, readTimeout, call, eventListener);
            tunnelRequest = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
            if (tunnelRequest != null) {
                Util.closeQuietly(this.rawSocket);
                this.rawSocket = null;
                this.sink = null;
                this.source = null;
                eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), null);
                i++;
            } else {
                return;
            }
        }
    }

    private void connectSocket(int connectTimeout, int readTimeout, Call call, EventListener eventListener) throws IOException {
        Socket createSocket;
        Proxy proxy = this.route.proxy();
        Address address = this.route.address();
        if (proxy.type() == Type.DIRECT || proxy.type() == Type.HTTP) {
            createSocket = address.socketFactory().createSocket();
        } else {
            createSocket = new Socket(proxy);
        }
        this.rawSocket = createSocket;
        eventListener.connectStart(call, this.route.socketAddress(), proxy);
        this.rawSocket.setSoTimeout(readTimeout);
        try {
            Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), connectTimeout);
            try {
                this.source = Okio.buffer(Okio.source(this.rawSocket));
                this.sink = Okio.buffer(Okio.sink(this.rawSocket));
            } catch (NullPointerException npe) {
                if (NPE_THROW_WITH_NULL.equals(npe.getMessage())) {
                    throw new IOException(npe);
                }
            }
        } catch (ConnectException e) {
            ConnectException ce = new ConnectException("Failed to connect to " + this.route.socketAddress());
            ce.initCause(e);
            throw ce;
        }
    }

    private void establishProtocol(ConnectionSpecSelector connectionSpecSelector, Call call, EventListener eventListener) throws IOException {
        if (this.route.address().sslSocketFactory() == null) {
            this.protocol = Protocol.HTTP_1_1;
            this.socket = this.rawSocket;
            return;
        }
        eventListener.secureConnectStart(call);
        connectTls(connectionSpecSelector);
        eventListener.secureConnectEnd(call, this.handshake);
        if (this.protocol == Protocol.HTTP_2) {
            this.socket.setSoTimeout(0);
            this.http2Connection = new Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).build();
            this.http2Connection.start();
        }
    }

    private void connectTls(ConnectionSpecSelector connectionSpecSelector) throws IOException {
        Address address = this.route.address();
        Socket sslSocket = null;
        try {
            sslSocket = (SSLSocket) address.sslSocketFactory().createSocket(this.rawSocket, address.url().host(), address.url().port(), true);
            ConnectionSpec connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
            if (connectionSpec.supportsTlsExtensions()) {
                Platform.get().configureTlsExtensions(sslSocket, address.url().host(), address.protocols());
            }
            sslSocket.startHandshake();
            Handshake unverifiedHandshake = Handshake.get(sslSocket.getSession());
            if (address.hostnameVerifier().verify(address.url().host(), sslSocket.getSession())) {
                String maybeProtocol;
                Protocol protocol;
                address.certificatePinner().check(address.url().host(), unverifiedHandshake.peerCertificates());
                if (connectionSpec.supportsTlsExtensions()) {
                    maybeProtocol = Platform.get().getSelectedProtocol(sslSocket);
                } else {
                    maybeProtocol = null;
                }
                this.socket = sslSocket;
                this.source = Okio.buffer(Okio.source(this.socket));
                this.sink = Okio.buffer(Okio.sink(this.socket));
                this.handshake = unverifiedHandshake;
                if (maybeProtocol != null) {
                    protocol = Protocol.get(maybeProtocol);
                } else {
                    protocol = Protocol.HTTP_1_1;
                }
                this.protocol = protocol;
                if (sslSocket != null) {
                    Platform.get().afterHandshake(sslSocket);
                }
                if (!true) {
                    Util.closeQuietly(sslSocket);
                    return;
                }
                return;
            }
            X509Certificate cert = (X509Certificate) unverifiedHandshake.peerCertificates().get(0);
            throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified:\n    certificate: " + CertificatePinner.pin(cert) + "\n    DN: " + cert.getSubjectDN().getName() + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(cert));
        } catch (AssertionError e) {
            if (Util.isAndroidGetsocknameError(e)) {
                throw new IOException(e);
            }
            throw e;
        } catch (Throwable th) {
            if (sslSocket != null) {
                Platform.get().afterHandshake(sslSocket);
            }
            if (!false) {
                Util.closeQuietly(sslSocket);
            }
        }
    }

    private Request createTunnel(int readTimeout, int writeTimeout, Request tunnelRequest, HttpUrl url) throws IOException {
        String requestLine = "CONNECT " + Util.hostHeader(url, true) + " HTTP/1.1";
        Response response;
        do {
            Http1Codec tunnelConnection = new Http1Codec(null, null, this.source, this.sink);
            this.source.timeout().timeout((long) readTimeout, TimeUnit.MILLISECONDS);
            this.sink.timeout().timeout((long) writeTimeout, TimeUnit.MILLISECONDS);
            tunnelConnection.writeRequest(tunnelRequest.headers(), requestLine);
            tunnelConnection.finishRequest();
            response = tunnelConnection.readResponseHeaders(false).request(tunnelRequest).build();
            long contentLength = HttpHeaders.contentLength(response);
            if (contentLength == -1) {
                contentLength = 0;
            }
            Source body = tunnelConnection.newFixedLengthSource(contentLength);
            Util.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
            body.close();
            switch (response.code()) {
                case 200:
                    if (this.source.buffer().exhausted() && this.sink.buffer().exhausted()) {
                        return null;
                    }
                    throw new IOException("TLS tunnel buffered too many bytes!");
                case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED /*407*/:
                    tunnelRequest = this.route.address().proxyAuthenticator().authenticate(this.route, response);
                    if (tunnelRequest != null) {
                        break;
                    }
                    throw new IOException("Failed to authenticate with proxy");
                default:
                    throw new IOException("Unexpected response code for CONNECT: " + response.code());
            }
        } while (!"close".equalsIgnoreCase(response.header("Connection")));
        return tunnelRequest;
    }

    private Request createTunnelRequest() {
        return new Request.Builder().url(this.route.address().url()).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", HTTP.CONN_KEEP_ALIVE).header("User-Agent", Version.userAgent()).build();
    }

    public boolean isEligible(Address address, @Nullable Route route) {
        if (this.allocations.size() >= this.allocationLimit || this.noNewStreams || !Internal.instance.equalsNonHost(this.route.address(), address)) {
            return false;
        }
        if (address.url().host().equals(route().address().url().host())) {
            return true;
        }
        if (this.http2Connection == null || route == null || route.proxy().type() != Type.DIRECT || this.route.proxy().type() != Type.DIRECT || !this.route.socketAddress().equals(route.socketAddress()) || route.address().hostnameVerifier() != OkHostnameVerifier.INSTANCE || !supportsUrl(address.url())) {
            return false;
        }
        try {
            address.certificatePinner().check(address.url().host(), handshake().peerCertificates());
            return true;
        } catch (SSLPeerUnverifiedException e) {
            return false;
        }
    }

    public boolean supportsUrl(HttpUrl url) {
        if (url.port() != this.route.address().url().port()) {
            return false;
        }
        if (url.host().equals(this.route.address().url().host())) {
            return true;
        }
        boolean z = this.handshake != null && OkHostnameVerifier.INSTANCE.verify(url.host(), (X509Certificate) this.handshake.peerCertificates().get(0));
        return z;
    }

    public HttpCodec newCodec(OkHttpClient client, Chain chain, StreamAllocation streamAllocation) throws SocketException {
        if (this.http2Connection != null) {
            return new Http2Codec(client, chain, streamAllocation, this.http2Connection);
        }
        this.socket.setSoTimeout(chain.readTimeoutMillis());
        this.source.timeout().timeout((long) chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
        this.sink.timeout().timeout((long) chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        return new Http1Codec(client, streamAllocation, this.source, this.sink);
    }

    public Streams newWebSocketStreams(StreamAllocation streamAllocation) {
        final StreamAllocation streamAllocation2 = streamAllocation;
        return new Streams(true, this.source, this.sink) {
            public void close() throws IOException {
                streamAllocation2.streamFinished(true, streamAllocation2.codec(), -1, null);
            }
        };
    }

    public Route route() {
        return this.route;
    }

    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }

    public Socket socket() {
        return this.socket;
    }

    public boolean isHealthy(boolean doExtensiveChecks) {
        if (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
            return false;
        }
        if (this.http2Connection != null) {
            if (this.http2Connection.isShutdown()) {
                return false;
            }
            return true;
        } else if (!doExtensiveChecks) {
            return true;
        } else {
            int readTimeout;
            try {
                readTimeout = this.socket.getSoTimeout();
                this.socket.setSoTimeout(1);
                if (this.source.exhausted()) {
                    this.socket.setSoTimeout(readTimeout);
                    return false;
                }
                this.socket.setSoTimeout(readTimeout);
                return true;
            } catch (SocketTimeoutException e) {
                return true;
            } catch (IOException e2) {
                return false;
            } catch (Throwable th) {
                this.socket.setSoTimeout(readTimeout);
            }
        }
    }

    public void onStream(Http2Stream stream) throws IOException {
        stream.close(ErrorCode.REFUSED_STREAM);
    }

    public void onSettings(Http2Connection connection) {
        synchronized (this.connectionPool) {
            this.allocationLimit = connection.maxConcurrentStreams();
        }
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public String toString() {
        Object cipherSuite;
        StringBuilder append = new StringBuilder().append("Connection{").append(this.route.address().url().host()).append(":").append(this.route.address().url().port()).append(", proxy=").append(this.route.proxy()).append(" hostAddress=").append(this.route.socketAddress()).append(" cipherSuite=");
        if (this.handshake != null) {
            cipherSuite = this.handshake.cipherSuite();
        } else {
            cipherSuite = "none";
        }
        return append.append(cipherSuite).append(" protocol=").append(this.protocol).append('}').toString();
    }
}
