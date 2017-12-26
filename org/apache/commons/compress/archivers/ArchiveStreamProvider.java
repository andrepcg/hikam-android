package org.apache.commons.compress.archivers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface ArchiveStreamProvider {
    ArchiveInputStream createArchiveInputStream(String str, InputStream inputStream, String str2) throws ArchiveException;

    ArchiveOutputStream createArchiveOutputStream(String str, OutputStream outputStream, String str2) throws ArchiveException;

    Set<String> getInputStreamArchiveNames();

    Set<String> getOutputStreamArchiveNames();
}
