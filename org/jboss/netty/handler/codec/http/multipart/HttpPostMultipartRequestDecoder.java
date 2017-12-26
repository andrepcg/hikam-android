package org.jboss.netty.handler.codec.http.multipart;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpConstants;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.multipart.HttpPostBodyUtil.TransferEncodingMechanism;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder.NotEnoughDataDecoderException;
import org.jboss.netty.util.internal.CaseIgnoringComparator;
import org.jboss.netty.util.internal.StringUtil;

public class HttpPostMultipartRequestDecoder implements InterfaceHttpPostRequestDecoder {
    private final List<InterfaceHttpData> bodyListHttpData;
    private int bodyListHttpDataRank;
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
    private Charset charset;
    private Attribute currentAttribute;
    private Map<String, Attribute> currentFieldAttributes;
    private FileUpload currentFileUpload;
    private MultiPartStatus currentStatus;
    private final HttpDataFactory factory;
    private boolean isLastChunk;
    private String multipartDataBoundary;
    private String multipartMixedBoundary;
    private final HttpRequest request;
    private ChannelBuffer undecodedChunk;

    public HttpPostMultipartRequestDecoder(HttpRequest request) throws ErrorDataDecoderException {
        this(new DefaultHttpDataFactory(16384), request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request) throws ErrorDataDecoderException {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) throws ErrorDataDecoderException {
        this.bodyListHttpData = new ArrayList();
        this.bodyMapHttpData = new TreeMap(CaseIgnoringComparator.INSTANCE);
        this.currentStatus = MultiPartStatus.NOTSTARTED;
        if (factory == null) {
            throw new NullPointerException("factory");
        } else if (request == null) {
            throw new NullPointerException("request");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.request = request;
            this.charset = charset;
            this.factory = factory;
            setMultipart(this.request.headers().get("Content-Type"));
            if (!this.request.isChunked()) {
                this.undecodedChunk = this.request.getContent();
                this.isLastChunk = true;
                parseBody();
            }
        }
    }

    private void setMultipart(String contentType) throws ErrorDataDecoderException {
        String[] dataBoundary = HttpPostRequestDecoder.getMultipartDataBoundary(contentType);
        if (dataBoundary != null) {
            this.multipartDataBoundary = dataBoundary[0];
            if (dataBoundary.length > 1 && dataBoundary[1] != null) {
                this.charset = Charset.forName(dataBoundary[1]);
            }
        } else {
            this.multipartDataBoundary = null;
        }
        this.currentStatus = MultiPartStatus.HEADERDELIMITER;
    }

    public boolean isMultipart() {
        return true;
    }

    public List<InterfaceHttpData> getBodyHttpDatas() throws NotEnoughDataDecoderException {
        if (this.isLastChunk) {
            return this.bodyListHttpData;
        }
        throw new NotEnoughDataDecoderException();
    }

    public List<InterfaceHttpData> getBodyHttpDatas(String name) throws NotEnoughDataDecoderException {
        if (this.isLastChunk) {
            return (List) this.bodyMapHttpData.get(name);
        }
        throw new NotEnoughDataDecoderException();
    }

    public InterfaceHttpData getBodyHttpData(String name) throws NotEnoughDataDecoderException {
        if (this.isLastChunk) {
            List<InterfaceHttpData> list = (List) this.bodyMapHttpData.get(name);
            if (list != null) {
                return (InterfaceHttpData) list.get(0);
            }
            return null;
        }
        throw new NotEnoughDataDecoderException();
    }

    public void offer(HttpChunk chunk) throws ErrorDataDecoderException {
        ChannelBuffer chunked = chunk.getContent();
        if (this.undecodedChunk == null) {
            this.undecodedChunk = chunked;
        } else {
            this.undecodedChunk = ChannelBuffers.wrappedBuffer(this.undecodedChunk, chunked);
        }
        if (chunk.isLast()) {
            this.isLastChunk = true;
        }
        parseBody();
    }

    public boolean hasNext() throws EndOfDataDecoderException {
        if (this.currentStatus != MultiPartStatus.EPILOGUE || this.bodyListHttpDataRank < this.bodyListHttpData.size()) {
            return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
        } else {
            throw new EndOfDataDecoderException();
        }
    }

    public InterfaceHttpData next() throws EndOfDataDecoderException {
        if (!hasNext()) {
            return null;
        }
        List list = this.bodyListHttpData;
        int i = this.bodyListHttpDataRank;
        this.bodyListHttpDataRank = i + 1;
        return (InterfaceHttpData) list.get(i);
    }

    private void parseBody() throws ErrorDataDecoderException {
        if (this.currentStatus != MultiPartStatus.PREEPILOGUE && this.currentStatus != MultiPartStatus.EPILOGUE) {
            parseBodyMultipart();
        } else if (this.isLastChunk) {
            this.currentStatus = MultiPartStatus.EPILOGUE;
        }
    }

    private void addHttpData(InterfaceHttpData data) {
        if (data != null) {
            List<InterfaceHttpData> datas = (List) this.bodyMapHttpData.get(data.getName());
            if (datas == null) {
                datas = new ArrayList(1);
                this.bodyMapHttpData.put(data.getName(), datas);
            }
            datas.add(data);
            this.bodyListHttpData.add(data);
        }
    }

    private void parseBodyMultipart() throws ErrorDataDecoderException {
        if (this.undecodedChunk != null && this.undecodedChunk.readableBytes() != 0) {
            InterfaceHttpData data = decodeMultipart(this.currentStatus);
            while (data != null) {
                addHttpData(data);
                if (this.currentStatus != MultiPartStatus.PREEPILOGUE && this.currentStatus != MultiPartStatus.EPILOGUE) {
                    data = decodeMultipart(this.currentStatus);
                } else {
                    return;
                }
            }
        }
    }

    private InterfaceHttpData decodeMultipart(MultiPartStatus state) throws ErrorDataDecoderException {
        switch (state) {
            case NOTSTARTED:
                throw new ErrorDataDecoderException("Should not be called with the current status");
            case PREAMBLE:
                throw new ErrorDataDecoderException("Should not be called with the current status");
            case HEADERDELIMITER:
                return findMultipartDelimiter(this.multipartDataBoundary, MultiPartStatus.DISPOSITION, MultiPartStatus.PREEPILOGUE);
            case DISPOSITION:
                return findMultipartDisposition();
            case FIELD:
                Charset localCharset = null;
                Attribute charsetAttribute = (Attribute) this.currentFieldAttributes.get("charset");
                if (charsetAttribute != null) {
                    try {
                        localCharset = Charset.forName(charsetAttribute.getValue());
                    } catch (Throwable e) {
                        throw new ErrorDataDecoderException(e);
                    }
                }
                Attribute nameAttribute = (Attribute) this.currentFieldAttributes.get(HttpPostBodyUtil.NAME);
                if (this.currentAttribute == null) {
                    try {
                        this.currentAttribute = this.factory.createAttribute(this.request, cleanString(nameAttribute.getValue()));
                        if (localCharset != null) {
                            this.currentAttribute.setCharset(localCharset);
                        }
                    } catch (Throwable e2) {
                        throw new ErrorDataDecoderException(e2);
                    } catch (Throwable e22) {
                        throw new ErrorDataDecoderException(e22);
                    } catch (Throwable e222) {
                        throw new ErrorDataDecoderException(e222);
                    }
                }
                try {
                    loadFieldMultipart(this.multipartDataBoundary);
                    InterfaceHttpData finalAttribute = this.currentAttribute;
                    this.currentAttribute = null;
                    this.currentFieldAttributes = null;
                    this.currentStatus = MultiPartStatus.HEADERDELIMITER;
                    return finalAttribute;
                } catch (NotEnoughDataDecoderException e3) {
                    return null;
                }
            case FILEUPLOAD:
                return getFileUpload(this.multipartDataBoundary);
            case MIXEDDELIMITER:
                return findMultipartDelimiter(this.multipartMixedBoundary, MultiPartStatus.MIXEDDISPOSITION, MultiPartStatus.HEADERDELIMITER);
            case MIXEDDISPOSITION:
                return findMultipartDisposition();
            case MIXEDFILEUPLOAD:
                return getFileUpload(this.multipartMixedBoundary);
            case PREEPILOGUE:
                return null;
            case EPILOGUE:
                return null;
            default:
                throw new ErrorDataDecoderException("Shouldn't reach here.");
        }
    }

    void skipControlCharacters() throws NotEnoughDataDecoderException {
        try {
            SeekAheadOptimize sao = new SeekAheadOptimize(this.undecodedChunk);
            while (sao.pos < sao.limit) {
                byte[] bArr = sao.bytes;
                int i = sao.pos;
                sao.pos = i + 1;
                char c = (char) (bArr[i] & 255);
                if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
                    sao.setReadPosition(1);
                    return;
                }
            }
            throw new NotEnoughDataDecoderException("Access out of bounds");
        } catch (SeekAheadNoBackArrayException e) {
            try {
                skipControlCharactersStandard();
            } catch (Throwable e1) {
                throw new NotEnoughDataDecoderException(e1);
            }
        }
    }

    void skipControlCharactersStandard() {
        while (true) {
            char c = (char) this.undecodedChunk.readUnsignedByte();
            if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                return;
            }
        }
    }

    private InterfaceHttpData findMultipartDelimiter(String delimiter, MultiPartStatus dispositionStatus, MultiPartStatus closeDelimiterStatus) throws ErrorDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            skipControlCharacters();
            skipOneLine();
            try {
                String newline = readDelimiter(delimiter);
                if (newline.equals(delimiter)) {
                    this.currentStatus = dispositionStatus;
                    return decodeMultipart(dispositionStatus);
                } else if (newline.equals(delimiter + "--")) {
                    this.currentStatus = closeDelimiterStatus;
                    if (this.currentStatus != MultiPartStatus.HEADERDELIMITER) {
                        return null;
                    }
                    this.currentFieldAttributes = null;
                    return decodeMultipart(MultiPartStatus.HEADERDELIMITER);
                } else {
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new ErrorDataDecoderException("No Multipart delimiter found");
                }
            } catch (NotEnoughDataDecoderException e) {
                this.undecodedChunk.readerIndex(readerIndex);
                return null;
            }
        } catch (NotEnoughDataDecoderException e2) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
    }

    private InterfaceHttpData findMultipartDisposition() throws ErrorDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        if (this.currentStatus == MultiPartStatus.DISPOSITION) {
            this.currentFieldAttributes = new TreeMap(CaseIgnoringComparator.INSTANCE);
        }
        while (!skipOneLine()) {
            try {
                skipControlCharacters();
                String newline = readLine();
                String[] contents = splitMultipartHeader(newline);
                int i;
                Attribute attribute;
                if (contents[0].equalsIgnoreCase(HttpPostBodyUtil.CONTENT_DISPOSITION)) {
                    boolean checkSecondArg;
                    if (this.currentStatus == MultiPartStatus.DISPOSITION) {
                        checkSecondArg = contents[1].equalsIgnoreCase(HttpPostBodyUtil.FORM_DATA);
                    } else {
                        checkSecondArg = contents[1].equalsIgnoreCase(HttpPostBodyUtil.ATTACHMENT) || contents[1].equalsIgnoreCase(HttpPostBodyUtil.FILE);
                    }
                    if (checkSecondArg) {
                        i = 2;
                        while (i < contents.length) {
                            String[] values = StringUtil.split(contents[i], '=', 2);
                            try {
                                String name = cleanString(values[0]);
                                String value = values[1];
                                if (HttpPostBodyUtil.FILENAME.equals(name)) {
                                    value = value.substring(1, value.length() - 1);
                                } else {
                                    value = cleanString(value);
                                }
                                attribute = this.factory.createAttribute(this.request, name, value);
                                this.currentFieldAttributes.put(attribute.getName(), attribute);
                                i++;
                            } catch (Throwable e) {
                                throw new ErrorDataDecoderException(e);
                            } catch (Throwable e2) {
                                throw new ErrorDataDecoderException(e2);
                            }
                        }
                        continue;
                    } else {
                        continue;
                    }
                } else if (contents[0].equalsIgnoreCase(Names.CONTENT_TRANSFER_ENCODING)) {
                    try {
                        this.currentFieldAttributes.put(Names.CONTENT_TRANSFER_ENCODING, this.factory.createAttribute(this.request, Names.CONTENT_TRANSFER_ENCODING, cleanString(contents[1])));
                    } catch (Throwable e22) {
                        throw new ErrorDataDecoderException(e22);
                    } catch (Throwable e222) {
                        throw new ErrorDataDecoderException(e222);
                    }
                } else if (contents[0].equalsIgnoreCase("Content-Length")) {
                    try {
                        this.currentFieldAttributes.put("Content-Length", this.factory.createAttribute(this.request, "Content-Length", cleanString(contents[1])));
                    } catch (Throwable e2222) {
                        throw new ErrorDataDecoderException(e2222);
                    } catch (Throwable e22222) {
                        throw new ErrorDataDecoderException(e22222);
                    }
                } else if (!contents[0].equalsIgnoreCase("Content-Type")) {
                    throw new ErrorDataDecoderException("Unknown Params: " + newline);
                } else if (!contents[1].equalsIgnoreCase(HttpPostBodyUtil.MULTIPART_MIXED)) {
                    for (i = 1; i < contents.length; i++) {
                        if (contents[i].toLowerCase().startsWith("charset")) {
                            try {
                                this.currentFieldAttributes.put("charset", this.factory.createAttribute(this.request, "charset", cleanString(StringUtil.substringAfter(contents[i], '='))));
                            } catch (Throwable e222222) {
                                throw new ErrorDataDecoderException(e222222);
                            } catch (Throwable e2222222) {
                                throw new ErrorDataDecoderException(e2222222);
                            }
                        }
                        try {
                            attribute = this.factory.createAttribute(this.request, cleanString(contents[0]), contents[i]);
                            this.currentFieldAttributes.put(attribute.getName(), attribute);
                        } catch (Throwable e22222222) {
                            throw new ErrorDataDecoderException(e22222222);
                        } catch (Throwable e222222222) {
                            throw new ErrorDataDecoderException(e222222222);
                        }
                    }
                    continue;
                } else if (this.currentStatus == MultiPartStatus.DISPOSITION) {
                    this.multipartMixedBoundary = "--" + StringUtil.substringAfter(contents[2], '=');
                    this.currentStatus = MultiPartStatus.MIXEDDELIMITER;
                    return decodeMultipart(MultiPartStatus.MIXEDDELIMITER);
                } else {
                    throw new ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
                }
            } catch (NotEnoughDataDecoderException e3) {
                this.undecodedChunk.readerIndex(readerIndex);
                return null;
            }
        }
        Attribute filenameAttribute = (Attribute) this.currentFieldAttributes.get(HttpPostBodyUtil.FILENAME);
        if (this.currentStatus == MultiPartStatus.DISPOSITION) {
            if (filenameAttribute != null) {
                this.currentStatus = MultiPartStatus.FILEUPLOAD;
                return decodeMultipart(MultiPartStatus.FILEUPLOAD);
            }
            this.currentStatus = MultiPartStatus.FIELD;
            return decodeMultipart(MultiPartStatus.FIELD);
        } else if (filenameAttribute != null) {
            this.currentStatus = MultiPartStatus.MIXEDFILEUPLOAD;
            return decodeMultipart(MultiPartStatus.MIXEDFILEUPLOAD);
        } else {
            throw new ErrorDataDecoderException("Filename not found");
        }
    }

    private InterfaceHttpData getFileUpload(String delimiter) throws ErrorDataDecoderException {
        Attribute encoding = (Attribute) this.currentFieldAttributes.get(Names.CONTENT_TRANSFER_ENCODING);
        Charset localCharset = this.charset;
        TransferEncodingMechanism mechanism = TransferEncodingMechanism.BIT7;
        if (encoding != null) {
            try {
                String code = encoding.getValue().toLowerCase();
                if (code.equals(TransferEncodingMechanism.BIT7.value())) {
                    localCharset = HttpPostBodyUtil.US_ASCII;
                } else if (code.equals(TransferEncodingMechanism.BIT8.value())) {
                    localCharset = HttpPostBodyUtil.ISO_8859_1;
                    mechanism = TransferEncodingMechanism.BIT8;
                } else if (code.equals(TransferEncodingMechanism.BINARY.value())) {
                    mechanism = TransferEncodingMechanism.BINARY;
                } else {
                    throw new ErrorDataDecoderException("TransferEncoding Unknown: " + code);
                }
            } catch (Throwable e) {
                throw new ErrorDataDecoderException(e);
            }
        }
        Attribute charsetAttribute = (Attribute) this.currentFieldAttributes.get("charset");
        if (charsetAttribute != null) {
            try {
                localCharset = Charset.forName(charsetAttribute.getValue());
            } catch (Throwable e2) {
                throw new ErrorDataDecoderException(e2);
            }
        }
        if (this.currentFileUpload == null) {
            long size;
            Attribute filenameAttribute = (Attribute) this.currentFieldAttributes.get(HttpPostBodyUtil.FILENAME);
            Attribute nameAttribute = (Attribute) this.currentFieldAttributes.get(HttpPostBodyUtil.NAME);
            Attribute contentTypeAttribute = (Attribute) this.currentFieldAttributes.get("Content-Type");
            if (contentTypeAttribute == null) {
                contentTypeAttribute = new MemoryAttribute("Content-Type");
                try {
                    contentTypeAttribute.setValue("application/octet-stream");
                } catch (IOException e3) {
                    throw new ErrorDataDecoderException("Content-Type is absent but required, and cannot be reverted to default");
                }
            }
            Attribute lengthAttribute = (Attribute) this.currentFieldAttributes.get("Content-Length");
            if (lengthAttribute != null) {
                try {
                    size = Long.parseLong(lengthAttribute.getValue());
                } catch (Throwable e22) {
                    throw new ErrorDataDecoderException(e22);
                } catch (NumberFormatException e4) {
                    size = 0;
                }
            } else {
                size = 0;
            }
            try {
                this.currentFileUpload = this.factory.createFileUpload(this.request, cleanString(nameAttribute.getValue()), cleanString(filenameAttribute.getValue()), contentTypeAttribute.getValue(), mechanism.value(), localCharset, size);
            } catch (Throwable e222) {
                throw new ErrorDataDecoderException(e222);
            } catch (Throwable e2222) {
                throw new ErrorDataDecoderException(e2222);
            } catch (Throwable e22222) {
                throw new ErrorDataDecoderException(e22222);
            }
        }
        try {
            readFileUploadByteMultipart(delimiter);
            if (!this.currentFileUpload.isCompleted()) {
                return null;
            }
            if (this.currentStatus == MultiPartStatus.FILEUPLOAD) {
                this.currentStatus = MultiPartStatus.HEADERDELIMITER;
                this.currentFieldAttributes = null;
            } else {
                this.currentStatus = MultiPartStatus.MIXEDDELIMITER;
                cleanMixedAttributes();
            }
            FileUpload fileUpload = this.currentFileUpload;
            this.currentFileUpload = null;
            return fileUpload;
        } catch (NotEnoughDataDecoderException e5) {
            return null;
        }
    }

    public void cleanFiles() {
        this.factory.cleanRequestHttpDatas(this.request);
    }

    public void removeHttpDataFromClean(InterfaceHttpData data) {
        this.factory.removeHttpDataFromClean(this.request, data);
    }

    private void cleanMixedAttributes() {
        this.currentFieldAttributes.remove("charset");
        this.currentFieldAttributes.remove("Content-Length");
        this.currentFieldAttributes.remove(Names.CONTENT_TRANSFER_ENCODING);
        this.currentFieldAttributes.remove("Content-Type");
        this.currentFieldAttributes.remove(HttpPostBodyUtil.FILENAME);
    }

    private String readLineStandard() throws NotEnoughDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            ChannelBuffer line = ChannelBuffers.dynamicBuffer(64);
            while (this.undecodedChunk.readable()) {
                byte nextByte = this.undecodedChunk.readByte();
                if (nextByte == HttpConstants.CR) {
                    if (this.undecodedChunk.getByte(this.undecodedChunk.readerIndex()) == (byte) 10) {
                        this.undecodedChunk.readByte();
                        return line.toString(this.charset);
                    }
                    line.writeByte(13);
                } else if (nextByte == (byte) 10) {
                    return line.toString(this.charset);
                } else {
                    line.writeByte(nextByte);
                }
            }
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException();
        } catch (Throwable e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e);
        }
    }

    private String readLine() throws NotEnoughDataDecoderException {
        try {
            SeekAheadOptimize sao = new SeekAheadOptimize(this.undecodedChunk);
            int readerIndex = this.undecodedChunk.readerIndex();
            ChannelBuffer line = ChannelBuffers.dynamicBuffer(64);
            while (sao.pos < sao.limit) {
                byte[] bArr = sao.bytes;
                int i = sao.pos;
                sao.pos = i + 1;
                byte nextByte = bArr[i];
                if (nextByte == HttpConstants.CR) {
                    if (sao.pos < sao.limit) {
                        bArr = sao.bytes;
                        i = sao.pos;
                        sao.pos = i + 1;
                        if (bArr[i] == (byte) 10) {
                            sao.setReadPosition(0);
                            return line.toString(this.charset);
                        }
                        sao.pos--;
                        line.writeByte(13);
                    } else {
                        try {
                            line.writeByte(nextByte);
                        } catch (Throwable e) {
                            this.undecodedChunk.readerIndex(readerIndex);
                            throw new NotEnoughDataDecoderException(e);
                        }
                    }
                } else if (nextByte == (byte) 10) {
                    sao.setReadPosition(0);
                    return line.toString(this.charset);
                } else {
                    line.writeByte(nextByte);
                }
            }
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException();
        } catch (SeekAheadNoBackArrayException e2) {
            return readLineStandard();
        }
    }

    private String readDelimiterStandard(String delimiter) throws NotEnoughDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            StringBuilder sb = new StringBuilder(64);
            int delimiterPos = 0;
            int len = delimiter.length();
            while (this.undecodedChunk.readable() && delimiterPos < len) {
                char nextByte = this.undecodedChunk.readByte();
                if (nextByte == delimiter.charAt(delimiterPos)) {
                    delimiterPos++;
                    sb.append((char) nextByte);
                } else {
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new NotEnoughDataDecoderException();
                }
            }
            if (this.undecodedChunk.readable()) {
                byte nextByte2 = this.undecodedChunk.readByte();
                if (nextByte2 == HttpConstants.CR) {
                    if (this.undecodedChunk.readByte() == (byte) 10) {
                        return sb.toString();
                    }
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new NotEnoughDataDecoderException();
                } else if (nextByte2 == (byte) 10) {
                    return sb.toString();
                } else {
                    if (nextByte2 == (byte) 45) {
                        sb.append('-');
                        if (this.undecodedChunk.readByte() == (byte) 45) {
                            sb.append('-');
                            if (!this.undecodedChunk.readable()) {
                                return sb.toString();
                            }
                            nextByte2 = this.undecodedChunk.readByte();
                            if (nextByte2 == HttpConstants.CR) {
                                if (this.undecodedChunk.readByte() == (byte) 10) {
                                    return sb.toString();
                                }
                                this.undecodedChunk.readerIndex(readerIndex);
                                throw new NotEnoughDataDecoderException();
                            } else if (nextByte2 == (byte) 10) {
                                return sb.toString();
                            } else {
                                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                                return sb.toString();
                            }
                        }
                    }
                }
            }
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException();
        } catch (Throwable e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e);
        }
    }

    private String readDelimiter(String delimiter) throws NotEnoughDataDecoderException {
        try {
            SeekAheadOptimize sao = new SeekAheadOptimize(this.undecodedChunk);
            int readerIndex = this.undecodedChunk.readerIndex();
            int delimiterPos = 0;
            int len = delimiter.length();
            try {
                byte[] bArr;
                int i;
                StringBuilder sb = new StringBuilder(64);
                while (sao.pos < sao.limit && delimiterPos < len) {
                    bArr = sao.bytes;
                    i = sao.pos;
                    sao.pos = i + 1;
                    char nextByte = bArr[i];
                    if (nextByte == delimiter.charAt(delimiterPos)) {
                        delimiterPos++;
                        sb.append((char) nextByte);
                    } else {
                        this.undecodedChunk.readerIndex(readerIndex);
                        throw new NotEnoughDataDecoderException();
                    }
                }
                if (sao.pos < sao.limit) {
                    bArr = sao.bytes;
                    i = sao.pos;
                    sao.pos = i + 1;
                    byte nextByte2 = bArr[i];
                    if (nextByte2 == HttpConstants.CR) {
                        if (sao.pos < sao.limit) {
                            bArr = sao.bytes;
                            i = sao.pos;
                            sao.pos = i + 1;
                            if (bArr[i] == (byte) 10) {
                                sao.setReadPosition(0);
                                return sb.toString();
                            }
                            this.undecodedChunk.readerIndex(readerIndex);
                            throw new NotEnoughDataDecoderException();
                        }
                        this.undecodedChunk.readerIndex(readerIndex);
                        throw new NotEnoughDataDecoderException();
                    } else if (nextByte2 == (byte) 10) {
                        sao.setReadPosition(0);
                        return sb.toString();
                    } else if (nextByte2 == (byte) 45) {
                        sb.append('-');
                        if (sao.pos < sao.limit) {
                            bArr = sao.bytes;
                            i = sao.pos;
                            sao.pos = i + 1;
                            if (bArr[i] == (byte) 45) {
                                sb.append('-');
                                if (sao.pos < sao.limit) {
                                    bArr = sao.bytes;
                                    i = sao.pos;
                                    sao.pos = i + 1;
                                    nextByte2 = bArr[i];
                                    if (nextByte2 == HttpConstants.CR) {
                                        if (sao.pos < sao.limit) {
                                            bArr = sao.bytes;
                                            i = sao.pos;
                                            sao.pos = i + 1;
                                            if (bArr[i] == (byte) 10) {
                                                sao.setReadPosition(0);
                                                return sb.toString();
                                            }
                                            this.undecodedChunk.readerIndex(readerIndex);
                                            throw new NotEnoughDataDecoderException();
                                        }
                                        this.undecodedChunk.readerIndex(readerIndex);
                                        throw new NotEnoughDataDecoderException();
                                    } else if (nextByte2 == (byte) 10) {
                                        sao.setReadPosition(0);
                                        return sb.toString();
                                    } else {
                                        sao.setReadPosition(1);
                                        return sb.toString();
                                    }
                                }
                                sao.setReadPosition(0);
                                return sb.toString();
                            }
                        }
                    }
                }
                this.undecodedChunk.readerIndex(readerIndex);
                throw new NotEnoughDataDecoderException();
            } catch (Throwable e) {
                this.undecodedChunk.readerIndex(readerIndex);
                throw new NotEnoughDataDecoderException(e);
            }
        } catch (SeekAheadNoBackArrayException e2) {
            return readDelimiterStandard(delimiter);
        }
    }

    private void readFileUploadByteMultipartStandard(String delimiter) throws NotEnoughDataDecoderException, ErrorDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        boolean newLine = true;
        int index = 0;
        int lastPosition = this.undecodedChunk.readerIndex();
        boolean found = false;
        while (this.undecodedChunk.readable()) {
            byte nextByte = this.undecodedChunk.readByte();
            if (newLine) {
                if (nextByte == delimiter.codePointAt(index)) {
                    index++;
                    if (delimiter.length() == index) {
                        found = true;
                        break;
                    }
                } else {
                    newLine = false;
                    index = 0;
                    if (nextByte == HttpConstants.CR) {
                        if (this.undecodedChunk.readable()) {
                            if (this.undecodedChunk.readByte() == (byte) 10) {
                                newLine = true;
                                index = 0;
                                lastPosition = this.undecodedChunk.readerIndex() - 2;
                            } else {
                                lastPosition = this.undecodedChunk.readerIndex() - 1;
                                this.undecodedChunk.readerIndex(lastPosition);
                            }
                        }
                    } else if (nextByte == (byte) 10) {
                        newLine = true;
                        index = 0;
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                    } else {
                        lastPosition = this.undecodedChunk.readerIndex();
                    }
                }
            } else if (nextByte == HttpConstants.CR) {
                if (this.undecodedChunk.readable()) {
                    if (this.undecodedChunk.readByte() == (byte) 10) {
                        newLine = true;
                        index = 0;
                        lastPosition = this.undecodedChunk.readerIndex() - 2;
                    } else {
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                        this.undecodedChunk.readerIndex(lastPosition);
                    }
                }
            } else if (nextByte == (byte) 10) {
                newLine = true;
                index = 0;
                lastPosition = this.undecodedChunk.readerIndex() - 1;
            } else {
                lastPosition = this.undecodedChunk.readerIndex();
            }
        }
        ChannelBuffer buffer = this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex);
        if (found) {
            try {
                this.currentFileUpload.addContent(buffer, true);
                this.undecodedChunk.readerIndex(lastPosition);
                return;
            } catch (Throwable e) {
                throw new ErrorDataDecoderException(e);
            }
        }
        try {
            this.currentFileUpload.addContent(buffer, false);
            this.undecodedChunk.readerIndex(lastPosition);
            throw new NotEnoughDataDecoderException();
        } catch (Throwable e2) {
            throw new ErrorDataDecoderException(e2);
        }
    }

    private void readFileUploadByteMultipart(String delimiter) throws NotEnoughDataDecoderException, ErrorDataDecoderException {
        try {
            SeekAheadOptimize sao = new SeekAheadOptimize(this.undecodedChunk);
            int readerIndex = this.undecodedChunk.readerIndex();
            boolean newLine = true;
            int index = 0;
            int lastrealpos = sao.pos;
            boolean found = false;
            while (sao.pos < sao.limit) {
                byte[] bArr = sao.bytes;
                int i = sao.pos;
                sao.pos = i + 1;
                byte nextByte = bArr[i];
                if (newLine) {
                    if (nextByte == delimiter.codePointAt(index)) {
                        index++;
                        if (delimiter.length() == index) {
                            found = true;
                            break;
                        }
                    } else {
                        newLine = false;
                        index = 0;
                        if (nextByte == HttpConstants.CR) {
                            if (sao.pos < sao.limit) {
                                bArr = sao.bytes;
                                i = sao.pos;
                                sao.pos = i + 1;
                                if (bArr[i] == (byte) 10) {
                                    newLine = true;
                                    index = 0;
                                    lastrealpos = sao.pos - 2;
                                } else {
                                    sao.pos--;
                                    lastrealpos = sao.pos;
                                }
                            }
                        } else if (nextByte == (byte) 10) {
                            newLine = true;
                            index = 0;
                            lastrealpos = sao.pos - 1;
                        } else {
                            lastrealpos = sao.pos;
                        }
                    }
                } else if (nextByte == HttpConstants.CR) {
                    if (sao.pos < sao.limit) {
                        bArr = sao.bytes;
                        i = sao.pos;
                        sao.pos = i + 1;
                        if (bArr[i] == (byte) 10) {
                            newLine = true;
                            index = 0;
                            lastrealpos = sao.pos - 2;
                        } else {
                            sao.pos--;
                            lastrealpos = sao.pos;
                        }
                    }
                } else if (nextByte == (byte) 10) {
                    newLine = true;
                    index = 0;
                    lastrealpos = sao.pos - 1;
                } else {
                    lastrealpos = sao.pos;
                }
            }
            int lastPosition = sao.getReadPosition(lastrealpos);
            ChannelBuffer buffer = this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex);
            if (found) {
                try {
                    this.currentFileUpload.addContent(buffer, true);
                    this.undecodedChunk.readerIndex(lastPosition);
                    return;
                } catch (Throwable e) {
                    throw new ErrorDataDecoderException(e);
                }
            }
            try {
                this.currentFileUpload.addContent(buffer, false);
                this.undecodedChunk.readerIndex(lastPosition);
                throw new NotEnoughDataDecoderException();
            } catch (Throwable e2) {
                throw new ErrorDataDecoderException(e2);
            }
        } catch (SeekAheadNoBackArrayException e3) {
            readFileUploadByteMultipartStandard(delimiter);
        }
    }

    private void loadFieldMultipartStandard(String delimiter) throws NotEnoughDataDecoderException, ErrorDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        boolean newLine = true;
        int index = 0;
        try {
            int lastPosition = this.undecodedChunk.readerIndex();
            boolean found = false;
            while (this.undecodedChunk.readable()) {
                byte nextByte = this.undecodedChunk.readByte();
                if (newLine) {
                    if (nextByte == delimiter.codePointAt(index)) {
                        index++;
                        if (delimiter.length() == index) {
                            found = true;
                            break;
                        }
                    } else {
                        newLine = false;
                        index = 0;
                        if (nextByte == HttpConstants.CR) {
                            if (!this.undecodedChunk.readable()) {
                                lastPosition = this.undecodedChunk.readerIndex() - 1;
                            } else if (this.undecodedChunk.readByte() == (byte) 10) {
                                newLine = true;
                                index = 0;
                                lastPosition = this.undecodedChunk.readerIndex() - 2;
                            } else {
                                lastPosition = this.undecodedChunk.readerIndex() - 1;
                                this.undecodedChunk.readerIndex(lastPosition);
                            }
                        } else if (nextByte == (byte) 10) {
                            newLine = true;
                            index = 0;
                            lastPosition = this.undecodedChunk.readerIndex() - 1;
                        } else {
                            lastPosition = this.undecodedChunk.readerIndex();
                        }
                    }
                } else if (nextByte == HttpConstants.CR) {
                    if (!this.undecodedChunk.readable()) {
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                    } else if (this.undecodedChunk.readByte() == (byte) 10) {
                        newLine = true;
                        index = 0;
                        lastPosition = this.undecodedChunk.readerIndex() - 2;
                    } else {
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                        this.undecodedChunk.readerIndex(lastPosition);
                    }
                } else if (nextByte == (byte) 10) {
                    newLine = true;
                    index = 0;
                    lastPosition = this.undecodedChunk.readerIndex() - 1;
                } else {
                    lastPosition = this.undecodedChunk.readerIndex();
                }
            }
            if (found) {
                this.currentAttribute.addContent(this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex), true);
                this.undecodedChunk.readerIndex(lastPosition);
                return;
            }
            this.currentAttribute.addContent(this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex), false);
            this.undecodedChunk.readerIndex(lastPosition);
            throw new NotEnoughDataDecoderException();
        } catch (Throwable e) {
            throw new ErrorDataDecoderException(e);
        } catch (Throwable e2) {
            throw new ErrorDataDecoderException(e2);
        } catch (Throwable e22) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e22);
        }
    }

    private void loadFieldMultipart(String delimiter) throws NotEnoughDataDecoderException, ErrorDataDecoderException {
        try {
            SeekAheadOptimize sao = new SeekAheadOptimize(this.undecodedChunk);
            int readerIndex = this.undecodedChunk.readerIndex();
            boolean newLine = true;
            int index = 0;
            try {
                int lastrealpos = sao.pos;
                boolean found = false;
                while (sao.pos < sao.limit) {
                    byte[] bArr = sao.bytes;
                    int i = sao.pos;
                    sao.pos = i + 1;
                    byte nextByte = bArr[i];
                    if (newLine) {
                        if (nextByte == delimiter.codePointAt(index)) {
                            index++;
                            if (delimiter.length() == index) {
                                found = true;
                                break;
                            }
                        } else {
                            newLine = false;
                            index = 0;
                            if (nextByte == HttpConstants.CR) {
                                if (sao.pos < sao.limit) {
                                    bArr = sao.bytes;
                                    i = sao.pos;
                                    sao.pos = i + 1;
                                    if (bArr[i] == (byte) 10) {
                                        newLine = true;
                                        index = 0;
                                        lastrealpos = sao.pos - 2;
                                    } else {
                                        sao.pos--;
                                        lastrealpos = sao.pos;
                                    }
                                }
                            } else if (nextByte == (byte) 10) {
                                newLine = true;
                                index = 0;
                                lastrealpos = sao.pos - 1;
                            } else {
                                lastrealpos = sao.pos;
                            }
                        }
                    } else if (nextByte == HttpConstants.CR) {
                        if (sao.pos < sao.limit) {
                            bArr = sao.bytes;
                            i = sao.pos;
                            sao.pos = i + 1;
                            if (bArr[i] == (byte) 10) {
                                newLine = true;
                                index = 0;
                                lastrealpos = sao.pos - 2;
                            } else {
                                sao.pos--;
                                lastrealpos = sao.pos;
                            }
                        }
                    } else if (nextByte == (byte) 10) {
                        newLine = true;
                        index = 0;
                        lastrealpos = sao.pos - 1;
                    } else {
                        lastrealpos = sao.pos;
                    }
                }
                int lastPosition = sao.getReadPosition(lastrealpos);
                if (found) {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex), true);
                    this.undecodedChunk.readerIndex(lastPosition);
                    return;
                }
                this.currentAttribute.addContent(this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex), false);
                this.undecodedChunk.readerIndex(lastPosition);
                throw new NotEnoughDataDecoderException();
            } catch (Throwable e) {
                throw new ErrorDataDecoderException(e);
            } catch (Throwable e2) {
                throw new ErrorDataDecoderException(e2);
            } catch (Throwable e22) {
                this.undecodedChunk.readerIndex(readerIndex);
                throw new NotEnoughDataDecoderException(e22);
            }
        } catch (SeekAheadNoBackArrayException e3) {
            loadFieldMultipartStandard(delimiter);
        }
    }

    private static String cleanString(String field) {
        StringBuilder sb = new StringBuilder(field.length());
        for (int i = 0; i < field.length(); i++) {
            char nextChar = field.charAt(i);
            if (nextChar == ':') {
                sb.append(32);
            } else if (nextChar == ',') {
                sb.append(32);
            } else if (nextChar == '=') {
                sb.append(32);
            } else if (nextChar == ';') {
                sb.append(32);
            } else if (nextChar == '\t') {
                sb.append(32);
            } else if (nextChar != '\"') {
                sb.append(nextChar);
            }
        }
        return sb.toString().trim();
    }

    private boolean skipOneLine() {
        if (!this.undecodedChunk.readable()) {
            return false;
        }
        byte nextByte = this.undecodedChunk.readByte();
        if (nextByte == HttpConstants.CR) {
            if (!this.undecodedChunk.readable()) {
                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                return false;
            } else if (this.undecodedChunk.readByte() == (byte) 10) {
                return true;
            } else {
                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
                return false;
            }
        } else if (nextByte == (byte) 10) {
            return true;
        } else {
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
            return false;
        }
    }

    private static String[] splitMultipartHeader(String sb) {
        ArrayList<String> headers = new ArrayList(1);
        int nameStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
        int nameEnd = nameStart;
        while (nameEnd < sb.length()) {
            char ch = sb.charAt(nameEnd);
            if (ch == ':' || Character.isWhitespace(ch)) {
                break;
            }
            nameEnd++;
        }
        int colonEnd = nameEnd;
        while (colonEnd < sb.length()) {
            if (sb.charAt(colonEnd) == ':') {
                colonEnd++;
                break;
            }
            colonEnd++;
        }
        int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
        int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
        headers.add(sb.substring(nameStart, nameEnd));
        String svalue = sb.substring(valueStart, valueEnd);
        String[] values;
        if (svalue.indexOf(59) >= 0) {
            values = splitMultipartHeaderValues(svalue);
        } else {
            values = StringUtil.split(svalue, ',');
        }
        for (String value : values) {
            headers.add(value.trim());
        }
        String[] array = new String[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            array[i] = (String) headers.get(i);
        }
        return array;
    }

    private static String[] splitMultipartHeaderValues(String svalue) {
        List<String> values = new ArrayList(1);
        boolean inQuote = false;
        boolean escapeNext = false;
        int start = 0;
        for (int i = 0; i < svalue.length(); i++) {
            char c = svalue.charAt(i);
            if (inQuote) {
                if (escapeNext) {
                    escapeNext = false;
                } else if (c == '\\') {
                    escapeNext = true;
                } else if (c == '\"') {
                    inQuote = false;
                }
            } else if (c == '\"') {
                inQuote = true;
            } else if (c == ';') {
                values.add(svalue.substring(start, i));
                start = i + 1;
            }
        }
        values.add(svalue.substring(start));
        return (String[]) values.toArray(new String[values.size()]);
    }
}
