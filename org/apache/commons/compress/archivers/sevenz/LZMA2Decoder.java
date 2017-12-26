package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.LZMA2InputStream;
import org.tukaani.xz.LZMA2Options;

class LZMA2Decoder extends CoderBase {
    LZMA2Decoder() {
        super(LZMA2Options.class, Number.class);
    }

    InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password) throws IOException {
        try {
            return new LZMA2InputStream(in, getDictionarySize(coder));
        } catch (IllegalArgumentException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    OutputStream encode(OutputStream out, Object opts) throws IOException {
        return getOptions(opts).getOutputStream(new FinishableWrapperOutputStream(out));
    }

    byte[] getOptionsAsProperties(Object opts) {
        int dictSize = getDictSize(opts);
        int secondBit = (dictSize >>> (30 - Integer.numberOfLeadingZeros(dictSize))) - 2;
        return new byte[]{(byte) (((19 - Integer.numberOfLeadingZeros(dictSize)) * 2) + secondBit)};
    }

    Object getOptionsFromCoder(Coder coder, InputStream in) {
        return Integer.valueOf(getDictionarySize(coder));
    }

    private int getDictSize(Object opts) {
        if (opts instanceof LZMA2Options) {
            return ((LZMA2Options) opts).getDictSize();
        }
        return numberOptionOrDefault(opts);
    }

    private int getDictionarySize(Coder coder) throws IllegalArgumentException {
        int dictionarySizeBits = coder.properties[0] & 255;
        if ((dictionarySizeBits & -64) != 0) {
            throw new IllegalArgumentException("Unsupported LZMA2 property bits");
        } else if (dictionarySizeBits > 40) {
            throw new IllegalArgumentException("Dictionary larger than 4GiB maximum size");
        } else if (dictionarySizeBits == 40) {
            return -1;
        } else {
            return ((dictionarySizeBits & 1) | 2) << ((dictionarySizeBits / 2) + 11);
        }
    }

    private LZMA2Options getOptions(Object opts) throws IOException {
        if (opts instanceof LZMA2Options) {
            return (LZMA2Options) opts;
        }
        LZMA2Options options = new LZMA2Options();
        options.setDictSize(numberOptionOrDefault(opts));
        return options;
    }

    private int numberOptionOrDefault(Object opts) {
        return CoderBase.numberOptionOrDefault(opts, 8388608);
    }
}
