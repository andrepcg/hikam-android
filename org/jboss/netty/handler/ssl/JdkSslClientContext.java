package org.jboss.netty.handler.ssl;

import java.io.File;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;

public final class JdkSslClientContext extends JdkSslContext {
    private final SSLContext ctx;
    private final List<String> nextProtocols;

    public JdkSslClientContext() throws SSLException {
        this(null, null, null, null, null, 0, 0);
    }

    public JdkSslClientContext(File certChainFile) throws SSLException {
        this(certChainFile, null);
    }

    public JdkSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, trustManagerFactory);
    }

    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, certChainFile, trustManagerFactory, null, null, 0, 0);
    }

    public JdkSslClientContext(SslBufferPool bufPool, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(bufPool, ciphers);
        if (nextProtocols == null || !nextProtocols.iterator().hasNext()) {
            this.nextProtocols = Collections.emptyList();
        } else if (JettyNpnSslEngine.isAvailable()) {
            List<String> nextProtoList = new ArrayList();
            for (String p : nextProtocols) {
                if (p == null) {
                    break;
                }
                nextProtoList.add(p);
            }
            this.nextProtocols = Collections.unmodifiableList(nextProtoList);
        } else {
            throw new SSLException("NPN/ALPN unsupported: " + nextProtocols);
        }
        if (certChainFile == null) {
            try {
                this.ctx = SSLContext.getInstance("TLS");
                if (trustManagerFactory == null) {
                    this.ctx.init(null, null, null);
                } else {
                    trustManagerFactory.init((KeyStore) null);
                    this.ctx.init(null, trustManagerFactory.getTrustManagers(), null);
                }
            } catch (Exception e) {
                throw new SSLException("failed to initialize the server-side SSL context", e);
            }
        }
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        for (ChannelBuffer buf : PemReader.readCertificates(certChainFile)) {
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ChannelBufferInputStream(buf));
            ks.setCertificateEntry(cert.getSubjectX500Principal().getName("RFC2253"), cert);
        }
        if (trustManagerFactory == null) {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        }
        trustManagerFactory.init(ks);
        this.ctx = SSLContext.getInstance("TLS");
        this.ctx.init(null, trustManagerFactory.getTrustManagers(), null);
        SSLSessionContext sessCtx = this.ctx.getClientSessionContext();
        if (sessionCacheSize > 0) {
            sessCtx.setSessionCacheSize((int) Math.min(sessionCacheSize, 2147483647L));
        }
        if (sessionTimeout > 0) {
            sessCtx.setSessionTimeout((int) Math.min(sessionTimeout, 2147483647L));
        }
    }

    public boolean isClient() {
        return true;
    }

    public List<String> nextProtocols() {
        return this.nextProtocols;
    }

    public SSLContext context() {
        return this.ctx;
    }
}
