package net.sourceforge.pinyin4j;

import java.io.BufferedInputStream;

class ResourceHelper {
    static Class class$net$sourceforge$pinyin4j$ResourceHelper;

    ResourceHelper() {
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    static BufferedInputStream getResourceInputStream(String str) {
        Class class$;
        if (class$net$sourceforge$pinyin4j$ResourceHelper == null) {
            class$ = class$("net.sourceforge.pinyin4j.ResourceHelper");
            class$net$sourceforge$pinyin4j$ResourceHelper = class$;
        } else {
            class$ = class$net$sourceforge$pinyin4j$ResourceHelper;
        }
        return new BufferedInputStream(class$.getResourceAsStream(str));
    }
}
