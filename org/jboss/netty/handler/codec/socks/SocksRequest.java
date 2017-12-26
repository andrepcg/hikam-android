package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.handler.codec.socks.SocksMessage.MessageType;

public abstract class SocksRequest extends SocksMessage {
    private final SocksRequestType socksRequestType;

    public enum SocksRequestType {
        INIT,
        AUTH,
        CMD,
        UNKNOWN
    }

    protected SocksRequest(SocksRequestType socksRequestType) {
        super(MessageType.REQUEST);
        if (socksRequestType == null) {
            throw new NullPointerException("socksRequestType");
        }
        this.socksRequestType = socksRequestType;
    }

    public SocksRequestType getSocksRequestType() {
        return this.socksRequestType;
    }
}
