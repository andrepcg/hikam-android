package org.apache.commons.compress.archivers.sevenz;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;
import org.tukaani.xz.ARMOptions;
import org.tukaani.xz.ARMThumbOptions;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.IA64Options;
import org.tukaani.xz.PowerPCOptions;
import org.tukaani.xz.SPARCOptions;
import org.tukaani.xz.X86Options;

class Coders {
    private static final Map<SevenZMethod, CoderBase> CODER_MAP = new C07901();

    static class C07901 extends HashMap<SevenZMethod, CoderBase> {
        private static final long serialVersionUID = 1664829131806520867L;

        C07901() {
            put(SevenZMethod.COPY, new CopyDecoder());
            put(SevenZMethod.LZMA, new LZMADecoder());
            put(SevenZMethod.LZMA2, new LZMA2Decoder());
            put(SevenZMethod.DEFLATE, new DeflateDecoder());
            put(SevenZMethod.BZIP2, new BZIP2Decoder());
            put(SevenZMethod.AES256SHA256, new AES256SHA256Decoder());
            put(SevenZMethod.BCJ_X86_FILTER, new BCJDecoder(new X86Options()));
            put(SevenZMethod.BCJ_PPC_FILTER, new BCJDecoder(new PowerPCOptions()));
            put(SevenZMethod.BCJ_IA64_FILTER, new BCJDecoder(new IA64Options()));
            put(SevenZMethod.BCJ_ARM_FILTER, new BCJDecoder(new ARMOptions()));
            put(SevenZMethod.BCJ_ARM_THUMB_FILTER, new BCJDecoder(new ARMThumbOptions()));
            put(SevenZMethod.BCJ_SPARC_FILTER, new BCJDecoder(new SPARCOptions()));
            put(SevenZMethod.DELTA_FILTER, new DeltaDecoder());
        }
    }

    private static class DummyByteAddingInputStream extends FilterInputStream {
        private boolean addDummyByte;

        private DummyByteAddingInputStream(InputStream in) {
            super(in);
            this.addDummyByte = true;
        }

        public int read() throws IOException {
            int result = super.read();
            if (result != -1 || !this.addDummyByte) {
                return result;
            }
            this.addDummyByte = false;
            return 0;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int result = super.read(b, off, len);
            if (result != -1 || !this.addDummyByte) {
                return result;
            }
            this.addDummyByte = false;
            b[off] = (byte) 0;
            return 1;
        }
    }

    static class BCJDecoder extends CoderBase {
        private final FilterOptions opts;

        BCJDecoder(FilterOptions opts) {
            super(new Class[0]);
            this.opts = opts;
        }

        InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password) throws IOException {
            try {
                return this.opts.getInputStream(in);
            } catch (AssertionError e) {
                throw new IOException("BCJ filter used in " + archiveName + " needs XZ for Java > 1.4 - see http://commons.apache.org/proper/commons-compress/limitations.html#7Z", e);
            }
        }

        OutputStream encode(OutputStream out, Object options) {
            return new FlushShieldFilterOutputStream(this.opts.getOutputStream(new FinishableWrapperOutputStream(out)));
        }
    }

    static class BZIP2Decoder extends CoderBase {
        BZIP2Decoder() {
            super(Number.class);
        }

        InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password) throws IOException {
            return new BZip2CompressorInputStream(in);
        }

        OutputStream encode(OutputStream out, Object options) throws IOException {
            return new BZip2CompressorOutputStream(out, CoderBase.numberOptionOrDefault(options, 9));
        }
    }

    static class CopyDecoder extends CoderBase {
        CopyDecoder() {
            super(new Class[0]);
        }

        InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password) throws IOException {
            return in;
        }

        OutputStream encode(OutputStream out, Object options) {
            return out;
        }
    }

    static class DeflateDecoder extends CoderBase {
        DeflateDecoder() {
            super(Number.class);
        }

        InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password) throws IOException {
            final Inflater inflater = new Inflater(true);
            final InflaterInputStream inflaterInputStream = new InflaterInputStream(new DummyByteAddingInputStream(in), inflater);
            return new InputStream() {
                public int read() throws IOException {
                    return inflaterInputStream.read();
                }

                public int read(byte[] b, int off, int len) throws IOException {
                    return inflaterInputStream.read(b, off, len);
                }

                public int read(byte[] b) throws IOException {
                    return inflaterInputStream.read(b);
                }

                public void close() throws IOException {
                    try {
                        inflaterInputStream.close();
                    } finally {
                        inflater.end();
                    }
                }
            };
        }

        OutputStream encode(OutputStream out, Object options) {
            final Deflater deflater = new Deflater(CoderBase.numberOptionOrDefault(options, 9), true);
            final DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(out, deflater);
            return new OutputStream() {
                public void write(int b) throws IOException {
                    deflaterOutputStream.write(b);
                }

                public void write(byte[] b) throws IOException {
                    deflaterOutputStream.write(b);
                }

                public void write(byte[] b, int off, int len) throws IOException {
                    deflaterOutputStream.write(b, off, len);
                }

                public void close() throws IOException {
                    try {
                        deflaterOutputStream.close();
                    } finally {
                        deflater.end();
                    }
                }
            };
        }
    }

    Coders() {
    }

    static CoderBase findByMethod(SevenZMethod method) {
        return (CoderBase) CODER_MAP.get(method);
    }

    static InputStream addDecoder(String archiveName, InputStream is, long uncompressedLength, Coder coder, byte[] password) throws IOException {
        CoderBase cb = findByMethod(SevenZMethod.byId(coder.decompressionMethodId));
        if (cb != null) {
            return cb.decode(archiveName, is, uncompressedLength, coder, password);
        }
        throw new IOException("Unsupported compression method " + Arrays.toString(coder.decompressionMethodId) + " used in " + archiveName);
    }

    static OutputStream addEncoder(OutputStream out, SevenZMethod method, Object options) throws IOException {
        CoderBase cb = findByMethod(method);
        if (cb != null) {
            return cb.encode(out, options);
        }
        throw new IOException("Unsupported compression method " + method);
    }
}
