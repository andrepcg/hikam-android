package org.jboss.netty.handler.codec.http;

public class HttpResponseDecoder extends HttpMessageDecoder {
    public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
    }

    protected HttpMessage createMessage(String[] initialLine) {
        return new DefaultHttpResponse(HttpVersion.valueOf(initialLine[0]), new HttpResponseStatus(Integer.valueOf(initialLine[1]).intValue(), initialLine[2]));
    }

    protected boolean isDecodingRequest() {
        return false;
    }
}
