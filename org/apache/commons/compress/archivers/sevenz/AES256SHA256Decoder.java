package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.compress.PasswordRequiredException;

class AES256SHA256Decoder extends CoderBase {
    AES256SHA256Decoder() {
        super(new Class[0]);
    }

    InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] passwordBytes) throws IOException {
        final Coder coder2 = coder;
        final String str = archiveName;
        final byte[] bArr = passwordBytes;
        final InputStream inputStream = in;
        return new InputStream() {
            private CipherInputStream cipherInputStream = null;
            private boolean isInitialized = false;

            private CipherInputStream init() throws IOException {
                if (this.isInitialized) {
                    return this.cipherInputStream;
                }
                int byte0 = coder2.properties[0] & 255;
                int numCyclesPower = byte0 & 63;
                int byte1 = coder2.properties[1] & 255;
                int ivSize = ((byte0 >> 6) & 1) + (byte1 & 15);
                int saltSize = ((byte0 >> 7) & 1) + (byte1 >> 4);
                if ((saltSize + 2) + ivSize > coder2.properties.length) {
                    throw new IOException("Salt size + IV size too long in " + str);
                }
                Object salt = new byte[saltSize];
                System.arraycopy(coder2.properties, 2, salt, 0, saltSize);
                byte[] iv = new byte[16];
                System.arraycopy(coder2.properties, saltSize + 2, iv, 0, ivSize);
                if (bArr == null) {
                    throw new PasswordRequiredException(str);
                }
                byte[] aesKeyBytes;
                if (numCyclesPower == 63) {
                    aesKeyBytes = new byte[32];
                    System.arraycopy(salt, 0, aesKeyBytes, 0, saltSize);
                    System.arraycopy(bArr, 0, aesKeyBytes, saltSize, Math.min(bArr.length, aesKeyBytes.length - saltSize));
                } else {
                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] extra = new byte[8];
                        for (long j = 0; j < (1 << numCyclesPower); j++) {
                            digest.update(salt);
                            digest.update(bArr);
                            digest.update(extra);
                            for (int k = 0; k < extra.length; k++) {
                                extra[k] = (byte) (extra[k] + 1);
                                if (extra[k] != (byte) 0) {
                                    break;
                                }
                            }
                        }
                        aesKeyBytes = digest.digest();
                    } catch (Throwable noSuchAlgorithmException) {
                        throw new IOException("SHA-256 is unsupported by your Java implementation", noSuchAlgorithmException);
                    }
                }
                SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
                try {
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(2, aesKey, new IvParameterSpec(iv));
                    this.cipherInputStream = new CipherInputStream(inputStream, cipher);
                    this.isInitialized = true;
                    return this.cipherInputStream;
                } catch (GeneralSecurityException generalSecurityException) {
                    throw new IOException("Decryption error (do you have the JCE Unlimited Strength Jurisdiction Policy Files installed?)", generalSecurityException);
                }
            }

            public int read() throws IOException {
                return init().read();
            }

            public int read(byte[] b, int off, int len) throws IOException {
                return init().read(b, off, len);
            }

            public void close() {
            }
        };
    }
}
