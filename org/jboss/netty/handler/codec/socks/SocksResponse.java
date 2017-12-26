package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.handler.codec.socks.SocksMessage.MessageType;

public abstract class SocksResponse extends SocksMessage {
    private final SocksResponseType socksResponseType;

    public enum SocksResponseType {
        INIT,
        AUTH,
        CMD,
        UNKNOWN
    }

    protected SocksResponse(SocksResponseType socksResponseType) {
        super(MessageType.RESPONSE);
        if (socksResponseType == null) {
            throw new NullPointerException("socksResponseType");
        }
        this.socksResponseType = socksResponseType;
    }

    public SocksResponseType getSocksResponseType() {
        return this.socksResponseType;
    }
}
