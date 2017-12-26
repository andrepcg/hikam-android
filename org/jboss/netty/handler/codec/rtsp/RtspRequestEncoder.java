package org.jboss.netty.handler.codec.rtsp;

import org.apache.http.protocol.HTTP;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class RtspRequestEncoder extends RtspMessageEncoder {
    protected void encodeInitialLine(ChannelBuffer buf, HttpMessage message) throws Exception {
        HttpRequest request = (HttpRequest) message;
        buf.writeBytes(request.getMethod().toString().getBytes(HTTP.ASCII));
        buf.writeByte(32);
        buf.writeBytes(request.getUri().getBytes("UTF-8"));
        buf.writeByte(32);
        buf.writeBytes(request.getProtocolVersion().toString().getBytes(HTTP.ASCII));
        buf.writeByte(13);
        buf.writeByte(10);
    }
}
