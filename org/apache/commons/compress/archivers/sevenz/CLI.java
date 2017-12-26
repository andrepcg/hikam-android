package org.apache.commons.compress.archivers.sevenz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CLI {
    private static final byte[] BUF = new byte[8192];

    private enum Mode {
        LIST("Analysing") {
            public void takeAction(SevenZFile archive, SevenZArchiveEntry entry) {
                System.out.print(entry.getName());
                if (entry.isDirectory()) {
                    System.out.print(" dir");
                } else {
                    System.out.print(" " + entry.getCompressedSize() + "/" + entry.getSize());
                }
                if (entry.getHasLastModifiedDate()) {
                    System.out.print(" " + entry.getLastModifiedDate());
                } else {
                    System.out.print(" no last modified date");
                }
                if (entry.isDirectory()) {
                    System.out.println("");
                } else {
                    System.out.println(" " + getContentMethods(entry));
                }
            }

            private String getContentMethods(SevenZArchiveEntry entry) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (SevenZMethodConfiguration m : entry.getContentMethods()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    first = false;
                    sb.append(m.getMethod());
                    if (m.getOptions() != null) {
                        sb.append("(").append(m.getOptions()).append(")");
                    }
                }
                return sb.toString();
            }
        },
        EXTRACT("Extracting") {
            public void takeAction(SevenZFile archive, SevenZArchiveEntry entry) throws IOException {
                FileOutputStream fos;
                Throwable th;
                Throwable th2;
                File outFile = new File(entry.getName());
                if (!entry.isDirectory()) {
                    System.out.println("extracting to " + outFile);
                    File parent = outFile.getParentFile();
                    if (parent == null || parent.exists() || parent.mkdirs()) {
                        fos = new FileOutputStream(outFile);
                        th = null;
                        try {
                            long total = entry.getSize();
                            long off = 0;
                            while (off < total) {
                                SevenZFile sevenZFile = archive;
                                int bytesRead = sevenZFile.read(CLI.BUF, 0, (int) Math.min(total - off, (long) CLI.BUF.length));
                                if (bytesRead < 1) {
                                    throw new IOException("reached end of entry " + entry.getName() + " after " + off + " bytes, expected " + total);
                                }
                                off += (long) bytesRead;
                                fos.write(CLI.BUF, 0, bytesRead);
                            }
                            if (fos == null) {
                                return;
                            }
                            if (th != null) {
                                try {
                                    fos.close();
                                    return;
                                } catch (Throwable th22) {
                                    th.addSuppressed(th22);
                                    return;
                                }
                            }
                            fos.close();
                            return;
                        } catch (Throwable th3) {
                            Throwable th4 = th3;
                            th3 = th22;
                            th22 = th4;
                        }
                    } else {
                        throw new IOException("Cannot create " + parent);
                    }
                } else if (outFile.isDirectory() || outFile.mkdirs()) {
                    System.out.println("created directory " + outFile);
                    return;
                } else {
                    throw new IOException("Cannot create directory " + outFile);
                }
                if (fos != null) {
                    if (th3 != null) {
                        try {
                            fos.close();
                        } catch (Throwable th5) {
                            th3.addSuppressed(th5);
                        }
                    } else {
                        fos.close();
                    }
                }
                throw th22;
                throw th22;
            }
        };
        
        private final String message;

        public abstract void takeAction(SevenZFile sevenZFile, SevenZArchiveEntry sevenZArchiveEntry) throws IOException;

        private Mode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static void main(String[] args) throws Exception {
        Throwable th;
        if (args.length == 0) {
            usage();
            return;
        }
        Mode mode = grabMode(args);
        System.out.println(mode.getMessage() + " " + args[0]);
        File f = new File(args[0]);
        if (!f.isFile()) {
            System.err.println(f + " doesn't exist or is a directory");
        }
        SevenZFile archive = new SevenZFile(f);
        Throwable th2 = null;
        while (true) {
            try {
                SevenZArchiveEntry ae = archive.getNextEntry();
                if (ae == null) {
                    break;
                }
                mode.takeAction(archive, ae);
            } catch (Throwable th22) {
                Throwable th3 = th22;
                th22 = th;
                th = th3;
            }
        }
        if (archive == null) {
            return;
        }
        if (th22 != null) {
            try {
                archive.close();
                return;
            } catch (Throwable th4) {
                th22.addSuppressed(th4);
                return;
            }
        }
        archive.close();
        return;
        throw th4;
        if (archive != null) {
            if (th22 != null) {
                try {
                    archive.close();
                } catch (Throwable th5) {
                    th22.addSuppressed(th5);
                }
            } else {
                archive.close();
            }
        }
        throw th4;
    }

    private static void usage() {
        System.out.println("Parameters: archive-name [list|extract]");
    }

    private static Mode grabMode(String[] args) {
        if (args.length < 2) {
            return Mode.LIST;
        }
        return (Mode) Enum.valueOf(Mode.class, args[1].toUpperCase());
    }
}
