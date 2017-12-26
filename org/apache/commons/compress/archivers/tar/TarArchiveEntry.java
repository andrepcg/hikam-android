package org.apache.commons.compress.archivers.tar;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.jboss.netty.handler.codec.http.HttpConstants;

public class TarArchiveEntry implements ArchiveEntry, TarConstants {
    public static final int DEFAULT_DIR_MODE = 16877;
    public static final int DEFAULT_FILE_MODE = 33188;
    private static final TarArchiveEntry[] EMPTY_TAR_ARCHIVE_ENTRIES = new TarArchiveEntry[0];
    public static final int MAX_NAMELEN = 31;
    public static final int MILLIS_PER_SECOND = 1000;
    private boolean checkSumOK;
    private int devMajor;
    private int devMinor;
    private final File file;
    private long groupId;
    private String groupName;
    private boolean isExtended;
    private byte linkFlag;
    private String linkName;
    private String magic;
    private long modTime;
    private int mode;
    private String name;
    private boolean paxGNUSparse;
    private boolean preserveLeadingSlashes;
    private long realSize;
    private long size;
    private boolean starSparse;
    private long userId;
    private String userName;
    private String version;

    private TarArchiveEntry() {
        this.name = "";
        this.userId = 0;
        this.groupId = 0;
        this.size = 0;
        this.linkName = "";
        this.magic = "ustar\u0000";
        this.version = TarConstants.VERSION_POSIX;
        this.groupName = "";
        this.devMajor = 0;
        this.devMinor = 0;
        String user = System.getProperty("user.name", "");
        if (user.length() > 31) {
            user = user.substring(0, 31);
        }
        this.userName = user;
        this.file = null;
    }

    public TarArchiveEntry(String name) {
        this(name, false);
    }

    public TarArchiveEntry(String name, boolean preserveLeadingSlashes) {
        this();
        this.preserveLeadingSlashes = preserveLeadingSlashes;
        name = normalizeFileName(name, preserveLeadingSlashes);
        boolean isDir = name.endsWith("/");
        this.name = name;
        this.mode = isDir ? DEFAULT_DIR_MODE : DEFAULT_FILE_MODE;
        this.linkFlag = isDir ? TarConstants.LF_DIR : TarConstants.LF_NORMAL;
        this.modTime = new Date().getTime() / 1000;
        this.userName = "";
    }

    public TarArchiveEntry(String name, byte linkFlag) {
        this(name, linkFlag, false);
    }

    public TarArchiveEntry(String name, byte linkFlag, boolean preserveLeadingSlashes) {
        this(name, preserveLeadingSlashes);
        this.linkFlag = linkFlag;
        if (linkFlag == TarConstants.LF_GNUTYPE_LONGNAME) {
            this.magic = TarConstants.MAGIC_GNU;
            this.version = TarConstants.VERSION_GNU_SPACE;
        }
    }

    public TarArchiveEntry(File file) {
        this(file, file.getPath());
    }

    public TarArchiveEntry(File file, String fileName) {
        this.name = "";
        this.userId = 0;
        this.groupId = 0;
        this.size = 0;
        this.linkName = "";
        this.magic = "ustar\u0000";
        this.version = TarConstants.VERSION_POSIX;
        this.groupName = "";
        this.devMajor = 0;
        this.devMinor = 0;
        String normalizedName = normalizeFileName(fileName, false);
        this.file = file;
        if (file.isDirectory()) {
            this.mode = DEFAULT_DIR_MODE;
            this.linkFlag = TarConstants.LF_DIR;
            int nameLength = normalizedName.length();
            if (nameLength == 0 || normalizedName.charAt(nameLength - 1) != '/') {
                this.name = normalizedName + "/";
            } else {
                this.name = normalizedName;
            }
        } else {
            this.mode = DEFAULT_FILE_MODE;
            this.linkFlag = TarConstants.LF_NORMAL;
            this.size = file.length();
            this.name = normalizedName;
        }
        this.modTime = file.lastModified() / 1000;
        this.userName = "";
    }

    public TarArchiveEntry(byte[] headerBuf) {
        this();
        parseTarHeader(headerBuf);
    }

    public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding) throws IOException {
        this();
        parseTarHeader(headerBuf, encoding);
    }

    public boolean equals(TarArchiveEntry it) {
        return it != null && getName().equals(it.getName());
    }

    public boolean equals(Object it) {
        if (it == null || getClass() != it.getClass()) {
            return false;
        }
        return equals((TarArchiveEntry) it);
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean isDescendent(TarArchiveEntry desc) {
        return desc.getName().startsWith(getName());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = normalizeFileName(name, this.preserveLeadingSlashes);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getLinkName() {
        return this.linkName;
    }

    public void setLinkName(String link) {
        this.linkName = link;
    }

    @Deprecated
    public int getUserId() {
        return (int) (this.userId & -1);
    }

    public void setUserId(int userId) {
        setUserId((long) userId);
    }

    public long getLongUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Deprecated
    public int getGroupId() {
        return (int) (this.groupId & -1);
    }

    public void setGroupId(int groupId) {
        setGroupId((long) groupId);
    }

    public long getLongGroupId() {
        return this.groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setIds(int userId, int groupId) {
        setUserId(userId);
        setGroupId(groupId);
    }

    public void setNames(String userName, String groupName) {
        setUserName(userName);
        setGroupName(groupName);
    }

    public void setModTime(long time) {
        this.modTime = time / 1000;
    }

    public void setModTime(Date time) {
        this.modTime = time.getTime() / 1000;
    }

    public Date getModTime() {
        return new Date(this.modTime * 1000);
    }

    public Date getLastModifiedDate() {
        return getModTime();
    }

    public boolean isCheckSumOK() {
        return this.checkSumOK;
    }

    public File getFile() {
        return this.file;
    }

    public int getMode() {
        return this.mode;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size is out of range: " + size);
        }
        this.size = size;
    }

    public int getDevMajor() {
        return this.devMajor;
    }

    public void setDevMajor(int devNo) {
        if (devNo < 0) {
            throw new IllegalArgumentException("Major device number is out of range: " + devNo);
        }
        this.devMajor = devNo;
    }

    public int getDevMinor() {
        return this.devMinor;
    }

    public void setDevMinor(int devNo) {
        if (devNo < 0) {
            throw new IllegalArgumentException("Minor device number is out of range: " + devNo);
        }
        this.devMinor = devNo;
    }

    public boolean isExtended() {
        return this.isExtended;
    }

    public long getRealSize() {
        return this.realSize;
    }

    public boolean isGNUSparse() {
        return isOldGNUSparse() || isPaxGNUSparse();
    }

    public boolean isOldGNUSparse() {
        return this.linkFlag == TarConstants.LF_GNUTYPE_SPARSE;
    }

    public boolean isPaxGNUSparse() {
        return this.paxGNUSparse;
    }

    public boolean isStarSparse() {
        return this.starSparse;
    }

    public boolean isGNULongLinkEntry() {
        return this.linkFlag == TarConstants.LF_GNUTYPE_LONGLINK;
    }

    public boolean isGNULongNameEntry() {
        return this.linkFlag == TarConstants.LF_GNUTYPE_LONGNAME;
    }

    public boolean isPaxHeader() {
        return this.linkFlag == TarConstants.LF_PAX_EXTENDED_HEADER_LC || this.linkFlag == TarConstants.LF_PAX_EXTENDED_HEADER_UC;
    }

    public boolean isGlobalPaxHeader() {
        return this.linkFlag == TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER;
    }

    public boolean isDirectory() {
        if (this.file != null) {
            return this.file.isDirectory();
        }
        if (this.linkFlag == TarConstants.LF_DIR) {
            return true;
        }
        if (isPaxHeader() || isGlobalPaxHeader() || !getName().endsWith("/")) {
            return false;
        }
        return true;
    }

    public boolean isFile() {
        if (this.file != null) {
            return this.file.isFile();
        }
        if (this.linkFlag == (byte) 0 || this.linkFlag == TarConstants.LF_NORMAL || !getName().endsWith("/")) {
            return true;
        }
        return false;
    }

    public boolean isSymbolicLink() {
        return this.linkFlag == TarConstants.LF_SYMLINK;
    }

    public boolean isLink() {
        return this.linkFlag == TarConstants.LF_LINK;
    }

    public boolean isCharacterDevice() {
        return this.linkFlag == TarConstants.LF_CHR;
    }

    public boolean isBlockDevice() {
        return this.linkFlag == TarConstants.LF_BLK;
    }

    public boolean isFIFO() {
        return this.linkFlag == TarConstants.LF_FIFO;
    }

    public boolean isSparse() {
        return isGNUSparse() || isStarSparse();
    }

    public TarArchiveEntry[] getDirectoryEntries() {
        if (this.file == null || !this.file.isDirectory()) {
            return EMPTY_TAR_ARCHIVE_ENTRIES;
        }
        String[] list = this.file.list();
        if (list == null) {
            return EMPTY_TAR_ARCHIVE_ENTRIES;
        }
        TarArchiveEntry[] result = new TarArchiveEntry[list.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new TarArchiveEntry(new File(this.file, list[i]));
        }
        return result;
    }

    public void writeEntryHeader(byte[] outbuf) {
        try {
            writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
        } catch (IOException e) {
            try {
                writeEntryHeader(outbuf, TarUtils.FALLBACK_ENCODING, false);
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    public void writeEntryHeader(byte[] outbuf, ZipEncoding encoding, boolean starMode) throws IOException {
        long j = (long) this.mode;
        int offset = writeEntryHeaderField(this.modTime, outbuf, writeEntryHeaderField(this.size, outbuf, writeEntryHeaderField(this.groupId, outbuf, writeEntryHeaderField(this.userId, outbuf, writeEntryHeaderField(j, outbuf, TarUtils.formatNameBytes(this.name, outbuf, 0, 100, encoding), 8, starMode), 8, starMode), 8, starMode), 12, starMode), 12, starMode);
        int csOffset = offset;
        int c = 0;
        int offset2 = offset;
        while (c < 8) {
            offset = offset2 + 1;
            outbuf[offset2] = HttpConstants.SP;
            c++;
            offset2 = offset;
        }
        offset = offset2 + 1;
        outbuf[offset2] = this.linkFlag;
        j = (long) this.devMajor;
        offset = writeEntryHeaderField(j, outbuf, TarUtils.formatNameBytes(this.groupName, outbuf, TarUtils.formatNameBytes(this.userName, outbuf, TarUtils.formatNameBytes(this.version, outbuf, TarUtils.formatNameBytes(this.magic, outbuf, TarUtils.formatNameBytes(this.linkName, outbuf, offset, 100, encoding), 6), 2), 32, encoding), 32, encoding), 8, starMode);
        offset = writeEntryHeaderField((long) this.devMinor, outbuf, offset, 8, starMode);
        while (offset < outbuf.length) {
            offset2 = offset + 1;
            outbuf[offset] = (byte) 0;
            offset = offset2;
        }
        TarUtils.formatCheckSumOctalBytes(TarUtils.computeCheckSum(outbuf), outbuf, csOffset, 8);
    }

    private int writeEntryHeaderField(long value, byte[] outbuf, int offset, int length, boolean starMode) {
        if (starMode || (value >= 0 && value < (1 << ((length - 1) * 3)))) {
            return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
        }
        return TarUtils.formatLongOctalBytes(0, outbuf, offset, length);
    }

    public void parseTarHeader(byte[] header) {
        try {
            parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
        } catch (IOException e) {
            try {
                parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true);
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    public void parseTarHeader(byte[] header, ZipEncoding encoding) throws IOException {
        parseTarHeader(header, encoding, false);
    }

    private void parseTarHeader(byte[] header, ZipEncoding encoding, boolean oldStyle) throws IOException {
        String parseName;
        if (oldStyle) {
            parseName = TarUtils.parseName(header, 0, 100);
        } else {
            parseName = TarUtils.parseName(header, 0, 100, encoding);
        }
        this.name = parseName;
        int offset = 0 + 100;
        this.mode = (int) TarUtils.parseOctalOrBinary(header, offset, 8);
        offset += 8;
        this.userId = (long) ((int) TarUtils.parseOctalOrBinary(header, offset, 8));
        offset += 8;
        this.groupId = (long) ((int) TarUtils.parseOctalOrBinary(header, offset, 8));
        offset += 8;
        this.size = TarUtils.parseOctalOrBinary(header, offset, 12);
        offset += 12;
        this.modTime = TarUtils.parseOctalOrBinary(header, offset, 12);
        offset += 12;
        this.checkSumOK = TarUtils.verifyCheckSum(header);
        offset += 8;
        int offset2 = offset + 1;
        this.linkFlag = header[offset];
        if (oldStyle) {
            parseName = TarUtils.parseName(header, offset2, 100);
        } else {
            parseName = TarUtils.parseName(header, offset2, 100, encoding);
        }
        this.linkName = parseName;
        offset = offset2 + 100;
        this.magic = TarUtils.parseName(header, offset, 6);
        offset += 6;
        this.version = TarUtils.parseName(header, offset, 2);
        offset += 2;
        if (oldStyle) {
            parseName = TarUtils.parseName(header, offset, 32);
        } else {
            parseName = TarUtils.parseName(header, offset, 32, encoding);
        }
        this.userName = parseName;
        offset += 32;
        if (oldStyle) {
            parseName = TarUtils.parseName(header, offset, 32);
        } else {
            parseName = TarUtils.parseName(header, offset, 32, encoding);
        }
        this.groupName = parseName;
        offset += 32;
        this.devMajor = (int) TarUtils.parseOctalOrBinary(header, offset, 8);
        offset += 8;
        this.devMinor = (int) TarUtils.parseOctalOrBinary(header, offset, 8);
        offset += 8;
        switch (evaluateType(header)) {
            case 2:
                offset = (((((offset + 12) + 12) + 12) + 4) + 1) + 96;
                this.isExtended = TarUtils.parseBoolean(header, offset);
                offset++;
                this.realSize = TarUtils.parseOctal(header, offset, 12);
                offset += 12;
                return;
            case 4:
                String xstarPrefix;
                if (oldStyle) {
                    xstarPrefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN_XSTAR);
                } else {
                    xstarPrefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN_XSTAR, encoding);
                }
                if (xstarPrefix.length() > 0) {
                    this.name = xstarPrefix + "/" + this.name;
                    return;
                }
                return;
            default:
                String prefix;
                if (oldStyle) {
                    prefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN);
                } else {
                    prefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN, encoding);
                }
                if (isDirectory() && !this.name.endsWith("/")) {
                    this.name += "/";
                }
                if (prefix.length() > 0) {
                    this.name = prefix + "/" + this.name;
                    return;
                }
                return;
        }
    }

    private static String normalizeFileName(String fileName, boolean preserveLeadingSlashes) {
        String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (osname != null) {
            if (osname.startsWith("windows")) {
                if (fileName.length() > 2) {
                    char ch1 = fileName.charAt(0);
                    if (fileName.charAt(1) == ':' && ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z'))) {
                        fileName = fileName.substring(2);
                    }
                }
            } else if (osname.contains("netware")) {
                int colon = fileName.indexOf(58);
                if (colon != -1) {
                    fileName = fileName.substring(colon + 1);
                }
            }
        }
        fileName = fileName.replace(File.separatorChar, '/');
        while (!preserveLeadingSlashes && fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        return fileName;
    }

    private int evaluateType(byte[] header) {
        if (ArchiveUtils.matchAsciiBuffer(TarConstants.MAGIC_GNU, header, 257, 6)) {
            return 2;
        }
        if (!ArchiveUtils.matchAsciiBuffer("ustar\u0000", header, 257, 6)) {
            return 0;
        }
        if (ArchiveUtils.matchAsciiBuffer(TarConstants.MAGIC_XSTAR, header, TarConstants.XSTAR_MAGIC_OFFSET, 4)) {
            return 4;
        }
        return 3;
    }

    void fillGNUSparse0xData(Map<String, String> headers) {
        this.paxGNUSparse = true;
        this.realSize = (long) Integer.parseInt((String) headers.get("GNU.sparse.size"));
        if (headers.containsKey("GNU.sparse.name")) {
            this.name = (String) headers.get("GNU.sparse.name");
        }
    }

    void fillGNUSparse1xData(Map<String, String> headers) {
        this.paxGNUSparse = true;
        this.realSize = (long) Integer.parseInt((String) headers.get("GNU.sparse.realsize"));
        this.name = (String) headers.get("GNU.sparse.name");
    }

    void fillStarSparseData(Map<String, String> headers) {
        this.starSparse = true;
        if (headers.containsKey("SCHILY.realsize")) {
            this.realSize = Long.parseLong((String) headers.get("SCHILY.realsize"));
        }
    }
}
