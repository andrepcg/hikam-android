package org.jboss.netty.handler.codec.http.multipart;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class DefaultHttpDataFactory implements HttpDataFactory {
    public static final long MAXSIZE = -1;
    public static final long MINSIZE = 16384;
    private final boolean checkSize;
    private long maxSize;
    private long minSize;
    private final Map<HttpRequest, List<HttpData>> requestFileDeleteMap;
    private final boolean useDisk;

    public DefaultHttpDataFactory() {
        this.maxSize = -1;
        this.requestFileDeleteMap = new ConcurrentHashMap();
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = 16384;
    }

    public DefaultHttpDataFactory(boolean useDisk) {
        this.maxSize = -1;
        this.requestFileDeleteMap = new ConcurrentHashMap();
        this.useDisk = useDisk;
        this.checkSize = false;
    }

    public DefaultHttpDataFactory(long minSize) {
        this.maxSize = -1;
        this.requestFileDeleteMap = new ConcurrentHashMap();
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = minSize;
    }

    public void setMaxLimit(long max) {
        this.maxSize = max;
    }

    private List<HttpData> getList(HttpRequest request) {
        List<HttpData> list = (List) this.requestFileDeleteMap.get(request);
        if (list != null) {
            return list;
        }
        list = new ArrayList();
        this.requestFileDeleteMap.put(request, list);
        return list;
    }

    public Attribute createAttribute(HttpRequest request, String name) {
        Attribute attribute;
        if (this.useDisk) {
            attribute = new DiskAttribute(name);
            attribute.setMaxSize(this.maxSize);
            getList(request).add(attribute);
            return attribute;
        } else if (this.checkSize) {
            attribute = new MixedAttribute(name, this.minSize);
            attribute.setMaxSize(this.maxSize);
            getList(request).add(attribute);
            return attribute;
        } else {
            attribute = new MemoryAttribute(name);
            attribute.setMaxSize(this.maxSize);
            return attribute;
        }
    }

    private void checkHttpDataSize(HttpData data) {
        try {
            data.checkSize(data.length());
        } catch (IOException e) {
            throw new IllegalArgumentException("Attribute bigger than maxSize allowed");
        }
    }

    public Attribute createAttribute(HttpRequest request, String name, String value) {
        Attribute attribute;
        if (this.useDisk) {
            try {
                attribute = new DiskAttribute(name, value);
                attribute.setMaxSize(this.maxSize);
            } catch (IOException e) {
                attribute = new MixedAttribute(name, value, this.minSize);
                attribute.setMaxSize(this.maxSize);
            }
            checkHttpDataSize(attribute);
            getList(request).add(attribute);
            return attribute;
        } else if (this.checkSize) {
            attribute = new MixedAttribute(name, value, this.minSize);
            attribute.setMaxSize(this.maxSize);
            checkHttpDataSize(attribute);
            getList(request).add(attribute);
            return attribute;
        } else {
            try {
                attribute = new MemoryAttribute(name, value);
                attribute.setMaxSize(this.maxSize);
                checkHttpDataSize(attribute);
                return attribute;
            } catch (IOException e2) {
                throw new IllegalArgumentException(e2);
            }
        }
    }

    public FileUpload createFileUpload(HttpRequest request, String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
        FileUpload fileUpload;
        if (this.useDisk) {
            fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
            fileUpload.setMaxSize(this.maxSize);
            checkHttpDataSize(fileUpload);
            getList(request).add(fileUpload);
            return fileUpload;
        } else if (this.checkSize) {
            fileUpload = new MixedFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, this.minSize);
            fileUpload.setMaxSize(this.maxSize);
            checkHttpDataSize(fileUpload);
            getList(request).add(fileUpload);
            return fileUpload;
        } else {
            fileUpload = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
            fileUpload.setMaxSize(this.maxSize);
            checkHttpDataSize(fileUpload);
            return fileUpload;
        }
    }

    public void removeHttpDataFromClean(HttpRequest request, InterfaceHttpData data) {
        if (data instanceof HttpData) {
            getList(request).remove(data);
        }
    }

    public void cleanRequestHttpDatas(HttpRequest request) {
        List<HttpData> fileToDelete = (List) this.requestFileDeleteMap.remove(request);
        if (fileToDelete != null) {
            for (HttpData data : fileToDelete) {
                data.delete();
            }
            fileToDelete.clear();
        }
    }

    public void cleanAllHttpDatas() {
        for (HttpRequest request : this.requestFileDeleteMap.keySet()) {
            List<HttpData> fileToDelete = (List) this.requestFileDeleteMap.get(request);
            if (fileToDelete != null) {
                for (HttpData data : fileToDelete) {
                    data.delete();
                }
                fileToDelete.clear();
            }
            this.requestFileDeleteMap.remove(request);
        }
    }
}
