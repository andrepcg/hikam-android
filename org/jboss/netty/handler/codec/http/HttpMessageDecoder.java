package org.jboss.netty.handler.codec.http;

import java.util.List;
import org.apache.http.HttpStatus;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public abstract class HttpMessageDecoder extends ReplayingDecoder<State> {
    static final /* synthetic */ boolean $assertionsDisabled = (!HttpMessageDecoder.class.desiredAssertionStatus());
    private long chunkSize;
    private ChannelBuffer content;
    private int contentRead;
    private int headerSize;
    private final int maxChunkSize;
    private final int maxHeaderSize;
    private final int maxInitialLineLength;
    private HttpMessage message;

    protected enum State {
        SKIP_CONTROL_CHARS,
        READ_INITIAL,
        READ_HEADER,
        READ_VARIABLE_LENGTH_CONTENT,
        READ_VARIABLE_LENGTH_CONTENT_AS_CHUNKS,
        READ_FIXED_LENGTH_CONTENT,
        READ_FIXED_LENGTH_CONTENT_AS_CHUNKS,
        READ_CHUNK_SIZE,
        READ_CHUNKED_CONTENT,
        READ_CHUNKED_CONTENT_AS_CHUNKS,
        READ_CHUNK_DELIMITER,
        READ_CHUNK_FOOTER,
        UPGRADED
    }

    protected abstract HttpMessage createMessage(String[] strArr) throws Exception;

    protected abstract boolean isDecodingRequest();

    protected HttpMessageDecoder() {
        this(4096, 8192, 8192);
    }

    protected HttpMessageDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        super(State.SKIP_CONTROL_CHARS, true);
        if (maxInitialLineLength <= 0) {
            throw new IllegalArgumentException("maxInitialLineLength must be a positive integer: " + maxInitialLineLength);
        } else if (maxHeaderSize <= 0) {
            throw new IllegalArgumentException("maxHeaderSize must be a positive integer: " + maxHeaderSize);
        } else if (maxChunkSize < 0) {
            throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + maxChunkSize);
        } else {
            this.maxInitialLineLength = maxInitialLineLength;
            this.maxHeaderSize = maxHeaderSize;
            this.maxChunkSize = maxChunkSize;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected java.lang.Object decode(org.jboss.netty.channel.ChannelHandlerContext r23, org.jboss.netty.channel.Channel r24, org.jboss.netty.buffer.ChannelBuffer r25, org.jboss.netty.handler.codec.http.HttpMessageDecoder.State r26) throws java.lang.Exception {
        /*
        r22 = this;
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.C08391.f491x5907aa57;
        r18 = r26.ordinal();
        r17 = r17[r18];
        switch(r17) {
            case 1: goto L_0x0213;
            case 2: goto L_0x0176;
            case 3: goto L_0x0013;
            case 4: goto L_0x0022;
            case 5: goto L_0x0064;
            case 6: goto L_0x01cf;
            case 7: goto L_0x021d;
            case 8: goto L_0x0286;
            case 9: goto L_0x02c2;
            case 10: goto L_0x0308;
            case 11: goto L_0x036d;
            case 12: goto L_0x03a3;
            case 13: goto L_0x03c0;
            default: goto L_0x000b;
        };
    L_0x000b:
        r17 = new java.lang.Error;
        r18 = "Shouldn't reach here.";
        r17.<init>(r18);
        throw r17;
    L_0x0013:
        skipControlCharacters(r25);	 Catch:{ all -> 0x004a }
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_INITIAL;	 Catch:{ all -> 0x004a }
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);	 Catch:{ all -> 0x004a }
        r22.checkpoint();
    L_0x0022:
        r0 = r22;
        r0 = r0.maxInitialLineLength;
        r17 = r0;
        r0 = r25;
        r1 = r17;
        r17 = readLine(r0, r1);
        r5 = splitInitialLine(r17);
        r0 = r5.length;
        r17 = r0;
        r18 = 3;
        r0 = r17;
        r1 = r18;
        if (r0 >= r1) goto L_0x004f;
    L_0x003f:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.SKIP_CONTROL_CHARS;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
        r4 = 0;
    L_0x0049:
        return r4;
    L_0x004a:
        r17 = move-exception;
        r22.checkpoint();
        throw r17;
    L_0x004f:
        r0 = r22;
        r17 = r0.createMessage(r5);
        r0 = r17;
        r1 = r22;
        r1.message = r0;
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_HEADER;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
    L_0x0064:
        r0 = r22;
        r1 = r25;
        r12 = r0.readHeaders(r1);
        r0 = r22;
        r0.checkpoint(r12);
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_CHUNK_SIZE;
        r0 = r17;
        if (r12 != r0) goto L_0x0087;
    L_0x0077:
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r18 = 1;
        r17.setChunked(r18);
        r0 = r22;
        r4 = r0.message;
        goto L_0x0049;
    L_0x0087:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.SKIP_CONTROL_CHARS;
        r0 = r17;
        if (r12 != r0) goto L_0x00a4;
    L_0x008d:
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r17 = r17.headers();
        r18 = "Transfer-Encoding";
        r17.remove(r18);
        r22.resetState();
        r0 = r22;
        r4 = r0.message;
        goto L_0x0049;
    L_0x00a4:
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r18 = -1;
        r8 = org.jboss.netty.handler.codec.http.HttpHeaders.getContentLength(r17, r18);
        r18 = 0;
        r17 = (r8 > r18 ? 1 : (r8 == r18 ? 0 : -1));
        if (r17 == 0) goto L_0x00c2;
    L_0x00b6:
        r18 = -1;
        r17 = (r8 > r18 ? 1 : (r8 == r18 ? 0 : -1));
        if (r17 != 0) goto L_0x00d0;
    L_0x00bc:
        r17 = r22.isDecodingRequest();
        if (r17 == 0) goto L_0x00d0;
    L_0x00c2:
        r17 = org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;
        r0 = r17;
        r1 = r22;
        r1.content = r0;
        r4 = r22.reset();
        goto L_0x0049;
    L_0x00d0:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.C08391.f491x5907aa57;
        r18 = r12.ordinal();
        r17 = r17[r18];
        switch(r17) {
            case 1: goto L_0x00f6;
            case 2: goto L_0x013d;
            default: goto L_0x00db;
        };
    L_0x00db:
        r17 = new java.lang.IllegalStateException;
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = "Unexpected state: ";
        r18 = r18.append(r19);
        r0 = r18;
        r18 = r0.append(r12);
        r18 = r18.toString();
        r17.<init>(r18);
        throw r17;
    L_0x00f6:
        r0 = r22;
        r0 = r0.maxChunkSize;
        r17 = r0;
        r0 = r17;
        r0 = (long) r0;
        r18 = r0;
        r17 = (r8 > r18 ? 1 : (r8 == r18 ? 0 : -1));
        if (r17 > 0) goto L_0x0111;
    L_0x0105:
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r17 = org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected(r17);
        if (r17 == 0) goto L_0x0173;
    L_0x0111:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_FIXED_LENGTH_CONTENT_AS_CHUNKS;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r18 = 1;
        r17.setChunked(r18);
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r18 = -1;
        r18 = org.jboss.netty.handler.codec.http.HttpHeaders.getContentLength(r17, r18);
        r0 = r18;
        r2 = r22;
        r2.chunkSize = r0;
        r0 = r22;
        r4 = r0.message;
        goto L_0x0049;
    L_0x013d:
        r17 = r25.readableBytes();
        r0 = r22;
        r0 = r0.maxChunkSize;
        r18 = r0;
        r0 = r17;
        r1 = r18;
        if (r0 > r1) goto L_0x0159;
    L_0x014d:
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r17 = org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected(r17);
        if (r17 == 0) goto L_0x0173;
    L_0x0159:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_VARIABLE_LENGTH_CONTENT_AS_CHUNKS;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r18 = 1;
        r17.setChunked(r18);
        r0 = r22;
        r4 = r0.message;
        goto L_0x0049;
    L_0x0173:
        r4 = 0;
        goto L_0x0049;
    L_0x0176:
        r15 = r22.actualReadableBytes();
        r0 = r22;
        r0 = r0.maxChunkSize;
        r17 = r0;
        r0 = r17;
        if (r15 <= r0) goto L_0x0188;
    L_0x0184:
        r0 = r22;
        r15 = r0.maxChunkSize;
    L_0x0188:
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r17 = r17.isChunked();
        if (r17 != 0) goto L_0x01c0;
    L_0x0194:
        r0 = r22;
        r0 = r0.message;
        r17 = r0;
        r18 = 1;
        r17.setChunked(r18);
        r17 = 2;
        r0 = r17;
        r4 = new java.lang.Object[r0];
        r17 = 0;
        r0 = r22;
        r0 = r0.message;
        r18 = r0;
        r4[r17] = r18;
        r17 = 1;
        r18 = new org.jboss.netty.handler.codec.http.DefaultHttpChunk;
        r0 = r25;
        r19 = r0.readBytes(r15);
        r18.<init>(r19);
        r4[r17] = r18;
        goto L_0x0049;
    L_0x01c0:
        r4 = new org.jboss.netty.handler.codec.http.DefaultHttpChunk;
        r0 = r25;
        r17 = r0.readBytes(r15);
        r0 = r17;
        r4.<init>(r0);
        goto L_0x0049;
    L_0x01cf:
        r15 = r22.actualReadableBytes();
        r0 = r22;
        r0 = r0.maxChunkSize;
        r17 = r0;
        r0 = r17;
        if (r15 <= r0) goto L_0x01e1;
    L_0x01dd:
        r0 = r22;
        r15 = r0.maxChunkSize;
    L_0x01e1:
        r4 = new org.jboss.netty.handler.codec.http.DefaultHttpChunk;
        r0 = r25;
        r17 = r0.readBytes(r15);
        r0 = r17;
        r4.<init>(r0);
        r17 = r25.readable();
        if (r17 != 0) goto L_0x0049;
    L_0x01f4:
        r22.reset();
        r17 = r4.isLast();
        if (r17 != 0) goto L_0x0049;
    L_0x01fd:
        r17 = 2;
        r0 = r17;
        r0 = new java.lang.Object[r0];
        r16 = r0;
        r17 = 0;
        r16[r17] = r4;
        r17 = 1;
        r18 = org.jboss.netty.handler.codec.http.HttpChunk.LAST_CHUNK;
        r16[r17] = r18;
        r4 = r16;
        goto L_0x0049;
    L_0x0213:
        r0 = r22;
        r1 = r25;
        r4 = r0.readFixedLengthContent(r1);
        goto L_0x0049;
    L_0x021d:
        r0 = r22;
        r6 = r0.chunkSize;
        r13 = r22.actualReadableBytes();
        if (r13 != 0) goto L_0x022a;
    L_0x0227:
        r4 = 0;
        goto L_0x0049;
    L_0x022a:
        r15 = r13;
        r0 = r22;
        r0 = r0.maxChunkSize;
        r17 = r0;
        r0 = r17;
        if (r15 <= r0) goto L_0x0239;
    L_0x0235:
        r0 = r22;
        r15 = r0.maxChunkSize;
    L_0x0239:
        r0 = (long) r15;
        r18 = r0;
        r17 = (r18 > r6 ? 1 : (r18 == r6 ? 0 : -1));
        if (r17 <= 0) goto L_0x0241;
    L_0x0240:
        r15 = (int) r6;
    L_0x0241:
        r4 = new org.jboss.netty.handler.codec.http.DefaultHttpChunk;
        r0 = r25;
        r17 = r0.readBytes(r15);
        r0 = r17;
        r4.<init>(r0);
        r0 = (long) r15;
        r18 = r0;
        r17 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r17 <= 0) goto L_0x0283;
    L_0x0255:
        r0 = (long) r15;
        r18 = r0;
        r6 = r6 - r18;
    L_0x025a:
        r0 = r22;
        r0.chunkSize = r6;
        r18 = 0;
        r17 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1));
        if (r17 != 0) goto L_0x0049;
    L_0x0264:
        r22.reset();
        r17 = r4.isLast();
        if (r17 != 0) goto L_0x0049;
    L_0x026d:
        r17 = 2;
        r0 = r17;
        r0 = new java.lang.Object[r0];
        r16 = r0;
        r17 = 0;
        r16[r17] = r4;
        r17 = 1;
        r18 = org.jboss.netty.handler.codec.http.HttpChunk.LAST_CHUNK;
        r16[r17] = r18;
        r4 = r16;
        goto L_0x0049;
    L_0x0283:
        r6 = 0;
        goto L_0x025a;
    L_0x0286:
        r0 = r22;
        r0 = r0.maxInitialLineLength;
        r17 = r0;
        r0 = r25;
        r1 = r17;
        r10 = readLine(r0, r1);
        r6 = getChunkSize(r10);
        r0 = (long) r6;
        r18 = r0;
        r0 = r18;
        r2 = r22;
        r2.chunkSize = r0;
        if (r6 != 0) goto L_0x02af;
    L_0x02a3:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_CHUNK_FOOTER;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
        r4 = 0;
        goto L_0x0049;
    L_0x02af:
        r0 = r22;
        r0 = r0.maxChunkSize;
        r17 = r0;
        r0 = r17;
        if (r6 <= r0) goto L_0x02d9;
    L_0x02b9:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_CHUNKED_CONTENT_AS_CHUNKS;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
    L_0x02c2:
        r17 = $assertionsDisabled;
        if (r17 != 0) goto L_0x02e3;
    L_0x02c6:
        r0 = r22;
        r0 = r0.chunkSize;
        r18 = r0;
        r20 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r17 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r17 <= 0) goto L_0x02e3;
    L_0x02d3:
        r17 = new java.lang.AssertionError;
        r17.<init>();
        throw r17;
    L_0x02d9:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_CHUNKED_CONTENT;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
        goto L_0x02c2;
    L_0x02e3:
        r4 = new org.jboss.netty.handler.codec.http.DefaultHttpChunk;
        r0 = r22;
        r0 = r0.chunkSize;
        r18 = r0;
        r0 = r18;
        r0 = (int) r0;
        r17 = r0;
        r0 = r25;
        r1 = r17;
        r17 = r0.readBytes(r1);
        r0 = r17;
        r4.<init>(r0);
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_CHUNK_DELIMITER;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
        goto L_0x0049;
    L_0x0308:
        r17 = $assertionsDisabled;
        if (r17 != 0) goto L_0x031f;
    L_0x030c:
        r0 = r22;
        r0 = r0.chunkSize;
        r18 = r0;
        r20 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r17 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r17 <= 0) goto L_0x031f;
    L_0x0319:
        r17 = new java.lang.AssertionError;
        r17.<init>();
        throw r17;
    L_0x031f:
        r0 = r22;
        r0 = r0.chunkSize;
        r18 = r0;
        r0 = r18;
        r6 = (int) r0;
        r13 = r22.actualReadableBytes();
        if (r13 != 0) goto L_0x0331;
    L_0x032e:
        r4 = 0;
        goto L_0x0049;
    L_0x0331:
        r15 = r6;
        r0 = r22;
        r0 = r0.maxChunkSize;
        r17 = r0;
        r0 = r17;
        if (r15 <= r0) goto L_0x0340;
    L_0x033c:
        r0 = r22;
        r15 = r0.maxChunkSize;
    L_0x0340:
        if (r15 <= r13) goto L_0x0343;
    L_0x0342:
        r15 = r13;
    L_0x0343:
        r4 = new org.jboss.netty.handler.codec.http.DefaultHttpChunk;
        r0 = r25;
        r17 = r0.readBytes(r15);
        r0 = r17;
        r4.<init>(r0);
        if (r6 <= r15) goto L_0x038f;
    L_0x0352:
        r6 = r6 - r15;
    L_0x0353:
        r0 = (long) r6;
        r18 = r0;
        r0 = r18;
        r2 = r22;
        r2.chunkSize = r0;
        if (r6 != 0) goto L_0x0367;
    L_0x035e:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_CHUNK_DELIMITER;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
    L_0x0367:
        r17 = r4.isLast();
        if (r17 == 0) goto L_0x0049;
    L_0x036d:
        r11 = r25.readByte();
        r17 = 13;
        r0 = r17;
        if (r11 != r0) goto L_0x0391;
    L_0x0377:
        r17 = r25.readByte();
        r18 = 10;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x036d;
    L_0x0383:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_CHUNK_SIZE;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
        r4 = 0;
        goto L_0x0049;
    L_0x038f:
        r6 = 0;
        goto L_0x0353;
    L_0x0391:
        r17 = 10;
        r0 = r17;
        if (r11 != r0) goto L_0x036d;
    L_0x0397:
        r17 = org.jboss.netty.handler.codec.http.HttpMessageDecoder.State.READ_CHUNK_SIZE;
        r0 = r22;
        r1 = r17;
        r0.checkpoint(r1);
        r4 = 0;
        goto L_0x0049;
    L_0x03a3:
        r0 = r22;
        r1 = r25;
        r16 = r0.readTrailingHeaders(r1);
        r0 = r22;
        r0 = r0.maxChunkSize;
        r17 = r0;
        if (r17 != 0) goto L_0x03b9;
    L_0x03b3:
        r4 = r22.reset();
        goto L_0x0049;
    L_0x03b9:
        r22.reset();
        r4 = r16;
        goto L_0x0049;
    L_0x03c0:
        r14 = r22.actualReadableBytes();
        if (r14 <= 0) goto L_0x03d4;
    L_0x03c6:
        r17 = r22.actualReadableBytes();
        r0 = r25;
        r1 = r17;
        r4 = r0.readBytes(r1);
        goto L_0x0049;
    L_0x03d4:
        r4 = 0;
        goto L_0x0049;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.codec.http.HttpMessageDecoder.decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer, org.jboss.netty.handler.codec.http.HttpMessageDecoder$State):java.lang.Object");
    }

    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        if (!(msg instanceof HttpResponse)) {
            return false;
        }
        HttpResponse res = (HttpResponse) msg;
        int code = res.getStatus().getCode();
        if (code < 100 || code >= 200) {
            switch (code) {
                case HttpStatus.SC_NO_CONTENT /*204*/:
                case HttpStatus.SC_RESET_CONTENT /*205*/:
                case HttpStatus.SC_NOT_MODIFIED /*304*/:
                    return true;
                default:
                    return false;
            }
        } else if (code != 101 || res.headers().contains(Names.SEC_WEBSOCKET_ACCEPT)) {
            return true;
        } else {
            return false;
        }
    }

    private Object reset() {
        HttpMessage message = this.message;
        ChannelBuffer content = this.content;
        if (content != null) {
            message.setContent(content);
            this.content = null;
        }
        resetState();
        this.message = null;
        return message;
    }

    private void resetState() {
        if (!isDecodingRequest()) {
            HttpResponse res = this.message;
            if (res != null && res.getStatus().getCode() == 101) {
                checkpoint(State.UPGRADED);
                return;
            }
        }
        checkpoint(State.SKIP_CONTROL_CHARS);
    }

    private static void skipControlCharacters(ChannelBuffer buffer) {
        while (true) {
            char c = (char) buffer.readUnsignedByte();
            if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
                buffer.readerIndex(buffer.readerIndex() - 1);
                return;
            }
        }
    }

    private Object readFixedLengthContent(ChannelBuffer buffer) {
        long length = HttpHeaders.getContentLength(this.message, -1);
        if ($assertionsDisabled || length <= 2147483647L) {
            int toRead = ((int) length) - this.contentRead;
            if (toRead > actualReadableBytes()) {
                toRead = actualReadableBytes();
            }
            this.contentRead += toRead;
            if (length >= ((long) this.contentRead)) {
                if (this.content == null) {
                    this.content = buffer.readBytes((int) length);
                } else {
                    this.content.writeBytes(buffer, (int) length);
                }
                return reset();
            } else if (this.message.isChunked()) {
                return new DefaultHttpChunk(buffer.readBytes(toRead));
            } else {
                this.message.setChunked(true);
                return new Object[]{this.message, new DefaultHttpChunk(buffer.readBytes(toRead))};
            }
        }
        throw new AssertionError();
    }

    private State readHeaders(ChannelBuffer buffer) throws TooLongFrameException {
        this.headerSize = 0;
        HttpMessage message = this.message;
        String line = readHeader(buffer);
        String name = null;
        Object value = null;
        if (line.length() != 0) {
            message.headers().clear();
            do {
                char firstChar = line.charAt(0);
                if (name == null || !(firstChar == ' ' || firstChar == '\t')) {
                    if (name != null) {
                        message.headers().add(name, value);
                    }
                    String[] header = splitHeader(line);
                    name = header[0];
                    value = header[1];
                } else {
                    value = value + ' ' + line.trim();
                }
                line = readHeader(buffer);
            } while (line.length() != 0);
            if (name != null) {
                message.headers().add(name, value);
            }
        }
        if (isContentAlwaysEmpty(message)) {
            return State.SKIP_CONTROL_CHARS;
        }
        if (message.isChunked()) {
            return State.READ_CHUNK_SIZE;
        }
        if (HttpHeaders.getContentLength(message, -1) >= 0) {
            return State.READ_FIXED_LENGTH_CONTENT;
        }
        return State.READ_VARIABLE_LENGTH_CONTENT;
    }

    private HttpChunkTrailer readTrailingHeaders(ChannelBuffer buffer) throws TooLongFrameException {
        this.headerSize = 0;
        String line = readHeader(buffer);
        String lastHeader = null;
        if (line.length() == 0) {
            return HttpChunk.LAST_CHUNK;
        }
        HttpChunkTrailer trailer = new DefaultHttpChunkTrailer();
        do {
            char firstChar = line.charAt(0);
            if (lastHeader == null || !(firstChar == ' ' || firstChar == '\t')) {
                String[] header = splitHeader(line);
                String name = header[0];
                if (!(name.equalsIgnoreCase("Content-Length") || name.equalsIgnoreCase("Transfer-Encoding") || name.equalsIgnoreCase(Names.TRAILER))) {
                    trailer.trailingHeaders().add(name, header[1]);
                }
                lastHeader = name;
            } else {
                List<String> current = trailer.trailingHeaders().getAll(lastHeader);
                if (!current.isEmpty()) {
                    int lastPos = current.size() - 1;
                    current.set(lastPos, ((String) current.get(lastPos)) + line.trim());
                }
            }
            line = readHeader(buffer);
        } while (line.length() != 0);
        return trailer;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String readHeader(org.jboss.netty.buffer.ChannelBuffer r7) throws org.jboss.netty.handler.codec.frame.TooLongFrameException {
        /*
        r6 = this;
        r2 = new java.lang.StringBuilder;
        r3 = 64;
        r2.<init>(r3);
        r0 = r6.headerSize;
    L_0x0009:
        r3 = r7.readByte();
        r1 = (char) r3;
        r0 = r0 + 1;
        switch(r1) {
            case 10: goto L_0x0043;
            case 11: goto L_0x0013;
            case 12: goto L_0x0013;
            case 13: goto L_0x0038;
            default: goto L_0x0013;
        };
    L_0x0013:
        r3 = r6.maxHeaderSize;
        if (r0 < r3) goto L_0x004a;
    L_0x0017:
        r3 = new org.jboss.netty.handler.codec.frame.TooLongFrameException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "HTTP header is larger than ";
        r4 = r4.append(r5);
        r5 = r6.maxHeaderSize;
        r4 = r4.append(r5);
        r5 = " bytes.";
        r4 = r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x0038:
        r3 = r7.readByte();
        r1 = (char) r3;
        r0 = r0 + 1;
        r3 = 10;
        if (r1 != r3) goto L_0x0013;
    L_0x0043:
        r6.headerSize = r0;
        r3 = r2.toString();
        return r3;
    L_0x004a:
        r2.append(r1);
        goto L_0x0009;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.handler.codec.http.HttpMessageDecoder.readHeader(org.jboss.netty.buffer.ChannelBuffer):java.lang.String");
    }

    private static int getChunkSize(String hex) {
        hex = hex.trim();
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            if (c == ';' || Character.isWhitespace(c) || Character.isISOControl(c)) {
                hex = hex.substring(0, i);
                break;
            }
        }
        return Integer.parseInt(hex, 16);
    }

    private static String readLine(ChannelBuffer buffer, int maxLineLength) throws TooLongFrameException {
        StringBuilder sb = new StringBuilder(64);
        int lineLength = 0;
        while (true) {
            byte nextByte = buffer.readByte();
            if (nextByte == HttpConstants.CR) {
                if (buffer.readByte() == (byte) 10) {
                    return sb.toString();
                }
            } else if (nextByte == (byte) 10) {
                return sb.toString();
            } else {
                if (lineLength >= maxLineLength) {
                    throw new TooLongFrameException("An HTTP line is larger than " + maxLineLength + " bytes.");
                }
                lineLength++;
                sb.append((char) nextByte);
            }
        }
    }

    private static String[] splitInitialLine(String sb) {
        int aStart = findNonWhitespace(sb, 0);
        int aEnd = findWhitespace(sb, aStart);
        int bStart = findNonWhitespace(sb, aEnd);
        int bEnd = findWhitespace(sb, bStart);
        int cStart = findNonWhitespace(sb, bEnd);
        int cEnd = findEndOfString(sb);
        String[] strArr = new String[3];
        strArr[0] = sb.substring(aStart, aEnd);
        strArr[1] = sb.substring(bStart, bEnd);
        strArr[2] = cStart < cEnd ? sb.substring(cStart, cEnd) : "";
        return strArr;
    }

    private static String[] splitHeader(String sb) {
        int length = sb.length();
        int nameEnd = findNonWhitespace(sb, 0);
        while (nameEnd < length) {
            char ch = sb.charAt(nameEnd);
            if (ch == ':' || Character.isWhitespace(ch)) {
                break;
            }
            nameEnd++;
        }
        int colonEnd = nameEnd;
        while (colonEnd < length) {
            if (sb.charAt(colonEnd) == ':') {
                colonEnd++;
                break;
            }
            colonEnd++;
        }
        if (findNonWhitespace(sb, colonEnd) == length) {
            return new String[]{sb.substring(nameStart, nameEnd), ""};
        }
        int valueEnd = findEndOfString(sb);
        return new String[]{sb.substring(nameStart, nameEnd), sb.substring(valueStart, valueEnd)};
    }

    private static int findNonWhitespace(String sb, int offset) {
        int result = offset;
        while (result < sb.length() && Character.isWhitespace(sb.charAt(result))) {
            result++;
        }
        return result;
    }

    private static int findWhitespace(String sb, int offset) {
        int result = offset;
        while (result < sb.length() && !Character.isWhitespace(sb.charAt(result))) {
            result++;
        }
        return result;
    }

    private static int findEndOfString(String sb) {
        int result = sb.length();
        while (result > 0 && Character.isWhitespace(sb.charAt(result - 1))) {
            result--;
        }
        return result;
    }
}
