package org.apache.commons.compress.archivers;

import java.io.InputStream;

public final class Lister {
    private static final ArchiveStreamFactory factory = new ArchiveStreamFactory();

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void main(java.lang.String[] r10) throws java.lang.Exception {
        /*
        r6 = 0;
        r8 = 0;
        r4 = r10.length;
        if (r4 != 0) goto L_0x0009;
    L_0x0005:
        usage();
    L_0x0008:
        return;
    L_0x0009:
        r4 = java.lang.System.out;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r7 = "Analysing ";
        r5 = r5.append(r7);
        r7 = r10[r8];
        r5 = r5.append(r7);
        r5 = r5.toString();
        r4.println(r5);
        r2 = new java.io.File;
        r4 = r10[r8];
        r2.<init>(r4);
        r4 = r2.isFile();
        if (r4 != 0) goto L_0x0048;
    L_0x0030:
        r4 = java.lang.System.err;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5 = r5.append(r2);
        r7 = " doesn't exist or is a directory";
        r5 = r5.append(r7);
        r5 = r5.toString();
        r4.println(r5);
    L_0x0048:
        r3 = new java.io.BufferedInputStream;
        r4 = r2.toPath();
        r5 = new java.nio.file.OpenOption[r8];
        r4 = java.nio.file.Files.newInputStream(r4, r5);
        r3.<init>(r4);
        r1 = createArchiveInputStream(r10, r3);	 Catch:{ Throwable -> 0x0096, all -> 0x00be }
        r4 = 0;
        r5 = java.lang.System.out;	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r7 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r7.<init>();	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r8 = "Created ";
        r7 = r7.append(r8);	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r8 = r1.toString();	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r7 = r7.append(r8);	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r7 = r7.toString();	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r5.println(r7);	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
    L_0x0078:
        r0 = r1.getNextEntry();	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        if (r0 == 0) goto L_0x00a3;
    L_0x007e:
        r5 = java.lang.System.out;	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r7 = r0.getName();	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        r5.println(r7);	 Catch:{ Throwable -> 0x0088, all -> 0x00db }
        goto L_0x0078;
    L_0x0088:
        r4 = move-exception;
        throw r4;	 Catch:{ all -> 0x008a }
    L_0x008a:
        r5 = move-exception;
        r9 = r5;
        r5 = r4;
        r4 = r9;
    L_0x008e:
        if (r1 == 0) goto L_0x0095;
    L_0x0090:
        if (r5 == 0) goto L_0x00c9;
    L_0x0092:
        r1.close();	 Catch:{ Throwable -> 0x00c4, all -> 0x00be }
    L_0x0095:
        throw r4;	 Catch:{ Throwable -> 0x0096, all -> 0x00be }
    L_0x0096:
        r4 = move-exception;
        throw r4;	 Catch:{ all -> 0x0098 }
    L_0x0098:
        r5 = move-exception;
        r6 = r4;
        r4 = r5;
    L_0x009b:
        if (r3 == 0) goto L_0x00a2;
    L_0x009d:
        if (r6 == 0) goto L_0x00d7;
    L_0x009f:
        r3.close();	 Catch:{ Throwable -> 0x00d2 }
    L_0x00a2:
        throw r4;
    L_0x00a3:
        if (r1 == 0) goto L_0x00aa;
    L_0x00a5:
        if (r6 == 0) goto L_0x00c0;
    L_0x00a7:
        r1.close();	 Catch:{ Throwable -> 0x00b9, all -> 0x00be }
    L_0x00aa:
        if (r3 == 0) goto L_0x0008;
    L_0x00ac:
        if (r6 == 0) goto L_0x00cd;
    L_0x00ae:
        r3.close();	 Catch:{ Throwable -> 0x00b3 }
        goto L_0x0008;
    L_0x00b3:
        r4 = move-exception;
        r6.addSuppressed(r4);
        goto L_0x0008;
    L_0x00b9:
        r5 = move-exception;
        r4.addSuppressed(r5);	 Catch:{ Throwable -> 0x0096, all -> 0x00be }
        goto L_0x00aa;
    L_0x00be:
        r4 = move-exception;
        goto L_0x009b;
    L_0x00c0:
        r1.close();	 Catch:{ Throwable -> 0x0096, all -> 0x00be }
        goto L_0x00aa;
    L_0x00c4:
        r7 = move-exception;
        r5.addSuppressed(r7);	 Catch:{ Throwable -> 0x0096, all -> 0x00be }
        goto L_0x0095;
    L_0x00c9:
        r1.close();	 Catch:{ Throwable -> 0x0096, all -> 0x00be }
        goto L_0x0095;
    L_0x00cd:
        r3.close();
        goto L_0x0008;
    L_0x00d2:
        r5 = move-exception;
        r6.addSuppressed(r5);
        goto L_0x00a2;
    L_0x00d7:
        r3.close();
        goto L_0x00a2;
    L_0x00db:
        r4 = move-exception;
        r5 = r6;
        goto L_0x008e;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.Lister.main(java.lang.String[]):void");
    }

    private static ArchiveInputStream createArchiveInputStream(String[] args, InputStream fis) throws ArchiveException {
        if (args.length > 1) {
            return factory.createArchiveInputStream(args[1], fis);
        }
        return factory.createArchiveInputStream(fis);
    }

    private static void usage() {
        System.out.println("Parameters: archive-name [archive-type]");
    }
}
