package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.socks.SocksMessage.AddressType;
import org.jboss.netty.handler.codec.socks.SocksMessage.CmdStatus;
import org.jboss.netty.handler.codec.socks.SocksMessage.ProtocolVersion;

public class SocksCmdResponseDecoder extends ReplayingDecoder<State> {
    private AddressType addressType;
    private CmdStatus cmdStatus;
    private int fieldLength;
    private String host;
    private SocksResponse msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
    private int port;
    private byte reserved;
    private ProtocolVersion version;

    public enum State {
        CHECK_PROTOCOL_VERSION,
        READ_CMD_HEADER,
        READ_CMD_ADDRESS
    }

    public SocksCmdResponseDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected java.lang.Object decode(org.jboss.netty.channel.ChannelHandlerContext r4, org.jboss.netty.channel.Channel r5, org.jboss.netty.buffer.ChannelBuffer r6, org.jboss.netty.handler.codec.socks.SocksCmdResponseDecoder.State r7) throws java.lang.Exception {
        /*
        r3 = this;
        r0 = org.jboss.netty.handler.codec.socks.SocksCmdResponseDecoder.C08481.f501x4e40fe99;
        r1 = r7.ordinal();
        r0 = r0[r1];
        switch(r0) {
            case 1: goto L_0x0015;
            case 2: goto L_0x002a;
            case 3: goto L_0x0049;
            default: goto L_0x000b;
        };
    L_0x000b:
        r0 = r4.getPipeline();
        r0.remove(r3);
        r0 = r3.msg;
        return r0;
    L_0x0015:
        r0 = r6.readByte();
        r0 = org.jboss.netty.handler.codec.socks.SocksMessage.ProtocolVersion.fromByte(r0);
        r3.version = r0;
        r0 = r3.version;
        r1 = org.jboss.netty.handler.codec.socks.SocksMessage.ProtocolVersion.SOCKS5;
        if (r0 != r1) goto L_0x000b;
    L_0x0025:
        r0 = org.jboss.netty.handler.codec.socks.SocksCmdResponseDecoder.State.READ_CMD_HEADER;
        r3.checkpoint(r0);
    L_0x002a:
        r0 = r6.readByte();
        r0 = org.jboss.netty.handler.codec.socks.SocksMessage.CmdStatus.fromByte(r0);
        r3.cmdStatus = r0;
        r0 = r6.readByte();
        r3.reserved = r0;
        r0 = r6.readByte();
        r0 = org.jboss.netty.handler.codec.socks.SocksMessage.AddressType.fromByte(r0);
        r3.addressType = r0;
        r0 = org.jboss.netty.handler.codec.socks.SocksCmdResponseDecoder.State.READ_CMD_ADDRESS;
        r3.checkpoint(r0);
    L_0x0049:
        r0 = org.jboss.netty.handler.codec.socks.SocksCmdResponseDecoder.C08481.f502x1cb923ce;
        r1 = r3.addressType;
        r1 = r1.ordinal();
        r0 = r0[r1];
        switch(r0) {
            case 1: goto L_0x0057;
            case 2: goto L_0x0073;
            case 3: goto L_0x009a;
            default: goto L_0x0056;
        };
    L_0x0056:
        goto L_0x000b;
    L_0x0057:
        r0 = r6.readInt();
        r0 = org.jboss.netty.handler.codec.socks.SocksCommonUtils.intToIp(r0);
        r3.host = r0;
        r0 = r6.readUnsignedShort();
        r3.port = r0;
        r0 = new org.jboss.netty.handler.codec.socks.SocksCmdResponse;
        r1 = r3.cmdStatus;
        r2 = r3.addressType;
        r0.<init>(r1, r2);
        r3.msg = r0;
        goto L_0x000b;
    L_0x0073:
        r0 = r6.readByte();
        r3.fieldLength = r0;
        r0 = r3.fieldLength;
        r0 = r6.readBytes(r0);
        r1 = org.jboss.netty.util.CharsetUtil.US_ASCII;
        r0 = r0.toString(r1);
        r3.host = r0;
        r0 = r6.readUnsignedShort();
        r3.port = r0;
        r0 = new org.jboss.netty.handler.codec.socks.SocksCmdResponse;
        r1 = r3.cmdStatus;
        r2 = r3.addressType;
        r0.<init>(r1, r2);
        r3.msg = r0;
        goto L_0x000b;
    L_0x009a:
        r0 = 16;
        r0 = r6.readBytes(r0);
        r0 = r0.array();
        r0 = org.jboss.netty.handler.codec.socks.SocksCommonUtils.ipv6toStr(r0);
        r3.host = r0;
        r0 = r6.readUnsignedShort();
        r3.port = r0;
        r0 = new org.jboss.netty.handler.codec.socks.SocksCmdResponse;
        r1 = r3.cmdStatus;
        r2 = r3.addressType;
        r0.<init>(r1, r2);
        r3.msg = r0;
        goto L_0x000b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.codec.socks.SocksCmdResponseDecoder.decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer, org.jboss.netty.handler.codec.socks.SocksCmdResponseDecoder$State):java.lang.Object");
    }
}
