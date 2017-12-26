package okhttp3;

import java.nio.charset.Charset;
import okhttp3.internal.Util;
import okio.ByteString;

public final class Credentials {
    private Credentials() {
    }

    public static String basic(String userName, String password) {
        return basic(userName, password, Util.ISO_8859_1);
    }

    public static String basic(String userName, String password, Charset charset) {
        return "Basic " + ByteString.encodeString(userName + ":" + password, charset).base64();
    }
}
