package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.compression.JdkZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.DetectionUtil;
import org.jboss.netty.util.internal.StringUtil;
import org.jboss.netty.util.internal.SystemPropertyUtil;

public class HttpContentCompressor extends HttpContentEncoder {
    private static final int DEFAULT_JDK_MEM_LEVEL = 8;
    private static final int DEFAULT_JDK_WINDOW_SIZE = 15;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpContentCompressor.class);
    private static final boolean noJdkZlibEncoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibEncoder", false);
    private final int compressionLevel;
    private final int memLevel;
    private final int windowBits;

    static {
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.noJdkZlibEncoder: " + noJdkZlibEncoder);
        }
    }

    public HttpContentCompressor() {
        this(6);
    }

    public HttpContentCompressor(int compressionLevel) {
        this(compressionLevel, 15, 8);
    }

    public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        } else if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        } else if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        } else {
            this.compressionLevel = compressionLevel;
            this.windowBits = windowBits;
            this.memLevel = memLevel;
        }
    }

    protected EncoderEmbedder<ChannelBuffer> newContentEncoder(HttpMessage msg, String acceptEncoding) throws Exception {
        String contentEncoding = msg.headers().get("Content-Encoding");
        if ((contentEncoding != null && !"identity".equalsIgnoreCase(contentEncoding)) || determineWrapper(acceptEncoding) == null) {
            return null;
        }
        if (DetectionUtil.javaVersion() < 7 || noJdkZlibEncoder || this.windowBits != 15 || this.memLevel != 8) {
            return new EncoderEmbedder(new ZlibEncoder(wrapper, this.compressionLevel, this.windowBits, this.memLevel));
        }
        return new EncoderEmbedder(new JdkZlibEncoder(wrapper, this.compressionLevel));
    }

    protected String getTargetContentEncoding(String acceptEncoding) throws Exception {
        ZlibWrapper wrapper = determineWrapper(acceptEncoding);
        if (wrapper == null) {
            return null;
        }
        switch (wrapper) {
            case GZIP:
                return "gzip";
            case ZLIB:
                return "deflate";
            default:
                throw new Error();
        }
    }

    private static ZlibWrapper determineWrapper(String acceptEncoding) {
        float starQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (String encoding : StringUtil.split(acceptEncoding, ',')) {
            float q = 1.0f;
            int equalsPos = encoding.indexOf(61);
            if (equalsPos != -1) {
                try {
                    q = Float.valueOf(encoding.substring(equalsPos + 1)).floatValue();
                } catch (NumberFormatException e) {
                    q = 0.0f;
                }
            }
            if (encoding.indexOf(42) >= 0) {
                starQ = q;
            } else if (encoding.contains("gzip") && q > gzipQ) {
                gzipQ = q;
            } else if (encoding.contains("deflate") && q > deflateQ) {
                deflateQ = q;
            }
        }
        if (gzipQ <= 0.0f && deflateQ <= 0.0f) {
            if (starQ > 0.0f) {
                if (gzipQ == -1.0f) {
                    return ZlibWrapper.GZIP;
                }
                if (deflateQ == -1.0f) {
                    return ZlibWrapper.ZLIB;
                }
            }
            return null;
        } else if (gzipQ >= deflateQ) {
            return ZlibWrapper.GZIP;
        } else {
            return ZlibWrapper.ZLIB;
        }
    }
}
