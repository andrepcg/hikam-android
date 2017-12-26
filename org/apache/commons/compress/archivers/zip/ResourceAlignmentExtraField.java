package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public class ResourceAlignmentExtraField implements ZipExtraField {
    private static final int ALLOW_METHOD_MESSAGE_CHANGE_FLAG = 32768;
    public static final int BASE_SIZE = 2;
    public static final ZipShort ID = new ZipShort(41246);
    private short alignment;
    private boolean allowMethodChange;
    private int padding;

    public ResourceAlignmentExtraField() {
        this.padding = 0;
    }

    public ResourceAlignmentExtraField(int alignment) {
        this(alignment, false);
    }

    public ResourceAlignmentExtraField(int alignment, boolean allowMethodChange) {
        this(alignment, allowMethodChange, 0);
    }

    public ResourceAlignmentExtraField(int alignment, boolean allowMethodChange, int padding) {
        this.padding = 0;
        if (alignment < 0 || alignment > 32767) {
            throw new IllegalArgumentException("Alignment must be between 0 and 0x7fff, was: " + alignment);
        }
        this.alignment = (short) alignment;
        this.allowMethodChange = allowMethodChange;
        this.padding = padding;
    }

    public short getAlignment() {
        return this.alignment;
    }

    public boolean allowMethodChange() {
        return this.allowMethodChange;
    }

    public ZipShort getHeaderId() {
        return ID;
    }

    public ZipShort getLocalFileDataLength() {
        return new ZipShort(this.padding + 2);
    }

    public ZipShort getCentralDirectoryLength() {
        return new ZipShort(2);
    }

    public byte[] getLocalFileDataData() {
        int i;
        byte[] content = new byte[(this.padding + 2)];
        short s = this.alignment;
        if (this.allowMethodChange) {
            i = 32768;
        } else {
            i = 0;
        }
        ZipShort.putShort(i | s, content, 0);
        return content;
    }

    public byte[] getCentralDirectoryData() {
        return ZipShort.getBytes((this.allowMethodChange ? 32768 : 0) | this.alignment);
    }

    public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
        parseFromCentralDirectoryData(buffer, offset, length);
        this.padding = length - 2;
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        if (length < 2) {
            throw new ZipException("Too short content for ResourceAlignmentExtraField (0xa11e): " + length);
        }
        int alignmentValue = ZipShort.getValue(buffer, offset);
        this.alignment = (short) (alignmentValue & 32767);
        this.allowMethodChange = (32768 & alignmentValue) != 0;
    }
}
