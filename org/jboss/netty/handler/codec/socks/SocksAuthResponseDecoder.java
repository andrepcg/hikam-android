package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.socks.SocksMessage.AuthStatus;
import org.jboss.netty.handler.codec.socks.SocksMessage.SubnegotiationVersion;

public class SocksAuthResponseDecoder extends ReplayingDecoder<State> {
    private AuthStatus authStatus;
    private SocksResponse msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
    private SubnegotiationVersion version;

    public enum State {
        CHECK_PROTOCOL_VERSION,
        READ_AUTH_RESPONSE
    }

    public SocksAuthResponseDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {
        switch (state) {
            case CHECK_PROTOCOL_VERSION:
                this.version = SubnegotiationVersion.fromByte(buffer.readByte());
                if (this.version == SubnegotiationVersion.AUTH_PASSWORD) {
                    checkpoint(State.READ_AUTH_RESPONSE);
                }
                break;
            case READ_AUTH_RESPONSE:
                this.authStatus = AuthStatus.fromByte(buffer.readByte());
                this.msg = new SocksAuthResponse(this.authStatus);
                break;
        }
        ctx.getPipeline().remove((ChannelHandler) this);
        return this.msg;
    }
}
