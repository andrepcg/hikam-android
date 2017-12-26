package org.jboss.netty.util.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public final class NativeLibraryLoader {
    private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
    private static final String OSNAME = SystemPropertyUtil.get("os.name", "").toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
    private static final File WORKDIR;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);

    static {
        String workdir = SystemPropertyUtil.get("io.netty.native.workdir");
        if (workdir != null) {
            File f = new File(workdir);
            if (!f.exists()) {
                f.mkdirs();
            }
            try {
                f = f.getAbsoluteFile();
            } catch (Exception e) {
            }
            WORKDIR = f;
            logger.debug("-Dio.netty.netty.workdir: " + WORKDIR);
            return;
        }
        WORKDIR = tmpdir();
        logger.debug("-Dio.netty.netty.workdir: " + WORKDIR + " (io.netty.tmpdir)");
    }

    private static File tmpdir() {
        File f;
        try {
            f = toDirectory(SystemPropertyUtil.get("io.netty.tmpdir"));
            if (f != null) {
                logger.debug("-Dio.netty.tmpdir: " + f);
            } else {
                f = toDirectory(SystemPropertyUtil.get("java.io.tmpdir"));
                if (f != null) {
                    logger.debug("-Dio.netty.tmpdir: " + f + " (java.io.tmpdir)");
                } else if (isWindows()) {
                    f = toDirectory(System.getenv("TEMP"));
                    if (f != null) {
                        logger.debug("-Dio.netty.tmpdir: " + f + " (%TEMP%)");
                    } else {
                        String userprofile = System.getenv("USERPROFILE");
                        if (userprofile != null) {
                            f = toDirectory(userprofile + "\\AppData\\Local\\Temp");
                            if (f != null) {
                                logger.debug("-Dio.netty.tmpdir: " + f + " (%USERPROFILE%\\AppData\\Local\\Temp)");
                            } else {
                                f = toDirectory(userprofile + "\\Local Settings\\Temp");
                                if (f != null) {
                                    logger.debug("-Dio.netty.tmpdir: " + f + " (%USERPROFILE%\\Local Settings\\Temp)");
                                }
                            }
                        }
                        if (isWindows()) {
                            f = new File("/tmp");
                        } else {
                            f = new File("C:\\Windows\\Temp");
                        }
                        logger.warn("Failed to get the temporary directory; falling back to: " + f);
                    }
                } else {
                    f = toDirectory(System.getenv("TMPDIR"));
                    if (f != null) {
                        logger.debug("-Dio.netty.tmpdir: " + f + " ($TMPDIR)");
                    }
                    if (isWindows()) {
                        f = new File("/tmp");
                    } else {
                        f = new File("C:\\Windows\\Temp");
                    }
                    logger.warn("Failed to get the temporary directory; falling back to: " + f);
                }
            }
        } catch (Exception e) {
        }
        return f;
    }

    private static File toDirectory(String path) {
        if (path == null) {
            return null;
        }
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        if (!f.isDirectory()) {
            return null;
        }
        try {
            return f.getAbsoluteFile();
        } catch (Exception e) {
            return f;
        }
    }

    private static boolean isWindows() {
        return OSNAME.startsWith("windows");
    }

    private static boolean isOSX() {
        return OSNAME.startsWith("macosx") || OSNAME.startsWith("osx");
    }

    public static void load(String name, ClassLoader loader) {
        Exception e;
        Throwable th;
        String libname = System.mapLibraryName(name);
        String path = NATIVE_RESOURCE_HOME + libname;
        URL url = loader.getResource(path);
        if (url == null && isOSX()) {
            url = path.endsWith(".jnilib") ? loader.getResource("META-INF/native/lib" + name + ".dynlib") : loader.getResource("META-INF/native/lib" + name + ".jnilib");
        }
        if (url == null) {
            System.loadLibrary(name);
            return;
        }
        int index = libname.lastIndexOf(46);
        InputStream in = null;
        OutputStream outputStream = null;
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(libname.substring(0, index), libname.substring(index, libname.length()), WORKDIR);
            in = url.openStream();
            OutputStream out = new FileOutputStream(tmpFile);
            try {
                byte[] buffer = new byte[8192];
                while (true) {
                    int length = in.read(buffer);
                    if (length <= 0) {
                        break;
                    }
                    out.write(buffer, 0, length);
                }
                out.flush();
                out.close();
                outputStream = null;
                System.load(tmpFile.getPath());
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e2) {
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e3) {
                    }
                }
                if (tmpFile == null) {
                    return;
                }
                if (true) {
                    tmpFile.deleteOnExit();
                } else if (!tmpFile.delete()) {
                    tmpFile.deleteOnExit();
                }
            } catch (Exception e4) {
                e = e4;
                outputStream = out;
                try {
                    throw ((UnsatisfiedLinkError) new UnsatisfiedLinkError("could not load a native library: " + name).initCause(e));
                } catch (Throwable th2) {
                    th = th2;
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e5) {
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e6) {
                        }
                    }
                    if (tmpFile != null) {
                        if (false) {
                            tmpFile.deleteOnExit();
                        } else if (!tmpFile.delete()) {
                            tmpFile.deleteOnExit();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                outputStream = out;
                if (in != null) {
                    in.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (tmpFile != null) {
                    if (false) {
                        tmpFile.deleteOnExit();
                    } else if (tmpFile.delete()) {
                        tmpFile.deleteOnExit();
                    }
                }
                throw th;
            }
        } catch (Exception e7) {
            e = e7;
            throw ((UnsatisfiedLinkError) new UnsatisfiedLinkError("could not load a native library: " + name).initCause(e));
        }
    }

    private NativeLibraryLoader() {
    }
}
