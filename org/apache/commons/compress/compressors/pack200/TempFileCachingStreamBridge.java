package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;

class TempFileCachingStreamBridge extends StreamBridge {
    private final File f692f = File.createTempFile("commons-compress", "packtemp");

    TempFileCachingStreamBridge() throws IOException {
        this.f692f.deleteOnExit();
        this.out = Files.newOutputStream(this.f692f.toPath(), new OpenOption[0]);
    }

    InputStream getInputView() throws IOException {
        this.out.close();
        return new FilterInputStream(Files.newInputStream(this.f692f.toPath(), new OpenOption[0])) {
            public void close() throws IOException {
                super.close();
                TempFileCachingStreamBridge.this.f692f.delete();
            }
        };
    }
}
