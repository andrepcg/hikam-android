package org.jboss.netty.handler.codec.rtsp;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMessage;

public class RtspRequestDecoder extends RtspMessageDecoder {
    public RtspRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
        super(maxInitialLineLength, maxHeaderSize, maxContentLength);
    }

    protected HttpMessage createMessage(String[] initialLine) throws Exception {
        return new DefaultHttpRequest(RtspVersions.valueOf(initialLine[2]), RtspMethods.valueOf(initialLine[0]), initialLine[1]);
    }

    protected boolean isDecodingRequest() {
        return true;
    }
}
