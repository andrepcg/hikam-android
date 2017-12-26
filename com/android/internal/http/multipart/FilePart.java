package com.android.internal.http.multipart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class FilePart extends PartBase {
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_TRANSFER_ENCODING = "binary";
    protected static final String FILE_NAME = "; filename=";

    public FilePart(String name, PartSource partSource, String contentType, String charset) {
        super((String) null, (String) null, (String) null, (String) null);
        throw new RuntimeException("Stub!");
    }

    public FilePart(String name, PartSource partSource) {
        super((String) null, (String) null, (String) null, (String) null);
        throw new RuntimeException("Stub!");
    }

    public FilePart(String name, File file) throws FileNotFoundException {
        super((String) null, (String) null, (String) null, (String) null);
        throw new RuntimeException("Stub!");
    }

    public FilePart(String name, File file, String contentType, String charset) throws FileNotFoundException {
        super((String) null, (String) null, (String) null, (String) null);
        throw new RuntimeException("Stub!");
    }

    public FilePart(String name, String fileName, File file) throws FileNotFoundException {
        super((String) null, (String) null, (String) null, (String) null);
        throw new RuntimeException("Stub!");
    }

    public FilePart(String name, String fileName, File file, String contentType, String charset) throws FileNotFoundException {
        super((String) null, (String) null, (String) null, (String) null);
        throw new RuntimeException("Stub!");
    }

    protected void sendDispositionHeader(OutputStream out) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void sendData(OutputStream out) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected PartSource getSource() {
        throw new RuntimeException("Stub!");
    }

    protected long lengthOfData() {
        throw new RuntimeException("Stub!");
    }
}
