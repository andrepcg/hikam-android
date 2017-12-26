package org.jboss.netty.handler.ssl;

import android.support.v4.view.accessibility.AccessibilityEventCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public final class OpenSslServerContext extends SslContext {
    private static final List<String> DEFAULT_CIPHERS;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSslServerContext.class);
    private final long aprPool;
    private final List<String> ciphers;
    private final long ctx;
    private final List<String> nextProtocols;
    private final long sessionCacheSize;
    private final long sessionTimeout;
    private final OpenSslSessionStats stats;
    private final List<String> unmodifiableCiphers;

    static {
        List<String> ciphers = new ArrayList();
        Collections.addAll(ciphers, new String[]{"ECDHE-RSA-AES128-GCM-SHA256", "ECDHE-RSA-AES128-SHA", "ECDHE-RSA-AES256-SHA", "AES128-GCM-SHA256", "AES128-SHA", "AES256-SHA", "DES-CBC3-SHA", "RC4-SHA"});
        DEFAULT_CIPHERS = Collections.unmodifiableList(ciphers);
        if (logger.isDebugEnabled()) {
            logger.debug("Default cipher suite (OpenSSL): " + ciphers);
        }
    }

    public OpenSslServerContext(File certChainFile, File keyFile) throws SSLException {
        this(certChainFile, keyFile, null);
    }

    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        this(null, certChainFile, keyFile, keyPassword, null, null, 0, 0);
    }

    public OpenSslServerContext(SslBufferPool bufPool, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(bufPool);
        this.ciphers = new ArrayList();
        this.unmodifiableCiphers = Collections.unmodifiableList(this.ciphers);
        OpenSsl.ensureAvailability();
        if (certChainFile == null) {
            throw new NullPointerException("certChainFile");
        } else if (!certChainFile.isFile()) {
            throw new IllegalArgumentException("certChainFile is not a file: " + certChainFile);
        } else if (keyFile == null) {
            throw new NullPointerException("keyPath");
        } else if (keyFile.isFile()) {
            if (ciphers == null) {
                ciphers = DEFAULT_CIPHERS;
            }
            if (keyPassword == null) {
                keyPassword = "";
            }
            if (nextProtocols == null) {
                nextProtocols = Collections.emptyList();
            }
            for (String c : ciphers) {
                if (c == null) {
                    break;
                }
                this.ciphers.add(c);
            }
            List<String> nextProtoList = new ArrayList();
            for (String p : nextProtocols) {
                if (p == null) {
                    break;
                }
                nextProtoList.add(p);
            }
            this.nextProtocols = Collections.unmodifiableList(nextProtoList);
            this.aprPool = Pool.create(0);
            boolean success = false;
            try {
                synchronized (OpenSslServerContext.class) {
                    try {
                        this.ctx = SSLContext.make(this.aprPool, 6, 1);
                        SSLContext.setOptions(this.ctx, UnixStat.PERM_MASK);
                        SSLContext.setOptions(this.ctx, 16777216);
                        SSLContext.setOptions(this.ctx, 33554432);
                        SSLContext.setOptions(this.ctx, AccessibilityEventCompat.TYPE_WINDOWS_CHANGED);
                        SSLContext.setOptions(this.ctx, 524288);
                        SSLContext.setOptions(this.ctx, 1048576);
                        SSLContext.setOptions(this.ctx, 65536);
                        StringBuilder cipherBuf = new StringBuilder();
                        for (String c2 : this.ciphers) {
                            cipherBuf.append(c2);
                            cipherBuf.append(':');
                        }
                        cipherBuf.setLength(cipherBuf.length() - 1);
                        SSLContext.setCipherSuite(this.ctx, cipherBuf.toString());
                        SSLContext.setVerify(this.ctx, 0, 10);
                        if (!SSLContext.setCertificate(this.ctx, certChainFile.getPath(), keyFile.getPath(), keyPassword, 0)) {
                            throw new SSLException("failed to set certificate: " + certChainFile + " and " + keyFile + " (" + SSL.getLastError() + ')');
                        } else if (SSLContext.setCertificateChainFile(this.ctx, certChainFile.getPath(), true) || SSL.getLastError().startsWith("error:00000000:")) {
                            if (!nextProtoList.isEmpty()) {
                                StringBuilder nextProtocolBuf = new StringBuilder();
                                for (String p2 : nextProtoList) {
                                    nextProtocolBuf.append(p2);
                                    nextProtocolBuf.append(',');
                                }
                                nextProtocolBuf.setLength(nextProtocolBuf.length() - 1);
                                SSLContext.setNextProtos(this.ctx, nextProtocolBuf.toString());
                            }
                            if (sessionCacheSize > 0) {
                                this.sessionCacheSize = sessionCacheSize;
                                SSLContext.setSessionCacheSize(this.ctx, sessionCacheSize);
                            } else {
                                sessionCacheSize = SSLContext.setSessionCacheSize(this.ctx, 20480);
                                this.sessionCacheSize = sessionCacheSize;
                                SSLContext.setSessionCacheSize(this.ctx, sessionCacheSize);
                            }
                            if (sessionTimeout > 0) {
                                this.sessionTimeout = sessionTimeout;
                                SSLContext.setSessionCacheTimeout(this.ctx, sessionTimeout);
                            } else {
                                sessionTimeout = SSLContext.setSessionCacheTimeout(this.ctx, 300);
                                this.sessionTimeout = sessionTimeout;
                                SSLContext.setSessionCacheTimeout(this.ctx, sessionTimeout);
                            }
                        } else {
                            throw new SSLException("failed to set certificate chain: " + certChainFile + " (" + SSL.getLastError() + ')');
                        }
                    } catch (SSLException e) {
                        throw e;
                    } catch (Exception e2) {
                        throw new SSLException("failed to set certificate: " + certChainFile + " and " + keyFile, e2);
                    } catch (Exception e22) {
                        throw new SSLException("failed to create an SSL_CTX", e22);
                    } catch (SSLException e3) {
                        throw e3;
                    } catch (Exception e222) {
                        throw new SSLException("failed to set cipher suite: " + this.ciphers, e222);
                    }
                }
                success = true;
                this.stats = new OpenSslSessionStats(this.ctx);
            } finally {
                if (!success) {
                    destroyPools();
                }
            }
        } else {
            throw new IllegalArgumentException("keyPath is not a file: " + keyFile);
        }
    }

    SslBufferPool newBufferPool() {
        return new SslBufferPool(true, true);
    }

    public boolean isClient() {
        return false;
    }

    public List<String> cipherSuites() {
        return this.unmodifiableCiphers;
    }

    public long sessionCacheSize() {
        return this.sessionCacheSize;
    }

    public long sessionTimeout() {
        return this.sessionTimeout;
    }

    public List<String> nextProtocols() {
        return this.nextProtocols;
    }

    public long context() {
        return this.ctx;
    }

    public OpenSslSessionStats stats() {
        return this.stats;
    }

    public SSLEngine newEngine() {
        if (this.nextProtocols.isEmpty()) {
            return new OpenSslEngine(this.ctx, bufferPool(), null);
        }
        return new OpenSslEngine(this.ctx, bufferPool(), (String) this.nextProtocols.get(this.nextProtocols.size() - 1));
    }

    public SSLEngine newEngine(String peerHost, int peerPort) {
        throw new UnsupportedOperationException();
    }

    public void setTicketKeys(byte[] keys) {
        if (keys == null) {
            throw new NullPointerException("keys");
        }
        SSLContext.setSessionTicketKeys(this.ctx, keys);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        synchronized (OpenSslServerContext.class) {
            if (this.ctx != 0) {
                SSLContext.free(this.ctx);
            }
        }
        destroyPools();
    }

    private void destroyPools() {
        if (this.aprPool != 0) {
            Pool.destroy(this.aprPool);
        }
    }
}
