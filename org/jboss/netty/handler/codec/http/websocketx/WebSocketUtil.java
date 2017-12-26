package org.jboss.netty.handler.codec.http.websocketx;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.util.CharsetUtil;

final class WebSocketUtil {
    static ChannelBuffer md5(ChannelBuffer buffer) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (buffer.hasArray()) {
                md.update(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes());
            } else {
                md.update(buffer.toByteBuffer());
            }
            return ChannelBuffers.wrappedBuffer(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError("MD5 not supported on this platform");
        }
    }

    static ChannelBuffer sha1(ChannelBuffer buffer) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            if (buffer.hasArray()) {
                md.update(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes());
            } else {
                md.update(buffer.toByteBuffer());
            }
            return ChannelBuffers.wrappedBuffer(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError("SHA-1 not supported on this platform");
        }
    }

    static String base64(ChannelBuffer buffer) {
        return Base64.encode(buffer).toString(CharsetUtil.UTF_8);
    }

    static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) randomNumber(0, 255);
        }
        return bytes;
    }

    static int randomNumber(int min, int max) {
        return (int) ((Math.random() * ((double) max)) + ((double) min));
    }

    private WebSocketUtil() {
    }
}
