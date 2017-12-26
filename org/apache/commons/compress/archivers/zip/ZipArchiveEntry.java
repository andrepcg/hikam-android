package org.apache.commons.compress.archivers.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.EntryStreamOffsets;
import org.apache.commons.compress.archivers.zip.ExtraFieldUtils.UnparseableExtraField;

public class ZipArchiveEntry extends ZipEntry implements ArchiveEntry, EntryStreamOffsets {
    public static final int CRC_UNKNOWN = -1;
    private static final byte[] EMPTY = new byte[0];
    public static final int PLATFORM_FAT = 0;
    public static final int PLATFORM_UNIX = 3;
    private static final int SHORT_MASK = 65535;
    private static final int SHORT_SHIFT = 16;
    private static final ZipExtraField[] noExtraFields = new ZipExtraField[0];
    private int alignment;
    private long dataOffset;
    private long externalAttributes;
    private ZipExtraField[] extraFields;
    private GeneralPurposeBit gpb;
    private int internalAttributes;
    private boolean isStreamContiguous;
    private long localHeaderOffset;
    private int method;
    private String name;
    private int platform;
    private int rawFlag;
    private byte[] rawName;
    private long size;
    private UnparseableExtraFieldData unparseableExtra;
    private int versionMadeBy;
    private int versionRequired;

    public ZipArchiveEntry(String name) {
        super(name);
        this.method = -1;
        this.size = -1;
        this.internalAttributes = 0;
        this.platform = 0;
        this.externalAttributes = 0;
        this.alignment = 0;
        this.unparseableExtra = null;
        this.name = null;
        this.rawName = null;
        this.gpb = new GeneralPurposeBit();
        this.localHeaderOffset = -1;
        this.dataOffset = -1;
        this.isStreamContiguous = false;
        setName(name);
    }

    public ZipArchiveEntry(ZipEntry entry) throws ZipException {
        super(entry);
        this.method = -1;
        this.size = -1;
        this.internalAttributes = 0;
        this.platform = 0;
        this.externalAttributes = 0;
        this.alignment = 0;
        this.unparseableExtra = null;
        this.name = null;
        this.rawName = null;
        this.gpb = new GeneralPurposeBit();
        this.localHeaderOffset = -1;
        this.dataOffset = -1;
        this.isStreamContiguous = false;
        setName(entry.getName());
        byte[] extra = entry.getExtra();
        if (extra != null) {
            setExtraFields(ExtraFieldUtils.parse(extra, true, UnparseableExtraField.READ));
        } else {
            setExtra();
        }
        setMethod(entry.getMethod());
        this.size = entry.getSize();
    }

    public ZipArchiveEntry(ZipArchiveEntry entry) throws ZipException {
        GeneralPurposeBit generalPurposeBit;
        this((ZipEntry) entry);
        setInternalAttributes(entry.getInternalAttributes());
        setExternalAttributes(entry.getExternalAttributes());
        setExtraFields(getAllExtraFieldsNoCopy());
        setPlatform(entry.getPlatform());
        GeneralPurposeBit other = entry.getGeneralPurposeBit();
        if (other == null) {
            generalPurposeBit = null;
        } else {
            generalPurposeBit = (GeneralPurposeBit) other.clone();
        }
        setGeneralPurposeBit(generalPurposeBit);
    }

    protected ZipArchiveEntry() {
        this("");
    }

    public ZipArchiveEntry(File inputFile, String entryName) {
        if (inputFile.isDirectory() && !entryName.endsWith("/")) {
            entryName = entryName + "/";
        }
        this(entryName);
        if (inputFile.isFile()) {
            setSize(inputFile.length());
        }
        setTime(inputFile.lastModified());
    }

    public Object clone() {
        ZipArchiveEntry e = (ZipArchiveEntry) super.clone();
        e.setInternalAttributes(getInternalAttributes());
        e.setExternalAttributes(getExternalAttributes());
        e.setExtraFields(getAllExtraFieldsNoCopy());
        return e;
    }

    public int getMethod() {
        return this.method;
    }

    public void setMethod(int method) {
        if (method < 0) {
            throw new IllegalArgumentException("ZIP compression method can not be negative: " + method);
        }
        this.method = method;
    }

    public int getInternalAttributes() {
        return this.internalAttributes;
    }

    public void setInternalAttributes(int value) {
        this.internalAttributes = value;
    }

    public long getExternalAttributes() {
        return this.externalAttributes;
    }

    public void setExternalAttributes(long value) {
        this.externalAttributes = value;
    }

    public void setUnixMode(int mode) {
        int i = 0;
        int i2 = ((mode & 128) == 0 ? 1 : 0) | (mode << 16);
        if (isDirectory()) {
            i = 16;
        }
        setExternalAttributes((long) (i2 | i));
        this.platform = 3;
    }

    public int getUnixMode() {
        if (this.platform != 3) {
            return 0;
        }
        return (int) ((getExternalAttributes() >> 16) & 65535);
    }

    public boolean isUnixSymlink() {
        return (getUnixMode() & 61440) == 40960;
    }

    public int getPlatform() {
        return this.platform;
    }

    protected void setPlatform(int platform) {
        this.platform = platform;
    }

    protected int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment) {
        if (((alignment - 1) & alignment) != 0 || alignment > 65535) {
            throw new IllegalArgumentException("Invalid value for alignment, must be power of two and no bigger than 65535 but is " + alignment);
        }
        this.alignment = alignment;
    }

    public void setExtraFields(ZipExtraField[] fields) {
        List<ZipExtraField> newFields = new ArrayList();
        for (ZipExtraField field : fields) {
            if (field instanceof UnparseableExtraFieldData) {
                this.unparseableExtra = (UnparseableExtraFieldData) field;
            } else {
                newFields.add(field);
            }
        }
        this.extraFields = (ZipExtraField[]) newFields.toArray(new ZipExtraField[newFields.size()]);
        setExtra();
    }

    public ZipExtraField[] getExtraFields() {
        return getParseableExtraFields();
    }

    public ZipExtraField[] getExtraFields(boolean includeUnparseable) {
        if (includeUnparseable) {
            return getAllExtraFields();
        }
        return getParseableExtraFields();
    }

    private ZipExtraField[] getParseableExtraFieldsNoCopy() {
        if (this.extraFields == null) {
            return noExtraFields;
        }
        return this.extraFields;
    }

    private ZipExtraField[] getParseableExtraFields() {
        ZipExtraField[] parseableExtraFields = getParseableExtraFieldsNoCopy();
        return parseableExtraFields == this.extraFields ? copyOf(parseableExtraFields) : parseableExtraFields;
    }

    private ZipExtraField[] getAllExtraFieldsNoCopy() {
        if (this.extraFields == null) {
            return getUnparseableOnly();
        }
        return this.unparseableExtra != null ? getMergedFields() : this.extraFields;
    }

    private ZipExtraField[] copyOf(ZipExtraField[] src) {
        return copyOf(src, src.length);
    }

    private ZipExtraField[] copyOf(ZipExtraField[] src, int length) {
        ZipExtraField[] cpy = new ZipExtraField[length];
        System.arraycopy(src, 0, cpy, 0, Math.min(src.length, length));
        return cpy;
    }

    private ZipExtraField[] getMergedFields() {
        ZipExtraField[] zipExtraFields = copyOf(this.extraFields, this.extraFields.length + 1);
        zipExtraFields[this.extraFields.length] = this.unparseableExtra;
        return zipExtraFields;
    }

    private ZipExtraField[] getUnparseableOnly() {
        if (this.unparseableExtra == null) {
            return noExtraFields;
        }
        return new ZipExtraField[]{this.unparseableExtra};
    }

    private ZipExtraField[] getAllExtraFields() {
        ZipExtraField[] allExtraFieldsNoCopy = getAllExtraFieldsNoCopy();
        return allExtraFieldsNoCopy == this.extraFields ? copyOf(allExtraFieldsNoCopy) : allExtraFieldsNoCopy;
    }

    public void addExtraField(ZipExtraField ze) {
        if (ze instanceof UnparseableExtraFieldData) {
            this.unparseableExtra = (UnparseableExtraFieldData) ze;
        } else if (this.extraFields == null) {
            this.extraFields = new ZipExtraField[]{ze};
        } else {
            if (getExtraField(ze.getHeaderId()) != null) {
                removeExtraField(ze.getHeaderId());
            }
            ZipExtraField[] zipExtraFields = copyOf(this.extraFields, this.extraFields.length + 1);
            zipExtraFields[zipExtraFields.length - 1] = ze;
            this.extraFields = zipExtraFields;
        }
        setExtra();
    }

    public void addAsFirstExtraField(ZipExtraField ze) {
        if (ze instanceof UnparseableExtraFieldData) {
            this.unparseableExtra = (UnparseableExtraFieldData) ze;
        } else {
            int newLen;
            if (getExtraField(ze.getHeaderId()) != null) {
                removeExtraField(ze.getHeaderId());
            }
            ZipExtraField[] copy = this.extraFields;
            if (this.extraFields != null) {
                newLen = this.extraFields.length + 1;
            } else {
                newLen = 1;
            }
            this.extraFields = new ZipExtraField[newLen];
            this.extraFields[0] = ze;
            if (copy != null) {
                System.arraycopy(copy, 0, this.extraFields, 1, this.extraFields.length - 1);
            }
        }
        setExtra();
    }

    public void removeExtraField(ZipShort type) {
        if (this.extraFields == null) {
            throw new NoSuchElementException();
        }
        List<ZipExtraField> newResult = new ArrayList();
        for (ZipExtraField extraField : this.extraFields) {
            if (!type.equals(extraField.getHeaderId())) {
                newResult.add(extraField);
            }
        }
        if (this.extraFields.length == newResult.size()) {
            throw new NoSuchElementException();
        }
        this.extraFields = (ZipExtraField[]) newResult.toArray(new ZipExtraField[newResult.size()]);
        setExtra();
    }

    public void removeUnparseableExtraFieldData() {
        if (this.unparseableExtra == null) {
            throw new NoSuchElementException();
        }
        this.unparseableExtra = null;
        setExtra();
    }

    public ZipExtraField getExtraField(ZipShort type) {
        if (this.extraFields != null) {
            for (ZipExtraField extraField : this.extraFields) {
                if (type.equals(extraField.getHeaderId())) {
                    return extraField;
                }
            }
        }
        return null;
    }

    public UnparseableExtraFieldData getUnparseableExtraFieldData() {
        return this.unparseableExtra;
    }

    public void setExtra(byte[] extra) throws RuntimeException {
        try {
            mergeExtraFields(ExtraFieldUtils.parse(extra, true, UnparseableExtraField.READ), true);
        } catch (ZipException e) {
            throw new RuntimeException("Error parsing extra fields for entry: " + getName() + " - " + e.getMessage(), e);
        }
    }

    protected void setExtra() {
        super.setExtra(ExtraFieldUtils.mergeLocalFileDataData(getAllExtraFieldsNoCopy()));
    }

    public void setCentralDirectoryExtra(byte[] b) {
        try {
            mergeExtraFields(ExtraFieldUtils.parse(b, false, UnparseableExtraField.READ), false);
        } catch (ZipException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public byte[] getLocalFileDataExtra() {
        byte[] extra = getExtra();
        return extra != null ? extra : EMPTY;
    }

    public byte[] getCentralDirectoryExtra() {
        return ExtraFieldUtils.mergeCentralDirectoryData(getAllExtraFieldsNoCopy());
    }

    public String getName() {
        return this.name == null ? super.getName() : this.name;
    }

    public boolean isDirectory() {
        return getName().endsWith("/");
    }

    protected void setName(String name) {
        if (!(name == null || getPlatform() != 0 || name.contains("/"))) {
            name = name.replace('\\', '/');
        }
        this.name = name;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("invalid entry size");
        }
        this.size = size;
    }

    protected void setName(String name, byte[] rawName) {
        setName(name);
        this.rawName = rawName;
    }

    public byte[] getRawName() {
        if (this.rawName == null) {
            return null;
        }
        byte[] b = new byte[this.rawName.length];
        System.arraycopy(this.rawName, 0, b, 0, this.rawName.length);
        return b;
    }

    protected long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }

    protected void setLocalHeaderOffset(long localHeaderOffset) {
        this.localHeaderOffset = localHeaderOffset;
    }

    public long getDataOffset() {
        return this.dataOffset;
    }

    protected void setDataOffset(long dataOffset) {
        this.dataOffset = dataOffset;
    }

    public boolean isStreamContiguous() {
        return this.isStreamContiguous;
    }

    protected void setStreamContiguous(boolean isStreamContiguous) {
        this.isStreamContiguous = isStreamContiguous;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public GeneralPurposeBit getGeneralPurposeBit() {
        return this.gpb;
    }

    public void setGeneralPurposeBit(GeneralPurposeBit b) {
        this.gpb = b;
    }

    private void mergeExtraFields(ZipExtraField[] f, boolean local) throws ZipException {
        if (this.extraFields == null) {
            setExtraFields(f);
            return;
        }
        for (ZipExtraField element : f) {
            ZipExtraField existing;
            if (element instanceof UnparseableExtraFieldData) {
                existing = this.unparseableExtra;
            } else {
                existing = getExtraField(element.getHeaderId());
            }
            if (existing == null) {
                addExtraField(element);
            } else if (local) {
                b = element.getLocalFileDataData();
                existing.parseFromLocalFileData(b, 0, b.length);
            } else {
                b = element.getCentralDirectoryData();
                existing.parseFromCentralDirectoryData(b, 0, b.length);
            }
        }
        setExtra();
    }

    public Date getLastModifiedDate() {
        return new Date(getTime());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ZipArchiveEntry other = (ZipArchiveEntry) obj;
        String myName = getName();
        String otherName = other.getName();
        if (myName == null) {
            if (otherName != null) {
                return false;
            }
        } else if (!myName.equals(otherName)) {
            return false;
        }
        String myComment = getComment();
        String otherComment = other.getComment();
        if (myComment == null) {
            myComment = "";
        }
        if (otherComment == null) {
            otherComment = "";
        }
        if (!(getTime() == other.getTime() && myComment.equals(otherComment) && getInternalAttributes() == other.getInternalAttributes() && getPlatform() == other.getPlatform() && getExternalAttributes() == other.getExternalAttributes() && getMethod() == other.getMethod() && getSize() == other.getSize() && getCrc() == other.getCrc() && getCompressedSize() == other.getCompressedSize() && Arrays.equals(getCentralDirectoryExtra(), other.getCentralDirectoryExtra()) && Arrays.equals(getLocalFileDataExtra(), other.getLocalFileDataExtra()) && this.localHeaderOffset == other.localHeaderOffset && this.dataOffset == other.dataOffset && this.gpb.equals(other.gpb))) {
            z = false;
        }
        return z;
    }

    public void setVersionMadeBy(int versionMadeBy) {
        this.versionMadeBy = versionMadeBy;
    }

    public void setVersionRequired(int versionRequired) {
        this.versionRequired = versionRequired;
    }

    public int getVersionRequired() {
        return this.versionRequired;
    }

    public int getVersionMadeBy() {
        return this.versionMadeBy;
    }

    public int getRawFlag() {
        return this.rawFlag;
    }

    public void setRawFlag(int rawFlag) {
        this.rawFlag = rawFlag;
    }
}
