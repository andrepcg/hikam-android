package com.google.zxing.common;

import java.util.Hashtable;

public final class CharacterSetECI extends ECI {
    private static Hashtable NAME_TO_ECI;
    private static Hashtable VALUE_TO_ECI;
    private final String encodingName;

    private static void initialize() {
        VALUE_TO_ECI = new Hashtable(29);
        NAME_TO_ECI = new Hashtable(29);
        addCharacterSet(0, "Cp437");
        addCharacterSet(1, new String[]{"ISO8859_1", "ISO-8859-1"});
        addCharacterSet(2, "Cp437");
        addCharacterSet(3, new String[]{"ISO8859_1", "ISO-8859-1"});
        addCharacterSet(4, "ISO8859_2");
        addCharacterSet(5, "ISO8859_3");
        addCharacterSet(6, "ISO8859_4");
        addCharacterSet(7, "ISO8859_5");
        addCharacterSet(8, "ISO8859_6");
        addCharacterSet(9, "ISO8859_7");
        addCharacterSet(10, "ISO8859_8");
        addCharacterSet(11, "ISO8859_9");
        addCharacterSet(12, "ISO8859_10");
        addCharacterSet(13, "ISO8859_11");
        addCharacterSet(15, "ISO8859_13");
        addCharacterSet(16, "ISO8859_14");
        addCharacterSet(17, "ISO8859_15");
        addCharacterSet(18, "ISO8859_16");
        addCharacterSet(20, new String[]{StringUtils.SHIFT_JIS, "Shift_JIS"});
    }

    private CharacterSetECI(int value, String encodingName) {
        super(value);
        this.encodingName = encodingName;
    }

    public String getEncodingName() {
        return this.encodingName;
    }

    private static void addCharacterSet(int value, String encodingName) {
        CharacterSetECI eci = new CharacterSetECI(value, encodingName);
        VALUE_TO_ECI.put(new Integer(value), eci);
        NAME_TO_ECI.put(encodingName, eci);
    }

    private static void addCharacterSet(int value, String[] encodingNames) {
        CharacterSetECI eci = new CharacterSetECI(value, encodingNames[0]);
        VALUE_TO_ECI.put(new Integer(value), eci);
        for (Object put : encodingNames) {
            NAME_TO_ECI.put(put, eci);
        }
    }

    public static CharacterSetECI getCharacterSetECIByValue(int value) {
        if (VALUE_TO_ECI == null) {
            initialize();
        }
        if (value >= 0 && value < 900) {
            return (CharacterSetECI) VALUE_TO_ECI.get(new Integer(value));
        }
        throw new IllegalArgumentException(new StringBuffer().append("Bad ECI value: ").append(value).toString());
    }

    public static CharacterSetECI getCharacterSetECIByName(String name) {
        if (NAME_TO_ECI == null) {
            initialize();
        }
        return (CharacterSetECI) NAME_TO_ECI.get(name);
    }
}
