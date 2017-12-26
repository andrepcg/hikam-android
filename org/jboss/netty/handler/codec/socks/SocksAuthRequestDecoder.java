package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.socks.SocksMessage.SubnegotiationVersion;

public class SocksAuthRequestDecoder extends ReplayingDecoder<State> {
    private int fieldLength;
    private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
    private String password;
    private String username;
    private SubnegotiationVersion version;

    enum State {
        CHECK_PROTOCOL_VERSION,
        READ_USERNAME,
        READ_PASSWORD
    }

    public SocksAuthRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected java.lang.Object decode(org.jboss.netty.channel.ChannelHandlerContext r4, org.jboss.netty.channel.Channel r5, org.jboss.netty.buffer.ChannelBuffer r6, org.jboss.netty.handler.codec.socks.SocksAuthRequestDecoder.State r7) throws java.lang.Exception {
        /*
        r3 = this;
        r0 = org.jboss.netty.handler.codec.socks.SocksAuthRequestDecoder.C08431.f495xab6245ed;
        r1 = r7.ordinal();
        r0 = r0[r1];
        switch(r0) {
            case 1: goto L_0x0015;
            case 2: goto L_0x002a;
            case 3: goto L_0x0043;
            default: goto L_0x000b;
        };
    L_0x000b:
        r0 = r4.getPipeline();
        r0.remove(r3);
        r0 = r3.msg;
        return r0;
    L_0x0015:
        r0 = r6.readByte();
        r0 = org.jboss.netty.handler.codec.socks.SocksMessage.SubnegotiationVersion.fromByte(r0);
        r3.version = r0;
        r0 = r3.version;
        r1 = org.jboss.netty.handler.codec.socks.SocksMessage.SubnegotiationVersion.AUTH_PASSWORD;
        if (r0 != r1) goto L_0x000b;
    L_0x0025:
        r0 = org.jboss.netty.handler.codec.socks.SocksAuthRequestDecoder.State.READ_USERNAME;
        r3.checkpoint(r0);
    L_0x002a:
        r0 = r6.readByte();
        r3.fieldLength = r0;
        r0 = r3.fieldLength;
        r0 = r6.readBytes(r0);
        r1 = org.jboss.netty.util.CharsetUtil.US_ASCII;
        r0 = r0.toString(r1);
        r3.username = r0;
        r0 = org.jboss.netty.handler.codec.socks.SocksAuthRequestDecoder.State.READ_PASSWORD;
        r3.checkpoint(r0);
    L_0x0043:
        r0 = r6.readByte();
        r3.fieldLength = r0;
        r0 = r3.fieldLength;
        r0 = r6.readBytes(r0);
        r1 = org.jboss.netty.util.CharsetUtil.US_ASCII;
        r0 = r0.toString(r1);
        r3.password = r0;
        r0 = new org.jboss.netty.handler.codec.socks.SocksAuthRequest;
        r1 = r3.username;
        r2 = r3.password;
        r0.<init>(r1, r2);
        r3.msg = r0;
        goto L_0x000b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.codec.socks.SocksAuthRequestDecoder.decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer, org.jboss.netty.handler.codec.socks.SocksAuthRequestDecoder$State):java.lang.Object");
    }
}
