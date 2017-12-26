package org.jboss.netty.handler.codec.http.multipart;

import java.io.File;
import java.nio.charset.Charset;
import org.apache.http.protocol.HTTP;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

public class DiskFileUpload extends AbstractDiskHttpData implements FileUpload {
    public static String baseDirectory = null;
    public static boolean deleteOnExitTemporaryFile = true;
    public static final String postfix = ".tmp";
    public static final String prefix = "FUp_";
    private String contentTransferEncoding;
    private String contentType;
    private String filename;

    public DiskFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
        super(name, charset, size);
        setFilename(filename);
        setContentType(contentType);
        setContentTransferEncoding(contentTransferEncoding);
    }

    public HttpDataType getHttpDataType() {
        return HttpDataType.FileUpload;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        if (filename == null) {
            throw new NullPointerException(HttpPostBodyUtil.FILENAME);
        }
        this.filename = filename;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Attribute)) {
            return false;
        }
        return getName().equalsIgnoreCase(((Attribute) o).getName());
    }

    public int compareTo(InterfaceHttpData o) {
        if (o instanceof FileUpload) {
            return compareTo((FileUpload) o);
        }
        throw new ClassCastException("Cannot compare " + getHttpDataType() + " with " + o.getHttpDataType());
    }

    public int compareTo(FileUpload o) {
        int v = getName().compareToIgnoreCase(o.getName());
        return v != 0 ? v : v;
    }

    public void setContentType(String contentType) {
        if (contentType == null) {
            throw new NullPointerException("contentType");
        }
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getContentTransferEncoding() {
        return this.contentTransferEncoding;
    }

    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    public String toString() {
        return "Content-Disposition: form-data; name=\"" + getName() + "\"; " + HttpPostBodyUtil.FILENAME + "=\"" + this.filename + "\"\r\n" + "Content-Type" + ": " + this.contentType + (this.charset != null ? HTTP.CHARSET_PARAM + this.charset.name() + "\r\n" : "\r\n") + "Content-Length" + ": " + length() + "\r\n" + "Completed: " + isCompleted() + "\r\nIsInMemory: " + isInMemory() + "\r\nRealFile: " + this.file.getAbsolutePath() + " DefaultDeleteAfter: " + deleteOnExitTemporaryFile;
    }

    protected boolean deleteOnExit() {
        return deleteOnExitTemporaryFile;
    }

    protected String getBaseDirectory() {
        return baseDirectory;
    }

    protected String getDiskFilename() {
        return new File(this.filename).getName();
    }

    protected String getPostfix() {
        return postfix;
    }

    protected String getPrefix() {
        return prefix;
    }
}
