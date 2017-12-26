package org.apache.commons.compress.compressors.pack200;

import java.io.IOException;

public enum Pack200Strategy {
    IN_MEMORY {
        StreamBridge newStreamBridge() {
            return new InMemoryCachingStreamBridge();
        }
    },
    TEMP_FILE {
        StreamBridge newStreamBridge() throws IOException {
            return new TempFileCachingStreamBridge();
        }
    };

    abstract StreamBridge newStreamBridge() throws IOException;
}
