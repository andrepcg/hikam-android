package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class SpdyHeaderBlockRawDecoder extends SpdyHeaderBlockDecoder {
    private static final int LENGTH_FIELD_SIZE = 4;
    private ChannelBuffer cumulation;
    private int headerSize;
    private int length;
    private final int maxHeaderSize;
    private String name;
    private int numHeaders;
    private State state;

    private enum State {
        READ_NUM_HEADERS,
        READ_NAME_LENGTH,
        READ_NAME,
        SKIP_NAME,
        READ_VALUE_LENGTH,
        READ_VALUE,
        SKIP_VALUE,
        END_HEADER_BLOCK,
        ERROR
    }

    public SpdyHeaderBlockRawDecoder(SpdyVersion spdyVersion, int maxHeaderSize) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        this.maxHeaderSize = maxHeaderSize;
        this.state = State.READ_NUM_HEADERS;
    }

    private int readLengthField(ChannelBuffer buffer) {
        int length = SpdyCodecUtil.getSignedInt(buffer, buffer.readerIndex());
        buffer.skipBytes(4);
        return length;
    }

    void decode(ChannelBuffer headerBlock, SpdyHeadersFrame frame) throws Exception {
        if (headerBlock == null) {
            throw new NullPointerException("headerBlock");
        } else if (frame == null) {
            throw new NullPointerException("frame");
        } else if (this.cumulation == null) {
            decodeHeaderBlock(headerBlock, frame);
            if (headerBlock.readable()) {
                this.cumulation = ChannelBuffers.dynamicBuffer(headerBlock.readableBytes());
                this.cumulation.writeBytes(headerBlock);
            }
        } else {
            this.cumulation.writeBytes(headerBlock);
            decodeHeaderBlock(this.cumulation, frame);
            if (this.cumulation.readable()) {
                this.cumulation.discardReadBytes();
            } else {
                this.cumulation = null;
            }
        }
    }

    protected void decodeHeaderBlock(ChannelBuffer headerBlock, SpdyHeadersFrame frame) throws Exception {
        while (headerBlock.readable()) {
            int skipLength;
            int i;
            switch (this.state) {
                case READ_NUM_HEADERS:
                    if (headerBlock.readableBytes() >= 4) {
                        this.numHeaders = readLengthField(headerBlock);
                        if (this.numHeaders >= 0) {
                            if (this.numHeaders != 0) {
                                this.state = State.READ_NAME_LENGTH;
                                break;
                            } else {
                                this.state = State.END_HEADER_BLOCK;
                                break;
                            }
                        }
                        this.state = State.ERROR;
                        frame.setInvalid();
                        break;
                    }
                    return;
                case READ_NAME_LENGTH:
                    if (headerBlock.readableBytes() >= 4) {
                        this.length = readLengthField(headerBlock);
                        if (this.length > 0) {
                            if (this.length <= this.maxHeaderSize && this.headerSize <= this.maxHeaderSize - this.length) {
                                this.headerSize += this.length;
                                this.state = State.READ_NAME;
                                break;
                            }
                            this.headerSize = this.maxHeaderSize + 1;
                            this.state = State.SKIP_NAME;
                            frame.setTruncated();
                            break;
                        }
                        this.state = State.ERROR;
                        frame.setInvalid();
                        break;
                    }
                    return;
                    break;
                case READ_NAME:
                    if (headerBlock.readableBytes() >= this.length) {
                        byte[] nameBytes = new byte[this.length];
                        headerBlock.readBytes(nameBytes);
                        this.name = new String(nameBytes, "UTF-8");
                        if (!frame.headers().contains(this.name)) {
                            this.state = State.READ_VALUE_LENGTH;
                            break;
                        }
                        this.state = State.ERROR;
                        frame.setInvalid();
                        break;
                    }
                    return;
                case SKIP_NAME:
                    skipLength = Math.min(headerBlock.readableBytes(), this.length);
                    headerBlock.skipBytes(skipLength);
                    this.length -= skipLength;
                    if (this.length != 0) {
                        break;
                    }
                    this.state = State.READ_VALUE_LENGTH;
                    break;
                case READ_VALUE_LENGTH:
                    if (headerBlock.readableBytes() >= 4) {
                        this.length = readLengthField(headerBlock);
                        if (this.length >= 0) {
                            if (this.length != 0) {
                                if (this.length <= this.maxHeaderSize && this.headerSize <= this.maxHeaderSize - this.length) {
                                    this.headerSize += this.length;
                                    this.state = State.READ_VALUE;
                                    break;
                                }
                                this.headerSize = this.maxHeaderSize + 1;
                                this.name = null;
                                this.state = State.SKIP_VALUE;
                                frame.setTruncated();
                                break;
                            }
                            if (!frame.isTruncated()) {
                                frame.headers().add(this.name, (Object) "");
                            }
                            this.name = null;
                            i = this.numHeaders - 1;
                            this.numHeaders = i;
                            if (i != 0) {
                                this.state = State.READ_NAME_LENGTH;
                                break;
                            } else {
                                this.state = State.END_HEADER_BLOCK;
                                break;
                            }
                        }
                        this.state = State.ERROR;
                        frame.setInvalid();
                        break;
                    }
                    return;
                case READ_VALUE:
                    if (headerBlock.readableBytes() >= this.length) {
                        byte[] valueBytes = new byte[this.length];
                        headerBlock.readBytes(valueBytes);
                        int index = 0;
                        int offset = 0;
                        if (valueBytes[0] == (byte) 0) {
                            this.state = State.ERROR;
                            frame.setInvalid();
                            break;
                        }
                        while (index < this.length) {
                            while (index < valueBytes.length && valueBytes[index] != (byte) 0) {
                                index++;
                            }
                            if (index < valueBytes.length && (index + 1 == valueBytes.length || valueBytes[index + 1] == (byte) 0)) {
                                this.state = State.ERROR;
                                frame.setInvalid();
                                this.name = null;
                                if (this.state == State.ERROR) {
                                    break;
                                }
                                i = this.numHeaders - 1;
                                this.numHeaders = i;
                                if (i == 0) {
                                    this.state = State.READ_NAME_LENGTH;
                                    break;
                                } else {
                                    this.state = State.END_HEADER_BLOCK;
                                    break;
                                }
                            }
                            try {
                                frame.headers().add(this.name, new String(valueBytes, offset, index - offset, "UTF-8"));
                                index++;
                                offset = index;
                            } catch (IllegalArgumentException e) {
                                this.state = State.ERROR;
                                frame.setInvalid();
                            }
                        }
                        this.name = null;
                        if (this.state == State.ERROR) {
                            i = this.numHeaders - 1;
                            this.numHeaders = i;
                            if (i == 0) {
                                this.state = State.READ_NAME_LENGTH;
                            } else {
                                this.state = State.END_HEADER_BLOCK;
                            }
                        }
                    } else {
                        return;
                    }
                    break;
                case SKIP_VALUE:
                    skipLength = Math.min(headerBlock.readableBytes(), this.length);
                    headerBlock.skipBytes(skipLength);
                    this.length -= skipLength;
                    if (this.length != 0) {
                        break;
                    }
                    i = this.numHeaders - 1;
                    this.numHeaders = i;
                    if (i != 0) {
                        this.state = State.READ_NAME_LENGTH;
                        break;
                    } else {
                        this.state = State.END_HEADER_BLOCK;
                        break;
                    }
                case END_HEADER_BLOCK:
                    this.state = State.ERROR;
                    frame.setInvalid();
                    break;
                case ERROR:
                    headerBlock.skipBytes(headerBlock.readableBytes());
                    return;
                default:
                    throw new Error("Shouldn't reach here.");
            }
        }
    }

    void endHeaderBlock(SpdyHeadersFrame frame) throws Exception {
        if (this.state != State.END_HEADER_BLOCK) {
            frame.setInvalid();
        }
        this.cumulation = null;
        this.headerSize = 0;
        this.name = null;
        this.state = State.READ_NUM_HEADERS;
    }

    void end() {
        this.cumulation = null;
    }
}
