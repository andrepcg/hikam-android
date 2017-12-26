package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;

public abstract class SocksMessage {
    private final MessageType messageType;
    private final ProtocolVersion protocolVersion = ProtocolVersion.SOCKS5;

    public enum AddressType {
        IPv4((byte) 1),
        DOMAIN((byte) 3),
        IPv6((byte) 4),
        UNKNOWN((byte) -1);
        
        private final byte f505b;

        private AddressType(byte b) {
            this.f505b = b;
        }

        public static AddressType fromByte(byte b) {
            for (AddressType code : values()) {
                if (code.f505b == b) {
                    return code;
                }
            }
            return UNKNOWN;
        }

        public byte getByteValue() {
            return this.f505b;
        }
    }

    public enum AuthScheme {
        NO_AUTH((byte) 0),
        AUTH_GSSAPI((byte) 1),
        AUTH_PASSWORD((byte) 2),
        UNKNOWN((byte) -1);
        
        private final byte f506b;

        private AuthScheme(byte b) {
            this.f506b = b;
        }

        public static AuthScheme fromByte(byte b) {
            for (AuthScheme code : values()) {
                if (code.f506b == b) {
                    return code;
                }
            }
            return UNKNOWN;
        }

        public byte getByteValue() {
            return this.f506b;
        }
    }

    public enum AuthStatus {
        SUCCESS((byte) 0),
        FAILURE((byte) -1);
        
        private final byte f507b;

        private AuthStatus(byte b) {
            this.f507b = b;
        }

        public static AuthStatus fromByte(byte b) {
            for (AuthStatus code : values()) {
                if (code.f507b == b) {
                    return code;
                }
            }
            return FAILURE;
        }

        public byte getByteValue() {
            return this.f507b;
        }
    }

    public enum CmdStatus {
        SUCCESS((byte) 0),
        FAILURE((byte) 1),
        FORBIDDEN((byte) 2),
        NETWORK_UNREACHABLE((byte) 3),
        HOST_UNREACHABLE((byte) 4),
        REFUSED((byte) 5),
        TTL_EXPIRED((byte) 6),
        COMMAND_NOT_SUPPORTED((byte) 7),
        ADDRESS_NOT_SUPPORTED((byte) 8),
        UNASSIGNED((byte) -1);
        
        private final byte f508b;

        private CmdStatus(byte b) {
            this.f508b = b;
        }

        public static CmdStatus fromByte(byte b) {
            for (CmdStatus code : values()) {
                if (code.f508b == b) {
                    return code;
                }
            }
            return UNASSIGNED;
        }

        public byte getByteValue() {
            return this.f508b;
        }
    }

    public enum CmdType {
        CONNECT((byte) 1),
        BIND((byte) 2),
        UDP((byte) 3),
        UNKNOWN((byte) -1);
        
        private final byte f509b;

        private CmdType(byte b) {
            this.f509b = b;
        }

        public static CmdType fromByte(byte b) {
            for (CmdType code : values()) {
                if (code.f509b == b) {
                    return code;
                }
            }
            return UNKNOWN;
        }

        public byte getByteValue() {
            return this.f509b;
        }
    }

    public enum MessageType {
        REQUEST,
        RESPONSE,
        UNKNOWN
    }

    public enum ProtocolVersion {
        SOCKS4a((byte) 4),
        SOCKS5((byte) 5),
        UNKNOWN((byte) -1);
        
        private final byte f510b;

        private ProtocolVersion(byte b) {
            this.f510b = b;
        }

        public static ProtocolVersion fromByte(byte b) {
            for (ProtocolVersion code : values()) {
                if (code.f510b == b) {
                    return code;
                }
            }
            return UNKNOWN;
        }

        public byte getByteValue() {
            return this.f510b;
        }
    }

    public enum SubnegotiationVersion {
        AUTH_PASSWORD((byte) 1),
        UNKNOWN((byte) -1);
        
        private final byte f511b;

        private SubnegotiationVersion(byte b) {
            this.f511b = b;
        }

        public static SubnegotiationVersion fromByte(byte b) {
            for (SubnegotiationVersion code : values()) {
                if (code.f511b == b) {
                    return code;
                }
            }
            return UNKNOWN;
        }

        public byte getByteValue() {
            return this.f511b;
        }
    }

    public abstract void encodeAsByteBuf(ChannelBuffer channelBuffer) throws Exception;

    protected SocksMessage(MessageType messageType) {
        if (messageType == null) {
            throw new NullPointerException("messageType");
        }
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }
}
