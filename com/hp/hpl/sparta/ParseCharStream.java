package com.hp.hpl.sparta;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import org.apache.http.cookie.ClientCookie;

class ParseCharStream implements ParseSource {
    private static final char[] BEGIN_CDATA = "<![CDATA[".toCharArray();
    private static final char[] BEGIN_ETAG = "</".toCharArray();
    private static final char[] CHARREF_BEGIN = "&#".toCharArray();
    private static final char[] COMMENT_BEGIN = "<!--".toCharArray();
    private static final char[] COMMENT_END = "-->".toCharArray();
    private static final boolean DEBUG = true;
    private static final char[] DOCTYPE_BEGIN = "<!DOCTYPE".toCharArray();
    private static final char[] ENCODING = "encoding".toCharArray();
    private static final char[] END_CDATA = "]]>".toCharArray();
    private static final char[] END_EMPTYTAG = "/>".toCharArray();
    private static final char[] ENTITY_BEGIN = "<!ENTITY".toCharArray();
    public static final int HISTORY_LENGTH = 100;
    private static final boolean H_DEBUG = false;
    private static final boolean[] IS_NAME_CHAR = new boolean[128];
    private static final char[] MARKUPDECL_BEGIN = "<!".toCharArray();
    private static final int MAX_COMMON_CHAR = 128;
    private static final char[] NAME_PUNCT_CHARS = new char[]{'.', '-', '_', ':'};
    private static final char[] NDATA = "NDATA".toCharArray();
    private static final char[] PI_BEGIN = "<?".toCharArray();
    private static final char[] PUBLIC = "PUBLIC".toCharArray();
    private static final char[] QU_END = "?>".toCharArray();
    private static final char[] SYSTEM = "SYSTEM".toCharArray();
    private static final int TMP_BUF_SIZE = 255;
    private static final char[] VERSION = ClientCookie.VERSION_ATTR.toCharArray();
    private static final char[] VERSIONNUM_PUNC_CHARS = new char[]{'_', '.', ':', '-'};
    private static final char[] XML_BEGIN = "<?xml".toCharArray();
    private final int CBUF_SIZE;
    private final char[] cbuf_;
    private int ch_;
    private int curPos_;
    private String docTypeName_;
    private final String encoding_;
    private int endPos_;
    private final Hashtable entities_;
    private boolean eos_;
    private final ParseHandler handler_;
    private final CharCircBuffer history_;
    private boolean isExternalDtd_;
    private int lineNumber_;
    private final ParseLog log_;
    private final Hashtable pes_;
    private final Reader reader_;
    private String systemId_;
    private final char[] tmpBuf_;

    static {
        for (char c = '\u0000'; c < ''; c = (char) (c + 1)) {
            IS_NAME_CHAR[c] = isNameChar(c);
        }
    }

    public ParseCharStream(String str, Reader reader, ParseLog parseLog, String str2, ParseHandler parseHandler) throws ParseException, EncodingMismatchException, IOException {
        this(str, reader, null, parseLog, str2, parseHandler);
    }

    public ParseCharStream(String str, Reader reader, char[] cArr, ParseLog parseLog, String str2, ParseHandler parseHandler) throws ParseException, EncodingMismatchException, IOException {
        this.docTypeName_ = null;
        this.entities_ = new Hashtable();
        this.pes_ = new Hashtable();
        this.ch_ = -2;
        this.isExternalDtd_ = false;
        this.CBUF_SIZE = 1024;
        this.curPos_ = 0;
        this.endPos_ = 0;
        this.eos_ = false;
        this.tmpBuf_ = new char[255];
        this.lineNumber_ = -1;
        this.lineNumber_ = 1;
        this.history_ = null;
        if (parseLog == null) {
            parseLog = ParseSource.DEFAULT_LOG;
        }
        this.log_ = parseLog;
        this.encoding_ = str2 == null ? null : str2.toLowerCase();
        this.entities_.put("lt", "<");
        this.entities_.put("gt", ">");
        this.entities_.put("amp", "&");
        this.entities_.put("apos", "'");
        this.entities_.put("quot", "\"");
        if (cArr != null) {
            this.cbuf_ = cArr;
            this.curPos_ = 0;
            this.endPos_ = this.cbuf_.length;
            this.eos_ = DEBUG;
            this.reader_ = null;
        } else {
            this.reader_ = reader;
            this.cbuf_ = new char[1024];
            fillBuf();
        }
        this.systemId_ = str;
        this.handler_ = parseHandler;
        this.handler_.setParseSource(this);
        readProlog();
        this.handler_.startDocument();
        Element readElement = readElement();
        if (!(this.docTypeName_ == null || this.docTypeName_.equals(readElement.getTagName()))) {
            this.log_.warning(new StringBuffer().append("DOCTYPE name \"").append(this.docTypeName_).append("\" not same as tag name, \"").append(readElement.getTagName()).append("\" of root element").toString(), this.systemId_, getLineNumber());
        }
        while (isMisc()) {
            readMisc();
        }
        if (this.reader_ != null) {
            this.reader_.close();
        }
        this.handler_.endDocument();
    }

    public ParseCharStream(String str, char[] cArr, ParseLog parseLog, String str2, ParseHandler parseHandler) throws ParseException, EncodingMismatchException, IOException {
        this(str, null, cArr, parseLog, str2, parseHandler);
    }

    private int fillBuf() throws IOException {
        if (this.eos_) {
            return -1;
        }
        if (this.endPos_ == this.cbuf_.length) {
            this.endPos_ = 0;
            this.curPos_ = 0;
        }
        int read = this.reader_.read(this.cbuf_, this.endPos_, this.cbuf_.length - this.endPos_);
        if (read <= 0) {
            this.eos_ = DEBUG;
            return -1;
        }
        this.endPos_ += read;
        return read;
    }

    private int fillBuf(int i) throws IOException {
        int i2 = 0;
        if (this.eos_) {
            return -1;
        }
        int i3;
        if (this.cbuf_.length - this.curPos_ < i) {
            for (i3 = 0; this.curPos_ + i3 < this.endPos_; i3++) {
                this.cbuf_[i3] = this.cbuf_[this.curPos_ + i3];
            }
            i3 = this.endPos_ - this.curPos_;
            this.endPos_ = i3;
            this.curPos_ = 0;
            i2 = i3;
        }
        i3 = fillBuf();
        return i3 == -1 ? i2 == 0 ? -1 : i2 : i2 + i3;
    }

    private boolean isCdSect() throws ParseException, IOException {
        return isSymbol(BEGIN_CDATA);
    }

    private final boolean isChar(char c) throws ParseException, IOException {
        if (this.curPos_ < this.endPos_ || fillBuf() != -1) {
            return this.cbuf_[this.curPos_] == c ? DEBUG : false;
        } else {
            throw new ParseException(this, "unexpected end of expression.");
        }
    }

    private final boolean isChar(char c, char c2) throws ParseException, IOException {
        if (this.curPos_ >= this.endPos_ && fillBuf() == -1) {
            return false;
        }
        char c3 = this.cbuf_[this.curPos_];
        return (c3 == c || c3 == c2) ? DEBUG : false;
    }

    private final boolean isChar(char c, char c2, char c3, char c4) throws ParseException, IOException {
        if (this.curPos_ >= this.endPos_ && fillBuf() == -1) {
            return false;
        }
        char c5 = this.cbuf_[this.curPos_];
        return (c5 == c || c5 == c2 || c5 == c3 || c5 == c4) ? DEBUG : false;
    }

    private final boolean isComment() throws ParseException, IOException {
        return isSymbol(COMMENT_BEGIN);
    }

    private boolean isDeclSep() throws ParseException, IOException {
        return (isPeReference() || isS()) ? DEBUG : false;
    }

    private boolean isDocTypeDecl() throws ParseException, IOException {
        return isSymbol(DOCTYPE_BEGIN);
    }

    private boolean isETag() throws ParseException, IOException {
        return isSymbol(BEGIN_ETAG);
    }

    private boolean isEncodingDecl() throws ParseException, IOException {
        return isSymbol(ENCODING);
    }

    private boolean isEntityDecl() throws ParseException, IOException {
        return isSymbol(ENTITY_BEGIN);
    }

    private final boolean isEntityValue() throws ParseException, IOException {
        return isChar('\'', '\"');
    }

    private static boolean isExtender(char c) {
        switch (c) {
            case '·':
            case 'ː':
            case 'ˑ':
            case '·':
            case 'ـ':
            case 'ๆ':
            case 'ໆ':
            case '々':
            case '〱':
            case '〲':
            case '〳':
            case '〴':
            case '〵':
            case 'ゝ':
            case 'ゞ':
            case 'ー':
            case 'ヽ':
            case 'ヾ':
                return DEBUG;
            default:
                return false;
        }
    }

    private boolean isExternalId() throws ParseException, IOException {
        return (isSymbol(SYSTEM) || isSymbol(PUBLIC)) ? DEBUG : false;
    }

    private static final boolean isIn(char c, char[] cArr) {
        for (char c2 : cArr) {
            if (c == c2) {
                return DEBUG;
            }
        }
        return false;
    }

    private static boolean isLetter(char c) {
        return "abcdefghijklmnopqrstuvwxyz".indexOf(Character.toLowerCase(c)) != -1 ? DEBUG : false;
    }

    private boolean isMisc() throws ParseException, IOException {
        return (isComment() || isPi() || isS()) ? DEBUG : false;
    }

    private boolean isNameChar() throws ParseException, IOException {
        char peekChar = peekChar();
        return peekChar < '' ? IS_NAME_CHAR[peekChar] : isNameChar(peekChar);
    }

    private static boolean isNameChar(char c) {
        return (Character.isDigit(c) || isLetter(c) || isIn(c, NAME_PUNCT_CHARS) || isExtender(c)) ? DEBUG : false;
    }

    private boolean isPeReference() throws ParseException, IOException {
        return isChar('%');
    }

    private final boolean isPi() throws ParseException, IOException {
        return isSymbol(PI_BEGIN);
    }

    private final boolean isReference() throws ParseException, IOException {
        return isChar('&');
    }

    private final boolean isS() throws ParseException, IOException {
        return isChar(' ', '\t', '\r', '\n');
    }

    private final boolean isSymbol(char[] cArr) throws ParseException, IOException {
        int length = cArr.length;
        if (this.endPos_ - this.curPos_ >= length || fillBuf(length) > 0) {
            this.ch_ = this.cbuf_[this.endPos_ - 1];
            if (this.endPos_ - this.curPos_ < length) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                if (this.cbuf_[this.curPos_ + i] != cArr[i]) {
                    return false;
                }
            }
            return DEBUG;
        }
        this.ch_ = -1;
        return false;
    }

    private boolean isVersionNumChar() throws ParseException, IOException {
        char peekChar = peekChar();
        return (Character.isDigit(peekChar) || (('a' <= peekChar && peekChar <= 'z') || (('Z' <= peekChar && peekChar <= 'Z') || isIn(peekChar, VERSIONNUM_PUNC_CHARS)))) ? DEBUG : false;
    }

    private boolean isXmlDecl() throws ParseException, IOException {
        return isSymbol(XML_BEGIN);
    }

    private final char peekChar() throws ParseException, IOException {
        if (this.curPos_ < this.endPos_ || fillBuf() != -1) {
            return this.cbuf_[this.curPos_];
        }
        throw new ParseException(this, "unexpected end of expression.");
    }

    private String readAttValue() throws ParseException, IOException {
        char readChar = readChar('\'', '\"');
        StringBuffer stringBuffer = new StringBuffer();
        while (!isChar(readChar)) {
            if (isReference()) {
                stringBuffer.append(readReference());
            } else {
                stringBuffer.append(readChar());
            }
        }
        readChar(readChar);
        return stringBuffer.toString();
    }

    private void readAttribute(Element element) throws ParseException, IOException {
        String readName = readName();
        readEq();
        String readAttValue = readAttValue();
        if (element.getAttribute(readName) != null) {
            this.log_.warning(new StringBuffer().append("Element ").append(this).append(" contains attribute ").append(readName).append("more than once").toString(), this.systemId_, getLineNumber());
        }
        element.setAttribute(readName, readAttValue);
    }

    private void readCdSect() throws ParseException, IOException {
        StringBuffer stringBuffer = null;
        readSymbol(BEGIN_CDATA);
        int i = 0;
        while (!isSymbol(END_CDATA)) {
            StringBuffer stringBuffer2;
            int i2;
            if (i >= 255) {
                if (stringBuffer == null) {
                    stringBuffer = new StringBuffer(i);
                    stringBuffer.append(this.tmpBuf_, 0, i);
                } else {
                    stringBuffer.append(this.tmpBuf_, 0, i);
                }
                stringBuffer2 = stringBuffer;
                i2 = 0;
            } else {
                int i3 = i;
                stringBuffer2 = stringBuffer;
                i2 = i3;
            }
            int i4 = i2 + 1;
            this.tmpBuf_[i2] = readChar();
            stringBuffer = stringBuffer2;
            i = i4;
        }
        readSymbol(END_CDATA);
        if (stringBuffer != null) {
            stringBuffer.append(this.tmpBuf_, 0, i);
            char[] toCharArray = stringBuffer.toString().toCharArray();
            this.handler_.characters(toCharArray, 0, toCharArray.length);
            return;
        }
        this.handler_.characters(this.tmpBuf_, 0, i);
    }

    private final char readChar() throws ParseException, IOException {
        if (this.curPos_ < this.endPos_ || fillBuf() != -1) {
            if (this.cbuf_[this.curPos_] == '\n') {
                this.lineNumber_++;
            }
            char[] cArr = this.cbuf_;
            int i = this.curPos_;
            this.curPos_ = i + 1;
            return cArr[i];
        }
        throw new ParseException(this, "unexpected end of expression.");
    }

    private final char readChar(char c, char c2) throws ParseException, IOException {
        char readChar = readChar();
        if (readChar == c || readChar == c2) {
            return readChar;
        }
        throw new ParseException(this, readChar, new char[]{c, c2});
    }

    private final char readChar(char c, char c2, char c3, char c4) throws ParseException, IOException {
        char readChar = readChar();
        if (readChar == c || readChar == c2 || readChar == c3 || readChar == c4) {
            return readChar;
        }
        throw new ParseException(this, readChar, new char[]{c, c2, c3, c4});
    }

    private final void readChar(char c) throws ParseException, IOException {
        char readChar = readChar();
        if (readChar != c) {
            throw new ParseException(this, readChar, c);
        }
    }

    private char readCharRef() throws ParseException, IOException {
        readSymbol(CHARREF_BEGIN);
        Object obj = 10;
        if (isChar('x')) {
            readChar();
            obj = 16;
        }
        int i = 0;
        while (!isChar(';')) {
            int i2 = i + 1;
            this.tmpBuf_[i] = readChar();
            if (i2 >= 255) {
                this.log_.warning("Tmp buffer overflow on readCharRef", this.systemId_, getLineNumber());
                return ' ';
            }
            i = i2;
        }
        readChar(';');
        String str = new String(this.tmpBuf_, 0, i);
        try {
            return (char) Integer.parseInt(str, obj);
        } catch (NumberFormatException e) {
            this.log_.warning(new StringBuffer().append("\"").append(str).append("\" is not a valid ").append(obj == 16 ? "hexadecimal" : "decimal").append(" number").toString(), this.systemId_, getLineNumber());
            return ' ';
        }
    }

    private final void readComment() throws ParseException, IOException {
        readSymbol(COMMENT_BEGIN);
        while (!isSymbol(COMMENT_END)) {
            readChar();
        }
        readSymbol(COMMENT_END);
    }

    private void readContent() throws ParseException, IOException {
        readPossibleCharData();
        int i = 1;
        while (i != 0) {
            if (isETag()) {
                i = 0;
            } else if (isReference()) {
                char[] readReference = readReference();
                this.handler_.characters(readReference, 0, readReference.length);
            } else if (isCdSect()) {
                readCdSect();
            } else if (isPi()) {
                readPi();
            } else if (isComment()) {
                readComment();
            } else if (isChar('<')) {
                readElement();
            } else {
                i = 0;
            }
            readPossibleCharData();
        }
    }

    private void readDeclSep() throws ParseException, IOException {
        if (isPeReference()) {
            readPeReference();
        } else {
            readS();
        }
    }

    private void readDocTypeDecl() throws ParseException, IOException {
        readSymbol(DOCTYPE_BEGIN);
        readS();
        this.docTypeName_ = readName();
        if (isS()) {
            readS();
            if (!(isChar('>') || isChar('['))) {
                this.isExternalDtd_ = DEBUG;
                readExternalId();
                if (isS()) {
                    readS();
                }
            }
        }
        if (isChar('[')) {
            readChar();
            while (!isChar(']')) {
                if (isDeclSep()) {
                    readDeclSep();
                } else {
                    readMarkupDecl();
                }
            }
            readChar(']');
            if (isS()) {
                readS();
            }
        }
        readChar('>');
    }

    private void readETag(Element element) throws ParseException, IOException {
        readSymbol(BEGIN_ETAG);
        String readName = readName();
        if (!readName.equals(element.getTagName())) {
            this.log_.warning(new StringBuffer().append("end tag (").append(readName).append(") does not match begin tag (").append(element.getTagName()).append(")").toString(), this.systemId_, getLineNumber());
        }
        if (isS()) {
            readS();
        }
        readChar('>');
    }

    private final Element readElement() throws ParseException, IOException {
        Element element = new Element();
        boolean readEmptyElementTagOrSTag = readEmptyElementTagOrSTag(element);
        this.handler_.startElement(element);
        if (readEmptyElementTagOrSTag) {
            readContent();
            readETag(element);
        }
        this.handler_.endElement(element);
        return element;
    }

    private boolean readEmptyElementTagOrSTag(Element element) throws ParseException, IOException {
        readChar('<');
        element.setTagName(readName());
        while (isS()) {
            readS();
            if (!isChar('/', '>')) {
                readAttribute(element);
            }
        }
        if (isS()) {
            readS();
        }
        boolean isChar = isChar('>');
        if (isChar) {
            readChar('>');
        } else {
            readSymbol(END_EMPTYTAG);
        }
        return isChar;
    }

    private String readEncodingDecl() throws ParseException, IOException {
        readSymbol(ENCODING);
        readEq();
        char readChar = readChar('\'', '\"');
        StringBuffer stringBuffer = new StringBuffer();
        while (!isChar(readChar)) {
            stringBuffer.append(readChar());
        }
        readChar(readChar);
        return stringBuffer.toString();
    }

    private void readEntityDecl() throws ParseException, IOException {
        readSymbol(ENTITY_BEGIN);
        readS();
        String readName;
        if (isChar('%')) {
            readChar('%');
            readS();
            readName = readName();
            readS();
            this.pes_.put(readName, isEntityValue() ? readEntityValue() : readExternalId());
        } else {
            Object readEntityValue;
            readName = readName();
            readS();
            if (isEntityValue()) {
                readEntityValue = readEntityValue();
            } else if (isExternalId()) {
                readEntityValue = readExternalId();
                if (isS()) {
                    readS();
                }
                if (isSymbol(NDATA)) {
                    readSymbol(NDATA);
                    readS();
                    readName();
                }
            } else {
                throw new ParseException(this, "expecting double-quote, \"PUBLIC\" or \"SYSTEM\" while reading entity declaration");
            }
            this.entities_.put(readName, readEntityValue);
        }
        if (isS()) {
            readS();
        }
        readChar('>');
    }

    private String readEntityRef() throws ParseException, IOException {
        readChar('&');
        String readName = readName();
        String str = (String) this.entities_.get(readName);
        if (str == null) {
            str = "";
            if (this.isExternalDtd_) {
                this.log_.warning(new StringBuffer().append("&").append(readName).append("; not found -- possibly defined in external DTD)").toString(), this.systemId_, getLineNumber());
            } else {
                this.log_.warning(new StringBuffer().append("No declaration of &").append(readName).append(";").toString(), this.systemId_, getLineNumber());
            }
        }
        readChar(';');
        return str;
    }

    private final String readEntityValue() throws ParseException, IOException {
        char readChar = readChar('\'', '\"');
        StringBuffer stringBuffer = new StringBuffer();
        while (!isChar(readChar)) {
            if (isPeReference()) {
                stringBuffer.append(readPeReference());
            } else if (isReference()) {
                stringBuffer.append(readReference());
            } else {
                stringBuffer.append(readChar());
            }
        }
        readChar(readChar);
        return stringBuffer.toString();
    }

    private final void readEq() throws ParseException, IOException {
        if (isS()) {
            readS();
        }
        readChar('=');
        if (isS()) {
            readS();
        }
    }

    private String readExternalId() throws ParseException, IOException {
        if (isSymbol(SYSTEM)) {
            readSymbol(SYSTEM);
        } else if (isSymbol(PUBLIC)) {
            readSymbol(PUBLIC);
            readS();
            readPubidLiteral();
        } else {
            throw new ParseException(this, "expecting \"SYSTEM\" or \"PUBLIC\" while reading external ID");
        }
        readS();
        readSystemLiteral();
        return "(WARNING: external ID not read)";
    }

    private void readMarkupDecl() throws ParseException, IOException {
        if (isPi()) {
            readPi();
        } else if (isComment()) {
            readComment();
        } else if (isEntityDecl()) {
            readEntityDecl();
        } else if (isSymbol(MARKUPDECL_BEGIN)) {
            while (!isChar('>')) {
                if (isChar('\'', '\"')) {
                    char readChar = readChar();
                    while (!isChar(readChar)) {
                        readChar();
                    }
                    readChar(readChar);
                } else {
                    readChar();
                }
            }
            readChar('>');
        } else {
            throw new ParseException(this, "expecting processing instruction, comment, or \"<!\"");
        }
    }

    private void readMisc() throws ParseException, IOException {
        if (isComment()) {
            readComment();
        } else if (isPi()) {
            readPi();
        } else if (isS()) {
            readS();
        } else {
            throw new ParseException(this, "expecting comment or processing instruction or space");
        }
    }

    private final String readName() throws ParseException, IOException {
        StringBuffer stringBuffer = null;
        int i = 1;
        this.tmpBuf_[0] = readNameStartChar();
        while (isNameChar()) {
            StringBuffer stringBuffer2;
            int i2;
            if (i >= 255) {
                if (stringBuffer == null) {
                    stringBuffer = new StringBuffer(i);
                    stringBuffer.append(this.tmpBuf_, 0, i);
                } else {
                    stringBuffer.append(this.tmpBuf_, 0, i);
                }
                stringBuffer2 = stringBuffer;
                i2 = 0;
            } else {
                int i3 = i;
                stringBuffer2 = stringBuffer;
                i2 = i3;
            }
            int i4 = i2 + 1;
            this.tmpBuf_[i2] = readChar();
            stringBuffer = stringBuffer2;
            i = i4;
        }
        if (stringBuffer == null) {
            return Sparta.intern(new String(this.tmpBuf_, 0, i));
        }
        stringBuffer.append(this.tmpBuf_, 0, i);
        return stringBuffer.toString();
    }

    private char readNameStartChar() throws ParseException, IOException {
        char readChar = readChar();
        if (isLetter(readChar) || readChar == '_' || readChar == ':') {
            return readChar;
        }
        throw new ParseException(this, readChar, "letter, underscore, colon");
    }

    private String readPeReference() throws ParseException, IOException {
        readChar('%');
        String readName = readName();
        String str = (String) this.pes_.get(readName);
        if (str == null) {
            str = "";
            this.log_.warning(new StringBuffer().append("No declaration of %").append(readName).append(";").toString(), this.systemId_, getLineNumber());
        }
        readChar(';');
        return str;
    }

    private final void readPi() throws ParseException, IOException {
        readSymbol(PI_BEGIN);
        while (!isSymbol(QU_END)) {
            readChar();
        }
        readSymbol(QU_END);
    }

    private void readPossibleCharData() throws ParseException, IOException {
        int i = 0;
        while (!isChar('<') && !isChar('&') && !isSymbol(END_CDATA)) {
            this.tmpBuf_[i] = readChar();
            if (this.tmpBuf_[i] == '\r' && peekChar() == '\n') {
                this.tmpBuf_[i] = readChar();
            }
            i++;
            if (i == 255) {
                this.handler_.characters(this.tmpBuf_, 0, 255);
                i = 0;
            }
        }
        if (i > 0) {
            this.handler_.characters(this.tmpBuf_, 0, i);
        }
    }

    private void readProlog() throws ParseException, EncodingMismatchException, IOException {
        if (isXmlDecl()) {
            readXmlDecl();
        }
        while (isMisc()) {
            readMisc();
        }
        if (isDocTypeDecl()) {
            readDocTypeDecl();
            while (isMisc()) {
                readMisc();
            }
        }
    }

    private final void readPubidLiteral() throws ParseException, IOException {
        readSystemLiteral();
    }

    private final char[] readReference() throws ParseException, IOException {
        if (!isSymbol(CHARREF_BEGIN)) {
            return readEntityRef().toCharArray();
        }
        return new char[]{readCharRef()};
    }

    private final void readS() throws ParseException, IOException {
        readChar(' ', '\t', '\r', '\n');
        while (isChar(' ', '\t', '\r', '\n')) {
            readChar();
        }
    }

    private final void readSymbol(char[] cArr) throws ParseException, IOException {
        int length = cArr.length;
        if (this.endPos_ - this.curPos_ >= length || fillBuf(length) > 0) {
            this.ch_ = this.cbuf_[this.endPos_ - 1];
            if (this.endPos_ - this.curPos_ < length) {
                throw new ParseException(this, "end of XML file", cArr);
            }
            for (int i = 0; i < length; i++) {
                if (this.cbuf_[this.curPos_ + i] != cArr[i]) {
                    throw new ParseException(this, new String(this.cbuf_, this.curPos_, length), cArr);
                }
            }
            this.curPos_ += length;
            return;
        }
        this.ch_ = -1;
        throw new ParseException(this, "end of XML file", cArr);
    }

    private final void readSystemLiteral() throws ParseException, IOException {
        char readChar = readChar();
        while (peekChar() != readChar) {
            readChar();
        }
        readChar(readChar);
    }

    private void readVersionInfo() throws ParseException, IOException {
        readS();
        readSymbol(VERSION);
        readEq();
        char readChar = readChar('\'', '\"');
        readVersionNum();
        readChar(readChar);
    }

    private void readVersionNum() throws ParseException, IOException {
        readChar();
        while (isVersionNumChar()) {
            readChar();
        }
    }

    private void readXmlDecl() throws ParseException, EncodingMismatchException, IOException {
        readSymbol(XML_BEGIN);
        readVersionInfo();
        if (isS()) {
            readS();
        }
        if (isEncodingDecl()) {
            String readEncodingDecl = readEncodingDecl();
            if (!(this.encoding_ == null || readEncodingDecl.toLowerCase().equals(this.encoding_))) {
                throw new EncodingMismatchException(this.systemId_, readEncodingDecl, this.encoding_);
            }
        }
        while (!isSymbol(QU_END)) {
            readChar();
        }
        readSymbol(QU_END);
    }

    final String getHistory() {
        return "";
    }

    int getLastCharRead() {
        return this.ch_;
    }

    public int getLineNumber() {
        return this.lineNumber_;
    }

    ParseLog getLog() {
        return this.log_;
    }

    public String getSystemId() {
        return this.systemId_;
    }

    public String toString() {
        return this.systemId_;
    }
}
