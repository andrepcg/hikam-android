package com.tencent.bugly.proguard;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/* compiled from: BUGLY */
public final class ae implements af {
    private String f562a = null;

    public final byte[] mo2281a(byte[] bArr) throws Exception {
        if (this.f562a == null || bArr == null) {
            return null;
        }
        Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
        instance.init(2, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.f562a.getBytes("UTF-8"))), new IvParameterSpec(this.f562a.getBytes("UTF-8")));
        return instance.doFinal(bArr);
    }

    public final byte[] mo2282b(byte[] bArr) throws Exception, NoSuchAlgorithmException {
        if (this.f562a == null || bArr == null) {
            return null;
        }
        Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
        instance.init(1, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.f562a.getBytes("UTF-8"))), new IvParameterSpec(this.f562a.getBytes("UTF-8")));
        return instance.doFinal(bArr);
    }

    public final void mo2280a(String str) {
        if (str != null) {
            this.f562a = str;
        }
    }
}
