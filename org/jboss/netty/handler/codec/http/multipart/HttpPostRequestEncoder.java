package org.jboss.netty.handler.codec.http.multipart;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;
import org.apache.http.protocol.HTTP;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpConstants;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.multipart.HttpPostBodyUtil.TransferEncodingMechanism;
import org.jboss.netty.handler.stream.ChunkedInput;

public class HttpPostRequestEncoder implements ChunkedInput {
    private static final Map<Pattern, String> percentEncodings = new HashMap();
    private final List<InterfaceHttpData> bodyListDatas;
    private final Charset charset;
    private ChannelBuffer currentBuffer;
    private InterfaceHttpData currentData;
    private FileUpload currentFileUpload;
    private boolean duringMixedMode;
    private final EncoderMode encoderMode;
    private final HttpDataFactory factory;
    private long globalBodySize;
    private boolean headerFinalized;
    private boolean isChunked;
    private boolean isKey;
    private boolean isLastChunk;
    private boolean isLastChunkSent;
    private final boolean isMultipart;
    private ListIterator<InterfaceHttpData> iterator;
    String multipartDataBoundary;
    final List<InterfaceHttpData> multipartHttpDatas;
    String multipartMixedBoundary;
    private final HttpRequest request;

    public enum EncoderMode {
        RFC1738,
        RFC3986,
        HTML5
    }

    public static class ErrorDataEncoderException extends Exception {
        private static final long serialVersionUID = 5020247425493164465L;

        public ErrorDataEncoderException(String msg) {
            super(msg);
        }

        public ErrorDataEncoderException(Throwable cause) {
            super(cause);
        }

        public ErrorDataEncoderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    static {
        percentEncodings.put(Pattern.compile("\\*"), "%2A");
        percentEncodings.put(Pattern.compile("\\+"), "%20");
        percentEncodings.put(Pattern.compile("%7E"), "~");
    }

    public HttpPostRequestEncoder(HttpRequest request, boolean multipart) throws ErrorDataEncoderException {
        this(new DefaultHttpDataFactory(16384), request, multipart, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestEncoder(HttpDataFactory factory, HttpRequest request, boolean multipart) throws ErrorDataEncoderException {
        this(factory, request, multipart, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestEncoder(HttpDataFactory factory, HttpRequest request, boolean multipart, Charset charset) throws ErrorDataEncoderException {
        this(factory, request, multipart, charset, EncoderMode.RFC1738);
    }

    public HttpPostRequestEncoder(HttpDataFactory factory, HttpRequest request, boolean multipart, Charset charset, EncoderMode encoderMode) throws ErrorDataEncoderException {
        this.isKey = true;
        if (factory == null) {
            throw new NullPointerException("factory");
        } else if (request == null) {
            throw new NullPointerException("request");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            HttpMethod method = request.getMethod();
            if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT) || method.equals(HttpMethod.PATCH)) {
                this.request = request;
                this.charset = charset;
                this.factory = factory;
                this.encoderMode = encoderMode;
                this.bodyListDatas = new ArrayList();
                this.isLastChunk = false;
                this.isLastChunkSent = false;
                this.isMultipart = multipart;
                this.multipartHttpDatas = new ArrayList();
                if (this.isMultipart) {
                    initDataMultipart();
                    return;
                }
                return;
            }
            throw new ErrorDataEncoderException("Cannot create a Encoder if not a POST");
        }
    }

    public void cleanFiles() {
        this.factory.cleanRequestHttpDatas(this.request);
    }

    public boolean isMultipart() {
        return this.isMultipart;
    }

    private void initDataMultipart() {
        this.multipartDataBoundary = getNewMultipartDelimiter();
    }

    private void initMixedMultipart() {
        this.multipartMixedBoundary = getNewMultipartDelimiter();
    }

    private static String getNewMultipartDelimiter() {
        return Long.toHexString(new Random().nextLong()).toLowerCase();
    }

    public List<InterfaceHttpData> getBodyListAttributes() {
        return this.bodyListDatas;
    }

    public void setBodyHttpDatas(List<InterfaceHttpData> datas) throws ErrorDataEncoderException {
        if (datas == null) {
            throw new NullPointerException("datas");
        }
        this.globalBodySize = 0;
        this.bodyListDatas.clear();
        this.currentFileUpload = null;
        this.duringMixedMode = false;
        this.multipartHttpDatas.clear();
        for (InterfaceHttpData data : datas) {
            addBodyHttpData(data);
        }
    }

    public void addBodyAttribute(String name, String value) throws ErrorDataEncoderException {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
        }
        String svalue = value;
        if (value == null) {
            svalue = "";
        }
        addBodyHttpData(this.factory.createAttribute(this.request, name, svalue));
    }

    public void addBodyFileUpload(String name, File file, String contentType, boolean isText) throws ErrorDataEncoderException {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
        } else if (file == null) {
            throw new NullPointerException(HttpPostBodyUtil.FILE);
        } else {
            String scontentType = contentType;
            String contentTransferEncoding = null;
            if (contentType == null) {
                if (isText) {
                    scontentType = "text/plain";
                } else {
                    scontentType = "application/octet-stream";
                }
            }
            if (!isText) {
                contentTransferEncoding = TransferEncodingMechanism.BINARY.value();
            }
            FileUpload fileUpload = this.factory.createFileUpload(this.request, name, file.getName(), scontentType, contentTransferEncoding, null, file.length());
            try {
                fileUpload.setContent(file);
                addBodyHttpData(fileUpload);
            } catch (Throwable e) {
                throw new ErrorDataEncoderException(e);
            }
        }
    }

    public void addBodyFileUploads(String name, File[] file, String[] contentType, boolean[] isText) throws ErrorDataEncoderException {
        if (file.length == contentType.length || file.length == isText.length) {
            for (int i = 0; i < file.length; i++) {
                addBodyFileUpload(name, file[i], contentType[i], isText[i]);
            }
            return;
        }
        throw new NullPointerException("Different array length");
    }

    public void addBodyHttpData(InterfaceHttpData data) throws ErrorDataEncoderException {
        if (this.headerFinalized) {
            throw new ErrorDataEncoderException("Cannot add value once finalized");
        } else if (data == null) {
            throw new NullPointerException("data");
        } else {
            this.bodyListDatas.add(data);
            Attribute attribute;
            FileUpload fileUpload;
            if (this.isMultipart) {
                InternalAttribute internal;
                if (data instanceof Attribute) {
                    if (this.duringMixedMode) {
                        internal = new InternalAttribute(this.charset);
                        internal.addValue("\r\n--" + this.multipartMixedBoundary + "--");
                        this.multipartHttpDatas.add(internal);
                        this.multipartMixedBoundary = null;
                        this.currentFileUpload = null;
                        this.duringMixedMode = false;
                    }
                    internal = new InternalAttribute(this.charset);
                    if (!this.multipartHttpDatas.isEmpty()) {
                        internal.addValue("\r\n");
                    }
                    internal.addValue("--" + this.multipartDataBoundary + "\r\n");
                    attribute = (Attribute) data;
                    internal.addValue("Content-Disposition: form-data; name=\"" + attribute.getName() + "\"\r\n");
                    Charset localcharset = attribute.getCharset();
                    if (localcharset != null) {
                        internal.addValue("Content-Type: text/plain; charset=" + localcharset.name() + "\r\n");
                    }
                    internal.addValue("\r\n");
                    this.multipartHttpDatas.add(internal);
                    this.multipartHttpDatas.add(data);
                    this.globalBodySize += attribute.length() + ((long) internal.size());
                } else if (data instanceof FileUpload) {
                    boolean localMixed;
                    fileUpload = (FileUpload) data;
                    internal = new InternalAttribute(this.charset);
                    if (!this.multipartHttpDatas.isEmpty()) {
                        internal.addValue("\r\n");
                    }
                    if (this.duringMixedMode) {
                        if (this.currentFileUpload == null || !this.currentFileUpload.getName().equals(fileUpload.getName())) {
                            internal.addValue("--" + this.multipartMixedBoundary + "--");
                            this.multipartHttpDatas.add(internal);
                            this.multipartMixedBoundary = null;
                            internal = new InternalAttribute(this.charset);
                            internal.addValue("\r\n");
                            localMixed = false;
                            this.currentFileUpload = fileUpload;
                            this.duringMixedMode = false;
                        } else {
                            localMixed = true;
                        }
                    } else if (this.encoderMode == EncoderMode.HTML5 || this.currentFileUpload == null || !this.currentFileUpload.getName().equals(fileUpload.getName())) {
                        localMixed = false;
                        this.currentFileUpload = fileUpload;
                        this.duringMixedMode = false;
                    } else {
                        initMixedMultipart();
                        InternalAttribute pastAttribute = (InternalAttribute) this.multipartHttpDatas.get(this.multipartHttpDatas.size() - 2);
                        this.globalBodySize -= (long) pastAttribute.size();
                        StringBuilder replacement = new StringBuilder((((this.multipartDataBoundary.length() + 139) + (this.multipartMixedBoundary.length() * 2)) + fileUpload.getFilename().length()) + fileUpload.getName().length());
                        replacement.append("--");
                        replacement.append(this.multipartDataBoundary);
                        replacement.append("\r\n");
                        replacement.append(HttpPostBodyUtil.CONTENT_DISPOSITION);
                        replacement.append(": ");
                        replacement.append(HttpPostBodyUtil.FORM_DATA);
                        replacement.append("; ");
                        replacement.append(HttpPostBodyUtil.NAME);
                        replacement.append("=\"");
                        replacement.append(fileUpload.getName());
                        replacement.append("\"\r\n");
                        replacement.append("Content-Type");
                        replacement.append(": ");
                        replacement.append(HttpPostBodyUtil.MULTIPART_MIXED);
                        replacement.append("; ");
                        replacement.append(Values.BOUNDARY);
                        replacement.append('=');
                        replacement.append(this.multipartMixedBoundary);
                        replacement.append("\r\n\r\n");
                        replacement.append("--");
                        replacement.append(this.multipartMixedBoundary);
                        replacement.append("\r\n");
                        replacement.append(HttpPostBodyUtil.CONTENT_DISPOSITION);
                        replacement.append(": ");
                        replacement.append(HttpPostBodyUtil.ATTACHMENT);
                        replacement.append("; ");
                        replacement.append(HttpPostBodyUtil.FILENAME);
                        replacement.append("=\"");
                        replacement.append(fileUpload.getFilename());
                        replacement.append("\"\r\n");
                        pastAttribute.setValue(replacement.toString(), 1);
                        pastAttribute.setValue("", 2);
                        this.globalBodySize += (long) pastAttribute.size();
                        localMixed = true;
                        this.duringMixedMode = true;
                    }
                    if (localMixed) {
                        internal.addValue("--" + this.multipartMixedBoundary + "\r\n");
                        internal.addValue("Content-Disposition: attachment; filename=\"" + fileUpload.getFilename() + "\"\r\n");
                    } else {
                        internal.addValue("--" + this.multipartDataBoundary + "\r\n");
                        internal.addValue("Content-Disposition: form-data; name=\"" + fileUpload.getName() + "\"; " + HttpPostBodyUtil.FILENAME + "=\"" + fileUpload.getFilename() + "\"\r\n");
                    }
                    internal.addValue("Content-Type: " + fileUpload.getContentType());
                    String contentTransferEncoding = fileUpload.getContentTransferEncoding();
                    if (contentTransferEncoding != null && contentTransferEncoding.equals(TransferEncodingMechanism.BINARY.value())) {
                        internal.addValue("\r\nContent-Transfer-Encoding: " + TransferEncodingMechanism.BINARY.value() + "\r\n\r\n");
                    } else if (fileUpload.getCharset() != null) {
                        internal.addValue(HTTP.CHARSET_PARAM + fileUpload.getCharset().name() + "\r\n\r\n");
                    } else {
                        internal.addValue("\r\n\r\n");
                    }
                    this.multipartHttpDatas.add(internal);
                    this.multipartHttpDatas.add(data);
                    this.globalBodySize += fileUpload.length() + ((long) internal.size());
                }
            } else if (data instanceof Attribute) {
                attribute = (Attribute) data;
                try {
                    newattribute = this.factory.createAttribute(this.request, encodeAttribute(attribute.getName(), this.charset), encodeAttribute(attribute.getValue(), this.charset));
                    this.multipartHttpDatas.add(newattribute);
                    this.globalBodySize += (((long) (newattribute.getName().length() + 1)) + newattribute.length()) + 1;
                } catch (Throwable e) {
                    throw new ErrorDataEncoderException(e);
                }
            } else if (data instanceof FileUpload) {
                fileUpload = (FileUpload) data;
                newattribute = this.factory.createAttribute(this.request, encodeAttribute(fileUpload.getName(), this.charset), encodeAttribute(fileUpload.getFilename(), this.charset));
                this.multipartHttpDatas.add(newattribute);
                this.globalBodySize += (((long) (newattribute.getName().length() + 1)) + newattribute.length()) + 1;
            }
        }
    }

    public HttpRequest finalizeRequest() throws ErrorDataEncoderException {
        HttpHeaders headers = this.request.headers();
        if (this.headerFinalized) {
            throw new ErrorDataEncoderException("Header already encoded");
        }
        if (this.isMultipart) {
            InternalAttribute internal = new InternalAttribute(this.charset);
            if (this.duringMixedMode) {
                internal.addValue("\r\n--" + this.multipartMixedBoundary + "--");
            }
            internal.addValue("\r\n--" + this.multipartDataBoundary + "--\r\n");
            this.multipartHttpDatas.add(internal);
            this.multipartMixedBoundary = null;
            this.currentFileUpload = null;
            this.duringMixedMode = false;
            this.globalBodySize += (long) internal.size();
        }
        this.headerFinalized = true;
        List<String> contentTypes = headers.getAll("Content-Type");
        List<String> transferEncoding = headers.getAll("Transfer-Encoding");
        if (contentTypes != null) {
            headers.remove("Content-Type");
            for (Object contentType : contentTypes) {
                String lowercased = contentType.toLowerCase();
                if (!(lowercased.startsWith(Values.MULTIPART_FORM_DATA) || lowercased.startsWith("application/x-www-form-urlencoded"))) {
                    headers.add("Content-Type", contentType);
                }
            }
        }
        if (this.isMultipart) {
            headers.add("Content-Type", "multipart/form-data; boundary=" + this.multipartDataBoundary);
        } else {
            headers.add("Content-Type", (Object) "application/x-www-form-urlencoded");
        }
        long realSize = this.globalBodySize;
        if (this.isMultipart) {
            this.iterator = this.multipartHttpDatas.listIterator();
        } else {
            realSize--;
            this.iterator = this.multipartHttpDatas.listIterator();
        }
        headers.set("Content-Length", String.valueOf(realSize));
        if (realSize > 8096 || this.isMultipart) {
            this.isChunked = true;
            if (transferEncoding != null) {
                headers.remove("Transfer-Encoding");
                for (Object v : transferEncoding) {
                    if (!v.equalsIgnoreCase("chunked")) {
                        headers.add("Transfer-Encoding", v);
                    }
                }
            }
            headers.add("Transfer-Encoding", (Object) "chunked");
            this.request.setContent(ChannelBuffers.EMPTY_BUFFER);
        } else {
            this.request.setContent(nextChunk().getContent());
        }
        return this.request;
    }

    public boolean isChunked() {
        return this.isChunked;
    }

    private String encodeAttribute(String s, Charset charset) throws ErrorDataEncoderException {
        if (s == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(s, charset.name());
            if (this.encoderMode != EncoderMode.RFC3986) {
                return encoded;
            }
            for (Entry<Pattern, String> entry : percentEncodings.entrySet()) {
                encoded = ((Pattern) entry.getKey()).matcher(encoded).replaceAll((String) entry.getValue());
            }
            return encoded;
        } catch (UnsupportedEncodingException e) {
            throw new ErrorDataEncoderException(charset.name(), e);
        }
    }

    private ChannelBuffer fillChannelBuffer() {
        if (this.currentBuffer.readableBytes() > HttpPostBodyUtil.chunkSize) {
            ChannelBuffer slice = this.currentBuffer.slice(this.currentBuffer.readerIndex(), HttpPostBodyUtil.chunkSize);
            this.currentBuffer.skipBytes(HttpPostBodyUtil.chunkSize);
            return slice;
        }
        slice = this.currentBuffer;
        this.currentBuffer = null;
        return slice;
    }

    private HttpChunk encodeNextChunkMultipart(int sizeleft) throws ErrorDataEncoderException {
        if (this.currentData == null) {
            return null;
        }
        ChannelBuffer buffer;
        if (this.currentData instanceof InternalAttribute) {
            buffer = ((InternalAttribute) this.currentData).toChannelBuffer();
            this.currentData = null;
        } else {
            if (this.currentData instanceof Attribute) {
                try {
                    buffer = ((Attribute) this.currentData).getChunk(sizeleft);
                } catch (Throwable e) {
                    throw new ErrorDataEncoderException(e);
                }
            }
            try {
                buffer = ((HttpData) this.currentData).getChunk(sizeleft);
            } catch (Throwable e2) {
                throw new ErrorDataEncoderException(e2);
            }
            if (buffer.capacity() == 0) {
                this.currentData = null;
                return null;
            }
        }
        if (this.currentBuffer == null) {
            this.currentBuffer = buffer;
        } else {
            this.currentBuffer = ChannelBuffers.wrappedBuffer(this.currentBuffer, buffer);
        }
        if (this.currentBuffer.readableBytes() >= HttpPostBodyUtil.chunkSize) {
            return new DefaultHttpChunk(fillChannelBuffer());
        }
        this.currentData = null;
        return null;
    }

    private HttpChunk encodeNextChunkUrlEncoded(int sizeleft) throws ErrorDataEncoderException {
        if (this.currentData == null) {
            return null;
        }
        ChannelBuffer buffer;
        int size = sizeleft;
        if (this.isKey) {
            buffer = ChannelBuffers.wrappedBuffer(this.currentData.getName().getBytes());
            this.isKey = false;
            if (this.currentBuffer == null) {
                this.currentBuffer = ChannelBuffers.wrappedBuffer(buffer, ChannelBuffers.wrappedBuffer("=".getBytes()));
                size -= buffer.readableBytes() + 1;
            } else {
                this.currentBuffer = ChannelBuffers.wrappedBuffer(this.currentBuffer, buffer, ChannelBuffers.wrappedBuffer("=".getBytes()));
                size -= buffer.readableBytes() + 1;
            }
            if (this.currentBuffer.readableBytes() >= HttpPostBodyUtil.chunkSize) {
                return new DefaultHttpChunk(fillChannelBuffer());
            }
        }
        try {
            buffer = ((HttpData) this.currentData).getChunk(size);
            ChannelBuffer delimiter = null;
            if (buffer.readableBytes() < size) {
                this.isKey = true;
                if (this.iterator.hasNext()) {
                    delimiter = ChannelBuffers.wrappedBuffer("&".getBytes());
                } else {
                    delimiter = null;
                }
            }
            if (buffer.capacity() == 0) {
                this.currentData = null;
                if (this.currentBuffer == null) {
                    this.currentBuffer = delimiter;
                } else if (delimiter != null) {
                    this.currentBuffer = ChannelBuffers.wrappedBuffer(this.currentBuffer, delimiter);
                }
                if (this.currentBuffer.readableBytes() >= HttpPostBodyUtil.chunkSize) {
                    return new DefaultHttpChunk(fillChannelBuffer());
                }
                return null;
            }
            if (this.currentBuffer == null) {
                if (delimiter != null) {
                    this.currentBuffer = ChannelBuffers.wrappedBuffer(buffer, delimiter);
                } else {
                    this.currentBuffer = buffer;
                }
            } else if (delimiter != null) {
                this.currentBuffer = ChannelBuffers.wrappedBuffer(this.currentBuffer, buffer, delimiter);
            } else {
                this.currentBuffer = ChannelBuffers.wrappedBuffer(this.currentBuffer, buffer);
            }
            if (this.currentBuffer.readableBytes() >= HttpPostBodyUtil.chunkSize) {
                return new DefaultHttpChunk(fillChannelBuffer());
            }
            this.currentData = null;
            this.isKey = true;
            return null;
        } catch (Throwable e) {
            throw new ErrorDataEncoderException(e);
        }
    }

    public void close() throws Exception {
    }

    public HttpChunk nextChunk() throws ErrorDataEncoderException {
        if (this.isLastChunk) {
            this.isLastChunkSent = true;
            return new DefaultHttpChunk(ChannelBuffers.EMPTY_BUFFER);
        }
        int size = HttpPostBodyUtil.chunkSize;
        if (this.currentBuffer != null) {
            size = HttpPostBodyUtil.chunkSize - this.currentBuffer.readableBytes();
        }
        if (size <= 0) {
            return new DefaultHttpChunk(fillChannelBuffer());
        }
        HttpChunk chunk;
        if (this.currentData != null) {
            if (this.isMultipart) {
                chunk = encodeNextChunkMultipart(size);
                if (chunk != null) {
                    return chunk;
                }
            }
            chunk = encodeNextChunkUrlEncoded(size);
            if (chunk != null) {
                return chunk;
            }
            size = 8096 - this.currentBuffer.readableBytes();
        }
        if (this.iterator.hasNext()) {
            while (size > 0 && this.iterator.hasNext()) {
                this.currentData = (InterfaceHttpData) this.iterator.next();
                if (this.isMultipart) {
                    chunk = encodeNextChunkMultipart(size);
                } else {
                    chunk = encodeNextChunkUrlEncoded(size);
                }
                if (chunk != null) {
                    return chunk;
                }
                size = 8096 - this.currentBuffer.readableBytes();
            }
            this.isLastChunk = true;
            if (this.currentBuffer == null) {
                this.isLastChunkSent = true;
                return new DefaultHttpChunk(ChannelBuffers.EMPTY_BUFFER);
            }
            ChannelBuffer buffer = this.currentBuffer;
            this.currentBuffer = null;
            return new DefaultHttpChunk(buffer);
        }
        this.isLastChunk = true;
        buffer = this.currentBuffer;
        this.currentBuffer = null;
        return new DefaultHttpChunk(buffer);
    }

    public boolean isEndOfInput() throws Exception {
        return this.isLastChunkSent;
    }

    public boolean hasNextChunk() throws Exception {
        return !this.isLastChunkSent;
    }
}
