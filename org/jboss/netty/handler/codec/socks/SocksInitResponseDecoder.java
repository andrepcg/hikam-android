package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.socks.SocksMessage.AuthScheme;
import org.jboss.netty.handler.codec.socks.SocksMessage.ProtocolVersion;

public class SocksInitResponseDecoder extends ReplayingDecoder<State> {
    private AuthScheme authScheme;
    private SocksResponse msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
    private ProtocolVersion version;

    public enum State {
        CHECK_PROTOCOL_VERSION,
        READ_PREFFERED_AUTH_TYPE
    }

    public SocksInitResponseDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {
        switch (state) {
            case CHECK_PROTOCOL_VERSION:
                this.version = ProtocolVersion.fromByte(buffer.readByte());
                if (this.version == ProtocolVersion.SOCKS5) {
                    checkpoint(State.READ_PREFFERED_AUTH_TYPE);
                }
                break;
            case READ_PREFFERED_AUTH_TYPE:
                this.authScheme = AuthScheme.fromByte(buffer.readByte());
                this.msg = new SocksInitResponse(this.authScheme);
                break;
        }
        ctx.getPipeline().remove((ChannelHandler) this);
        return this.msg;
    }
}
