package org.apache.commons.compress.parallel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;

public class FileBasedScatterGatherBackingStore implements ScatterGatherBackingStore {
    private boolean closed;
    private final OutputStream os;
    private final File target;

    public FileBasedScatterGatherBackingStore(File target) throws FileNotFoundException {
        this.target = target;
        try {
            this.os = Files.newOutputStream(target.toPath(), new OpenOption[0]);
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(this.target.toPath(), new OpenOption[0]);
    }

    public void closeForWriting() throws IOException {
        if (!this.closed) {
            this.os.close();
            this.closed = true;
        }
    }

    public void writeOut(byte[] data, int offset, int length) throws IOException {
        this.os.write(data, offset, length);
    }

    public void close() throws IOException {
        closeForWriting();
        this.target.delete();
    }
}
