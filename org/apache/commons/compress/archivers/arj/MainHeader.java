package org.apache.commons.compress.archivers.arj;

import java.util.Arrays;

class MainHeader {
    long archiveSize;
    int archiverVersionNumber;
    int arjFlags;
    int arjFlags2;
    int arjProtectionFactor;
    String comment;
    int dateTimeCreated;
    int dateTimeModified;
    int encryptionVersion;
    byte[] extendedHeaderBytes = null;
    int fileSpecPosition;
    int fileType;
    int hostOS;
    int lastChapter;
    int minVersionToExtract;
    String name;
    int reserved;
    int securityEnvelopeFilePosition;
    int securityEnvelopeLength;
    int securityVersion;

    static class Flags {
        static final int ALTNAME = 128;
        static final int ARJPROT = 8;
        static final int BACKUP = 32;
        static final int GARBLED = 1;
        static final int OLD_SECURED_NEW_ANSI_PAGE = 2;
        static final int PATHSYM = 16;
        static final int SECURED = 64;
        static final int VOLUME = 4;

        Flags() {
        }
    }

    MainHeader() {
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MainHeader [archiverVersionNumber=");
        builder.append(this.archiverVersionNumber);
        builder.append(", minVersionToExtract=");
        builder.append(this.minVersionToExtract);
        builder.append(", hostOS=");
        builder.append(this.hostOS);
        builder.append(", arjFlags=");
        builder.append(this.arjFlags);
        builder.append(", securityVersion=");
        builder.append(this.securityVersion);
        builder.append(", fileType=");
        builder.append(this.fileType);
        builder.append(", reserved=");
        builder.append(this.reserved);
        builder.append(", dateTimeCreated=");
        builder.append(this.dateTimeCreated);
        builder.append(", dateTimeModified=");
        builder.append(this.dateTimeModified);
        builder.append(", archiveSize=");
        builder.append(this.archiveSize);
        builder.append(", securityEnvelopeFilePosition=");
        builder.append(this.securityEnvelopeFilePosition);
        builder.append(", fileSpecPosition=");
        builder.append(this.fileSpecPosition);
        builder.append(", securityEnvelopeLength=");
        builder.append(this.securityEnvelopeLength);
        builder.append(", encryptionVersion=");
        builder.append(this.encryptionVersion);
        builder.append(", lastChapter=");
        builder.append(this.lastChapter);
        builder.append(", arjProtectionFactor=");
        builder.append(this.arjProtectionFactor);
        builder.append(", arjFlags2=");
        builder.append(this.arjFlags2);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", comment=");
        builder.append(this.comment);
        builder.append(", extendedHeaderBytes=");
        builder.append(Arrays.toString(this.extendedHeaderBytes));
        builder.append("]");
        return builder.toString();
    }
}
