package okhttp3;

import java.io.IOException;
import javax.annotation.Nullable;

public interface Authenticator {
    public static final Authenticator NONE = new C11531();

    class C11531 implements Authenticator {
        C11531() {
        }

        public Request authenticate(Route route, Response response) {
            return null;
        }
    }

    @Nullable
    Request authenticate(Route route, Response response) throws IOException;
}
