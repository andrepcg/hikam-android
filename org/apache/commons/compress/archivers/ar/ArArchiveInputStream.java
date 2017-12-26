package org.apache.commons.compress.archivers.ar;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.IOUtils;

public class ArArchiveInputStream extends ArchiveInputStream {
    private static final String BSD_LONGNAME_PATTERN = "^#1/\\d+";
    static final String BSD_LONGNAME_PREFIX = "#1/";
    private static final int BSD_LONGNAME_PREFIX_LEN = BSD_LONGNAME_PREFIX.length();
    private static final String GNU_LONGNAME_PATTERN = "^/\\d+";
    private static final String GNU_STRING_TABLE_NAME = "//";
    private boolean closed;
    private ArArchiveEntry currentEntry = null;
    private long entryOffset = -1;
    private final byte[] fileModeBuf = new byte[8];
    private final byte[] idBuf = new byte[6];
    private final InputStream input;
    private final byte[] lastModifiedBuf = new byte[12];
    private final byte[] lengthBuf = new byte[10];
    private final byte[] nameBuf = new byte[16];
    private byte[] namebuffer = null;
    private long offset = 0;

    public ArArchiveInputStream(InputStream pInput) {
        this.input = pInput;
        this.closed = false;
    }

    public ArArchiveEntry getNextArEntry() throws IOException {
        byte[] expected;
        byte[] realized;
        int i;
        if (this.currentEntry != null) {
            IOUtils.skip(this, (this.entryOffset + this.currentEntry.getLength()) - this.offset);
            this.currentEntry = null;
        }
        if (this.offset == 0) {
            expected = ArchiveUtils.toAsciiBytes(ArArchiveEntry.HEADER);
            realized = new byte[expected.length];
            if (IOUtils.readFully((InputStream) this, realized) != expected.length) {
                throw new IOException("failed to read header. Occured at byte: " + getBytesRead());
            }
            for (i = 0; i < expected.length; i++) {
                if (expected[i] != realized[i]) {
                    throw new IOException("invalid header " + ArchiveUtils.toAsciiString(realized));
                }
            }
        }
        if (this.offset % 2 != 0 && read() < 0) {
            return null;
        }
        if (this.input.available() == 0) {
            return null;
        }
        IOUtils.readFully((InputStream) this, this.nameBuf);
        IOUtils.readFully((InputStream) this, this.lastModifiedBuf);
        IOUtils.readFully((InputStream) this, this.idBuf);
        int userId = asInt(this.idBuf, true);
        IOUtils.readFully((InputStream) this, this.idBuf);
        IOUtils.readFully((InputStream) this, this.fileModeBuf);
        IOUtils.readFully((InputStream) this, this.lengthBuf);
        expected = ArchiveUtils.toAsciiBytes(ArArchiveEntry.TRAILER);
        realized = new byte[expected.length];
        if (IOUtils.readFully((InputStream) this, realized) != expected.length) {
            throw new IOException("failed to read entry trailer. Occured at byte: " + getBytesRead());
        }
        for (i = 0; i < expected.length; i++) {
            if (expected[i] != realized[i]) {
                throw new IOException("invalid entry trailer. not read the content? Occured at byte: " + getBytesRead());
            }
        }
        this.entryOffset = this.offset;
        String temp = ArchiveUtils.toAsciiString(this.nameBuf).trim();
        if (isGNUStringTable(temp)) {
            this.currentEntry = readGNUStringTable(this.lengthBuf);
            return getNextArEntry();
        }
        long len = asLong(this.lengthBuf);
        if (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        } else if (isGNULongName(temp)) {
            temp = getExtendedName(Integer.parseInt(temp.substring(1)));
        } else if (isBSDLongName(temp)) {
            temp = getBSDLongName(temp);
            int nameLen = temp.length();
            len -= (long) nameLen;
            this.entryOffset += (long) nameLen;
        }
        this.currentEntry = new ArArchiveEntry(temp, len, userId, asInt(this.idBuf, true), asInt(this.fileModeBuf, 8), asLong(this.lastModifiedBuf));
        return this.currentEntry;
    }

    private String getExtendedName(int offset) throws IOException {
        if (this.namebuffer == null) {
            throw new IOException("Cannot process GNU long filename as no // record was found");
        }
        int i = offset;
        while (i < this.namebuffer.length) {
            if (this.namebuffer[i] == (byte) 10 || this.namebuffer[i] == (byte) 0) {
                if (this.namebuffer[i - 1] == (byte) 47) {
                    i--;
                }
                return ArchiveUtils.toAsciiString(this.namebuffer, offset, i - offset);
            }
            i++;
        }
        throw new IOException("Failed to read entry: " + offset);
    }

    private long asLong(byte[] byteArray) {
        return Long.parseLong(ArchiveUtils.toAsciiString(byteArray).trim());
    }

    private int asInt(byte[] byteArray) {
        return asInt(byteArray, 10, false);
    }

    private int asInt(byte[] byteArray, boolean treatBlankAsZero) {
        return asInt(byteArray, 10, treatBlankAsZero);
    }

    private int asInt(byte[] byteArray, int base) {
        return asInt(byteArray, base, false);
    }

    private int asInt(byte[] byteArray, int base, boolean treatBlankAsZero) {
        String string = ArchiveUtils.toAsciiString(byteArray).trim();
        if (string.length() == 0 && treatBlankAsZero) {
            return 0;
        }
        return Integer.parseInt(string, base);
    }

    public ArchiveEntry getNextEntry() throws IOException {
        return getNextArEntry();
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.input.close();
        }
        this.currentEntry = null;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int toRead = len;
        if (this.currentEntry != null) {
            long entryEnd = this.entryOffset + this.currentEntry.getLength();
            if (len <= 0 || entryEnd <= this.offset) {
                return -1;
            }
            toRead = (int) Math.min((long) len, entryEnd - this.offset);
        }
        int ret = this.input.read(b, off, toRead);
        count(ret);
        this.offset = (ret > 0 ? (long) ret : 0) + this.offset;
        return ret;
    }

    public static boolean matches(byte[] signature, int length) {
        if (length >= 8 && signature[0] == (byte) 33 && signature[1] == (byte) 60 && signature[2] == (byte) 97 && signature[3] == (byte) 114 && signature[4] == (byte) 99 && signature[5] == (byte) 104 && signature[6] == (byte) 62 && signature[7] == (byte) 10) {
            return true;
        }
        return false;
    }

    private static boolean isBSDLongName(String name) {
        return name != null && name.matches(BSD_LONGNAME_PATTERN);
    }

    private String getBSDLongName(String bsdLongName) throws IOException {
        int nameLen = Integer.parseInt(bsdLongName.substring(BSD_LONGNAME_PREFIX_LEN));
        byte[] name = new byte[nameLen];
        if (IOUtils.readFully((InputStream) this, name) == nameLen) {
            return ArchiveUtils.toAsciiString(name);
        }
        throw new EOFException();
    }

    private static boolean isGNUStringTable(String name) {
        return GNU_STRING_TABLE_NAME.equals(name);
    }

    private ArArchiveEntry readGNUStringTable(byte[] length) throws IOException {
        int bufflen = asInt(length);
        this.namebuffer = new byte[bufflen];
        int read = IOUtils.readFully(this, this.namebuffer, 0, bufflen);
        if (read == bufflen) {
            return new ArArchiveEntry(GNU_STRING_TABLE_NAME, (long) bufflen);
        }
        throw new IOException("Failed to read complete // record: expected=" + bufflen + " read=" + read);
    }

    private boolean isGNULongName(String name) {
        return name != null && name.matches(GNU_LONGNAME_PATTERN);
    }
}
