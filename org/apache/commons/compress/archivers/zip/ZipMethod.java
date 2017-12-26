package org.apache.commons.compress.archivers.zip;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ZipMethod {
    STORED(0),
    UNSHRINKING(1),
    EXPANDING_LEVEL_1(2),
    EXPANDING_LEVEL_2(3),
    EXPANDING_LEVEL_3(4),
    EXPANDING_LEVEL_4(5),
    IMPLODING(6),
    TOKENIZATION(7),
    DEFLATED(8),
    ENHANCED_DEFLATED(9),
    PKWARE_IMPLODING(10),
    BZIP2(12),
    LZMA(14),
    JPEG(96),
    WAVPACK(97),
    PPMD(98),
    AES_ENCRYPTED(99),
    UNKNOWN;
    
    static final int UNKNOWN_CODE = -1;
    private static final Map<Integer, ZipMethod> codeToEnum = null;
    private final int code;

    static {
        Map<Integer, ZipMethod> cte = new HashMap();
        ZipMethod[] values = values();
        int length = values.length;
        int i;
        while (i < length) {
            ZipMethod method = values[i];
            cte.put(Integer.valueOf(method.getCode()), method);
            i++;
        }
        codeToEnum = Collections.unmodifiableMap(cte);
    }

    private ZipMethod(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static ZipMethod getMethodByCode(int code) {
        return (ZipMethod) codeToEnum.get(Integer.valueOf(code));
    }
}
