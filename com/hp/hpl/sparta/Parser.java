package com.hp.hpl.sparta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class Parser {
    public static Document parse(String str) throws ParseException, IOException {
        return parse(str.toCharArray());
    }

    public static Document parse(String str, InputStream inputStream) throws ParseException, IOException {
        Object buildDocument = new BuildDocument();
        ParseByteStream parseByteStream = new ParseByteStream(str, inputStream, null, null, buildDocument);
        return buildDocument.getDocument();
    }

    public static Document parse(String str, InputStream inputStream, ParseLog parseLog) throws ParseException, IOException {
        Object buildDocument = new BuildDocument();
        ParseByteStream parseByteStream = new ParseByteStream(str, inputStream, parseLog, null, buildDocument);
        return buildDocument.getDocument();
    }

    public static Document parse(String str, InputStream inputStream, ParseLog parseLog, String str2) throws ParseException, IOException {
        Object buildDocument = new BuildDocument();
        ParseByteStream parseByteStream = new ParseByteStream(str, inputStream, parseLog, str2, buildDocument);
        return buildDocument.getDocument();
    }

    public static Document parse(String str, Reader reader) throws ParseException, IOException {
        ParseHandler buildDocument = new BuildDocument();
        ParseCharStream parseCharStream = new ParseCharStream(str, reader, null, null, buildDocument);
        return buildDocument.getDocument();
    }

    public static Document parse(String str, Reader reader, ParseLog parseLog) throws ParseException, IOException {
        ParseHandler buildDocument = new BuildDocument();
        ParseCharStream parseCharStream = new ParseCharStream(str, reader, parseLog, null, buildDocument);
        return buildDocument.getDocument();
    }

    public static Document parse(String str, Reader reader, ParseLog parseLog, String str2) throws ParseException, EncodingMismatchException, IOException {
        ParseHandler buildDocument = new BuildDocument();
        ParseCharStream parseCharStream = new ParseCharStream(str, reader, parseLog, str2, buildDocument);
        return buildDocument.getDocument();
    }

    public static Document parse(byte[] bArr) throws ParseException, IOException {
        Object buildDocument = new BuildDocument();
        ParseByteStream parseByteStream = new ParseByteStream("file:anonymous-string", new ByteArrayInputStream(bArr), null, null, buildDocument);
        return buildDocument.getDocument();
    }

    public static Document parse(char[] cArr) throws ParseException, IOException {
        ParseHandler buildDocument = new BuildDocument();
        ParseCharStream parseCharStream = new ParseCharStream("file:anonymous-string", cArr, null, null, buildDocument);
        return buildDocument.getDocument();
    }

    public static void parse(String str, ParseHandler parseHandler) throws ParseException, IOException {
        parse(str.toCharArray(), parseHandler);
    }

    public static void parse(String str, InputStream inputStream, ParseHandler parseHandler) throws ParseException, IOException {
        ParseByteStream parseByteStream = new ParseByteStream(str, inputStream, null, null, parseHandler);
    }

    public static void parse(String str, InputStream inputStream, ParseLog parseLog, ParseHandler parseHandler) throws ParseException, IOException {
        ParseByteStream parseByteStream = new ParseByteStream(str, inputStream, parseLog, null, parseHandler);
    }

    public static void parse(String str, InputStream inputStream, ParseLog parseLog, String str2, ParseHandler parseHandler) throws ParseException, IOException {
        ParseByteStream parseByteStream = new ParseByteStream(str, inputStream, parseLog, str2, parseHandler);
    }

    public static void parse(String str, Reader reader, ParseHandler parseHandler) throws ParseException, IOException {
        ParseCharStream parseCharStream = new ParseCharStream(str, reader, null, null, parseHandler);
    }

    public static void parse(String str, Reader reader, ParseLog parseLog, ParseHandler parseHandler) throws ParseException, IOException {
        ParseCharStream parseCharStream = new ParseCharStream(str, reader, parseLog, null, parseHandler);
    }

    public static void parse(String str, Reader reader, ParseLog parseLog, String str2, ParseHandler parseHandler) throws ParseException, EncodingMismatchException, IOException {
        ParseCharStream parseCharStream = new ParseCharStream(str, reader, parseLog, str2, parseHandler);
    }

    public static void parse(byte[] bArr, ParseHandler parseHandler) throws ParseException, IOException {
        ParseByteStream parseByteStream = new ParseByteStream("file:anonymous-string", new ByteArrayInputStream(bArr), null, null, parseHandler);
    }

    public static void parse(char[] cArr, ParseHandler parseHandler) throws ParseException, IOException {
        ParseCharStream parseCharStream = new ParseCharStream("file:anonymous-string", cArr, null, null, parseHandler);
    }
}
