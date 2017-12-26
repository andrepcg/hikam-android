package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.socks.SocksMessage.AuthStatus;
import org.jboss.netty.handler.codec.socks.SocksMessage.SubnegotiationVersion;
import org.jboss.netty.handler.codec.socks.SocksResponse.SocksResponseType;

public final class SocksAuthResponse extends SocksResponse {
    private static final SubnegotiationVersion SUBNEGOTIATION_VERSION = SubnegotiationVersion.AUTH_PASSWORD;
    private final AuthStatus authStatus;

    public SocksAuthResponse(AuthStatus authStatus) {
        super(SocksResponseType.AUTH);
        if (authStatus == null) {
            throw new NullPointerException("authStatus");
        }
        this.authStatus = authStatus;
    }

    public AuthStatus getAuthStatus() {
        return this.authStatus;
    }

    public void encodeAsByteBuf(ChannelBuffer channelBuffer) {
        channelBuffer.writeByte(SUBNEGOTIATION_VERSION.getByteValue());
        channelBuffer.writeByte(this.authStatus.getByteValue());
    }
}
