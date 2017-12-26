package org.jboss.netty.buffer;

import org.jboss.netty.handler.codec.http.HttpConstants;

public interface ChannelBufferIndexFinder {
    public static final ChannelBufferIndexFinder CR = new C12013();
    public static final ChannelBufferIndexFinder CRLF = new C12057();
    public static final ChannelBufferIndexFinder LF = new C12035();
    public static final ChannelBufferIndexFinder LINEAR_WHITESPACE = new C12079();
    public static final ChannelBufferIndexFinder NOT_CR = new C12024();
    public static final ChannelBufferIndexFinder NOT_CRLF = new C12068();
    public static final ChannelBufferIndexFinder NOT_LF = new C12046();
    public static final ChannelBufferIndexFinder NOT_LINEAR_WHITESPACE = new ChannelBufferIndexFinder() {
        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            byte b = buffer.getByte(guessedIndex);
            return (b == HttpConstants.SP || b == (byte) 9) ? false : true;
        }
    };
    public static final ChannelBufferIndexFinder NOT_NUL = new C12002();
    public static final ChannelBufferIndexFinder NUL = new C11991();

    static class C11991 implements ChannelBufferIndexFinder {
        C11991() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            return buffer.getByte(guessedIndex) == (byte) 0;
        }
    }

    static class C12002 implements ChannelBufferIndexFinder {
        C12002() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            return buffer.getByte(guessedIndex) != (byte) 0;
        }
    }

    static class C12013 implements ChannelBufferIndexFinder {
        C12013() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            return buffer.getByte(guessedIndex) == HttpConstants.CR;
        }
    }

    static class C12024 implements ChannelBufferIndexFinder {
        C12024() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            return buffer.getByte(guessedIndex) != HttpConstants.CR;
        }
    }

    static class C12035 implements ChannelBufferIndexFinder {
        C12035() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            return buffer.getByte(guessedIndex) == (byte) 10;
        }
    }

    static class C12046 implements ChannelBufferIndexFinder {
        C12046() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            return buffer.getByte(guessedIndex) != (byte) 10;
        }
    }

    static class C12057 implements ChannelBufferIndexFinder {
        C12057() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            byte b = buffer.getByte(guessedIndex);
            return b == HttpConstants.CR || b == (byte) 10;
        }
    }

    static class C12068 implements ChannelBufferIndexFinder {
        C12068() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            byte b = buffer.getByte(guessedIndex);
            return (b == HttpConstants.CR || b == (byte) 10) ? false : true;
        }
    }

    static class C12079 implements ChannelBufferIndexFinder {
        C12079() {
        }

        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            byte b = buffer.getByte(guessedIndex);
            return b == HttpConstants.SP || b == (byte) 9;
        }
    }

    boolean find(ChannelBuffer channelBuffer, int i);
}
