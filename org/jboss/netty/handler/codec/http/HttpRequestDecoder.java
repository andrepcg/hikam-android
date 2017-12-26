package org.jboss.netty.handler.codec.http;

public class HttpRequestDecoder extends HttpMessageDecoder {
    public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
    }

    protected HttpMessage createMessage(String[] initialLine) throws Exception {
        return new DefaultHttpRequest(HttpVersion.valueOf(initialLine[2]), HttpMethod.valueOf(initialLine[0]), initialLine[1]);
    }

    protected boolean isDecodingRequest() {
        return true;
    }
}
