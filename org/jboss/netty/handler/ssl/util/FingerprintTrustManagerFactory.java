package org.jboss.netty.handler.ssl.util;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.internal.EmptyArrays;

public final class FingerprintTrustManagerFactory extends SimpleTrustManagerFactory {
    private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("^[0-9a-fA-F:]+$");
    private static final Pattern FINGERPRINT_STRIP_PATTERN = Pattern.compile(":");
    private static final int SHA1_BYTE_LEN = 20;
    private static final int SHA1_HEX_LEN = 40;
    private static final ThreadLocal<MessageDigest> tlmd = new C08651();
    private final byte[][] fingerprints;
    private final TrustManager tm;

    static class C08651 extends ThreadLocal<MessageDigest> {
        C08651() {
        }

        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("SHA1");
            } catch (NoSuchAlgorithmException e) {
                throw new Error(e);
            }
        }
    }

    class C08662 implements X509TrustManager {
        C08662() {
        }

        public void checkClientTrusted(X509Certificate[] chain, String s) throws CertificateException {
            checkTrusted("client", chain);
        }

        public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
            checkTrusted("server", chain);
        }

        private void checkTrusted(String type, X509Certificate[] chain) throws CertificateException {
            X509Certificate cert = chain[0];
            byte[] fingerprint = fingerprint(cert);
            boolean found = false;
            for (byte[] allowedFingerprint : FingerprintTrustManagerFactory.this.fingerprints) {
                if (Arrays.equals(fingerprint, allowedFingerprint)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new CertificateException(type + " certificate with unknown fingerprint: " + cert.getSubjectDN());
            }
        }

        private byte[] fingerprint(X509Certificate cert) throws CertificateEncodingException {
            MessageDigest md = (MessageDigest) FingerprintTrustManagerFactory.tlmd.get();
            md.reset();
            return md.digest(cert.getEncoded());
        }

        public X509Certificate[] getAcceptedIssuers() {
            return EmptyArrays.EMPTY_X509_CERTIFICATES;
        }
    }

    public FingerprintTrustManagerFactory(Iterable<String> fingerprints) {
        this(toFingerprintArray(fingerprints));
    }

    public FingerprintTrustManagerFactory(String... fingerprints) {
        this(toFingerprintArray(Arrays.asList(fingerprints)));
    }

    public FingerprintTrustManagerFactory(byte[]... fingerprints) {
        this.tm = new C08662();
        if (fingerprints == null) {
            throw new NullPointerException("fingerprints");
        }
        List<byte[]> list = new ArrayList();
        byte[][] arr$ = fingerprints;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            byte[] f = arr$[i$];
            if (f == null) {
                break;
            } else if (f.length != 20) {
                throw new IllegalArgumentException("malformed fingerprint: " + ChannelBuffers.hexDump(ChannelBuffers.wrappedBuffer(f)) + " (expected: SHA1)");
            } else {
                list.add(f.clone());
                i$++;
            }
        }
        this.fingerprints = (byte[][]) list.toArray(new byte[list.size()][]);
    }

    private static byte[][] toFingerprintArray(Iterable<String> fingerprints) {
        if (fingerprints == null) {
            throw new NullPointerException("fingerprints");
        }
        List<byte[]> list = new ArrayList();
        for (String f : fingerprints) {
            String f2;
            if (f2 == null) {
                break;
            } else if (FINGERPRINT_PATTERN.matcher(f2).matches()) {
                f2 = FINGERPRINT_STRIP_PATTERN.matcher(f2).replaceAll("");
                if (f2.length() != 40) {
                    throw new IllegalArgumentException("malformed fingerprint: " + f2 + " (expected: SHA1)");
                }
                byte[] farr = new byte[20];
                for (int i = 0; i < farr.length; i++) {
                    int strIdx = i << 1;
                    farr[i] = (byte) Integer.parseInt(f2.substring(strIdx, strIdx + 2), 16);
                }
            } else {
                throw new IllegalArgumentException("malformed fingerprint: " + f2);
            }
        }
        return (byte[][]) list.toArray(new byte[list.size()][]);
    }

    protected void engineInit(KeyStore keyStore) throws Exception {
    }

    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{this.tm};
    }
}
