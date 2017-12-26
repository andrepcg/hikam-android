package okhttp3.internal;

import android.support.v4.media.TransportMediator;
import java.io.Closeable;
import java.io.IOException;
import java.net.IDN;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import org.apache.commons.compress.utils.CharsetNames;

public final class Util {
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final RequestBody EMPTY_REQUEST = RequestBody.create(null, EMPTY_BYTE_ARRAY);
    public static final ResponseBody EMPTY_RESPONSE = ResponseBody.create(null, EMPTY_BYTE_ARRAY);
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Comparator<String> NATURAL_ORDER = new C07731();
    public static final TimeZone UTC = TimeZone.getTimeZone("GMT");
    private static final Charset UTF_16_BE = Charset.forName(CharsetNames.UTF_16BE);
    private static final ByteString UTF_16_BE_BOM = ByteString.decodeHex("feff");
    private static final Charset UTF_16_LE = Charset.forName(CharsetNames.UTF_16LE);
    private static final ByteString UTF_16_LE_BOM = ByteString.decodeHex("fffe");
    private static final Charset UTF_32_BE = Charset.forName("UTF-32BE");
    private static final ByteString UTF_32_BE_BOM = ByteString.decodeHex("0000ffff");
    private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");
    private static final ByteString UTF_32_LE_BOM = ByteString.decodeHex("ffff0000");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final ByteString UTF_8_BOM = ByteString.decodeHex("efbbbf");
    private static final Pattern VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");

    class C07731 implements Comparator<String> {
        C07731() {
        }

        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    class C07742 implements ThreadFactory {
        final /* synthetic */ boolean val$daemon;
        final /* synthetic */ String val$name;

        C07742(String str, boolean z) {
            this.val$name = str;
            this.val$daemon = z;
        }

        public Thread newThread(Runnable runnable) {
            Thread result = new Thread(runnable, this.val$name);
            result.setDaemon(this.val$daemon);
            return result;
        }
    }

    public static boolean skipAll(okio.Source r12, int r13, java.util.concurrent.TimeUnit r14) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x006c in list []
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
        r6 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r2 = java.lang.System.nanoTime();
        r8 = r12.timeout();
        r8 = r8.hasDeadline();
        if (r8 == 0) goto L_0x0052;
    L_0x0013:
        r8 = r12.timeout();
        r8 = r8.deadlineNanoTime();
        r4 = r8 - r2;
    L_0x001d:
        r8 = r12.timeout();
        r10 = (long) r13;
        r10 = r14.toNanos(r10);
        r10 = java.lang.Math.min(r4, r10);
        r10 = r10 + r2;
        r8.deadlineNanoTime(r10);
        r1 = new okio.Buffer;	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0076 }
        r1.<init>();	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0076 }
    L_0x0033:
        r8 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0076 }
        r8 = r12.read(r1, r8);	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0076 }
        r10 = -1;	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0076 }
        r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0076 }
        if (r8 == 0) goto L_0x0054;	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0076 }
    L_0x003f:
        r1.clear();	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0076 }
        goto L_0x0033;
    L_0x0043:
        r0 = move-exception;
        r8 = 0;
        r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r6 != 0) goto L_0x006c;
    L_0x0049:
        r6 = r12.timeout();
        r6.clearDeadline();
    L_0x0050:
        r6 = r8;
    L_0x0051:
        return r6;
    L_0x0052:
        r4 = r6;
        goto L_0x001d;
    L_0x0054:
        r8 = 1;
        r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r6 != 0) goto L_0x0062;
    L_0x0059:
        r6 = r12.timeout();
        r6.clearDeadline();
    L_0x0060:
        r6 = r8;
        goto L_0x0051;
    L_0x0062:
        r6 = r12.timeout();
        r10 = r2 + r4;
        r6.deadlineNanoTime(r10);
        goto L_0x0060;
    L_0x006c:
        r6 = r12.timeout();
        r10 = r2 + r4;
        r6.deadlineNanoTime(r10);
        goto L_0x0050;
    L_0x0076:
        r8 = move-exception;
        r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r6 != 0) goto L_0x0083;
    L_0x007b:
        r6 = r12.timeout();
        r6.clearDeadline();
    L_0x0082:
        throw r8;
    L_0x0083:
        r6 = r12.timeout();
        r10 = r2 + r4;
        r6.deadlineNanoTime(r10);
        goto L_0x0082;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.Util.skipAll(okio.Source, int, java.util.concurrent.TimeUnit):boolean");
    }

    private Util() {
    }

    public static void checkOffsetAndCount(long arrayLength, long offset, long count) {
        if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (AssertionError e) {
                if (!isAndroidGetsocknameError(e)) {
                    throw e;
                }
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e2) {
            }
        }
    }

    public static void closeQuietly(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static boolean discard(Source source, int timeout, TimeUnit timeUnit) {
        try {
            return skipAll(source, timeout, timeUnit);
        } catch (IOException e) {
            return false;
        }
    }

    public static <T> List<T> immutableList(List<T> list) {
        return Collections.unmodifiableList(new ArrayList(list));
    }

    public static <T> List<T> immutableList(T... elements) {
        return Collections.unmodifiableList(Arrays.asList((Object[]) elements.clone()));
    }

    public static ThreadFactory threadFactory(String name, boolean daemon) {
        return new C07742(name, daemon);
    }

    public static String[] intersect(Comparator<? super String> comparator, String[] first, String[] second) {
        List<String> result = new ArrayList();
        for (String a : first) {
            for (String b : second) {
                if (comparator.compare(a, b) == 0) {
                    result.add(a);
                    break;
                }
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public static boolean nonEmptyIntersection(Comparator<String> comparator, String[] first, String[] second) {
        if (first == null || second == null || first.length == 0 || second.length == 0) {
            return false;
        }
        for (String a : first) {
            for (String b : second) {
                if (comparator.compare(a, b) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String hostHeader(HttpUrl url, boolean includeDefaultPort) {
        String host;
        if (url.host().contains(":")) {
            host = "[" + url.host() + "]";
        } else {
            host = url.host();
        }
        if (includeDefaultPort || url.port() != HttpUrl.defaultPort(url.scheme())) {
            return host + ":" + url.port();
        }
        return host;
    }

    public static String toHumanReadableAscii(String s) {
        int i = 0;
        int length = s.length();
        while (i < length) {
            int c = s.codePointAt(i);
            if (c <= 31 || c >= TransportMediator.KEYCODE_MEDIA_PAUSE) {
                Buffer buffer = new Buffer();
                buffer.writeUtf8(s, 0, i);
                int j = i;
                while (j < length) {
                    c = s.codePointAt(j);
                    int i2 = (c <= 31 || c >= TransportMediator.KEYCODE_MEDIA_PAUSE) ? 63 : c;
                    buffer.writeUtf8CodePoint(i2);
                    j += Character.charCount(c);
                }
                return buffer.readUtf8();
            }
            i += Character.charCount(c);
        }
        return s;
    }

    public static boolean isAndroidGetsocknameError(AssertionError e) {
        return (e.getCause() == null || e.getMessage() == null || !e.getMessage().contains("getsockname failed")) ? false : true;
    }

    public static int indexOf(Comparator<String> comparator, String[] array, String value) {
        int size = array.length;
        for (int i = 0; i < size; i++) {
            if (comparator.compare(array[i], value) == 0) {
                return i;
            }
        }
        return -1;
    }

    public static String[] concat(String[] array, String value) {
        String[] result = new String[(array.length + 1)];
        System.arraycopy(array, 0, result, 0, array.length);
        result[result.length - 1] = value;
        return result;
    }

    public static int skipLeadingAsciiWhitespace(String input, int pos, int limit) {
        int i = pos;
        while (i < limit) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    i++;
                default:
                    return i;
            }
        }
        return limit;
    }

    public static int skipTrailingAsciiWhitespace(String input, int pos, int limit) {
        int i = limit - 1;
        while (i >= pos) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    i--;
                default:
                    return i + 1;
            }
        }
        return pos;
    }

    public static String trimSubstring(String string, int pos, int limit) {
        int start = skipLeadingAsciiWhitespace(string, pos, limit);
        return string.substring(start, skipTrailingAsciiWhitespace(string, start, limit));
    }

    public static int delimiterOffset(String input, int pos, int limit, String delimiters) {
        for (int i = pos; i < limit; i++) {
            if (delimiters.indexOf(input.charAt(i)) != -1) {
                return i;
            }
        }
        return limit;
    }

    public static int delimiterOffset(String input, int pos, int limit, char delimiter) {
        for (int i = pos; i < limit; i++) {
            if (input.charAt(i) == delimiter) {
                return i;
            }
        }
        return limit;
    }

    public static String canonicalizeHost(String host) {
        if (host.contains(":")) {
            InetAddress inetAddress;
            if (host.startsWith("[") && host.endsWith("]")) {
                inetAddress = decodeIpv6(host, 1, host.length() - 1);
            } else {
                inetAddress = decodeIpv6(host, 0, host.length());
            }
            if (inetAddress == null) {
                return null;
            }
            byte[] address = inetAddress.getAddress();
            if (address.length == 16) {
                return inet6AddressToAscii(address);
            }
            throw new AssertionError("Invalid IPv6 address: '" + host + "'");
        }
        try {
            String result = IDN.toASCII(host).toLowerCase(Locale.US);
            if (result.isEmpty()) {
                return null;
            }
            if (containsInvalidHostnameAsciiCodes(result)) {
                return null;
            }
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean containsInvalidHostnameAsciiCodes(String hostnameAscii) {
        for (int i = 0; i < hostnameAscii.length(); i++) {
            char c = hostnameAscii.charAt(i);
            if (c <= '\u001f' || c >= '' || " #%/:?@[\\]".indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    public static int indexOfControlOrNonAscii(String input) {
        int i = 0;
        int length = input.length();
        while (i < length) {
            char c = input.charAt(i);
            if (c <= '\u001f' || c >= '') {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static boolean verifyAsIpAddress(String host) {
        return VERIFY_AS_IP_ADDRESS.matcher(host).matches();
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }

    public static Charset bomAwareCharset(BufferedSource source, Charset charset) throws IOException {
        if (source.rangeEquals(0, UTF_8_BOM)) {
            source.skip((long) UTF_8_BOM.size());
            return UTF_8;
        } else if (source.rangeEquals(0, UTF_16_BE_BOM)) {
            source.skip((long) UTF_16_BE_BOM.size());
            return UTF_16_BE;
        } else if (source.rangeEquals(0, UTF_16_LE_BOM)) {
            source.skip((long) UTF_16_LE_BOM.size());
            return UTF_16_LE;
        } else if (source.rangeEquals(0, UTF_32_BE_BOM)) {
            source.skip((long) UTF_32_BE_BOM.size());
            return UTF_32_BE;
        } else if (!source.rangeEquals(0, UTF_32_LE_BOM)) {
            return charset;
        } else {
            source.skip((long) UTF_32_LE_BOM.size());
            return UTF_32_LE;
        }
    }

    public static int checkDuration(String name, long duration, TimeUnit unit) {
        if (duration < 0) {
            throw new IllegalArgumentException(name + " < 0");
        } else if (unit == null) {
            throw new NullPointerException("unit == null");
        } else {
            long millis = unit.toMillis(duration);
            if (millis > 2147483647L) {
                throw new IllegalArgumentException(name + " too large.");
            } else if (millis != 0 || duration <= 0) {
                return (int) millis;
            } else {
                throw new IllegalArgumentException(name + " too small.");
            }
        }
    }

    public static AssertionError assertionError(String message, Exception e) {
        return (AssertionError) new AssertionError(message).initCause(e);
    }

    public static int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 97) + 10;
        }
        if (c < 'A' || c > 'F') {
            return -1;
        }
        return (c - 65) + 10;
    }

    @Nullable
    private static InetAddress decodeIpv6(String input, int pos, int limit) {
        byte[] address = new byte[16];
        int b = 0;
        int compress = -1;
        int groupOffset = -1;
        int i = pos;
        while (i < limit) {
            if (b == address.length) {
                return null;
            }
            if (i + 2 <= limit && input.regionMatches(i, "::", 0, 2)) {
                if (compress == -1) {
                    i += 2;
                    b += 2;
                    compress = b;
                    if (i == limit) {
                        break;
                    }
                }
                return null;
            } else if (b != 0) {
                if (input.regionMatches(i, ":", 0, 1)) {
                    i++;
                } else if (!input.regionMatches(i, ".", 0, 1)) {
                    return null;
                } else {
                    if (!decodeIpv4Suffix(input, groupOffset, limit, address, b - 2)) {
                        return null;
                    }
                    b += 2;
                }
            }
            int value = 0;
            groupOffset = i;
            while (i < limit) {
                int hexDigit = decodeHexDigit(input.charAt(i));
                if (hexDigit == -1) {
                    break;
                }
                value = (value << 4) + hexDigit;
                i++;
            }
            int groupLength = i - groupOffset;
            if (groupLength == 0 || groupLength > 4) {
                return null;
            }
            int i2 = b + 1;
            address[b] = (byte) ((value >>> 8) & 255);
            b = i2 + 1;
            address[i2] = (byte) (value & 255);
        }
        if (b != address.length) {
            if (compress == -1) {
                return null;
            }
            System.arraycopy(address, compress, address, address.length - (b - compress), b - compress);
            Arrays.fill(address, compress, (address.length - b) + compress, (byte) 0);
        }
        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    private static boolean decodeIpv4Suffix(String input, int pos, int limit, byte[] address, int addressOffset) {
        int i = pos;
        int b = addressOffset;
        while (i < limit) {
            if (b == address.length) {
                return false;
            }
            if (b != addressOffset) {
                if (input.charAt(i) != '.') {
                    return false;
                }
                i++;
            }
            int value = 0;
            int groupOffset = i;
            while (i < limit) {
                char c = input.charAt(i);
                if (c < '0' || c > '9') {
                    break;
                } else if (value == 0 && groupOffset != i) {
                    return false;
                } else {
                    value = ((value * 10) + c) - 48;
                    if (value > 255) {
                        return false;
                    }
                    i++;
                }
            }
            if (i - groupOffset == 0) {
                return false;
            }
            int b2 = b + 1;
            address[b] = (byte) value;
            b = b2;
        }
        if (b == addressOffset + 4) {
            return true;
        }
        return false;
    }

    private static String inet6AddressToAscii(byte[] address) {
        int longestRunOffset = -1;
        int longestRunLength = 0;
        int i = 0;
        while (i < address.length) {
            int currentRunOffset = i;
            while (i < 16 && address[i] == (byte) 0 && address[i + 1] == (byte) 0) {
                i += 2;
            }
            int currentRunLength = i - currentRunOffset;
            if (currentRunLength > longestRunLength && currentRunLength >= 4) {
                longestRunOffset = currentRunOffset;
                longestRunLength = currentRunLength;
            }
            i += 2;
        }
        Buffer result = new Buffer();
        i = 0;
        while (i < address.length) {
            if (i == longestRunOffset) {
                result.writeByte(58);
                i += longestRunLength;
                if (i == 16) {
                    result.writeByte(58);
                }
            } else {
                if (i > 0) {
                    result.writeByte(58);
                }
                result.writeHexadecimalUnsignedLong((long) (((address[i] & 255) << 8) | (address[i + 1] & 255)));
                i += 2;
            }
        }
        return result.readUtf8();
    }
}
