package org.apache.commons.compress.utils;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public final class IOUtils {
    private static final int COPY_BUF_SIZE = 8024;
    private static final byte[] SKIP_BUF = new byte[4096];
    private static final int SKIP_BUF_SIZE = 4096;

    private IOUtils() {
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, COPY_BUF_SIZE);
    }

    public static long copy(InputStream input, OutputStream output, int buffersize) throws IOException {
        byte[] buffer = new byte[buffersize];
        long count = 0;
        while (true) {
            int n = input.read(buffer);
            if (-1 == n) {
                return count;
            }
            output.write(buffer, 0, n);
            count += (long) n;
        }
    }

    public static long skip(InputStream input, long numToSkip) throws IOException {
        long available = numToSkip;
        while (numToSkip > 0) {
            long skipped = input.skip(numToSkip);
            if (skipped == 0) {
                break;
            }
            numToSkip -= skipped;
        }
        while (numToSkip > 0) {
            int read = readFully(input, SKIP_BUF, 0, (int) Math.min(numToSkip, PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM));
            if (read < 1) {
                break;
            }
            numToSkip -= (long) read;
        }
        return available - numToSkip;
    }

    public static int readFully(InputStream input, byte[] b) throws IOException {
        return readFully(input, b, 0, b.length);
    }

    public static int readFully(InputStream input, byte[] b, int offset, int len) throws IOException {
        if (len < 0 || offset < 0 || len + offset > b.length) {
            throw new IndexOutOfBoundsException();
        }
        int count = 0;
        while (count != len) {
            int x = input.read(b, offset + count, len - count);
            if (x == -1) {
                break;
            }
            count += x;
        }
        return count;
    }

    public static void readFully(ReadableByteChannel channel, ByteBuffer b) throws IOException {
        int expectedLength = b.remaining();
        int read = 0;
        while (read < expectedLength) {
            int readNow = channel.read(b);
            if (readNow <= 0) {
                break;
            }
            read += readNow;
        }
        if (read < expectedLength) {
            throw new EOFException();
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }
}
