package okhttp3.internal.publicsuffix;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.internal.Util;
import okio.GzipSource;
import okio.Okio;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public final class PublicSuffixDatabase {
    private static final String[] EMPTY_RULE = new String[0];
    private static final byte EXCEPTION_MARKER = (byte) 33;
    private static final String[] PREVAILING_RULE = new String[]{WebSocketServerHandshaker.SUB_PROTOCOL_WILDCARD};
    public static final String PUBLIC_SUFFIX_RESOURCE = "publicsuffixes.gz";
    private static final byte[] WILDCARD_LABEL = new byte[]{(byte) 42};
    private static final PublicSuffixDatabase instance = new PublicSuffixDatabase();
    private final AtomicBoolean listRead = new AtomicBoolean(false);
    private byte[] publicSuffixExceptionListBytes;
    private byte[] publicSuffixListBytes;
    private final CountDownLatch readCompleteLatch = new CountDownLatch(1);

    private void readTheListUninterruptibly() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x000d in list [B:11:0x001e]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r5 = this;
        r1 = 0;
    L_0x0001:
        r5.readTheList();	 Catch:{ InterruptedIOException -> 0x000e, IOException -> 0x0011, all -> 0x0026 }
        if (r1 == 0) goto L_0x000d;
    L_0x0006:
        r2 = java.lang.Thread.currentThread();
        r2.interrupt();
    L_0x000d:
        return;
    L_0x000e:
        r0 = move-exception;
        r1 = 1;
        goto L_0x0001;
    L_0x0011:
        r0 = move-exception;
        r2 = okhttp3.internal.platform.Platform.get();	 Catch:{ InterruptedIOException -> 0x000e, IOException -> 0x0011, all -> 0x0026 }
        r3 = 5;	 Catch:{ InterruptedIOException -> 0x000e, IOException -> 0x0011, all -> 0x0026 }
        r4 = "Failed to read public suffix list";	 Catch:{ InterruptedIOException -> 0x000e, IOException -> 0x0011, all -> 0x0026 }
        r2.log(r3, r4, r0);	 Catch:{ InterruptedIOException -> 0x000e, IOException -> 0x0011, all -> 0x0026 }
        if (r1 == 0) goto L_0x000d;
    L_0x001e:
        r2 = java.lang.Thread.currentThread();
        r2.interrupt();
        goto L_0x000d;
    L_0x0026:
        r2 = move-exception;
        if (r1 == 0) goto L_0x0030;
    L_0x0029:
        r3 = java.lang.Thread.currentThread();
        r3.interrupt();
    L_0x0030:
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.publicsuffix.PublicSuffixDatabase.readTheListUninterruptibly():void");
    }

    public static PublicSuffixDatabase get() {
        return instance;
    }

    public String getEffectiveTldPlusOne(String domain) {
        if (domain == null) {
            throw new NullPointerException("domain == null");
        }
        String[] domainLabels = IDN.toUnicode(domain).split("\\.");
        String[] rule = findMatchingRule(domainLabels);
        if (domainLabels.length == rule.length && rule[0].charAt(0) != '!') {
            return null;
        }
        int firstLabelOffset;
        if (rule[0].charAt(0) == '!') {
            firstLabelOffset = domainLabels.length - rule.length;
        } else {
            firstLabelOffset = domainLabels.length - (rule.length + 1);
        }
        StringBuilder effectiveTldPlusOne = new StringBuilder();
        String[] punycodeLabels = domain.split("\\.");
        for (int i = firstLabelOffset; i < punycodeLabels.length; i++) {
            effectiveTldPlusOne.append(punycodeLabels[i]).append('.');
        }
        effectiveTldPlusOne.deleteCharAt(effectiveTldPlusOne.length() - 1);
        return effectiveTldPlusOne.toString();
    }

    private String[] findMatchingRule(String[] domainLabels) {
        int i;
        int labelIndex;
        if (this.listRead.get() || !this.listRead.compareAndSet(false, true)) {
            try {
                this.readCompleteLatch.await();
            } catch (InterruptedException e) {
            }
        } else {
            readTheListUninterruptibly();
        }
        synchronized (this) {
            if (this.publicSuffixListBytes == null) {
                throw new IllegalStateException("Unable to load publicsuffixes.gz resource from the classpath.");
            }
        }
        byte[][] domainLabelsUtf8Bytes = new byte[domainLabels.length][];
        for (i = 0; i < domainLabels.length; i++) {
            domainLabelsUtf8Bytes[i] = domainLabels[i].getBytes(Util.UTF_8);
        }
        String exactMatch = null;
        for (i = 0; i < domainLabelsUtf8Bytes.length; i++) {
            String rule = binarySearchBytes(this.publicSuffixListBytes, domainLabelsUtf8Bytes, i);
            if (rule != null) {
                exactMatch = rule;
                break;
            }
        }
        String wildcardMatch = null;
        if (domainLabelsUtf8Bytes.length > 1) {
            byte[][] labelsWithWildcard = (byte[][]) domainLabelsUtf8Bytes.clone();
            for (labelIndex = 0; labelIndex < labelsWithWildcard.length - 1; labelIndex++) {
                labelsWithWildcard[labelIndex] = WILDCARD_LABEL;
                rule = binarySearchBytes(this.publicSuffixListBytes, labelsWithWildcard, labelIndex);
                if (rule != null) {
                    wildcardMatch = rule;
                    break;
                }
            }
        }
        String exception = null;
        if (wildcardMatch != null) {
            for (labelIndex = 0; labelIndex < domainLabelsUtf8Bytes.length - 1; labelIndex++) {
                rule = binarySearchBytes(this.publicSuffixExceptionListBytes, domainLabelsUtf8Bytes, labelIndex);
                if (rule != null) {
                    exception = rule;
                    break;
                }
            }
        }
        if (exception != null) {
            return ("!" + exception).split("\\.");
        }
        if (exactMatch == null && wildcardMatch == null) {
            return PREVAILING_RULE;
        }
        String[] exactRuleLabels;
        String[] wildcardRuleLabels;
        if (exactMatch != null) {
            exactRuleLabels = exactMatch.split("\\.");
        } else {
            exactRuleLabels = EMPTY_RULE;
        }
        if (wildcardMatch != null) {
            wildcardRuleLabels = wildcardMatch.split("\\.");
        } else {
            wildcardRuleLabels = EMPTY_RULE;
        }
        return exactRuleLabels.length <= wildcardRuleLabels.length ? wildcardRuleLabels : exactRuleLabels;
    }

    private static String binarySearchBytes(byte[] bytesToSearch, byte[][] labels, int labelIndex) {
        int low = 0;
        int high = bytesToSearch.length;
        while (low < high) {
            int mid = (low + high) / 2;
            while (mid > -1 && bytesToSearch[mid] != (byte) 10) {
                mid--;
            }
            mid++;
            int end = 1;
            while (bytesToSearch[mid + end] != (byte) 10) {
                end++;
            }
            int publicSuffixLength = (mid + end) - mid;
            int currentLabelIndex = labelIndex;
            int currentLabelByteIndex = 0;
            int publicSuffixByteIndex = 0;
            boolean expectDot = false;
            while (true) {
                int byte0;
                if (expectDot) {
                    byte0 = 46;
                    expectDot = false;
                } else {
                    byte0 = labels[currentLabelIndex][currentLabelByteIndex] & 255;
                }
                int compareResult = byte0 - (bytesToSearch[mid + publicSuffixByteIndex] & 255);
                if (compareResult == 0) {
                    publicSuffixByteIndex++;
                    currentLabelByteIndex++;
                    if (publicSuffixByteIndex != publicSuffixLength) {
                        if (labels[currentLabelIndex].length == currentLabelByteIndex) {
                            if (currentLabelIndex == labels.length - 1) {
                                break;
                            }
                            currentLabelIndex++;
                            currentLabelByteIndex = -1;
                            expectDot = true;
                        }
                    } else {
                        break;
                    }
                }
                break;
            }
            if (compareResult < 0) {
                high = mid - 1;
            } else if (compareResult > 0) {
                low = (mid + end) + 1;
            } else {
                int publicSuffixBytesLeft = publicSuffixLength - publicSuffixByteIndex;
                int labelBytesLeft = labels[currentLabelIndex].length - currentLabelByteIndex;
                for (int i = currentLabelIndex + 1; i < labels.length; i++) {
                    labelBytesLeft += labels[i].length;
                }
                if (labelBytesLeft < publicSuffixBytesLeft) {
                    high = mid - 1;
                } else if (labelBytesLeft <= publicSuffixBytesLeft) {
                    return new String(bytesToSearch, mid, publicSuffixLength, Util.UTF_8);
                } else {
                    low = (mid + end) + 1;
                }
            }
        }
        return null;
    }

    private void readTheList() throws IOException {
        InputStream resource = PublicSuffixDatabase.class.getResourceAsStream(PUBLIC_SUFFIX_RESOURCE);
        if (resource != null) {
            Closeable bufferedSource = Okio.buffer(new GzipSource(Okio.source(resource)));
            try {
                byte[] publicSuffixListBytes = new byte[bufferedSource.readInt()];
                bufferedSource.readFully(publicSuffixListBytes);
                byte[] publicSuffixExceptionListBytes = new byte[bufferedSource.readInt()];
                bufferedSource.readFully(publicSuffixExceptionListBytes);
                synchronized (this) {
                    this.publicSuffixListBytes = publicSuffixListBytes;
                    this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
                }
                this.readCompleteLatch.countDown();
            } finally {
                Util.closeQuietly(bufferedSource);
            }
        }
    }

    void setListBytes(byte[] publicSuffixListBytes, byte[] publicSuffixExceptionListBytes) {
        this.publicSuffixListBytes = publicSuffixListBytes;
        this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
        this.listRead.set(true);
        this.readCompleteLatch.countDown();
    }
}
