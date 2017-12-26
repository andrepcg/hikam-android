package org.jboss.netty.channel;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class DefaultFileRegion implements FileRegion {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
    private final long count;
    private final FileChannel file;
    private final long position;
    private final boolean releaseAfterTransfer;

    public DefaultFileRegion(FileChannel file, long position, long count) {
        this(file, position, count, false);
    }

    public DefaultFileRegion(FileChannel file, long position, long count, boolean releaseAfterTransfer) {
        this.file = file;
        this.position = position;
        this.count = count;
        this.releaseAfterTransfer = releaseAfterTransfer;
    }

    public long getPosition() {
        return this.position;
    }

    public long getCount() {
        return this.count;
    }

    public boolean releaseAfterTransfer() {
        return this.releaseAfterTransfer;
    }

    public long transferTo(WritableByteChannel target, long position) throws IOException {
        long count = this.count - position;
        if (count < 0 || position < 0) {
            throw new IllegalArgumentException("position out of range: " + position + " (expected: 0 - " + (this.count - 1) + ')');
        } else if (count == 0) {
            return 0;
        } else {
            return this.file.transferTo(this.position + position, count, target);
        }
    }

    public void releaseExternalResources() {
        try {
            this.file.close();
        } catch (IOException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to close a file.", e);
            }
        }
    }
}
