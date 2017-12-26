package com.hp.hpl.sparta;

public class DOMException extends Exception {
    public static final short DOMSTRING_SIZE_ERR = (short) 2;
    public static final short HIERARCHY_REQUEST_ERR = (short) 3;
    public static final short NOT_FOUND_ERR = (short) 8;
    public short code;

    public DOMException(short s, String str) {
        super(str);
        this.code = s;
    }
}
