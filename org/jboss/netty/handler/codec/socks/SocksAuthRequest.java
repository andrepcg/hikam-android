package org.jboss.netty.handler.codec.socks;

import java.nio.charset.CharsetEncoder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.socks.SocksMessage.SubnegotiationVersion;
import org.jboss.netty.handler.codec.socks.SocksRequest.SocksRequestType;
import org.jboss.netty.util.CharsetUtil;

public final class SocksAuthRequest extends SocksRequest {
    private static final SubnegotiationVersion SUBNEGOTIATION_VERSION = SubnegotiationVersion.AUTH_PASSWORD;
    private static final CharsetEncoder asciiEncoder = CharsetUtil.getEncoder(CharsetUtil.US_ASCII);
    private final String password;
    private final String username;

    public SocksAuthRequest(String username, String password) {
        super(SocksRequestType.AUTH);
        if (username == null) {
            throw new NullPointerException("username");
        } else if (password == null) {
            throw new NullPointerException("password");
        } else if (!asciiEncoder.canEncode(username) || !asciiEncoder.canEncode(password)) {
            throw new IllegalArgumentException("username: " + username + " or password: **** values should be in pure ascii");
        } else if (username.length() > 255) {
            throw new IllegalArgumentException("username: " + username + " exceeds 255 char limit");
        } else if (password.length() > 255) {
            throw new IllegalArgumentException("password: **** exceeds 255 char limit");
        } else {
            this.username = username;
            this.password = password;
        }
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void encodeAsByteBuf(ChannelBuffer channelBuffer) throws Exception {
        channelBuffer.writeByte(SUBNEGOTIATION_VERSION.getByteValue());
        channelBuffer.writeByte(this.username.length());
        channelBuffer.writeBytes(this.username.getBytes("US-ASCII"));
        channelBuffer.writeByte(this.password.length());
        channelBuffer.writeBytes(this.password.getBytes("US-ASCII"));
    }
}
