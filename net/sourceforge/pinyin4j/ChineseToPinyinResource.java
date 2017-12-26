package net.sourceforge.pinyin4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

class ChineseToPinyinResource {
    private Properties unicodeToHanyuPinyinTable;

    static class C07661 {
    }

    private static class ChineseToPinyinResourceHolder {
        static final ChineseToPinyinResource theInstance = new ChineseToPinyinResource(null);

        private ChineseToPinyinResourceHolder() {
        }
    }

    class Field {
        static final String COMMA = ",";
        static final String LEFT_BRACKET = "(";
        static final String RIGHT_BRACKET = ")";
        private final ChineseToPinyinResource this$0;

        Field(ChineseToPinyinResource chineseToPinyinResource) {
            this.this$0 = chineseToPinyinResource;
        }
    }

    private ChineseToPinyinResource() {
        this.unicodeToHanyuPinyinTable = null;
        initializeResource();
    }

    ChineseToPinyinResource(C07661 c07661) {
        this();
    }

    private String getHanyuPinyinRecordFromChar(char c) {
        String property = getUnicodeToHanyuPinyinTable().getProperty(Integer.toHexString(c).toUpperCase());
        return isValidRecord(property) ? property : null;
    }

    static ChineseToPinyinResource getInstance() {
        return ChineseToPinyinResourceHolder.theInstance;
    }

    private Properties getUnicodeToHanyuPinyinTable() {
        return this.unicodeToHanyuPinyinTable;
    }

    private void initializeResource() {
        try {
            setUnicodeToHanyuPinyinTable(new Properties());
            getUnicodeToHanyuPinyinTable().load(ResourceHelper.getResourceInputStream("/pinyindb/unicode_to_hanyu_pinyin.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private boolean isValidRecord(String str) {
        return str != null && !str.equals("(none0)") && str.startsWith("(") && str.endsWith(")");
    }

    private void setUnicodeToHanyuPinyinTable(Properties properties) {
        this.unicodeToHanyuPinyinTable = properties;
    }

    String[] getHanyuPinyinStringArray(char c) {
        String hanyuPinyinRecordFromChar = getHanyuPinyinRecordFromChar(c);
        if (hanyuPinyinRecordFromChar == null) {
            return null;
        }
        int indexOf = hanyuPinyinRecordFromChar.indexOf("(");
        return hanyuPinyinRecordFromChar.substring(indexOf + "(".length(), hanyuPinyinRecordFromChar.lastIndexOf(")")).split(",");
    }
}
