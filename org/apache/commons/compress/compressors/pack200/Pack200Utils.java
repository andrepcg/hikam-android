package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Pack200Utils {
    private Pack200Utils() {
    }

    public static void normalize(File jar) throws IOException {
        normalize(jar, jar, null);
    }

    public static void normalize(File jar, Map<String, String> props) throws IOException {
        normalize(jar, jar, props);
    }

    public static void normalize(File from, File to) throws IOException {
        normalize(from, to, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void normalize(java.io.File r11, java.io.File r12, java.util.Map<java.lang.String, java.lang.String> r13) throws java.io.IOException {
        /*
        r8 = 0;
        if (r13 != 0) goto L_0x0008;
    L_0x0003:
        r13 = new java.util.HashMap;
        r13.<init>();
    L_0x0008:
        r6 = "pack.segment.limit";
        r7 = "-1";
        r13.put(r6, r7);
        r6 = "commons-compress";
        r7 = "pack200normalize";
        r4 = java.io.File.createTempFile(r6, r7);
        r0 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0074 }
        r0.<init>(r4);	 Catch:{ all -> 0x0074 }
        r6 = 0;
        r1 = new java.util.jar.JarFile;	 Catch:{ Throwable -> 0x0067, all -> 0x0083 }
        r1.<init>(r11);	 Catch:{ Throwable -> 0x0067, all -> 0x0083 }
        r7 = 0;
        r3 = java.util.jar.Pack200.newPacker();	 Catch:{ Throwable -> 0x0085, all -> 0x00cb }
        r9 = r3.properties();	 Catch:{ Throwable -> 0x0085, all -> 0x00cb }
        r9.putAll(r13);	 Catch:{ Throwable -> 0x0085, all -> 0x00cb }
        r3.pack(r1, r0);	 Catch:{ Throwable -> 0x0085, all -> 0x00cb }
        if (r1 == 0) goto L_0x0038;
    L_0x0033:
        if (r8 == 0) goto L_0x007f;
    L_0x0035:
        r1.close();	 Catch:{ Throwable -> 0x0062, all -> 0x0083 }
    L_0x0038:
        if (r0 == 0) goto L_0x003f;
    L_0x003a:
        if (r8 == 0) goto L_0x00a1;
    L_0x003c:
        r0.close();	 Catch:{ Throwable -> 0x009c }
    L_0x003f:
        r5 = java.util.jar.Pack200.newUnpacker();	 Catch:{ all -> 0x0074 }
        r2 = new java.util.jar.JarOutputStream;	 Catch:{ all -> 0x0074 }
        r6 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0074 }
        r6.<init>(r12);	 Catch:{ all -> 0x0074 }
        r2.<init>(r6);	 Catch:{ all -> 0x0074 }
        r6 = 0;
        r5.unpack(r4, r2);	 Catch:{ Throwable -> 0x00b7 }
        if (r2 == 0) goto L_0x0058;
    L_0x0053:
        if (r8 == 0) goto L_0x00b3;
    L_0x0055:
        r2.close();	 Catch:{ Throwable -> 0x00ae }
    L_0x0058:
        r6 = r4.delete();
        if (r6 != 0) goto L_0x0061;
    L_0x005e:
        r4.deleteOnExit();
    L_0x0061:
        return;
    L_0x0062:
        r9 = move-exception;
        r7.addSuppressed(r9);	 Catch:{ Throwable -> 0x0067, all -> 0x0083 }
        goto L_0x0038;
    L_0x0067:
        r6 = move-exception;
        throw r6;	 Catch:{ all -> 0x0069 }
    L_0x0069:
        r7 = move-exception;
        r8 = r6;
        r6 = r7;
    L_0x006c:
        if (r0 == 0) goto L_0x0073;
    L_0x006e:
        if (r8 == 0) goto L_0x00aa;
    L_0x0070:
        r0.close();	 Catch:{ Throwable -> 0x00a5 }
    L_0x0073:
        throw r6;	 Catch:{ all -> 0x0074 }
    L_0x0074:
        r6 = move-exception;
        r7 = r4.delete();
        if (r7 != 0) goto L_0x007e;
    L_0x007b:
        r4.deleteOnExit();
    L_0x007e:
        throw r6;
    L_0x007f:
        r1.close();	 Catch:{ Throwable -> 0x0067, all -> 0x0083 }
        goto L_0x0038;
    L_0x0083:
        r6 = move-exception;
        goto L_0x006c;
    L_0x0085:
        r6 = move-exception;
        throw r6;	 Catch:{ all -> 0x0087 }
    L_0x0087:
        r7 = move-exception;
        r10 = r7;
        r7 = r6;
        r6 = r10;
    L_0x008b:
        if (r1 == 0) goto L_0x0092;
    L_0x008d:
        if (r7 == 0) goto L_0x0098;
    L_0x008f:
        r1.close();	 Catch:{ Throwable -> 0x0093, all -> 0x0083 }
    L_0x0092:
        throw r6;	 Catch:{ Throwable -> 0x0067, all -> 0x0083 }
    L_0x0093:
        r9 = move-exception;
        r7.addSuppressed(r9);	 Catch:{ Throwable -> 0x0067, all -> 0x0083 }
        goto L_0x0092;
    L_0x0098:
        r1.close();	 Catch:{ Throwable -> 0x0067, all -> 0x0083 }
        goto L_0x0092;
    L_0x009c:
        r7 = move-exception;
        r6.addSuppressed(r7);	 Catch:{ all -> 0x0074 }
        goto L_0x003f;
    L_0x00a1:
        r0.close();	 Catch:{ all -> 0x0074 }
        goto L_0x003f;
    L_0x00a5:
        r7 = move-exception;
        r8.addSuppressed(r7);	 Catch:{ all -> 0x0074 }
        goto L_0x0073;
    L_0x00aa:
        r0.close();	 Catch:{ all -> 0x0074 }
        goto L_0x0073;
    L_0x00ae:
        r7 = move-exception;
        r6.addSuppressed(r7);	 Catch:{ all -> 0x0074 }
        goto L_0x0058;
    L_0x00b3:
        r2.close();	 Catch:{ all -> 0x0074 }
        goto L_0x0058;
    L_0x00b7:
        r8 = move-exception;
        throw r8;	 Catch:{ all -> 0x00b9 }
    L_0x00b9:
        r6 = move-exception;
        if (r2 == 0) goto L_0x00c1;
    L_0x00bc:
        if (r8 == 0) goto L_0x00c7;
    L_0x00be:
        r2.close();	 Catch:{ Throwable -> 0x00c2 }
    L_0x00c1:
        throw r6;	 Catch:{ all -> 0x0074 }
    L_0x00c2:
        r7 = move-exception;
        r8.addSuppressed(r7);	 Catch:{ all -> 0x0074 }
        goto L_0x00c1;
    L_0x00c7:
        r2.close();	 Catch:{ all -> 0x0074 }
        goto L_0x00c1;
    L_0x00cb:
        r6 = move-exception;
        r7 = r8;
        goto L_0x008b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.compressors.pack200.Pack200Utils.normalize(java.io.File, java.io.File, java.util.Map):void");
    }
}
