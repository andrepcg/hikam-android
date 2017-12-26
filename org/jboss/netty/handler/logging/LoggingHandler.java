package org.jboss.netty.handler.logging;

import android.support.v4.media.TransportMediator;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

@Sharable
public class LoggingHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {
    private static final char[] BYTE2CHAR = new char[256];
    private static final String[] BYTE2HEX = new String[256];
    private static final String[] BYTEPADDING = new String[16];
    private static final InternalLogLevel DEFAULT_LEVEL = InternalLogLevel.DEBUG;
    private static final String[] HEXPADDING = new String[16];
    private static final String NEWLINE = String.format("%n", new Object[0]);
    private final boolean hexDump;
    private final InternalLogLevel level;
    private final InternalLogger logger;

    static {
        int i = 0;
        while (i < 10) {
            StringBuilder buf = new StringBuilder(3);
            buf.append(" 0");
            buf.append(i);
            BYTE2HEX[i] = buf.toString();
            i++;
        }
        while (i < 16) {
            buf = new StringBuilder(3);
            buf.append(" 0");
            buf.append((char) ((i + 97) - 10));
            BYTE2HEX[i] = buf.toString();
            i++;
        }
        while (i < BYTE2HEX.length) {
            buf = new StringBuilder(3);
            buf.append(' ');
            buf.append(Integer.toHexString(i));
            BYTE2HEX[i] = buf.toString();
            i++;
        }
        for (i = 0; i < HEXPADDING.length; i++) {
            int j;
            int padding = HEXPADDING.length - i;
            buf = new StringBuilder(padding * 3);
            for (j = 0; j < padding; j++) {
                buf.append("   ");
            }
            HEXPADDING[i] = buf.toString();
        }
        for (i = 0; i < BYTEPADDING.length; i++) {
            padding = BYTEPADDING.length - i;
            buf = new StringBuilder(padding);
            for (j = 0; j < padding; j++) {
                buf.append(' ');
            }
            BYTEPADDING[i] = buf.toString();
        }
        i = 0;
        while (i < BYTE2CHAR.length) {
            if (i <= 31 || i >= TransportMediator.KEYCODE_MEDIA_PAUSE) {
                BYTE2CHAR[i] = '.';
            } else {
                BYTE2CHAR[i] = (char) i;
            }
            i++;
        }
    }

    public LoggingHandler() {
        this(true);
    }

    public LoggingHandler(InternalLogLevel level) {
        this(level, true);
    }

    public LoggingHandler(boolean hexDump) {
        this(DEFAULT_LEVEL, hexDump);
    }

    public LoggingHandler(InternalLogLevel level, boolean hexDump) {
        if (level == null) {
            throw new NullPointerException(Param.LEVEL);
        }
        this.logger = InternalLoggerFactory.getInstance(getClass());
        this.level = level;
        this.hexDump = hexDump;
    }

    public LoggingHandler(Class<?> clazz) {
        this((Class) clazz, true);
    }

    public LoggingHandler(Class<?> clazz, boolean hexDump) {
        this((Class) clazz, DEFAULT_LEVEL, hexDump);
    }

    public LoggingHandler(Class<?> clazz, InternalLogLevel level) {
        this((Class) clazz, level, true);
    }

    public LoggingHandler(Class<?> clazz, InternalLogLevel level, boolean hexDump) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        } else if (level == null) {
            throw new NullPointerException(Param.LEVEL);
        } else {
            this.logger = InternalLoggerFactory.getInstance((Class) clazz);
            this.level = level;
            this.hexDump = hexDump;
        }
    }

    public LoggingHandler(String name) {
        this(name, true);
    }

    public LoggingHandler(String name, boolean hexDump) {
        this(name, DEFAULT_LEVEL, hexDump);
    }

    public LoggingHandler(String name, InternalLogLevel level, boolean hexDump) {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
        } else if (level == null) {
            throw new NullPointerException(Param.LEVEL);
        } else {
            this.logger = InternalLoggerFactory.getInstance(name);
            this.level = level;
            this.hexDump = hexDump;
        }
    }

    public InternalLogger getLogger() {
        return this.logger;
    }

    public InternalLogLevel getLevel() {
        return this.level;
    }

    public void log(ChannelEvent e) {
        if (getLogger().isEnabled(this.level)) {
            String msg = e.toString();
            if (this.hexDump && (e instanceof MessageEvent)) {
                MessageEvent me = (MessageEvent) e;
                if (me.getMessage() instanceof ChannelBuffer) {
                    msg = msg + formatBuffer((ChannelBuffer) me.getMessage());
                }
            }
            if (e instanceof ExceptionEvent) {
                getLogger().log(this.level, msg, ((ExceptionEvent) e).getCause());
            } else {
                getLogger().log(this.level, msg);
            }
        }
    }

    private static String formatBuffer(ChannelBuffer buf) {
        int j;
        int length = buf.readableBytes();
        StringBuilder dump = new StringBuilder((((length % 15 == 0 ? 0 : 1) + (length / 16)) + 4) * 80);
        dump.append(NEWLINE + "         +-------------------------------------------------+" + NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + NEWLINE + "+--------+-------------------------------------------------+----------------+");
        int startIndex = buf.readerIndex();
        int endIndex = buf.writerIndex();
        int i = startIndex;
        while (i < endIndex) {
            int relIdx = i - startIndex;
            int relIdxMod16 = relIdx & 15;
            if (relIdxMod16 == 0) {
                dump.append(NEWLINE);
                dump.append(Long.toHexString((((long) relIdx) & 4294967295L) | 4294967296L));
                dump.setCharAt(dump.length() - 9, '|');
                dump.append('|');
            }
            dump.append(BYTE2HEX[buf.getUnsignedByte(i)]);
            if (relIdxMod16 == 15) {
                dump.append(" |");
                for (j = i - 15; j <= i; j++) {
                    dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
                }
                dump.append('|');
            }
            i++;
        }
        if (((i - startIndex) & 15) != 0) {
            int remainder = length & 15;
            dump.append(HEXPADDING[remainder]);
            dump.append(" |");
            for (j = i - remainder; j < i; j++) {
                dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
            }
            dump.append(BYTEPADDING[remainder]);
            dump.append('|');
        }
        dump.append(NEWLINE + "+--------+-------------------------------------------------+----------------+");
        return dump.toString();
    }

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log(e);
        ctx.sendUpstream(e);
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log(e);
        ctx.sendDownstream(e);
    }
}
