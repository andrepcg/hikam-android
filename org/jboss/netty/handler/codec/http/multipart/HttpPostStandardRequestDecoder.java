package org.jboss.netty.handler.codec.http.multipart;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpConstants;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder.NotEnoughDataDecoderException;
import org.jboss.netty.util.internal.CaseIgnoringComparator;

public class HttpPostStandardRequestDecoder implements InterfaceHttpPostRequestDecoder {
    private final List<InterfaceHttpData> bodyListHttpData;
    private int bodyListHttpDataRank;
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
    private final Charset charset;
    private Attribute currentAttribute;
    private MultiPartStatus currentStatus;
    private final HttpDataFactory factory;
    private boolean isLastChunk;
    private final HttpRequest request;
    private ChannelBuffer undecodedChunk;

    public HttpPostStandardRequestDecoder(HttpRequest request) throws ErrorDataDecoderException {
        this(new DefaultHttpDataFactory(16384), request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request) throws ErrorDataDecoderException {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) throws ErrorDataDecoderException {
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
            if (!this.request.isChunked()) {
                this.undecodedChunk = this.request.getContent();
                this.isLastChunk = true;
                parseBody();
            }
        }
    }

    public boolean isMultipart() {
        return false;
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
            parseBodyAttributes();
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

    private void parseBodyAttributesStandard() throws ErrorDataDecoderException {
        int firstpos = this.undecodedChunk.readerIndex();
        int currentpos = firstpos;
        if (this.currentStatus == MultiPartStatus.NOTSTARTED) {
            this.currentStatus = MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        while (this.undecodedChunk.readable() && contRead) {
            try {
                char read = (char) this.undecodedChunk.readUnsignedByte();
                currentpos++;
                switch (this.currentStatus) {
                    case DISPOSITION:
                        if (read != '=') {
                            if (read != '&') {
                                break;
                            }
                            this.currentStatus = MultiPartStatus.DISPOSITION;
                            this.currentAttribute = this.factory.createAttribute(this.request, decodeAttribute(this.undecodedChunk.toString(firstpos, (currentpos - 1) - firstpos, this.charset), this.charset));
                            this.currentAttribute.setValue("");
                            addHttpData(this.currentAttribute);
                            this.currentAttribute = null;
                            firstpos = currentpos;
                            contRead = true;
                            break;
                        }
                        this.currentStatus = MultiPartStatus.FIELD;
                        this.currentAttribute = this.factory.createAttribute(this.request, decodeAttribute(this.undecodedChunk.toString(firstpos, (currentpos - 1) - firstpos, this.charset), this.charset));
                        firstpos = currentpos;
                        break;
                    case FIELD:
                        if (read == '&') {
                            this.currentStatus = MultiPartStatus.DISPOSITION;
                            setFinalBuffer(this.undecodedChunk.slice(firstpos, (currentpos - 1) - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            break;
                        } else if (read == '\r') {
                            if (!this.undecodedChunk.readable()) {
                                currentpos--;
                                break;
                            }
                            currentpos++;
                            if (((char) this.undecodedChunk.readUnsignedByte()) == '\n') {
                                this.currentStatus = MultiPartStatus.PREEPILOGUE;
                                setFinalBuffer(this.undecodedChunk.slice(firstpos, (currentpos - 2) - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                break;
                            }
                            throw new ErrorDataDecoderException("Bad end of line");
                        } else if (read == '\n') {
                            this.currentStatus = MultiPartStatus.PREEPILOGUE;
                            setFinalBuffer(this.undecodedChunk.slice(firstpos, (currentpos - 1) - firstpos));
                            firstpos = currentpos;
                            contRead = false;
                            break;
                        } else {
                            continue;
                        }
                    default:
                        contRead = false;
                        break;
                }
            } catch (ErrorDataDecoderException e) {
                this.undecodedChunk.readerIndex(firstpos);
                throw e;
            } catch (Throwable e2) {
                this.undecodedChunk.readerIndex(firstpos);
                throw new ErrorDataDecoderException(e2);
            }
        }
        if (!this.isLastChunk || this.currentAttribute == null) {
            if (contRead) {
                if (this.currentAttribute != null) {
                    if (this.currentStatus == MultiPartStatus.FIELD) {
                        this.currentAttribute.addContent(this.undecodedChunk.slice(firstpos, currentpos - firstpos), false);
                        firstpos = currentpos;
                    }
                    this.undecodedChunk.readerIndex(firstpos);
                    return;
                }
            }
            this.undecodedChunk.readerIndex(firstpos);
            return;
        }
        int ampersandpos = currentpos;
        if (ampersandpos > firstpos) {
            setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
        } else if (!this.currentAttribute.isCompleted()) {
            setFinalBuffer(ChannelBuffers.EMPTY_BUFFER);
        }
        firstpos = currentpos;
        this.currentStatus = MultiPartStatus.EPILOGUE;
        this.undecodedChunk.readerIndex(firstpos);
    }

    private void parseBodyAttributes() throws ErrorDataDecoderException {
        try {
            SeekAheadOptimize sao = new SeekAheadOptimize(this.undecodedChunk);
            int firstpos = this.undecodedChunk.readerIndex();
            int currentpos = firstpos;
            if (this.currentStatus == MultiPartStatus.NOTSTARTED) {
                this.currentStatus = MultiPartStatus.DISPOSITION;
            }
            boolean contRead = true;
            while (sao.pos < sao.limit) {
                try {
                    int ampersandpos;
                    byte[] bArr = sao.bytes;
                    int i = sao.pos;
                    sao.pos = i + 1;
                    char read = (char) (bArr[i] & 255);
                    currentpos++;
                    switch (this.currentStatus) {
                        case DISPOSITION:
                            if (read != '=') {
                                if (read != '&') {
                                    break;
                                }
                                this.currentStatus = MultiPartStatus.DISPOSITION;
                                this.currentAttribute = this.factory.createAttribute(this.request, decodeAttribute(this.undecodedChunk.toString(firstpos, (currentpos - 1) - firstpos, this.charset), this.charset));
                                this.currentAttribute.setValue("");
                                addHttpData(this.currentAttribute);
                                this.currentAttribute = null;
                                firstpos = currentpos;
                                contRead = true;
                                break;
                            }
                            this.currentStatus = MultiPartStatus.FIELD;
                            this.currentAttribute = this.factory.createAttribute(this.request, decodeAttribute(this.undecodedChunk.toString(firstpos, (currentpos - 1) - firstpos, this.charset), this.charset));
                            firstpos = currentpos;
                            continue;
                        case FIELD:
                            if (read != '&') {
                                if (read != '\r') {
                                    if (read != '\n') {
                                        break;
                                    }
                                    this.currentStatus = MultiPartStatus.PREEPILOGUE;
                                    ampersandpos = currentpos - 1;
                                    sao.setReadPosition(0);
                                    setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                                    firstpos = currentpos;
                                    contRead = false;
                                    break;
                                } else if (sao.pos >= sao.limit) {
                                    if (sao.limit <= 0) {
                                        break;
                                    }
                                    currentpos--;
                                    break;
                                } else {
                                    bArr = sao.bytes;
                                    i = sao.pos;
                                    sao.pos = i + 1;
                                    currentpos++;
                                    if (((char) (bArr[i] & 255)) == '\n') {
                                        this.currentStatus = MultiPartStatus.PREEPILOGUE;
                                        ampersandpos = currentpos - 2;
                                        sao.setReadPosition(0);
                                        setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                                        firstpos = currentpos;
                                        contRead = false;
                                        break;
                                    }
                                    sao.setReadPosition(0);
                                    throw new ErrorDataDecoderException("Bad end of line");
                                }
                            }
                            this.currentStatus = MultiPartStatus.DISPOSITION;
                            setFinalBuffer(this.undecodedChunk.slice(firstpos, (currentpos - 1) - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        default:
                            sao.setReadPosition(0);
                            contRead = false;
                            break;
                    }
                    if (this.isLastChunk || this.currentAttribute == null) {
                        if (contRead) {
                            if (this.currentAttribute != null) {
                                if (this.currentStatus == MultiPartStatus.FIELD) {
                                    this.currentAttribute.addContent(this.undecodedChunk.slice(firstpos, currentpos - firstpos), false);
                                    firstpos = currentpos;
                                }
                                this.undecodedChunk.readerIndex(firstpos);
                                return;
                            }
                        }
                        this.undecodedChunk.readerIndex(firstpos);
                    }
                    ampersandpos = currentpos;
                    if (ampersandpos > firstpos) {
                        setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                    } else if (!this.currentAttribute.isCompleted()) {
                        setFinalBuffer(ChannelBuffers.EMPTY_BUFFER);
                    }
                    firstpos = currentpos;
                    this.currentStatus = MultiPartStatus.EPILOGUE;
                    this.undecodedChunk.readerIndex(firstpos);
                    return;
                } catch (ErrorDataDecoderException e) {
                    this.undecodedChunk.readerIndex(firstpos);
                    throw e;
                } catch (Throwable e2) {
                    this.undecodedChunk.readerIndex(firstpos);
                    throw new ErrorDataDecoderException(e2);
                }
            }
            if (this.isLastChunk) {
            }
            if (contRead) {
                if (this.currentAttribute != null) {
                    if (this.currentStatus == MultiPartStatus.FIELD) {
                        this.currentAttribute.addContent(this.undecodedChunk.slice(firstpos, currentpos - firstpos), false);
                        firstpos = currentpos;
                    }
                    this.undecodedChunk.readerIndex(firstpos);
                    return;
                }
            }
            this.undecodedChunk.readerIndex(firstpos);
        } catch (SeekAheadNoBackArrayException e3) {
            parseBodyAttributesStandard();
        }
    }

    private void setFinalBuffer(ChannelBuffer buffer) throws ErrorDataDecoderException, IOException {
        this.currentAttribute.addContent(buffer, true);
        this.currentAttribute.setValue(decodeAttribute(this.currentAttribute.getChannelBuffer().toString(this.charset), this.charset));
        addHttpData(this.currentAttribute);
        this.currentAttribute = null;
    }

    private static String decodeAttribute(String s, Charset charset) throws ErrorDataDecoderException {
        if (s == null) {
            return "";
        }
        try {
            return URLDecoder.decode(s, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new ErrorDataDecoderException(charset.toString(), e);
        } catch (IllegalArgumentException e2) {
            throw new ErrorDataDecoderException("Bad string: '" + s + '\'', e2);
        }
    }

    public void cleanFiles() {
        this.factory.cleanRequestHttpDatas(this.request);
    }

    public void removeHttpDataFromClean(InterfaceHttpData data) {
        this.factory.removeHttpDataFromClean(this.request, data);
    }
}
