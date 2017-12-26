package org.jboss.netty.handler.codec.http;

import org.apache.http.protocol.HTTP;
import org.jboss.netty.buffer.ChannelBuffer;

public class HttpRequestEncoder extends HttpMessageEncoder {
    private static final char QUESTION_MARK = '?';
    private static final char SLASH = '/';

    protected void encodeInitialLine(ChannelBuffer buf, HttpMessage message) throws Exception {
        HttpRequest request = (HttpRequest) message;
        buf.writeBytes(request.getMethod().toString().getBytes(HTTP.ASCII));
        buf.writeByte(32);
        String uri = request.getUri();
        int start = uri.indexOf("://");
        if (start != -1) {
            int startIndex = start + 3;
            int index = uri.indexOf(63, startIndex);
            if (index == -1) {
                if (uri.lastIndexOf(47) <= startIndex) {
                    uri = uri + SLASH;
                }
            } else if (uri.lastIndexOf(47, index) <= startIndex) {
                int len = uri.length();
                StringBuilder sb = new StringBuilder(len + 1);
                sb.append(uri, 0, index);
                sb.append(SLASH);
                sb.append(uri, index, len);
                uri = sb.toString();
            }
        }
        buf.writeBytes(uri.getBytes("UTF-8"));
        buf.writeByte(32);
        buf.writeBytes(request.getProtocolVersion().toString().getBytes(HTTP.ASCII));
        buf.writeByte(13);
        buf.writeByte(10);
    }
}
