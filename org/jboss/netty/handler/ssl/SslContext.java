package org.jboss.netty.handler.ssl;

import java.io.File;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public abstract class SslContext {
    private final SslBufferPool bufferPool;

    public abstract List<String> cipherSuites();

    public abstract boolean isClient();

    public abstract SSLEngine newEngine();

    public abstract SSLEngine newEngine(String str, int i);

    public abstract List<String> nextProtocols();

    public abstract long sessionCacheSize();

    public abstract long sessionTimeout();

    public static SslProvider defaultServerProvider() {
        if (OpenSsl.isAvailable()) {
            return SslProvider.OPENSSL;
        }
        return SslProvider.JDK;
    }

    public static SslProvider defaultClientProvider() {
        return SslProvider.JDK;
    }

    public static SslContext newServerContext(File certChainFile, File keyFile) throws SSLException {
        return newServerContext(null, null, certChainFile, keyFile, null, null, null, 0, 0);
    }

    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        return newServerContext(null, null, certChainFile, keyFile, keyPassword, null, null, 0, 0);
    }

    public static SslContext newServerContext(SslBufferPool bufPool, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return newServerContext(null, bufPool, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }

    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile) throws SSLException {
        return newServerContext(provider, null, certChainFile, keyFile, null, null, null, 0, 0);
    }

    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword) throws SSLException {
        return newServerContext(provider, null, certChainFile, keyFile, keyPassword, null, null, 0, 0);
    }

    public static SslContext newServerContext(SslProvider provider, SslBufferPool bufPool, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        if (provider == null) {
            provider = OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
        }
        switch (provider) {
            case JDK:
                return new JdkSslServerContext(bufPool, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
            case OPENSSL:
                return new OpenSslServerContext(bufPool, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
            default:
                throw new Error(provider.toString());
        }
    }

    public static SslContext newClientContext() throws SSLException {
        return newClientContext(null, null, null, null, null, null, 0, 0);
    }

    public static SslContext newClientContext(File certChainFile) throws SSLException {
        return newClientContext(null, null, certChainFile, null, null, null, 0, 0);
    }

    public static SslContext newClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        return newClientContext(null, null, null, trustManagerFactory, null, null, 0, 0);
    }

    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        return newClientContext(null, null, certChainFile, trustManagerFactory, null, null, 0, 0);
    }

    public static SslContext newClientContext(SslBufferPool bufPool, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return newClientContext(null, bufPool, certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }

    public static SslContext newClientContext(SslProvider provider) throws SSLException {
        return newClientContext(provider, null, null, null, null, null, 0, 0);
    }

    public static SslContext newClientContext(SslProvider provider, File certChainFile) throws SSLException {
        return newClientContext(provider, null, certChainFile, null, null, null, 0, 0);
    }

    public static SslContext newClientContext(SslProvider provider, TrustManagerFactory trustManagerFactory) throws SSLException {
        return newClientContext(provider, null, null, trustManagerFactory, null, null, 0, 0);
    }

    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        return newClientContext(provider, null, certChainFile, trustManagerFactory, null, null, 0, 0);
    }

    public static SslContext newClientContext(SslProvider provider, SslBufferPool bufPool, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        if (provider == null || provider == SslProvider.JDK) {
            return new JdkSslClientContext(bufPool, certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
        }
        throw new SSLException("client context unsupported for: " + provider);
    }

    SslContext(SslBufferPool bufferPool) {
        if (bufferPool == null) {
            bufferPool = newBufferPool();
        }
        this.bufferPool = bufferPool;
    }

    SslBufferPool newBufferPool() {
        return new SslBufferPool(false, false);
    }

    public final boolean isServer() {
        return !isClient();
    }

    public final SslBufferPool bufferPool() {
        return this.bufferPool;
    }

    public final SslHandler newHandler() {
        return newHandler(newEngine());
    }

    public final SslHandler newHandler(String peerHost, int peerPort) {
        return newHandler(newEngine(peerHost, peerPort));
    }

    private SslHandler newHandler(SSLEngine engine) {
        SslHandler handler = new SslHandler(engine, bufferPool());
        if (isClient()) {
            handler.setIssueHandshake(true);
        }
        handler.setCloseOnSSLException(true);
        return handler;
    }
}
