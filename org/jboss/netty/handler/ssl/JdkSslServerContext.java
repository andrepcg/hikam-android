package org.jboss.netty.handler.ssl;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;

public final class JdkSslServerContext extends JdkSslContext {
    private final SSLContext ctx;
    private final List<String> nextProtocols;

    public JdkSslServerContext(File certChainFile, File keyFile) throws SSLException {
        this(certChainFile, keyFile, null);
    }

    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        this(null, certChainFile, keyFile, keyPassword, null, null, 0, 0);
    }

    public JdkSslServerContext(SslBufferPool bufPool, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(bufPool, ciphers);
        if (certChainFile == null) {
            throw new NullPointerException("certChainFile");
        } else if (keyFile == null) {
            throw new NullPointerException("keyFile");
        } else {
            if (keyPassword == null) {
                keyPassword = "";
            }
            if (nextProtocols == null || !nextProtocols.iterator().hasNext()) {
                this.nextProtocols = Collections.emptyList();
            } else if (JettyNpnSslEngine.isAvailable()) {
                List<String> list = new ArrayList();
                for (String p : nextProtocols) {
                    if (p == null) {
                        break;
                    }
                    list.add(p);
                }
                this.nextProtocols = Collections.unmodifiableList(list);
            } else {
                throw new SSLException("NPN/ALPN unsupported: " + nextProtocols);
            }
            String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
            if (algorithm == null) {
                algorithm = "SunX509";
            }
            try {
                PrivateKey key;
                KeyStore ks = KeyStore.getInstance("JKS");
                ks.load(null, null);
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                KeyFactory rsaKF = KeyFactory.getInstance("RSA");
                KeyFactory dsaKF = KeyFactory.getInstance("DSA");
                ChannelBuffer encodedKeyBuf = PemReader.readPrivateKey(keyFile);
                byte[] encodedKey = new byte[encodedKeyBuf.readableBytes()];
                encodedKeyBuf.readBytes(encodedKey);
                char[] keyPasswordChars = keyPassword.toCharArray();
                PKCS8EncodedKeySpec encodedKeySpec = generateKeySpec(keyPasswordChars, encodedKey);
                try {
                    key = rsaKF.generatePrivate(encodedKeySpec);
                } catch (InvalidKeySpecException e) {
                    key = dsaKF.generatePrivate(encodedKeySpec);
                }
                List<Certificate> certChain = new ArrayList();
                for (ChannelBuffer buf : PemReader.readCertificates(certChainFile)) {
                    certChain.add(cf.generateCertificate(new ChannelBufferInputStream(buf)));
                }
                ks.setKeyEntry("key", key, keyPasswordChars, (Certificate[]) certChain.toArray(new Certificate[certChain.size()]));
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
                kmf.init(ks, keyPasswordChars);
                this.ctx = SSLContext.getInstance("TLS");
                this.ctx.init(kmf.getKeyManagers(), null, null);
                SSLSessionContext sessCtx = this.ctx.getServerSessionContext();
                if (sessionCacheSize > 0) {
                    sessCtx.setSessionCacheSize((int) Math.min(sessionCacheSize, 2147483647L));
                }
                if (sessionTimeout > 0) {
                    sessCtx.setSessionTimeout((int) Math.min(sessionTimeout, 2147483647L));
                }
            } catch (Exception e2) {
                throw new SSLException("failed to initialize the server-side SSL context", e2);
            }
        }
    }

    public boolean isClient() {
        return false;
    }

    public List<String> nextProtocols() {
        return this.nextProtocols;
    }

    public SSLContext context() {
        return this.ctx;
    }

    private static PKCS8EncodedKeySpec generateKeySpec(char[] password, byte[] key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (password == null || password.length == 0) {
            return new PKCS8EncodedKeySpec(key);
        }
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(key);
        SecretKey pbeKey = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName()).generateSecret(new PBEKeySpec(password));
        Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
        cipher.init(2, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());
        return encryptedPrivateKeyInfo.getKeySpec(cipher);
    }
}
