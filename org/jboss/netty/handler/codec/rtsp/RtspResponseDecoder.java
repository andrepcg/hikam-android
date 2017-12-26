package org.jboss.netty.handler.codec.rtsp;

import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class RtspResponseDecoder extends RtspMessageDecoder {
    public RtspResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
        super(maxInitialLineLength, maxHeaderSize, maxContentLength);
    }

    protected HttpMessage createMessage(String[] initialLine) throws Exception {
        return new DefaultHttpResponse(RtspVersions.valueOf(initialLine[0]), new HttpResponseStatus(Integer.valueOf(initialLine[1]).intValue(), initialLine[2]));
    }

    protected boolean isDecodingRequest() {
        return false;
    }
}
