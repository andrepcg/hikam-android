package com.google.zxing.qrcode.decoder;

import android.support.v4.media.TransportMediator;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitSource;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

final class DecodedBitStreamParser {
    private static final char[] ALPHANUMERIC_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '$', '%', '*', '+', '-', '.', '/', ':'};

    private DecodedBitStreamParser() {
    }

    static DecoderResult decode(byte[] bytes, Version version, ErrorCorrectionLevel ecLevel, Hashtable hints) throws FormatException {
        BitSource bits = new BitSource(bytes);
        StringBuffer result = new StringBuffer(50);
        CharacterSetECI currentCharacterSetECI = null;
        boolean fc1InEffect = false;
        Vector byteSegments = new Vector(1);
        Mode mode;
        do {
            if (bits.available() < 4) {
                mode = Mode.TERMINATOR;
            } else {
                try {
                    mode = Mode.forBits(bits.readBits(4));
                } catch (IllegalArgumentException e) {
                    throw FormatException.getFormatInstance();
                }
            }
            if (!mode.equals(Mode.TERMINATOR)) {
                if (mode.equals(Mode.FNC1_FIRST_POSITION) || mode.equals(Mode.FNC1_SECOND_POSITION)) {
                    fc1InEffect = true;
                } else if (mode.equals(Mode.STRUCTURED_APPEND)) {
                    bits.readBits(16);
                } else if (mode.equals(Mode.ECI)) {
                    currentCharacterSetECI = CharacterSetECI.getCharacterSetECIByValue(parseECIValue(bits));
                    if (currentCharacterSetECI == null) {
                        throw FormatException.getFormatInstance();
                    }
                } else {
                    int count = bits.readBits(mode.getCharacterCountBits(version));
                    if (mode.equals(Mode.NUMERIC)) {
                        decodeNumericSegment(bits, result, count);
                    } else if (mode.equals(Mode.ALPHANUMERIC)) {
                        decodeAlphanumericSegment(bits, result, count, fc1InEffect);
                    } else if (mode.equals(Mode.BYTE)) {
                        decodeByteSegment(bits, result, count, currentCharacterSetECI, byteSegments, hints);
                    } else if (mode.equals(Mode.KANJI)) {
                        decodeKanjiSegment(bits, result, count);
                    } else {
                        throw FormatException.getFormatInstance();
                    }
                }
            }
        } while (!mode.equals(Mode.TERMINATOR));
        String stringBuffer = result.toString();
        if (byteSegments.isEmpty()) {
            byteSegments = null;
        }
        return new DecoderResult(bytes, stringBuffer, byteSegments, ecLevel);
    }

    private static void decodeKanjiSegment(BitSource bits, StringBuffer result, int count) throws FormatException {
        byte[] buffer = new byte[(count * 2)];
        int offset = 0;
        while (count > 0) {
            int twoBytes = bits.readBits(13);
            int assembledTwoBytes = ((twoBytes / 192) << 8) | (twoBytes % 192);
            if (assembledTwoBytes < 7936) {
                assembledTwoBytes += 33088;
            } else {
                assembledTwoBytes += 49472;
            }
            buffer[offset] = (byte) (assembledTwoBytes >> 8);
            buffer[offset + 1] = (byte) assembledTwoBytes;
            offset += 2;
            count--;
        }
        try {
            result.append(new String(buffer, StringUtils.SHIFT_JIS));
        } catch (UnsupportedEncodingException e) {
            throw FormatException.getFormatInstance();
        }
    }

    private static void decodeByteSegment(BitSource bits, StringBuffer result, int count, CharacterSetECI currentCharacterSetECI, Vector byteSegments, Hashtable hints) throws FormatException {
        byte[] readBytes = new byte[count];
        if ((count << 3) > bits.available()) {
            throw FormatException.getFormatInstance();
        }
        String encoding;
        for (int i = 0; i < count; i++) {
            readBytes[i] = (byte) bits.readBits(8);
        }
        if (currentCharacterSetECI == null) {
            encoding = StringUtils.guessEncoding(readBytes, hints);
        } else {
            encoding = currentCharacterSetECI.getEncodingName();
        }
        try {
            result.append(new String(readBytes, encoding));
            byteSegments.addElement(readBytes);
        } catch (UnsupportedEncodingException e) {
            throw FormatException.getFormatInstance();
        }
    }

    private static char toAlphaNumericChar(int value) throws FormatException {
        if (value < ALPHANUMERIC_CHARS.length) {
            return ALPHANUMERIC_CHARS[value];
        }
        throw FormatException.getFormatInstance();
    }

    private static void decodeAlphanumericSegment(BitSource bits, StringBuffer result, int count, boolean fc1InEffect) throws FormatException {
        int start = result.length();
        while (count > 1) {
            int nextTwoCharsBits = bits.readBits(11);
            result.append(toAlphaNumericChar(nextTwoCharsBits / 45));
            result.append(toAlphaNumericChar(nextTwoCharsBits % 45));
            count -= 2;
        }
        if (count == 1) {
            result.append(toAlphaNumericChar(bits.readBits(6)));
        }
        if (fc1InEffect) {
            int i = start;
            while (i < result.length()) {
                if (result.charAt(i) == '%') {
                    if (i >= result.length() - 1 || result.charAt(i + 1) != '%') {
                        result.setCharAt(i, '\u001d');
                    } else {
                        result.deleteCharAt(i + 1);
                    }
                }
                i++;
            }
        }
    }

    private static void decodeNumericSegment(BitSource bits, StringBuffer result, int count) throws FormatException {
        while (count >= 3) {
            int threeDigitsBits = bits.readBits(10);
            if (threeDigitsBits >= 1000) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(threeDigitsBits / 100));
            result.append(toAlphaNumericChar((threeDigitsBits / 10) % 10));
            result.append(toAlphaNumericChar(threeDigitsBits % 10));
            count -= 3;
        }
        if (count == 2) {
            int twoDigitsBits = bits.readBits(7);
            if (twoDigitsBits >= 100) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(twoDigitsBits / 10));
            result.append(toAlphaNumericChar(twoDigitsBits % 10));
        } else if (count == 1) {
            int digitBits = bits.readBits(4);
            if (digitBits >= 10) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(digitBits));
        }
    }

    private static int parseECIValue(BitSource bits) {
        int firstByte = bits.readBits(8);
        if ((firstByte & 128) == 0) {
            return firstByte & TransportMediator.KEYCODE_MEDIA_PAUSE;
        }
        if ((firstByte & 192) == 128) {
            return ((firstByte & 63) << 8) | bits.readBits(8);
        } else if ((firstByte & 224) == 192) {
            return ((firstByte & 31) << 16) | bits.readBits(16);
        } else {
            throw new IllegalArgumentException(new StringBuffer().append("Bad ECI bits starting with byte ").append(firstByte).toString());
        }
    }
}
