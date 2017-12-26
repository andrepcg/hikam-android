package org.jboss.netty.handler.codec.socks;

import java.util.Collections;
import java.util.List;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.socks.SocksMessage.AuthScheme;
import org.jboss.netty.handler.codec.socks.SocksRequest.SocksRequestType;

public final class SocksInitRequest extends SocksRequest {
    private final List<AuthScheme> authSchemes;

    public SocksInitRequest(List<AuthScheme> authSchemes) {
        super(SocksRequestType.INIT);
        if (authSchemes == null) {
            throw new NullPointerException("authSchemes");
        }
        this.authSchemes = authSchemes;
    }

    public List<AuthScheme> getAuthSchemes() {
        return Collections.unmodifiableList(this.authSchemes);
    }

    public void encodeAsByteBuf(ChannelBuffer channelBuffer) {
        channelBuffer.writeByte(getProtocolVersion().getByteValue());
        channelBuffer.writeByte(this.authSchemes.size());
        for (AuthScheme authScheme : this.authSchemes) {
            channelBuffer.writeByte(authScheme.getByteValue());
        }
    }
}
