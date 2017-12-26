package org.jboss.netty.handler.codec.http.multipart;

import java.nio.charset.Charset;
import java.util.List;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpConstants;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.util.internal.StringUtil;

public class HttpPostRequestDecoder implements InterfaceHttpPostRequestDecoder {
    private final InterfaceHttpPostRequestDecoder decoder;

    public static class EndOfDataDecoderException extends Exception {
        private static final long serialVersionUID = 1336267941020800769L;
    }

    public static class ErrorDataDecoderException extends Exception {
        private static final long serialVersionUID = 5020247425493164465L;

        public ErrorDataDecoderException(String msg) {
            super(msg);
        }

        public ErrorDataDecoderException(Throwable cause) {
            super(cause);
        }

        public ErrorDataDecoderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    protected enum MultiPartStatus {
        NOTSTARTED,
        PREAMBLE,
        HEADERDELIMITER,
        DISPOSITION,
        FIELD,
        FILEUPLOAD,
        MIXEDPREAMBLE,
        MIXEDDELIMITER,
        MIXEDDISPOSITION,
        MIXEDFILEUPLOAD,
        MIXEDCLOSEDELIMITER,
        CLOSEDELIMITER,
        PREEPILOGUE,
        EPILOGUE
    }

    public static class NotEnoughDataDecoderException extends Exception {
        private static final long serialVersionUID = -7846841864603865638L;

        public NotEnoughDataDecoderException(String msg) {
            super(msg);
        }

        public NotEnoughDataDecoderException(Throwable cause) {
            super(cause);
        }

        public NotEnoughDataDecoderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public HttpPostRequestDecoder(HttpRequest request) throws ErrorDataDecoderException {
        this(new DefaultHttpDataFactory(16384), request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request) throws ErrorDataDecoderException {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) throws ErrorDataDecoderException {
        if (factory == null) {
            throw new NullPointerException("factory");
        } else if (request == null) {
            throw new NullPointerException("request");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else if (isMultipart(request)) {
            this.decoder = new HttpPostMultipartRequestDecoder(factory, request, charset);
        } else {
            this.decoder = new HttpPostStandardRequestDecoder(factory, request, charset);
        }
    }

    public static boolean isMultipart(HttpRequest request) throws ErrorDataDecoderException {
        if (!request.headers().contains("Content-Type") || getMultipartDataBoundary(request.headers().get("Content-Type")) == null) {
            return false;
        }
        return true;
    }

    protected static String[] getMultipartDataBoundary(String contentType) throws ErrorDataDecoderException {
        String[] headerContentType = splitHeaderContentType(contentType);
        if (!headerContentType[0].toLowerCase().startsWith(Values.MULTIPART_FORM_DATA)) {
            return null;
        }
        int mrank;
        int crank;
        if (headerContentType[1].toLowerCase().startsWith(Values.BOUNDARY.toString())) {
            mrank = 1;
            crank = 2;
        } else if (!headerContentType[2].toLowerCase().startsWith(Values.BOUNDARY.toString())) {
            return null;
        } else {
            mrank = 2;
            crank = 1;
        }
        String boundary = StringUtil.substringAfter(headerContentType[mrank], '=');
        if (boundary == null) {
            throw new ErrorDataDecoderException("Needs a boundary value");
        }
        if (boundary.charAt(0) == '\"') {
            String bound = boundary.trim();
            int index = bound.length() - 1;
            if (bound.charAt(index) == '\"') {
                boundary = bound.substring(1, index);
            }
        }
        if (!headerContentType[crank].toLowerCase().startsWith("charset".toString()) || StringUtil.substringAfter(headerContentType[crank], '=') == null) {
            return new String[]{"--" + boundary};
        }
        return new String[]{"--" + boundary, StringUtil.substringAfter(headerContentType[crank], '=')};
    }

    public boolean isMultipart() {
        return this.decoder.isMultipart();
    }

    public List<InterfaceHttpData> getBodyHttpDatas() throws NotEnoughDataDecoderException {
        return this.decoder.getBodyHttpDatas();
    }

    public List<InterfaceHttpData> getBodyHttpDatas(String name) throws NotEnoughDataDecoderException {
        return this.decoder.getBodyHttpDatas(name);
    }

    public InterfaceHttpData getBodyHttpData(String name) throws NotEnoughDataDecoderException {
        return this.decoder.getBodyHttpData(name);
    }

    public void offer(HttpChunk chunk) throws ErrorDataDecoderException {
        this.decoder.offer(chunk);
    }

    public boolean hasNext() throws EndOfDataDecoderException {
        return this.decoder.hasNext();
    }

    public InterfaceHttpData next() throws EndOfDataDecoderException {
        return this.decoder.next();
    }

    public void cleanFiles() {
        this.decoder.cleanFiles();
    }

    public void removeHttpDataFromClean(InterfaceHttpData data) {
        this.decoder.removeHttpDataFromClean(data);
    }

    private static String[] splitHeaderContentType(String sb) {
        int aStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
        int aEnd = sb.indexOf(59);
        if (aEnd == -1) {
            return new String[]{sb, "", ""};
        }
        int bStart = HttpPostBodyUtil.findNonWhitespace(sb, aEnd + 1);
        if (sb.charAt(aEnd - 1) == ' ') {
            aEnd--;
        }
        int bEnd = sb.indexOf(59, bStart);
        if (bEnd == -1) {
            bEnd = HttpPostBodyUtil.findEndOfString(sb);
            return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), ""};
        }
        int cStart = HttpPostBodyUtil.findNonWhitespace(sb, bEnd + 1);
        if (sb.charAt(bEnd - 1) == ' ') {
            bEnd--;
        }
        int cEnd = HttpPostBodyUtil.findEndOfString(sb);
        return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), sb.substring(cStart, cEnd)};
    }
}
