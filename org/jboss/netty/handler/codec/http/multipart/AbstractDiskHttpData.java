package org.jboss.netty.handler.codec.http.multipart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpConstants;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public abstract class AbstractDiskHttpData extends AbstractHttpData {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
    protected File file;
    private FileChannel fileChannel;
    private boolean isRenamed;

    protected abstract boolean deleteOnExit();

    protected abstract String getBaseDirectory();

    protected abstract String getDiskFilename();

    protected abstract String getPostfix();

    protected abstract String getPrefix();

    protected AbstractDiskHttpData(String name, Charset charset, long size) {
        super(name, charset, size);
    }

    private File tempFile() throws IOException {
        String newpostfix;
        File tmpFile;
        String diskFilename = getDiskFilename();
        if (diskFilename != null) {
            newpostfix = '_' + diskFilename;
        } else {
            newpostfix = getPostfix();
        }
        if (getBaseDirectory() == null) {
            tmpFile = File.createTempFile(getPrefix(), newpostfix);
        } else {
            tmpFile = File.createTempFile(getPrefix(), newpostfix, new File(getBaseDirectory()));
        }
        if (deleteOnExit()) {
            tmpFile.deleteOnExit();
        }
        return tmpFile;
    }

    public void setContent(ChannelBuffer buffer) throws IOException {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.size = (long) buffer.readableBytes();
        checkSize(this.size);
        if (this.definedSize <= 0 || this.definedSize >= this.size) {
            if (this.file == null) {
                this.file = tempFile();
            }
            if (buffer.readableBytes() == 0) {
                this.file.createNewFile();
                return;
            }
            FileOutputStream outputStream = new FileOutputStream(this.file);
            FileChannel localfileChannel = outputStream.getChannel();
            ByteBuffer byteBuffer = buffer.toByteBuffer();
            int written = 0;
            while (((long) written) < this.size) {
                written += localfileChannel.write(byteBuffer);
            }
            buffer.readerIndex(buffer.readerIndex() + written);
            localfileChannel.force(false);
            localfileChannel.close();
            outputStream.close();
            this.completed = true;
            return;
        }
        throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
    }

    public void addContent(ChannelBuffer buffer, boolean last) throws IOException {
        if (buffer != null) {
            int localsize = buffer.readableBytes();
            checkSize(this.size + ((long) localsize));
            if (this.definedSize <= 0 || this.definedSize >= this.size + ((long) localsize)) {
                ByteBuffer byteBuffer = buffer.toByteBuffer();
                int written = 0;
                if (this.file == null) {
                    this.file = tempFile();
                }
                if (this.fileChannel == null) {
                    this.fileChannel = new FileOutputStream(this.file).getChannel();
                }
                while (written < localsize) {
                    written += this.fileChannel.write(byteBuffer);
                }
                this.size += (long) localsize;
                buffer.readerIndex(buffer.readerIndex() + written);
            } else {
                throw new IOException("Out of size: " + (this.size + ((long) localsize)) + " > " + this.definedSize);
            }
        }
        if (last) {
            if (this.file == null) {
                this.file = tempFile();
            }
            if (this.fileChannel == null) {
                this.fileChannel = new FileOutputStream(this.file).getChannel();
            }
            this.fileChannel.force(false);
            this.fileChannel.close();
            this.fileChannel = null;
            this.completed = true;
        } else if (buffer == null) {
            throw new NullPointerException("buffer");
        }
    }

    public void setContent(File file) throws IOException {
        if (this.file != null) {
            delete();
        }
        this.file = file;
        this.size = file.length();
        checkSize(this.size);
        this.isRenamed = true;
        this.completed = true;
    }

    public void setContent(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream");
        }
        if (this.file != null) {
            delete();
        }
        this.file = tempFile();
        FileChannel localfileChannel = new FileOutputStream(this.file).getChannel();
        byte[] bytes = new byte[16384];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int read = inputStream.read(bytes);
        int written = 0;
        while (read > 0) {
            byteBuffer.position(read).flip();
            written += localfileChannel.write(byteBuffer);
            checkSize((long) written);
            read = inputStream.read(bytes);
        }
        localfileChannel.force(false);
        localfileChannel.close();
        this.size = (long) written;
        if (this.definedSize <= 0 || this.definedSize >= this.size) {
            this.isRenamed = true;
            this.completed = true;
            return;
        }
        this.file.delete();
        this.file = null;
        throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
    }

    public void delete() {
        if (this.fileChannel != null) {
            try {
                this.fileChannel.force(false);
                this.fileChannel.close();
            } catch (IOException e) {
                logger.warn("Failed to close a file.", e);
            }
            this.fileChannel = null;
        }
        if (!this.isRenamed) {
            if (this.file != null && this.file.exists()) {
                this.file.delete();
            }
            this.file = null;
        }
    }

    public byte[] get() throws IOException {
        if (this.file == null) {
            return new byte[0];
        }
        return readFrom(this.file);
    }

    public ChannelBuffer getChannelBuffer() throws IOException {
        if (this.file == null) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        return ChannelBuffers.wrappedBuffer(readFrom(this.file));
    }

    public ChannelBuffer getChunk(int length) throws IOException {
        if (this.file == null || length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        if (this.fileChannel == null) {
            this.fileChannel = new FileInputStream(this.file).getChannel();
        }
        int read = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        while (read < length) {
            int readnow = this.fileChannel.read(byteBuffer);
            if (readnow == -1) {
                this.fileChannel.close();
                this.fileChannel = null;
                break;
            }
            read += readnow;
        }
        if (read == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        byteBuffer.flip();
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(byteBuffer);
        buffer.readerIndex(0);
        buffer.writerIndex(read);
        return buffer;
    }

    public String getString() throws IOException {
        return getString(HttpConstants.DEFAULT_CHARSET);
    }

    public String getString(Charset encoding) throws IOException {
        if (this.file == null) {
            return "";
        }
        if (encoding == null) {
            return new String(readFrom(this.file), HttpConstants.DEFAULT_CHARSET.name());
        }
        return new String(readFrom(this.file), encoding.name());
    }

    public boolean isInMemory() {
        return false;
    }

    public boolean renameTo(File dest) throws IOException {
        if (dest == null) {
            throw new NullPointerException("dest");
        } else if (this.file == null) {
            throw new IOException("No file defined so cannot be renamed");
        } else if (this.file.renameTo(dest)) {
            this.file = dest;
            this.isRenamed = true;
            return true;
        } else {
            FileInputStream inputStream = new FileInputStream(this.file);
            FileOutputStream outputStream = new FileOutputStream(dest);
            FileChannel in = inputStream.getChannel();
            FileChannel out = outputStream.getChannel();
            long chunkSize = 8196;
            long position = 0;
            while (position < this.size) {
                if (chunkSize < this.size - position) {
                    chunkSize = this.size - position;
                }
                position += in.transferTo(position, chunkSize, out);
            }
            in.close();
            out.close();
            if (position == this.size) {
                this.file.delete();
                this.file = dest;
                this.isRenamed = true;
                return true;
            }
            dest.delete();
            return false;
        }
    }

    private static byte[] readFrom(File src) throws IOException {
        long srcsize = src.length();
        if (srcsize > 2147483647L) {
            throw new IllegalArgumentException("File too big to be loaded in memory");
        }
        FileChannel fileChannel = new FileInputStream(src).getChannel();
        byte[] array = new byte[((int) srcsize)];
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        for (int read = 0; ((long) read) < srcsize; read += fileChannel.read(byteBuffer)) {
        }
        fileChannel.close();
        return array;
    }

    public File getFile() throws IOException {
        return this.file;
    }
}
