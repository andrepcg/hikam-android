package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.socks.SocksMessage.AddressType;
import org.jboss.netty.handler.codec.socks.SocksMessage.CmdType;
import org.jboss.netty.handler.codec.socks.SocksMessage.ProtocolVersion;

public class SocksCmdRequestDecoder extends ReplayingDecoder<State> {
    private AddressType addressType;
    private CmdType cmdType;
    private int fieldLength;
    private String host;
    private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
    private int port;
    private byte reserved;
    private ProtocolVersion version;

    enum State {
        CHECK_PROTOCOL_VERSION,
        READ_CMD_HEADER,
        READ_CMD_ADDRESS
    }

    public SocksCmdRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected java.lang.Object decode(org.jboss.netty.channel.ChannelHandlerContext r6, org.jboss.netty.channel.Channel r7, org.jboss.netty.buffer.ChannelBuffer r8, org.jboss.netty.handler.codec.socks.SocksCmdRequestDecoder.State r9) throws java.lang.Exception {
        /*
        r5 = this;
        r0 = org.jboss.netty.handler.codec.socks.SocksCmdRequestDecoder.C08461.f498xb7c50759;
        r1 = r9.ordinal();
        r0 = r0[r1];
        switch(r0) {
            case 1: goto L_0x0015;
            case 2: goto L_0x002a;
            case 3: goto L_0x0049;
            default: goto L_0x000b;
        };
    L_0x000b:
        r0 = r6.getPipeline();
        r0.remove(r5);
        r0 = r5.msg;
        return r0;
    L_0x0015:
        r0 = r8.readByte();
        r0 = org.jboss.netty.handler.codec.socks.SocksMessage.ProtocolVersion.fromByte(r0);
        r5.version = r0;
        r0 = r5.version;
        r1 = org.jboss.netty.handler.codec.socks.SocksMessage.ProtocolVersion.SOCKS5;
        if (r0 != r1) goto L_0x000b;
    L_0x0025:
        r0 = org.jboss.netty.handler.codec.socks.SocksCmdRequestDecoder.State.READ_CMD_HEADER;
        r5.checkpoint(r0);
    L_0x002a:
        r0 = r8.readByte();
        r0 = org.jboss.netty.handler.codec.socks.SocksMessage.CmdType.fromByte(r0);
        r5.cmdType = r0;
        r0 = r8.readByte();
        r5.reserved = r0;
        r0 = r8.readByte();
        r0 = org.jboss.netty.handler.codec.socks.SocksMessage.AddressType.fromByte(r0);
        r5.addressType = r0;
        r0 = org.jboss.netty.handler.codec.socks.SocksCmdRequestDecoder.State.READ_CMD_ADDRESS;
        r5.checkpoint(r0);
    L_0x0049:
        r0 = org.jboss.netty.handler.codec.socks.SocksCmdRequestDecoder.C08461.f499x1cb923ce;
        r1 = r5.addressType;
        r1 = r1.ordinal();
        r0 = r0[r1];
        switch(r0) {
            case 1: goto L_0x0057;
            case 2: goto L_0x0077;
            case 3: goto L_0x00a2;
            default: goto L_0x0056;
        };
    L_0x0056:
        goto L_0x000b;
    L_0x0057:
        r0 = r8.readInt();
        r0 = org.jboss.netty.handler.codec.socks.SocksCommonUtils.intToIp(r0);
        r5.host = r0;
        r0 = r8.readUnsignedShort();
        r5.port = r0;
        r0 = new org.jboss.netty.handler.codec.socks.SocksCmdRequest;
        r1 = r5.cmdType;
        r2 = r5.addressType;
        r3 = r5.host;
        r4 = r5.port;
        r0.<init>(r1, r2, r3, r4);
        r5.msg = r0;
        goto L_0x000b;
    L_0x0077:
        r0 = r8.readByte();
        r5.fieldLength = r0;
        r0 = r5.fieldLength;
        r0 = r8.readBytes(r0);
        r1 = org.jboss.netty.util.CharsetUtil.US_ASCII;
        r0 = r0.toString(r1);
        r5.host = r0;
        r0 = r8.readUnsignedShort();
        r5.port = r0;
        r0 = new org.jboss.netty.handler.codec.socks.SocksCmdRequest;
        r1 = r5.cmdType;
        r2 = r5.addressType;
        r3 = r5.host;
        r4 = r5.port;
        r0.<init>(r1, r2, r3, r4);
        r5.msg = r0;
        goto L_0x000b;
    L_0x00a2:
        r0 = 16;
        r0 = r8.readBytes(r0);
        r0 = r0.array();
        r0 = org.jboss.netty.handler.codec.socks.SocksCommonUtils.ipv6toStr(r0);
        r5.host = r0;
        r0 = r8.readUnsignedShort();
        r5.port = r0;
        r0 = new org.jboss.netty.handler.codec.socks.SocksCmdRequest;
        r1 = r5.cmdType;
        r2 = r5.addressType;
        r3 = r5.host;
        r4 = r5.port;
        r0.<init>(r1, r2, r3, r4);
        r5.msg = r0;
        goto L_0x000b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.codec.socks.SocksCmdRequestDecoder.decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer, org.jboss.netty.handler.codec.socks.SocksCmdRequestDecoder$State):java.lang.Object");
    }
}
