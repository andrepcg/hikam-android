package org.apache.commons.compress.archivers.ar;

import java.io.File;
import java.util.Date;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class ArArchiveEntry implements ArchiveEntry {
    private static final int DEFAULT_MODE = 33188;
    public static final String HEADER = "!<arch>\n";
    public static final String TRAILER = "`\n";
    private final int groupId;
    private final long lastModified;
    private final long length;
    private final int mode;
    private final String name;
    private final int userId;

    public ArArchiveEntry(String name, long length) {
        this(name, length, 0, 0, 33188, System.currentTimeMillis() / 1000);
    }

    public ArArchiveEntry(String name, long length, int userId, int groupId, int mode, long lastModified) {
        this.name = name;
        this.length = length;
        this.userId = userId;
        this.groupId = groupId;
        this.mode = mode;
        this.lastModified = lastModified;
    }

    public ArArchiveEntry(File inputFile, String entryName) {
        this(entryName, inputFile.isFile() ? inputFile.length() : 0, 0, 0, 33188, inputFile.lastModified() / 1000);
    }

    public long getSize() {
        return getLength();
    }

    public String getName() {
        return this.name;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getMode() {
        return this.mode;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public Date getLastModifiedDate() {
        return new Date(1000 * getLastModified());
    }

    public long getLength() {
        return this.length;
    }

    public boolean isDirectory() {
        return false;
    }

    public int hashCode() {
        return (this.name == null ? 0 : this.name.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ArArchiveEntry other = (ArArchiveEntry) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
            return true;
        } else if (this.name.equals(other.name)) {
            return true;
        } else {
            return false;
        }
    }
}
