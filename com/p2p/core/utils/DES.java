package com.p2p.core.utils;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DES {
    static byte[] key = new byte[]{(byte) -100, (byte) -82, (byte) 106, (byte) 90, (byte) -31, (byte) -4, (byte) -80, (byte) -126};

    public static byte[] des(byte[] str, int type) throws Exception {
        if (type == 0) {
            return desEncrypt(str, key);
        }
        return desDecrypt(str, key);
    }

    public static byte[] desEncrypt(byte[] source, byte[] rawKeyData) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeySpec key = new SecretKeySpec(rawKeyData, "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(1, key);
        return cipher.doFinal(source);
    }

    public static byte[] desDecrypt(byte[] data, byte[] rawKeyData) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeySpec key = new SecretKeySpec(rawKeyData, "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(2, key);
        return cipher.doFinal(data);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("[");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            String hv = Integer.toHexString(src[i] & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (i != src.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
