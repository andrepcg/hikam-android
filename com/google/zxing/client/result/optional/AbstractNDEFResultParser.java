package com.google.zxing.client.result.optional;

import com.google.zxing.client.result.ResultParser;
import java.io.UnsupportedEncodingException;

abstract class AbstractNDEFResultParser extends ResultParser {
    AbstractNDEFResultParser() {
    }

    static String bytesToString(byte[] bytes, int offset, int length, String encoding) {
        try {
            return new String(bytes, offset, length, encoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(new StringBuffer().append("Platform does not support required encoding: ").append(uee).toString());
        }
    }
}
