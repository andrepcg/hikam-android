package org.jboss.netty.handler.ssl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.CharsetUtil;

final class PemReader {
    private static final Pattern CERT_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
    private static final Pattern KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PemReader.class);

    static ChannelBuffer[] readCertificates(File file) throws CertificateException {
        try {
            String content = readContent(file);
            List<ChannelBuffer> certs = new ArrayList();
            Matcher m = CERT_PATTERN.matcher(content);
            for (int start = 0; m.find(start); start = m.end()) {
                certs.add(Base64.decode(ChannelBuffers.copiedBuffer(m.group(1), CharsetUtil.US_ASCII)));
            }
            if (!certs.isEmpty()) {
                return (ChannelBuffer[]) certs.toArray(new ChannelBuffer[certs.size()]);
            }
            throw new CertificateException("found no certificates: " + file);
        } catch (IOException e) {
            throw new CertificateException("failed to read a file: " + file, e);
        }
    }

    static ChannelBuffer readPrivateKey(File file) throws KeyException {
        try {
            Matcher m = KEY_PATTERN.matcher(readContent(file));
            if (m.find()) {
                return Base64.decode(ChannelBuffers.copiedBuffer(m.group(1), CharsetUtil.US_ASCII));
            }
            throw new KeyException("found no private key: " + file);
        } catch (IOException e) {
            throw new KeyException("failed to read a file: " + file, e);
        }
    }

    private static String readContent(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        OutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[8192];
            while (true) {
                int ret = in.read(buf);
                if (ret < 0) {
                    break;
                }
                out.write(buf, 0, ret);
            }
            String byteArrayOutputStream = out.toString(CharsetUtil.US_ASCII.name());
            return byteArrayOutputStream;
        } finally {
            safeClose(in);
            safeClose(out);
        }
    }

    private static void safeClose(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            logger.warn("Failed to close a stream.", e);
        }
    }

    private static void safeClose(OutputStream out) {
        try {
            out.close();
        } catch (IOException e) {
            logger.warn("Failed to close a stream.", e);
        }
    }

    private PemReader() {
    }
}
