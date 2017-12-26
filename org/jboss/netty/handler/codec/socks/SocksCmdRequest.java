package org.jboss.netty.handler.codec.socks;

import java.net.IDN;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.socks.SocksMessage.AddressType;
import org.jboss.netty.handler.codec.socks.SocksMessage.CmdType;
import org.jboss.netty.handler.codec.socks.SocksRequest.SocksRequestType;
import org.jboss.netty.util.NetUtil;
import org.jboss.netty.util.internal.DetectionUtil;

public final class SocksCmdRequest extends SocksRequest {
    private final AddressType addressType;
    private final CmdType cmdType;
    private final String host;
    private final int port;

    public SocksCmdRequest(CmdType cmdType, AddressType addressType, String host, int port) {
        super(SocksRequestType.CMD);
        if (DetectionUtil.javaVersion() < 6) {
            throw new IllegalStateException("Only supported with Java version 6+");
        } else if (cmdType == null) {
            throw new NullPointerException("cmdType");
        } else if (addressType == null) {
            throw new NullPointerException("addressType");
        } else if (host == null) {
            throw new NullPointerException("host");
        } else {
            switch (addressType) {
                case IPv4:
                    if (!NetUtil.isValidIpV4Address(host)) {
                        throw new IllegalArgumentException(host + " is not a valid IPv4 address");
                    }
                    break;
                case DOMAIN:
                    if (IDN.toASCII(host).length() > 255) {
                        throw new IllegalArgumentException(host + " IDN: " + IDN.toASCII(host) + " exceeds 255 char limit");
                    }
                    break;
                case IPv6:
                    if (!NetUtil.isValidIpV6Address(host)) {
                        throw new IllegalArgumentException(host + " is not a valid IPv6 address");
                    }
                    break;
            }
            if (port <= 0 || port >= 65536) {
                throw new IllegalArgumentException(port + " is not in bounds 0 < x < 65536");
            }
            this.cmdType = cmdType;
            this.addressType = addressType;
            this.host = IDN.toASCII(host);
            this.port = port;
        }
    }

    public CmdType getCmdType() {
        return this.cmdType;
    }

    public AddressType getAddressType() {
        return this.addressType;
    }

    public String getHost() {
        return IDN.toUnicode(this.host);
    }

    public int getPort() {
        return this.port;
    }

    public void encodeAsByteBuf(ChannelBuffer channelBuffer) throws Exception {
        channelBuffer.writeByte(getProtocolVersion().getByteValue());
        channelBuffer.writeByte(this.cmdType.getByteValue());
        channelBuffer.writeByte(0);
        channelBuffer.writeByte(this.addressType.getByteValue());
        switch (this.addressType) {
            case IPv4:
                channelBuffer.writeBytes(NetUtil.createByteArrayFromIpAddressString(this.host));
                channelBuffer.writeShort(this.port);
                return;
            case DOMAIN:
                channelBuffer.writeByte(this.host.length());
                channelBuffer.writeBytes(this.host.getBytes("US-ASCII"));
                channelBuffer.writeShort(this.port);
                return;
            case IPv6:
                channelBuffer.writeBytes(NetUtil.createByteArrayFromIpAddressString(this.host));
                channelBuffer.writeShort(this.port);
                return;
            default:
                return;
        }
    }
}
