package org.jboss.netty.handler.codec.replay;

import java.net.SocketAddress;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public abstract class ReplayingDecoder<T extends Enum<T>> extends FrameDecoder {
    private int checkpoint;
    private boolean needsCleanup;
    private final ReplayingDecoderBuffer replayable;
    private T state;

    protected abstract Object decode(ChannelHandlerContext channelHandlerContext, Channel channel, ChannelBuffer channelBuffer, T t) throws Exception;

    protected ReplayingDecoder() {
        this(null);
    }

    protected ReplayingDecoder(boolean unfold) {
        this(null, unfold);
    }

    protected ReplayingDecoder(T initialState) {
        this(initialState, false);
    }

    protected ReplayingDecoder(T initialState, boolean unfold) {
        super(unfold);
        this.replayable = new ReplayingDecoderBuffer(this);
        this.state = initialState;
    }

    protected ChannelBuffer internalBuffer() {
        return super.internalBuffer();
    }

    protected void checkpoint() {
        ChannelBuffer cumulation = this.cumulation;
        if (cumulation != null) {
            this.checkpoint = cumulation.readerIndex();
        } else {
            this.checkpoint = -1;
        }
    }

    protected void checkpoint(T state) {
        checkpoint();
        setState(state);
    }

    protected T getState() {
        return this.state;
    }

    protected T setState(T newState) {
        T oldState = this.state;
        this.state = newState;
        return oldState;
    }

    protected Object decodeLast(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, T state) throws Exception {
        return decode(ctx, channel, buffer, state);
    }

    protected final Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        return decode(ctx, channel, buffer, this.state);
    }

    protected final Object decodeLast(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        return decodeLast(ctx, channel, buffer, this.state);
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        int bytesToPreserve;
        ChannelBuffer cumulation;
        ChannelBuffer m = e.getMessage();
        if (m instanceof ChannelBuffer) {
            ChannelBuffer input = m;
            if (input.readable()) {
                this.needsCleanup = true;
                if (this.cumulation == null) {
                    this.cumulation = input;
                    int oldReaderIndex = input.readerIndex();
                    int inputSize = input.readableBytes();
                    int readableBytes;
                    int inputCapacity;
                    boolean copy;
                    try {
                        callDecode(ctx, e.getChannel(), input, this.replayable, e.getRemoteAddress());
                        readableBytes = input.readableBytes();
                        if (readableBytes > 0) {
                            inputCapacity = input.capacity();
                            copy = readableBytes != inputCapacity && inputCapacity > getMaxCumulationBufferCapacity();
                            if (this.checkpoint > 0) {
                                bytesToPreserve = inputSize - (this.checkpoint - oldReaderIndex);
                                if (copy) {
                                    cumulation = newCumulationBuffer(ctx, bytesToPreserve);
                                    this.cumulation = cumulation;
                                    cumulation.writeBytes(input, this.checkpoint, bytesToPreserve);
                                    return;
                                }
                                this.cumulation = input.slice(this.checkpoint, bytesToPreserve);
                                return;
                            } else if (this.checkpoint == 0) {
                                if (copy) {
                                    cumulation = newCumulationBuffer(ctx, inputSize);
                                    this.cumulation = cumulation;
                                    cumulation.writeBytes(input, oldReaderIndex, inputSize);
                                    cumulation.readerIndex(input.readerIndex());
                                    return;
                                }
                                cumulation = input.slice(oldReaderIndex, inputSize);
                                this.cumulation = cumulation;
                                cumulation.readerIndex(input.readerIndex());
                                return;
                            } else if (copy) {
                                cumulation = newCumulationBuffer(ctx, input.readableBytes());
                                this.cumulation = cumulation;
                                cumulation.writeBytes(input);
                                return;
                            } else {
                                this.cumulation = input;
                                return;
                            }
                        }
                        this.cumulation = null;
                        return;
                    } catch (Throwable th) {
                        readableBytes = input.readableBytes();
                        if (readableBytes > 0) {
                            inputCapacity = input.capacity();
                            copy = readableBytes != inputCapacity && inputCapacity > getMaxCumulationBufferCapacity();
                            if (this.checkpoint > 0) {
                                bytesToPreserve = inputSize - (this.checkpoint - oldReaderIndex);
                                if (copy) {
                                    cumulation = newCumulationBuffer(ctx, bytesToPreserve);
                                    this.cumulation = cumulation;
                                    cumulation.writeBytes(input, this.checkpoint, bytesToPreserve);
                                } else {
                                    this.cumulation = input.slice(this.checkpoint, bytesToPreserve);
                                }
                            } else if (this.checkpoint == 0) {
                                if (copy) {
                                    cumulation = newCumulationBuffer(ctx, inputSize);
                                    this.cumulation = cumulation;
                                    cumulation.writeBytes(input, oldReaderIndex, inputSize);
                                    cumulation.readerIndex(input.readerIndex());
                                } else {
                                    cumulation = input.slice(oldReaderIndex, inputSize);
                                    this.cumulation = cumulation;
                                    cumulation.readerIndex(input.readerIndex());
                                }
                            } else if (copy) {
                                cumulation = newCumulationBuffer(ctx, input.readableBytes());
                                this.cumulation = cumulation;
                                cumulation.writeBytes(input);
                            } else {
                                this.cumulation = input;
                            }
                        } else {
                            this.cumulation = null;
                        }
                    }
                } else {
                    input = appendToCumulation(input);
                    try {
                        callDecode(ctx, e.getChannel(), input, this.replayable, e.getRemoteAddress());
                        return;
                    } finally {
                        updateCumulation(ctx, input);
                    }
                }
            } else {
                return;
            }
        }
        ctx.sendUpstream(e);
    }

    private void callDecode(ChannelHandlerContext context, Channel channel, ChannelBuffer input, ChannelBuffer replayableInput, SocketAddress remoteAddress) throws Exception {
        while (input.readable()) {
            int oldReaderIndex = input.readerIndex();
            this.checkpoint = oldReaderIndex;
            Object obj = null;
            T oldState = this.state;
            try {
                obj = decode(context, channel, replayableInput, this.state);
                if (obj == null) {
                    if (oldReaderIndex == input.readerIndex() && oldState == this.state) {
                        throw new IllegalStateException("null cannot be returned if no data is consumed and state didn't change.");
                    }
                }
            } catch (ReplayError e) {
                int checkpoint = this.checkpoint;
                if (checkpoint >= 0) {
                    input.readerIndex(checkpoint);
                }
            }
            if (obj != null) {
                if (oldReaderIndex == input.readerIndex() && oldState == this.state) {
                    throw new IllegalStateException("decode() method must consume at least one byte if it returned a decoded message (caused by: " + getClass() + ')');
                }
                unfoldAndFireMessageReceived(context, remoteAddress, obj);
            } else {
                return;
            }
        }
    }

    protected void cleanup(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        try {
            ChannelBuffer cumulation = this.cumulation;
            if (this.needsCleanup) {
                this.needsCleanup = false;
                this.replayable.terminate();
                if (cumulation != null && cumulation.readable()) {
                    callDecode(ctx, e.getChannel(), cumulation, this.replayable, null);
                }
                Object partiallyDecoded = decodeLast(ctx, e.getChannel(), this.replayable, this.state);
                this.cumulation = null;
                if (partiallyDecoded != null) {
                    unfoldAndFireMessageReceived(ctx, null, partiallyDecoded);
                }
            }
        } catch (ReplayError e2) {
        } catch (Throwable th) {
            ctx.sendUpstream(e);
        }
        ctx.sendUpstream(e);
    }
}
