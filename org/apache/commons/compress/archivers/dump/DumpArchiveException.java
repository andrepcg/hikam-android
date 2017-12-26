package org.apache.commons.compress.archivers.dump;

import java.io.IOException;

public class DumpArchiveException extends IOException {
    private static final long serialVersionUID = 1;

    public DumpArchiveException(String msg) {
        super(msg);
    }

    public DumpArchiveException(Throwable cause) {
        initCause(cause);
    }

    public DumpArchiveException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }
}
