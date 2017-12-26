package org.jboss.netty.handler.ssl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public abstract class JdkSslContext extends SslContext {
    static final List<String> DEFAULT_CIPHERS;
    static final String PROTOCOL = "TLS";
    static final String[] PROTOCOLS;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
    private final String[] cipherSuites;
    private final List<String> unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));

    public abstract SSLContext context();

    static {
        try {
            SSLContext context = SSLContext.getInstance(PROTOCOL);
            context.init(null, null, null);
            SSLEngine engine = context.createSSLEngine();
            String[] supportedProtocols = engine.getSupportedProtocols();
            List<String> protocols = new ArrayList();
            addIfSupported(supportedProtocols, protocols, "TLSv1.2", "TLSv1.1", "TLSv1");
            if (protocols.isEmpty()) {
                PROTOCOLS = engine.getEnabledProtocols();
            } else {
                PROTOCOLS = (String[]) protocols.toArray(new String[protocols.size()]);
            }
            String[] supportedCiphers = engine.getSupportedCipherSuites();
            List<String> ciphers = new ArrayList();
            addIfSupported(supportedCiphers, ciphers, "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA", "SSL_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_RSA_WITH_RC4_128_SHA");
            if (ciphers.isEmpty()) {
                DEFAULT_CIPHERS = Collections.unmodifiableList(Arrays.asList(engine.getEnabledCipherSuites()));
            } else {
                DEFAULT_CIPHERS = Collections.unmodifiableList(ciphers);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Default protocols (JDK): " + Arrays.asList(PROTOCOLS));
                logger.debug("Default cipher suites (JDK): " + DEFAULT_CIPHERS);
            }
        } catch (Exception e) {
            throw new Error("failed to initialize the default SSL context", e);
        }
    }

    private static void addIfSupported(String[] supported, List<String> enabled, String... names) {
        for (String n : names) {
            for (String s : supported) {
                if (n.equals(s)) {
                    enabled.add(s);
                    break;
                }
            }
        }
    }

    JdkSslContext(SslBufferPool bufferPool, Iterable<String> ciphers) {
        super(bufferPool);
        this.cipherSuites = toCipherSuiteArray(ciphers);
    }

    public final SSLSessionContext sessionContext() {
        if (isServer()) {
            return context().getServerSessionContext();
        }
        return context().getClientSessionContext();
    }

    public final List<String> cipherSuites() {
        return this.unmodifiableCipherSuites;
    }

    public final long sessionCacheSize() {
        return (long) sessionContext().getSessionCacheSize();
    }

    public final long sessionTimeout() {
        return (long) sessionContext().getSessionTimeout();
    }

    public final SSLEngine newEngine() {
        SSLEngine engine = context().createSSLEngine();
        engine.setEnabledCipherSuites(this.cipherSuites);
        engine.setEnabledProtocols(PROTOCOLS);
        engine.setUseClientMode(isClient());
        return wrapEngine(engine);
    }

    public final SSLEngine newEngine(String peerHost, int peerPort) {
        SSLEngine engine = context().createSSLEngine(peerHost, peerPort);
        engine.setEnabledCipherSuites(this.cipherSuites);
        engine.setEnabledProtocols(PROTOCOLS);
        engine.setUseClientMode(isClient());
        return wrapEngine(engine);
    }

    private SSLEngine wrapEngine(SSLEngine engine) {
        return nextProtocols().isEmpty() ? engine : new JettyNpnSslEngine(engine, nextProtocols(), isServer());
    }

    private static String[] toCipherSuiteArray(Iterable<String> ciphers) {
        if (ciphers == null) {
            return (String[]) DEFAULT_CIPHERS.toArray(new String[DEFAULT_CIPHERS.size()]);
        }
        List<String> newCiphers = new ArrayList();
        for (String c : ciphers) {
            if (c == null) {
                break;
            }
            newCiphers.add(c);
        }
        return (String[]) newCiphers.toArray(new String[newCiphers.size()]);
    }
}
