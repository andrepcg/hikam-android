package org.jboss.netty.handler.codec.socks;

import java.util.ArrayList;
import java.util.List;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.socks.SocksMessage.AuthScheme;
import org.jboss.netty.handler.codec.socks.SocksMessage.ProtocolVersion;

public class SocksInitRequestDecoder extends ReplayingDecoder<State> {
    private byte authSchemeNum;
    private final List<AuthScheme> authSchemes = new ArrayList();
    private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
    private ProtocolVersion version;

    enum State {
        CHECK_PROTOCOL_VERSION,
        READ_AUTH_SCHEMES
    }

    public SocksInitRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, State state) throws Exception {
        switch (state) {
            case CHECK_PROTOCOL_VERSION:
                this.version = ProtocolVersion.fromByte(buffer.readByte());
                if (this.version == ProtocolVersion.SOCKS5) {
                    checkpoint(State.READ_AUTH_SCHEMES);
                }
                break;
            case READ_AUTH_SCHEMES:
                this.authSchemes.clear();
                this.authSchemeNum = buffer.readByte();
                for (byte i = (byte) 0; i < this.authSchemeNum; i++) {
                    this.authSchemes.add(AuthScheme.fromByte(buffer.readByte()));
                }
                this.msg = new SocksInitRequest(this.authSchemes);
                break;
        }
        ctx.getPipeline().remove((ChannelHandler) this);
        return this.msg;
    }
}
