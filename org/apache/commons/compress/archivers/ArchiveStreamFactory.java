package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.ServiceLoaderIterator;
import org.apache.commons.compress.utils.Sets;

public class ArchiveStreamFactory implements ArchiveStreamProvider {
    public static final String AR = "ar";
    public static final String ARJ = "arj";
    public static final String CPIO = "cpio";
    public static final String DUMP = "dump";
    private static final int DUMP_SIGNATURE_SIZE = 32;
    public static final String JAR = "jar";
    public static final String SEVEN_Z = "7z";
    private static final int SIGNATURE_SIZE = 12;
    private static final ArchiveStreamFactory SINGLETON = new ArchiveStreamFactory();
    public static final String TAR = "tar";
    private static final int TAR_HEADER_SIZE = 512;
    public static final String ZIP = "zip";
    private SortedMap<String, ArchiveStreamProvider> archiveInputStreamProviders;
    private SortedMap<String, ArchiveStreamProvider> archiveOutputStreamProviders;
    private final String encoding;
    private volatile String entryEncoding;

    static class C07841 implements PrivilegedAction<SortedMap<String, ArchiveStreamProvider>> {
        C07841() {
        }

        public SortedMap<String, ArchiveStreamProvider> run() {
            TreeMap<String, ArchiveStreamProvider> map = new TreeMap();
            ArchiveStreamFactory.putAll(ArchiveStreamFactory.SINGLETON.getInputStreamArchiveNames(), ArchiveStreamFactory.SINGLETON, map);
            Iterator it = ArchiveStreamFactory.findArchiveStreamProviders().iterator();
            while (it.hasNext()) {
                ArchiveStreamProvider provider = (ArchiveStreamProvider) it.next();
                ArchiveStreamFactory.putAll(provider.getInputStreamArchiveNames(), provider, map);
            }
            return map;
        }
    }

    static class C07852 implements PrivilegedAction<SortedMap<String, ArchiveStreamProvider>> {
        C07852() {
        }

        public SortedMap<String, ArchiveStreamProvider> run() {
            TreeMap<String, ArchiveStreamProvider> map = new TreeMap();
            ArchiveStreamFactory.putAll(ArchiveStreamFactory.SINGLETON.getOutputStreamArchiveNames(), ArchiveStreamFactory.SINGLETON, map);
            Iterator it = ArchiveStreamFactory.findArchiveStreamProviders().iterator();
            while (it.hasNext()) {
                ArchiveStreamProvider provider = (ArchiveStreamProvider) it.next();
                ArchiveStreamFactory.putAll(provider.getOutputStreamArchiveNames(), provider, map);
            }
            return map;
        }
    }

    private static ArrayList<ArchiveStreamProvider> findArchiveStreamProviders() {
        return Lists.newArrayList(serviceLoaderIterator());
    }

    static void putAll(Set<String> names, ArchiveStreamProvider provider, TreeMap<String, ArchiveStreamProvider> map) {
        for (String name : names) {
            map.put(toKey(name), provider);
        }
    }

    private static Iterator<ArchiveStreamProvider> serviceLoaderIterator() {
        return new ServiceLoaderIterator(ArchiveStreamProvider.class);
    }

    private static String toKey(String name) {
        return name.toUpperCase(Locale.ROOT);
    }

    public static SortedMap<String, ArchiveStreamProvider> findAvailableArchiveInputStreamProviders() {
        return (SortedMap) AccessController.doPrivileged(new C07841());
    }

    public static SortedMap<String, ArchiveStreamProvider> findAvailableArchiveOutputStreamProviders() {
        return (SortedMap) AccessController.doPrivileged(new C07852());
    }

    public ArchiveStreamFactory() {
        this(null);
    }

    public ArchiveStreamFactory(String encoding) {
        this.encoding = encoding;
        this.entryEncoding = encoding;
    }

    public String getEntryEncoding() {
        return this.entryEncoding;
    }

    @Deprecated
    public void setEntryEncoding(String entryEncoding) {
        if (this.encoding != null) {
            throw new IllegalStateException("Cannot overide encoding set by the constructor");
        }
        this.entryEncoding = entryEncoding;
    }

    public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in) throws ArchiveException {
        return createArchiveInputStream(archiverName, in, this.entryEncoding);
    }

    public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in, String actualEncoding) throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        } else if (in == null) {
            throw new IllegalArgumentException("InputStream must not be null.");
        } else if (AR.equalsIgnoreCase(archiverName)) {
            return new ArArchiveInputStream(in);
        } else {
            if (ARJ.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new ArjArchiveInputStream(in, actualEncoding);
                }
                return new ArjArchiveInputStream(in);
            } else if (ZIP.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new ZipArchiveInputStream(in, actualEncoding);
                }
                return new ZipArchiveInputStream(in);
            } else if (TAR.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new TarArchiveInputStream(in, actualEncoding);
                }
                return new TarArchiveInputStream(in);
            } else if (JAR.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new JarArchiveInputStream(in, actualEncoding);
                }
                return new JarArchiveInputStream(in);
            } else if (CPIO.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new CpioArchiveInputStream(in, actualEncoding);
                }
                return new CpioArchiveInputStream(in);
            } else if (DUMP.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new DumpArchiveInputStream(in, actualEncoding);
                }
                return new DumpArchiveInputStream(in);
            } else if (SEVEN_Z.equalsIgnoreCase(archiverName)) {
                throw new StreamingNotSupportedException(SEVEN_Z);
            } else {
                ArchiveStreamProvider archiveStreamProvider = (ArchiveStreamProvider) getArchiveInputStreamProviders().get(toKey(archiverName));
                if (archiveStreamProvider != null) {
                    return archiveStreamProvider.createArchiveInputStream(archiverName, in, actualEncoding);
                }
                throw new ArchiveException("Archiver: " + archiverName + " not found.");
            }
        }
    }

    public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out) throws ArchiveException {
        return createArchiveOutputStream(archiverName, out, this.entryEncoding);
    }

    public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out, String actualEncoding) throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        } else if (out == null) {
            throw new IllegalArgumentException("OutputStream must not be null.");
        } else if (AR.equalsIgnoreCase(archiverName)) {
            return new ArArchiveOutputStream(out);
        } else {
            if (ZIP.equalsIgnoreCase(archiverName)) {
                ArchiveOutputStream zip = new ZipArchiveOutputStream(out);
                if (actualEncoding == null) {
                    return zip;
                }
                zip.setEncoding(actualEncoding);
                return zip;
            } else if (TAR.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new TarArchiveOutputStream(out, actualEncoding);
                }
                return new TarArchiveOutputStream(out);
            } else if (JAR.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new JarArchiveOutputStream(out, actualEncoding);
                }
                return new JarArchiveOutputStream(out);
            } else if (CPIO.equalsIgnoreCase(archiverName)) {
                if (actualEncoding != null) {
                    return new CpioArchiveOutputStream(out, actualEncoding);
                }
                return new CpioArchiveOutputStream(out);
            } else if (SEVEN_Z.equalsIgnoreCase(archiverName)) {
                throw new StreamingNotSupportedException(SEVEN_Z);
            } else {
                ArchiveStreamProvider archiveStreamProvider = (ArchiveStreamProvider) getArchiveOutputStreamProviders().get(toKey(archiverName));
                if (archiveStreamProvider != null) {
                    return archiveStreamProvider.createArchiveOutputStream(archiverName, out, actualEncoding);
                }
                throw new ArchiveException("Archiver: " + archiverName + " not found.");
            }
        }
    }

    public ArchiveInputStream createArchiveInputStream(InputStream in) throws ArchiveException {
        return createArchiveInputStream(detect(in), in);
    }

    public static String detect(InputStream in) throws ArchiveException {
        Throwable th;
        if (in == null) {
            throw new IllegalArgumentException("Stream must not be null.");
        } else if (in.markSupported()) {
            byte[] signature = new byte[12];
            in.mark(signature.length);
            try {
                int signatureLength = IOUtils.readFully(in, signature);
                in.reset();
                if (ZipArchiveInputStream.matches(signature, signatureLength)) {
                    return ZIP;
                }
                if (JarArchiveInputStream.matches(signature, signatureLength)) {
                    return JAR;
                }
                if (ArArchiveInputStream.matches(signature, signatureLength)) {
                    return AR;
                }
                if (CpioArchiveInputStream.matches(signature, signatureLength)) {
                    return CPIO;
                }
                if (ArjArchiveInputStream.matches(signature, signatureLength)) {
                    return ARJ;
                }
                if (SevenZFile.matches(signature, signatureLength)) {
                    return SEVEN_Z;
                }
                byte[] dumpsig = new byte[32];
                in.mark(dumpsig.length);
                try {
                    signatureLength = IOUtils.readFully(in, dumpsig);
                    in.reset();
                    if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
                        return DUMP;
                    }
                    byte[] tarHeader = new byte[512];
                    in.mark(tarHeader.length);
                    try {
                        signatureLength = IOUtils.readFully(in, tarHeader);
                        in.reset();
                        if (TarArchiveInputStream.matches(tarHeader, signatureLength)) {
                            return TAR;
                        }
                        if (signatureLength >= 512) {
                            TarArchiveInputStream tais = null;
                            try {
                                TarArchiveInputStream tais2 = new TarArchiveInputStream(new ByteArrayInputStream(tarHeader));
                                try {
                                    if (tais2.getNextTarEntry().isCheckSumOK()) {
                                        String str = TAR;
                                        IOUtils.closeQuietly(tais2);
                                        return str;
                                    }
                                    IOUtils.closeQuietly(tais2);
                                } catch (Exception e) {
                                    tais = tais2;
                                    IOUtils.closeQuietly(tais);
                                    throw new ArchiveException("No Archiver found for the stream signature");
                                } catch (Throwable th2) {
                                    th = th2;
                                    tais = tais2;
                                    IOUtils.closeQuietly(tais);
                                    throw th;
                                }
                            } catch (Exception e2) {
                                IOUtils.closeQuietly(tais);
                                throw new ArchiveException("No Archiver found for the stream signature");
                            } catch (Throwable th3) {
                                th = th3;
                                IOUtils.closeQuietly(tais);
                                throw th;
                            }
                        }
                        throw new ArchiveException("No Archiver found for the stream signature");
                    } catch (IOException e3) {
                        throw new ArchiveException("IOException while reading tar signature", e3);
                    }
                } catch (IOException e32) {
                    throw new ArchiveException("IOException while reading dump signature", e32);
                }
            } catch (IOException e322) {
                throw new ArchiveException("IOException while reading signature.", e322);
            }
        } else {
            throw new IllegalArgumentException("Mark is not supported.");
        }
    }

    public SortedMap<String, ArchiveStreamProvider> getArchiveInputStreamProviders() {
        if (this.archiveInputStreamProviders == null) {
            this.archiveInputStreamProviders = Collections.unmodifiableSortedMap(findAvailableArchiveInputStreamProviders());
        }
        return this.archiveInputStreamProviders;
    }

    public SortedMap<String, ArchiveStreamProvider> getArchiveOutputStreamProviders() {
        if (this.archiveOutputStreamProviders == null) {
            this.archiveOutputStreamProviders = Collections.unmodifiableSortedMap(findAvailableArchiveOutputStreamProviders());
        }
        return this.archiveOutputStreamProviders;
    }

    public Set<String> getInputStreamArchiveNames() {
        return Sets.newHashSet(AR, ARJ, ZIP, TAR, JAR, CPIO, DUMP, SEVEN_Z);
    }

    public Set<String> getOutputStreamArchiveNames() {
        return Sets.newHashSet(AR, ZIP, TAR, JAR, CPIO, SEVEN_Z);
    }
}
